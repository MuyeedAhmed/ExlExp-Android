package com.muyeedahmed.exlexp.ui.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.domain.repository.CardRepository
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository
import com.muyeedahmed.exlexp.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class AccountsUiState(
    val checkingAccounts: List<CreditCard> = emptyList(),
    val savingsAccounts: List<CreditCard> = emptyList(),
    val brokerageAccounts: List<CreditCard> = emptyList(),
    val selectedTabId: String = "",
    val accountExpenses: List<Expense> = emptyList(),
    val accountBalance: Double = 0.0,
    val brokerageBalances: Map<String, Double> = emptyMap()
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val expenseRepository: ExpenseRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _selectedTabId = MutableStateFlow("")
    val selectedTabId: StateFlow<String> = _selectedTabId.asStateFlow()

    val uiState: StateFlow<AccountsUiState> = combine(
        cardRepository.getNonHiddenCardsFlow(syncRepository.getCurrentUsername()),
        expenseRepository.getAllExpensesFlow(syncRepository.getCurrentUsername()),
        _selectedTabId
    ) { cards, expenses, currentTabId ->
        val checking = cards.filter { it.isChecking }
        val savings = cards.filter { it.isSaving }
        val brokerage = cards.filter { it.isBrokerage }

        // Default tab selection if empty
        val activeTabId = if (currentTabId.isEmpty()) {
            checking.firstOrNull()?.id
                ?: savings.firstOrNull()?.id
                ?: if (brokerage.isNotEmpty()) "brokerage_portfolio" else ""
        } else {
            currentTabId
        }

        val expensesByCard = expenses.groupBy { it.creditCardId }
        val selectedExpenses = (expensesByCard[activeTabId] ?: emptyList()).sortedByDescending { it.date }
        val balance = selectedExpenses.sumOf { it.amount }

        val brokBalances = brokerage.associate { b ->
            b.id to ((expensesByCard[b.id] ?: emptyList()).sumOf { it.amount })
        }

        AccountsUiState(
            checkingAccounts = checking,
            savingsAccounts = savings,
            brokerageAccounts = brokerage,
            selectedTabId = activeTabId,
            accountExpenses = selectedExpenses,
            accountBalance = balance,
            brokerageBalances = brokBalances
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AccountsUiState()
    )

    fun selectTab(tabId: String) {
        _selectedTabId.value = tabId
    }

    fun updateBrokerageBalance(cardId: String, newBalance: Double) {
        viewModelScope.launch {
            val username = syncRepository.getCurrentUsername()
            val existingExpenses = expenseRepository.getExpensesForCard(cardId, username)
            val currentBalanceTx = existingExpenses.find { it.description == "Current Balance" }
            if (currentBalanceTx != null) {
                val otherSum = existingExpenses.filter { it.id != currentBalanceTx.id }.sumOf { it.amount }
                val newAmount = newBalance - otherSum
                expenseRepository.saveExpense(
                    currentBalanceTx.copy(
                        amount = newAmount,
                        date = LocalDate.now().toString(),
                        isSyncDirty = true
                    )
                )
            } else if (existingExpenses.isEmpty()) {
                expenseRepository.saveExpense(
                    Expense(
                        id = "exp-${UUID.randomUUID()}",
                        description = "Current Balance",
                        amount = newBalance,
                        creditCardId = cardId,
                        date = LocalDate.now().toString(),
                        fromTo = "Imported Balance",
                        details = "Calculated from transfer logs",
                        category = "Investment",
                        username = username,
                        isSyncDirty = true
                    )
                )
            } else {
                val currentSum = existingExpenses.sumOf { it.amount }
                val delta = newBalance - currentSum
                if (kotlin.math.abs(delta) > 0.001) {
                    expenseRepository.saveExpense(
                        Expense(
                            id = "exp-${UUID.randomUUID()}",
                            description = "Current Balance",
                            amount = delta,
                            creditCardId = cardId,
                            date = LocalDate.now().toString(),
                            fromTo = "Adjustment",
                            category = "Investment",
                            username = username,
                            isSyncDirty = true
                        )
                    )
                }
            }
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id)
        }
    }
}
