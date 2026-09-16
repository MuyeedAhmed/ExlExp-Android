package com.muyeedahmed.exlexp

import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.domain.model.FutureExpense
import com.muyeedahmed.exlexp.domain.usecase.CalculateBalancesUseCase
import com.muyeedahmed.exlexp.domain.usecase.CalculateCreditAgeUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateBalancesUseCaseTest {

    private lateinit var useCase: CalculateBalancesUseCase

    @Before
    fun setup() {
        useCase = CalculateBalancesUseCase(CalculateCreditAgeUseCase())
    }

    @Test
    fun testDepositAndCreditCardBalances() {
        val checking = CreditCard(
            id = "checking-1",
            name = "Chase Checking",
            isChecking = true,
            openDate = "2022-01-01"
        )
        val creditCard1 = CreditCard(
            id = "cc-1",
            name = "Sapphire Reserve",
            isChecking = false,
            openDate = "2023-01-01"
        )
        val creditCard2 = CreditCard(
            id = "cc-2",
            name = "Freedom Flex",
            isChecking = false,
            openDate = "2023-06-01"
        )

        val expenses = listOf(
            // Checking: +2500 paycheck, -1200 rent, -100 utility => balance = 1200
            Expense(id = "1", description = "Paycheck", amount = 2500.0, creditCardId = "checking-1", date = "2026-09-01"),
            Expense(id = "2", description = "Rent", amount = -1200.0, creditCardId = "checking-1", date = "2026-09-02"),
            Expense(id = "3", description = "Utility", amount = -100.0, creditCardId = "checking-1", date = "2026-09-03"),

            // CC 1: +300 flight, +50 dinner, -200 payment => balance due = 150
            Expense(id = "4", description = "Flight", amount = 300.0, creditCardId = "cc-1", date = "2026-09-01"),
            Expense(id = "5", description = "Dinner", amount = 50.0, creditCardId = "cc-1", date = "2026-09-02"),
            Expense(id = "6", description = "Payment", amount = -200.0, creditCardId = "cc-1", date = "2026-09-03"),

            // CC 2: -50 overpayment => balance due = -50 (debt max(0, -50) = 0)
            Expense(id = "7", description = "Refund", amount = -50.0, creditCardId = "cc-2", date = "2026-09-01")
        )

        val futureBills = listOf(
            FutureExpense(id = "f1", description = "Insurance", amount = 150.0)
        )

        val summary = useCase(listOf(checking, creditCard1, creditCard2), expenses, futureBills)

        assertEquals(1200.0, summary.totalCheckingBalance, 0.001)
        assertEquals(150.0, summary.totalCreditCardDebt, 0.001)
        assertEquals(150.0, summary.totalFutureExpenses, 0.001)

        // Net Balance = 1200 (checking) - 150 (debt) - 150 (future) = 900
        assertEquals(900.0, summary.netBalance, 0.001)
    }
}
