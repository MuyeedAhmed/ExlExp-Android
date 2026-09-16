package com.muyeedahmed.exlexp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.muyeedahmed.exlexp.data.local.dao.CardDao
import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao
import com.muyeedahmed.exlexp.data.local.dao.ExpenseDao
import com.muyeedahmed.exlexp.data.local.dao.FutureExpenseDao
import com.muyeedahmed.exlexp.data.local.entity.CreditCardEntity
import com.muyeedahmed.exlexp.data.local.entity.ExpenseEntity
import com.muyeedahmed.exlexp.data.local.entity.FutureExpenseEntity
import com.muyeedahmed.exlexp.data.remote.SupabaseClientProvider
import com.muyeedahmed.exlexp.domain.repository.ImportResult
import com.muyeedahmed.exlexp.domain.repository.SyncRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BackupPayload(
    val exportDate: String,
    val username: String,
    val cards: List<CreditCardEntity>,
    val expenses: List<ExpenseEntity>,
    val futureExpenses: List<FutureExpenseEntity>
)

@Singleton
class SyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabaseProvider: SupabaseClientProvider,
    private val cardDao: CardDao,
    private val expenseDao: ExpenseDao,
    private val futureExpenseDao: FutureExpenseDao,
    private val deletedRecordDao: DeletedRecordDao
) : SyncRepository {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("exlexp_prefs", Context.MODE_PRIVATE)
    }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override fun getCurrentUsername(): String {
        return prefs.getString("current_username", "local") ?: "local"
    }

    fun setCurrentUsername(username: String) {
        prefs.edit().putString("current_username", username).apply()
    }

    override suspend fun getLastSyncTimestamp(): Long? {
        val ts = prefs.getLong("last_sync_timestamp", -1L)
        return if (ts == -1L) null else ts
    }

    override suspend fun setLastSyncTimestamp(timestamp: Long) {
        prefs.edit().putLong("last_sync_timestamp", timestamp).apply()
    }

    override suspend fun syncWithCloud(): Result<Unit> {
        val username = getCurrentUsername()
        if (username == "local") {
            // Local guest mode bypasses cloud sync
            return Result.success(Unit)
        }

        return runCatching {
            val client = supabaseProvider.client
            val postgrest = client.postgrest

            // 1. Process deletions
            val deletedRecords = deletedRecordDao.getAllDeletedRecords()
            if (deletedRecords.isNotEmpty()) {
                val deletedCards = deletedRecords.filter { it.tableName == "cards" }.map { it.id }
                val deletedExpenses = deletedRecords.filter { it.tableName == "expenses" }.map { it.id }
                val deletedFuture = deletedRecords.filter { it.tableName == "future_expenses" }.map { it.id }

                if (deletedCards.isNotEmpty()) {
                    deletedCards.chunked(100).forEach { chunk ->
                        postgrest["cards"].delete {
                            filter { isIn("id", chunk) }
                        }
                    }
                }
                if (deletedExpenses.isNotEmpty()) {
                    deletedExpenses.chunked(100).forEach { chunk ->
                        postgrest["expenses"].delete {
                            filter { isIn("id", chunk) }
                        }
                    }
                }
                if (deletedFuture.isNotEmpty()) {
                    deletedFuture.chunked(100).forEach { chunk ->
                        postgrest["future_expenses"].delete {
                            filter { isIn("id", chunk) }
                        }
                    }
                }
                deletedRecordDao.deleteRecords(deletedRecords.map { it.id })
            }

            // 2. Batch upload dirty cards
            val dirtyCards = cardDao.getDirtyCards()
            if (dirtyCards.isNotEmpty()) {
                dirtyCards.chunked(100).forEach { chunk ->
                    postgrest["cards"].upsert(chunk)
                    cardDao.markClean(chunk.map { it.id })
                }
            }

            // 3. Batch upload dirty expenses
            val dirtyExpenses = expenseDao.getDirtyExpenses()
            if (dirtyExpenses.isNotEmpty()) {
                dirtyExpenses.chunked(100).forEach { chunk ->
                    postgrest["expenses"].upsert(chunk)
                    expenseDao.markClean(chunk.map { it.id })
                }
            }

            // 4. Batch upload dirty future expenses
            val dirtyFuture = futureExpenseDao.getDirtyFutureExpenses()
            if (dirtyFuture.isNotEmpty()) {
                dirtyFuture.chunked(100).forEach { chunk ->
                    postgrest["future_expenses"].upsert(chunk)
                    futureExpenseDao.markClean(chunk.map { it.id })
                }
            }

            // 5. Download remote updates
            val remoteCards = postgrest["cards"].select {
                filter { eq("username", username) }
            }.decodeList<CreditCardEntity>()
            if (remoteCards.isNotEmpty()) {
                cardDao.insertCards(remoteCards.map { it.copy(isSyncDirty = false) })
            }

            val remoteExpenses = postgrest["expenses"].select {
                filter { eq("username", username) }
            }.decodeList<ExpenseEntity>()
            if (remoteExpenses.isNotEmpty()) {
                expenseDao.insertExpenses(remoteExpenses.map { it.copy(isSyncDirty = false) })
            }

            val remoteFuture = postgrest["future_expenses"].select {
                filter { eq("username", username) }
            }.decodeList<FutureExpenseEntity>()
            if (remoteFuture.isNotEmpty()) {
                futureExpenseDao.insertFutureExpenses(remoteFuture.map { it.copy(isSyncDirty = false) })
            }

            setLastSyncTimestamp(System.currentTimeMillis())
        }
    }

    override suspend fun exportDataAsJson(username: String): String {
        val cards = cardDao.getAllCards(username)
        val expenses = expenseDao.getAllExpenses(username)
        val futureExpenses = futureExpenseDao.getAllFutureExpenses(username)

        val payload = BackupPayload(
            exportDate = java.time.LocalDateTime.now().toString(),
            username = username,
            cards = cards,
            expenses = expenses,
            futureExpenses = futureExpenses
        )
        return json.encodeToString(payload)
    }

    override suspend fun importDataFromJson(jsonString: String, targetUsername: String): Result<ImportResult> {
        return runCatching {
            val trimmed = jsonString.trim()
            if (trimmed.isEmpty()) {
                throw IllegalArgumentException("Input JSON is empty")
            }

            var importedCards = 0
            var importedExpenses = 0
            var importedFuture = 0

            // 1. Try parsing as BackupPayload
            val payload = try {
                json.decodeFromString<BackupPayload>(trimmed)
            } catch (_: Exception) {
                null
            }

            if (payload != null) {
                if (payload.cards.isNotEmpty()) {
                    val cardsToInsert = payload.cards.map { card ->
                        card.copy(
                            username = targetUsername,
                            isSyncDirty = true
                        )
                    }
                    cardDao.insertCards(cardsToInsert)
                    importedCards += cardsToInsert.size
                }

                if (payload.expenses.isNotEmpty()) {
                    val expensesToInsert = payload.expenses.map { exp ->
                        exp.copy(
                            username = targetUsername,
                            isSyncDirty = true
                        )
                    }
                    expenseDao.insertExpenses(expensesToInsert)
                    importedExpenses += expensesToInsert.size
                }

                if (payload.futureExpenses.isNotEmpty()) {
                    val futureToInsert = payload.futureExpenses.map { fut ->
                        fut.copy(
                            username = targetUsername,
                            isSyncDirty = true
                        )
                    }
                    futureExpenseDao.insertFutureExpenses(futureToInsert)
                    importedFuture += futureToInsert.size
                }
            } else {
                // 2. Fallback: Try parsing as list of ExpenseEntity directly
                val expensesList = try {
                    json.decodeFromString<List<ExpenseEntity>>(trimmed)
                } catch (_: Exception) {
                    null
                }

                if (!expensesList.isNullOrEmpty()) {
                    val expensesToInsert = expensesList.map { exp ->
                        exp.copy(
                            username = targetUsername,
                            isSyncDirty = true
                        )
                    }
                    expenseDao.insertExpenses(expensesToInsert)
                    importedExpenses += expensesToInsert.size
                } else {
                    // 3. Fallback: Try parsing as list of CreditCardEntity directly
                    val cardsList = try {
                        json.decodeFromString<List<CreditCardEntity>>(trimmed)
                    } catch (_: Exception) {
                        null
                    }

                    if (!cardsList.isNullOrEmpty()) {
                        val cardsToInsert = cardsList.map { card ->
                            card.copy(
                                username = targetUsername,
                                isSyncDirty = true
                            )
                        }
                        cardDao.insertCards(cardsToInsert)
                        importedCards += cardsToInsert.size
                    } else {
                        throw IllegalArgumentException("Could not parse JSON. Ensure it is valid backup JSON or transaction list.")
                    }
                }
            }

            ImportResult(
                cardsCount = importedCards,
                expensesCount = importedExpenses,
                futureExpensesCount = importedFuture
            )
        }
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return runCatching {
            supabaseProvider.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val userEmail = supabaseProvider.client.auth.currentUserOrNull()?.email ?: email
            setCurrentUsername(userEmail)
            userEmail
        }
    }

    override suspend fun signUp(email: String, password: String): Result<String> {
        return runCatching {
            supabaseProvider.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val userEmail = supabaseProvider.client.auth.currentUserOrNull()?.email ?: email
            setCurrentUsername(userEmail)
            userEmail
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            supabaseProvider.client.auth.signOut()
            setCurrentUsername("local")
        }
    }
}
