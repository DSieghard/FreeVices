package com.sgtech.freevices.views.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.HelpDialog
import com.sgtech.freevices.views.ui.LoginActivity
import com.sgtech.freevices.views.ui.ViewModelProvider
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

class NewUserSettingsActivity : AppCompatActivity() {
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

        setContent {
            val scope = lifecycleScope
            FreeVicesTheme(useDynamicColors = themeViewModel.isDynamicColor.value) {
                UserSettingsView(scope)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UserSettingsView(scope: LifecycleCoroutineScope) {
        val activity = Activity()
        var isHelpOpen by rememberSaveable { mutableStateOf(false) }
        if (isHelpOpen) {
            HelpDialog(
                onDismissRequest = { isHelpOpen = false },
                text = LocalContext.current.getString(R.string.user_settings_help)
            )
        }
        var isChangeNameSelected by rememberSaveable { mutableStateOf(false) }
        if (isChangeNameSelected) {
            ChangeNameDialog {
                isChangeNameSelected = false
            }
        }
        var isChangePasswordSelected by rememberSaveable { mutableStateOf(false) }
        if (isChangePasswordSelected) {
            ChangePasswordDialog {
                isChangePasswordSelected = false
            }
        }
        var isChangeEmailSelected by rememberSaveable { mutableStateOf(false) }
        if (isChangeEmailSelected) {
            ChangeEmailDialog {
                isChangeEmailSelected = false
            }
        }
        var isDeleteAccountSelected by rememberSaveable { mutableStateOf(false) }
        if (isDeleteAccountSelected) {
            DeleteAccountDialog {
                isDeleteAccountSelected = false
            }
        }

        Scaffold(
            topBar = {
                MediumTopAppBar(title = { Text(text = stringResource(R.string.user_settings)) },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                activity.finish()
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            isHelpOpen = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.HelpOutline,
                                contentDescription = null
                            )
                        }
                    })
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                SettingsMenuLink(title = { Text(text = stringResource(R.string.change_display_name)) },
                    subtitle = { Text(text = stringResource(R.string.change_your_display_name)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        isChangeNameSelected = true
                    })
                SettingsMenuLink(title = { Text(text = stringResource(R.string.change_password)) },
                    subtitle = { Text(text = stringResource(R.string.require_your_current_password)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        isChangePasswordSelected = true
                    })
                SettingsMenuLink(title = { Text(text = stringResource(R.string.change_email_address)) },
                    subtitle = { Text(text = stringResource(R.string.update_your_email_address_require_your_current_password)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.AlternateEmail,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        isChangeEmailSelected = true
                    })
                Divider(modifier = Modifier.padding(16.dp))
                SettingsMenuLink(title = { Text(text = stringResource(R.string.delete_account)) },
                    subtitle = { Text(text = stringResource(R.string.delete_your_account_and_all_associated_data_this_cannot_be_undone)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        isDeleteAccountSelected = true
                    })
            }
        }
    }

    private fun splitNames(input: String): Pair<String, String>? {
        val names = input.split(" ")

        when (names.size) {
            TWO -> {
                val firstName = names[ZERO]
                val lastName = names[ONE]
                return Pair(firstName, lastName)
            }

            THREE -> {
                val firstName = names[ZERO]
                val lastName = names[ONE]
                val lastSecondName = names[TWO]
                return Pair("$firstName $lastName", lastSecondName)
            }

            FOUR -> {
                val firstName = names[ZERO]
                val secondName = names[ONE]
                val lastName = names[TWO]
                val lastSecondName = names[THREE]
                return Pair("$firstName $secondName", "$lastName $lastSecondName")
            }

            else -> {
                return null
            }
        }
    }

    @Composable
    fun DeleteAccountDialog(onDismissRequest: () -> Unit) {
        val scope = rememberCoroutineScope()
        val snackbarHost = remember { SnackbarHostState() }
        val context = LocalContext.current
        var isDeleteAccountConfirmed by rememberSaveable { mutableStateOf(false) }
        if (isDeleteAccountConfirmed) {
            FirebaseUtils.deleteAccount(
                onSuccess = {
                    scope.launch {
                        snackbarHost.showSnackbar(
                            message = context.getString(R.string.account_deleted),
                            duration = SnackbarDuration.Short
                        )
                    }
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(context, intent, null)
                    onDismissRequest()
                },
                {
                    scope.launch {
                        snackbarHost.showSnackbar(
                            message = context.getString(
                                R.string.error_deleting_account,
                                it.message
                            ),
                        )
                    }
                }
            )
            onDismissRequest()
        }

        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = {
                Text(text = stringResource(R.string.delete_account))
            },
            text = {
                Text(
                    stringResource(R.string.delete_account_warning),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleteAccountConfirmed = true
                    }
                ) {
                    Text(stringResource(R.string.delete))
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

    @Composable
    fun ChangeNameDialog(onDismissRequest: () -> Unit) {
        var isChangeNameConfirmed by rememberSaveable { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        if (isChangeNameConfirmed) {
            FirebaseUtils.configDisplayNameOnAuth(name)
            splitNames(name)?.let {
                FirebaseUtils.updateDisplayNameOnFirestore(it.first, it.second, {}, {})
            }
            isChangeNameConfirmed = false
            onDismissRequest()
        }
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            title = {
                Text(text = stringResource(R.string.change_display_name))
            },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.new_display_name)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isChangeNameConfirmed = true
                    }
                ) {
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

    @Composable
    fun ChangePasswordDialog(onDismissRequest: () -> Unit) {
        val context = LocalContext.current
        var isChangePasswordConfirmed by rememberSaveable { mutableStateOf(false) }
        var newPassword by remember { mutableStateOf("") }
        var confirmNewPassword by remember { mutableStateOf("") }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        if (isChangePasswordConfirmed) {
            FirebaseUtils.configPasswordOnAuth(newPassword, onSuccess = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.password_changed_successfully),
                        duration = SnackbarDuration.Short
                    )
                }
            }, onFailure = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_updating_password, it.message),
                        duration = SnackbarDuration.Short
                    )
                }
            })
            isChangePasswordConfirmed = false
            onDismissRequest()
        }
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = { Icon(Icons.Filled.Password, contentDescription = null) },
            title = {
                Text(text = stringResource(R.string.change_password))
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(stringResource(R.string.new_password)) }
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text(stringResource(R.string.confirm_new_password)) }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPassword == confirmNewPassword) {
                            isChangePasswordConfirmed = true
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.passwords_don_t_match),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                ) {
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

    @Composable
    fun ChangeEmailDialog(onDismissRequest: () -> Unit) {
        val context = LocalContext.current
        var isChangeEmailConfirmed by rememberSaveable { mutableStateOf(false) }
        var newEmail by remember { mutableStateOf("") }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        if (isChangeEmailConfirmed) {
            FirebaseUtils.configEmailOnAuth(newEmail, onSuccess = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.email_changed_successfully),
                        duration = SnackbarDuration.Short
                    )
                }
            }, onFailure = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_updating_email, it.message),
                        duration = SnackbarDuration.Short
                    )
                }
            })
            isChangeEmailConfirmed = false
            onDismissRequest()
        }
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = { Icon(Icons.Filled.Email, contentDescription = null) },
            title = {
                Text(text = stringResource(R.string.change_email_address))
            },
            text = {
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text(stringResource(R.string.new_email)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isChangeEmailConfirmed = true
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

    companion object {
        private const val ZERO = 0
        private const val ONE = 1
        private const val TWO = 2
        private const val THREE = 3
        private const val FOUR = 4
    }
}
