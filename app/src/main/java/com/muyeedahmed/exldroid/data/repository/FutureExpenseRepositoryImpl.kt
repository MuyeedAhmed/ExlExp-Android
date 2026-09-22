package com.muyeedahmed.exldroid.data.repository

import com.muyeedahmed.exldroid.data.local.dao.DeletedRecordDao
import com.muyeedahmed.exldroid.data.local.dao.FutureExpenseDao
import com.muyeedahmed.exldroid.data.local.entity.DeletedRecordEntity
import com.muyeedahmed.exldroid.data.local.entity.FutureExpenseEntity
import com.muyeedahmed.exldroid.domain.model.FutureExpense
import com.muyeedahmed.exldroid.domain.repository.FutureExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FutureExpenseRepositoryImpl @Inject constructor(
    private val futureExpenseDao: FutureExpenseDao,
    private val deletedRecordDao: DeletedRecordDao
) : FutureExpenseRepository {

    override fun getAllFutureExpensesFlow(username: String): Flow<List<FutureExpense>> {
        return futureExpenseDao.getAllFutureExpensesFlow(username).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAllFutureExpenses(username: String): List<FutureExpense> {
        return futureExpenseDao.getAllFutureExpenses(username).map { it.toDomain() }
    }

    override suspend fun saveFutureExpense(bill: FutureExpense) {
        futureExpenseDao.insertFutureExpense(
            bill.toEntity().copy(isSyncDirty = true)
        )
    }

    override suspend fun deleteFutureExpense(id: String) {
        futureExpenseDao.deleteFutureExpense(id)
        deletedRecordDao.insertDeletedRecord(
            DeletedRecordEntity(id = id, tableName = "future_expenses")
        )
    }

    private fun FutureExpenseEntity.toDomain(): FutureExpense = FutureExpense(
        id = id,
        description = description,
        amount = amount,
        dueDate = dueDate,
        username = username,
        isSyncDirty = isSyncDirty
    )

    private fun FutureExpense.toEntity(): FutureExpenseEntity = FutureExpenseEntity(
        id = id,
        description = description,
        amount = amount,
        dueDate = dueDate,
        username = username,
        isSyncDirty = isSyncDirty
    )
}
