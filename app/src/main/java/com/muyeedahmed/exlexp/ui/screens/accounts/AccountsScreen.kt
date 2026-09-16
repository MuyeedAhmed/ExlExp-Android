package com.muyeedahmed.exlexp.ui.screens.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.ui.theme.BorderTable
import com.muyeedahmed.exlexp.ui.theme.HeaderRowBg
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate
import java.util.Locale
import kotlin.math.abs

@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel,
    onEditExpense: (Expense) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val checkingOnly = uiState.checkingAccounts
    val savingsOnly = uiState.savingsAccounts
    val brokerageAccounts = uiState.brokerageAccounts
    val hasBrokerage = brokerageAccounts.isNotEmpty()

    // Active sheet id
    val selectedAccountId = uiState.selectedTabId

    // Pagination for transactions
    var visibleCount by remember { mutableIntStateOf(25) }

    LaunchedEffect(selectedAccountId) {
        visibleCount = 25
    }

    val isBrokerage = selectedAccountId == "brokerage_portfolio"
    val activeAccount = if (isBrokerage) null else (checkingOnly + savingsOnly).find { it.id == selectedAccountId }
    val isSaving = activeAccount?.isSaving == true

    // Brokerage inline editing state
    var editingBrokerageId by remember { mutableStateOf<String?>(null) }
    var editingBrokerageValue by remember { mutableStateOf("") }

    // Delete confirmation state
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val tableHorizontalScrollState = rememberScrollState()
    val tableVerticalScrollState = rememberScrollState()

    if (checkingOnly.isEmpty() && savingsOnly.isEmpty() && !hasBrokerage) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🏦 No Bank Accounts Configured",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimarySlate,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add your Checking, Savings, or Brokerage accounts in Settings to track balances and transactions.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimarySlate)
                        .clickable { onNavigateToSettings() }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "➕ Add Bank Account",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Excel Sheet Style Tabs Bar
        val sheetTabsScroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .background(Color(0xFFF1F5F9))
                .border(width = 1.dp, color = BorderTable)
                .horizontalScroll(sheetTabsScroll)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Checking group
            if (checkingOnly.isNotEmpty()) {
                Text(
                    text = "CHECKING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)
                )
                checkingOnly.forEach { account ->
                    val isSelected = selectedAccountId == account.id
                    SheetTabButton(
                        text = account.name,
                        isSelected = isSelected,
                        onClick = { viewModel.selectTab(account.id) }
                    )
                }
            }

            // Savings group
            if (savingsOnly.isNotEmpty()) {
                if (checkingOnly.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 8.dp)
                            .width(1.dp)
                            .height(18.dp)
                            .background(BorderTable)
                    )
                }
                Text(
                    text = "SAVINGS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)
                )
                savingsOnly.forEach { account ->
                    val isSelected = selectedAccountId == account.id
                    SheetTabButton(
                        text = account.name,
                        isSelected = isSelected,
                        onClick = { viewModel.selectTab(account.id) }
                    )
                }
            }

            // Brokerage group
            if (hasBrokerage) {
                if (checkingOnly.isNotEmpty() || savingsOnly.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 8.dp)
                            .width(1.dp)
                            .height(18.dp)
                            .background(BorderTable)
                    )
                }
                Text(
                    text = "BROKERAGE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)
                )
                val isSelected = selectedAccountId == "brokerage_portfolio"
                SheetTabButton(
                    text = "Portfolio List",
                    isSelected = isSelected,
                    onClick = { viewModel.selectTab("brokerage_portfolio") }
                )
            }

            // Add Account Button
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimarySlate)
                    .clickable { onNavigateToSettings() }
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "➕ Add Account",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. Account Balance Banner
        if (activeAccount != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .border(width = 1.dp, color = BorderTable)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Account: ${activeAccount.name}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimarySlate
                )
                val bal = uiState.accountBalance
                val formattedBalance = if (bal >= 0) {
                    String.format(Locale.US, "$%,.2f", bal)
                } else {
                    String.format(Locale.US, "-$%,.2f", abs(bal))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Current Balance: ",
                        fontSize = 14.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = formattedBalance,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (bal >= 0) PositiveGreen else NegativeRed
                    )
                }
            }
        } else if (isBrokerage) {
            val totalBrokerage = uiState.brokerageBalances.values.sum()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .border(width = 1.dp, color = BorderTable)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Account: Brokerage Portfolio",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimarySlate
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Current Balance: ",
                        fontSize = 14.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = String.format(Locale.US, "$%,.2f", totalBrokerage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = PrimarySlate
                    )
                }
            }
        }

        // 3. Spreadsheet Table Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(tableHorizontalScrollState)
        ) {
            if (isBrokerage) {
                // ==================== BROKERAGE TABLE (550dp) ====================
                Column(
                    modifier = Modifier
                        .width(550.dp)
                        .verticalScroll(tableVerticalScrollState)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(Color(0xFFF1F5F9))
                            .border(width = 1.dp, color = BorderTable),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Account Name",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            modifier = Modifier
                                .width(250.dp)
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "Current Balance",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .width(150.dp)
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "Actions",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(150.dp)
                                .padding(horizontal = 8.dp)
                        )
                    }

                    // Data Rows
                    if (brokerageAccounts.isEmpty()) {
                        Text(
                            text = "No brokerage accounts configured.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    } else {
                        brokerageAccounts.forEach { item ->
                            val currentVal = uiState.brokerageBalances[item.id] ?: 0.0
                            val isEditing = editingBrokerageId == item.id

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .background(Color.White)
                                    .border(width = 0.5.dp, color = Color(0xFFE2E8F0)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Account Name
                                Text(
                                    text = item.name,
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .width(250.dp)
                                        .padding(horizontal = 8.dp)
                                )

                                // Current Balance
                                Box(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    if (isEditing) {
                                        BasicTextField(
                                            value = editingBrokerageValue,
                                            onValueChange = { editingBrokerageValue = it },
                                            singleLine = true,
                                            textStyle = TextStyle(
                                                fontSize = 13.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = PrimarySlate,
                                                textAlign = TextAlign.End
                                            ),
                                            cursorBrush = SolidColor(PrimarySlate),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Decimal,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onDone = {
                                                    val parsed = editingBrokerageValue.toDoubleOrNull()
                                                    if (parsed != null) {
                                                        viewModel.updateBrokerageBalance(item.id, parsed)
                                                    }
                                                    editingBrokerageId = null
                                                }
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFEFF6FF), RoundedCornerShape(4.dp))
                                                .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    } else {
                                        Text(
                                            text = String.format(Locale.US, "$%,.2f", currentVal),
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF334155),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }

                                // Actions
                                Row(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isEditing) {
                                        Text(
                                            text = "💾",
                                            fontSize = 14.sp,
                                            modifier = Modifier
                                                .clickable {
                                                    val parsed = editingBrokerageValue.toDoubleOrNull()
                                                    if (parsed != null) {
                                                        viewModel.updateBrokerageBalance(item.id, parsed)
                                                    }
                                                    editingBrokerageId = null
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                        Text(
                                            text = "❌",
                                            fontSize = 14.sp,
                                            modifier = Modifier
                                                .clickable { editingBrokerageId = null }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "✏️",
                                            fontSize = 14.sp,
                                            modifier = Modifier
                                                .clickable {
                                                    editingBrokerageId = item.id
                                                    editingBrokerageValue = String.format(Locale.US, "%.2f", currentVal)
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ==================== CHECKING (790dp) or SAVINGS (820dp) ====================
                val tableWidth = if (isSaving) 820.dp else 790.dp
                Column(
                    modifier = Modifier
                        .width(tableWidth)
                        .verticalScroll(tableVerticalScrollState)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(Color(0xFFF1F5F9))
                            .border(width = 1.dp, color = BorderTable),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            modifier = Modifier
                                .width(90.dp)
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "From/To",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            modifier = Modifier
                                .width(180.dp)
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "Amount",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .width(110.dp)
                                .padding(horizontal = 8.dp)
                        )
                        if (isSaving) {
                            Text(
                                text = "Interest",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .width(110.dp)
                                    .padding(horizontal = 8.dp)
                            )
                        }
                        Text(
                            text = "Details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            modifier = Modifier
                                .width(if (isSaving) 120.dp else 200.dp)
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            modifier = Modifier
                                .width(110.dp)
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "Actions",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(100.dp)
                                .padding(horizontal = 8.dp)
                        )
                    }

                    // Data Rows
                    if (uiState.accountExpenses.isEmpty()) {
                        Text(
                            text = "No transactions recorded.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    } else {
                        val displayedExpenses = uiState.accountExpenses.take(visibleCount)
                        displayedExpenses.forEach { item ->
                            val isDeposit = item.amount >= 0
                            val formattedAmount = if (isDeposit) {
                                String.format(Locale.US, "+$%,.2f", item.amount)
                            } else {
                                String.format(Locale.US, "-$%,.2f", abs(item.amount))
                            }
                            val dateShort = if (item.date.length >= 10) item.date.substring(5) else item.date
                            val fromToDisplay = item.fromTo?.ifEmpty { null }
                                ?: if (item.details?.startsWith("Zelle ") == true || item.description.startsWith("Zelle ")) "Zelle"
                                else item.description

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .background(Color.White)
                                    .border(width = 0.5.dp, color = Color(0xFFE2E8F0)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Date (MM-DD)
                                Text(
                                    text = dateShort,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF334155),
                                    modifier = Modifier
                                        .width(90.dp)
                                        .padding(horizontal = 8.dp)
                                )

                                // From/To
                                Text(
                                    text = fromToDisplay,
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .width(180.dp)
                                        .padding(horizontal = 8.dp)
                                )

                                if (isSaving) {
                                    // Savings Amount Column
                                    Text(
                                        text = if (item.isInterest) "-" else formattedAmount,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium,
                                        color = if (item.isInterest) Color(0xFF94A3B8) else if (isDeposit) PositiveGreen else PrimarySlate,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .width(110.dp)
                                            .padding(horizontal = 8.dp)
                                    )

                                    // Savings Interest Column
                                    Text(
                                        text = if (item.isInterest) formattedAmount else "-",
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium,
                                        color = if (item.isInterest) PositiveGreen else Color(0xFF94A3B8),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .width(110.dp)
                                            .padding(horizontal = 8.dp)
                                    )
                                } else {
                                    // Checking Amount Column
                                    Text(
                                        text = formattedAmount,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDeposit) PositiveGreen else PrimarySlate,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .width(110.dp)
                                            .padding(horizontal = 8.dp)
                                    )
                                }

                                // Details
                                Text(
                                    text = item.details ?: "",
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .width(if (isSaving) 120.dp else 200.dp)
                                        .padding(horizontal = 8.dp)
                                )

                                // Category
                                Text(
                                    text = item.category.ifEmpty { "Others" },
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .width(110.dp)
                                        .padding(horizontal = 8.dp)
                                )

                                // Actions (✏️ 🗑️)
                                Row(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✏️",
                                        fontSize = 13.sp,
                                        modifier = Modifier
                                            .clickable { onEditExpense(item) }
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                    Text(
                                        text = "🗑️",
                                        fontSize = 13.sp,
                                        modifier = Modifier
                                            .clickable { expenseToDelete = item }
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Load More row
                        if (uiState.accountExpenses.size > visibleCount) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC))
                                    .border(width = 0.5.dp, color = BorderTable)
                                    .clickable { visibleCount += 25 }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Show More (showing $visibleCount of ${uiState.accountExpenses.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF3B82F6)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (expenseToDelete != null) {
        val exp = expenseToDelete!!
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExpense(exp.id)
                        expenseToDelete = null
                    }
                ) {
                    Text("Delete", color = NegativeRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SheetTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(if (isSelected) Color.White else Color(0xFFE2E8F0))
            .border(
                width = 1.dp,
                color = BorderTable,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PrimarySlate else Color(0xFF475569)
        )
    }
    Spacer(modifier = Modifier.width(4.dp))
}

