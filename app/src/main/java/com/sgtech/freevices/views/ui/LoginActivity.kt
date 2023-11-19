package com.sgtech.freevices.views.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
        val snackbarHostState = remember { SnackbarHostState() }
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
                        EmailEditText(email) { newValue -> email = newValue }
                        Spacer(modifier = Modifier.size(48.dp))
                        PasswordEditText(password) { newValue -> password = newValue }
                        Spacer(modifier = Modifier.size(128.dp))
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
                        TextButton(onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = getString(context, R.string.fill_all_the_fields),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                isLoading = true
                                scope.launch {
                                    FirebaseUtils.signInWithEmail(email, password, {
                                        isLoading = false
                                        isLoginOk = true
                                    }) {exception ->
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
                                                context.getString(R.string.unknown_error)
                                            }
                                        }
                                        isLoading = false
                                    }
                                    if (authError?.isNotEmpty() == true) {
                                        snackbarHostState.showSnackbar(
                                            message = authError!!,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }) {
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
}