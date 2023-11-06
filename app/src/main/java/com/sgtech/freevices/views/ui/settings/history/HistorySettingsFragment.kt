package com.sgtech.freevices.views.ui.settings.history

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils

class HistorySettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_history_preferences, rootKey)

        val delete30Days: Preference? = getPreference("delete_history_30_days")
        val delete60Days: Preference? = getPreference("delete_history_60_days")
        val delete90Days: Preference? = getPreference("delete_history_90_days")
        val deleteAll: Preference? = getPreference("delete_all_history")

        delete30Days?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val title = getString(R.string.delete_last_30_days)
            val message = "Are you sure you want to delete the last 30 days of history?"
            alertDialogDeleteHistory(title, message)
            return@OnPreferenceClickListener true
        }

        delete60Days?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val title = getString(R.string.delete_last_60_days)
            val message = "Are you sure you want to delete the last 60 days of history?"
            alertDialogDeleteHistory(title, message)
            return@OnPreferenceClickListener true
        }

        delete90Days?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val title = getString(R.string.delete_last_90_days)
            val message = "Are you sure you want to delete the last 90 days of history?"
            alertDialogDeleteHistory(title, message)
            return@OnPreferenceClickListener true
        }

        deleteAll?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val title = getString(R.string.delete_all)
            val message = "Are you sure you want to delete all history?"
            alertDialogDeleteHistory(title, message)
            return@OnPreferenceClickListener true
        }
    }

    private fun getPreference(key: String): Preference? {
        return findPreference(key)
    }

    private fun alertDialogDeleteHistory(title: String, message: String) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(DELETE) { _, _ ->
            when (title) {
                getString(R.string.delete_last_30_days) -> {
                    FirebaseUtils.delete30Days(requireView(), requireContext())
                }
                getString(R.string.delete_last_60_days) -> {
                    FirebaseUtils.delete60Days(requireView(), requireContext())
                }
                getString(R.string.delete_last_90_days) -> {
                    FirebaseUtils.delete90Days(requireView(), requireContext())
                }
                getString(R.string.delete_all) -> {
                }
            }
        }
        builder.setNegativeButton(CANCEL) { _, _ ->
        }
        builder.show()
    }

    companion object {
        private const val DELETE = "Delete"
        private const val CANCEL = "Cancel"
    }
}