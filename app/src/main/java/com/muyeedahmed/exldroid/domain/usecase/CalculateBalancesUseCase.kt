package com.muyeedahmed.exldroid.domain.usecase

import com.muyeedahmed.exldroid.domain.model.CreditCard
import com.muyeedahmed.exldroid.domain.model.Expense
import com.muyeedahmed.exldroid.domain.model.FinancialSummary
import com.muyeedahmed.exldroid.domain.model.FutureExpense
import javax.inject.Inject

class CalculateBalancesUseCase @Inject constructor(
    private val calculateCreditAgeUseCase: CalculateCreditAgeUseCase
) {

    operator fun invoke(
        cards: List<CreditCard>,
        expenses: List<Expense>,
        futureExpenses: List<FutureExpense>
    ): FinancialSummary {
        // Group expenses by creditCardId
        val expensesByCard = expenses.groupBy { it.creditCardId }

        var totalCheckingBalance = 0.0
        var totalBrokerageBalance = 0.0
        var totalCreditCardDebt = 0.0

        for (card in cards) {
            val cardExpenses = expensesByCard[card.id] ?: emptyList()
            val sumAmount = cardExpenses.sumOf { it.amount }

            when {
                card.isChecking -> {
                    totalCheckingBalance += sumAmount
                }
                card.isBrokerage -> {
                    totalBrokerageBalance += sumAmount
                }
                card.isSaving -> {
                    // Savings accounts are deposit accounts, can be counted in liquid/deposits
                }
                else -> {
                    // Credit Card: Balance Due = sum(amount).
                    totalCreditCardDebt += sumAmount
                }
            }
        }

        val totalFutureBills = futureExpenses.sumOf { it.amount }
        val netBalance = totalCheckingBalance - totalCreditCardDebt - totalFutureBills

        val (openCards, closedCards) = cards.filter { !it.accountType.isDeposit }.partition { !it.isClosed }
        val avgCreditAge = calculateCreditAgeUseCase.calculateAverageCreditAge(openCards)

        return FinancialSummary(
            netBalance = netBalance,
            totalCheckingBalance = totalCheckingBalance,
            totalCreditCardDebt = totalCreditCardDebt,
            totalFutureExpenses = totalFutureBills,
            totalBrokerageBalance = totalBrokerageBalance,
            averageCreditAge = avgCreditAge,
            openCardsCount = openCards.size,
            closedCardsCount = closedCards.size
        )
    }

    fun calculateSingleCardBalance(card: CreditCard, expenses: List<Expense>): Double {
        val sum = expenses.filter { it.creditCardId == card.id }.sumOf { it.amount }
        return sum
    }
}
