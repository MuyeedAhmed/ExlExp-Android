package com.muyeedahmed.exlexp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import com.muyeedahmed.exlexp.ui.components.AppDatePickerDialog
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.ui.components.AccountBadge
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val importDialogState by viewModel.importDialogState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddAccountDialog by remember { mutableStateOf(false) }
    var newAccountName by remember { mutableStateOf("") }
    var newAccountType by remember { mutableStateOf("Checking") }
    var newAccountDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var showAccountDatePicker by remember { mutableStateOf(false) }

    var showAuthDialog by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var authEmail by remember { mutableStateOf("") }
    var authPassword by remember { mutableStateOf("") }

    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader -> reader.readText() }
                if (!content.isNullOrBlank()) {
                    importJsonText = content
                }
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to read file: ${e.message}")
                }
            }
        }
    }

    var cardToRename by remember { mutableStateOf<CreditCard?>(null) }
    var renameText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 70.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Page Title
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Manage accounts, cloud synchronization, and backup data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Section 1: Account Management
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Accounts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${uiState.cards.size} configured accounts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        FilledTonalButton(
                            onClick = { showAddAccountDialog = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Account", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.cards.forEachIndexed { index, card ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AccountBadge(accountType = card.accountType)
                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = card.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (card.isHidden) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Opened: ${card.openDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Reorder Up/Down
                            IconButton(
                                onClick = { viewModel.moveAccount(card.id, moveUp = true) },
                                enabled = index > 0,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = "Up",
                                    tint = if (index > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.moveAccount(card.id, moveUp = false) },
                                enabled = index < uiState.cards.size - 1,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = "Down",
                                    tint = if (index < uiState.cards.size - 1) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Visibility Toggle
                            IconButton(
                                onClick = { viewModel.toggleVisibility(card) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = if (card.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Visibility",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Rename
                            IconButton(
                                onClick = {
                                    cardToRename = card
                                    renameText = card.name
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Rename",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Delete
                            IconButton(
                                onClick = { viewModel.deleteCard(card.id) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = NegativeRed.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        if (index < uiState.cards.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }

            // Section 2: Cloud Sync & User Management
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cloud Sync & Backup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // User Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Current Account",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (uiState.currentUsername == "local") "Local Offline User" else uiState.currentUsername,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (uiState.currentUsername == "local") MaterialTheme.colorScheme.onSurfaceVariant else PositiveGreen
                                )
                            }
                        }

                        if (uiState.currentUsername == "local") {
                            FilledTonalButton(
                                onClick = {
                                    isSignUpMode = false
                                    showAuthDialog = true
                                }
                            ) {
                                Text("Sign In", style = MaterialTheme.typography.labelMedium)
                            }
                        } else {
                            TextButton(onClick = { viewModel.logout() }) {
                                Text("Log Out", style = MaterialTheme.typography.labelMedium, color = NegativeRed)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Sync Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Last Synced",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = uiState.lastSyncTime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.syncNow() },
                            enabled = !uiState.isSyncing
                        ) {
                            if (uiState.isSyncing) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Now", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Export JSON Backup Button
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                exportedJsonText = viewModel.exportJson()
                                showExportDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export All Data as Structured JSON")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Import JSON Backup Button
                    OutlinedButton(
                        onClick = {
                            importJsonText = ""
                            showImportDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Import Old Data (JSON)")
                    }
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))

        // Add Account Dialog
        if (showAddAccountDialog) {
            val accountTypes = listOf("Checking", "Savings", "Brokerage", "Credit Card")
            var typeExpanded by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showAddAccountDialog = false },
                title = { Text("Add New Account") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newAccountName,
                            onValueChange = { newAccountName = it },
                            label = { Text("Account / Card Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded }
                        ) {
                            OutlinedTextField(
                                value = newAccountType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Account Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                accountTypes.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(t) },
                                        onClick = {
                                            newAccountType = t
                                            typeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = newAccountDate,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Open Date") },
                                trailingIcon = {
                                    IconButton(onClick = { showAccountDatePicker = true }) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showAccountDatePicker = true }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newAccountName.isNotBlank()) {
                                viewModel.addAccount(newAccountName, newAccountType, newAccountDate)
                                newAccountName = ""
                                showAddAccountDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddAccountDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showAccountDatePicker) {
            AppDatePickerDialog(
                initialDate = newAccountDate,
                onDateSelected = { selectedDate ->
                    newAccountDate = selectedDate
                    showAccountDatePicker = false
                },
                onDismiss = { showAccountDatePicker = false }
            )
        }

        // Rename Card Dialog
        if (cardToRename != null) {
            AlertDialog(
                onDismissRequest = { cardToRename = null },
                title = { Text("Rename Account") },
                text = {
                    OutlinedTextField(
                        value = renameText,
                        onValueChange = { renameText = it },
                        label = { Text("New Account Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (renameText.isNotBlank() && cardToRename != null) {
                                viewModel.renameCard(cardToRename!!, renameText)
                                cardToRename = null
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { cardToRename = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Cloud Login / Register Dialog
        if (showAuthDialog) {
            AlertDialog(
                onDismissRequest = { showAuthDialog = false },
                title = { Text(if (isSignUpMode) "Register Cloud Account" else "Sign In with Cloud") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = authEmail,
                            onValueChange = { authEmail = it },
                            label = { Text("Email Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = authPassword,
                            onValueChange = { authPassword = it },
                            label = { Text("Password") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
                            Text(
                                if (isSignUpMode) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (authEmail.isNotBlank() && authPassword.isNotBlank()) {
                                if (isSignUpMode) {
                                    viewModel.signUp(authEmail, authPassword)
                                } else {
                                    viewModel.login(authEmail, authPassword)
                                }
                                showAuthDialog = false
                            }
                        }
                    ) {
                        Text(if (isSignUpMode) "Sign Up" else "Sign In")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAuthDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Export JSON Dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("JSON Data Backup") },
                text = {
                    Column {
                        Text(
                            text = "Copy your raw structured data:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = exportedJsonText,
                            onValueChange = {},
                            readOnly = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showExportDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Import JSON Dialog
        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Import Old Data") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Paste your exported JSON backup data below or pick a backup file from storage:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FilledTonalButton(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Backup File (*.json)")
                        }

                        OutlinedTextField(
                            value = importJsonText,
                            onValueChange = { importJsonText = it },
                            placeholder = { Text("Paste JSON backup content here...") },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (importJsonText.isNotBlank()) {
                                viewModel.importData(importJsonText)
                                showImportDialog = false
                            }
                        },
                        enabled = importJsonText.isNotBlank()
                    ) {
                        Text("Import Data")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        importDialogState?.let { dialog ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissImportDialog() },
                title = {
                    Text(
                        text = dialog.title,
                        fontWeight = FontWeight.Bold,
                        color = if (dialog.isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = dialog.message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.dismissImportDialog() }) {
                        Text("OK")
                    }
                },
                dismissButton = if (!dialog.isSuccess) {
                    {
                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clipboard?.setPrimaryClip(ClipData.newPlainText("Import Error", dialog.message))
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error details copied to clipboard")
                                }
                            }
                        ) {
                            Text("Copy Error")
                        }
                    }
                } else null
            )
        }
    }
}
