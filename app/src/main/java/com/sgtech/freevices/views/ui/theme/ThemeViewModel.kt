package com.sgtech.freevices.views.ui.theme

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    private val _isDynamicColor = mutableStateOf(true)
    val isDynamicColor: State<Boolean> = _isDynamicColor

    fun setDynamicColor(value: Boolean) {
        _isDynamicColor.value = value
        Log.d("ThemeViewModel", "setDynamicColor: $value")
    }
}