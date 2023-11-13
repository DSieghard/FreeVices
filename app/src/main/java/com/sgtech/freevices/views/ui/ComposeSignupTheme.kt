package com.sgtech.freevices.views.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.sgtech.freevices.R

@OptIn(ExperimentalMaterial3Api::class)
object ComposeSignupTheme {


    @Composable
    fun NameEditText(name: String, onValueChange: (value: String) -> Unit) {
        OutlinedTextField(value = name, onValueChange = onValueChange, label = { Text(text = stringResource(
            R.string.first_name)) })
    }

    @Composable
    fun LastNameEditText(name: String, onValueChange: (value: String) -> Unit) {
        OutlinedTextField(value = name, onValueChange = onValueChange, label = { Text(text = stringResource(
            id = R.string.last_name
        )) })
    }

    @Composable
    fun EmailEditText(email: String, onValueChange: (value: String) -> Unit) {
        OutlinedTextField(value = email, onValueChange = onValueChange, label = { Text(text = stringResource(
            id = R.string.email
        )) })
    }

    @Composable
    fun PasswordEditText(password: String, onValueChange: (value: String) -> Unit) {
        var passwordHidden by rememberSaveable { mutableStateOf(true) }
        OutlinedTextField(
            value = password,
            onValueChange = onValueChange,
            singleLine = true,
            label = { Text(stringResource(R.string.password)) },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIcon =
                        if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }
        )
    }

    @Composable
    fun ConfirmPasswordEditText(password: String, onValueChange: (value: String) -> Unit) {
        var passwordHidden by rememberSaveable { mutableStateOf(true) }
        OutlinedTextField(
            value = password,
            onValueChange = onValueChange,
            singleLine = true,
            label = { Text(stringResource(id = R.string.confirm_password)) },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIcon =
                        if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }
        )
    }

}
