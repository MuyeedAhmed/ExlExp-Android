package com.muyeedahmed.exldroid.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "expenses",
    indices = [
        Index("creditCardId"),
        Index("date"),
        Index("username"),
        Index("category"),
        Index("transferLinkId")
    ]
)
@Serializable
data class ExpenseEntity(
    @PrimaryKey
    val id: String,                         // e.g. "exp-abc123xyz"
    val description: String,                // Merchant or transaction description
    val amount: Double,                     // Signed numeric amount
    val creditCardId: String,               // Foreign key referencing cards.id
    val date: String,                       // ISO date (YYYY-MM-DD)
    val fromTo: String? = null,             // Payee/Payer for checking/savings accounts
    val details: String? = null,            // Additional notes, Zelle info, bill pay note
    val isFee: Boolean = false,             // True if Annual Fee or Account Fee
    val isReward: Boolean = false,          // True if Cashback / Reward redemption
    val rewardType: String? = null,         // "cashback" | "other" (miles/points)
    val rewardValue: Double? = null,        // Reward value in dollars or points
    val isTransfer: Boolean = false,        // True if this is an inter-account transfer
    val transferLinkId: String? = null,     // Shared GUID linking two transfer legs
    val isInterest: Boolean = false,        // True if HYSA Interest earned
    val category: String = "Others",        // Standardized category
    val username: String = "local",         // User identifier
    val isSyncDirty: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
