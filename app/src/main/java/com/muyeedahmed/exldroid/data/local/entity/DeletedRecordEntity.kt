package com.muyeedahmed.exldroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "deleted_records")
@Serializable
data class DeletedRecordEntity(
    @PrimaryKey
    val id: String,
    val tableName: String,                  // "cards", "expenses", "future_expenses"
    val deletedAt: Long = System.currentTimeMillis()
)
