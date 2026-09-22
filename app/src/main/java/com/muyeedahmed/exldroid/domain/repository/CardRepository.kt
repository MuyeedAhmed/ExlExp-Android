package com.muyeedahmed.exldroid.domain.repository

import com.muyeedahmed.exldroid.domain.model.CreditCard
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getAllCardsFlow(username: String = "local"): Flow<List<CreditCard>>
    fun getNonHiddenCardsFlow(username: String = "local"): Flow<List<CreditCard>>
    suspend fun getAllCards(username: String = "local"): List<CreditCard>
    suspend fun getCardById(id: String): CreditCard?
    suspend fun saveCard(card: CreditCard)
    suspend fun saveCards(cards: List<CreditCard>)
    suspend fun deleteCard(id: String)
    suspend fun reorderCards(cards: List<CreditCard>)
}
