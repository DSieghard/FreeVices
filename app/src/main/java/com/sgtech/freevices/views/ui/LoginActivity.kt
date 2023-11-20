package com.sgtech.freevices.views.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.utils.FirebaseUtils.isUserLoggedIn
import com.sgtech.freevices.utils.PreferencesManager
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private val themeViewModel = ViewModelProvider.provideThemeViewModel()
    private val snackbarHostState: SnackbarHostState = SnackbarHostState()
    private var isEmailError: Boolean = false
    private var isPasswordError: Boolean = false

    override fun onStart() {
        super.onStart()
        isUserLoggedIn()
        if (isUserLoggedIn()) {
            val intent = Intent(this, NewMainActivity::class.java)
            startActivity(intent)
            finish()
        }
        preferencesManager = PreferencesManager(this)
        if (preferencesManager.isFirstRun()) {
            val intent = Intent(this, WelcomeActivity::class.java)
            preferencesManager.setFirstRun()
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.hashCode(),
                Color.Transparent.hashCode()
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.Transparent.hashCode(),
                Color.Transparent.hashCode()
            ),
        )
        setContent {
            FreeVicesTheme(useDynamicColors = themeViewModel.isDynamicColor.value) {
                LoginScreenView()
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginScreenView() {
        val context = LocalContext.current
        val activity = LocalContext.current as Activity
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        val scope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(false) }
        if (isLoading) {
            DialogForLoad { isLoading = false }
        }
        var isLoginOk by remember { mutableStateOf(false) }
        if (isLoginOk) {
            val intent = Intent(context, NewMainActivity::class.java)
            startActivity(context, intent, null)
            isLoginOk = false
            activity.finish()
        }
        var authError: String? by remember { mutableStateOf(null) }
        var isResetPasswordRequested by remember { mutableStateOf(false) }
        if (isResetPasswordRequested) {
            ResetPasswordDialog { isResetPasswordRequested = false }
        }

        Scaffold(
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.welcome_to_freevices),
                            style = MaterialTheme.typography.headlineLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            content = { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        AnimatedVisibility(visible = isResetPasswordRequested) {
                            ResetPasswordDialog { isResetPasswordRequested = false }
                        }

                        AnimatedVisibility(visible = !isResetPasswordRequested) {
                            EmailEditText(email) { newValue -> email = newValue }
                        }

                        AnimatedVisibility(visible = !isResetPasswordRequested) {
                            PasswordEditText(password) { newValue -> password = newValue }
                        }

                        Spacer(modifier = Modifier.size(128.dp))

                        AnimatedVisibility(visible = !isResetPasswordRequested) {
                            TextButton(onClick = {
                                isResetPasswordRequested = true
                            }, modifier = Modifier.padding(14.dp)) {
                                Text(text = stringResource(R.string.forgot_password))
                            }
                        }
                    }
                }
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        TextButton(onClick = {
                            val intent =
                                Intent(
                                    context,
                                    CreateAccountActivity::class.java
                                )
                            context.startActivity(intent)
                        }) {
                            Text(
                                text = stringResource(R.string.sign_up),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                    floatingActionButton = {
                        TextButton(
                            onClick = {
                                when {
                                    email.isEmpty() || password.isEmpty() -> {
                                        isEmailError = true
                                        isPasswordError = true
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = getString(context, R.string.fill_all_the_fields),
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                    email.isEmpty() -> {
                                        isEmailError = true
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = getString(context, R.string.fill_all_the_fields),
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                    password.isEmpty() -> {
                                        isPasswordError = true
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = getString(context, R.string.fill_all_the_fields),
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                    else -> {
                                        isLoading = true
                                        scope.launch {
                                            FirebaseUtils.signInWithEmail(email, password, {
                                                isLoading = false
                                                isLoginOk = true
                                            }) { exception ->
                                                authError = when (exception) {
                                                    is FirebaseAuthInvalidUserException -> {
                                                        context.getString(R.string.user_inexistent)
                                                    }
                                                    is FirebaseAuthInvalidCredentialsException -> {
                                                        context.getString(R.string.password_not_match)
                                                    }
                                                    is FirebaseAuthActionCodeException -> {
                                                        context.getString(R.string.no_connection_error)
                                                    }
                                                    else -> {
                                                        context.getString(R.string.password_not_match)
                                                    }
                                                }
                                                isLoading = false
                                                if (authError?.isNotEmpty() == true) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            message = authError!!,
                                                            duration = SnackbarDuration.Short
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.sign_in),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        )
    }

    @Composable
    fun EmailEditText(email: String, onValueChange: (value: String) -> Unit) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = onValueChange,
                label = { Text(stringResource(R.string.email)) },
                isError = isEmailError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next),
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
            OutlinedTextField(
                value = password,
                onValueChange = onValueChange,
                singleLine = true,
                isError = isPasswordError,
                label = { Text(stringResource(R.string.enter_password)) },
                visualTransformation =
                if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done),
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
    fun ResetPasswordDialog(onDismissRequest: () -> Unit) {
        var email: String? by remember { mutableStateOf("") }
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        var isEmailError by remember { mutableStateOf(false) }
        var isPasswordResetRequested by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = { Icon(Icons.Filled.Email, contentDescription = null) },
            title = {
                Text(text = stringResource(R.string.reset_password))
            },
            text = {
                OutlinedTextField(
                    value = email!!,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.enter_your_email)) },
                    isError = isEmailError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (email!!.isEmpty()) {
                            isEmailError = true
                            scope.launch {
                                Toast.makeText(context, getString(R.string.fill_all_the_fields), Toast.LENGTH_LONG).show()
                            }
                        } else {
                            isEmailError = false
                            FirebaseUtils.resetPasswordRequest(email!!, onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = getString(R.string.password_reset_email_sent),
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                }
                            },
                                onFailure = {
                                    val message = when (it) {
                                        is FirebaseAuthInvalidUserException -> {
                                            getString(R.string.user_inexistent)
                                        }

                                        is FirebaseAuthInvalidCredentialsException -> {
                                            getString(R.string.invalid_email)
                                        }

                                        is FirebaseAuthActionCodeException -> {
                                            getString(R.string.no_connection_error)
                                        }

                                        else -> {
                                            getString(R.string.error_resetting_password)
                                        }
                                    }
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short,
                                            withDismissAction = true
                                        )
                                    }
                                })
                            isPasswordResetRequested = false
                        }
                    }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}