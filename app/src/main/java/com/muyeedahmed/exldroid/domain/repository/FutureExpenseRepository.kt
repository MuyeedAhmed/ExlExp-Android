package com.muyeedahmed.exldroid.domain.repository

import com.muyeedahmed.exldroid.domain.model.FutureExpense
import kotlinx.coroutines.flow.Flow

interface FutureExpenseRepository {
    fun getAllFutureExpensesFlow(username: String = "local"): Flow<List<FutureExpense>>
    suspend fun getAllFutureExpenses(username: String = "local"): List<FutureExpense>
    suspend fun saveFutureExpense(bill: FutureExpense)
    suspend fun deleteFutureExpense(id: String)
}
