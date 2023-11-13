package com.sgtech.freevices.views.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
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
                viewModel.updateOneMonthLiveDataValues(this, data)
                Log.d("MainActivityResolver", "Data obtained: $data")
            },
            onFailure = {
                Log.d("MainActivityResolver", "Error getting data: $it")
            }
        )
        setContent {

            val context = LocalContext.current
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            val snackbarHostState = remember { SnackbarHostState() }
            var signOutRequest by remember { mutableStateOf(false) }
            val tobaccoData by viewModel.tobaccoLiveData.observeAsState(initial = 0f)
            val alcoholData by viewModel.alcoholLiveData.observeAsState(initial = 0f)
            val partiesData by viewModel.partiesLiveData.observeAsState(initial = 0f)
            val othersData by viewModel.othersLiveData.observeAsState(initial = 0f)
            var isDialogOpen by remember { mutableStateOf(false) }
            val totals = tobaccoData + alcoholData + partiesData + othersData

            if (signOutRequest) {
                FreeVicesTheme {
                    SignOutDialog(
                        onDismissRequest = { signOutRequest = false },
                        onSignOutConfirmed = { finish() },
                        context = context
                    )
                }
            }

            FreeVicesTheme {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text(
                                "FreeVices",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(
                                "Awakening awareness of the price of your vices",
                                modifier = Modifier.padding(24.dp, 12.dp, 24.dp, 16.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Divider()
                            Spacer(modifier = Modifier.padding(8.dp))
                            NavigationDrawerItem(
                                label = { Text(text = "Home") },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                selected = false,
                                onClick = { /*TODO*/ }
                            )
                            NavigationDrawerItem(
                                label = { Text(text = "History") },
                                icon = { Icon(Icons.Filled.History, contentDescription = "History") },
                                selected = false,
                                onClick = { /*TODO*/ }
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.padding(8.dp))
                            NavigationDrawerItem(
                                label = { Text(text = "Settings") },
                                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                                selected = false,
                                onClick = { /*TODO*/ }
                            )

                            Row(modifier = Modifier.fillMaxSize( ),
                                verticalAlignment = Alignment.Bottom) {
                                NavigationDrawerItem(
                                    label = { Text(text = "Logout") },
                                    icon = { Icon(Icons.Filled.Logout, contentDescription = "Logout") },
                                    selected = false,
                                    onClick = { signOutRequest = true}
                                )
                                Spacer(modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                ) {
                Scaffold(
                    topBar = { CenterAlignedTopAppBar(
                        title = {
                            Text(text = stringResource(R.string.menu_overview),
                                style = MaterialTheme.typography.titleLarge)},
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
                        })

                    },
                    snackbarHost =  { SnackbarHost(snackbarHostState) },
                    content = { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(60.dp, 60.dp)
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CategoryCard(tobaccoData.toInt(), stringResource(R.string.tobacco))
                            Spacer(modifier = Modifier.padding(16.dp))
                            CategoryCard(alcoholData.toInt(), stringResource(R.string.alcohol))
                            Spacer(modifier = Modifier.padding(16.dp))
                            CategoryCard(partiesData.toInt(), stringResource(R.string.parties))
                            Spacer(modifier = Modifier.padding(16.dp))
                            CategoryCard(othersData.toInt(), stringResource(R.string.others))
                        }
                    },
                    bottomBar = {
                        BottomAppBar(
                            actions = {
                                TextButton(onClick = {
                                    isDialogOpen = true
                                }) {
                                    Text(text = stringResource(R.string.week_spend, totals.toInt()),
                                        style = MaterialTheme.typography.bodyLarge)
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun ThemePreview() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        var isNavigationMenuEnabled by remember { mutableStateOf(false) }
        var isDialogOpen by remember { mutableStateOf(false) }
        val tobaccoData = 1
        val alcoholData = 2
        val partiesData = 3
        val othersData = 4
        val totals = tobaccoData + alcoholData + partiesData + othersData

        FreeVicesTheme {
            Scaffold(
                topBar = { CenterAlignedTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.menu_overview),
                            style = MaterialTheme.typography.titleLarge)},
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = {
                            isNavigationMenuEnabled = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu",
                            )
                        }
                    })

                },
                content = { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(60.dp, 60.dp)
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CategoryCard(tobaccoData, stringResource(R.string.tobacco))
                        Spacer(modifier = Modifier.padding(16.dp))
                        CategoryCard(alcoholData, stringResource(R.string.alcohol))
                        Spacer(modifier = Modifier.padding(16.dp))
                        CategoryCard(partiesData, stringResource(R.string.parties))
                        Spacer(modifier = Modifier.padding(16.dp))
                        CategoryCard(othersData, stringResource(R.string.others))

                    }
                },
                bottomBar = {
                    BottomAppBar(
                        actions = {
                            TextButton(onClick = {
                                isDialogOpen = true
                            }) {
                                Text(text = stringResource(R.string.week_spend, totals),
                                    style = MaterialTheme.typography.bodyLarge)
                            }
                        },
                        floatingActionButton = {
                            HomeFab()
                        }
                    )

                    if (isDialogOpen) {
                        DetailsExtendedDialog(
                            onDismissRequest = { isDialogOpen = false }, "totals"
                        )
                    }
                }
            )
        }
    }
}

