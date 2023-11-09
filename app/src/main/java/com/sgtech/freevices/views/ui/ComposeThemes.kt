package com.sgtech.freevices.views.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.overview.ui.theme.FreeVicesTheme

@Composable
    fun TobaccoCard(value: Int) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .size(width = 240.dp, height = 120.dp)
        ) {
            Text(
                text = stringResource(R.string.tobacco),
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
    }

    @Composable
    fun AlcoholCard(value: Int) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .size(width = 240.dp, height = 120.dp)
        ) {
            Text(
                text = stringResource(R.string.alcohol),
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
    }

    @Composable
    fun PartiesCard(value: Int) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .size(width = 240.dp, height = 120.dp)

        ) {
            Text(
                text = stringResource(R.string.parties),
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
    }

    @Composable
    fun OtherCard(value: Int) {
        ElevatedCard (
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .size(width = 240.dp, height = 120.dp)
        ) {
            Text(
                text = stringResource(R.string.others),
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
    }

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
                .size(width = 360.dp, height = 120.dp)
        ) {
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

        val padding = Modifier.padding(16.dp)
        FreeVicesTheme {
            // A surface container using the 'background' color from the theme
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                TobaccoCard(0)
                Spacer(modifier = padding)
                AlcoholCard(0)
                Spacer(modifier = padding)
                PartiesCard(0)
                Spacer(modifier = padding)
                OtherCard(0)
                Spacer(modifier = Modifier.padding(32.dp))
                TotalCardForMonth(0)
            }
        }
    }
