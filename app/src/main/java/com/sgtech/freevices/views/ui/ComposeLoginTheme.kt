@file:OptIn(ExperimentalMaterial3Api::class)

package com.sgtech.freevices.views.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.R

@Composable
fun EmailEditText(email: String, onValueChange: (value: String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = email,
            onValueChange = onValueChange,
            label = { Text(stringResource(id = R.string.email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next),
        )
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordEditText(password: String, onValueChange: (value: String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = password,
            onValueChange = onValueChange,
            singleLine = true,
            label = { Text(stringResource(R.string.enter_password)) },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIcon =
                        if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordHidden) stringResource(R.string.show_password) else stringResource(R.string.hide_password)
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }
        )
    }

}