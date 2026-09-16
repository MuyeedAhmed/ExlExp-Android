package com.muyeedahmed.exlexp.ui.screens.logexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.domain.repository.CardRepository
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository
import com.muyeedahmed.exlexp.domain.repository.SyncRepository
import com.muyeedahmed.exlexp.domain.usecase.DualLegTransferUseCase
import com.muyeedahmed.exlexp.domain.usecase.ParseZelleDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs

enum class ModalMode {
    TRANSACTION,
    TRANSFER
}

data class LogExpenseUiState(
    val cards: List<CreditCard> = emptyList(),
    val isEditing: Boolean = false,
    val editExpenseId: String? = null
)

@HiltViewModel
class LogExpenseViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val expenseRepository: ExpenseRepository,
    private val syncRepository: SyncRepository,
    private val dualLegTransferUseCase: DualLegTransferUseCase,
    private val parseZelleDetailsUseCase: ParseZelleDetailsUseCase
) : ViewModel() {

    val nonHiddenCards: StateFlow<List<CreditCard>> = cardRepository
        .getNonHiddenCardsFlow(syncRepository.getCurrentUsername())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun logStandardExpense(
        card: CreditCard,
        description: String,
        amount: Double,
        date: String,
        category: String,
        fromTo: String? = null,
        details: String? = null,
        isFee: Boolean = false,
        isReward: Boolean = false,
        rewardType: String? = null,
        rewardValue: Double? = null,
        isInterest: Boolean = false,
        isDepositInflow: Boolean = false,
        editId: String? = null
    ) {
        viewModelScope.launch {
            val username = syncRepository.getCurrentUsername()
            val parsedAmount = abs(amount)

            // Determine sign of amount:
            // Deposit account: inflow (+), outflow (-)
            // Credit card: spend/charge/fee (+), payment/reward credit (-)
            val signedAmount = if (card.accountType.isDeposit) {
                if (isDepositInflow) parsedAmount else -parsedAmount
            } else {
                if (isReward) -parsedAmount else parsedAmount
            }

            val expense = Expense(
                id = editId ?: "exp-${UUID.randomUUID()}",
                description = description.trim(),
                amount = signedAmount,
                creditCardId = card.id,
                date = date,
                fromTo = fromTo,
                details = details,
                isFee = isFee,
                isReward = isReward,
                rewardType = rewardType,
                rewardValue = rewardValue,
                isInterest = isInterest,
                category = category.trim().ifBlank { "Others" },
                username = username,
                isSyncDirty = true,
                updatedAt = System.currentTimeMillis()
            )

            expenseRepository.saveExpense(expense)
        }
    }

    fun logZelleExpense(
        card: CreditCard,
        direction: String,
        name: String,
        memo: String?,
        amount: Double,
        date: String,
        category: String,
        editId: String? = null
    ) {
        viewModelScope.launch {
            val username = syncRepository.getCurrentUsername()
            val zelleFormatted = parseZelleDetailsUseCase.formatZelle(
                direction = direction,
                name = name,
                memo = memo,
                rawAmount = amount
            )

            val expense = Expense(
                id = editId ?: "exp-${UUID.randomUUID()}",
                description = zelleFormatted.description,
                amount = zelleFormatted.amount,
                creditCardId = card.id,
                date = date,
                fromTo = zelleFormatted.fromTo,
                details = zelleFormatted.details,
                category = category.trim().ifBlank { "Others" },
                username = username,
                isSyncDirty = true,
                updatedAt = System.currentTimeMillis()
            )

            expenseRepository.saveExpense(expense)
        }
    }

    fun logTransfer(
        sourceCard: CreditCard,
        targetCard: CreditCard,
        amount: Double,
        date: String,
        memo: String?,
        isCcBillPay: Boolean,
        oldTransferLinkId: String? = null
    ) {
        viewModelScope.launch {
            val username = syncRepository.getCurrentUsername()
            if (oldTransferLinkId != null) {
                val allExpenses = expenseRepository.getAllExpenses(username)
                val oldLegs = allExpenses.filter { it.transferLinkId == oldTransferLinkId }
                oldLegs.forEach { expenseRepository.deleteExpense(it.id) }
            }

            val (sourceLeg, targetLeg) = dualLegTransferUseCase.createTransferLegs(
                sourceCard = sourceCard,
                targetCard = targetCard,
                amount = amount,
                date = date,
                memo = memo,
                isCcBillPay = isCcBillPay,
                username = username
            )

            expenseRepository.saveTransfer(sourceLeg, targetLeg)
        }
    }
}
