package com.muyeedahmed.exlexp.ui.screens.alltransactions

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.ui.components.TransactionRowItem
import com.muyeedahmed.exlexp.ui.theme.BorderTable
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate

@Composable
fun AllTransactionsScreen(
    viewModel: AllTransactionsViewModel,
    onNavigateBack: () -> Unit,
    onEditExpense: (Expense) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var visibleCount by remember { mutableIntStateOf(50) }
    LaunchedEffect(uiState.searchQuery, uiState.activeFilter) {
        visibleCount = 50
    }

    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val displayed = uiState.transactions.take(visibleCount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 1.dp, color = Color(0xFFF1F5F9))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Back button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .clickable { onNavigateBack() }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimarySlate
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Analytics",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimarySlate
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "All Transactions",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimarySlate
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Showing ${displayed.size} of ${uiState.transactions.size} entries (${uiState.totalCount} total)",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        // 2. Search & Filter Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 1.dp, color = Color(0xFFE2E8F0))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            color = PrimarySlate
                        ),
                        cursorBrush = SolidColor(PrimarySlate),
                        decorationBox = { innerTextField ->
                            if (uiState.searchQuery.isEmpty()) {
                                Text(
                                    text = "Search transactions (description, account, amount, date)...",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    if (uiState.searchQuery.isNotEmpty()) {
                        Text(
                            text = "✕",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier
                                .clickable { viewModel.onSearchQueryChange("") }
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterPill(
                    text = "All (${uiState.totalCount})",
                    isSelected = uiState.activeFilter == TransactionFilter.ALL,
                    onClick = { viewModel.onFilterChange(TransactionFilter.ALL) }
                )
                FilterPill(
                    text = "🏛️ Bank & Invest",
                    isSelected = uiState.activeFilter == TransactionFilter.BANK_AND_INVEST,
                    onClick = { viewModel.onFilterChange(TransactionFilter.BANK_AND_INVEST) }
                )
                FilterPill(
                    text = "💳 Credit Cards",
                    isSelected = uiState.activeFilter == TransactionFilter.CREDIT_CARDS,
                    onClick = { viewModel.onFilterChange(TransactionFilter.CREDIT_CARDS) }
                )
                FilterPill(
                    text = "🔄 Transfers",
                    isSelected = uiState.activeFilter == TransactionFilter.TRANSFERS,
                    onClick = { viewModel.onFilterChange(TransactionFilter.TRANSFERS) }
                )
            }
        }

        // 3. Transactions List
        if (uiState.transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "No matching transactions found.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = displayed,
                    key = { it.expense.id }
                ) { tx ->
                    TransactionRowItem(
                        transaction = tx,
                        onEditClick = { onEditExpense(tx.expense) },
                        onDeleteClick = { expenseToDelete = tx.expense }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = BorderTable)
                }

                // Load More Button or All Loaded Indicator
                item {
                    if (uiState.transactions.size > visibleCount) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC))
                                .clickable { visibleCount += 50 }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Load 50 More (Showing ${displayed.size} of ${uiState.transactions.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF3B82F6)
                            )
                        }
                    } else if (uiState.transactions.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "All ${uiState.transactions.size} transactions loaded",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
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
private fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) PrimarySlate else Color(0xFFF1F5F9))
            .border(
                width = 1.dp,
                color = if (isSelected) PrimarySlate else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) Color.White else Color(0xFF475569)
        )
    }
}

