package com.sgtech.freevices.views.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.SettingsActivity
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel = ViewModelProvider.provideMainViewModel()
        super.onCreate(savedInstanceState)
        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = 30,
            onSuccess = { data ->
                viewModel.updateThirtyDaysLiveDataValues(this, data)
            },
            onFailure = {
            }
        )
        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = 60,
            onSuccess = { data ->
                viewModel.updateSixtyDaysLiveDataValues(this, data)
            },
            onFailure = {
                
            }
        )
        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = 90,
            onSuccess = { data ->
                viewModel.updateThreeMonthsLiveDataValues(this, data)
            },
            onFailure = {
                
            }
        )
        FirebaseUtils.dataHandler(
            context = applicationContext,
            days = 180,
            onSuccess = { data ->
                viewModel.updateSixMonthLiveDataValues(this, data)
            },
            onFailure = {
                
            }
        )
        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            val context = LocalContext.current
            val tobaccoDataThirtyDays by viewModel.tobaccoThirtyDaysData.observeAsState(initial = 0f)
            val alcoholDataThirtyDays by viewModel.alcoholThirtyDaysData.observeAsState(initial = 0f)
            val partiesDataThirtyDays by viewModel.partiesThirtyDaysData.observeAsState(initial = 0f)
            val othersDataThirtyDays by viewModel.othersThirtyDaysData.observeAsState(initial = 0f)
            val tobaccoDataSixtyDays by viewModel.tobaccoSixtyDaysData.observeAsState(initial = 0f)
            val alcoholDataSixtyDays by viewModel.alcoholSixtyDaysData.observeAsState(initial = 0f)
            val partiesDataSixtyDays by viewModel.partiesSixtyDaysData.observeAsState(initial = 0f)
            val othersDataSixtyDays by viewModel.othersSixtyDaysData.observeAsState(initial = 0f)
            val tobaccoDataThreeMonths by viewModel.tobaccoThreeMonthsData.observeAsState(initial = 0f)
            val alcoholDataThreeMonths by viewModel.alcoholThreeMonthsData.observeAsState(initial = 0f)
            val partiesDataThreeMonths by viewModel.partiesThreeMonthsData.observeAsState(initial = 0f)
            val othersDataThreeMonths by viewModel.othersThreeMonthsData.observeAsState(initial = 0f)
            val tobaccoDataSixMonths by viewModel.tobaccoSixMonthData.observeAsState(initial = 0f)
            val alcoholDataSixMonths by viewModel.alcoholSixMonthData.observeAsState(initial = 0f)
            val partiesDataSixMonths by viewModel.partiesSixMonthData.observeAsState(initial = 0f)
            val othersDataSixMonths by viewModel.othersSixMonthData.observeAsState(initial = 0f)
            val totalThirtyDays = tobaccoDataThirtyDays + alcoholDataThirtyDays + partiesDataThirtyDays + othersDataThirtyDays
            val totalSixtyDays = tobaccoDataSixtyDays + alcoholDataSixtyDays + partiesDataSixtyDays + othersDataSixtyDays
            val totalThreeMonths = tobaccoDataThreeMonths + alcoholDataThreeMonths + partiesDataThreeMonths + othersDataThreeMonths
            val totalSixMonths = tobaccoDataSixMonths + alcoholDataSixMonths + partiesDataSixMonths + othersDataSixMonths

            FreeVicesTheme {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        MainNavigationDrawer()
                    }
                ) {
                    Scaffold(
                        topBar = {
                            LargeTopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(R.string.menu_history),
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                },
                                scrollBehavior = scrollBehavior,
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            finish()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Finish History",
                                        )
                                    }

                                },
                                actions = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            val intent =
                                                android.content.Intent(
                                                    context,
                                                    SettingsActivity::class.java
                                                )
                                            context.startActivity(intent)
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Settings,
                                            contentDescription = "Settings",
                                        )
                                    }
                                }
                            )
                        },
                        snackbarHost = {
                            SnackbarHost(hostState = SnackbarHostState())
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { paddingValues ->
                        LazyColumn(
                            modifier = Modifier.padding(paddingValues),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            item { HistoryCard(30, totalThirtyDays.toInt()) }
                            item { HistoryCard(60, totalSixtyDays.toInt()) }
                            item { HistoryCard(90, totalThreeMonths.toInt()) }
                            item { HistoryCard(180, totalSixMonths.toInt()) }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HistoryCard(days: Int, value: Int) {
        var isDialogPressed by remember { mutableStateOf(false) }
        if (isDialogPressed) {
            ExpandedHistoryCard({isDialogPressed = false}, days)
        }
        FreeVicesTheme {
            OutlinedCard(
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                ),
                onClick = {
                    isDialogPressed = true
                }
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
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
    }

    @Composable
    fun ExpandedHistoryCard(onDismissRequest: () -> Unit, days: Int) {
        val viewModel = ViewModelProvider.provideMainViewModel()
        var tobaccoValue by remember {  mutableFloatStateOf(0f) }
        var alcoholValue by remember { mutableFloatStateOf(0f) }
        var partiesValue by remember { mutableFloatStateOf(0f) }
        var othersValue by remember { mutableFloatStateOf(0f) }

        when (days) {
            30 -> {
                tobaccoValue = viewModel.tobaccoThirtyDaysData.value ?: 0f
                alcoholValue = viewModel.alcoholThirtyDaysData.value ?: 0f
                partiesValue = viewModel.partiesThirtyDaysData.value ?: 0f
                othersValue = viewModel.othersThirtyDaysData.value ?: 0f
            }
            60 -> {
                tobaccoValue = viewModel.tobaccoSixtyDaysData.value ?: 0f
                alcoholValue = viewModel.alcoholSixtyDaysData.value ?: 0f
                partiesValue = viewModel.partiesSixtyDaysData.value ?: 0f
                othersValue = viewModel.othersSixtyDaysData.value ?: 0f
            }
            90 -> {
                tobaccoValue = viewModel.tobaccoThreeMonthsData.value ?: 0f
                alcoholValue = viewModel.alcoholThreeMonthsData.value ?: 0f
                partiesValue = viewModel.partiesThreeMonthsData.value ?: 0f
                othersValue = viewModel.othersThreeMonthsData.value ?: 0f
            }
            180 -> {
                tobaccoValue = viewModel.tobaccoSixMonthData.value ?: 0f
                alcoholValue = viewModel.alcoholSixMonthData.value ?: 0f
                partiesValue = viewModel.partiesSixMonthData.value ?: 0f
                othersValue = viewModel.othersSixMonthData.value ?: 0f
            }
        }
        Dialog(onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,

            ),
            content = {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 12.dp)
                ) {
                    LazyColumn(
                        content = {
                        item {
                            ElevatedCard(
                                modifier = Modifier.padding(24.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 12.dp
                                )
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxSize(),
                                    textAlign = TextAlign.Center,
                                    text = stringResource(
                                        R.string.tobacco_expense,
                                        tobaccoValue.toInt()
                                    ),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                            item {
                                ElevatedCard(
                                    modifier = Modifier.padding(24.dp),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 12.dp
                                    )
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxSize(),
                                        textAlign = TextAlign.Center,
                                        text = stringResource(
                                            R.string.alcohol_expense,
                                            alcoholValue.toInt()
                                        ),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                            item {
                                ElevatedCard(
                                    modifier = Modifier.padding(24.dp),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 12.dp
                                    )
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxSize(),
                                        textAlign = TextAlign.Center,
                                        text = stringResource(
                                            R.string.parties_expense,
                                            partiesValue.toInt()
                                        ),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                            item {
                                ElevatedCard(
                                    modifier = Modifier.padding(24.dp),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 12.dp
                                    )
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxSize(),
                                        textAlign = TextAlign.Center,
                                        text = stringResource(
                                            R.string.others_expense,
                                            othersValue.toInt()
                                        ),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                    })
                }

            }
        )
    }
}
