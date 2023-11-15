package com.sgtech.freevices.views.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
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
import com.sgtech.freevices.views.ui.settings.NewAppSettingsActivity
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class NewMainActivity : ComponentActivity() {
    private val viewModel = ViewModelProvider.provideMainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = 7,
            onSuccess = { data ->
                viewModel.updateLiveDataValues(this, data)
                Log.d("MainActivityResolver", "Data obtained: $data")
            },
            onFailure = {
                Log.d("MainActivityResolver", "Error getting data: $it")
            }
        )
        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = 14,
            onSuccess = { data ->
                viewModel.updateTwoWeekLiveDataValues(this, data)
                Log.d("MainActivityResolver", "Data obtained: $data")
            },
            onFailure = {
                Log.d("MainActivityResolver", "Error getting data: $it")
            }
        )
        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = 30,
            onSuccess = { data ->
                viewModel.updateThirtyDaysLiveDataValues(this, data)
                Log.d("MainActivityResolver", "Data obtained: $data")
            },
            onFailure = {
                Log.d("MainActivityResolver", "Error getting data: $it")
            }
        )
        setContent {
            NewMainScreen()
        }
    }

    @Composable
    fun NewMainScreen() {
        val context = LocalContext.current
        val viewModel = ViewModelProvider.provideMainViewModel()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val snackbarHostState = remember { SnackbarHostState() }
        val tobaccoData by viewModel.tobaccoLiveData.observeAsState(initial = 0f)
        val alcoholData by viewModel.alcoholLiveData.observeAsState(initial = 0f)
        val partiesData by viewModel.partiesLiveData.observeAsState(initial = 0f)
        val othersData by viewModel.othersLiveData.observeAsState(initial = 0f)
        var isDialogOpen by remember { mutableStateOf(false) }
        val totals = tobaccoData + alcoholData + partiesData + othersData

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
}



