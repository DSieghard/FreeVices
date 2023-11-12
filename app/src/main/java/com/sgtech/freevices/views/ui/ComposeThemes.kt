package com.sgtech.freevices.views.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme


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
            onDismissRequest = { isDialogOpen = false }, 0, 0, 0
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TotalCardForMonth(value: Int){
        ElevatedCard (
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            modifier = Modifier
                .size(width = 360.dp, height = 120.dp),
        )
        {
            Text(
                text = stringResource(R.string.total),
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = "$$value",
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }


    @Preview
    @Composable
    fun PreviewCards() {

        Modifier.padding(16.dp)
        FreeVicesTheme {
            // A surface container using the 'background' color from the theme
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                CategoryCard(0, "Tobacco")
                Spacer(modifier = Modifier.padding(16.dp))
                CategoryCard(0, "Alcohol")
                Spacer(modifier = Modifier.padding(16.dp))
                CategoryCard(0, "Parties")
                Spacer(modifier = Modifier.padding(16.dp))
                CategoryCard(0, "Others")
                Spacer(modifier = Modifier.padding(32.dp))
                TotalCardForMonth(0)
            }
        }
    }

