package com.sgtech.freevices.views.ui.settings.app

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sgtech.freevices.R

class AppSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_app_preferences, rootKey)

        val enableNotifications: Preference? = getPreferences("enable_notifications")
        val resetNotifications: Preference? = getPreferences("reset_notifications")
        val darkMode: Preference? = getPreferences("dark_mode")

        enableNotifications?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            return@OnPreferenceClickListener true
        }

        resetNotifications?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            return@OnPreferenceClickListener true
        }

        darkMode?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            return@OnPreferenceClickListener true
        }
    }

    private fun getPreferences(key: String): Preference? {
        return findPreference(key)
    }
}