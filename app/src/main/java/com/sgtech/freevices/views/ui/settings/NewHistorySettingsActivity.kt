package com.sgtech.freevices.views.ui.settings

import android.app.Activity
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ManageHistory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.HelpDialog
import com.sgtech.freevices.views.ui.ViewModelProvider
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

class NewHistorySettingsActivity : AppCompatActivity() {
    private val themeViewModel = ViewModelProvider.provideThemeViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
        )

        setContent {
            FreeVicesTheme(useDynamicColors = themeViewModel.isDynamicColor.value) {
                HistorySettingsView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HistorySettingsView() {
        val activity = Activity()
        var days by remember { mutableIntStateOf(0) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        var isHelpPressed by remember { mutableStateOf(false) }
        if (isHelpPressed) {
            HelpDialog( onDismissRequest = { isHelpPressed = false },
                stringResource(id = R.string.history_settings_help)
            )
        }
        var isDeleteButtonPressed by remember { mutableStateOf(false) }
        if (isDeleteButtonPressed) {
            when (days) {
                THIRTY_DAYS -> DeleteDialog(days = days) {
                    isDeleteButtonPressed = false
                }
                SIXTY_DAYS -> DeleteDialog(days = days) {
                    isDeleteButtonPressed = false
                }
                NINETY_DAYS -> DeleteDialog(days = days) {
                    isDeleteButtonPressed = false
                }
                ALL_DAYS -> DeleteDialog(days = days) {
                    isDeleteButtonPressed = false
                }
            }
        }
        Scaffold(
            topBar = {
                MediumTopAppBar(title = { Text(text = stringResource(id = R.string.history_settings)) },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = {
                            activity.finish()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            isHelpPressed = true
                        }) {
                            Icon(imageVector = Icons.Filled.HelpOutline, contentDescription = null)
                        }
                    })
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                SettingsMenuLink(title = { Text (text = stringResource(R.string.clear_last_7_days)) },
                    subtitle = { Text(text = stringResource(R.string.clear_seven_days_subtitle))},
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        days = SEVEN_DAYS
                        isDeleteButtonPressed = true
                    })
                SettingsMenuLink(title = { Text(text = stringResource(R.string.clear_last_14_days)) },
                    subtitle = {Text(text = stringResource(R.string.clear_14_days_subtitle))},
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        days = FOURTEEN_DAYS
                        isDeleteButtonPressed = true
                    })
                SettingsMenuLink(title = { Text(text = stringResource(R.string.clear_last_30_days)) },
                    subtitle = { Text(text = stringResource(R.string.delete_last_30_days_from_history_this_action_cannot_be_undone)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        days = THIRTY_DAYS
                        isDeleteButtonPressed = true
                    })
                SettingsMenuLink(title = { Text(text = stringResource(R.string.clear_last_60_days)) },
                    subtitle = { Text(text = stringResource(R.string.delete_last_60_days_from_history_this_action_cannot_be_undone)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        days = SIXTY_DAYS
                        isDeleteButtonPressed = true
                    })
                SettingsMenuLink(title = { Text(text = stringResource(R.string.clear_last_90_days)) },
                    subtitle = { Text(text = stringResource(R.string.delete_last_90_days_from_history_this_action_cannot_be_undone)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        days = NINETY_DAYS
                        isDeleteButtonPressed = true
                    })
                Divider(modifier = Modifier.padding(16.dp))
                SettingsMenuLink(title = { Text(text = stringResource(R.string.clear_all_history)) },
                    subtitle = { Text(text = stringResource(R.string.delete_all_history_this_action_cannot_be_undone)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        days = ALL_DAYS
                        isDeleteButtonPressed = true
                    })
            }
        }
    }

    @Composable
    fun DeleteDialog(days: Int, onDismissRequest: () -> Unit) {
        val scope = rememberCoroutineScope()
        val snackbarHost = remember { SnackbarHostState() }
        val context = LocalContext.current
        var isDeleteConfirmed by remember { mutableStateOf(false) }
        if (isDeleteConfirmed) {
            FirebaseUtils.deleteHistory(days, onSuccess = {
                scope.launch {
                    snackbarHost.showSnackbar(
                        message = context.getString(R.string.history_deleted, days),
                        duration = SnackbarDuration.Short
                    )
                }
                onDismissRequest()
            }) {
                scope.launch {
                    snackbarHost.showSnackbar(
                        message = context.getString(R.string.error_deleting_history, it.message),
                        duration = SnackbarDuration.Short
                    )
                }
                onDismissRequest()
            }
        }
        AlertDialog(onDismissRequest = { onDismissRequest() },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = {
                Text(text = stringResource(R.string.delete_history))
            },
            text = {
                if (days == 5000){
                    Text(stringResource(R.string.delete_all_history),
                        style = MaterialTheme.typography.bodyLarge)
                } else {
                    Text(
                        stringResource(R.string.delete_history_text, days),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleteConfirmed = true
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    companion object{
        private const val SEVEN_DAYS = 7
        private const val FOURTEEN_DAYS = 14
        private const val THIRTY_DAYS = 30
        private const val SIXTY_DAYS = 60
        private const val NINETY_DAYS = 90
        private const val ALL_DAYS = 5000
    }

}

