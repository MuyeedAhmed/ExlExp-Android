package com.muyeedahmed.exldroid.domain.repository

import androidx.paging.PagingData
import com.muyeedahmed.exldroid.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpensesFlow(username: String = "local"): Flow<List<Expense>>
    suspend fun getAllExpenses(username: String = "local"): List<Expense>
    fun getExpensesForCardFlow(cardId: String, username: String = "local"): Flow<List<Expense>>
    suspend fun getExpensesForCard(cardId: String, username: String = "local"): List<Expense>
    fun getPagedExpenses(username: String = "local"): Flow<PagingData<Expense>>
    fun searchPagedExpenses(username: String = "local", query: String): Flow<PagingData<Expense>>
    suspend fun getExpenseById(id: String): Expense?
    suspend fun saveExpense(expense: Expense)
    suspend fun saveTransfer(sourceExpense: Expense, targetExpense: Expense)
    suspend fun deleteExpense(id: String)
}
