package com.muyeedahmed.exlexp.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.DisplayTransaction
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.domain.model.FinancialSummary
import com.muyeedahmed.exlexp.domain.model.FutureExpense
import com.muyeedahmed.exlexp.domain.model.MonthlyCategoryDistribution
import com.muyeedahmed.exlexp.domain.model.SpendingTrend
import com.muyeedahmed.exlexp.domain.repository.CardRepository
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository
import com.muyeedahmed.exlexp.domain.repository.FutureExpenseRepository
import com.muyeedahmed.exlexp.domain.repository.SyncRepository
import com.muyeedahmed.exlexp.domain.usecase.CalculateBalancesUseCase
import com.muyeedahmed.exlexp.domain.usecase.CalculateRollingTrendUseCase
import com.muyeedahmed.exlexp.domain.usecase.ConsolidateTransfersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class DashboardUiState(
    val summary: FinancialSummary = FinancialSummary(0.0, 0.0, 0.0, 0.0, 0.0, "0 mos", 0, 0),
    val futureExpenses: List<FutureExpense> = emptyList(),
    val spendingTrend: SpendingTrend = SpendingTrend(emptyList(), 0.0, 0.0, 1.0),
    val selectedMonthKey: String = String.format(Locale.US, "%04d-%02d", LocalDate.now().year, LocalDate.now().monthValue),
    val availableMonths: List<String> = emptyList(),
    val categoryDistribution: MonthlyCategoryDistribution = MonthlyCategoryDistribution("", 0.0, emptyList()),
    val recentTransactions: List<DisplayTransaction> = emptyList(),
    val activeCheckingAccounts: List<Pair<CreditCard, Double>> = emptyList(),
    val activeCreditCards: List<Pair<CreditCard, Double>> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val expenseRepository: ExpenseRepository,
    private val futureExpenseRepository: FutureExpenseRepository,
    private val syncRepository: SyncRepository,
    private val calculateBalancesUseCase: CalculateBalancesUseCase,
    private val calculateRollingTrendUseCase: CalculateRollingTrendUseCase,
    private val consolidateTransfersUseCase: ConsolidateTransfersUseCase
) : ViewModel() {

    private val currentMonthKey = String.format(Locale.US, "%04d-%02d", LocalDate.now().year, LocalDate.now().monthValue)
    private val _selectedMonthKey = MutableStateFlow(currentMonthKey)
    val selectedMonthKey: StateFlow<String> = _selectedMonthKey.asStateFlow()

    val uiState: StateFlow<DashboardUiState> = combine(
        cardRepository.getAllCardsFlow(syncRepository.getCurrentUsername()),
        expenseRepository.getAllExpensesFlow(syncRepository.getCurrentUsername()),
        futureExpenseRepository.getAllFutureExpensesFlow(syncRepository.getCurrentUsername()),
        _selectedMonthKey
    ) { cards, expenses, futureExpenses, selMonthKey ->
        val summary = calculateBalancesUseCase(cards, expenses, futureExpenses)
        val trend = calculateRollingTrendUseCase.calculate12MonthTrend(cards, expenses)
        val catDist = calculateRollingTrendUseCase.calculateCategoryDistribution(selMonthKey, cards, expenses)

        // Recent 10 consolidated transactions
        val displayTransactions = consolidateTransfersUseCase.toDisplayTransactions(
            expenses = expenses,
            cards = cards,
            consolidateTransfers = true
        )
        val recent10 = displayTransactions.take(10)

        // Active accounts with non-zero balances
        val expensesByCard = expenses.groupBy { it.creditCardId }
        val checkingList = mutableListOf<Pair<CreditCard, Double>>()
        val cardList = mutableListOf<Pair<CreditCard, Double>>()

        for (c in cards) {
            val balance = (expensesByCard[c.id] ?: emptyList()).sumOf { it.amount }
            if (c.isChecking) {
                if (kotlin.math.abs(balance) >= 0.005) {
                    checkingList.add(Pair(c, balance))
                }
            } else if (!c.accountType.isDeposit) {
                if (kotlin.math.abs(balance) >= 0.005) {
                    cardList.add(Pair(c, balance))
                }
            }
        }

        DashboardUiState(
            summary = summary,
            futureExpenses = futureExpenses,
            spendingTrend = trend,
            selectedMonthKey = selMonthKey,
            availableMonths = trend.monthlySpends.map { it.monthKey }.reversed(),
            categoryDistribution = catDist,
            recentTransactions = recent10,
            activeCheckingAccounts = checkingList,
            activeCreditCards = cardList,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun selectMonth(monthKey: String) {
        _selectedMonthKey.value = monthKey
    }

    fun addFutureExpense(description: String, amount: Double, dueDate: String?) {
        viewModelScope.launch {
            val bill = FutureExpense(
                id = "fut-${UUID.randomUUID()}",
                description = description.trim(),
                amount = amount,
                dueDate = dueDate?.ifBlank { null },
                username = syncRepository.getCurrentUsername(),
                isSyncDirty = true
            )
            futureExpenseRepository.saveFutureExpense(bill)
        }
    }

    fun deleteFutureExpense(id: String) {
        viewModelScope.launch {
            futureExpenseRepository.deleteFutureExpense(id)
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id)
        }
    }
}
