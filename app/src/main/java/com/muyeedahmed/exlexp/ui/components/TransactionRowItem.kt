package com.muyeedahmed.exlexp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.DisplayTransaction
import com.muyeedahmed.exlexp.ui.theme.MonoFontFamily
import com.muyeedahmed.exlexp.ui.theme.NegativeRed
import com.muyeedahmed.exlexp.ui.theme.NeutralGray
import com.muyeedahmed.exlexp.ui.theme.PositiveGreen
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate
import com.muyeedahmed.exlexp.ui.theme.SubtitleGray

@Composable
fun TransactionRowItem(
    transaction: DisplayTransaction,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val amountColor = when {
        transaction.isGreenColor -> PositiveGreen
        transaction.isRedColor -> NegativeRed
        else -> PrimarySlate
    }

    val displayAccountText = if (transaction.displayAccount.isNotBlank()) {
        transaction.displayAccount
    } else {
        "${transaction.accountIcon} ${transaction.accountName}"
    }

    val displayDate = if (transaction.dateMmDd.isNotBlank()) {
        transaction.dateMmDd
    } else if (transaction.expense.date.length >= 5) {
        transaction.expense.date.substring(5)
    } else {
        transaction.expense.date
    }

    val descText = if (transaction.description.isNotBlank()) {
        transaction.description
    } else {
        transaction.expense.description
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 9.dp, horizontal = 12.dp)
    ) {
        // Line 1: Date (11sp mono 44dp), Description (13sp black), Amount (13sp mono bold), Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayDate,
                fontSize = 11.sp,
                fontFamily = MonoFontFamily,
                color = NeutralGray,
                modifier = Modifier.width(44.dp)
            )

            Text(
                text = descText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp, end = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = transaction.formattedAmount,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MonoFontFamily,
                    color = amountColor,
                    textAlign = TextAlign.End
                )

                if (onEditClick != null || onDeleteClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    if (onEditClick != null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onEditClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✏️", fontSize = 12.sp)
                        }
                    }
                    if (onDeleteClick != null) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onDeleteClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🗑️", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Line 2: Empty Spacer (44dp) + Account with Icon (10sp bold #5d5d5d)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(44.dp))

            Text(
                text = displayAccountText,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = SubtitleGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp, end = 8.dp)
            )
        }
    }
}
