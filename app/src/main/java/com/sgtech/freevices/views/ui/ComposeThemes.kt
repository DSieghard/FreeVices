package com.sgtech.freevices.views.ui

import android.app.Activity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.NewMainActivity.*
import com.sgtech.freevices.views.ui.settings.NewUserSettingsActivity
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
            onDismissRequest = { isDialogOpen = false }, category
        )
    }
}

@Preview
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
        val currentScreen = remember { mutableStateOf(NewMainActivity()) } // Store the current screen

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

        Text(
            "Account: ${currentUser?.displayName}",
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.titleLarge
        )



        Spacer(modifier = Modifier.padding(8.dp))
        Divider()
        Spacer(modifier = Modifier.padding(8.dp))

        NavigationDrawerItem(
            label = { Text(text = "Account Settings") },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            selected = false,
            onClick = {
                val intent = android.content.Intent(context,
                    NewUserSettingsActivity::class.java)
                context.startActivity(intent)
            }
        )
        NavigationDrawerItem(
            label = { Text(text = "Send feedback") },
            icon = { Icon(Icons.Filled.Feedback, contentDescription = "Feedback") },
            selected = false,
            onClick = { /*TODO: Open Feedback (Explicit Intent)*/ }
        )

        Row(modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom) {
            NavigationDrawerItem(
                label = { Text(text = "Logout") },
                icon = { Icon(Icons.Filled.Logout, contentDescription = "Logout") },
                selected = false,
                onClick = { signOutRequest = true }
            )
            Spacer(modifier = Modifier.padding(8.dp))
        }

        // Display the current screen based on the stored value
        currentScreen.value
    }
}
