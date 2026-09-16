package com.muyeedahmed.exlexp.ui.screens.logexpense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.ui.theme.BorderTable
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
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
        mutableStateOf(existingExpense?.let { String.format(java.util.Locale.US, "%.2f", abs(it.amount)) } ?: "")
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
        mutableStateOf(existingExpense?.rewardValue?.let { String.format(java.util.Locale.US, "%.2f", it) } ?: "")
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
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingExpense != null) "Edit Item" else "Log Expense",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimarySlate
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = PrimarySlate)
                }
            }

            // Success Toast banner
            AnimatedVisibility(
                visible = showToast,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(Color(0xFFDCFCE7), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✅ Entry logged successfully!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mode Toggle: Transaction vs Transfer (if not editing)
            if (existingExpense == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (mode == ModalMode.TRANSACTION) PrimarySlate else Color.Transparent)
                            .clickable { mode = ModalMode.TRANSACTION }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📝 Transaction",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mode == ModalMode.TRANSACTION) Color.White else Color(0xFF475569)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (mode == ModalMode.TRANSFER) PrimarySlate else Color.Transparent)
                            .clickable { mode = ModalMode.TRANSFER }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔄 Transfer",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mode == ModalMode.TRANSFER) Color.White else Color(0xFF475569)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Date Input with Quick Buttons
            Text("Date", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = PrimarySlate
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(
                    onClick = { dateText = LocalDate.now().toString() },
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                        .border(1.dp, BorderTable, RoundedCornerShape(6.dp))
                ) {
                    Text("Today", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimarySlate)
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { dateText = LocalDate.now().minusDays(1).toString() },
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                        .border(1.dp, BorderTable, RoundedCornerShape(6.dp))
                ) {
                    Text("Yesterday", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimarySlate)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (mode == ModalMode.TRANSACTION) {
                // Account Selector Chips with visual icons
                Text("Select Payment Account / Card", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(6.dp))
                val chipScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(chipScrollState),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cards.forEach { card ->
                        val isSelected = card.id == selectedCardId
                        val prefix = when {
                            card.isChecking -> "🏛️ "
                            card.isSaving -> "💰 "
                            card.isBrokerage -> "📈 "
                            else -> "💳 "
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimarySlate else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    if (isSelected) PrimarySlate else BorderTable,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedCardId = card.id }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "$prefix${card.name}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else PrimarySlate
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Input
                Text("Amount ($)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        color = PrimarySlate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isDeposit) {
                    // Deposit / Withdrawal Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Direction:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimarySlate)
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (!isDepositInflow) PrimarySlate else Color.Transparent)
                                    .clickable { isDepositInflow = false }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "To (Money Out)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isDepositInflow) Color.White else Color(0xFF475569)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isDepositInflow) PositiveGreen else Color.Transparent)
                                    .clickable { isDepositInflow = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "From (Money In)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDepositInflow) Color.White else Color(0xFF475569)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                            Text("HYSA Interest Earned", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    if (!isInterest) {
                        // Zelle Checkbox
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isZelle,
                                onCheckedChange = { isZelle = it }
                            )
                            Text("Zelle Transaction", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        if (isZelle) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                                    .border(1.dp, BorderTable, RoundedCornerShape(6.dp))
                                    .padding(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = zelleName,
                                    onValueChange = { zelleName = it },
                                    label = { Text("Zelle Recipient / Sender Name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = zelleDetails,
                                    onValueChange = { zelleDetails = it },
                                    label = { Text("Zelle Memo / Notes (Optional)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                            Text("Reward / Credit", fontSize = 12.sp)
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
                            Text("Annual Fee", fontSize = 12.sp)
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
                Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
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
                Text("Source Account (Where money comes from)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(6.dp))
                val sourceScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(sourceScroll),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cards.filter { it.accountType.isDeposit }.forEach { card ->
                        val isSelected = card.id == sourceCardId
                        val prefix = when {
                            card.isChecking -> "🏛️ "
                            card.isSaving -> "💰 "
                            card.isBrokerage -> "📈 "
                            else -> "💳 "
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimarySlate else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    if (isSelected) PrimarySlate else BorderTable,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { sourceCardId = card.id }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "$prefix${card.name}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else PrimarySlate
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Target Account (Where money goes to)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(6.dp))
                val targetScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(targetScroll),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cards.filter { it.id != sourceCardId }.forEach { card ->
                        val isSelected = card.id == targetCardId
                        val prefix = when {
                            card.isChecking -> "🏛️ "
                            card.isSaving -> "💰 "
                            card.isBrokerage -> "📈 "
                            else -> "💳 "
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimarySlate else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    if (isSelected) PrimarySlate else BorderTable,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    targetCardId = card.id
                                    if (!card.accountType.isDeposit) {
                                        isCcBillPay = true
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "$prefix${card.name}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else PrimarySlate
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Transfer Amount ($)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        color = PrimarySlate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCcBillPay, onCheckedChange = { isCcBillPay = it })
                    Text("Credit Card Bill Payment", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                OutlinedTextField(
                    value = transferMemo,
                    onValueChange = { transferMemo = it },
                    label = { Text("Transfer Memo / Notes (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stay on page checkbox (if not editing)
            if (existingExpense == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stay on page to log multiple entries", fontSize = 13.sp, color = Color(0xFF64748B))
                    Switch(checked = stayOnPage, onCheckedChange = { stayOnPage = it })
                }
                Spacer(modifier = Modifier.height(14.dp))
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
                colors = ButtonDefaults.buttonColors(containerColor = PrimarySlate),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (existingExpense != null) "Update Item" else if (mode == ModalMode.TRANSACTION) "➕ Log Transaction" else "🔄 Execute Transfer",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

