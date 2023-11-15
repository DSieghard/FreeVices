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
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mail
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.HelpDialog
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme

class NewAppSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(),Color.Transparent.hashCode()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(),Color.Transparent.hashCode()),
        )
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
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var isHelpPressed by remember { mutableStateOf(false) }
    if (isHelpPressed){
        HelpDialog(onDismissRequest = {
            isHelpPressed = false
        },
            text = context.getString(R.string.app_settings_help))
    }

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
                            isHelpPressed = true
                        }) {
                            Icon(imageVector = Icons.Filled.HelpOutline, contentDescription = null)
                        }
                    })
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            content = { it ->
                Column(modifier = Modifier.padding(it)) {
                    Divider(modifier = Modifier.padding(16.dp))
                    SettingsMenuLink(title = { Text(text = stringResource(R.string.write_a_review)) },
                        subtitle = { Text(text = stringResource(R.string.tell_us_what_you_think)) },
                        icon = { Icon(imageVector = Icons.Filled.Reviews, contentDescription = null) },
                        onClick = {

                        })
                    SettingsMenuLink(title = { Text(text = stringResource(R.string.contact_us))},
                        subtitle = { Text(text = stringResource(R.string.send_us_an_email)) },
                        icon = { Icon(imageVector = Icons.Filled.Mail, contentDescription = null) },
                        onClick = {

                        })
                }
            }
        )
    }

}