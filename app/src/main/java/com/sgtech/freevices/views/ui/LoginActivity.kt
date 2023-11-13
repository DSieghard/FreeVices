package com.sgtech.freevices.views.ui

import android.os.Bundle
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.utils.FirebaseUtils.checkIfUserIsLoggedIn
import com.sgtech.freevices.views.CreateAccountActivity
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {




    override fun onStart() {
        super.onStart()
        checkIfUserIsLoggedIn(this)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
            val scope = rememberCoroutineScope()
            var isLoading by remember { mutableStateOf(false) }

            FreeVicesTheme {
                Scaffold(
                    Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                Text(
                                    "Welcome to FreeVices",
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
                                        android.content.Intent(context, CreateAccountActivity::class.java)
                                    context.startActivity(intent)
                                }) {
                                    Text(text = stringResource(R.string.sign_up), style = MaterialTheme.typography.bodyLarge)
                                }
                            },
                            floatingActionButton = {
                                TextButton(onClick = {
                                    isLoading = true
                                    scope.launch {
                                        FirebaseUtils.signInWithEmail(context, email, password, {
                                            isLoading = false
                                        },
                                            {
                                                isLoading = false
                                            })
                                        isLoading = false
                                    }
                                }) {
                                    Text(text = stringResource(R.string.sign_in), style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        )
                    }
                )
            }
            if (isLoading) {
               LoadingDialog()
            }

        }
    }
}