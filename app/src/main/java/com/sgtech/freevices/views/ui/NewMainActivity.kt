package com.sgtech.freevices.views.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.settings.NewAppSettingsActivity
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class NewMainActivity : ComponentActivity() {
    private val viewModel = ViewModelProvider.provideMainViewModel()
    private var errorCode = ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
        )

        setContent {
            NewMainScreen()
            ErrorDialog {}
        }
    }

    @Composable
    fun NewMainScreen() {
        val context = LocalContext.current
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val snackbarHostState = remember { SnackbarHostState() }
        val tobaccoData by viewModel.tobaccoLiveData.observeAsState(initial = 0f)
        val alcoholData by viewModel.alcoholLiveData.observeAsState(initial = 0f)
        val partiesData by viewModel.partiesLiveData.observeAsState(initial = 0f)
        val othersData by viewModel.othersLiveData.observeAsState(initial = 0f)
        var isDialogOpen by remember { mutableStateOf(false) }
        var isLoadingDialogVisible by rememberSaveable { mutableStateOf(false) }
        val totals = tobaccoData + alcoholData + partiesData + othersData
        dataHandlerForActivity{isVisible -> isLoadingDialogVisible = isVisible}

        FreeVicesTheme {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    MainNavigationDrawer()
                }
            ) {
                Scaffold(
                    Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        MediumTopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.menu_overview),
                                    style = MaterialTheme.typography.headlineLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            scrollBehavior = scrollBehavior,
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Menu",
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    scope.launch {
                                        val intent = android.content.Intent(
                                            context, NewAppSettingsActivity::class.java
                                        )
                                        context.startActivity(intent)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Settings",
                                    )
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        val intent = android.content.Intent(
                                            context, HistoryActivity::class.java
                                        )
                                        context.startActivity(intent)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.History,
                                        contentDescription = "History",
                                    )
                                }
                            })

                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    content = { padding ->
                        if (isLoadingDialogVisible) {
                            DialogForLoad { isLoadingDialogVisible = false }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(60.dp, 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                CategoryCard(tobaccoData.toInt(), stringResource(R.string.tobacco))
                                Spacer(modifier = Modifier.padding(16.dp))
                                CategoryCard(alcoholData.toInt(), stringResource(R.string.alcohol))
                                Spacer(modifier = Modifier.padding(16.dp))
                                CategoryCard(partiesData.toInt(), stringResource(R.string.parties))
                                Spacer(modifier = Modifier.padding(16.dp))
                                CategoryCard(othersData.toInt(), stringResource(R.string.others))
                            }
                        }
                    },
                    bottomBar = {
                        BottomAppBar(
                            actions = {
                                TextButton(onClick = {
                                    isDialogOpen = true
                                }) {
                                    Text(
                                        text = stringResource(
                                            R.string.week_spend,
                                            totals.toInt()
                                        ),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            },
                            floatingActionButton = {
                                HomeFab()
                            }
                        )

                        if (isDialogOpen) {
                            DetailsExtendedDialog(
                                onDismissRequest = { isDialogOpen = false },
                                "totals"
                            )
                        }
                    }
                )
            }
        }
    }

    private fun dataHandlerForActivity(
        onLoadingVisibilityChange: (Boolean) -> Unit) {
        onLoadingVisibilityChange(true)

        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = SEVEN_DAYS,
            onSuccess = { data ->
                viewModel.updateLiveDataValues(this, data)
            },
            onFailure = {
                errorCode = SEVEN_DAYS
                onLoadingVisibilityChange(false)
            }
        )

        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = FOURTEEN_DAYS,
            onSuccess = { data ->
                viewModel.updateTwoWeekLiveDataValues(this, data)
            },
            onFailure = {
                errorCode = FOURTEEN_DAYS
                onLoadingVisibilityChange(false)
            }
        )

        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = THIRTY_DAYS,
            onSuccess = { data ->
                viewModel.updateThirtyDaysLiveDataValues(this, data)
            },
            onFailure = {
                errorCode = THIRTY_DAYS
                onLoadingVisibilityChange(false)
            }
        )


        onLoadingVisibilityChange(false)
    }


    @Composable
    fun ErrorDialog(onDismissRequest: () -> Unit) {
        when(errorCode) {
            THIRTY_DAYS -> {
                AlertDialog(onDismissRequest = { onDismissRequest() },
                    title = { Text(text = stringResource(R.string.error)) },
                    text = { Text(text = stringResource(R.string.error_30_days)) },
                    confirmButton = {
                        TextButton(onClick = {
                            onDismissRequest()
                        }) {
                            Text(text = stringResource(R.string.close))
                        }
                    })
            }
            FOURTEEN_DAYS -> {
                AlertDialog(onDismissRequest = { onDismissRequest() },
                    title = { Text(text = stringResource(R.string.error)) },
                    text = { Text(text = stringResource(R.string.error_14_days)) },
                    confirmButton = {
                        TextButton(onClick = {
                            onDismissRequest()
                        }) {
                            Text(text = stringResource(R.string.close))
                        }
                    })
            }
            SEVEN_DAYS -> {
                AlertDialog(onDismissRequest = { onDismissRequest() },
                    title = { Text(text = stringResource(R.string.error)) },
                    text = { Text(text = stringResource(R.string.error_7_days)) },
                    confirmButton = {
                        TextButton(onClick = {
                            onDismissRequest()
                        }) {
                            Text(text = stringResource(R.string.close))
                        }
                    })
            }
            ZERO -> {

            }
        }
    }

    companion object {
        private const val THIRTY_DAYS = 30
        private const val FOURTEEN_DAYS = 14
        private const val SEVEN_DAYS = 7
        private const val ZERO = 0
    }
}