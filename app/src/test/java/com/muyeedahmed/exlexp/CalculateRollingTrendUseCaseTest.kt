package com.muyeedahmed.exlexp

import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.domain.usecase.CalculateRollingTrendUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CalculateRollingTrendUseCaseTest {

    private lateinit var useCase: CalculateRollingTrendUseCase
    private val fixedToday = LocalDate.of(2026, 9, 5)

    @Before
    fun setup() {
        useCase = CalculateRollingTrendUseCase()
    }

    @Test
    fun test12MonthTrendCalculationsAndExclusions() {
        val checking = CreditCard(id = "ch-1", name = "Checking", isChecking = true, openDate = "2020-01-01")
        val cc = CreditCard(id = "cc-1", name = "Card", isChecking = false, openDate = "2020-01-01")

        val expenses = listOf(
            // Deposit spending (amount < 0) => +150 spend
            Expense(id = "1", description = "Grocery", amount = -150.0, creditCardId = "ch-1", date = "2026-09-01", category = "Groceries"),
            // CC spending (amount > 0) => +80 spend
            Expense(id = "2", description = "Dinner", amount = 80.0, creditCardId = "cc-1", date = "2026-09-02", category = "Food"),
            // Transfer => EXCLUDED
            Expense(id = "3", description = "Transfer to Savings", amount = -500.0, creditCardId = "ch-1", date = "2026-09-02", isTransfer = true, category = "Transfer"),
            // Salary => EXCLUDED
            Expense(id = "4", description = "Bi-weekly paycheck", amount = 3000.0, creditCardId = "ch-1", date = "2026-09-01", category = "Salary"),
            // Previous month (2026-08)
            Expense(id = "5", description = "Gas", amount = 60.0, creditCardId = "cc-1", date = "2026-08-15", category = "Gas")
        )

        val trend = useCase.calculate12MonthTrend(listOf(checking, cc), expenses, fixedToday)

        // Must have exactly 12 months
        assertEquals(12, trend.monthlySpends.size)

        // Oldest month is 2025-10, newest is 2026-09
        assertEquals("2025-10", trend.monthlySpends.first().monthKey)
        assertEquals("2026-09", trend.monthlySpends.last().monthKey)

        // 2026-09 spend should be 150 (deposit) + 80 (cc) = 230
        val sepSpend = trend.monthlySpends.first { it.monthKey == "2026-09" }
        assertEquals(230.0, sepSpend.totalSpend, 0.001)

        // 2026-08 spend should be 60
        val augSpend = trend.monthlySpends.first { it.monthKey == "2026-08" }
        assertEquals(60.0, augSpend.totalSpend, 0.001)

        // Grand total: 230 + 60 = 290
        assertEquals(290.0, trend.grandTotal, 0.001)
        assertEquals(290.0 / 12.0, trend.monthlyAverage, 0.001)
        assertEquals(230.0, trend.maxMonthlySpend, 0.001)
    }

    @Test
    fun testCategoryDistributionForMonth() {
        val cc = CreditCard(id = "cc-1", name = "Card", isChecking = false, openDate = "2020-01-01")
        val expenses = listOf(
            Expense(id = "1", description = "Target", amount = 100.0, creditCardId = "cc-1", date = "2026-09-01", category = "Groceries"),
            Expense(id = "2", description = "Chipotle", amount = 25.0, creditCardId = "cc-1", date = "2026-09-02", category = "Food")
        )

        val dist = useCase.calculateCategoryDistribution("2026-09", listOf(cc), expenses)
        assertEquals(125.0, dist.totalSpending, 0.001)
        assertEquals(2, dist.shares.size)

        val groceries = dist.shares.first { it.category == "Groceries" }
        assertEquals(100.0, groceries.amount, 0.001)
        assertEquals(80.0, groceries.percentage, 0.001) // 100 / 125 * 100 = 80%

        val food = dist.shares.first { it.category == "Food" }
        assertEquals(25.0, food.amount, 0.001)
        assertEquals(20.0, food.percentage, 0.001) // 25 / 125 * 100 = 20%
    }
}
