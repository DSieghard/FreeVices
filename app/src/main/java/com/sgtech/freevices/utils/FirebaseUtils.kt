package com.sgtech.freevices.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.sgtech.freevices.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FirebaseUtils {

    fun isUserLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
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

    fun updateDisplayNameOnFirestore(firstName: String, lastName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        val userId = user?.uid

        if (userId != null) {
            val userDb = db.collection(USERS).document(userId)
            userDb.update(FIRST_NAME, firstName)
            userDb.update(LAST_NAME, lastName)
            onSuccess()
        } else {
            onFailure(Exception())
        }

    }

    fun createAccount(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
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
            FIRST_NAME to firstName,
            LAST_NAME to lastName,
            EMAIL to email
        )
        val userRef = FirebaseFirestore.getInstance().collection(USERS).document(uid!!)

        userRef.set(data)
            .addOnSuccessListener {
                configDisplayNameOnAuth(name = "$firstName $lastName")

                val categoriesCollection = userRef.collection(CATEGORIES)

                val tobaccoDataCollection = categoriesCollection.document(TOBACCO).collection(DATA)
                val alcoholDataCollection = categoriesCollection.document(ALCOHOL).collection(DATA)
                val partiesDataCollection = categoriesCollection.document(PARTIES).collection(DATA)
                val othersDataCollection = categoriesCollection.document(OTHERS).collection(DATA)
                val currentDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
                val tobaccoData = mapOf(VALUE to 0)
                tobaccoDataCollection.document(currentDate).set(tobaccoData)
                val alcoholData = mapOf(VALUE to 0)
                alcoholDataCollection.document(currentDate).set(alcoholData)
                val partiesData = mapOf(VALUE to 0)
                partiesDataCollection.document(currentDate).set(partiesData)
                val othersData = mapOf(VALUE to 0)
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
            context.getString(R.string.tobacco) -> TOBACCO
            context.getString(R.string.alcohol) -> ALCOHOL
            context.getString(R.string.parties) -> PARTIES
            context.getString(R.string.others) -> OTHERS
            else -> null
        }

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        val currentDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())

        val categoriesCollection =
            FirebaseFirestore.getInstance().collection(USERS).document(uid!!)
                .collection(CATEGORIES)

        val categoryDataCollection =
            categoriesCollection.document(subCollectionName!!).collection(DATA)

        val currentDayDataDocument = categoryDataCollection.document(currentDate)

        currentDayDataDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentValue = documentSnapshot.getLong(VALUE)?.toInt() ?: 0

                    val updatedValue = currentValue + amount.toFloat()

                    currentDayDataDocument.set(mapOf(VALUE to updatedValue))
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onFailure(it)
                        }
                } else {
                    val newData = mapOf(VALUE to amount.toFloat())
                    currentDayDataDocument.set(newData)
                }
                onSuccess()
            }
            .addOnFailureListener {
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
                context.getString(R.string.tobacco) -> TOBACCO
                context.getString(R.string.alcohol) -> ALCOHOL
                context.getString(R.string.parties) -> PARTIES
                context.getString(R.string.others) -> OTHERS
                else -> null
            }

            val categoriesCollection = db.collection(USERS).document(userId)
                .collection(CATEGORIES).document(subCollectionName!!).collection(DATA)

            val dataList = mutableListOf<Pair<String, Float>>()
            val currentDate = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

            for (i in 0 until daysCount) {
                val currentDateStr = dateFormat.format(currentDate.time)

                val documentRef = categoriesCollection.document(currentDateStr)
                documentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        val categoryName = documentRef.id
                        val categoryValue = documentSnapshot.getDouble(VALUE)?.toFloat() ?: 0.0f
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
        db.collection(USERS).document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
    }

    private fun updateEmailOnFirestore(newEmail: String, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userDb = db.collection(USERS).document(currentUser.uid)
            userDb.update(EMAIL, newEmail)
                .addOnFailureListener { onFailure(it) }
        }
    }

    fun signOut(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            FirebaseAuth.getInstance().signOut()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
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
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        val batch = db.batch()

        val categories = listOf(
            TOBACCO,
            ALCOHOL,
            PARTIES,
            OTHERS
        )

        for (category in categories) {
            val categoryCollection = db.collection(USERS).document(userId)
                .collection(CATEGORIES).document(category).collection(DATA)

            categoryCollection
                .whereGreaterThanOrEqualTo(DATE, dateFormat.format(currentDate))
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

    private const val DATA = "data"
    private const val TOBACCO = "tobacco"
    private const val ALCOHOL = "alcohol"
    private const val PARTIES = "parties"
    private const val OTHERS = "others"
    private const val CATEGORIES = "categories"
    private const val VALUE = "value"
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val FIRST_NAME = "firstName"
    private const val LAST_NAME = "lastName"
    private const val EMAIL = "email"
    private const val USERS = "users"
    private const val DATE = "date"

}
