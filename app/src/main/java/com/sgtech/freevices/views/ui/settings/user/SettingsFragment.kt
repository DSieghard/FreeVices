package com.sgtech.freevices.views.ui.settings.user

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.sgtech.freevices.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        //set options in xml
        val updateDisplayName = findPreference<Preference>("update_name")
        val updateEmail = findPreference<Preference>("update_email")
        val updatePassword = findPreference<Preference>("update_password")
        val deleteAccount = findPreference<Preference>("delete_account")

        //When user press change Name Button, create a alert dialog to update display name
        updateDisplayName?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val title = "Update Display Name"
            val message = "Enter new display name"
            showAlertDialog(
                title,
                message,
                UPDATE,
                CANCEL)
            true
        }

        //When user press change Email Button, create a alert dialog to update email
        updateEmail?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val title = "Update Email"
            val message = "Enter new email"
            showAlertDialog(
                title,
                message,
                UPDATE,
                CANCEL
            )
            true
        }

        //When user press change Password Button, create a alert dialog to update password
        updatePassword?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val title = "Update Password"
            val message = "Enter new password"
            showAlertDialog(
                title,
                message,
                UPDATE,
                CANCEL
            )
            true
        }

        //When user press delete account Button, create a alert dialog to delete account
        deleteAccount?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val title = "Delete Account"
            val message = "Are you sure you want to delete your account?"
            showAlertDialog(
                title,
                message,
                DELETE,
                CANCEL
            )
            true
        }
    }

    //Create fun to update display name in Firebase
    fun updateDisplayName(displayName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(displayName).build()
        )
    }

    //Create fun to update email in Firebase
    private fun updateEmail(email: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.updateEmail(email)
    }

    private fun updatePassword(password: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.updatePassword(password)
    }

    //Create fun to update phone number in Firebase
    private fun updatePhoneNumber(phoneNumber: String) {
        //Todo update phone number
    }

    //Create fun to delete account, sign out and go back to login
    private fun deleteAccount() {
        FirebaseAuth.getInstance().currentUser?.delete()
        FirebaseAuth.getInstance().signOut()
        activity?.finish()
    }

    // Create contextual alert dialog with custom parameters
    private fun showAlertDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String
    ) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveButtonText) { _, _ ->
        }
        builder.setNegativeButton(negativeButtonText) { _, _ ->
            builder.show()
        }

    }

    companion object {
        private const val UPDATE = "Update"
        private const val DELETE = "Delete"
        private const val CANCEL = "Cancel"
    }
}