@file:OptIn(ExperimentalMaterial3Api::class)

package com.sgtech.freevices.views.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

@Composable
fun LoginText(){
    Text(
        text = "Welcome to FreeVices\nSign in or Register",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(48.dp),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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

@Composable
fun LoginButton(email: String, password:  String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    if (isLoading) {
        LoadingDialog()
    }

    ElevatedButton(
        modifier = Modifier.padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.large,
        onClick = {
            isLoading = true
            scope.launch {
                FirebaseUtils.signInWithEmail(context, email, password,
                    onSuccess = {
                        isLoading = false
                    },
                    onFailure = {
                        isLoading = false
                    })
            }
        })
    {
        Text(
        text = stringResource(R.string.sign_in),
        textAlign = TextAlign.Center
    )
    }
}

@Composable
fun SignUpButton() {
    val context = LocalContext.current
    ElevatedButton(
        modifier = Modifier.padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.large,
        onClick = {
            val intent = android.content.Intent(context, CreateAccountActivity::class.java)
            context.startActivity(intent)
        }) {
        Text(
            text = stringResource(R.string.sign_up),
        )
    }
}


@Composable
fun LoadingDialog() {
    Row {
        Text(text = "Loading...", modifier = Modifier.align(Alignment.CenterVertically))
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview
@Composable
fun LoginPreview() {
    FreeVicesTheme {
        LocalContext.current
        val email = "email"
        val password = "password"
        Column {
            LoginText()
            Spacer(modifier = Modifier.size(72.dp))
            EmailEditText("") {}
            Spacer(modifier = Modifier.size(48.dp))
            PasswordEditText("") {}
            Spacer(modifier = Modifier.size(96.dp))
            Column(modifier = Modifier.padding(16.dp))
            {
                Row {
                    LoginButton(email, password)
                    Spacer(modifier = Modifier.size(24.dp))
                    SignUpButton()
                }
            }

        }
    }
}