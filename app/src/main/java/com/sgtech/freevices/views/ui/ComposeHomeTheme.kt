package com.sgtech.freevices.views.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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

@Composable
fun SignOutDialog(onDismissRequest: () -> Unit, onSignOutConfirmed: () -> Unit, context: Context){
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
                "Sign Out",
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Are you sure you want to sign out?",
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
                    FirebaseUtils.signOut(context)
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
