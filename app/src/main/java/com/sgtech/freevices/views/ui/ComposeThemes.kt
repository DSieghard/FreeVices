package com.sgtech.freevices.views.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme

@Composable
fun HelpDialog(onDismissRequest: () -> Unit, text: String) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(context.getString(R.string.help)) },
        text = { Text(text = text, style = MaterialTheme.typography.bodyLarge) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = context.getString(R.string.close))
            }
        }
    )
}

@Composable
fun DialogForLoad(onDismiss: () -> Unit) {
    FreeVicesTheme{
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            ElevatedCard(
                modifier = Modifier
                    .padding(8.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp, 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Spacer(modifier = Modifier.padding(32.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(text = stringResource(R.string.loading))
                    Spacer(modifier = Modifier.padding(32.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoading(){
    DialogForLoad {}
}
