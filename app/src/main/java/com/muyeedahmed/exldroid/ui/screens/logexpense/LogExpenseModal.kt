package com.muyeedahmed.exldroid.ui.screens.logexpense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.style.TextAlign
import com.muyeedahmed.exldroid.ui.components.AppDatePickerDialog
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
import com.muyeedahmed.exldroid.domain.model.Expense
import com.muyeedahmed.exldroid.ui.theme.PositiveGreen
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

private fun formatDisplayDate(isoDate: String): String {
    return try {
        val parts = isoDate.trim().split("-")
        if (parts.size == 3) "${parts[1]}/${parts[2]}/${parts[0]}" else isoDate
    } catch (_: Exception) {
        isoDate
    }
}

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
    var showDatePicker by remember { mutableStateOf(false) }
    var accountDropdownExpanded by remember { mutableStateOf(false) }
    var sourceAccountDropdownExpanded by remember { mutableStateOf(false) }
    var targetAccountDropdownExpanded by remember { mutableStateOf(false) }

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

            // Mode Selection: Transaction vs Transfer (Segmented Pill from Screenshot)
            if (existingExpense == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(
                                    color = if (mode == ModalMode.TRANSACTION) Color(0xFF111827) else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable { mode = ModalMode.TRANSACTION },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TRANSACTION",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp,
                                color = if (mode == ModalMode.TRANSACTION) Color.White else Color(0xFF64748B)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(
                                    color = if (mode == ModalMode.TRANSFER) Color(0xFF111827) else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable { mode = ModalMode.TRANSFER },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TRANSFER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp,
                                color = if (mode == ModalMode.TRANSFER) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (mode == ModalMode.TRANSACTION) {
                // Row 1: ACCOUNT & CATEGORY Side by Side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ACCOUNT Dropdown
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACCOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = accountDropdownExpanded,
                            onExpandedChange = { accountDropdownExpanded = !accountDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedCard?.name ?: "Cash / Checking",
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(20.dp)
                                                .background(Color(0xFF16A34A), RoundedCornerShape(2.dp))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val icon = when {
                                            selectedCard?.isChecking == true -> Icons.Default.AccountBalance
                                            selectedCard?.isSaving == true -> Icons.Default.Savings
                                            selectedCard?.isBrokerage == true -> Icons.AutoMirrored.Filled.TrendingUp
                                            else -> Icons.Default.CreditCard
                                        }
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF16A34A)
                                        )
                                    }
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF94A3B8),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = accountDropdownExpanded,
                                onDismissRequest = { accountDropdownExpanded = false }
                            ) {
                                cards.forEach { card ->
                                    val icon = when {
                                        card.isChecking -> Icons.Default.AccountBalance
                                        card.isSaving -> Icons.Default.Savings
                                        card.isBrokerage -> Icons.AutoMirrored.Filled.TrendingUp
                                        else -> Icons.Default.CreditCard
                                    }
                                    DropdownMenuItem(
                                        text = { Text(card.name, fontSize = 14.sp) },
                                        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                        onClick = {
                                            selectedCardId = card.id
                                            accountDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // CATEGORY Dropdown
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CATEGORY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = categoryText,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF94A3B8),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
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
                                        text = { Text(cat, fontSize = 14.sp) },
                                        onClick = {
                                            categoryText = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Row 2: FROM / TO NAME (Full width)
                Text(
                    text = if (isDeposit) "FROM / TO NAME" else "DESCRIPTION / MERCHANT NAME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = if (isDeposit) fromToText else descriptionText,
                    onValueChange = {
                        if (isDeposit) fromToText = it else descriptionText = it
                    },
                    placeholder = {
                        Text(
                            text = if (isDeposit) "e.g. Landlord, Employer, John Doe" else "e.g. Trader Joe's, Netflix, Amazon",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF94A3B8),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Row 3: AMOUNT & DATE Side by Side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // AMOUNT
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AMOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            placeholder = {
                                Text(
                                    text = "0.00",
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.fillMaxWidth(),
                                    fontSize = 14.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF94A3B8),
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // DATE
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DATE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formatDisplayDate(dateText),
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Select Date",
                                        tint = Color(0xFF0F172A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF94A3B8),
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showDatePicker = true }
                            )
                        }
                    }
                }

                // Row 4: Flow Direction Buttons [ From ] and [ To ] (for Deposit accounts)
                if (isDeposit) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isFromSelected = isDepositInflow
                        val isToSelected = !isDepositInflow

                        // FROM button (Income / Deposit / Inflow)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(
                                    color = if (isFromSelected) Color(0xFFFFEDD5) else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = if (isFromSelected) 1.5.dp else 1.dp,
                                    color = if (isFromSelected) Color(0xFFF97316) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { isDepositInflow = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "From",
                                fontWeight = if (isFromSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isFromSelected) Color(0xFF9A3412) else Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        }

                        // TO button (Expense / Debit / Outflow)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(
                                    color = if (isToSelected) Color(0xFFFFEDD5) else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = if (isToSelected) 1.5.dp else 1.dp,
                                    color = if (isToSelected) Color(0xFFF97316) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { isDepositInflow = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "To",
                                fontWeight = if (isToSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isToSelected) Color(0xFF9A3412) else Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Credit Card Reward / Fee toggles
                    Spacer(modifier = Modifier.height(10.dp))
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
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Row 5: DETAILS with ZELLE Button
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DETAILS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    if (isDeposit) {
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = if (isZelle) Color(0xFF7C3AED) else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                    color = if (isZelle) Color(0xFFEDE9FE) else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable { isZelle = !isZelle }
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ZELLE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isZelle) Color(0xFF7C3AED) else Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = detailsText,
                    onValueChange = {
                        detailsText = it
                        if (isZelle) zelleDetails = it
                    },
                    placeholder = {
                        Text(
                            text = "e.g. Monthly rent, utility bill payment",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    },
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF94A3B8),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // ==================== TRANSFER MODE ====================
                val sourceCard = cards.find { it.id == sourceCardId }
                val targetCard = cards.find { it.id == targetCardId }

                // Row 1: FROM ACCOUNT & TO ACCOUNT Side by Side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SOURCE ACCOUNT
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FROM ACCOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = sourceAccountDropdownExpanded,
                            onExpandedChange = { sourceAccountDropdownExpanded = !sourceAccountDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = sourceCard?.name ?: "Select Source",
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = {
                                    val icon = if (sourceCard?.isSaving == true) Icons.Default.Savings else Icons.Default.AccountBalance
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF16A34A))
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(22.dp))
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF94A3B8),
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = sourceAccountDropdownExpanded,
                                onDismissRequest = { sourceAccountDropdownExpanded = false }
                            ) {
                                cards.filter { it.accountType.isDeposit }.forEach { card ->
                                    val icon = if (card.isSaving) Icons.Default.Savings else Icons.Default.AccountBalance
                                    DropdownMenuItem(
                                        text = { Text(card.name, fontSize = 14.sp) },
                                        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                        onClick = {
                                            sourceCardId = card.id
                                            sourceAccountDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // TARGET ACCOUNT
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TO ACCOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = targetAccountDropdownExpanded,
                            onExpandedChange = { targetAccountDropdownExpanded = !targetAccountDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = targetCard?.name ?: "Select Target",
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = {
                                    val icon = when {
                                        targetCard?.isChecking == true -> Icons.Default.AccountBalance
                                        targetCard?.isSaving == true -> Icons.Default.Savings
                                        targetCard?.isBrokerage == true -> Icons.AutoMirrored.Filled.TrendingUp
                                        else -> Icons.Default.CreditCard
                                    }
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(22.dp))
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF94A3B8),
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = targetAccountDropdownExpanded,
                                onDismissRequest = { targetAccountDropdownExpanded = false }
                            ) {
                                cards.filter { it.id != sourceCardId }.forEach { card ->
                                    val icon = when {
                                        card.isChecking -> Icons.Default.AccountBalance
                                        card.isSaving -> Icons.Default.Savings
                                        card.isBrokerage -> Icons.AutoMirrored.Filled.TrendingUp
                                        else -> Icons.Default.CreditCard
                                    }
                                    DropdownMenuItem(
                                        text = { Text(card.name, fontSize = 14.sp) },
                                        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                        onClick = {
                                            targetCardId = card.id
                                            if (!card.accountType.isDeposit) {
                                                isCcBillPay = true
                                            }
                                            targetAccountDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Row 2: AMOUNT & DATE Side by Side
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // AMOUNT
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AMOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            placeholder = {
                                Text(
                                    text = "0.00",
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.fillMaxWidth(),
                                    fontSize = 14.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF94A3B8),
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // DATE
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DATE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formatDisplayDate(dateText),
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Select Date",
                                        tint = Color(0xFF0F172A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF94A3B8),
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showDatePicker = true }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCcBillPay, onCheckedChange = { isCcBillPay = it })
                    Text("Credit Card Bill Payment", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "DETAILS / MEMO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = transferMemo,
                    onValueChange = { transferMemo = it },
                    placeholder = {
                        Text("e.g. Savings transfer, payment memo", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF94A3B8),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Row 6: Create Multiple Logs Checkbox (Centered from Screenshot)
            if (existingExpense == null) {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = stayOnPage,
                        onCheckedChange = { stayOnPage = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Create multiple logs (stay on this page)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }
            }

            // Row 7: Dark + ADD Button from Screenshot
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: return@Button

                    if (mode == ModalMode.TRANSACTION) {
                        val card = cards.find { it.id == selectedCardId } ?: return@Button

                        if (isZelle) {
                            viewModel.logZelleExpense(
                                card = card,
                                direction = if (isDepositInflow) "From" else "To",
                                name = if (isDeposit) fromToText.ifBlank { "Zelle Contact" } else descriptionText.ifBlank { "Zelle Contact" },
                                memo = detailsText,
                                amount = amt,
                                date = dateText,
                                category = categoryText,
                                editId = existingExpense?.id
                            )
                        } else {
                            val finalFromTo = if (isDeposit) fromToText.ifBlank { null } else null
                            val finalDetails = detailsText.ifBlank { null }
                            val rVal = rewardValueText.toDoubleOrNull()

                            viewModel.logStandardExpense(
                                card = card,
                                description = if (isDeposit) fromToText.ifBlank { if (isDepositInflow) "Money In" else "Money Out" } else descriptionText.ifBlank { if (isFee) "Annual Fee" else "Expense" },
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
                            memo = transferMemo.ifBlank { detailsText },
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
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111827),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (existingExpense != null) "UPDATE" else "ADD",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 15.sp
                )
            }
        }
    }
}
