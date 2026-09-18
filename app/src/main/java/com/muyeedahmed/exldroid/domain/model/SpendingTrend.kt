package com.muyeedahmed.exldroid.domain.model

data class MonthSpend(
    val monthKey: String,          // e.g. "2026-09"
    val displayLabel: String,      // e.g. "Sep '26"
    val totalSpend: Double
)

data class SpendingTrend(
    val monthlySpends: List<MonthSpend>,
    val grandTotal: Double,
    val monthlyAverage: Double,
    val maxMonthlySpend: Double
)
