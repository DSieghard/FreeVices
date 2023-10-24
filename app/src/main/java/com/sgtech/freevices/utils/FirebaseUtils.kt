package com.sgtech.freevices.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.sgtech.freevices.R
import com.sgtech.freevices.views.LoginActivity

object FirebaseUtils {
    fun checkIfUserIsLoggedIn(context: Context) {
        val welcomeText = context.getString(R.string.welcome)
        FirebaseAuth.getInstance().currentUser?.let {
            Toast.makeText(context, ("$welcomeText $it.displayName"), Toast.LENGTH_SHORT).show()
        } ?: run {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(context, intent, null)
        }
    }

    fun signInWithEmail(context: Context, email: String, password: String, callback: Boolean) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (callback) {
                        checkIfUserIsLoggedIn(context)
                    }
                } else {
                    Toast.makeText(context,
                        context.getString(R.string.invalid_credentials_or_account_not_found), Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun createAccount(context: Context, email: String, password: String, callback: Boolean) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (callback) {
                        checkIfUserIsLoggedIn(context)
                    }
                }
                else {
                    Toast.makeText(context, context.getString(R.string.invalid_credentials_or_account_not_found), Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun createDataOnFirestore(context: Context, firstName: String, lastName: String, email: String, phone: Int) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
            ?: run {
                Toast.makeText(
                    context,
                    context.getString(R.string.invalid_credentials_or_account_not_found),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        val uid = user.uid
        val data = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone
        )

        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)

        userRef.set(data)
            .addOnSuccessListener {
                val valuesCollection = userRef.collection("values")
                val initialValues = mapOf(
                    "tobacco" to 0,
                    "alcohol" to 0,
                    "parties" to 0,
                    "others" to 0
                )

                valuesCollection.document("categories")
                    .set(initialValues)
                    .addOnSuccessListener {
                        buildAlertDialog(context, "", context.getString(R.string.account_success))
                    }
                    .addOnFailureListener { e ->
                        buildAlertDialog(context, context.getString(R.string.account_create_error), e.message.toString())
                    }
            }
            .addOnFailureListener { e ->
                buildAlertDialog(context, context.getString(R.string.account_create_error), e.message.toString())
            }
    }


    fun getDataFromFirestore(onSuccess: (List<Pair<String, Float>>) -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val dataCollection = db.collection("data")

        val dataList = mutableListOf<Pair<String, Float>>()

        dataCollection.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val name = document.id
                    val valor = document.getDouble("valor")?.toFloat() ?: 0.0f
                    dataList.add(name to valor)
                }

                onSuccess(dataList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun buildAlertDialog(context: Context, title: String, message: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.ok)) { _, _ -> }
            .show()
    }
}
