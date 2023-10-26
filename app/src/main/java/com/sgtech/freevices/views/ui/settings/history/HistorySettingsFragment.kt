package com.sgtech.freevices.views.ui.settings.history

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.sgtech.freevices.R

class HistorySettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_history_preferences, rootKey)
    }
}