package com.muyeedahmed.exldroid.domain.usecase

import com.muyeedahmed.exldroid.domain.model.CategoryShare
import com.muyeedahmed.exldroid.domain.model.CreditCard
import com.muyeedahmed.exldroid.domain.model.Expense
import com.muyeedahmed.exldroid.domain.model.MonthSpend
import com.muyeedahmed.exldroid.domain.model.MonthlyCategoryDistribution
import com.muyeedahmed.exldroid.domain.model.SpendingTrend
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

class CalculateRollingTrendUseCase @Inject constructor() {

    fun calculate12MonthTrend(
        cards: List<CreditCard>,
        expenses: List<Expense>,
        now: LocalDate = LocalDate.now()
    ): SpendingTrend {
        val cardMap = cards.associateBy { it.id }

        // Generate the 12 rolling months from oldest (11 months ago) to newest (current month)
        val monthKeys = mutableListOf<String>()
        val displayLabels = mutableListOf<String>()
        val monthFormatter = DateTimeFormatter.ofPattern("MMM ''yy", Locale.US)

        for (i in 11 downTo 0) {
            val date = now.minusMonths(i.toLong())
            val key = String.format(Locale.US, "%04d-%02d", date.year, date.monthValue)
            monthKeys.add(key)
            displayLabels.add(date.format(monthFormatter))
        }

        val monthlySpendMap = monthKeys.associateWith { 0.0 }.toMutableMap()

        for (expense in expenses) {
            // 1. Exclude Transfers
            if (expense.isTransfer || expense.category.equals("Transfer", ignoreCase = true)) {
                continue
            }
            // 2. Exclude Salary / Income
            if (expense.category.equals("Salary", ignoreCase = true)) {
                continue
            }

            val expenseMonth = if (expense.date.length >= 7) expense.date.substring(0, 7) else continue
            if (!monthlySpendMap.containsKey(expenseMonth)) continue

            val card = cardMap[expense.creditCardId]
            val isDeposit = card?.accountType?.isDeposit ?: false

            val spending = if (isDeposit) {
                when {
                    expense.amount < 0 && !expense.isInterest -> abs(expense.amount)
                    expense.amount > 0 -> -expense.amount
                    else -> 0.0
                }
            } else {
                when {
                    expense.amount > 0 && !expense.isReward -> expense.amount
                    expense.amount < 0 -> expense.amount // refund reducing spend
                    else -> 0.0
                }
            }

            monthlySpendMap[expenseMonth] = (monthlySpendMap[expenseMonth] ?: 0.0) + spending
        }

        val monthlySpends = monthKeys.mapIndexed { index, key ->
            MonthSpend(
                monthKey = key,
                displayLabel = displayLabels[index],
                totalSpend = max(0.0, monthlySpendMap[key] ?: 0.0)
            )
        }

        val grandTotal = monthlySpends.sumOf { it.totalSpend }
        val monthlyAverage = if (monthlySpends.isNotEmpty()) grandTotal / monthlySpends.size else 0.0
        val maxMonthlySpend = monthlySpends.maxOfOrNull { it.totalSpend } ?: 1.0

        return SpendingTrend(
            monthlySpends = monthlySpends,
            grandTotal = grandTotal,
            monthlyAverage = monthlyAverage,
            maxMonthlySpend = if (maxMonthlySpend <= 0.0) 1.0 else maxMonthlySpend
        )
    }

    fun calculateCategoryDistribution(
        monthKey: String,
        cards: List<CreditCard>,
        expenses: List<Expense>
    ): MonthlyCategoryDistribution {
        val cardMap = cards.associateBy { it.id }
        val categoryMap = mutableMapOf<String, Double>()

        for (expense in expenses) {
            if (expense.isTransfer || expense.category.equals("Transfer", ignoreCase = true)) continue
            if (expense.category.equals("Salary", ignoreCase = true)) continue

            val expenseMonth = if (expense.date.length >= 7) expense.date.substring(0, 7) else continue
            if (expenseMonth != monthKey) continue

            val card = cardMap[expense.creditCardId]
            val isDeposit = card?.accountType?.isDeposit ?: false

            val spending = if (isDeposit) {
                when {
                    expense.amount < 0 && !expense.isInterest -> abs(expense.amount)
                    expense.amount > 0 -> -expense.amount
                    else -> 0.0
                }
            } else {
                when {
                    expense.amount > 0 && !expense.isReward -> expense.amount
                    expense.amount < 0 -> expense.amount
                    else -> 0.0
                }
            }

            val cat = expense.category.ifBlank { "Others" }
            categoryMap[cat] = (categoryMap[cat] ?: 0.0) + spending
        }

        val positiveCategories = categoryMap
            .filter { it.value > 0.0 }
            .toList()
            .sortedByDescending { it.second }

        val totalSpending = positiveCategories.sumOf { it.second }

        val shares = positiveCategories.map { (cat, amt) ->
            val percentage = if (totalSpending > 0) (amt / totalSpending) * 100.0 else 0.0
            CategoryShare(
                category = cat,
                amount = amt,
                percentage = percentage,
                colorHex = getCategoryColor(cat)
            )
        }

        return MonthlyCategoryDistribution(
            monthKey = monthKey,
            totalSpending = totalSpending,
            shares = shares
        )
    }

    fun getCategoryColor(category: String): String {
        return when (category.trim().lowercase()) {
            "rent", "housing" -> "#6366f1"
            "utilities", "utility", "bills" -> "#0284c7"
            "car payment", "transportation", "transport" -> "#8b5cf6"
            "gas" -> "#ec4899"
            "grocery", "groceries", "grocery / food" -> "#10b981"
            "food", "eating out", "dining", "restaurant" -> "#f59e0b"
            "necessary purchases" -> "#14b8a6"
            "luxury purchases", "shopping" -> "#ec4899"
            "entertainment" -> "#f97316"
            "subscriptions" -> "#a855f7"
            "health", "medical" -> "#ef4444"
            "travel" -> "#06b6d4"
            "fee", "annual fee" -> "#b45309"
            "others" -> "#64748b"
            "salary" -> "#16a34a"
            "transfer" -> "#3b82f6"
            else -> {
                val palette = listOf(
                    "#6366f1", "#0284c7", "#8b5cf6", "#ec4899", "#10b981",
                    "#f59e0b", "#14b8a6", "#f97316", "#a855f7", "#ef4444",
                    "#06b6d4", "#b45309", "#64748b"
                )
                val hash = abs(category.hashCode())
                palette[hash % palette.size]
            }
        }
    }
}
