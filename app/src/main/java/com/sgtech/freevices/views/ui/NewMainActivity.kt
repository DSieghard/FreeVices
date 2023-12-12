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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.settings.NewSettingsActivity
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
    private var isLoading by mutableStateOf(false)
    private var isMenuVisible by mutableStateOf(false)
    private val currentUser = FirebaseUtils.getCurrentUser()
    private val currentDisplayName = currentUser?.displayName ?: ""
    private val activity = Activity()
    private val context = this
    private var isDialogOpen by mutableStateOf(false)
    private var isHelpPressed by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataHandlerForActivity(
            onStart = { isLoading = true },
            onSuccess = { isLoading = false },
            onFailure = { isLoading = false })

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
                NewMainScreen()
                if (isLoading) {
                    DialogForLoad { }
                }
            }
        }
    }

    @Composable
    fun NewMainScreen() {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scaffoldScope = rememberCoroutineScope()
        val scrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        val tobaccoData by viewModel.tobaccoLiveData.observeAsState(initial = 0f)
        val alcoholData by viewModel.alcoholLiveData.observeAsState(initial = 0f)
        val partiesData by viewModel.partiesLiveData.observeAsState(initial = 0f)
        val othersData by viewModel.othersLiveData.observeAsState(initial = 0f)
        val totals = tobaccoData + alcoholData + partiesData + othersData
        if (isHelpPressed) {
            HelpDialog(
                onDismissRequest = { isHelpPressed = false },
                text = stringResource(id = R.string.help_acc)
            )
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = { MainNavigationDrawer() }) {
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
                                scaffoldScope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            })
                            {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = stringResource(R.string.menu)
                                )
                            }
                        },
                        actions = {
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
                                    contentDescription = stringResource(id = R.string.history_acc)
                                )
                            }

                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                tooltip = { PlainTooltip { Text(stringResource(R.string.about_help)) } },
                                state = rememberTooltipState()
                            ) {
                                IconButton(onClick = {
                                    isHelpPressed = true
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = stringResource(id = R.string.help_content)
                                    )
                                }
                            }
                        }
                    )
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
                            Spacer(modifier = Modifier.padding(20.dp))
                            CategoryCard(alcoholData.toInt(), stringResource(R.string.alcohol))
                            Spacer(modifier = Modifier.padding(20.dp))
                            CategoryCard(partiesData.toInt(), stringResource(R.string.parties))
                            Spacer(modifier = Modifier.padding(20.dp))
                            CategoryCard(othersData.toInt(), stringResource(R.string.others))
                        }
                    }
                },
                bottomBar = {
                    BottomAppBar(actions = {
                        TextButton(onClick = { isDialogOpen = true }) {
                            Text(
                                text = stringResource(R.string.week_spend, totals.toInt()),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                        floatingActionButton = { HomeFab() }
                    )
                },
            )
        }
    }

    private fun dataHandlerForActivity(
        onStart: () -> Unit,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        onStart()
        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = SEVEN_DAYS,
            onSuccess = { data ->
                viewModel.updateLiveDataValues(this, data)
                onSuccess()
            },
            onFailure = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = applicationContext.getString(R.string.error_updating_data),
                        duration = SnackbarDuration.Short,
                        withDismissAction = true
                    )
                }
                onFailure()
            }
        )

        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = FOURTEEN_DAYS,
            onSuccess = { data ->
                viewModel.updateTwoWeekLiveDataValues(this, data)
                onSuccess()
            },
            onFailure = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = applicationContext.getString(R.string.error_updating_data),
                        duration = SnackbarDuration.Short,
                        withDismissAction = true
                    )
                }
                onFailure()
            }
        )

        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = THIRTY_DAYS,
            onSuccess = { data ->
                viewModel.updateThirtyDaysLiveDataValues(this, data)
                onSuccess()
            },
            onFailure = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = applicationContext.getString(R.string.error_updating_data),
                        duration = SnackbarDuration.Short,
                        withDismissAction = true
                    )
                }
                onFailure()
            }
        )
    }

    @Composable
    fun CategoryCard(value: Int, category: String) {
        var isDialogOpen by remember { mutableStateOf(false) }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.size(width = 240.dp, height = 120.dp),
            onClick = { isDialogOpen = true }
        ) {
            Text(
                text = category,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$$value",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        if (isDialogOpen) {
            DetailsExtendedDialog(onDismissRequest = { isDialogOpen = false }, category)
        }
    }

    @Composable
    fun HomeFab() {
        if (isMenuVisible) {
            ExpenseModalSheet { isMenuVisible = false }
        }

        ExtendedFloatingActionButton(
            onClick = { isMenuVisible = true },
            icon = { Icon(Icons.Filled.Add, stringResource(R.string.add_expense_button)) },
            text = { Text(text = stringResource(R.string.add)) },
        )
    }

    @Composable
    fun MainNavigationDrawer() {
        val displayName by viewModel.displayName.observeAsState(currentDisplayName)
        viewModel.setDisplayName(currentUser?.displayName)
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
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                stringResource(
                    R.string.account,
                    displayName ?: stringResource(R.string.error_no_user)
                ),
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.padding(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(8.dp))

            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.settings)) },
                icon = {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.account_settings_acc)
                    )
                },
                selected = false,
                onClick = {
                    val intent =
                        Intent(context, NewSettingsActivity::class.java); context.startActivity(
                    intent
                )
                }
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom
            ) {
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.sign_out)) },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.logout_acc)
                        )
                    },
                    selected = false,
                    onClick = { signOutRequest = true }
                )
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }

    @Composable
    fun SignOutDialog(
        onDismissRequest: () -> Unit,
        onSignOutConfirmed: () -> Unit,
        context: Context
    ) {
        val scope = rememberCoroutineScope()
        val exitDisplayName = ""
        Dialog(
            onDismissRequest = { onDismissRequest() },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            ElevatedCard(elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)) {
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
                        isLoading = true
                        viewModel.setDisplayName(exitDisplayName)
                        FirebaseUtils.signOut(onSuccess = {
                            val intent = Intent(
                                context,
                                LoginActivity::class.java
                            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
                            context.startActivity(intent)
                        }, onFailure = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.error_signing_out),
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            }
                        }
                        )
                        onSignOutConfirmed()
                        onDismissRequest()
                        isLoading = false
                    }
                    ) {
                        Text(
                            text = stringResource(R.string.yes),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                    Button(onClick = { onDismissRequest() }) {
                        Text(
                            text = stringResource(R.string.no),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun DetailsExtendedDialog(onDismissRequest: () -> Unit, category: String) {
        val viewModel = ViewModelProvider.provideMainViewModel()
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
        val total2Weeks =
            tobaccoData2Weeks + alcoholData2Weeks + partiesData2Weeks + othersData2Weeks
        val totalThirtyDays =
            tobaccoDataThirtyDays + alcoholDataThirtyDays + partiesDataThirtyDays + othersDataThirtyDays

        Dialog(
            onDismissRequest = { onDismissRequest() },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            when (category) {
                stringResource(id = R.string.tobacco) -> {
                    ElevatedCardForCategory(
                        onDismissRequest,
                        tobaccoData.toInt(),
                        tobaccoData2Weeks.toInt(),
                        tobaccoDataThirtyDays.toInt()
                    )
                }

                stringResource(id = R.string.alcohol) -> {
                    ElevatedCardForCategory(
                        onDismissRequest,
                        alcoholData.toInt(),
                        alcoholData2Weeks.toInt(),
                        alcoholDataThirtyDays.toInt()
                    )
                }

                stringResource(id = R.string.parties) -> {
                    ElevatedCardForCategory(
                        onDismissRequest,
                        partiesData.toInt(),
                        partiesData2Weeks.toInt(),
                        partiesDataThirtyDays.toInt()
                    )
                }

                stringResource(id = R.string.others) -> {
                    ElevatedCardForCategory(
                        onDismissRequest,
                        othersData.toInt(),
                        othersData2Weeks.toInt(),
                        othersDataThirtyDays.toInt()
                    )
                }

                TOTALS -> {
                    ElevatedCardForCategory(
                        onDismissRequest,
                        totalWeek.toInt(),
                        total2Weeks.toInt(),
                        totalThirtyDays.toInt()
                    )
                }
            }

        }
    }

    @Composable
    fun DetailCardForCategory(text: Int, value: Int) {
        ElevatedCard(
            modifier = Modifier
                .padding(4.dp, 24.dp)
                .width(240.dp)
                .height(120.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(text, value),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
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
        ElevatedCard(modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(16.dp)) {
            LazyColumn(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    DetailCardForCategory(
                        R.string.your_spending_in_the_last_week_is,
                        weekValue
                    )
                }
                item {
                    DetailCardForCategory(
                        R.string.your_spending_in_the_last_2_weeks_is,
                        twoWeekValue
                    )
                }
                item {
                    DetailCardForCategory(
                        R.string.your_spending_in_the_last_month_is,
                        thirtyDaysValue
                    )
                }
                item {
                    ElevatedCard(
                        modifier = Modifier.padding(8.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.this_could_be_savings_for_your_vacation_or_to_fulfill_that_dream_you_have_pending),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        OutlinedButton(onClick = { onDismissRequest() }) {
                            Text(
                                text = stringResource(
                                    R.string.close
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ExpenseModalSheet(onClose: () -> Unit) {
        val edgeToEdgeEnabled by remember { mutableStateOf(false) }
        val windowInsets = if (edgeToEdgeEnabled)
            WindowInsets(0) else BottomSheetDefaults.windowInsets
        val categories = listOf(
            stringResource(R.string.tobacco),
            stringResource(R.string.alcohol),
            stringResource(R.string.parties),
            stringResource(R.string.others)
        )
        var isCategorySelected by remember { mutableIntStateOf(0) }
        var expense by remember { mutableIntStateOf(0) }
        var category by remember { mutableStateOf("") }
        var isEmpty by remember { mutableStateOf(false) }
        var hideKeyboard by remember { mutableStateOf(false) }

        ModalBottomSheet(
            onDismissRequest = { onClose() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            windowInsets = windowInsets
        ) {
            LazyColumn {
                item {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.choose_category),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.padding(
                                start = 40.dp,
                                end = 40.dp
                            )
                        ) {
                            categories.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = categories.size
                                    ),
                                    onClick = { isCategorySelected = index },
                                    selected = index == isCategorySelected
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(10.dp))
                    Spacer(modifier = Modifier.padding(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.how_many),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp, top = 14.dp),
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                if (isEmpty) {
                                    PlainTooltip {
                                        Text(
                                            text = stringResource(R.string.null_value),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            },
                            state = TooltipState(initialIsVisible = isEmpty),
                        ) {
                            OutlinedTextField(
                                value = expense.toString(),
                                onValueChange = { it: String ->
                                    val maxLength = SEVEN_DAYS
                                    val filteredValue = it.filter { it.isDigit() }.take(maxLength)
                                    expense =
                                        if (filteredValue.isNotEmpty()) filteredValue.toInt() else 0
                                },
                                isError = isEmpty,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { hideKeyboard = true }),
                                modifier = Modifier.padding(start = 48.dp, end = 16.dp),
                            )

                            if (hideKeyboard) {
                                LocalSoftwareKeyboardController.current?.hide()
                                hideKeyboard = false
                            }
                        }
                    }
                }
                items(2) {
                    Spacer(modifier = Modifier.padding(10.dp))
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        FilledTonalButton(modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                            onClick = {
                                if (expense == 0) {
                                    isEmpty = true
                                } else {
                                    isEmpty = false
                                    category = ""
                                    when (isCategorySelected) {
                                        0 -> category = TOBACCO
                                        1 -> category = ALCOHOL
                                        2 -> category = PARTIES
                                        3 -> category = OTHERS
                                    }
                                    FirebaseUtils.addDataToCategory(category, expense, {
                                        dataHandlerForActivity(
                                            onStart = { isLoading = true },
                                            onSuccess = { isLoading = false },
                                            onFailure = { isLoading = false })
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = applicationContext.getString(R.string.data_added_successfully),
                                                duration = SnackbarDuration.Short,
                                                withDismissAction = true
                                            )
                                        }
                                    }) {
                                        dataHandlerForActivity(
                                            onStart = { isLoading = true },
                                            onSuccess = { isLoading = false },
                                            onFailure = { isLoading = false })
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = applicationContext.getString(R.string.error_updating_data),
                                                duration = SnackbarDuration.Short,
                                                withDismissAction = true
                                            )
                                        }
                                    }
                                    onClose()
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.add))
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
        private const val TOBACCO = "tobacco"
        private const val ALCOHOL = "alcohol"
        private const val PARTIES = "parties"
        private const val OTHERS = "others"
        private const val TOTALS = "totals"
    }
}