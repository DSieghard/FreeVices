package com.sgtech.freevices.views.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils

@Composable
fun HomeFab() {
    var isMenuVisible by remember { mutableStateOf(false) }

    if (isMenuVisible) {
        DeployMenu(onDismissRequest = { isMenuVisible = false })
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
fun DetailsExtendedDialog(onDismissRequest: () -> Unit, weekValue: Int, twoWeekValue: Int, monthValue: Int) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
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
                Card(modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(24.dp)){
                    Text(text = stringResource(
                        R.string.your_spending_in_the_last_week_is,
                        weekValue
                    ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Card(modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(24.dp)){
                    Text(text = stringResource(
                        R.string.your_spending_in_the_last_2_weeks_is,
                        twoWeekValue
                    ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Card(modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(24.dp)){
                    Text(text = stringResource(
                        R.string.your_spending_in_the_last_month_is,
                        monthValue
                    ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp))
                }

                Spacer(modifier = Modifier.padding(32.dp))
                Card(modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(24.dp)){
                    Text(text = stringResource(R.string.this_could_be_savings_for_your_vacation_or_to_fulfill_that_dream_you_have_pending),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp))
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDialog(
    category: String,
    onCancel: () -> Unit
) {
    val viewModel = ViewModelProvider.provideMainViewModel()
    var expenseAmount by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onCancel
    ) {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Add Expense", modifier = Modifier.padding(16.dp))
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = expenseAmount.toString(),
                    onValueChange = {
                        expenseAmount = it.toIntOrNull() ?: 0
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
                                FirebaseUtils.dataHandlerForWeek(
                                context = context,
                                onSuccess = { data ->
                                    viewModel.updateLiveDataValues(context, data)
                                    onCancel()
                                },
                                onFailure = {
                                    //Create Snackbar with error

                                }
                            )
                            },
                            onFailure = {
                                //Create Snackbar with error
                            }
                        )
                    }) {
                        Text(stringResource(R.string.add))
                    }
                }
            }
        }
    }
}

@Composable
fun DeployMenu(onDismissRequest: () -> Unit) {
    var expenseSelected by remember { mutableStateOf(false) }
    var categorySelected by remember { mutableStateOf("") }
    if (expenseSelected) {
        when (categorySelected) {
            "tobacco" -> {
                ExpenseDialog(stringResource(id = R.string.tobacco), onDismissRequest)
            }
            "alcohol" -> {
                ExpenseDialog(stringResource(id = R.string.alcohol), onDismissRequest)
            }
            "parties" -> {
                ExpenseDialog(stringResource(id = R.string.parties), onDismissRequest)
            }
            "others" -> {
                ExpenseDialog(stringResource(id = R.string.others), onDismissRequest)
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
                "Choose Category",
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
                        "Tobacco",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                    onClick = { expenseSelected =  true
                    categorySelected = "tobacco"})
                DropdownMenuItem(text = {
                    Text(
                        "Alcohol",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                    onClick = { expenseSelected = true
                    categorySelected = "alcohol"})
                DropdownMenuItem(text = {
                    Text(
                        "Parties",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                    onClick = { expenseSelected = true
                    categorySelected = "parties"})
                DropdownMenuItem(text = {
                    Text(
                        "Others",
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

@Composable
fun LoadingDialog() {
    Row {
        Text(text = "Loading...", modifier = Modifier.align(Alignment.CenterVertically))
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}