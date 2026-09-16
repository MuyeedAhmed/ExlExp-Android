package com.muyeedahmed.exlexp.ui.screens.logexpense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import kotlin.math.abs

val STANDARD_CATEGORIES = listOf(
    "Rent", "Utilities", "Car Payment", "Transportation", "Grocery",
    "Eating Out", "Necessary Purchases", "Luxary Purchases", "Others",
    "Salary", "Transfer"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogExpenseModal(
    viewModel: LogExpenseViewModel,
    existingExpense: Expense? = null,
    onDismiss: () -> Unit
) {
    val cards by viewModel.nonHiddenCards.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(if (existingExpense?.isTransfer == true) ModalMode.TRANSFER else ModalMode.TRANSACTION) }
    var stayOnPage by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }

    // Form states
    var selectedCardId by remember {
        mutableStateOf(existingExpense?.creditCardId ?: "")
    }

    LaunchedEffect(cards) {
        if (selectedCardId.isEmpty() && cards.isNotEmpty()) {
            selectedCardId = cards.firstOrNull { !it.isBrokerage }?.id ?: cards.first().id
        }
    }

    var amountText by remember {
        mutableStateOf(existingExpense?.let { String.format(Locale.US, "%.2f", abs(it.amount)) } ?: "")
    }
    var dateText by remember {
        mutableStateOf(existingExpense?.date ?: LocalDate.now().toString())
    }
    var descriptionText by remember {
        mutableStateOf(existingExpense?.description ?: "")
    }
    var categoryText by remember {
        mutableStateOf(existingExpense?.category ?: "Others")
    }

    // Checking/Savings specifics
    var fromToText by remember {
        mutableStateOf(existingExpense?.fromTo ?: "")
    }
    var detailsText by remember {
        mutableStateOf(existingExpense?.details ?: "")
    }
    var isDepositInflow by remember {
        mutableStateOf((existingExpense?.amount ?: -1.0) >= 0)
    }
    var isZelle by remember {
        mutableStateOf(existingExpense?.fromTo?.equals("Zelle", ignoreCase = true) == true)
    }
    var zelleDirection by remember { mutableStateOf("To") }
    var zelleName by remember { mutableStateOf("") }
    var zelleDetails by remember { mutableStateOf("") }
    var isInterest by remember { mutableStateOf(existingExpense?.isInterest == true) }

    // Credit Card specifics
    var isFee by remember { mutableStateOf(existingExpense?.isFee == true) }
    var isReward by remember { mutableStateOf(existingExpense?.isReward == true) }
    var rewardValueText by remember {
        mutableStateOf(existingExpense?.rewardValue?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }

    // Transfer specifics
    var sourceCardId by remember {
        mutableStateOf(cards.firstOrNull { it.accountType.isDeposit }?.id ?: "")
    }
    var targetCardId by remember {
        mutableStateOf(cards.firstOrNull { it.id != sourceCardId }?.id ?: "")
    }
    var isCcBillPay by remember {
        mutableStateOf(existingExpense?.details?.startsWith("Credit Card Bill Pay") == true)
    }
    var transferMemo by remember {
        mutableStateOf(existingExpense?.details?.replaceFirst("Credit Card Bill Pay - [^:]+:?\\s*", "") ?: "")
    }

    LaunchedEffect(cards) {
        if (sourceCardId.isEmpty() && cards.isNotEmpty()) {
            sourceCardId = cards.firstOrNull { it.accountType.isDeposit }?.id ?: cards.first().id
            targetCardId = cards.firstOrNull { it.id != sourceCardId }?.id ?: cards.first().id
        }
    }

    val selectedCard = cards.find { it.id == selectedCardId }
    val isDeposit = selectedCard?.accountType?.isDeposit ?: false
    val isSavings = selectedCard?.isSaving == true

    var categoryExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (existingExpense != null) "Edit Entry" else "New Entry",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (existingExpense != null) "Update transaction details" else "Record an expense, income, or account transfer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Success Toast Banner
            AnimatedVisibility(
                visible = showToast,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Entry logged successfully!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mode Selection: Transaction vs Transfer (Only in Create Mode)
            if (existingExpense == null) {
                PrimaryTabRow(
                    selectedTabIndex = if (mode == ModalMode.TRANSACTION) 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = mode == ModalMode.TRANSACTION,
                        onClick = { mode = ModalMode.TRANSACTION },
                        text = { Text("Transaction", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = mode == ModalMode.TRANSFER,
                        onClick = { mode = ModalMode.TRANSFER },
                        text = { Text("Transfer", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Date Input & Quick Presets
            Text(
                text = "DATE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                singleLine = true,
                placeholder = { Text("YYYY-MM-DD") },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (mode == ModalMode.TRANSACTION) {
                // Account Selector Chips
                Text(
                    text = "PAYMENT ACCOUNT / CARD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                val chipScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(chipScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cards.forEach { card ->
                        val isSelected = card.id == selectedCardId
                        val icon = when {
                            card.isChecking -> Icons.Default.AccountBalance
                            card.isSaving -> Icons.Default.Savings
                            card.isBrokerage -> Icons.Default.TrendingUp
                            else -> Icons.Default.CreditCard
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCardId = card.id },
                            label = { Text(card.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Input
                Text(
                    text = "AMOUNT ($)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    placeholder = { Text("0.00") },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (isDeposit) {
                    // Deposit / Withdrawal Flow Toggle
                    Text(
                        text = "CASH FLOW DIRECTION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !isDepositInflow,
                            onClick = { isDepositInflow = false },
                            label = { Text("Money Out (Debit / Expense)", fontWeight = if (!isDepositInflow) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = { Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = isDepositInflow,
                            onClick = { isDepositInflow = true },
                            label = { Text("Money In (Deposit / Income)", fontWeight = if (isDepositInflow) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = { Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Savings Interest Checkbox
                    if (isSavings) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isInterest,
                                onCheckedChange = {
                                    isInterest = it
                                    if (it) {
                                        isDepositInflow = true
                                        categoryText = "Interest"
                                        fromToText = "Interest"
                                        detailsText = "Savings Interest"
                                    }
                                }
                            )
                            Text("HYSA Interest Earned", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    if (!isInterest) {
                        // Zelle Checkbox
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isZelle,
                                onCheckedChange = { isZelle = it }
                            )
                            Text("Zelle Transaction", style = MaterialTheme.typography.bodyMedium)
                        }

                        if (isZelle) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    OutlinedTextField(
                                        value = zelleName,
                                        onValueChange = { zelleName = it },
                                        label = { Text("Recipient / Sender Name") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = zelleDetails,
                                        onValueChange = { zelleDetails = it },
                                        label = { Text("Zelle Memo / Notes (Optional)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        } else {
                            OutlinedTextField(
                                value = fromToText,
                                onValueChange = { fromToText = it },
                                label = { Text("From / To (Merchant, Employer, Person)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = detailsText,
                                onValueChange = { detailsText = it },
                                label = { Text("Details / Memo (Optional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    // Credit Card Spends
                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text("Description / Merchant Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isReward,
                                onCheckedChange = {
                                    isReward = it
                                    if (it) {
                                        categoryText = "Reward"
                                        isFee = false
                                    }
                                }
                            )
                            Text("Reward / Credit", style = MaterialTheme.typography.bodyMedium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isFee,
                                onCheckedChange = {
                                    isFee = it
                                    if (it) {
                                        categoryText = "Fee"
                                        isReward = false
                                    }
                                }
                            )
                            Text("Annual Fee", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    if (isReward) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = rewardValueText,
                            onValueChange = { rewardValueText = it },
                            label = { Text("Reward Value ($)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Selector
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categoryText,
                        onValueChange = { categoryText = it },
                        readOnly = false,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        STANDARD_CATEGORIES.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoryText = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                // ==================== TRANSFER MODE ====================
                Text(
                    text = "SOURCE ACCOUNT (FROM)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                val sourceScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(sourceScroll),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cards.filter { it.accountType.isDeposit }.forEach { card ->
                        val isSelected = card.id == sourceCardId
                        val icon = if (card.isSaving) Icons.Default.Savings else Icons.Default.AccountBalance
                        FilterChip(
                            selected = isSelected,
                            onClick = { sourceCardId = card.id },
                            label = { Text(card.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "TARGET ACCOUNT (TO)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                val targetScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(targetScroll),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cards.filter { it.id != sourceCardId }.forEach { card ->
                        val isSelected = card.id == targetCardId
                        val icon = when {
                            card.isChecking -> Icons.Default.AccountBalance
                            card.isSaving -> Icons.Default.Savings
                            card.isBrokerage -> Icons.Default.TrendingUp
                            else -> Icons.Default.CreditCard
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                targetCardId = card.id
                                if (!card.accountType.isDeposit) {
                                    isCcBillPay = true
                                }
                            },
                            label = { Text(card.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "TRANSFER AMOUNT ($)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    placeholder = { Text("0.00") },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCcBillPay, onCheckedChange = { isCcBillPay = it })
                    Text("Credit Card Bill Payment", style = MaterialTheme.typography.bodyMedium)
                }

                OutlinedTextField(
                    value = transferMemo,
                    onValueChange = { transferMemo = it },
                    label = { Text("Transfer Memo / Notes (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stay on page checkbox (if not editing)
            if (existingExpense == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stay on sheet to log multiple entries",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(checked = stayOnPage, onCheckedChange = { stayOnPage = it })
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Submit Button
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: return@Button

                    if (mode == ModalMode.TRANSACTION) {
                        val card = cards.find { it.id == selectedCardId } ?: return@Button

                        if (isZelle) {
                            viewModel.logZelleExpense(
                                card = card,
                                direction = if (isDepositInflow) "From" else "To",
                                name = zelleName,
                                memo = zelleDetails,
                                amount = amt,
                                date = dateText,
                                category = categoryText,
                                editId = existingExpense?.id
                            )
                        } else {
                            val finalFromTo = if (isDeposit) fromToText.ifBlank { null } else null
                            val finalDetails = if (isDeposit) detailsText.ifBlank { null } else null
                            val rVal = rewardValueText.toDoubleOrNull()

                            viewModel.logStandardExpense(
                                card = card,
                                description = descriptionText.ifBlank { if (isFee) "Annual Fee" else if (isDeposit) fromToText else "Expense" },
                                amount = amt,
                                date = dateText,
                                category = categoryText,
                                fromTo = finalFromTo,
                                details = finalDetails,
                                isFee = isFee,
                                isReward = isReward,
                                rewardValue = rVal,
                                isInterest = isInterest,
                                isDepositInflow = isDepositInflow,
                                editId = existingExpense?.id
                            )
                        }
                    } else {
                        val source = cards.find { it.id == sourceCardId } ?: return@Button
                        val target = cards.find { it.id == targetCardId } ?: return@Button

                        viewModel.logTransfer(
                            sourceCard = source,
                            targetCard = target,
                            amount = amt,
                            date = dateText,
                            memo = transferMemo,
                            isCcBillPay = isCcBillPay,
                            oldTransferLinkId = existingExpense?.transferLinkId
                        )
                    }

                    if (!stayOnPage) {
                        onDismiss()
                    } else {
                        showToast = true
                        amountText = ""
                        descriptionText = ""
                        fromToText = ""
                        detailsText = ""
                        zelleName = ""
                        zelleDetails = ""
                        rewardValueText = ""
                        transferMemo = ""
                        coroutineScope.launch {
                            delay(2000)
                            showToast = false
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                val buttonIcon = if (existingExpense != null) Icons.Default.CheckCircle else if (mode == ModalMode.TRANSACTION) Icons.Default.Add else Icons.Default.SwapHoriz
                Icon(buttonIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (existingExpense != null) "Update Entry" else if (mode == ModalMode.TRANSACTION) "Log Transaction" else "Execute Transfer",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
