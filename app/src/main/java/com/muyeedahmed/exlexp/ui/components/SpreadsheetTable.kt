package com.muyeedahmed.exlexp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.ui.theme.BorderGray
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.NeutralGray
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import java.util.Locale
import kotlin.math.abs

data class SpreadsheetColumn(
    val title: String,
    val width: Dp,
    val alignment: TextAlign = TextAlign.Start
)

@Composable
fun SpreadsheetTable(
    columns: List<SpreadsheetColumn>,
    expenses: List<Expense>,
    isSavingsAccount: Boolean = false,
    isCreditCard: Boolean = false,
    modifier: Modifier = Modifier,
    onEditExpense: (Expense) -> Unit = {},
    onDeleteExpense: (Expense) -> Unit = {}
) {
    val horizontalScrollState = rememberScrollState()
    val totalTableWidth = columns.map { it.width }.fold(0.dp) { acc, dp -> acc + dp } + 80.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderGray)
    ) {
        // Horizontal scroll container
        Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
            Column(modifier = Modifier.width(totalTableWidth)) {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(Color(0xFFE2E8F0)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    columns.forEach { col ->
                        Text(
                            text = col.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            textAlign = col.alignment,
                            modifier = Modifier
                                .width(col.width)
                                .padding(horizontal = 6.dp)
                        )
                    }
                    Text(
                        text = "Actions",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(horizontal = 6.dp)
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = BorderGray)

                // Virtualized Rows
                LazyColumn(modifier = Modifier.height(380.dp)) {
                    itemsIndexed(expenses) { index, item ->
                        val rowBg = if (index % 2 == 0) Color.White else Color(0xFFF8FAFC)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(rowBg),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date
                            Text(
                                text = item.date,
                                fontSize = 11.sp,
                                color = NeutralGray,
                                modifier = Modifier
                                    .width(85.dp)
                                    .padding(horizontal = 6.dp)
                            )

                            // Description / From-To
                            val descText = if (isCreditCard) item.description else (item.fromTo ?: item.description)
                            Text(
                                text = descText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .width(130.dp)
                                    .padding(horizontal = 6.dp)
                            )

                            if (isCreditCard) {
                                // Spend
                                val spend = if (item.amount > 0 && !item.isReward) item.amount else 0.0
                                Text(
                                    text = if (spend > 0) String.format(Locale.US, "$%,.2f", spend) else "-",
                                    fontSize = 11.sp,
                                    color = if (spend > 0) NegativeRed else NeutralGray,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .width(75.dp)
                                        .padding(horizontal = 6.dp)
                                )

                                // Paid
                                val paid = if (item.amount < 0) abs(item.amount) else 0.0
                                Text(
                                    text = if (paid > 0) String.format(Locale.US, "$%,.2f", paid) else "-",
                                    fontSize = 11.sp,
                                    color = if (paid > 0) PositiveGreen else NeutralGray,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .width(75.dp)
                                        .padding(horizontal = 6.dp)
                                )

                                // Rewards
                                val reward = item.rewardValue ?: 0.0
                                Text(
                                    text = if (reward > 0) String.format(Locale.US, "$%,.2f", reward) else "-",
                                    fontSize = 11.sp,
                                    color = if (reward > 0) PositiveGreen else NeutralGray,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .width(70.dp)
                                        .padding(horizontal = 6.dp)
                                )
                            } else {
                                // Amount for Deposit account
                                val isDepositAmount = item.amount > 0
                                val amtColor = if (isDepositAmount) PositiveGreen else NegativeRed
                                Text(
                                    text = String.format(Locale.US, (if (isDepositAmount) "+$%,.2f" else "-$%,.2f"), abs(item.amount)),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = amtColor,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .width(90.dp)
                                        .padding(horizontal = 6.dp)
                                )

                                if (isSavingsAccount) {
                                    // Interest column
                                    val interest = if (item.isInterest) item.amount else 0.0
                                    Text(
                                        text = if (interest > 0) String.format(Locale.US, "+$%,.2f", interest) else "-",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (interest > 0) PositiveGreen else NeutralGray,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .width(75.dp)
                                            .padding(horizontal = 6.dp)
                                    )
                                }

                                // Details / Notes
                                Text(
                                    text = item.details ?: "-",
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = NeutralGray,
                                    modifier = Modifier
                                        .width(120.dp)
                                        .padding(horizontal = 6.dp)
                                )
                            }

                            // Category
                            Text(
                                text = item.category,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(horizontal = 6.dp)
                            )

                            // Actions (Edit, Delete)
                            Row(
                                modifier = Modifier.width(80.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onEditExpense(item) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = NeutralGray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteExpense(item) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = NegativeRed.copy(alpha = 0.8f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
                    }
                }
            }
        }
    }
}
