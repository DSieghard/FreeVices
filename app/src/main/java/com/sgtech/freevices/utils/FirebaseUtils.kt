package com.sgtech.freevices.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.LoginActivity
import com.sgtech.freevices.views.ui.NewMainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FirebaseUtils {
    private var loadingDialog: AlertDialog? = null
    fun checkIfUserIsLoggedIn(context: Context) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(context, NewMainActivity::class.java)
            startActivity(context, intent, null)
        } else {
            Log.d("FirebaseUtils", "User is not logged in")
        }

    }

    fun signInWithEmail(
        context: Context,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    try {
                        checkIfUserIsLoggedIn(context)
                        val intent = Intent(context, NewMainActivity::class.java)
                        startActivity(context, intent, null)
                        onSuccess()
                    } catch (e: FirebaseAuthInvalidUserException) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_user_not_found)
                        buildAlertDialog(context, title, message)
                        onFailure(e)
                    } catch (ec: FirebaseAuthActionCodeException) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_invalid_token)
                        buildAlertDialog(context, title, message)
                        onFailure(ec)
                    } catch (e: Exception) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_unknown)
                        buildAlertDialog(context, title, message)
                        onFailure(e)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                onFailure(e)
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun configDisplayNameOnAuth(name: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        user!!.updateProfile(profileUpdates)
    }

    fun updateDisplayNameOnFirestore(firstName: String, lastName: String) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        val userId = user?.uid

        if (userId != null) {
            val userDb = db.collection("users").document(userId)
            userDb.update("firstName", firstName)
            userDb.update("lastName", lastName)
        }
    }


    fun createAccount(context: Context, email: String, password: String, onSuccess: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        showLoadingDialog(context)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    hideLoadingDialog()
                    onSuccess()
                    startActivity(context, Intent(context, NewMainActivity::class.java), null)
                }
            }
            .addOnFailureListener { e ->
                when (e) {
                    is FirebaseAuthUserCollisionException -> {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_user_already_exists)
                        buildAlertDialog(context, title, message)
                        hideLoadingDialog()
                    }

                    is FirebaseAuthInvalidUserException -> {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_invalid_email)
                        buildAlertDialog(context, title, message)
                        hideLoadingDialog()
                    }

                    else -> {
                        hideLoadingDialog()
                    }
                }
            }
    }

    fun createDataOnFirestore(
        firstName: String,
        lastName: String,
        email: String
    ) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        val data = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email
        )
        val userRef = FirebaseFirestore.getInstance().collection("users").document(uid!!)

        userRef.set(data).addOnSuccessListener {

            configDisplayNameOnAuth(name = "$firstName $lastName")

            val categoriesCollection = userRef.collection("categories")

            val tobaccoDataCollection = categoriesCollection.document("tobacco").collection("data")
            val alcoholDataCollection = categoriesCollection.document("alcohol").collection("data")
            val partiesDataCollection = categoriesCollection.document("parties").collection("data")
            val othersDataCollection = categoriesCollection.document("others").collection("data")

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val tobaccoData = mapOf("value" to 0)
            tobaccoDataCollection.document(currentDate).set(tobaccoData)

            val alcoholData = mapOf("value" to 0)
            alcoholDataCollection.document(currentDate).set(alcoholData)

            val partiesData = mapOf("value" to 0)
            partiesDataCollection.document(currentDate).set(partiesData)

            val othersData = mapOf("value" to 0)
            othersDataCollection.document(currentDate).set(othersData)

        }
    }

    fun addDataToCategory(
        context: Context,
        category: String,
        amount: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val subCollectionName = when (category) {
            context.getString(R.string.tobacco) -> "tobacco"
            context.getString(R.string.alcohol) -> "alcohol"
            context.getString(R.string.parties) -> "parties"
            context.getString(R.string.others) -> "others"
            else -> "default"
        }

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val categoriesCollection =
            FirebaseFirestore.getInstance().collection("users").document(uid!!)
                .collection("categories")

        val categoryDataCollection =
            categoriesCollection.document(subCollectionName).collection("data")

        val currentDayDataDocument = categoryDataCollection.document(currentDate)

        currentDayDataDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentValue = documentSnapshot.getLong("value")?.toInt() ?: 0

                    val updatedValue = currentValue + amount.toFloat()

                    currentDayDataDocument.set(mapOf("value" to updatedValue))
                        .addOnSuccessListener {
                            Log.d("FirebaseUtils", "Data updated successfully")
                            onSuccess()
                        }
                        .addOnFailureListener {
                            Log.d("FirebaseUtils", "Error updating data")
                        }
                } else {
                    val newData = mapOf("value" to amount.toFloat())
                    currentDayDataDocument.set(newData)
                        .addOnSuccessListener {
                            Log.d("FirebaseUtils", "Data added successfully")
                        }
                        .addOnFailureListener {
                            Log.d("FirebaseUtils", "Error adding data")
                        }
                }
                onSuccess()
            }
            .addOnFailureListener {
                Log.d("FirebaseUtils", "Error getting data")
                onFailure(it)
            }
    }

    fun dataHandler(
        context: Context,
        days: Int,
        onSuccess: (Map<String, Float>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val categories = listOf(
            context.getString(R.string.tobacco),
            context.getString(R.string.alcohol),
            context.getString(R.string.parties),
            context.getString(R.string.others)
        )

        val dataMap = mutableMapOf<String, Float>()

        for (category in categories) {
            getDataFromFirestore(
                context = context,
                option = category,
                daysCount = days,
                onSuccess = { data ->
                    val totalValue = data.map { it.second }.sum()
                    dataMap[category] = totalValue

                    if (dataMap.size == categories.size) {
                        onSuccess(dataMap)
                    }
                },
                onFailure = { exception ->
                    onFailure(exception)
                }
            )
        }
    }

    private fun getDataFromFirestore(
        context: Context,
        option: String,
        daysCount: Int,
        onSuccess: (List<Pair<String, Float>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let {
            val userId = user.uid
            val subCollectionName = when (option) {
                context.getString(R.string.tobacco) -> "tobacco"
                context.getString(R.string.alcohol) -> "alcohol"
                context.getString(R.string.parties) -> "parties"
                context.getString(R.string.others) -> "others"
                else -> "default"
            }

            val categoriesCollection = db.collection("users").document(userId)
                .collection("categories").document(subCollectionName).collection("data")

            val dataList = mutableListOf<Pair<String, Float>>()
            val currentDate = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            for (i in 0 until daysCount) {
                val currentDateStr = dateFormat.format(currentDate.time)

                val documentRef = categoriesCollection.document(currentDateStr)
                documentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        val categoryName = documentRef.id
                        val categoryValue = documentSnapshot.getDouble("value")?.toFloat() ?: 0.0f
                        dataList.add(Pair(categoryName, categoryValue))

                        if (dataList.size == daysCount) {
                            onSuccess(dataList)
                        }
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }

                currentDate.add(Calendar.DAY_OF_YEAR, -1)
            }
        }
    }

    private fun deleteUserDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
    }

    private fun updateEmailOnFirestore(newEmail: String, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userDb = db.collection("users").document(currentUser.uid)
            userDb.update("email", newEmail)
        } else {
            onFailure(Exception("User is not logged in"))

        }
    }


    private fun buildAlertDialog(context: Context, title: String, message: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.ok)) { _, _ -> }
            .show()
    }

    fun showLoadingDialog(context: Context) {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setView(R.layout.loading_dialog)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog?.show()
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun signOut(context: Context) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(context, intent, null)
    }

    fun configPasswordOnAuth(newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener {
                onSuccess()
            }
            ?.addOnFailureListener { e ->
                onFailure(e)
            }

    }

    fun configEmailOnAuth(newEmail: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.updateEmail(newEmail)
            ?.addOnCompleteListener {
                updateEmailOnFirestore(newEmail, onFailure = { e -> onFailure(e) })
                onSuccess()
            }
            ?.addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun deleteAccount(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        deleteUserDataFromFirestore()
        currentUser?.delete()
            ?.addOnCompleteListener {
                onSuccess()
            }
            ?.addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun deleteHistory(days: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -days)
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val batch = db.batch()

        val categories = listOf(
            "tobacco",
            "alcohol",
            "parties",
            "others"
        )

        for (category in categories) {
            val categoryCollection = db.collection("users").document(userId)
                .collection("categories").document(category).collection("data")

            categoryCollection
                .whereGreaterThanOrEqualTo("date", dateFormat.format(currentDate))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        batch.delete(document.reference)
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

}
