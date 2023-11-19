package com.sgtech.freevices.views.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme

class WelcomeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeViewModel = ViewModelProvider.provideThemeViewModel()
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
                WelcomeScreenView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WelcomeScreenView() {
        var openFirstBottomSheet by rememberSaveable { mutableStateOf(false) }
        var openSecondBottomSheet by rememberSaveable { mutableStateOf(false) }
        var openThirdBottomSheet by rememberSaveable { mutableStateOf(false) }

        val context = LocalContext.current
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    actions = {
                        TextButton(onClick = {
                            val intent =
                                Intent(context, CreateAccountActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                }
                            context.startActivity(intent)
                            finish()

                        }) {
                            Text(
                                text = stringResource(R.string.sign_up),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                    floatingActionButton = {
                        TextButton(onClick = {
                            val intent = Intent(context, LoginActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                            context.startActivity(intent)
                            finish()
                        }) {
                            Text(
                                text = stringResource(R.string.sign_in),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                )
            }
        ) { contentPadding ->
            Surface(modifier = Modifier.padding(contentPadding),
                color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                )
                {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary),
                        onClick = { openFirstBottomSheet = true }
                    ) {
                        Spacer(modifier = Modifier.padding(16.dp))
                        Text(
                            text = stringResource(R.string.welcome_text),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.padding(16.dp))

                        if (openFirstBottomSheet) {
                            ModalSheetBuilder(text = stringResource(R.string.welcome_card_first_text),
                                onClose = { openFirstBottomSheet = false })
                        }
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                        onClick = { openSecondBottomSheet = true }
                    ) {
                        Spacer(modifier = Modifier.padding(16.dp))
                        Text(
                            text = stringResource(R.string.about_freevices),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.padding(16.dp))

                        if (openSecondBottomSheet) {
                            ModalSheetBuilder(text = stringResource(R.string.about_text),
                                onClose = {
                                    openSecondBottomSheet = false
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        onClick = {
                            openThirdBottomSheet = true
                        }
                    ) {
                        Spacer(modifier = Modifier.padding(16.dp))
                        Text(
                            text = stringResource(R.string.about_the_developers),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.padding(16.dp))

                        if (openThirdBottomSheet) {
                            ModalSheetBuilder(text = stringResource(R.string.about_developer_text)) {
                                openThirdBottomSheet = false
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(48.dp))
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = {

                        }
                    ) {
                        Spacer(modifier = Modifier.padding(16.dp))
                        Text(
                            text = stringResource(R.string.press_any_card_to_get_info),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.padding(16.dp))
                    }

                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ModalSheetBuilder(text: String, onClose: () -> Unit) {
        val edgeToEdgeEnabled by remember { mutableStateOf(false) }
        val windowInsets = if (edgeToEdgeEnabled)
            WindowInsets(0) else BottomSheetDefaults.windowInsets

        ModalBottomSheet(
            onDismissRequest = { onClose() },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            ),
            windowInsets = windowInsets
        ) {
            LazyColumn {
                item {
                    Row(horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.welcome_to_freevices),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                    Divider(modifier = Modifier.padding(10.dp))
                    Text(
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        text = text
                    )
                }
                items(10) {
                    Spacer(modifier = Modifier.padding(10.dp))
                }
            }
        }
    }
}
