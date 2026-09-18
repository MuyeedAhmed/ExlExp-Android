package com.muyeedahmed.exldroid.domain.model

data class CategoryShare(
    val category: String,
    val amount: Double,
    val percentage: Double,       // 0.0 to 100.0
    val colorHex: String
)

data class MonthlyCategoryDistribution(
    val monthKey: String,
    val totalSpending: Double,
    val shares: List<CategoryShare>
)
