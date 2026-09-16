package com.muyeedahmed.exlexp.ui.screens.creditcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.CreditCardStats
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.domain.repository.CardRepository
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository
import com.muyeedahmed.exlexp.domain.repository.SyncRepository
import com.muyeedahmed.exlexp.domain.usecase.CalculateCreditAgeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

data class CreditCardsUiState(
    val cards: List<CreditCard> = emptyList(),
    val cardStatsList: List<CreditCardStats> = emptyList(),
    val selectedTabId: String = "overview",
    val selectedCardExpenses: List<Expense> = emptyList(),
    val averageCreditAge: String = "N/A",
    val totalOpenCards: Int = 0,
    val totalClosedCards: Int = 0,
    val totalDebtDue: Double = 0.0,
    val totalLifetimeSpent: Double = 0.0,
    val totalPaymentsMade: Double = 0.0,
    val totalRewardsEarned: Double = 0.0,
    val totalAnnualFeesPaid: Double = 0.0
)

@HiltViewModel
class CreditCardsViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val expenseRepository: ExpenseRepository,
    private val syncRepository: SyncRepository,
    private val calculateCreditAgeUseCase: CalculateCreditAgeUseCase
) : ViewModel() {

    private val _selectedTabId = MutableStateFlow("overview")
    val selectedTabId: StateFlow<String> = _selectedTabId.asStateFlow()

    val uiState: StateFlow<CreditCardsUiState> = combine(
        cardRepository.getNonHiddenCardsFlow(syncRepository.getCurrentUsername()),
        expenseRepository.getAllExpensesFlow(syncRepository.getCurrentUsername()),
        _selectedTabId
    ) { allCards, expenses, currentTabId ->
        val creditCards = allCards.filter { !it.accountType.isDeposit }
        val expensesByCard = expenses.groupBy { it.creditCardId }

        val statsList = creditCards.map { card ->
            val cardExpenses = expensesByCard[card.id] ?: emptyList()

            var spent = 0.0
            var paid = 0.0
            var rewards = 0.0
            var fees = 0.0

            cardExpenses.forEach { e ->
                val amt = e.amount
                if (e.isReward) {
                    rewards += (e.rewardValue ?: kotlin.math.abs(amt))
                    if (amt < 0) {
                        paid += kotlin.math.abs(amt)
                    }
                } else if (e.isFee) {
                    fees += amt
                    spent += amt
                } else if (amt > 0) {
                    spent += amt
                } else if (amt < 0) {
                    paid += kotlin.math.abs(amt)
                }
            }

            val balanceDue = spent - paid
            val age = calculateCreditAgeUseCase.calculateCreditAge(card.openDate)

            CreditCardStats(
                card = card,
                spent = spent,
                paid = paid,
                rewards = rewards,
                fees = fees,
                balanceDue = balanceDue,
                creditAge = age
            )
        }

        val openCards = creditCards.filter { !it.isClosed }
        val closedCards = creditCards.filter { it.isClosed }
        val avgAge = calculateCreditAgeUseCase.calculateAverageCreditAge(openCards)

        val totalDebt = statsList.sumOf { it.balanceDue }
        val totalSpent = statsList.sumOf { it.spent }
        val totalPaid = statsList.sumOf { it.paid }
        val totalRewards = statsList.sumOf { it.rewards }
        val totalFees = statsList.sumOf { it.fees }

        val selectedExpenses = (expensesByCard[currentTabId] ?: emptyList()).sortedByDescending { it.date }

        CreditCardsUiState(
            cards = creditCards,
            cardStatsList = statsList,
            selectedTabId = currentTabId,
            selectedCardExpenses = selectedExpenses,
            averageCreditAge = avgAge,
            totalOpenCards = openCards.size,
            totalClosedCards = closedCards.size,
            totalDebtDue = totalDebt,
            totalLifetimeSpent = totalSpent,
            totalPaymentsMade = totalPaid,
            totalRewardsEarned = totalRewards,
            totalAnnualFeesPaid = totalFees
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CreditCardsUiState()
    )

    fun selectTab(tabId: String) {
        _selectedTabId.value = tabId
    }

    fun updateCardOpenDate(cardId: String, newOpenDate: String) {
        viewModelScope.launch {
            val card = cardRepository.getCardById(cardId) ?: return@launch
            cardRepository.saveCard(card.copy(openDate = newOpenDate))
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id)
        }
    }
}
