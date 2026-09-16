package com.muyeedahmed.exlexp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.muyeedahmed.exlexp.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE username = :username ORDER BY date DESC, id DESC")
    fun getAllExpensesFlow(username: String = "local"): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE username = :username ORDER BY date DESC, id DESC")
    suspend fun getAllExpenses(username: String = "local"): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE creditCardId = :cardId AND username = :username ORDER BY date DESC, id DESC")
    fun getExpensesForCardFlow(cardId: String, username: String = "local"): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE creditCardId = :cardId AND username = :username ORDER BY date DESC, id DESC")
    suspend fun getExpensesForCard(cardId: String, username: String = "local"): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE username = :username ORDER BY date DESC, id DESC")
    fun getPagedExpenses(username: String = "local"): PagingSource<Int, ExpenseEntity>

    @Query("""
        SELECT * FROM expenses 
        WHERE username = :username 
        AND (
            description LIKE '%' || :query || '%' 
            OR category LIKE '%' || :query || '%' 
            OR details LIKE '%' || :query || '%'
            OR fromTo LIKE '%' || :query || '%'
            OR date LIKE '%' || :query || '%'
        )
        ORDER BY date DESC, id DESC
    """)
    fun searchExpensesPaged(username: String = "local", query: String): PagingSource<Int, ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: String): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE transferLinkId = :transferLinkId")
    suspend fun getExpensesByTransferLinkId(transferLinkId: String): List<ExpenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: String)

    @Query("DELETE FROM expenses WHERE transferLinkId = :transferLinkId")
    suspend fun deleteByTransferLinkId(transferLinkId: String)

    @Query("SELECT * FROM expenses WHERE isSyncDirty = 1")
    suspend fun getDirtyExpenses(): List<ExpenseEntity>

    @Query("UPDATE expenses SET isSyncDirty = 0 WHERE id IN (:ids)")
    suspend fun markClean(ids: List<String>)
}
