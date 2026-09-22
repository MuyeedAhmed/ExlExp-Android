package com.muyeedahmed.exldroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "cards")
@Serializable
data class CreditCardEntity(
    @PrimaryKey
    val id: String,                         // e.g. "card-chase", "acc-checking-default"
    val name: String,                       // e.g. "Chase Freedom", "Santander Checking"
    val isChecking: Boolean = false,        // True if Checking account
    val isSaving: Boolean = false,          // True if High-Yield Savings account
    val isBrokerage: Boolean = false,       // True if Investment/Brokerage account
    val isHidden: Boolean = false,          // If true, hidden from selector dropdowns
    val priority: Int = 0,                  // Custom user ordering priority
    val openDate: String,                   // Account opening date (YYYY-MM-DD)
    val username: String = "local",         // Multi-user isolation key
    val isSyncDirty: Boolean = false,       // Local change pending cloud sync
    val updatedAt: Long = System.currentTimeMillis()
)
