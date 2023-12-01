package com.sgtech.freevices.views.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.google.firebase.auth.FirebaseAuth
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.DialogForLoad
import com.sgtech.freevices.views.ui.LoginActivity
import com.sgtech.freevices.views.ui.NewMainActivity
import com.sgtech.freevices.views.ui.ViewModelProvider
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewUserSettingsActivity : AppCompatActivity() {
    //View model
    private val themeViewModel = ViewModelProvider.provideThemeViewModel()
    private val mainViewModel = ViewModelProvider.provideMainViewModel()

    //Extensions
    private val context = this
    private val scope = CoroutineScope(Dispatchers.Main)
    private val snackbarHostState = SnackbarHostState()

    //Option Strings
    private var option by mutableStateOf("")

    //Booleans
    private var mailVerified by mutableStateOf(false)
    private var isPasswordChangeInvoked by mutableStateOf(false)
    private var isEmailChangeInvoked by mutableStateOf(false)
    private var isDeleteAccountInvoked by mutableStateOf(false)
    private var isReAuthRequired by mutableStateOf(false)
    private var isReAuthApproved by mutableStateOf(false)
    private var isHelpPressed by mutableStateOf(false)
    private var passwordHidden by mutableStateOf(true)
    private var isPasswordFilled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                UserSettingsView()
            }
        }
    }

    @Composable
    fun UserSettingsView() {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AppBar()
            }
        ) {
            Body(paddingValues = it)
        }

        when (option) {
            NAMECHANGE -> {
                ChangeNameDialog { option = BLANK }
            }

            PASSWORDCHANGE -> {
                ReAuthDialog(onDismissRequest = { option = BLANK }, option = PASSWORDCHANGE)
            }

            EMAILCHANGE -> {
                ReAuthDialog(onDismissRequest = { option = BLANK }, option = EMAILCHANGE)
            }

            DELETEACCOUNT -> {
                ReAuthDialog(onDismissRequest = { option = BLANK }, option = DELETEACCOUNT)
            }
        }

        if (isEmailChangeInvoked) {
            ChangeEmailDialog { isEmailChangeInvoked = false }
        }

        if (isPasswordChangeInvoked) {
            ChangePasswordDialog { isPasswordChangeInvoked = false }
        }

        if (isDeleteAccountInvoked) {
            DeleteAccountDialog { isDeleteAccountInvoked = false }
        }
    }

    @Composable
    fun Body(paddingValues: PaddingValues) {
        val pad = Modifier.padding(16.dp)
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                HorizontalDivider(pad)
                VerifyRow()
                HorizontalDivider(pad)
                ChangeNameSetting()
                ChangePasswordSetting()
                ChangeEmailSetting()
                HorizontalDivider(pad)
                DeleteAccountSetting()
                HorizontalDivider(pad)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar() {
        MediumTopAppBar(title = { Text(text = stringResource(R.string.user_settings)) },
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        val intent = Intent(context, NewMainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        startActivity(intent)
                    }
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = getString(R.string.back)
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
                ) {
                    IconButton(
                        onClick = { isHelpPressed = true }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = getString(R.string.help)
                        )
                    }
                }
            }
        )
    }

    @Composable
    fun VerifyRow() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(
                    R.string.account_status,
                    if (mailVerified) stringResource(R.string.verified) else stringResource(R.string.not_verified)
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (mailVerified) {
                VerifyButton(state = false)
            } else {
                VerifyButton(state = true)
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }

    @Composable
    fun ChangeNameSetting() {
        SettingsMenuLink(
            title = { Text(text = stringResource(R.string.change_display_name)) },
            subtitle = { Text(text = stringResource(R.string.change_your_display_name)) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = getString(R.string.change_display_name)
                )
            },
            onClick = { option = NAMECHANGE },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
    }

    @Composable
    fun ChangePasswordSetting() {
        SettingsMenuLink(
            title = { Text(text = stringResource(R.string.change_password)) },
            subtitle = { Text(text = stringResource(R.string.require_your_current_password)) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Password,
                    contentDescription = getString(R.string.change_password)
                )
            },
            onClick = { option = PASSWORDCHANGE },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
    }

    @Composable
    fun ChangeEmailSetting() {
        SettingsMenuLink(
            title = { Text(text = stringResource(R.string.change_email_address)) },
            subtitle = { Text(text = stringResource(R.string.update_your_email_address_require_your_current_password)) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.AlternateEmail,
                    contentDescription = getString(R.string.change_email_address)
                )
            },
            onClick = { option = EMAILCHANGE },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
    }

    @Composable
    fun DeleteAccountSetting() {
        SettingsMenuLink(
            title = { Text(text = stringResource(R.string.delete_account)) },
            subtitle = { Text(text = stringResource(R.string.delete_your_account_and_all_associated_data_this_cannot_be_undone)) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = getString(R.string.delete)
                )
            },
            onClick = { option = DELETEACCOUNT; isReAuthRequired = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
    }

    private fun splitNames(input: String): Pair<String, String>? {
        val names = input.split(" ")

        when (names.size) {
            TWO -> {
                val firstName = names[ZERO]
                val lastName = names[ONE]
                return Pair(firstName, lastName)
            }

            THREE -> {
                val firstName = names[ZERO]
                val lastName = names[ONE]
                val lastSecondName = names[TWO]
                return Pair("$firstName $lastName", lastSecondName)
            }

            FOUR -> {
                val firstName = names[ZERO]
                val secondName = names[ONE]
                val lastName = names[TWO]
                val lastSecondName = names[THREE]
                return Pair("$firstName $secondName", "$lastName $lastSecondName")
            }

            else -> {
                return null
            }
        }
    }

    @Composable
    fun DeleteAccountDialog(onDismissRequest: () -> Unit) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var isDeleteAccountConfirmed by rememberSaveable { mutableStateOf(false) }
        if (isDeleteAccountConfirmed) {
            FirebaseUtils.deleteAccount(
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.account_deleted),
                            duration = SnackbarDuration.Short
                        )
                    }
                    val intent = Intent(
                        context,
                        LoginActivity::class.java
                    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
                    startActivity(context, intent, null)
                    onDismissRequest()
                },
                {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(
                                R.string.error_deleting_account,
                                it.message
                            ),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
            onDismissRequest()
        }

        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            icon = { Icon(Icons.Filled.Delete, contentDescription = getString(R.string.delete)) },
            title = { Text(text = stringResource(R.string.delete_account)) },
            text = {
                Text(
                    stringResource(R.string.delete_account_warning),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(onClick = { isDeleteAccountConfirmed = true }) {
                    Text(
                        stringResource(R.string.delete)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissRequest(); option = BLANK
                }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    @Composable
    fun ChangeNameDialog(onDismissRequest: () -> Unit) {
        val firebaseUser = FirebaseUtils.getCurrentUser()
        var isChangeNameConfirmed by rememberSaveable { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        if (isChangeNameConfirmed) {
            FirebaseUtils.configDisplayNameOnAuth(name)
            splitNames(name)?.let {
                FirebaseUtils.updateDisplayNameOnFirestore(it.first, it.second, {}, {})
            }
            isChangeNameConfirmed = false
            mainViewModel.setDisplayName(firebaseUser?.displayName)
            onDismissRequest()
        }
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = getString(R.string.change_display_name)
                )
            },
            title = {
                Text(text = stringResource(R.string.change_display_name))
            },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.new_display_name)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isChangeNameConfirmed = true
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    @Composable
    fun ChangePasswordDialog(onDismissRequest: () -> Unit) {
        val context = LocalContext.current
        var showReLogin by rememberSaveable { mutableStateOf(false) }
        var isChangePasswordConfirmed by rememberSaveable { mutableStateOf(false) }
        var newPassword by remember { mutableStateOf("") }
        var confirmNewPassword by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        if (showReLogin) {
            ReLoginDialog(onDismissRequest = {
                showReLogin = false
                val intent = Intent(
                    context,
                    LoginActivity::class.java
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
                startActivity(context, intent, null)
            })
        }
        if (isChangePasswordConfirmed) {
            FirebaseUtils.configPasswordOnAuth(newPassword, onSuccess = {
                FirebaseUtils.signOut(onSuccess = {
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                    onDismissRequest()
                }, onFailure = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(
                                R.string.error_updating_password,
                                it.message
                            ),
                            duration = SnackbarDuration.Short,
                            withDismissAction = true
                        )
                    }
                })

            }, onFailure = {
                scope.launch {
                    Log.d("ChangePassword", "Password change failed")
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_updating_password, it.message),
                        duration = SnackbarDuration.Short,
                        withDismissAction = true
                    )
                }
            })
            isChangePasswordConfirmed = false
            onDismissRequest()
        }
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = {
                Icon(
                    Icons.Filled.Password,
                    contentDescription = getString(R.string.change_password)
                )
            },
            title = {
                Text(text = stringResource(R.string.change_password))
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.if_change_if_successful_you_must_login_again),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        singleLine = true,
                        isError = newPassword.isEmpty(),
                        label = { Text(stringResource(R.string.enter_password)) },
                        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { dismissKeyboardShortcutsHelper(); onDismissRequest() }),
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                val visibilityIcon =
                                    if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                val description =
                                    if (passwordHidden) stringResource(R.string.show_password) else stringResource(
                                        R.string.hide_password
                                    )
                                Icon(imageVector = visibilityIcon, contentDescription = description)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        singleLine = true,
                        isError = confirmNewPassword.isEmpty(),
                        label = { Text(stringResource(R.string.enter_password)) },
                        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { dismissKeyboardShortcutsHelper(); onDismissRequest() }),
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                val visibilityIcon =
                                    if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                val description =
                                    if (passwordHidden) stringResource(R.string.show_password) else stringResource(
                                        R.string.hide_password
                                    )
                                Icon(imageVector = visibilityIcon, contentDescription = description)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        if (newPassword == confirmNewPassword) {
                            isChangePasswordConfirmed = true
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.passwords_don_t_match),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.confirm_and_login))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { option = BLANK; onDismissRequest() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    @Composable
    fun ReLoginDialog(onDismissRequest: () -> Unit) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                val intent = Intent(
                    context,
                    LoginActivity::class.java
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
                startActivity(context, intent, null)
            },
            title = { Text(text = stringResource(R.string.password_changed_successfully)) },
            text = { Text(text = stringResource(R.string.relogin)) },
        )
    }

    @Composable
    fun ChangeEmailDialog(onDismissRequest: () -> Unit) {
        val context = LocalContext.current
        var isChangeEmailConfirmed by rememberSaveable { mutableStateOf(false) }
        var newEmail by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        if (isChangeEmailConfirmed) {
            FirebaseUtils.configEmailOnAuth(newEmail, onSuccess = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.email_changed_successfully),
                        duration = SnackbarDuration.Short
                    )
                }
            }, onFailure = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_updating_email, it.message),
                        duration = SnackbarDuration.Short
                    )
                }
            })
            isChangeEmailConfirmed = false
            onDismissRequest()
        }
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = getString(R.string.change_email_address)
                )
            },
            title = {
                Text(text = stringResource(R.string.change_email_address))
            },
            text = {
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text(stringResource(R.string.new_email)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isChangeEmailConfirmed = true
                    }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        option = BLANK
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    @Composable
    fun VerifyButton(state: Boolean) {
        val scope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(false) }
        if (isLoading) {
            DialogForLoad { }
        }
        Button(
            onClick = {
                isLoading = true
                FirebaseUtils.sendEmailVerification(
                    onSuccess = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = getString(R.string.verification_email_sent),
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        }
                        isLoading = false
                    }, onFailure = {
                        isLoading = false
                    })
            },
            enabled = state
        ) {
            Text(stringResource(R.string.verify))
        }
    }

    @Composable
    fun ReAuthDialog(onDismissRequest: () -> Unit, option: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        var password by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            icon = { Icon(Icons.Filled.Lock, contentDescription = getString(R.string.password)) },
            title = { Text(text = stringResource(R.string.reauth)) },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    isError = password.isEmpty(),
                    label = { Text(stringResource(R.string.enter_password)) },
                    visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { dismissKeyboardShortcutsHelper(); onDismissRequest() }),
                    trailingIcon = {
                        IconButton(onClick = { passwordHidden = !passwordHidden }) {
                            val visibilityIcon =
                                if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description =
                                if (passwordHidden) stringResource(R.string.show_password) else stringResource(
                                    R.string.hide_password
                                )
                            Icon(imageVector = visibilityIcon, contentDescription = description)
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { isPasswordFilled = true }) {
                    Text(
                        stringResource(R.string.confirm)
                    )
                }
            },
            dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text(stringResource(R.string.cancel)) } }
        )

        if (isPasswordFilled) {
            if (userEmail != null && password.isNotEmpty()) {
                FirebaseUtils.reAuthenticate(userEmail, password) { result ->
                    when (result) {
                        is FirebaseUtils.AuthResult.Success -> {
                            isReAuthApproved = true
                            when (option) {
                                PASSWORDCHANGE -> {
                                    isPasswordChangeInvoked = true
                                }

                                EMAILCHANGE -> {
                                    isEmailChangeInvoked = true
                                }

                                DELETEACCOUNT -> {
                                    isDeleteAccountInvoked = true
                                }
                            }
                        }

                        is FirebaseUtils.AuthResult.Failure -> {
                            val exception = result.exception
                            onDismissRequest()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = exception.message.toString(),
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val BLANK = ""
        private const val EMAILCHANGE = "emailchange"
        private const val PASSWORDCHANGE = "passwordchange"
        private const val NAMECHANGE = "namechange"
        private const val DELETEACCOUNT = "deleteaccount"
        private const val ZERO = 0
        private const val ONE = 1
        private const val TWO = 2
        private const val THREE = 3
        private const val FOUR = 4
    }
}