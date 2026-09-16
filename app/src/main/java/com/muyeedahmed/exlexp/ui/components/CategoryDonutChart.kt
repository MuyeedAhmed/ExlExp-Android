package com.muyeedahmed.exlexp.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.domain.model.MonthlyCategoryDistribution
import com.muyeedahmed.exlexp.ui.theme.BorderGray
import com.muyeedahmed.exlexp.ui.theme.DarkSlate
import com.muyeedahmed.exlexp.ui.theme.MonoFontFamily
import com.muyeedahmed.exlexp.ui.theme.NeutralGray
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate
import com.muyeedahmed.exlexp.ui.theme.SubHeaderRowBg
import com.muyeedahmed.exlexp.ui.theme.categoryToColor
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun formatMonthLabel(monthStr: String): String {
    if (monthStr.isBlank()) return ""
    return try {
        val ym = YearMonth.parse(monthStr)
        ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))
    } catch (e: Exception) {
        monthStr
    }
}

private fun formatShortK(valAmt: Double): String {
    if (valAmt <= 0) return "$0"
    if (valAmt >= 1000) {
        return String.format(Locale.US, "$%.1fk", valAmt / 1000.0)
    }
    return String.format(Locale.US, "$%.0f", valAmt)
}

@Composable
fun CategoryDonutChart(
    distribution: MonthlyCategoryDistribution,
    availableMonths: List<String> = emptyList(),
    selectedMonthKey: String = "",
    onMonthSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(top = 16.dp)) {
        // Month Selector Row (Tab strip above card)
        if (availableMonths.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    .background(SubHeaderRowBg)
                    .border(1.dp, BorderGray, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MONTH:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralGray,
                    modifier = Modifier.padding(end = 8.dp)
                )

                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (month in availableMonths) {
                        val isSelected = month == (if (selectedMonthKey.isNotBlank()) selectedMonthKey else distribution.monthKey)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isSelected) Color.White else Color(0xFFE2E8F0))
                                .border(1.dp, BorderGray, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .clickable { onMonthSelected(month) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = formatMonthLabel(month),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) PrimarySlate else NeutralGray
                            )
                        }
                    }
                }
            }
        }

        // Distribution Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    if (availableMonths.isNotEmpty())
                        RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                    else
                        RoundedCornerShape(6.dp)
                )
                .background(Color.White)
                .border(
                    1.dp,
                    BorderGray,
                    if (availableMonths.isNotEmpty())
                        RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                    else
                        RoundedCornerShape(6.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                // Card Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Spending Distribution - ${formatMonthLabel(distribution.monthKey)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimarySlate
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Total Spent: ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = String.format(Locale.US, "$%,.2f", distribution.totalSpending),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MonoFontFamily,
                            color = PrimarySlate
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(14.dp))

                if (distribution.shares.isEmpty() || distribution.totalSpending <= 0.005) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No categorized spending logged for this month.",
                            fontSize = 13.sp,
                            color = NeutralGray
                        )
                    }
                } else {
                    // Donut Wheel (Centered on Mobile)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.size(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(136.dp)) {
                                var startAngle = -90f
                                val strokeWidth = 24.dp.toPx()

                                for (share in distribution.shares) {
                                    val sweepAngle = (share.percentage / 100f * 360f).toFloat()
                                    val color = categoryToColor(share.category)

                                    drawArc(
                                        color = color,
                                        startAngle = startAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                    )
                                    startAngle += sweepAngle
                                }
                            }

                            // Donut Center Text
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SPENT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralGray
                                )
                                Text(
                                    text = formatShortK(distribution.totalSpending),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = MonoFontFamily,
                                    color = PrimarySlate
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Breakdown Legend
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (cat in distribution.shares) {
                            val color = categoryToColor(cat.category)

                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Top Line: Color dot + Name on left, Amount + Percent on right
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cat.category,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkSlate
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = String.format(Locale.US, "$%,.2f", cat.amount),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = MonoFontFamily,
                                            color = PrimarySlate
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = String.format(Locale.US, "%.1f%%", cat.percentage),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeutralGray,
                                            modifier = Modifier.width(46.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Category Bar Track & Fill
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(SubHeaderRowBg)
                                ) {
                                    val barWidthRatio = (cat.percentage / 100f).toFloat().coerceIn(0.03f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(barWidthRatio)
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
