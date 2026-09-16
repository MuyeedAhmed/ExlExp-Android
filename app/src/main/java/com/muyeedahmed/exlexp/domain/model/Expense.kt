package com.muyeedahmed.exlexp.domain.model

data class Expense(
    val id: String,
    val description: String,
    val amount: Double,
    val creditCardId: String,
    val date: String,
    val fromTo: String? = null,
    val details: String? = null,
    val isFee: Boolean = false,
    val isReward: Boolean = false,
    val rewardType: String? = null,
    val rewardValue: Double? = null,
    val isTransfer: Boolean = false,
    val transferLinkId: String? = null,
    val isInterest: Boolean = false,
    val category: String = "Others",
    val username: String = "local",
    val isSyncDirty: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
