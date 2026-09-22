package com.muyeedahmed.exldroid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.muyeedahmed.exldroid.data.local.entity.FutureExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FutureExpenseDao {
    @Query("SELECT * FROM future_expenses WHERE username = :username ORDER BY dueDate ASC, id DESC")
    fun getAllFutureExpensesFlow(username: String = "local"): Flow<List<FutureExpenseEntity>>

    @Query("SELECT * FROM future_expenses WHERE username = :username ORDER BY dueDate ASC, id DESC")
    suspend fun getAllFutureExpenses(username: String = "local"): List<FutureExpenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFutureExpense(bill: FutureExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFutureExpenses(bills: List<FutureExpenseEntity>)

    @Update
    suspend fun updateFutureExpense(bill: FutureExpenseEntity)

    @Query("DELETE FROM future_expenses WHERE id = :id")
    suspend fun deleteFutureExpense(id: String)

    @Query("SELECT * FROM future_expenses WHERE isSyncDirty = 1")
    suspend fun getDirtyFutureExpenses(): List<FutureExpenseEntity>

    @Query("UPDATE future_expenses SET isSyncDirty = 0 WHERE id IN (:ids)")
    suspend fun markClean(ids: List<String>)
}
