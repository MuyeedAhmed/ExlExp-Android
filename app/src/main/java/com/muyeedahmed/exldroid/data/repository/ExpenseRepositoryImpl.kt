package com.muyeedahmed.exldroid.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.muyeedahmed.exldroid.data.local.dao.DeletedRecordDao
import com.muyeedahmed.exldroid.data.local.dao.ExpenseDao
import com.muyeedahmed.exldroid.data.local.entity.DeletedRecordEntity
import com.muyeedahmed.exldroid.data.local.entity.ExpenseEntity
import com.muyeedahmed.exldroid.domain.model.Expense
import com.muyeedahmed.exldroid.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val deletedRecordDao: DeletedRecordDao
) : ExpenseRepository {

    override fun getAllExpensesFlow(username: String): Flow<List<Expense>> {
        return expenseDao.getAllExpensesFlow(username).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAllExpenses(username: String): List<Expense> {
        return expenseDao.getAllExpenses(username).map { it.toDomain() }
    }

    override fun getExpensesForCardFlow(cardId: String, username: String): Flow<List<Expense>> {
        return expenseDao.getExpensesForCardFlow(cardId, username).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getExpensesForCard(cardId: String, username: String): List<Expense> {
        return expenseDao.getExpensesForCard(cardId, username).map { it.toDomain() }
    }

    override fun getPagedExpenses(username: String): Flow<PagingData<Expense>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { expenseDao.getPagedExpenses(username) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun searchPagedExpenses(username: String, query: String): Flow<PagingData<Expense>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { expenseDao.searchExpensesPaged(username, query) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getExpenseById(id: String): Expense? {
        return expenseDao.getExpenseById(id)?.toDomain()
    }

    override suspend fun saveExpense(expense: Expense) {
        val entity = expense.toEntity().copy(
            isSyncDirty = true,
            updatedAt = System.currentTimeMillis()
        )
        expenseDao.insertExpense(entity)
    }

    override suspend fun saveTransfer(sourceExpense: Expense, targetExpense: Expense) {
        val now = System.currentTimeMillis()
        val sourceEntity = sourceExpense.toEntity().copy(isSyncDirty = true, updatedAt = now)
        val targetEntity = targetExpense.toEntity().copy(isSyncDirty = true, updatedAt = now)
        expenseDao.insertExpenses(listOf(sourceEntity, targetEntity))
    }

    override suspend fun deleteExpense(id: String) {
        val existing = expenseDao.getExpenseById(id) ?: return
        if (!existing.transferLinkId.isNullOrBlank()) {
            val linked = expenseDao.getExpensesByTransferLinkId(existing.transferLinkId)
            expenseDao.deleteByTransferLinkId(existing.transferLinkId)
            for (item in linked) {
                deletedRecordDao.insertDeletedRecord(
                    DeletedRecordEntity(id = item.id, tableName = "expenses")
                )
            }
        } else {
            expenseDao.deleteExpenseById(id)
            deletedRecordDao.insertDeletedRecord(
                DeletedRecordEntity(id = id, tableName = "expenses")
            )
        }
    }

    private fun ExpenseEntity.toDomain(): Expense = Expense(
        id = id,
        description = description,
        amount = amount,
        creditCardId = creditCardId,
        date = date,
        fromTo = fromTo,
        details = details,
        isFee = isFee,
        isReward = isReward,
        rewardType = rewardType,
        rewardValue = rewardValue,
        isTransfer = isTransfer,
        transferLinkId = transferLinkId,
        isInterest = isInterest,
        category = category,
        username = username,
        isSyncDirty = isSyncDirty,
        updatedAt = updatedAt
    )

    private fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
        id = id,
        description = description,
        amount = amount,
        creditCardId = creditCardId,
        date = date,
        fromTo = fromTo,
        details = details,
        isFee = isFee,
        isReward = isReward,
        rewardType = rewardType,
        rewardValue = rewardValue,
        isTransfer = isTransfer,
        transferLinkId = transferLinkId,
        isInterest = isInterest,
        category = category,
        username = username,
        isSyncDirty = isSyncDirty,
        updatedAt = updatedAt
    )
}
