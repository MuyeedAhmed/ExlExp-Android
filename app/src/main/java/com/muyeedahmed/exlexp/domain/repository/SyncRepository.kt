package com.muyeedahmed.exlexp.domain.repository

interface SyncRepository {
    suspend fun syncWithCloud(): Result<Unit>
    suspend fun exportDataAsJson(username: String = "local"): String
    suspend fun getLastSyncTimestamp(): Long?
    suspend fun setLastSyncTimestamp(timestamp: Long)
    suspend fun login(email: String, password: String): Result<String>
    suspend fun signUp(email: String, password: String): Result<String>
    suspend fun logout(): Result<Unit>
    fun getCurrentUsername(): String
}
