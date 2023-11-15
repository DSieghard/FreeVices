package com.sgtech.freevices.views.ui.settings

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ManageHistory
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme

class NewHistorySettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HistorySettingsView()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySettingsView() {
    val activity = Activity()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    FreeVicesTheme{
        Scaffold(
            topBar = {
                MediumTopAppBar(title = { Text(text = "History Settings") },
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
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                SettingsMenuLink(title = { Text(text = "Clear last 30 days") },
                    subtitle = { Text(text = "Delete last 30 days from history. This action cannot be undone") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {

                    })
                SettingsMenuLink(title = { Text(text = "Clear last 60 days") },
                    subtitle = { Text(text = "Delete last 60 days from history. This action cannot be undone") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {

                    })
                SettingsMenuLink(title = { Text(text = "Clear last 90 days") },
                    subtitle = { Text(text = "Delete last 90 days from history. This action cannot be undone") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {

                    })
                Divider(modifier = Modifier.padding(16.dp))
                SettingsMenuLink(title = { Text(text = "Clear all history") },
                    subtitle = { Text(text = "Delete all history. This action cannot be undone") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ManageHistory,
                            contentDescription = null
                        )
                    },
                    onClick = {

                    })
            }
        }
    }
}