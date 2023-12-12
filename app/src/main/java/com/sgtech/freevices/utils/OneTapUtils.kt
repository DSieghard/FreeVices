package com.sgtech.freevices.utils

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity

@Composable
fun rememberOneTapSignInState(): OneTapSignInState {
    return remember { OneTapSignInState() }
}

const val TAG = "OneTapUtils"

@Composable
fun OneTapSignInGoogleCredential(
    state: OneTapSignInState,
    clientId: String,
    rememberAccount: Boolean = true,
    onTokenReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit
) {
    val activity: Activity = LocalContext.current as Activity
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        Log.d(TAG, "Result: ${result.resultCode}")
        try {
            when (result.resultCode) {
                Activity.RESULT_CANCELED -> {
                    onDialogDismissed("Dialog Closed. ${result.resultCode}")
                    Log.d(TAG, "{$result.resultCode}")
                    state.close()
                }

                Activity.RESULT_OK -> {
                    // User successfully signed in using One Tap
                    val oneTapClient = Identity.getSignInClient(activity)
                    val credentials = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials.googleIdToken
                    if (googleIdToken != null) {
                        onTokenReceived(googleIdToken)
                        state.close()
                    } else {
                        // Failed to extract credentials from intent
                        Log.e(TAG, "Couldn't get credential from result.")
                        onDialogDismissed("Couldn't get credential from result.")
                        state.close()
                    }
                }

                else -> {
                    // Unexpected resultCode
                    Log.d(TAG, "Unexpected result: ${result.resultCode}")
                    onDialogDismissed("Unexpected result: ${result.resultCode}")
                    state.close()
                }
            }
        } catch (e: Exception) {
            // Handle any exceptions that occur during the process
            Log.e(TAG, "Error handling result: ${e.message}")
        }
    }

    LaunchedEffect(key1 = state.opened) {
        if (state.opened) {
            signIn(
                activity = activity,
                clientId = clientId,
                rememberAccount = rememberAccount,
                launchActivityResult = { intentSenderRequest ->
                    activityLauncher.launch(intentSenderRequest)
                },
                onError = {
                    Log.d(TAG, "Error: $it")
                    onDialogDismissed("Error: $it")
                    state.close()
                }
            )
        }
    }
}

private fun signIn(
    activity: Activity,
    clientId: String,
    rememberAccount: Boolean,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    onError: (Exception) -> Unit
) {
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(rememberAccount)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            Log.d(TAG, "Result: $signInRequest")
            Log.d(TAG, "Result sign in: $result")
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                )
            } catch (e: Exception) {
                Log.d(TAG, "Error: $e")
                onError(e.message?.let { Exception(it) } ?: e)
            }
        }
        .addOnFailureListener {
            signUp(
                activity = activity,
                clientId = clientId,
                rememberAccount = rememberAccount,
                launchActivityResult = launchActivityResult,
                onError = onError
            )
            Log.e(TAG, "Error: ${it.message}")
        }
}

private fun signUp(
    activity: Activity,
    clientId: String,
    rememberAccount: Boolean,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    onError: (Exception) -> Unit
) {
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(rememberAccount)
                .build()
        )
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                )
            } catch (e: Exception) {
                Log.d(TAG, "Error: $e")
                onError(e.message?.let { Exception(it) } ?: e)
            }

        }
        .addOnFailureListener {
            Log.e(TAG, "Error: ${it.message}")
            onError(it)
        }
}


