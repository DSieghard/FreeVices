package com.sgtech.freevices.views.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.settings.NewAppSettingsActivity
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class NewMainActivity : ComponentActivity() {
    private val viewModel = ViewModelProvider.provideMainViewModel()
    private val themeViewModel = ViewModelProvider.provideThemeViewModel()
    private var errorCode = ZERO
    private val snackbarHostState = SnackbarHostState()


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
        )

        setContent {
            FreeVicesTheme(useDynamicColors = themeViewModel.isDynamicColor.value) {
                NewMainScreen()
                ErrorDialog {}
            }
        }
    }

    @Composable
    fun NewMainScreen() {
        val context = LocalContext.current
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val tobaccoData by viewModel.tobaccoLiveData.observeAsState(initial = 0f)
        val alcoholData by viewModel.alcoholLiveData.observeAsState(initial = 0f)
        val partiesData by viewModel.partiesLiveData.observeAsState(initial = 0f)
        val othersData by viewModel.othersLiveData.observeAsState(initial = 0f)
        var isDialogOpen by remember { mutableStateOf(false) }
        var isLoadingDialogVisible by rememberSaveable { mutableStateOf(false) }
        val totals = tobaccoData + alcoholData + partiesData + othersData
        dataHandlerForActivity{isVisible -> isLoadingDialogVisible = isVisible}


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
                },
            )
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

    @Composable
    fun HomeFab() {
        var isMenuVisible by rememberSaveable { mutableStateOf(false) }

        if (isMenuVisible) {
            DeployMenu { isMenuVisible = false }
        }

        ExtendedFloatingActionButton(
            onClick = {
                isMenuVisible = true
            },
            icon = { Icon(Icons.Filled.Add, stringResource(R.string.add_expense_button)) },
            text = { Text(text = stringResource(R.string.add)) },
        )
    }

    @Composable
    fun DeployMenu(onDismissRequest: () -> Unit) {
        var expenseSelected by remember { mutableStateOf(false) }
        var categorySelected by remember { mutableStateOf("") }
        if (expenseSelected) {
            when (categorySelected) {
                "tobacco" -> {
                    ExpenseDialog(stringResource(id = R.string.tobacco), onDismissRequest, snackbarHostState)
                }
                "alcohol" -> {
                    ExpenseDialog(stringResource(id = R.string.alcohol), onDismissRequest, snackbarHostState)
                }
                "parties" -> {
                    ExpenseDialog(stringResource(id = R.string.parties), onDismissRequest, snackbarHostState)
                }
                "others" -> {
                    ExpenseDialog(stringResource(id = R.string.others), onDismissRequest, snackbarHostState)
                }
            }
        }

        Dialog(
            onDismissRequest = { onDismissRequest() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                ),
                modifier = Modifier.padding(18.dp),

                ) {
                Text(
                    text = stringResource(R.string.choose_category),
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.tobacco),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                        onClick = { expenseSelected =  true
                            categorySelected = "tobacco"})
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.alcohol),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                        onClick = { expenseSelected = true
                            categorySelected = "alcohol"})
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.parties),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                        onClick = { expenseSelected = true
                            categorySelected = "parties"})
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.others),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                        onClick = { expenseSelected = true
                            categorySelected = "others"})
                }
                Row(
                    modifier = Modifier
                        .padding(16.dp, 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom

                ) {
                    Button(onClick = { onDismissRequest() }) {
                        Text(text = stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onTertiary)
                    }
                }

            }
        }
    }

    @Composable
    fun ExpenseDialog(
        category: String,
        onCancel: () -> Unit,
        snackbarHostState: SnackbarHostState
    ) {
        val viewModel = ViewModelProvider.provideMainViewModel()
        var expenseAmount by remember { mutableIntStateOf(ZERO) }
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        Dialog(
            onDismissRequest = onCancel
        ) {
            Card(
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.add_expense), modifier = Modifier.padding(16.dp))
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = expenseAmount.toString(),
                        onValueChange = {
                            expenseAmount = it.toIntOrNull() ?: ZERO
                        },
                        label = { Text(stringResource(R.string.expended_in_category, category)) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.padding(32.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = onCancel) {
                            Text(stringResource(R.string.cancel))
                        }

                        Spacer(modifier = Modifier.padding(12.dp))

                        Button(onClick = {
                            FirebaseUtils.addDataToCategory(
                                context = context,
                                category = category,
                                amount = expenseAmount,
                                onSuccess = {
                                    FirebaseUtils.dataHandler(
                                        context = context,
                                        days = SEVEN_DAYS,
                                        onSuccess = { data ->
                                            viewModel.updateLiveDataValues(context, data)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = context.getString(R.string.data_updated_successfully),
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        },
                                        onFailure = {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = context.getString(R.string.error_updating_data),
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    )
                                },
                                onFailure = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.error_updating_data),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                            onCancel()
                        }) {
                            Text(stringResource(R.string.add))
                        }

                    }
                }
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