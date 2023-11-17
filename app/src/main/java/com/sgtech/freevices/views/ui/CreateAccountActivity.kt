package com.sgtech.freevices.views.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class CreateAccountActivity : AppCompatActivity() {
    private val themeViewModel = ViewModelProvider.provideThemeViewModel()

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
        var name by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            topBar = {
                MediumTopAppBar(title = { Text(text = getString(R.string.register_on_freevices),
                    style = MaterialTheme.typography.headlineMedium)
                },
                    scrollBehavior = scrollBehavior)
            },
            snackbarHost =  { SnackbarHost(snackbarHostState) },
            content = { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        ComposeSignupTheme.NameEditText(name) { newData -> name = newData }
                        Spacer(modifier = Modifier.size(16.dp))
                        ComposeSignupTheme.LastNameEditText(lastName) { newData -> lastName = newData }
                        Spacer(modifier = Modifier.size(16.dp))
                        ComposeSignupTheme.EmailEditText(email) { newData -> email = newData }
                        Spacer(modifier = Modifier.size(16.dp))
                        ComposeSignupTheme.PasswordEditText(password) { newData -> password = newData }
                        Spacer(modifier = Modifier.size(16.dp))
                        ComposeSignupTheme.ConfirmPasswordEditText(confirmPassword) { newData -> confirmPassword = newData }
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
                            if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = getString(R.string.fill_all_the_fields),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else if (password != confirmPassword) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = getString(R.string.passwords_don_t_match),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                FirebaseUtils.createAccount(email, password, onSuccess = {
                                    FirebaseUtils.createDataOnFirestore(name, lastName, email)
                                    FirebaseUtils.signInWithEmail(email, password, onSuccess = {}
                                    ) {}
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if (user != null) {
                                        val intent = Intent(context, NewMainActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                }) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = it.toString(),
                                            duration = SnackbarDuration.Short
                                        )
                                    }

                                }
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
}