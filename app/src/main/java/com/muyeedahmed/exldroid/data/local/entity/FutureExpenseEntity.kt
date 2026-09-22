package com.muyeedahmed.exldroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "future_expenses")
@Serializable
data class FutureExpenseEntity(
    @PrimaryKey
    val id: String,                         // e.g. "fut-abc123"
    val description: String,                // e.g. "Car Insurance Premium"
    val amount: Double,                     // Dollar amount
    val dueDate: String? = null,            // Optional ISO date (YYYY-MM-DD)
    val username: String = "local",
    val isSyncDirty: Boolean = false
)
