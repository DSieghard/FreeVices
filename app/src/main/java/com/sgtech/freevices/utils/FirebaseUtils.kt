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
import com.sgtech.freevices.views.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch

object FirebaseUtils {
    private var loadingDialog: AlertDialog? = null
    fun checkIfUserIsLoggedIn(context: Context) {
        context.getString(R.string.welcome)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val welcome = context.getString(R.string.welcome, currentUser.email)
            Log.d("FirebaseUtils", welcome)
            val intent = Intent(context, MainActivity::class.java)
            startActivity(context, intent, null)
        } else {
            Log.d("FirebaseUtils", "User is not logged in")
        }

    }
    fun signInWithEmail(context: Context, email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        showLoadingDialog(context)
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    try {
                        checkIfUserIsLoggedIn(context)
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(context, intent, null)
                        hideLoadingDialog()
                    } catch (e: FirebaseAuthInvalidUserException) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_user_not_found)
                        buildAlertDialog(context, title, message)
                        Log.d("FirebaseUtils", "signInWithEmail:failure", e)
                        hideLoadingDialog()
                    } catch (ec: FirebaseAuthActionCodeException) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_invalid_token)
                        buildAlertDialog(context, title, message)
                        Log.d("FirebaseUtils", "signInWithEmail:failure", ec)
                        hideLoadingDialog()
                    } catch (e: Exception) {
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_unknown)
                        buildAlertDialog(context, title, message)
                        Log.d("FirebaseUtils", "signInWithEmail:failure", e)
                        hideLoadingDialog()
                    } finally {
                        hideLoadingDialog()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                Log.d("FirebaseUtils", e.message.toString())
                Log.d("FirebaseUtils", "signInWithEmail:failure")
            }
    }

    fun createAccount(context: Context, email: String, password: String, onSuccess: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        showLoadingDialog(context)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseUtils", "createAccount:success")
                    hideLoadingDialog()
                    onSuccess()
                    startActivity(context, Intent(context, MainActivity::class.java), null)
                }
            }
            .addOnFailureListener { e ->
                when (e) {
                    is FirebaseAuthUserCollisionException -> {
                        Log.d("FirebaseUtils", "createAccount: User collision exception")
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_user_already_exists)
                        buildAlertDialog(context, title, message)
                        hideLoadingDialog()
                    }
                    is FirebaseAuthInvalidUserException -> {
                        Log.d("FirebaseUtils", "createAccount: Invalid user exception", e)
                        val title = context.getString(R.string.error)
                        val message = context.getString(R.string.error_invalid_email)
                        buildAlertDialog(context, title, message)
                        hideLoadingDialog()
                    }
                    else -> {
                        Log.d("FirebaseUtils", "createAccount: General exception", e)
                        hideLoadingDialog()
                    }
                }
            }
    } // Ok


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

    fun addDataToCategory(context: Context, option: String, dataValue: Int, rootView: View) {
        val subcollectionName = when (option) {
            context.getString(R.string.tobacco) -> "tobacco"
            context.getString(R.string.alcohol) -> "alcohol"
            context.getString(R.string.parties) -> "parties"
            context.getString(R.string.others) -> "others"
            else -> "default" // Otra opción predeterminada si es necesario
        }

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Obtiene una referencia a la colección "categories" para el usuario actual
        val categoriesCollection = FirebaseFirestore.getInstance().collection("users").document(uid!!)
            .collection("categories")

        // Obtiene una referencia a la subcolección "data" de la categoría específica
        val categoryDataCollection = categoriesCollection.document(subcollectionName).collection("data")

        // Obtiene una referencia al documento correspondiente al día actual en la subcolección "data"
        val currentDayDataDocument = categoryDataCollection.document(currentDate)

        // Verifica si el documento ya existe
        currentDayDataDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // El documento ya existe, obtén el valor actual
                    val currentValue = documentSnapshot.getLong("value")?.toInt() ?: 0

                    // Suma el nuevo valor al valor actual
                    val updatedValue = currentValue + dataValue

                    // Actualiza el valor en la base de datos
                    currentDayDataDocument.set(mapOf("value" to updatedValue))
                        .addOnSuccessListener {
                            Snackbar.make(rootView, "Data updated successfully", Snackbar.LENGTH_SHORT).show()
                            Log.d("FirebaseUtils", "Data updated successfully for $subcollectionName on $currentDate")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseUtils", "Failed to update data for $subcollectionName on $currentDate: $e")
                        }
                } else {
                    // El documento no existe, crea uno nuevo con el valor proporcionado
                    val newData = mapOf("value" to dataValue)
                    currentDayDataDocument.set(newData)
                        .addOnSuccessListener {
                            Snackbar.make(rootView, "Data added successfully", Snackbar.LENGTH_SHORT).show()
                            Log.d("FirebaseUtils", "Data added successfully to $subcollectionName on $currentDate")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseUtils", "Failed to add data to $subcollectionName on $currentDate: $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUtils", "Error checking for existing data: $e")
            }
    }

    fun dataHandler(context: Context, onSuccess: (Map<String, Float>) -> Unit) {
        val categories = listOf(
            context.getString(R.string.tobacco),
            context.getString(R.string.alcohol),
            context.getString(R.string.parties),
            context.getString(R.string.others)
        )

        val dataMap = mutableMapOf<String, Float>()
        val callbackCountdown = CountDownLatch(categories.size)

        for (category in categories) {
            getDataFromFirestoreForLastWeek(
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
                        onSuccess(dataMap)
                    }

                }
            ) { exception ->
                Log.e("TAG", "Error retrieving data for $category: $exception")
                callbackCountdown.countDown()
                if (callbackCountdown.count.toInt() == 0) {
                    onSuccess(dataMap)
                }
            }
        }
    }


    fun getDataFromFirestoreForLastWeek(
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
            Log.d("FirebaseUtils", userId)
            val subcollectionName = when (option) {
                context.getString(R.string.tobacco) -> "tobacco"
                context.getString(R.string.alcohol) -> "alcohol"
                context.getString(R.string.parties) -> "parties"
                context.getString(R.string.others) -> "others"
                else -> "default"
            }

            val categoriesCollection = db.collection("users").document(userId)
                .collection("categories").document(subcollectionName).collection("data")


            val dataList = mutableListOf<Pair<String, Float>>()

            val currentDate = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            for (i in 0 until 7) {
                val currentDateStr = dateFormat.format(currentDate.time)

                // Crea una referencia al documento para el día actual
                val documentRef = categoriesCollection.document(currentDateStr)
                Log.d("FirebaseUtils", currentDateStr)

                documentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        val categoryName = documentRef.id // El ID del documento es la fecha (currentDateStr)
                        val categoryValue = documentSnapshot.getDouble("value")?.toFloat() ?: 0.0f
                        dataList.add(Pair(categoryName, categoryValue))
                        Log.d("FirebaseUtils", "$categoryName: $categoryValue")
                        Log.d("FirebaseUtils", "$dataList")

                        // Verifica si se han obtenido todos los datos, independientemente del número
                        if (dataList.size == 7) {
                            onSuccess(dataList)
                        }
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                        Log.d("FirebaseUtils", "Error retrieving data: $exception")
                    }

                currentDate.add(Calendar.DAY_OF_YEAR, -1)
            }
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
}
