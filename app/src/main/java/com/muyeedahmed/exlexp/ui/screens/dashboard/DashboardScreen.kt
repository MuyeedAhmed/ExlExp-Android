package com.muyeedahmed.exlexp.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.ui.components.CategoryDonutChart
import com.muyeedahmed.exlexp.ui.components.TransactionRowItem
import com.muyeedahmed.exlexp.ui.components.TrendBarChart
import com.muyeedahmed.exlexp.ui.theme.BorderTable
import com.muyeedahmed.exlexp.ui.theme.DangerBg
import com.muyeedahmed.exlexp.ui.theme.DangerBorder
import com.muyeedahmed.exlexp.ui.theme.HeaderRowBg
import com.muyeedahmed.exlexp.ui.theme.LabelSlate
import com.muyeedahmed.exlexp.ui.theme.MonoFontFamily
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.NeutralGray
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate
import com.muyeedahmed.exlexp.ui.theme.RowAltBg
import com.muyeedahmed.exlexp.ui.theme.SubHeaderRowBg
import com.muyeedahmed.exlexp.ui.theme.TextSlate
import java.util.Locale
import kotlin.math.abs

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToAllTransactions: () -> Unit,
    onEditExpense: (Expense) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var futureDesc by remember { mutableStateOf("") }
    var futureAmount by remember { mutableStateOf("") }
    var futureDate by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 60.dp)
    ) {
        // =========================================================
        // 1. FINANCIAL SUMMARY (Spreadsheet Grid Style)
        // =========================================================
        Text(
            text = "Financial Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimarySlate,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Main Balances Grid Table
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                .background(Color.White)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderRowBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Account Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Balance Value",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            // Total Checking Balance Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Checking Balance",
                    fontSize = 13.sp,
                    color = TextSlate,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = String.format(Locale.US, "$%,.2f", uiState.summary.totalCheckingBalance),
                    fontSize = 13.sp,
                    fontFamily = MonoFontFamily,
                    color = PositiveGreen,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            // Total Credit Card Debt Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Credit Card Debt",
                    fontSize = 13.sp,
                    color = TextSlate,
                    modifier = Modifier.weight(2f)
                )
                val isDebtPositive = uiState.summary.totalCreditCardDebt > 0.005
                Text(
                    text = String.format(Locale.US, "$%,.2f", uiState.summary.totalCreditCardDebt),
                    fontSize = 13.sp,
                    fontFamily = MonoFontFamily,
                    color = if (isDebtPositive) NegativeRed else TextSlate,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            // Upcoming Scheduled Bills Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Scheduled Bills",
                    fontSize = 13.sp,
                    color = TextSlate,
                    modifier = Modifier.weight(2f)
                )
                val hasBills = uiState.summary.totalFutureExpenses > 0.005
                Text(
                    text = String.format(Locale.US, "$%,.2f", uiState.summary.totalFutureExpenses),
                    fontSize = 13.sp,
                    fontFamily = MonoFontFamily,
                    color = if (hasBills) NegativeRed else TextSlate,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            // Net Financial Position Row (Highlighted)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RowAltBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Financial Position",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimarySlate,
                    modifier = Modifier.weight(2f)
                )
                val netPos = uiState.summary.netBalance
                Text(
                    text = String.format(Locale.US, (if (netPos < -0.005) "-$%,.2f" else "$%,.2f"), abs(netPos)),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MonoFontFamily,
                    color = if (netPos < -0.005) NegativeRed else PrimarySlate,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =========================================================
        // 2. 12-MONTH SPENDING TREND (BAR CHART)
        // =========================================================
        TrendBarChart(
            spendingTrend = uiState.spendingTrend,
            selectedMonthKey = uiState.selectedMonthKey,
            onMonthSelected = { viewModel.selectMonth(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // =========================================================
        // 3. SPENDING DISTRIBUTION (DONUT WHEEL & BREAKDOWN)
        // =========================================================
        CategoryDonutChart(
            distribution = uiState.categoryDistribution,
            availableMonths = uiState.availableMonths,
            selectedMonthKey = uiState.selectedMonthKey,
            onMonthSelected = { viewModel.selectMonth(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // =========================================================
        // 4. RECENT TRANSACTIONS (Spreadsheet Grid Style)
        // =========================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                .background(Color.White)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderRowBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate
                )
            }

            if (uiState.recentTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions recorded yet.",
                        fontSize = 13.sp,
                        color = NeutralGray
                    )
                }
            } else {
                uiState.recentTransactions.forEach { item ->
                    TransactionRowItem(
                        transaction = item,
                        onEditClick = null,
                        onDeleteClick = null
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.5.dp)
                }
            }

            // Footer Button: Show All Transactions →
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RowAltBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .clickable { onNavigateToAllTransactions() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Show all transactions →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimarySlate
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =========================================================
        // 5. CHECKING ACCOUNTS REGISTRY (ACTIVE)
        // =========================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderRowBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Checking Accounts Registry (Active)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Current Balance",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.activeCheckingAccounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No checking accounts with active balance.",
                        fontSize = 13.sp,
                        color = NeutralGray
                    )
                }
            } else {
                uiState.activeCheckingAccounts.forEach { (account, bal) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 0.5.dp, color = BorderTable)
                            .padding(vertical = 6.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏛️ ${account.name}",
                            fontSize = 13.sp,
                            color = TextSlate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = String.format(Locale.US, "$%,.2f", bal),
                            fontSize = 13.sp,
                            fontFamily = MonoFontFamily,
                            color = if (bal >= 0.005) PositiveGreen else if (bal < -0.005) NegativeRed else TextSlate,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =========================================================
        // 6. CREDIT CARD REGISTRY (ACTIVE)
        // =========================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderRowBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Credit Card Registry (Active)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Owed Balance",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.activeCreditCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No credit cards with active balance.",
                        fontSize = 13.sp,
                        color = NeutralGray
                    )
                }
            } else {
                uiState.activeCreditCards.forEach { (card, bal) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 0.5.dp, color = BorderTable)
                            .padding(vertical = 6.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💳 ${card.name}",
                            fontSize = 13.sp,
                            color = TextSlate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = String.format(Locale.US, "$%,.2f", bal),
                            fontSize = 13.sp,
                            fontFamily = MonoFontFamily,
                            color = if (bal > 0.005) NegativeRed else if (bal < -0.005) PositiveGreen else TextSlate,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =========================================================
        // 7. UPCOMING SCHEDULED BILLS (FUTURE EXPENSES)
        // =========================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                .background(Color.White)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderRowBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "Upcoming Scheduled Bills (Future Expenses)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate
                )
            }

            // Inline Add Row Form
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RowAltBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Description Input
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .height(32.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (futureDesc.isEmpty()) {
                        Text("Bill Name (e.g. Rent)", fontSize = 12.sp, color = NeutralGray)
                    }
                    BasicTextField(
                        value = futureDesc,
                        onValueChange = { futureDesc = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 12.sp, color = PrimarySlate)
                    )
                }

                // Amount Input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (futureAmount.isEmpty()) {
                        Text("Amount", fontSize = 12.sp, color = NeutralGray)
                    }
                    BasicTextField(
                        value = futureAmount,
                        onValueChange = { futureAmount = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(fontSize = 12.sp, color = PrimarySlate)
                    )
                }

                // Due Date Input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(1.dp, BorderTable, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (futureDate.isEmpty()) {
                        Text("Due Date", fontSize = 12.sp, color = NeutralGray)
                    }
                    BasicTextField(
                        value = futureDate,
                        onValueChange = { futureDate = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 12.sp, color = PrimarySlate)
                    )
                }

                // Plus Add Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimarySlate)
                        .clickable {
                            val amt = futureAmount.toDoubleOrNull() ?: 0.0
                            if (futureDesc.isNotBlank() && amt > 0) {
                                viewModel.addFutureExpense(futureDesc.trim(), amt, futureDate.trim())
                                futureDesc = ""
                                futureAmount = ""
                                futureDate = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("➕", fontSize = 12.sp, color = Color.White)
                }
            }

            // Table SubHeader Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SubHeaderRowBg)
                    .border(width = 0.5.dp, color = BorderTable)
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bill Item",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LabelSlate,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Amount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LabelSlate,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Due Date",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LabelSlate,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "Action",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LabelSlate,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.futureExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming scheduled bills logged.",
                        fontSize = 13.sp,
                        color = NeutralGray
                    )
                }
            } else {
                uiState.futureExpenses.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 0.5.dp, color = BorderTable)
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = TextSlate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = String.format(Locale.US, "$%,.2f", item.amount),
                            fontSize = 13.sp,
                            fontFamily = MonoFontFamily,
                            color = TextSlate,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.dueDate ?: "-",
                            fontSize = 11.sp,
                            fontFamily = MonoFontFamily,
                            color = NeutralGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.2f)
                        )
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DangerBg)
                                    .border(1.dp, DangerBorder, RoundedCornerShape(4.dp))
                                    .clickable { viewModel.deleteFutureExpense(item.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🗑️", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
