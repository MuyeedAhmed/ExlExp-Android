package com.muyeedahmed.exlexp.ui.screens.creditcards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import java.util.Locale
import kotlin.math.abs

private val MonoFontFamily = FontFamily.Monospace

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

    // Selected tab index calculation: 0 = overview, 1..N = cards
    val cardIndex = uiState.cards.indexOfFirst { it.id == uiState.selectedTabId }
    val selectedTabIndex = if (isOverview) 0 else if (cardIndex >= 0) cardIndex + 1 else 0

    var visibleCount by remember { mutableIntStateOf(25) }

    LaunchedEffect(uiState.selectedTabId) {
        visibleCount = 25
    }

    var cardToEditOpenDate by remember { mutableStateOf<CreditCard?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Material 3 Scrollable Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 12.dp
        ) {
            // Overview Tab
            Tab(
                selected = isOverview,
                onClick = { viewModel.selectTab("overview") },
                text = { Text("Overview", fontWeight = if (isOverview) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview", modifier = Modifier.size(18.dp)) }
            )

            // Individual Card Tabs
            uiState.cards.forEachIndexed { idx, card ->
                val isSelected = selectedTabIndex == idx + 1
                val label = if (card.isClosed) "${card.name} (Closed)" else card.name
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.selectTab(card.id) },
                    text = {
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = card.name, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        if (isOverview) {
            // ==================== OVERVIEW PAGE ====================
            val overviewScroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(overviewScroll)
                    .padding(16.dp)
                    .padding(bottom = 70.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section Header with Add Card Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Credit Portfolio",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${uiState.totalOpenCards} open account${if (uiState.totalOpenCards != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = onNavigateToSettings,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Card", style = MaterialTheme.typography.labelMedium)
                    }
                }

                // KPI Grid (6 Cards, 2 per row)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Average Credit Age (Highlighted)
                        M3KpiCard(
                            label = "AVG CREDIT AGE",
                            value = uiState.averageCreditAge,
                            sub = "Across ${uiState.totalOpenCards} open accounts",
                            valueColor = PositiveGreen,
                            isHighlight = true,
                            modifier = Modifier.weight(1f)
                        )

                        // Total Balance Due
                        val debtDueColor = if (uiState.totalDebtDue > 0.005) NegativeRed else PositiveGreen
                        M3KpiCard(
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
                        M3KpiCard(
                            label = "TOTAL SPENT",
                            value = String.format(Locale.US, "$%,.2f", uiState.totalLifetimeSpent),
                            sub = "Purchases & charges",
                            modifier = Modifier.weight(1f)
                        )

                        // Total Paid
                        M3KpiCard(
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
                        M3KpiCard(
                            label = "TOTAL REWARDS",
                            value = String.format(Locale.US, "$%,.2f", uiState.totalRewardsEarned),
                            sub = "Cashback & points",
                            valueColor = PositiveGreen,
                            modifier = Modifier.weight(1f)
                        )

                        // Annual Fees Paid
                        val feeColor = if (uiState.totalAnnualFeesPaid > 0) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurface
                        M3KpiCard(
                            label = "ANNUAL FEES PAID",
                            value = String.format(Locale.US, "$%,.2f", uiState.totalAnnualFeesPaid),
                            sub = "Lifetime card fees",
                            valueColor = feeColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // All Credit Cards Summary Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Card Accounts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tap any card's open date to edit. Tap \"View Transactions\" to inspect charges.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (uiState.cardStatsList.isEmpty()) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No credit cards found. Add one in Settings!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        uiState.cardStatsList.forEach { stat ->
                            val card = stat.card
                            val closed = card.isClosed

                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (closed) {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                ),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Row 1: Name, Badges, and Open Date edit chip
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f, fill = false)
                                        ) {
                                            Text(
                                                text = card.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (closed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (closed) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = MaterialTheme.colorScheme.errorContainer,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "CLOSED",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Open Date Chip (Clickable to edit)
                                        AssistChip(
                                            onClick = { cardToEditOpenDate = card },
                                            label = {
                                                Text(
                                                    text = card.openDate,
                                                    fontFamily = MonoFontFamily,
                                                    fontSize = 11.sp
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.EditCalendar,
                                                    contentDescription = "Edit date",
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Row 2: Balance Due & Credit Age
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Balance Due",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            val dueColor = if (stat.balanceDue > 0.005) NegativeRed else PositiveGreen
                                            Text(
                                                text = String.format(Locale.US, "$%,.2f", stat.balanceDue),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = MonoFontFamily,
                                                color = dueColor
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Credit Age",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = stat.creditAge.formatted,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = MonoFontFamily,
                                                color = if (closed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (closed) {
                                                Text(
                                                    text = "Excluded from average",
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Row 3: Stats sub-card
                                    OutlinedCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.outlinedCardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Spent", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    String.format(Locale.US, "$%,.2f", stat.spent),
                                                    fontSize = 12.sp,
                                                    fontFamily = MonoFontFamily,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Column {
                                                Text("Paid", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    String.format(Locale.US, "$%,.2f", stat.paid),
                                                    fontSize = 12.sp,
                                                    fontFamily = MonoFontFamily,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = PositiveGreen
                                                )
                                            }
                                            Column {
                                                Text("Rewards", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    String.format(Locale.US, "$%,.2f", stat.rewards),
                                                    fontSize = 12.sp,
                                                    fontFamily = MonoFontFamily,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = PositiveGreen
                                                )
                                            }
                                            Column {
                                                Text("Fees", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    String.format(Locale.US, "$%,.2f", stat.fees),
                                                    fontSize = 12.sp,
                                                    fontFamily = MonoFontFamily,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (stat.fees > 0) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Row 4: View Transactions Button
                                    FilledTonalButton(
                                        onClick = { viewModel.selectTab(card.id) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("View Card Transactions", style = MaterialTheme.typography.labelLarge)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ==================== INDIVIDUAL CARD SHEET ====================
            val stat = uiState.cardStatsList.find { it.card.id == selectedCard?.id }
            val balanceDue = stat?.balanceDue ?: 0.0
            val cardExpenses = uiState.selectedCardExpenses

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 70.dp)
            ) {
                // Card Header Banner
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = selectedCard?.name ?: "Credit Card",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (selectedCard?.isClosed == true) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "CLOSED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Opened: ${selectedCard?.openDate ?: "—"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Current Balance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", balanceDue),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = MonoFontFamily,
                                    color = if (balanceDue > 0.005) NegativeRed else PositiveGreen
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Transactions List
                if (cardExpenses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No transactions recorded for this card.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    val displayed = cardExpenses.take(visibleCount)
                    items(displayed, key = { it.id }) { item ->
                        val amt = item.amount
                        val isPaid = !item.isReward && amt < 0
                        val isReward = item.isReward

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Row 1: Date, Category badge, Action buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.date,
                                            fontSize = 11.sp,
                                            fontFamily = MonoFontFamily,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = item.category.ifEmpty { "Others" },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        if (item.isFee) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "FEE",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row {
                                        IconButton(
                                            onClick = { onEditExpense(item) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { expenseToDelete = item },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = NegativeRed.copy(alpha = 0.8f),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Row 2: Description & Amount
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    )

                                    val amountText = when {
                                        isReward -> String.format(Locale.US, "+$%,.2f", item.rewardValue ?: abs(amt))
                                        isPaid -> String.format(Locale.US, "-$%,.2f", abs(amt))
                                        else -> String.format(Locale.US, "$%,.2f", amt)
                                    }
                                    val amountColor = when {
                                        isReward -> PositiveGreen
                                        isPaid -> PositiveGreen
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }

                                    Text(
                                        text = amountText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = MonoFontFamily,
                                        color = amountColor
                                    )
                                }
                            }
                        }
                    }

                    // Load More Button
                    if (cardExpenses.size > visibleCount) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { visibleCount += 25 },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Show More (showing $visibleCount of ${cardExpenses.size})")
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
            text = { Text("Are you sure you want to delete \"${exp.description}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(exp.id)
                        expenseToDelete = null
                    }
                ) {
                    Text("Delete")
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
private fun M3KpiCard(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isHighlight: Boolean = false
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isHighlight) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = if (isHighlight) 20.sp else 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MonoFontFamily,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
