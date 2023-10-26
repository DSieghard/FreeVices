package com.sgtech.freevices.views.ui.settings.app

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.sgtech.freevices.R

class AppSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_app_preferences, rootKey)
    }
}