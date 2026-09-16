package com.muyeedahmed.exlexp.ui.screens.alltransactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muyeedahmed.exlexp.domain.model.AccountType
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.DisplayTransaction
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.domain.repository.CardRepository
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository
import com.muyeedahmed.exlexp.domain.repository.SyncRepository
import com.muyeedahmed.exlexp.domain.usecase.ConsolidateTransfersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TransactionFilter {
    ALL,
    BANK_AND_INVEST,
    CREDIT_CARDS,
    TRANSFERS
}

data class AllTransactionsUiState(
    val transactions: List<DisplayTransaction> = emptyList(),
    val totalCount: Int = 0,
    val searchQuery: String = "",
    val activeFilter: TransactionFilter = TransactionFilter.ALL,
    val isLoading: Boolean = false
)

@HiltViewModel
class AllTransactionsViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val expenseRepository: ExpenseRepository,
    private val syncRepository: SyncRepository,
    private val consolidateTransfersUseCase: ConsolidateTransfersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(TransactionFilter.ALL)
    val filter: StateFlow<TransactionFilter> = _filter.asStateFlow()

    val uiState: StateFlow<AllTransactionsUiState> = combine(
        cardRepository.getAllCardsFlow(syncRepository.getCurrentUsername()),
        expenseRepository.getAllExpensesFlow(syncRepository.getCurrentUsername()),
        _searchQuery,
        _filter
    ) { cards, expenses, query, currentFilter ->
        val cardMap = cards.associateBy { it.id }

        // Consolidate transfers: only outgoing leg
        val displayList = consolidateTransfersUseCase.toDisplayTransactions(
            expenses = expenses,
            cards = cards,
            consolidateTransfers = true
        )

        val totalCount = displayList.size

        // Filter by category / account type
        val filteredByType = when (currentFilter) {
            TransactionFilter.ALL -> displayList
            TransactionFilter.BANK_AND_INVEST -> displayList.filter { !it.isTransfer && it.accountType.isDeposit }
            TransactionFilter.CREDIT_CARDS -> displayList.filter { !it.isTransfer && !it.accountType.isDeposit }
            TransactionFilter.TRANSFERS -> displayList.filter { it.isTransfer }
        }

        // Search filter
        val searchFiltered = if (query.isBlank()) {
            filteredByType
        } else {
            val q = query.trim().lowercase()
            filteredByType.filter { tx ->
                tx.expense.description.lowercase().contains(q) ||
                        tx.accountName.lowercase().contains(q) ||
                        tx.expense.category.lowercase().contains(q) ||
                        (tx.expense.details?.lowercase()?.contains(q) == true) ||
                        tx.expense.date.contains(q) ||
                        tx.expense.amount.toString().contains(q) ||
                        tx.formattedAmount.lowercase().contains(q)
            }
        }

        AllTransactionsUiState(
            transactions = searchFiltered,
            totalCount = totalCount,
            searchQuery = query,
            activeFilter = currentFilter,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AllTransactionsUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterChange(newFilter: TransactionFilter) {
        _filter.value = newFilter
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id)
        }
    }
}
