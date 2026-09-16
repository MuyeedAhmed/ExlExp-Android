package com.muyeedahmed.exlexp.ui.screens.creditcards

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.ui.components.EditDateModal
import com.muyeedahmed.exlexp.ui.theme.BorderTable
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate
import java.util.Locale
import kotlin.math.abs

@Composable
fun CreditCardsScreen(
    viewModel: CreditCardsViewModel,
    onEditExpense: (Expense) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val isOverview = uiState.selectedTabId == "overview"
    val selectedCard = uiState.cards.find { it.id == uiState.selectedTabId }

    // Pagination for individual card sheet
    var visibleCount by remember { mutableIntStateOf(25) }

    LaunchedEffect(uiState.selectedTabId) {
        visibleCount = 25
    }

    var cardToEditOpenDate by remember { mutableStateOf<CreditCard?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val tableHorizontalScrollState = rememberScrollState()
    val tableVerticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Excel Sheet Tabs Bar
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
            Text(
                text = "Cards:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
            )

            // Overview Tab
            SheetTabButton(
                text = "📊 Overview",
                isSelected = isOverview,
                onClick = { viewModel.selectTab("overview") }
            )

            // Individual Card Tabs
            uiState.cards.forEach { card ->
                val isSelected = uiState.selectedTabId == card.id
                val tabText = if (card.isClosed) "${card.name} (Closed)" else card.name
                SheetTabButton(
                    text = tabText,
                    isSelected = isSelected,
                    isClosed = card.isClosed,
                    onClick = { viewModel.selectTab(card.id) }
                )
            }

            // Add Card Tab Button
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimarySlate)
                    .clickable { onNavigateToSettings() }
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "➕ Add Card",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (isOverview) {
            // ==================== 1. OVERVIEW PAGE ====================
            val overviewScroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(overviewScroll)
                    .padding(16.dp)
            ) {
                // KPI Grid (6 Cards, 2 per row)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Average Credit Age (Highlighted)
                        KpiCard(
                            label = "AVERAGE CREDIT AGE",
                            value = uiState.averageCreditAge,
                            sub = "Across ${uiState.totalOpenCards} open account${if (uiState.totalOpenCards != 1) "s" else ""} (excludes closed)",
                            valueColor = Color(0xFF15803D),
                            isHighlight = true,
                            modifier = Modifier.weight(1f)
                        )

                        // Total Balance Due
                        val debtDueColor = if (uiState.totalDebtDue > 0.005) NegativeRed else PositiveGreen
                        KpiCard(
                            label = "TOTAL BALANCE DUE",
                            value = String.format(Locale.US, "$%,.2f", uiState.totalDebtDue),
                            sub = "Across all cards",
                            valueColor = debtDueColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Total Spent
                        KpiCard(
                            label = "TOTAL SPENT",
                            value = String.format(Locale.US, "$%,.2f", uiState.totalLifetimeSpent),
                            sub = "Purchases & charges",
                            modifier = Modifier.weight(1f)
                        )

                        // Total Paid
                        KpiCard(
                            label = "TOTAL PAID",
                            value = String.format(Locale.US, "$%,.2f", uiState.totalPaymentsMade),
                            sub = "Payments & credits",
                            valueColor = PositiveGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Total Rewards
                        KpiCard(
                            label = "TOTAL REWARDS",
                            value = String.format(Locale.US, "$%,.2f", uiState.totalRewardsEarned),
                            sub = "Cashback & points",
                            valueColor = PositiveGreen,
                            modifier = Modifier.weight(1f)
                        )

                        // Annual Fees Paid
                        val feeColor = if (uiState.totalAnnualFeesPaid > 0) Color(0xFFD97706) else PrimarySlate
                        KpiCard(
                            label = "ANNUAL FEES PAID",
                            value = String.format(Locale.US, "$%,.2f", uiState.totalAnnualFeesPaid),
                            sub = "Total card fees",
                            valueColor = feeColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // All Credit Cards Summary Table Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderTable, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                ) {
                    // Header title area
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC))
                            .border(width = 1.dp, color = Color(0xFFE2E8F0))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "ALL CREDIT CARDS SUMMARY",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimarySlate,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Tap any card or opening date to edit. Tap \"View Sheet\" to see transactions.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Table Scroll
                    val summaryTableScroll = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(summaryTableScroll)
                    ) {
                        Column(modifier = Modifier.width(1030.dp)) {
                            // Table Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .background(Color(0xFFF1F5F9))
                                    .border(width = 1.dp, color = BorderTable),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Credit Card", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.width(160.dp).padding(horizontal = 8.dp))
                                Text("Opened Date", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.Center, modifier = Modifier.width(140.dp).padding(horizontal = 8.dp))
                                Text("Credit Age", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.width(110.dp).padding(horizontal = 8.dp))
                                Text("Total Spent", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.width(95.dp).padding(horizontal = 8.dp))
                                Text("Total Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.width(95.dp).padding(horizontal = 8.dp))
                                Text("Rewards", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.width(95.dp).padding(horizontal = 8.dp))
                                Text("Annual Fees", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.width(95.dp).padding(horizontal = 8.dp))
                                Text("Balance Due", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.width(110.dp).padding(horizontal = 8.dp))
                                Text("Actions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.Center, modifier = Modifier.width(130.dp).padding(horizontal = 8.dp))
                            }

                            // Table Rows
                            uiState.cardStatsList.forEach { stat ->
                                val card = stat.card
                                val closed = card.isClosed
                                val rowBg = if (closed) Color(0xFFF8FAFC) else Color.White

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .background(rowBg)
                                        .border(width = 0.5.dp, color = Color(0xFFE2E8F0)),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Card Name
                                    Row(
                                        modifier = Modifier
                                            .width(160.dp)
                                            .clickable { viewModel.selectTab(card.id) }
                                            .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = card.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (closed) Color(0xFF94A3B8) else PrimarySlate,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        if (closed) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFFEE2E2), RoundedCornerShape(3.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text("CLOSED", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = NegativeRed)
                                            }
                                        }
                                    }

                                    // Opened Date (Clickable to edit)
                                    Box(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .padding(horizontal = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                                                .clickable { cardToEditOpenDate = card }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${card.openDate} ✏️",
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.SemiBold,
                                                color = PrimarySlate
                                            )
                                        }
                                    }

                                    // Credit Age
                                    Column(
                                        modifier = Modifier
                                            .width(110.dp)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = stat.creditAge.formatted,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (closed) Color(0xFF94A3B8) else PrimarySlate
                                        )
                                        if (closed) {
                                            Text("excluded", fontSize = 9.sp, color = Color(0xFF94A3B8), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                        }
                                    }

                                    // Total Spent
                                    Text(
                                        text = String.format(Locale.US, "$%,.2f", stat.spent),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = PrimarySlate,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.width(95.dp).padding(horizontal = 8.dp)
                                    )

                                    // Total Paid
                                    Text(
                                        text = String.format(Locale.US, "$%,.2f", stat.paid),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = PositiveGreen,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.width(95.dp).padding(horizontal = 8.dp)
                                    )

                                    // Rewards
                                    Text(
                                        text = String.format(Locale.US, "$%,.2f", stat.rewards),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = PositiveGreen,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.width(95.dp).padding(horizontal = 8.dp)
                                    )

                                    // Annual Fees
                                    Text(
                                        text = String.format(Locale.US, "$%,.2f", stat.fees),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (stat.fees > 0) Color(0xFFD97706) else PrimarySlate,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.width(95.dp).padding(horizontal = 8.dp)
                                    )

                                    // Balance Due
                                    val dueColor = if (stat.balanceDue > 0.005) NegativeRed else PositiveGreen
                                    Text(
                                        text = String.format(Locale.US, "$%,.2f", stat.balanceDue),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = dueColor,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.width(110.dp).padding(horizontal = 8.dp)
                                    )

                                    // Action: View Sheet →
                                    Box(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .padding(horizontal = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(PrimarySlate)
                                                .clickable { viewModel.selectTab(card.id) }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "View Sheet →",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ==================== 2. INDIVIDUAL CARD SHEET ====================
            val stat = uiState.cardStatsList.find { it.card.id == selectedCard?.id }
            val balanceDue = stat?.balanceDue ?: 0.0

            // Header Banner
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
                    text = selectedCard?.name ?: "Credit Card",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimarySlate
                )

                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CURRENT BALANCE: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = String.format(Locale.US, "$%,.2f", balanceDue),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = if (balanceDue > 0.005) NegativeRed else PositiveGreen
                    )
                }
            }

            // Spreadsheet Grid (720dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .horizontalScroll(tableHorizontalScrollState)
            ) {
                Column(
                    modifier = Modifier
                        .width(720.dp)
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
                        Text("Date", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.width(90.dp).padding(horizontal = 8.dp))
                        Text("Description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.width(150.dp).padding(horizontal = 8.dp))
                        Text("Spend", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.width(90.dp).padding(horizontal = 8.dp))
                        Text("Paid", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.width(90.dp).padding(horizontal = 8.dp))
                        Text("Rewards", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.End, modifier = Modifier.width(90.dp).padding(horizontal = 8.dp))
                        Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.width(110.dp).padding(horizontal = 8.dp))
                        Text("Actions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.Center, modifier = Modifier.width(100.dp).padding(horizontal = 8.dp))
                    }

                    // Data Rows
                    if (uiState.selectedCardExpenses.isEmpty()) {
                        Text(
                            text = "No transactions recorded for this card.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    } else {
                        val displayed = uiState.selectedCardExpenses.take(visibleCount)
                        displayed.forEach { item ->
                            val amt = item.amount
                            val dateShort = if (item.date.length >= 10) item.date.substring(5) else item.date

                            val spendVal = if (item.isReward) "-" else if (amt > 0) String.format(Locale.US, "$%,.2f", amt) else "-"
                            val paidVal = if (item.isReward && amt < 0) String.format(Locale.US, "$%,.2f", abs(amt))
                            else if (amt < 0) String.format(Locale.US, "$%,.2f", abs(amt))
                            else "-"
                            val rewardsVal = if (item.isReward) String.format(Locale.US, "$%,.2f", item.rewardValue ?: abs(amt)) else "-"

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
                                    modifier = Modifier.width(90.dp).padding(horizontal = 8.dp)
                                )

                                // Description
                                Text(
                                    text = item.description,
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(150.dp).padding(horizontal = 8.dp)
                                )

                                // Spend (Standard Dark Slate, NOT Red!)
                                Text(
                                    text = spendVal,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF334155),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(90.dp).padding(horizontal = 8.dp)
                                )

                                // Paid (Green)
                                Text(
                                    text = paidVal,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (paidVal != "-") PositiveGreen else Color(0xFF334155),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(90.dp).padding(horizontal = 8.dp)
                                )

                                // Rewards (Green)
                                Text(
                                    text = rewardsVal,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (rewardsVal != "-") PositiveGreen else Color(0xFF334155),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(90.dp).padding(horizontal = 8.dp)
                                )

                                // Category
                                Text(
                                    text = item.category.ifEmpty { "Others" },
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(110.dp).padding(horizontal = 8.dp)
                                )

                                // Actions (✏️ 🗑️)
                                Row(
                                    modifier = Modifier.width(100.dp).padding(horizontal = 8.dp),
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
                        if (uiState.selectedCardExpenses.size > visibleCount) {
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
                                    text = "Show More (showing $visibleCount of ${uiState.selectedCardExpenses.size})",
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

    // Modal for Editing Card Open Date
    if (cardToEditOpenDate != null) {
        val card = cardToEditOpenDate!!
        EditDateModal(
            title = "Edit Opening Date: ${card.name}",
            initialDate = card.openDate,
            onDismiss = { cardToEditOpenDate = null },
            onConfirm = { newDate ->
                viewModel.updateCardOpenDate(card.id, newDate)
                cardToEditOpenDate = null
            }
        )
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
private fun KpiCard(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
    valueColor: Color = PrimarySlate,
    isHighlight: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHighlight) Color(0xFFF0FDF4) else Color(0xFFF8FAFC))
            .border(
                1.dp,
                if (isHighlight) Color(0xFF86EFAC) else Color(0xFFE2E8F0),
                RoundedCornerShape(6.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = if (isHighlight) 20.sp else 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun SheetTabButton(
    text: String,
    isSelected: Boolean,
    isClosed: Boolean = false,
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
            fontStyle = if (isClosed) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
            color = if (isSelected) PrimarySlate else if (isClosed) Color(0xFF94A3B8) else Color(0xFF475569)
        )
    }
    Spacer(modifier = Modifier.width(4.dp))
}

