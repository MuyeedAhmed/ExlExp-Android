package com.muyeedahmed.exlexp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.muyeedahmed.exlexp.data.local.entity.CreditCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE username = :username ORDER BY priority ASC, name ASC")
    fun getAllCardsFlow(username: String = "local"): Flow<List<CreditCardEntity>>

    @Query("SELECT * FROM cards WHERE username = :username ORDER BY priority ASC, name ASC")
    suspend fun getAllCards(username: String = "local"): List<CreditCardEntity>

    @Query("SELECT * FROM cards WHERE username = :username AND isHidden = 0 ORDER BY priority ASC, name ASC")
    fun getNonHiddenCardsFlow(username: String = "local"): Flow<List<CreditCardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id LIMIT 1")
    suspend fun getCardById(id: String): CreditCardEntity?

    @Query("SELECT COUNT(*) FROM cards")
    suspend fun getCardCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CreditCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CreditCardEntity>)

    @Update
    suspend fun updateCard(card: CreditCardEntity)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCard(id: String)

    @Query("SELECT * FROM cards WHERE isSyncDirty = 1")
    suspend fun getDirtyCards(): List<CreditCardEntity>

    @Query("UPDATE cards SET isSyncDirty = 0 WHERE id IN (:ids)")
    suspend fun markClean(ids: List<String>)
}
