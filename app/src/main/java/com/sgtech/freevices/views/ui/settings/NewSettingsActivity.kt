package com.sgtech.freevices.views.ui.settings

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.ManageHistory
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.storage.base.SettingValueState
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.google.firebase.auth.FirebaseAuth
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.views.ui.DialogForLoad
import com.sgtech.freevices.views.ui.HelpDialog
import com.sgtech.freevices.views.ui.LoginActivity
import com.sgtech.freevices.views.ui.ViewModelProvider
import com.sgtech.freevices.views.ui.theme.FreeVicesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewSettingsActivity : AppCompatActivity() {

    //Core val
    private val firebaseUser = FirebaseUtils.getCurrentUser()
    private val userEmail = firebaseUser?.email ?: BLANK
    private val themeViewModel = ViewModelProvider.provideThemeViewModel()
    private val mainViewModel = ViewModelProvider.provideMainViewModel()
    private val snackbarHostState = SnackbarHostState()
    private val scope = CoroutineScope(Dispatchers.Main)
    private val context = this

    //Blank Strings
    private var newPassword by mutableStateOf(BLANK)
    private var confirmNewPassword by mutableStateOf(BLANK)
    private var name by mutableStateOf(BLANK)
    private var option by mutableStateOf(BLANK)
    private var password by mutableStateOf(BLANK)
    private var isCategorySelected by mutableIntStateOf(CANCELLED)
    private var isTimeStampSelected by mutableIntStateOf(CANCELLED)
    private var selectedCategory by mutableStateOf(BLANK)
    private var selectedTimeStamp by mutableIntStateOf(CANCELLED)

    //Booleans
    private var confirmDeleteHistory by mutableStateOf(false)
    private var isHelpPressed by mutableStateOf(false)
    private var mailVerified by mutableStateOf(false)
    private var isReAuthApproved by mutableStateOf(false)
    private var isReAuthRequired by mutableStateOf(false)
    private var passwordHidden by mutableStateOf(true)
    private var isPasswordFilled by mutableStateOf(false)
    private var showReLogin by mutableStateOf(false)
    private var isHistoryButtonSelected by mutableStateOf(false)
    private var isChangePasswordConfirmed by mutableStateOf(false)
    private var isChangeNameConfirmed by mutableStateOf(false)
    private var isLoading by mutableStateOf(false)
    private var isDeleteHistoryConfirmed by mutableStateOf(false)


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
                AppSettingsView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppSettingsView() {
        val state = rememberBooleanSettingState(defaultValue = themeViewModel.isDynamicColor.value)
        val scrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        if (isHelpPressed) {
            HelpDialog(
                onDismissRequest = {
                    isHelpPressed = false
                },
                text = context.getString(R.string.app_settings_help)
            )
        }
        if (state.value) {
            themeViewModel.setDynamicColor(true)
        } else {
            themeViewModel.setDynamicColor(false)
        }


        Scaffold(
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                SettingsAppBar(scrollBehavior)
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            content = {
                SettingsBody(padding = it, state = state)

                if (isHistoryButtonSelected) {
                    HistoryModalSheet {
                        isHistoryButtonSelected = false
                    }
                }

                if (isReAuthRequired) {
                    ReAuthDialog(
                        onDismissRequest = {
                            isReAuthRequired = false
                            isReAuthApproved = true
                        }
                    )
                }

                if (isReAuthApproved) {
                    when (option) {
                        PASSWORDCHANGE -> {
                            ChangePasswordDialog {
                                isReAuthApproved = false
                            }
                        }

                        EMAILCHANGE -> {
                            ChangeEmailDialog {
                                isReAuthApproved = false
                            }
                        }

                        DELETEACCOUNT -> {
                            DeleteAccountDialog {
                                isReAuthApproved = false
                            }
                        }
                    }
                }

                when (option) {
                    NAMECHANGE -> {
                        ChangeNameDialog {
                            option = BLANK
                        }
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HistoryModalSheet(onClose: () -> Unit) {
        val edgeToEdgeEnabled by remember { mutableStateOf(false) }
        val windowInsets = if (edgeToEdgeEnabled)
            WindowInsets(0) else BottomSheetDefaults.windowInsets

        val categories = listOf(
            stringResource(R.string.tobacco),
            stringResource(R.string.alcohol),
            stringResource(R.string.parties),
            stringResource(R.string.others)
        )

        val categoryTimeStamp = listOf(
            TODAY,
            LAST_WEEK,
            TWO_WEEKS,
            LAST_MONTH,
            TWO_MONTHS,
            THREE_MONTHS,
            SIX_MONTHS,
            ALL_TIME
        )

        val categoryTimeLabel = listOf(
            stringResource(R.string.today),
            stringResource(R.string.one_week),
            stringResource(R.string.two_weeks),
            stringResource(R.string.one_month),
            stringResource(R.string.two_months),
            stringResource(R.string.three_months),
            stringResource(R.string.six_months),
            stringResource(R.string.all_time)
        )

        selectedCategory = when (isCategorySelected) {
            ZERO -> {
                getString(R.string.tobacco)
            }

            ONE -> {
                getString(R.string.alcohol)
            }

            TWO -> {
                getString(R.string.parties)
            }

            THREE -> {
                getString(R.string.others)
            }

            else -> {
                BLANK
            }
        }

        selectedTimeStamp = when (isTimeStampSelected) {
            ZERO -> {
                TODAY
            }

            ONE -> {
                LAST_WEEK
            }

            TWO -> {
                TWO_WEEKS
            }

            THREE -> {
                LAST_MONTH
            }

            FOUR -> {
                TWO_MONTHS
            }

            FIVE -> {
                THREE_MONTHS
            }

            SIX -> {
                SIX_MONTHS
            }

            SEVEN -> {
                ALL_TIME
            }

            else -> {
                CANCELLED
            }
        }

        if (confirmDeleteHistory) {
            var error by mutableStateOf(false)
            AlertDialog(
                onDismissRequest = { confirmDeleteHistory = false },
                confirmButton = {
                    Button(onClick = {
                        isDeleteHistoryConfirmed = true
                    }) {
                        Text(text = stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { confirmDeleteHistory = false }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                title = {
                    Text(text = stringResource(R.string.delete_confirmation))
                },
                text = {
                    Text(text = stringResource(R.string.delete_warning))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = getString(R.string.delete)
                    )
                }
            )

            if (isDeleteHistoryConfirmed) {
                if (selectedCategory.isEmpty() || selectedTimeStamp == CANCELLED) {
                    error = true
                } else {
                    FirebaseUtils.deleteHistory(
                        context = context,
                        category = selectedCategory,
                        days = selectedTimeStamp,
                        onSuccess = {
                            confirmDeleteHistory = false
                            isDeleteHistoryConfirmed = false
                            scope.launch {
                                if (selectedTimeStamp == TODAY) {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.today_history_deleted),
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(
                                            R.string.history_deleted,
                                            selectedTimeStamp
                                        ),
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                }
                            }
                            isCategorySelected = CANCELLED
                            isTimeStampSelected = CANCELLED
                            isHistoryButtonSelected = false
                        },
                        onFailure = {
                            confirmDeleteHistory = false
                            isDeleteHistoryConfirmed = false
                            isHistoryButtonSelected = false
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(
                                        R.string.error_deleting_history,
                                        it.message
                                    ),
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            }
                            isCategorySelected = CANCELLED
                            isTimeStampSelected = CANCELLED
                        }
                    )
                }
            }
        }
        ModalBottomSheet(
            onDismissRequest = { onClose() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            windowInsets = windowInsets
        ) {
            LazyColumn {
                item {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.choose_category),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.padding(
                                start = 40.dp,
                                end = 40.dp
                            )
                        ) {
                            categories.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = categories.size
                                    ),
                                    onClick = { isCategorySelected = index },
                                    selected = index == isCategorySelected
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }
                }
                items(categoryTimeStamp.size) { index ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 28.dp, end = 28.dp)
                    ) {
                        SettingsRadioButton(
                            title = categoryTimeLabel[index],
                            isSelected = index == isTimeStampSelected,
                            onSelected = { isTimeStampSelected = index }
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        FilledTonalButton(modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                            onClick = { confirmDeleteHistory = true })
                        {
                            Text(text = stringResource(R.string.delete))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SettingsRadioButton(title: String, isSelected: Boolean, onSelected: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable(onClick = onSelected)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(text = title, style = MaterialTheme.typography.titleSmall)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsAppBar(scrollBehavior: TopAppBarScrollBehavior) {
        LargeTopAppBar(title = { Text(text = stringResource(R.string.settings)) },
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        finish()
                    }
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = getString(R.string.back)
                    )
                }
            }
        )
    }

    @Composable
    fun SettingsBody(padding: PaddingValues, state: SettingValueState<Boolean>) {
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                Spacer(Modifier.padding(top = 20.dp, bottom = 20.dp))
                Text(
                    text = stringResource(R.string.color_settings),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                SettingsSwitch(
                    title = { Text(text = stringResource(R.string.use_dynamic_colors)) },
                    subtitle = { Text(text = stringResource(R.string.change_the_theme_based_on_your_wallpaper)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ColorLens,
                            contentDescription = getString(R.string.use_dynamic_colors)
                        )
                    },
                    enabled = true,
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                )
            }
            item {
                HorizontalDivider()
            }
            item {
                Spacer(Modifier.padding(top = 20.dp, bottom = 20.dp))
                Text(
                    text = stringResource(R.string.account_settings),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                VerifyRow()
                ChangeNameSetting()
                ChangeEmailSetting()
                ChangePasswordSetting()
            }
            item {
                HorizontalDivider()
            }
            item {
                Spacer(Modifier.padding(top = 20.dp, bottom = 20.dp))
                Text(
                    text = stringResource(R.string.history_settings),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                SettingsMenuLink(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    icon = {
                        Icon(
                            Icons.Filled.ManageHistory,
                            contentDescription = stringResource(R.string.history_settings)
                        )
                    },
                    title = { Text(text = stringResource(R.string.manage_history)) }) {
                    isHistoryButtonSelected = true
                }
            }
            item {
                Spacer(Modifier.padding(top = 20.dp, bottom = 20.dp))
                Text(
                    text = stringResource(R.string.miscellaneous),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                ReviewSetting()
                EmailSetting()
            }
            item {
                HorizontalDivider()
            }
            item {
                Spacer(Modifier.padding(top = 20.dp, bottom = 20.dp))
                Text(
                    text = stringResource(R.string.delete_account),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                HorizontalDivider(Modifier.padding(start = 16.dp, end = 16.dp))
                DeleteAccountSetting()
            }
        }
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
    fun VerifyButton(state: Boolean) {
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
    fun ChangeNameDialog(onDismissRequest: () -> Unit) {
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
            onClick = {
                checkTokenStatus(
                    onTokenValid = {
                        option = PASSWORDCHANGE
                        isReAuthApproved = true
                    },
                    onTokenInvalid = {
                        option = PASSWORDCHANGE
                        isReAuthRequired = true
                    }
                )
            },
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
            onClick = {
                checkTokenStatus(
                    onTokenValid = {
                        option = EMAILCHANGE
                        isReAuthApproved = true
                    },
                    onTokenInvalid = {
                        option = EMAILCHANGE
                        isReAuthRequired = true
                    }
                )
            },
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
            onClick = {
                checkTokenStatus(
                    onTokenValid = {
                        option = DELETEACCOUNT
                        isReAuthApproved = true
                    },
                    onTokenInvalid = {
                        option = DELETEACCOUNT
                        isReAuthRequired = true
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
    }

    @Composable
    fun DeleteAccountDialog(onDismissRequest: () -> Unit) {
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
                    startActivity(intent)
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
    fun ChangePasswordDialog(onDismissRequest: () -> Unit) {
        if (showReLogin) {
            ReAuthDialog(onDismissRequest = {
                showReLogin = false
                val intent = Intent(
                    context,
                    LoginActivity::class.java
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
                startActivity(intent)
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
    fun ChangeEmailDialog(onDismissRequest: () -> Unit) {
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
    fun ReAuthDialog(onDismissRequest: () -> Unit) {
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
            if (userEmail != BLANK && password.isNotEmpty()) {
                FirebaseUtils.reAuthenticate(userEmail, password) { result ->
                    when (result) {
                        is FirebaseUtils.AuthResult.Success -> {
                            isReAuthApproved = true
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

    @Composable
    fun ReviewSetting() {
        SettingsMenuLink(
            title = { Text(text = stringResource(R.string.write_a_review)) },
            subtitle = { Text(text = stringResource(R.string.tell_us_what_you_think)) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Reviews,
                    contentDescription = getString(R.string.write_a_review)
                )
            },
            onClick = {
                openPlayStoreForRating(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
    }

    @Composable
    fun EmailSetting() {
        SettingsMenuLink(
            title = { Text(text = stringResource(R.string.contact_us)) },
            subtitle = { Text(text = stringResource(R.string.send_us_an_email)) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Mail,
                    contentDescription = getString(R.string.contact_us)
                )
            },
            onClick = {
                sendEmail(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
    }

    private fun openPlayStoreForRating(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(APPDIRECTLINK))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(PLAYSTOREURL)
            )
            context.startActivity(intent)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun sendEmail(context: Context) {
        val emailSubject = context.getString(R.string.email_title)
        val emailAddress = context.getString(R.string.dev_email_address)

        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(MAILTO)
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.there_is_no_email_client_installed),
                        duration = SnackbarDuration.Short,
                        withDismissAction = true
                    )
                }
            }
        } catch (e: ActivityNotFoundException) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.unable_to_open_email_app),
                    duration = SnackbarDuration.Short,
                    withDismissAction = true
                )
            }
        }
    }

    private fun checkTokenStatus(onTokenValid: () -> Unit, onTokenInvalid: () -> Unit) {
        var isTokenValid by mutableStateOf(false)
        val user = FirebaseAuth.getInstance().currentUser
        val tokenThreshold = 3600L

        user?.getIdToken(false)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val expiresIn = task.result?.expirationTimestamp ?: 0L
                val currentTimeMillis = System.currentTimeMillis()
                val currentTimeSeconds = currentTimeMillis / 1000
                val timeRemainingSeconds = expiresIn - currentTimeSeconds
                isTokenValid = timeRemainingSeconds < tokenThreshold

                when (isTokenValid) {
                    true -> onTokenValid()
                    false -> onTokenInvalid()
                }
            }
        }
    }

    private fun splitNames(input: String): Pair<String, String>? {
        val names = input.split(SPACE)

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

    companion object {
        private const val PLAYSTOREURL =
            "https://play.google.com/store/apps/details?id=com.sgtech.freevices"
        private const val APPDIRECTLINK = "market://details?id=com.sgtech.freevices"
        private const val MAILTO = "mailto:"
        private const val BLANK = ""
        private const val SPACE = " "
        private const val EMAILCHANGE = "emailchange"
        private const val PASSWORDCHANGE = "passwordchange"
        private const val NAMECHANGE = "namechange"
        private const val DELETEACCOUNT = "deleteaccount"
        private const val CANCELLED = -1
        private const val ZERO = 0
        private const val ONE = 1
        private const val TWO = 2
        private const val THREE = 3
        private const val FOUR = 4
        private const val FIVE = 5
        private const val SIX = 6
        private const val SEVEN = 7
        private const val TODAY = 1
        private const val LAST_WEEK = 7
        private const val TWO_WEEKS = 14
        private const val LAST_MONTH = 30
        private const val TWO_MONTHS = 60
        private const val THREE_MONTHS = 90
        private const val SIX_MONTHS = 180
        private const val ALL_TIME = 45000
    }
}