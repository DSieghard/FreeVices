package com.sgtech.freevices.views.ui.settings

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme

class NewAppSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppSettingsView()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsView() {
    val snackbarHostState = remember { SnackbarHostState() }
    val activity = Activity()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    FreeVicesTheme{
        Scaffold(
            topBar = {
                MediumTopAppBar(title = { Text(text = "App Settings") },
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
                            //TODO: Help handler
                        }) {
                            Icon(imageVector = Icons.Filled.HelpOutline, contentDescription = null)
                        }
                    })
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            content = {
                Column(modifier = Modifier.padding(it)) {
                    SettingsSwitch(
                        title = { Text(text = "Dark Mode") },
                        icon = {
                               Icon(imageVector = Icons.Filled.DarkMode, contentDescription = null)
                        },
                        subtitle = { Text(text = "Enable Dark Mode") },
                        onCheckedChange = {}
                    )
                    SettingsCheckbox(title = { Text(text = "Enable Notifications") } ,
                        subtitle = { Text(text = "Enable Notifications") },
                        icon = {
                          Icon(imageVector = Icons.Filled.Notifications, contentDescription = null)
                        },
                        onCheckedChange = {})
                    Divider(modifier = Modifier.padding(16.dp))
                    SettingsMenuLink(title = { Text(text = "Write a review") },
                        subtitle = { Text(text = "Tell us what you think") },
                        icon = { Icon(imageVector = Icons.Filled.Reviews, contentDescription = null) },
                        onClick = {

                        })
                    SettingsMenuLink(title = { Text(text = "Contact Us")},
                        subtitle = { Text(text = "Send us an email") },
                        icon = { Icon(imageVector = Icons.Filled.Mail, contentDescription = null) },
                        onClick = {

                        })
                }
            }
        )
    }

}