package com.muyeedahmed.exlexp.domain.model

data class FinancialSummary(
    val netBalance: Double,
    val totalCheckingBalance: Double,
    val totalCreditCardDebt: Double,
    val totalFutureExpenses: Double,
    val totalBrokerageBalance: Double,
    val averageCreditAge: String,
    val openCardsCount: Int,
    val closedCardsCount: Int
)
