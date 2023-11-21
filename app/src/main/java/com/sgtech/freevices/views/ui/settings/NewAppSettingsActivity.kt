package com.sgtech.freevices.views.ui.settings

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.HelpDialog
import com.sgtech.freevices.views.ui.ViewModelProvider
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

class NewAppSettingsActivity : AppCompatActivity() {
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
            FreeVicesTheme(useDynamicColors = themeViewModel.isDynamicColor.value){
                AppSettingsView()
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppSettingsView() {
        val state = rememberBooleanSettingState(defaultValue = themeViewModel.isDynamicColor.value)
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        var isHelpPressed by remember { mutableStateOf(false) }
        if (isHelpPressed) {
            HelpDialog(
                onDismissRequest = {
                    isHelpPressed = false
                },
                text = context.getString(R.string.app_settings_help)
            )
        }
        if (state.value) {
            themeViewModel.setDynamicColor(true)
        } else {
            themeViewModel.setDynamicColor(false)
        }

        Scaffold(
            topBar = {
                MediumTopAppBar(title = { Text(text = stringResource(R.string.app_settings)) },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch{
                                finish()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = getString(R.string.back))
                        }
                    },
                    actions = {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.about_help))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(
                                onClick = { isHelpPressed = true }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = getString(R.string.help)
                                )
                            }
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            content = {
                Column(modifier = Modifier.padding(it)) {
                    SettingsSwitch(
                        title = { Text(text = stringResource(R.string.use_dynamic_colors)) },
                        subtitle = { Text(text = stringResource(R.string.change_the_theme_based_on_your_wallpaper)) },
                        icon = { Icon(imageVector = Icons.Filled.ColorLens, contentDescription = getString(R.string.use_dynamic_colors)) },
                        enabled = true,
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                    HorizontalDivider(modifier = Modifier.padding(16.dp))
                    SettingsMenuLink(title = { Text(text = stringResource(R.string.write_a_review)) },
                        subtitle = { Text(text = stringResource(R.string.tell_us_what_you_think)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Reviews,
                                contentDescription = getString(R.string.write_a_review)
                            )
                        },
                        onClick = {
                            openPlayStoreForRating(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp))
                    SettingsMenuLink(title = { Text(text = stringResource(R.string.contact_us)) },
                        subtitle = { Text(text = stringResource(R.string.send_us_an_email)) },
                        icon = { Icon(imageVector = Icons.Filled.Mail, contentDescription = getString(R.string.contact_us)) },
                        onClick = {
                            sendEmail(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp))
                }
            }
        )
    }

    private fun openPlayStoreForRating(context: Context) {
        val packageName = context.packageName

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            context.startActivity(intent)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun sendEmail(context: Context) {
        val emailSubject = context.getString(R.string.email_title)
        val emailAddress = context.getString(R.string.dev_email_address) //

        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.there_is_no_email_client_installed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                getString(R.string.unable_to_open_email_app),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}