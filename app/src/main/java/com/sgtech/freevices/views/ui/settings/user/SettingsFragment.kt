package com.sgtech.freevices.views.ui.settings.user

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils.deleteUserDataFromFirestore
import com.sgtech.freevices.utils.FirebaseUtils.updateEmailOnFirestore
import com.sgtech.freevices.views.LoginActivity

class SettingsFragment : PreferenceFragmentCompat() {

    private var currentUser: FirebaseUser? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Snackbar.make(requireView(), "Please sign in first", Snackbar.LENGTH_LONG).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        } else {
            val updateDisplayName = getPreference("update_name")
            val updateEmail = getPreference("update_email")
            val updatePassword = getPreference("update_password")
            val deleteAccount = getPreference("delete_account")

            updateDisplayName?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val title = "Update Display Name"
                val message = "Enter new display name"
                showSimpleAlertDialog(
                    title,
                    message
                )
                true
            }

            updateEmail?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val title = "Update Email"
                val message = "Enter new email"
                showSimpleAlertDialog(
                    title,
                    message
                )
                true
            }

            updatePassword?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                reAuthAlertDialog()
                true
            }

            deleteAccount?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                deleteAccountAlertDialog()
                true
            }
        }
    }

    private fun updateDisplayName(displayName: String) {
        currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(displayName).build()
        )
    }

    private fun updateEmail(email: String) {
        currentUser?.updateEmail(email)
        updateEmailOnFirestore(email, requireView(), requireContext())
    }

    private fun deleteAccount() {
        currentUser?.delete()
        FirebaseAuth.getInstance().signOut()
        activity?.finish()
    }

    private fun updatePassword(newPassword: String) {
        currentUser?.updatePassword(newPassword)
    }

    private fun showSimpleAlertDialog(
        title: String,
        message: String
    ) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val editText = TextInputEditText(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setView(editText)
        builder.setPositiveButton(UPDATE) { _, _ ->
            val newData = editText.text.toString()
            when (title) {
                "Update Name" -> {
                    updateDisplayName(newData)
                }
                "Update Email" -> {
                    updateEmail(newData)
                }
            }
        }
        builder.setNegativeButton(CANCEL) { _, _ ->
        }
        builder.show()
    }

    private fun reAuthAlertDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())

        val currentPasswordEditText = TextInputEditText(requireContext())
        val newPasswordEditText = TextInputEditText(requireContext())
        val confirmPasswordEditText = TextInputEditText(requireContext())

        builder.setTitle("Update Password")
        builder.setMessage("Enter current password")
        builder.setView(currentPasswordEditText)
        builder.setPositiveButton(UPDATE) { _, _ ->
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            if (newPassword == confirmPassword) {
                reAuthHandler(currentPassword) { success ->
                    if (success) {
                        updatePassword(newPassword)
                    }
                }
            }
            Snackbar.make(requireView(), "Passwords do not match", Snackbar.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(CANCEL) { _, _ ->
        }
        builder.show()
    }

    private fun deleteAccountAlertDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val deleteAccountPassword = TextInputEditText(requireContext())
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account?\nConfirm your password. This action cannot be undone.")
        builder.setView(deleteAccountPassword)
        builder.setPositiveButton(DELETE) { _, _ ->
            val password = deleteAccountPassword.text.toString()
            reAuthHandler(password) { success ->
                if (success) {
                    deleteUserDataFromFirestore()
                    deleteAccount()
                }
            }
        }
        builder.setNegativeButton(CANCEL) { _, _ ->
        }
        builder.show()
    }

    private fun reAuthHandler(currentPassword: String, reAuthCallback: (success: Boolean) -> Unit) {
        val credential =
            currentUser?.email?.let { EmailAuthProvider.getCredential(it, currentPassword) }
        if (credential != null) {
            currentUser?.reauthenticate(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reAuthCallback(true)
                    } else {
                        reAuthCallback(false)
                    }
                }
        }
    }

    private fun getPreference(key: String): Preference? {
        return findPreference(key)
    }

    companion object {
        private const val UPDATE = "Update"
        private const val DELETE = "Delete"
        private const val CANCEL = "Cancel"
    }
}