package com.sgtech.freevices.utils

import android.content.Context

class PreferencesManager(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("FreeVicesPreferences", Context.MODE_PRIVATE)

    fun isFirstRun(): Boolean {
        return sharedPreferences.getBoolean("isFirstRun", true)
    }

    fun setFirstRun() {
        sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
    }
}
