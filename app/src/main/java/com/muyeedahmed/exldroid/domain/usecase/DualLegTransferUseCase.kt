package com.muyeedahmed.exldroid.domain.usecase

import com.muyeedahmed.exldroid.domain.model.CreditCard
import com.muyeedahmed.exldroid.domain.model.Expense
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs

class DualLegTransferUseCase @Inject constructor() {

    fun createTransferLegs(
        sourceCard: CreditCard,
        targetCard: CreditCard,
        amount: Double,
        date: String,
        memo: String? = null,
        isCcBillPay: Boolean = false,
        username: String = "local"
    ): Pair<Expense, Expense> {
        val transferId = "tr-${UUID.randomUUID()}"
        val parsedAmount = abs(amount)

        val transferMemo = if (memo.isNullOrBlank()) null else memo.trim()
        val detailsText = if (isCcBillPay) {
            "Credit Card Bill Pay - ${targetCard.name}"
        } else {
            transferMemo
        }

        // 1. Source Leg (Sender)
        val sourceExpense = Expense(
            id = "exp-${UUID.randomUUID()}",
            description = "Transfer to ${targetCard.name}",
            amount = -parsedAmount,
            creditCardId = sourceCard.id,
            date = date,
            fromTo = targetCard.name,
            details = detailsText,
            isTransfer = true,
            transferLinkId = transferId,
            category = "Transfer",
            username = username,
            isSyncDirty = true,
            updatedAt = System.currentTimeMillis()
        )

        // 2. Target Leg (Receiver)
        val targetIsDeposit = targetCard.accountType.isDeposit
        val targetAmount = if (targetIsDeposit) {
            +parsedAmount
        } else {
            // For credit cards, payments reduce balance due (negative)
            -parsedAmount
        }

        val targetExpense = Expense(
            id = "exp-${UUID.randomUUID()}",
            description = "Transfer from ${sourceCard.name}",
            amount = targetAmount,
            creditCardId = targetCard.id,
            date = date,
            fromTo = sourceCard.name,
            details = detailsText,
            isTransfer = true,
            transferLinkId = transferId,
            category = "Transfer",
            username = username,
            isSyncDirty = true,
            updatedAt = System.currentTimeMillis()
        )

        return Pair(sourceExpense, targetExpense)
    }
}
