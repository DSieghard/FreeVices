package com.sgtech.freevices.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class OneTapSignInState {
    var opened: Boolean by mutableStateOf(false)

    fun open() {
        opened = true
    }

    fun close() {
        opened = false
    }
}