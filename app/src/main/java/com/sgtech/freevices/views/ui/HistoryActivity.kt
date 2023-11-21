package com.sgtech.freevices.views.ui

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.lifecycleScope
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.settings.NewHistorySettingsActivity
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private val viewModel = ViewModelProvider.provideMainViewModel()
    private val themeViewModel = ViewModelProvider.provideThemeViewModel()
    private val scope = lifecycleScope
    private val snackbarHostState = SnackbarHostState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val daysList = listOf(THIRTY_DAYS, SIXTY_DAYS, THREE_MONTHS, SIX_MONTHS)
        daysList.forEach { days ->
            FirebaseUtils.dataHandler(
                context = applicationContext,
                days = days,
                onSuccess = { data ->
                    when(days)
                    {
                        THIRTY_DAYS -> viewModel.updateThirtyDaysLiveDataValues(this, data)
                        SIXTY_DAYS -> viewModel.updateSixtyDaysLiveDataValues(this, data)
                        THREE_MONTHS -> viewModel.updateThreeMonthsLiveDataValues(this, data)
                        SIX_MONTHS -> viewModel.updateSixMonthLiveDataValues(this, data)
                    }
                },
                onFailure = {
                    scope.launch{
                        snackbarHostState.showSnackbar(
                            message = applicationContext.getString(R.string.error_updating_data),
                            duration = SnackbarDuration.Short,
                            withDismissAction = true
                        )
                    }
                }
            )
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()),
        )

        setContent {
            FreeVicesTheme(useDynamicColors = themeViewModel.isDynamicColor.value) {
                HistoryScreenView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HistoryScreenView(){
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val context = LocalContext.current
        val tobaccoDataThirtyDays by viewModel.tobaccoThirtyDaysData.observeAsState(initial = FLOAT_ZERO)
        val alcoholDataThirtyDays by viewModel.alcoholThirtyDaysData.observeAsState(initial = FLOAT_ZERO)
        val partiesDataThirtyDays by viewModel.partiesThirtyDaysData.observeAsState(initial = FLOAT_ZERO)
        val othersDataThirtyDays by viewModel.othersThirtyDaysData.observeAsState(initial = FLOAT_ZERO)
        val tobaccoDataSixtyDays by viewModel.tobaccoSixtyDaysData.observeAsState(initial = FLOAT_ZERO)
        val alcoholDataSixtyDays by viewModel.alcoholSixtyDaysData.observeAsState(initial = FLOAT_ZERO)
        val partiesDataSixtyDays by viewModel.partiesSixtyDaysData.observeAsState(initial = FLOAT_ZERO)
        val othersDataSixtyDays by viewModel.othersSixtyDaysData.observeAsState(initial = FLOAT_ZERO)
        val tobaccoDataThreeMonths by viewModel.tobaccoThreeMonthsData.observeAsState(initial = FLOAT_ZERO)
        val alcoholDataThreeMonths by viewModel.alcoholThreeMonthsData.observeAsState(initial = FLOAT_ZERO)
        val partiesDataThreeMonths by viewModel.partiesThreeMonthsData.observeAsState(initial = FLOAT_ZERO)
        val othersDataThreeMonths by viewModel.othersThreeMonthsData.observeAsState(initial = FLOAT_ZERO)
        val tobaccoDataSixMonths by viewModel.tobaccoSixMonthData.observeAsState(initial = FLOAT_ZERO)
        val alcoholDataSixMonths by viewModel.alcoholSixMonthData.observeAsState(initial = FLOAT_ZERO)
        val partiesDataSixMonths by viewModel.partiesSixMonthData.observeAsState(initial = FLOAT_ZERO)
        val othersDataSixMonths by viewModel.othersSixMonthData.observeAsState(initial = FLOAT_ZERO)
        val totalThirtyDays = tobaccoDataThirtyDays + alcoholDataThirtyDays + partiesDataThirtyDays + othersDataThirtyDays
        val totalSixtyDays = tobaccoDataSixtyDays + alcoholDataSixtyDays + partiesDataSixtyDays + othersDataSixtyDays
        val totalThreeMonths = tobaccoDataThreeMonths + alcoholDataThreeMonths + partiesDataThreeMonths + othersDataThreeMonths
        val totalSixMonths = tobaccoDataSixMonths + alcoholDataSixMonths + partiesDataSixMonths + othersDataSixMonths
        var isHelpPressed by rememberSaveable { mutableStateOf(false) }
        if (isHelpPressed) {
            HelpDialog(
                onDismissRequest = { isHelpPressed = false },
                text = stringResource(R.string.help_history_main)
            )
        }

        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.menu_history),
                            style = MaterialTheme.typography.headlineLarge)
                    },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { finish() } }) 
                        {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.finish_history),
                            )
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
                        )
                        { IconButton(onClick = {
                            isHelpPressed = true
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = stringResource(R.string.history_help_acc),
                                )
                            }
                        }
                        IconButton(onClick = {
                            scope.launch {
                                val intent =
                                    android.content.Intent(
                                        context,
                                        NewHistorySettingsActivity::class.java
                                    )
                                context.startActivity(intent)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.history_settings_acc),
                            )
                        }
                    }
                )
            },
            snackbarHost = {
                snackbarHostState
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(48.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item { HistoryCard(THIRTY_DAYS, totalThirtyDays.toInt()) }
                item { HistoryCard(SIXTY_DAYS, totalSixtyDays.toInt()) }
                item { HistoryCard(THREE_MONTHS, totalThreeMonths.toInt()) }
                item { HistoryCard(SIX_MONTHS, totalSixMonths.toInt()) }
            }
        }
    }

    @Composable
    fun HistoryCard(days: Int, value: Int) {
        var isDialogPressed by rememberSaveable { mutableStateOf(false) }
        if (isDialogPressed) {
            ExpandedHistoryCard({isDialogPressed = false}, days)
        }
        ElevatedCard(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            shape = MaterialTheme.shapes.large,
            onClick = {
                isDialogPressed = true
            }
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(
                    R.string.your_total_expense_in_the_last_days_is,
                    days,
                    value
                ),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

    @Composable
    fun ExpandedHistoryCard(onDismissRequest: () -> Unit, days: Int) {
        val viewModel = ViewModelProvider.provideMainViewModel()
        var tobaccoValue by remember {  mutableFloatStateOf(FLOAT_ZERO) }
        var alcoholValue by remember { mutableFloatStateOf(FLOAT_ZERO) }
        var partiesValue by remember { mutableFloatStateOf(FLOAT_ZERO) }
        var othersValue by remember { mutableFloatStateOf(FLOAT_ZERO) }

        when (days) {
            THIRTY_DAYS -> {
                tobaccoValue = viewModel.tobaccoThirtyDaysData.value ?: FLOAT_ZERO
                alcoholValue = viewModel.alcoholThirtyDaysData.value ?: FLOAT_ZERO
                partiesValue = viewModel.partiesThirtyDaysData.value ?: FLOAT_ZERO
                othersValue = viewModel.othersThirtyDaysData.value ?: FLOAT_ZERO
            }
            SIXTY_DAYS -> {
                tobaccoValue = viewModel.tobaccoSixtyDaysData.value ?: FLOAT_ZERO
                alcoholValue = viewModel.alcoholSixtyDaysData.value ?: FLOAT_ZERO
                partiesValue = viewModel.partiesSixtyDaysData.value ?: FLOAT_ZERO
                othersValue = viewModel.othersSixtyDaysData.value ?: FLOAT_ZERO
            }
            THREE_MONTHS -> {
                tobaccoValue = viewModel.tobaccoThreeMonthsData.value ?: FLOAT_ZERO
                alcoholValue = viewModel.alcoholThreeMonthsData.value ?: FLOAT_ZERO
                partiesValue = viewModel.partiesThreeMonthsData.value ?: FLOAT_ZERO
                othersValue = viewModel.othersThreeMonthsData.value ?: FLOAT_ZERO
            }
            SIX_MONTHS -> {
                tobaccoValue = viewModel.tobaccoSixMonthData.value ?: FLOAT_ZERO
                alcoholValue = viewModel.alcoholSixMonthData.value ?: FLOAT_ZERO
                partiesValue = viewModel.partiesSixMonthData.value ?: FLOAT_ZERO
                othersValue = viewModel.othersSixMonthData.value ?: FLOAT_ZERO
            }
        }
        Dialog(onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true),
            content = {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    LazyColumn(
                        content = {
                        item {DetailCard(text = stringResource(R.string.tobacco_expense, tobaccoValue.toInt()))}
                        item {DetailCard(text = stringResource(R.string.alcohol_expense, alcoholValue.toInt()))}
                        item {DetailCard(text = stringResource(R.string.parties_expense, partiesValue.toInt()))}
                        item {DetailCard(text = stringResource(R.string.others_expense, othersValue.toInt())) }
                        }
                    )
                }
            }
        )
    }

    @Composable
    fun DetailCard(text: String) {
        ElevatedCard(
            modifier = Modifier.padding(24.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center,
                text = text,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

    companion object {
        private const val THIRTY_DAYS = 30
        private const val SIXTY_DAYS = 60
        private const val THREE_MONTHS = 90
        private const val SIX_MONTHS = 180
        private const val FLOAT_ZERO = 0f
    }
}
