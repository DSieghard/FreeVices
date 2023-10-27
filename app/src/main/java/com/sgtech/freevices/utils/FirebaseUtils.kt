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
import com.google.firebase.firestore.FirebaseFirestore
import com.sgtech.freevices.R
import com.sgtech.freevices.views.MainActivity

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

            val tobaccoData = mapOf("value" to 0)
            val alcoholData = mapOf("value" to 0)
            val partiesData = mapOf("value" to 0)
            val othersData = mapOf("value" to 0)

            categoriesCollection.document("tobacco").set(tobaccoData)
                .addOnSuccessListener {
                    Log.d("FirebaseUtils", "Tobacco data created successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUtils", "Failed to create tobacco data: $e")
                }

            categoriesCollection.document("alcohol").set(alcoholData)
                .addOnSuccessListener {
                    Log.d("FirebaseUtils", "Alcohol data created successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUtils", "Failed to create alcohol data: $e")
                }

            categoriesCollection.document("parties").set(partiesData)
                .addOnSuccessListener {
                    Log.d("FirebaseUtils", "Parties data created successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUtils", "Failed to create parties data: $e")
                }

            categoriesCollection.document("others").set(othersData)
                .addOnSuccessListener {
                    Log.d("FirebaseUtils", "Others data created successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUtils", "Failed to create others data: $e")
                }
        }
    }


    fun getDataFromFirestore(onSuccess: (List<Pair<String, Float>>) -> Unit, onFailure: (Exception) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let {
            val userId = user.uid
            val categoriesCollection = db.collection("users").document(userId)
                .collection("categories")

            val dataList = mutableListOf<Pair<String, Float>>()

            categoriesCollection.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val categoryName = document.id
                        val categoryValue = document.getDouble("value")

                        if (categoryValue != null) {
                            dataList.add(Pair(categoryName, categoryValue.toFloat()))
                        }
                    }

                    onSuccess(dataList)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
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
