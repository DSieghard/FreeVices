package com.sgtech.freevices.views.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
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
import com.sgtech.freevices.views.ui.settings.NewUserSettingsActivity
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class NewMainActivity : ComponentActivity() {
    private val viewModel = ViewModelProvider.provideMainViewModel()
    private val themeViewModel = ViewModelProvider.provideThemeViewModel()
    private val snackbarHostState = SnackbarHostState()
    private val scope = CoroutineScope(Dispatchers.Main)
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
        )

        setContent {
            FreeVicesTheme(useDynamicColors = themeViewModel.isDynamicColor.value) {
                NewMainScreen()
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
        val totals = tobaccoData + alcoholData + partiesData + othersData
        dataHandlerForActivity()
        if (isLoading){
            DialogForLoad { }
        }

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
                                    contentDescription = stringResource(R.string.menu),
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    val intent = Intent(
                                        context, NewAppSettingsActivity::class.java
                                    )
                                    context.startActivity(intent)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = stringResource(id = R.string.settings),
                                )
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    val intent = Intent(
                                        context, HistoryActivity::class.java
                                    )
                                    context.startActivity(intent)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = stringResource(id = R.string.history_acc),
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
                            TOTALS
                        )
                    }
                },
            )
        }
    }

    private fun dataHandlerForActivity() {
        isLoading = true

        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = SEVEN_DAYS,
            onSuccess = { data ->
                viewModel.updateLiveDataValues(this, data)
            },
            onFailure = {
                isLoading = false
                scope.launch{
                    snackbarHostState.showSnackbar(
                        message = applicationContext.getString(R.string.error_updating_data),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )

        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = FOURTEEN_DAYS,
            onSuccess = { data ->
                viewModel.updateTwoWeekLiveDataValues(this, data)
            },
            onFailure = {
                isLoading = false
                scope.launch{
                    snackbarHostState.showSnackbar(
                        message = applicationContext.getString(R.string.error_updating_data),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )

        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = THIRTY_DAYS,
            onSuccess = { data ->
                viewModel.updateThirtyDaysLiveDataValues(this, data)
            },
            onFailure = {
                isLoading = false
                scope.launch{
                    snackbarHostState.showSnackbar(
                        message = applicationContext.getString(R.string.error_updating_data),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
        isLoading = false
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CategoryCard(value: Int, category: String){
        var isDialogOpen by remember { mutableStateOf(false) }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .size(width = 240.dp, height = 120.dp),
            onClick = { isDialogOpen = true }
        ) {
            Text(
                text = category,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$$value",
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        if (isDialogOpen) {
            DetailsExtendedDialog(
                onDismissRequest = { isDialogOpen = false }, category
            )
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
        var categorySelected: String? by remember { mutableStateOf(null) }
        if (expenseSelected) {
            when (categorySelected) {
                TOBACCO -> {
                    ExpenseDialog(stringResource(id = R.string.tobacco), onDismissRequest, snackbarHostState)
                }
                ALCOHOL -> {
                    ExpenseDialog(stringResource(id = R.string.alcohol), onDismissRequest, snackbarHostState)
                }
                PARTIES -> {
                    ExpenseDialog(stringResource(id = R.string.parties), onDismissRequest, snackbarHostState)
                }
                OTHERS -> {
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
                            categorySelected = TOBACCO
                        })
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.alcohol),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                        onClick = { expenseSelected = true
                            categorySelected = ALCOHOL
                        })
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.parties),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                        onClick = { expenseSelected = true
                            categorySelected = PARTIES
                        })
                    DropdownMenuItem(text = {
                        Text(
                            text = stringResource(R.string.others),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                        onClick = { expenseSelected = true
                            categorySelected = OTHERS
                        })
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

    @Composable
    fun MainNavigationDrawer(){
        val currentUser = FirebaseUtils.getCurrentUser()
        val activity = Activity()
        val context = LocalContext.current
        var signOutRequest by remember { mutableStateOf(false) }
        if (signOutRequest) {
            FreeVicesTheme {
                SignOutDialog(
                    onDismissRequest = { signOutRequest = false },
                    onSignOutConfirmed = { activity.finish() },
                    context = context
                )
            }
        }

        ModalDrawerSheet {
            remember { mutableStateOf(NewMainActivity()) }
            Text(
                stringResource(id = R.string.app_name_short),
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                stringResource(R.string.awakening_awareness_of_the_price_of_your_vices),
                modifier = Modifier.padding(24.dp, 12.dp, 24.dp, 16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Divider()
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                stringResource(R.string.account, currentUser?.displayName ?: stringResource(R.string.error_no_user)),
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Divider()
            Spacer(modifier = Modifier.padding(8.dp))

            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.account_settings)) },
                icon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.account_settings_acc)) },
                selected = false,
                onClick = {
                    val intent = Intent(context,
                        NewUserSettingsActivity::class.java)
                    context.startActivity(intent)
                }
            )
            Row(modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom) {
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.sign_out)) },
                    icon = { Icon(Icons.Filled.Logout, contentDescription = stringResource(R.string.logout_acc)) },
                    selected = false,
                    onClick = { signOutRequest = true }
                )
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }

    @Composable
    fun SignOutDialog(onDismissRequest: () -> Unit, onSignOutConfirmed: () -> Unit, context: Context) {
        val activity = Activity()
        val scope = rememberCoroutineScope()
        Dialog(
            onDismissRequest = { onDismissRequest() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp),
            )
            {
                Text(
                    stringResource(R.string.sign_out),
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    stringResource(R.string.sign_out_confirm),
                    modifier = Modifier.padding(24.dp, 12.dp, 24.dp, 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier
                        .padding(16.dp, 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(onClick = {
                        FirebaseUtils.signOut(onSuccess = {
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                            activity.finish()
                        },
                            onFailure = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.error_signing_out),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            })
                        onSignOutConfirmed()
                        onDismissRequest()
                    }) {
                        Text(text = stringResource(R.string.yes), color = MaterialTheme.colorScheme.onTertiary)
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                    Button(onClick = {
                        onDismissRequest()
                    })
                    {
                        Text(text = stringResource(R.string.no), color = MaterialTheme.colorScheme.onTertiary)
                    }
                }
            }
        }
    }

    @Composable
    fun DetailsExtendedDialog(
        onDismissRequest: () -> Unit,
        category: String
    ) {
        val viewModel = ViewModelProvider.provideMainViewModel()
        LocalContext.current
        val tobaccoData by viewModel.tobaccoLiveData.observeAsState(initial = 0f)
        val alcoholData by viewModel.alcoholLiveData.observeAsState(initial = 0f)
        val partiesData by viewModel.partiesLiveData.observeAsState(initial = 0f)
        val othersData by viewModel.othersLiveData.observeAsState(initial = 0f)
        val tobaccoData2Weeks by viewModel.tobaccoTwoWeekData.observeAsState(initial = 0f)
        val alcoholData2Weeks by viewModel.alcoholTwoWeekData.observeAsState(initial = 0f)
        val partiesData2Weeks by viewModel.partiesTwoWeekData.observeAsState(initial = 0f)
        val othersData2Weeks by viewModel.othersTwoWeekData.observeAsState(initial = 0f)
        val tobaccoDataThirtyDays by viewModel.tobaccoThirtyDaysData.observeAsState(initial = 0f)
        val alcoholDataThirtyDays by viewModel.alcoholThirtyDaysData.observeAsState(initial = 0f)
        val partiesDataThirtyDays by viewModel.partiesThirtyDaysData.observeAsState(initial = 0f)
        val othersDataThirtyDays by viewModel.othersThirtyDaysData.observeAsState(initial = 0f)
        val totalWeek = tobaccoData + alcoholData + partiesData + othersData
        val total2Weeks = tobaccoData2Weeks + alcoholData2Weeks + partiesData2Weeks + othersData2Weeks
        val totalThirtyDays = tobaccoDataThirtyDays + alcoholDataThirtyDays + partiesDataThirtyDays + othersDataThirtyDays

        Dialog(
            onDismissRequest = { onDismissRequest() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            when (category) {
                stringResource(id = R.string.tobacco) -> {
                    ElevatedCardForCategory(onDismissRequest, tobaccoData.toInt(), tobaccoData2Weeks.toInt(), tobaccoDataThirtyDays.toInt())
                }
                stringResource(id = R.string.alcohol) -> {
                    ElevatedCardForCategory(onDismissRequest, alcoholData.toInt(), alcoholData2Weeks.toInt(), alcoholDataThirtyDays.toInt())
                }
                stringResource(id = R.string.parties) -> {
                    ElevatedCardForCategory(onDismissRequest, partiesData.toInt(), partiesData2Weeks.toInt(), partiesDataThirtyDays.toInt())
                }
                stringResource(id = R.string.others) -> {
                    ElevatedCardForCategory(onDismissRequest, othersData.toInt(), othersData2Weeks.toInt(), othersDataThirtyDays.toInt())
                }
                "totals" -> {
                    ElevatedCardForCategory(onDismissRequest, totalWeek.toInt(), total2Weeks.toInt(), totalThirtyDays.toInt())
                }
            }

        }
    }

    @Composable
    fun ElevatedCardForCategory(
        onDismissRequest: () -> Unit,
        weekValue: Int,
        twoWeekValue: Int,
        thirtyDaysValue: Int
    ) {
        ElevatedCard(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.padding(24.dp))
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.your_spending_in_the_last_week_is,
                            weekValue
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.your_spending_in_the_last_2_weeks_is,
                            twoWeekValue
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.your_spending_in_the_last_month_is,
                            thirtyDaysValue
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(32.dp))
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.this_could_be_savings_for_your_vacation_or_to_fulfill_that_dream_you_have_pending),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedButton(onClick = { onDismissRequest() }) {
                        Text(text = stringResource(R.string.close))
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
        private const val TOBACCO = "tobacco"
        private const val ALCOHOL = "alcohol"
        private const val PARTIES = "parties"
        private const val OTHERS = "others"
        private const val TOTALS = "totals"
    }
}