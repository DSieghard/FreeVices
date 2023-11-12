package com.sgtech.freevices.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.LoginActivity
import com.sgtech.freevices.views.ui.NewMainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch

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
    fun signInWithEmail(context: Context, email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    try {
                        checkIfUserIsLoggedIn(context)
                        val intent = Intent(context, NewMainActivity::class.java)
                        startActivity(context, intent, null)
                    } catch (e: FirebaseAuthInvalidUserException) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_user_not_found)
                        buildAlertDialog(context, title, message)
                    } catch (ec: FirebaseAuthActionCodeException) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_invalid_token)
                        buildAlertDialog(context, title, message)
                    } catch (e: Exception) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_unknown)
                        buildAlertDialog(context, title, message)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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
        username: String,
        email: String,
        phone: Int
    ) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        val data = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "username" to username,
            "email" to email,
            "phone" to phone
        )
        val userRef = FirebaseFirestore.getInstance().collection("users").document(uid!!)

        userRef.set(data).addOnSuccessListener {
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
    )  {
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

        val categoriesCollection = FirebaseFirestore.getInstance().collection("users").document(uid!!)
            .collection("categories")

        val categoryDataCollection = categoriesCollection.document(subCollectionName).collection("data")

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

    fun dataHandlerForWeek(
        context: Context,
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
            getDataFromFirestoreForLastWeek(
                context = context,
                option = category,
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


    fun dataHandlerCurrentMonth(context: Context, onSuccess: (Map<String, Float>) -> Unit) {
        val categories = listOf(
            context.getString(R.string.tobacco),
            context.getString(R.string.alcohol),
            context.getString(R.string.parties),
            context.getString(R.string.others)
        )

        val dataMap = mutableMapOf<String, Float>()
        val callbackCountdown = CountDownLatch(categories.size)

        for (category in categories) {
            getDataFromFirestoreForCurrentMonth(
                context = context,
                option = category,
                onSuccess = { data ->
                    if (data.isNotEmpty()) {
                        val totalValue = data.map { it.second }.sum()
                        dataMap[category] = totalValue
                    } else {
                        dataMap[category] = 0.0f
                    }

                    callbackCountdown.countDown()
                    if (callbackCountdown.count.toInt() == 0) {
                        Log.d("FirebaseUtils: dataHandlerCurrentMonth/onSuccess", dataMap.toString())
                        onSuccess(dataMap)
                    }

                }
            ) {
                callbackCountdown.countDown()
                if (callbackCountdown.count.toInt() == 0) {
                    Log.d("FirebaseUtils: dataHandlerCurrentMonth/onFailure", dataMap.toString())
                    onSuccess(dataMap)
                }
            }
        }
    }

    fun deleteUserDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
    }

    fun updateEmailOnFirestore(newEmail: String, view: View, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userDb = db.collection("users").document(currentUser.uid)

            userDb.update("email", newEmail)
                .addOnSuccessListener {
                    Snackbar.make(view,
                        context.getString(R.string.email_updated_successfully), Snackbar.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    val errorMessage = e.message ?: context.getString(R.string.error_updating_email)
                    Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show()
                }
        } else {
            Snackbar.make(view, context.getString(R.string.error_updating_email), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun getDataFromFirestoreForLastWeek(
        context: Context,
        option: String,
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

            for (i in 0 until 7) {
                val currentDateStr = dateFormat.format(currentDate.time)

                val documentRef = categoriesCollection.document(currentDateStr)
                documentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        val categoryName = documentRef.id
                        val categoryValue = documentSnapshot.getDouble("value")?.toFloat() ?: 0.0f
                        dataList.add(Pair(categoryName, categoryValue))

                        if (dataList.size == 7) {
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

    private fun getDataFromFirestoreForCurrentMonth(
        context: Context,
        option: String,
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
            // db route: /users/{userId}/categories/{category}/data/ -> That's right

            val dataList = mutableListOf<Pair<String, Float>>()
            val currentDate = Calendar.getInstance()
            currentDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)) // Same format as week
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            for (i in 0 until currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                val currentDateStr = dateFormat.format(currentDate.time) // Works as expected

                val documentRef = categoriesCollection.document(currentDateStr) // All right here
                documentRef.get()
                    //This block is not running
                    .addOnSuccessListener { documentSnapshot ->
                        val categoryName = documentRef.id
                        val categoryValue = documentSnapshot.getDouble("value")?.toFloat() ?: 0.0f
                        dataList.add(Pair(categoryName, categoryValue))
                        try {
                            onSuccess(dataList)
                        } catch (e: Exception) {
                            onFailure(Exception())
                        } finally {
                            onSuccess(dataList)
                        }

                    }
                    // Nor this
                    .addOnFailureListener { e ->
                        onFailure(e)
                        Log.d("FirebaseUtils: dataHandlerCurrentMonth/onFailure", e.toString())
                    }

                currentDate.add(Calendar.DAY_OF_YEAR, -1)
            }
        }
    }



    private fun deleteHistory30Days(category: String, context: Context, view: View) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return

        val currentDate = Calendar.getInstance().time

        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        val thirtyDaysAgo = calendar.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val categoriesCollection = db.collection("users").document(userId)
            .collection("categories").document(category).collection("data")

        categoriesCollection
            .whereGreaterThanOrEqualTo("date", dateFormat.format(thirtyDaysAgo))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { exception ->
                Snackbar.make(view, context.getString(R.string.error_deleting_data, exception), Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun deleteHistory60Days(category: String, context: Context, view: View) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return

        val currentDate = Calendar.getInstance().time

        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, -60)
        val sixtyDaysAgo = calendar.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val categoriesCollection = db.collection("users").document(userId)
            .collection("categories").document(category).collection("data")

        categoriesCollection
            .whereGreaterThanOrEqualTo("date", dateFormat.format(sixtyDaysAgo))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { exception ->
                Snackbar.make(view, context.getString(R.string.error_deleting_data, exception), Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun deleteHistory90Days(category: String, context: Context, view: View) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return

        val currentDate = Calendar.getInstance().time

        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, -90)
        val ninetyDaysAgo = calendar.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val categoriesCollection = db.collection("users").document(userId)
            .collection("categories").document(category).collection("data")

        categoriesCollection
            .whereGreaterThanOrEqualTo("date", dateFormat.format(ninetyDaysAgo))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { exception ->
                Snackbar.make(view, context.getString(R.string.error_deleting_data, exception), Snackbar.LENGTH_SHORT).show()
            }
    }

    fun delete30Days(view: View, context: Context) {
        deleteHistory30Days("tobacco", context, view)
        deleteHistory30Days("alcohol", context, view)
        deleteHistory30Days("parties", context, view)
        deleteHistory30Days("others", context, view)
        Snackbar.make(view, context.getString(R.string.data_deleted), Snackbar.LENGTH_SHORT).show()
    }

    fun delete60Days(view: View, context: Context) {
        deleteHistory60Days("tobacco", context, view)
        deleteHistory60Days("alcohol", context, view)
        deleteHistory60Days("parties", context, view)
        deleteHistory60Days("others", context, view)
        Snackbar.make(view, context.getString(R.string.data_deleted), Snackbar.LENGTH_SHORT).show()
    }

    fun delete90Days(view: View, context: Context) {
        deleteHistory90Days("tobacco", context, view)
        deleteHistory90Days("alcohol", context, view)
        deleteHistory90Days("parties", context, view)
        deleteHistory90Days("others", context, view)
        Snackbar.make(view, context.getString(R.string.data_deleted), Snackbar.LENGTH_SHORT).show()
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

    fun signOut(context: Context){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(context, intent, null)
    }

}
