package com.muyeedahmed.exlexp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muyeedahmed.exlexp.data.local.entity.DeletedRecordEntity

@Dao
interface DeletedRecordDao {
    @Query("SELECT * FROM deleted_records")
    suspend fun getAllDeletedRecords(): List<DeletedRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedRecord(record: DeletedRecordEntity)

    @Query("DELETE FROM deleted_records WHERE id IN (:ids)")
    suspend fun deleteRecords(ids: List<String>)

    @Query("DELETE FROM deleted_records")
    suspend fun clearAll()
}
