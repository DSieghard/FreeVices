package com.sgtech.freevices.views.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme

@OptIn(ExperimentalMaterial3Api::class)
class CreateAccountActivity : AppCompatActivity() {

    private val themeViewModel = ViewModelProvider.provideThemeViewModel()
    private var isSignUpAttempted = false

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

        setContent{
            FreeVicesTheme(useDynamicColors = themeViewModel.isDynamicColor.value){
                CreateAccountScreen()
            }
        }
    }

    @Composable
    fun CreateAccountScreen() {
        val context = LocalContext.current
        var name: String? by remember { mutableStateOf("") }
        var lastName: String? by remember { mutableStateOf("") }
        var email: String? by remember { mutableStateOf("") }
        var password: String? by remember { mutableStateOf("") }
        var confirmPassword: String? by remember { mutableStateOf("") }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )
        var isHelpPressed by remember { mutableStateOf(false) }
        if (isHelpPressed) {
            HelpDialog(
                onDismissRequest = {
                    isHelpPressed = false
                },
                text = context.getString(R.string.create_account_help)
            )
        }
        var isLoading by rememberSaveable { mutableStateOf(false) }
        if (isLoading) {
            DialogForLoad { isLoading = false }
        }
        Scaffold(
            topBar = {
                MediumTopAppBar(title = { Text(text = getString(R.string.register_on_freevices),
                    style = MaterialTheme.typography.headlineMedium)
                },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(onClick = { isHelpPressed = true }) {
                            Icon(
                                imageVector = Icons.Filled.HelpOutline, contentDescription = stringResource(R.string.help_button)
                            )
                        }
                    })
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
                        NameEditText(name!!) { newData -> name = newData }
                        Spacer(modifier = Modifier.size(16.dp))
                        LastNameEditText(lastName!!) { newData -> lastName = newData }
                        Spacer(modifier = Modifier.size(16.dp))
                        EmailEditText(email!!) { newData -> email = newData }
                        Spacer(modifier = Modifier.size(16.dp))
                        PasswordEditText(password!!) { newData -> password = newData }
                        Spacer(modifier = Modifier.size(16.dp))
                        ConfirmPasswordEditText(confirmPassword!!) { newData -> confirmPassword = newData }
                    }
                }
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        TextButton(onClick = {
                            finish()
                        }) {
                            Text(text = stringResource(R.string.back),
                                style = MaterialTheme.typography.bodyLarge)
                        }
                    },
                    floatingActionButton = {
                        TextButton(onClick = {
                            isSignUpAttempted = true
                            if(name!!.isEmpty() || lastName!!.isEmpty() || email!!.isEmpty() || password!!.isEmpty() || confirmPassword!!.isEmpty()){
                                Toast.makeText(context, R.string.fill_all_the_fields, Toast.LENGTH_SHORT).show()
                            } else {
                                isLoading = true
                                createAccountHandler(name!!, lastName!!, email!!, password!!, confirmPassword!!, context,
                                    onSuccess = {
                                        isLoading = false
                                    },
                                    onFailure = {
                                        isLoading = false
                                    }
                                )
                            }
                        }) {
                            Text(text = stringResource(R.string.sign_up),
                                style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                )
            }
        )
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex(getString(R.string.valid_email_syntax))
        return email.matches(emailRegex)
    }

    private fun isPasswordValid(password: String): Boolean {
        val minLength = 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { it.isLetterOrDigit().not() }

        return password.length >= minLength &&
                hasUppercase &&
                hasLowercase &&
                hasDigit &&
                hasSpecialChar
    }

    private fun createAccountHandler(
        name: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        context: Context,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit
    ) {
        if (isEmailValid(email)) {
            if (isPasswordValid(password)) {
                if (password == confirmPassword) {
                    FirebaseUtils.createAccount(email, password, onSuccess = {
                        FirebaseUtils.createDataOnFirestore(name, lastName, email)
                        FirebaseUtils.signInWithEmail(email, password, onSuccess = {
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                val intent = Intent(context, NewMainActivity::class.java)
                                context.startActivity(intent)
                                onSuccess()
                                finish()
                            }
                        }, {
                            Toast.makeText(
                                context,
                                context.getString(R.string.unable_to_create_account),
                                Toast.LENGTH_LONG
                            ).show()
                            onFailure(it)
                        })
                    }) { e ->
                        when (e) {
                            is FirebaseAuthWeakPasswordException -> {
                                Toast.makeText(context, context.getString(R.string.password_too_weak), Toast.LENGTH_LONG).show()
                                onFailure(e)
                            }

                            is FirebaseAuthUserCollisionException -> {
                                Toast.makeText(context, context.getString(R.string.account_already_exists), Toast.LENGTH_LONG).show()
                                onFailure(e)
                            }

                            else -> {
                                Toast.makeText(context, context.getString(R.string.unable_to_create_account), Toast.LENGTH_LONG).show()
                                onFailure(e)
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.passwords_do_not_match), Toast.LENGTH_LONG).show()
                    onFailure(Exception())
                }
            } else {
                Toast.makeText(context, context.getString(R.string.password_too_weak), Toast.LENGTH_LONG).show()
                onFailure(Exception())
            }
        } else {
            Toast.makeText(context, context.getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show()
            onFailure(Exception())
        }
    }

    @Composable
    fun NameEditText(name: String, onValueChange: (value: String) -> Unit) {
        OutlinedTextField(value = name,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(id = R.string.first_name)) },
            singleLine = true,
            isError = isSignUpAttempted && name.isEmpty(),
            keyboardActions = KeyboardActions(onDone = {}),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )
    }

    @Composable
    fun LastNameEditText(name: String, onValueChange: (value: String) -> Unit) {
        OutlinedTextField(value = name,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(id = R.string.last_name))},
            singleLine = true,
            isError = isSignUpAttempted && name.isEmpty(),
            keyboardActions = KeyboardActions(onDone = {}),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )
    }

    @Composable
    fun EmailEditText(email: String, onValueChange: (value: String) -> Unit) {
        OutlinedTextField(value = email,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(id = R.string.email)) },
            singleLine = true,
            isError = isSignUpAttempted && email.isEmpty(),
            keyboardActions = KeyboardActions(onDone = {}),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email))
    }

    @Composable
    fun PasswordEditText(password: String, onValueChange: (value: String) -> Unit) {
        var passwordHidden by rememberSaveable { mutableStateOf(true) }
        OutlinedTextField(
            value = password,
            onValueChange = onValueChange,
            singleLine = true,
            label = { Text(stringResource(R.string.password)) },
            isError = isSignUpAttempted && password.isEmpty(),
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

    @Composable
    fun ConfirmPasswordEditText(password: String, onValueChange: (value: String) -> Unit) {
        var passwordHidden by rememberSaveable { mutableStateOf(true) }
        OutlinedTextField(
            value = password,
            onValueChange = onValueChange,
            singleLine = true,
            label = { Text(stringResource(id = R.string.confirm_password)) },
            isError = isSignUpAttempted && password.isEmpty(),
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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