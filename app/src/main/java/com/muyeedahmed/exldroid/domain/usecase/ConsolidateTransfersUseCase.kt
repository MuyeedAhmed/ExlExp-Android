package com.muyeedahmed.exldroid.domain.usecase

import com.muyeedahmed.exldroid.domain.model.CreditCard
import com.muyeedahmed.exldroid.domain.model.DisplayTransaction
import com.muyeedahmed.exldroid.domain.model.Expense
import javax.inject.Inject

class ConsolidateTransfersUseCase @Inject constructor() {

    fun isIncomingTransfer(expense: Expense): Boolean {
        val isTransfer = expense.isTransfer || expense.category.equals("Transfer", ignoreCase = true)
        if (!isTransfer) return false

        val desc = expense.description.lowercase()
        if (desc.startsWith("transfer from")) return true

        // Positive transfer amount is incoming money
        if (expense.amount > 0) return true

        return false
    }

    fun toDisplayTransactions(
        expenses: List<Expense>,
        cards: List<CreditCard>,
        consolidateTransfers: Boolean = true
    ): List<DisplayTransaction> {
        val cardMap = cards.associateBy { it.id }

        return expenses.mapNotNull { expense ->
            val isIncoming = isIncomingTransfer(expense)
            if (consolidateTransfers && isIncoming) {
                return@mapNotNull null
            }

            val card = cardMap[expense.creditCardId]
            val accountName = card?.name ?: "Unknown Account"
            val accountType = card?.accountType ?: com.muyeedahmed.exldroid.domain.model.AccountType.CHECKING
            val isDeposit = accountType.isDeposit

            val icon = when {
                card == null -> "💳"
                card.isSaving -> "💰"
                card.isBrokerage -> "📈"
                card.isChecking -> "🏛️"
                else -> "💳"
            }

            val dateMmDd = if (expense.date.length >= 5) expense.date.substring(5) else expense.date
            val desc = listOfNotNull(
                expense.details?.takeIf { it.isNotBlank() },
                expense.description.takeIf { it.isNotBlank() },
                expense.fromTo?.takeIf { it.isNotBlank() }
            ).firstOrNull()?.trim() ?: "Transaction"

            var formattedAmount = ""
            var isGreen = false
            var isRed = false

            if (isDeposit) {
                if (expense.amount >= 0) {
                    formattedAmount = String.format(java.util.Locale.US, "+$%,.2f", kotlin.math.abs(expense.amount))
                    isGreen = true
                } else {
                    formattedAmount = String.format(java.util.Locale.US, "-$%,.2f", kotlin.math.abs(expense.amount))
                    isRed = true
                }
            } else {
                // Credit Card: payment/credit reduces debt (negative amount)
                if (expense.amount < 0) {
                    formattedAmount = String.format(java.util.Locale.US, "-$%,.2f", kotlin.math.abs(expense.amount))
                    isGreen = true
                } else {
                    formattedAmount = String.format(java.util.Locale.US, "$%,.2f", kotlin.math.abs(expense.amount))
                }
            }

            DisplayTransaction(
                id = expense.id,
                expense = expense,
                accountName = accountName,
                accountType = accountType,
                isIncomingTransfer = isIncoming,
                accountIcon = icon,
                displayAccount = "$icon $accountName",
                description = desc,
                dateMmDd = dateMmDd,
                formattedAmount = formattedAmount,
                isGreenColor = isGreen,
                isRedColor = isRed
            )
        }
    }
}
