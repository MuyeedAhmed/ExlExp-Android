package com.muyeedahmed.exldroid.data.repository

import com.muyeedahmed.exldroid.data.local.dao.CardDao
import com.muyeedahmed.exldroid.data.local.dao.DeletedRecordDao
import com.muyeedahmed.exldroid.data.local.entity.CreditCardEntity
import com.muyeedahmed.exldroid.data.local.entity.DeletedRecordEntity
import com.muyeedahmed.exldroid.domain.model.CreditCard
import com.muyeedahmed.exldroid.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
    private val deletedRecordDao: DeletedRecordDao
) : CardRepository {

    override fun getAllCardsFlow(username: String): Flow<List<CreditCard>> {
        return cardDao.getAllCardsFlow(username).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNonHiddenCardsFlow(username: String): Flow<List<CreditCard>> {
        return cardDao.getNonHiddenCardsFlow(username).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAllCards(username: String): List<CreditCard> {
        return cardDao.getAllCards(username).map { it.toDomain() }
    }

    override suspend fun getCardById(id: String): CreditCard? {
        return cardDao.getCardById(id)?.toDomain()
    }

    override suspend fun saveCard(card: CreditCard) {
        val entity = card.toEntity().copy(
            isSyncDirty = true,
            updatedAt = System.currentTimeMillis()
        )
        cardDao.insertCard(entity)
    }

    override suspend fun saveCards(cards: List<CreditCard>) {
        val entities = cards.map {
            it.toEntity().copy(
                isSyncDirty = true,
                updatedAt = System.currentTimeMillis()
            )
        }
        cardDao.insertCards(entities)
    }

    override suspend fun deleteCard(id: String) {
        cardDao.deleteCard(id)
        deletedRecordDao.insertDeletedRecord(
            DeletedRecordEntity(id = id, tableName = "cards")
        )
    }

    override suspend fun reorderCards(cards: List<CreditCard>) {
        val updatedCards = cards.mapIndexed { index, card ->
            card.toEntity().copy(
                priority = index,
                isSyncDirty = true,
                updatedAt = System.currentTimeMillis()
            )
        }
        cardDao.insertCards(updatedCards)
    }

    private fun CreditCardEntity.toDomain(): CreditCard = CreditCard(
        id = id,
        name = name,
        isChecking = isChecking,
        isSaving = isSaving,
        isBrokerage = isBrokerage,
        isHidden = isHidden,
        priority = priority,
        openDate = openDate,
        username = username,
        isSyncDirty = isSyncDirty,
        updatedAt = updatedAt
    )

    private fun CreditCard.toEntity(): CreditCardEntity = CreditCardEntity(
        id = id,
        name = name,
        isChecking = isChecking,
        isSaving = isSaving,
        isBrokerage = isBrokerage,
        isHidden = isHidden,
        priority = priority,
        openDate = openDate,
        username = username,
        isSyncDirty = isSyncDirty,
        updatedAt = updatedAt
    )
}
