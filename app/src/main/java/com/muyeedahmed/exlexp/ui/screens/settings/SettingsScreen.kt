package com.muyeedahmed.exlexp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.ui.components.AccountBadge
import com.muyeedahmed.exlexp.ui.theme.BorderGray
import com.muyeedahmed.exlexp.ui.theme.ExcelGreen
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.NeutralGray
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddAccountDialog by remember { mutableStateOf(false) }
    var newAccountName by remember { mutableStateOf("") }
    var newAccountType by remember { mutableStateOf("Checking") }
    var newAccountDate by remember { mutableStateOf(LocalDate.now().toString()) }

    var showAuthDialog by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var authEmail by remember { mutableStateOf("") }
    var authPassword by remember { mutableStateOf("") }

    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    var cardToRename by remember { mutableStateOf<CreditCard?>(null) }
    var renameText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
                .padding(bottom = 70.dp)
        ) {
            // Section 1: Account Management
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACCOUNT MANAGEMENT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralGray
                        )
                        Button(
                            onClick = { showAddAccountDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ExcelGreen),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Account", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    uiState.cards.forEachIndexed { index, card ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AccountBadge(accountType = card.accountType)
                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = card.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (card.isHidden) NeutralGray else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Opened: ${card.openDate}",
                                    fontSize = 10.sp,
                                    color = NeutralGray
                                )
                            }

                            // Reorder Up/Down
                            IconButton(
                                onClick = { viewModel.moveAccount(card.id, moveUp = true) },
                                enabled = index > 0,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Up", modifier = Modifier.size(14.dp))
                            }
                            IconButton(
                                onClick = { viewModel.moveAccount(card.id, moveUp = false) },
                                enabled = index < uiState.cards.size - 1,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Down", modifier = Modifier.size(14.dp))
                            }

                            // Visibility Toggle
                            IconButton(
                                onClick = { viewModel.toggleVisibility(card) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = if (card.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Visibility",
                                    tint = NeutralGray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Rename
                            IconButton(
                                onClick = {
                                    cardToRename = card
                                    renameText = card.name
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Rename", tint = NeutralGray, modifier = Modifier.size(14.dp))
                            }

                            // Delete
                            IconButton(
                                onClick = { viewModel.deleteCard(card.id) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NegativeRed.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                            }
                        }
                        HorizontalDivider(color = BorderGray.copy(alpha = 0.5f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 2: Cloud Sync & User Management
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CLOUD SYNC & BACKUP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current User", fontSize = 11.sp, color = NeutralGray)
                            Text(
                                text = uiState.currentUsername,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.currentUsername == "local") NeutralGray else ExcelGreen
                            )
                        }

                        if (uiState.currentUsername == "local") {
                            OutlinedButton(
                                onClick = {
                                    isSignUpMode = false
                                    showAuthDialog = true
                                },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Sign In / Register", fontSize = 11.sp)
                            }
                        } else {
                            TextButton(onClick = { viewModel.logout() }) {
                                Text("Log Out", fontSize = 11.sp, color = NegativeRed)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = BorderGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Last Synced", fontSize = 11.sp, color = NeutralGray)
                            Text(text = uiState.lastSyncTime, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = { viewModel.syncNow() },
                            enabled = !uiState.isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = ExcelGreen),
                            modifier = Modifier.height(36.dp)
                        ) {
                            if (uiState.isSyncing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync Now", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Export JSON backup button
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                exportedJsonText = viewModel.exportJson()
                                showExportDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export All Data as Structured JSON")
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
                    Column {
                        OutlinedTextField(
                            value = newAccountName,
                            onValueChange = { newAccountName = it },
                            label = { Text("Account / Card Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

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

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newAccountDate,
                            onValueChange = { newAccountDate = it },
                            label = { Text("Open Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
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
                    Column {
                        OutlinedTextField(
                            value = authEmail,
                            onValueChange = { authEmail = it },
                            label = { Text("Email Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = authPassword,
                            onValueChange = { authPassword = it },
                            label = { Text("Password") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
                            Text(if (isSignUpMode) "Already have an account? Sign In" else "Don't have an account? Sign Up", fontSize = 11.sp)
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
                        Text("Copy your raw structured data:", fontSize = 12.sp, color = NeutralGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = exportedJsonText,
                            onValueChange = {},
                            readOnly = true,
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
    }
}
