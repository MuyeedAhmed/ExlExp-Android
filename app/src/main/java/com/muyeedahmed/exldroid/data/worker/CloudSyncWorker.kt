package com.muyeedahmed.exldroid.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.muyeedahmed.exldroid.domain.repository.SyncRepository
import javax.inject.Inject

class CloudSyncWorker(
    appContext: Context,
    params: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val result = syncRepository.syncWithCloud()
            if (result.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
