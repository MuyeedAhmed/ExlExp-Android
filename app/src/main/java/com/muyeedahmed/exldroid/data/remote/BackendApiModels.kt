package com.muyeedahmed.exldroid.data.remote

import kotlinx.serialization.Serializable
import com.muyeedahmed.exldroid.data.local.entity.CreditCardEntity
import com.muyeedahmed.exldroid.data.local.entity.ExpenseEntity
import com.muyeedahmed.exldroid.data.local.entity.FutureExpenseEntity

@Serializable
data class SyncPushRequest(
    val username: String? = null,
    val lastSyncTimestamp: Long? = null,
    val deletedCardIds: List<String> = emptyList(),
    val deletedExpenseIds: List<String> = emptyList(),
    val deletedFutureExpenseIds: List<String> = emptyList(),
    val cards: List<CreditCardEntity> = emptyList(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val futureExpenses: List<FutureExpenseEntity> = emptyList()
)

@Serializable
data class SyncResponseDto(
    val status: String = "success",
    val serverTimestamp: Long,
    val cards: List<CreditCardEntity> = emptyList(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val futureExpenses: List<FutureExpenseEntity> = emptyList(),
    val deletedCardIds: List<String> = emptyList(),
    val deletedExpenseIds: List<String> = emptyList(),
    val deletedFutureExpenseIds: List<String> = emptyList(),
    val message: String = ""
)

@Serializable
data class LoginRequestDto(
    val username_or_email: String,
    val password: String
)

@Serializable
data class SignUpRequestDto(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class TokenResponseDto(
    val access_token: String,
    val token_type: String = "bearer",
    val username: String,
    val email: String
)

@Serializable
data class BackendHealthDto(
    val status: String,
    val app_name: String? = null,
    val version: String? = null
)
