package com.muyeedahmed.exlexp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.SpendingTrend
import com.muyeedahmed.exlexp.ui.theme.ActiveBlue
import com.muyeedahmed.exlexp.ui.theme.ActiveSelectionBg
import com.muyeedahmed.exlexp.ui.theme.BorderGray
import com.muyeedahmed.exlexp.ui.theme.MonoFontFamily
import com.muyeedahmed.exlexp.ui.theme.NeutralGray
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate
import com.muyeedahmed.exlexp.ui.theme.SubHeaderRowBg
import java.util.Locale

private fun formatShortK(valAmt: Double): String {
    if (valAmt <= 0) return "$0"
    if (valAmt >= 1000) {
        return String.format(Locale.US, "$%.1fk", valAmt / 1000.0)
    }
    return String.format(Locale.US, "$%.0f", valAmt)
}

@Composable
fun TrendBarChart(
    spendingTrend: SpendingTrend,
    selectedMonthKey: String,
    onMonthSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
        // Section Header Row: Heading + Subtitle on Left, Stat Badges on Right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = "📊 12-Month Spending Trend",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimarySlate
                )
                Text(
                    text = "Tap any month bar to inspect its category breakdown",
                    fontSize = 12.sp,
                    color = NeutralGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // 12-Mo Total Badge
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SubHeaderRowBg)
                        .border(1.dp, BorderGray, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "12-MO TOTAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralGray
                    )
                    Text(
                        text = String.format(Locale.US, "$%,.2f", spendingTrend.grandTotal),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = MonoFontFamily,
                        color = PrimarySlate
                    )
                }

                // Monthly Avg Badge
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SubHeaderRowBg)
                        .border(1.dp, BorderGray, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "MONTHLY AVG",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralGray
                    )
                    Text(
                        text = String.format(Locale.US, "$%,.2f", spendingTrend.monthlyAverage),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = MonoFontFamily,
                        color = PrimarySlate
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bar Chart Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxSpend = spendingTrend.maxMonthlySpend.coerceAtLeast(1.0)

                for (month in spendingTrend.monthlySpends) {
                    val isSelected = month.monthKey == selectedMonthKey
                    val ratio = (month.totalSpend / maxSpend).toFloat().coerceIn(0.06f, 1.0f)
                    val animatedRatio by animateFloatAsState(targetValue = ratio, label = "barHeight")

                    val parts = month.displayLabel.split(" ")
                    val monthLabel = parts.firstOrNull() ?: month.displayLabel
                    val yearLabel = parts.getOrNull(1) ?: ""

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) ActiveSelectionBg else Color.Transparent)
                            .clickable { onMonthSelected(month.monthKey) }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        // Amount label on top
                        Text(
                            text = formatShortK(month.totalSpend),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontFamily = MonoFontFamily,
                            color = if (isSelected) ActiveBlue else NeutralGray,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        // Vertical Track & Fill
                        Box(
                            modifier = Modifier
                                .height(110.dp)
                                .width(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SubHeaderRowBg)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animatedRatio)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(if (isSelected) PrimarySlate else Color(0xFF94A3B8))
                            )
                        }

                        // Month Label
                        Text(
                            text = monthLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (isSelected) PrimarySlate else Color(0xFF475569),
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        // Year Label
                        Text(
                            text = yearLabel,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ActiveBlue else NeutralGray
                        )
                    }
                }
            }
        }
    }
}
