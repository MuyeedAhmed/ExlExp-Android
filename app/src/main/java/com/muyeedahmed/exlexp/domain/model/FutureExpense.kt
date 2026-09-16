package com.muyeedahmed.exlexp.domain.model

data class FutureExpense(
    val id: String,
    val description: String,
    val amount: Double,
    val dueDate: String? = null,
    val username: String = "local",
    val isSyncDirty: Boolean = false
)
