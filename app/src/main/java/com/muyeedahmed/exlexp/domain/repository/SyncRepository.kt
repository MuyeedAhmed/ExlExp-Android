package com.muyeedahmed.exlexp.domain.repository

data class ImportResult(
    val cardsCount: Int,
    val expensesCount: Int,
    val futureExpensesCount: Int
)

interface SyncRepository {
    suspend fun syncWithCloud(): Result<Unit>
    suspend fun exportDataAsJson(username: String = "local"): String
    suspend fun importDataFromJson(jsonString: String, targetUsername: String = getCurrentUsername()): Result<ImportResult>
    suspend fun getLastSyncTimestamp(): Long?
    suspend fun setLastSyncTimestamp(timestamp: Long)
    suspend fun login(email: String, password: String): Result<String>
    suspend fun signUp(email: String, password: String): Result<String>
    suspend fun logout(): Result<Unit>
    fun getCurrentUsername(): String
}
