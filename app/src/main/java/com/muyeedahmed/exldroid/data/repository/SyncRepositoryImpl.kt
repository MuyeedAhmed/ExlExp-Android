package com.muyeedahmed.exldroid.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.muyeedahmed.exldroid.data.local.dao.CardDao
import com.muyeedahmed.exldroid.data.local.dao.DeletedRecordDao
import com.muyeedahmed.exldroid.data.local.dao.ExpenseDao
import com.muyeedahmed.exldroid.data.local.dao.FutureExpenseDao
import com.muyeedahmed.exldroid.data.local.entity.CreditCardEntity
import com.muyeedahmed.exldroid.data.local.entity.ExpenseEntity
import com.muyeedahmed.exldroid.data.local.entity.FutureExpenseEntity
import com.muyeedahmed.exldroid.data.remote.SupabaseClientProvider
import com.muyeedahmed.exldroid.domain.repository.ImportResult
import com.muyeedahmed.exldroid.domain.repository.SyncRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import java.time.LocalDate
import java.util.UUID
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
        context.getSharedPreferences("exldroid_prefs", Context.MODE_PRIVATE)
    }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        allowSpecialFloatingPointValues = true
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

            val rootElement = try {
                json.parseToJsonElement(trimmed)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid JSON format: ${e.message}", e)
            }

            val cardsToInsert = mutableListOf<CreditCardEntity>()
            val expensesToInsert = mutableListOf<ExpenseEntity>()
            val futureToInsert = mutableListOf<FutureExpenseEntity>()

            fun cleanDate(input: String): String {
                val s = input.trim()
                val datePart = if (s.contains('T')) s.substringBefore('T') else s
                val normalized = datePart.replace('/', '-')
                return try {
                    LocalDate.parse(normalized).toString()
                } catch (_: Exception) {
                    val parts = normalized.split('-')
                    if (parts.size == 3) {
                        if (parts[0].length == 4) normalized
                        else if (parts[2].length == 4) "${parts[2]}-${parts[0].padStart(2, '0')}-${parts[1].padStart(2, '0')}"
                        else normalized
                    } else {
                        LocalDate.now().toString()
                    }
                }
            }

            fun getString(obj: JsonObject, vararg keys: String): String? {
                for (k in keys) {
                    val el = obj[k] ?: continue
                    if (el is JsonPrimitive) {
                        val content = el.content.trim()
                        if (content.isNotEmpty() && content != "null") return content
                    }
                }
                return null
            }

            fun getDouble(obj: JsonObject, vararg keys: String): Double? {
                for (k in keys) {
                    val el = obj[k] ?: continue
                    if (el is JsonPrimitive) {
                        val content = el.content.replace("$", "").replace(",", "").trim()
                        content.toDoubleOrNull()?.let { return it }
                    }
                }
                return null
            }

            fun getBoolean(obj: JsonObject, vararg keys: String): Boolean {
                for (k in keys) {
                    val el = obj[k] ?: continue
                    if (el is JsonPrimitive) {
                        if (el.booleanOrNull != null) return el.boolean
                        val str = el.content.trim().lowercase()
                        if (str == "true" || str == "1" || str == "yes") return true
                        if (str == "false" || str == "0" || str == "no") return false
                    }
                }
                return false
            }

            fun getInt(obj: JsonObject, vararg keys: String): Int? {
                for (k in keys) {
                    val el = obj[k] ?: continue
                    if (el is JsonPrimitive) {
                        el.intOrNull?.let { return it }
                        el.content.trim().toIntOrNull()?.let { return it }
                    }
                }
                return null
            }

            fun parseCard(obj: JsonObject): CreditCardEntity {
                val id = getString(obj, "id", "_id", "ID") ?: "card-${UUID.randomUUID()}"
                val name = getString(obj, "name", "card_name", "cardName", "account_name", "accountName", "title", "label") ?: "Account"
                val isChecking = getBoolean(obj, "isChecking", "is_checking")
                val isSaving = getBoolean(obj, "isSaving", "is_saving")
                val isBrokerage = getBoolean(obj, "isBrokerage", "is_brokerage")
                val isHidden = getBoolean(obj, "isHidden", "is_hidden")
                val priority = getInt(obj, "priority") ?: 0
                val rawOpenDate = getString(obj, "openDate", "open_date", "date", "created_at", "createdAt") ?: LocalDate.now().toString()
                val openDate = cleanDate(rawOpenDate)

                return CreditCardEntity(
                    id = id,
                    name = name,
                    isChecking = isChecking,
                    isSaving = isSaving,
                    isBrokerage = isBrokerage,
                    isHidden = isHidden,
                    priority = priority,
                    openDate = openDate,
                    username = targetUsername,
                    isSyncDirty = true,
                    updatedAt = System.currentTimeMillis()
                )
            }

            fun parseExpense(obj: JsonObject, fallbackCardId: String): ExpenseEntity {
                val id = getString(obj, "id", "_id", "ID") ?: "exp-${UUID.randomUUID()}"
                val desc = getString(obj, "description", "desc", "merchant", "title", "name", "payee", "fromTo", "from_to") ?: "Expense"
                val amount = getDouble(obj, "amount", "cost", "value", "price") ?: 0.0
                val cardId = getString(obj, "creditCardId", "credit_card_id", "card_id", "cardId", "accountId", "account_id") ?: fallbackCardId
                val rawDate = getString(obj, "date", "datetime", "created_at", "createdAt", "transaction_date") ?: LocalDate.now().toString()
                val date = cleanDate(rawDate)
                val fromTo = getString(obj, "fromTo", "from_to", "merchant", "recipient", "sender", "payee")
                val details = getString(obj, "details", "memo", "notes", "note", "comment")
                val isFee = getBoolean(obj, "isFee", "is_fee")
                val isReward = getBoolean(obj, "isReward", "is_reward")
                val rewardType = getString(obj, "rewardType", "reward_type")
                val rewardValue = getDouble(obj, "rewardValue", "reward_value")
                val isTransfer = getBoolean(obj, "isTransfer", "is_transfer")
                val transferLinkId = getString(obj, "transferLinkId", "transfer_link_id", "transferId", "transfer_id")
                val isInterest = getBoolean(obj, "isInterest", "is_interest")
                val category = getString(obj, "category", "category_name", "categoryName", "cat") ?: "Others"

                return ExpenseEntity(
                    id = id,
                    description = desc,
                    amount = amount,
                    creditCardId = cardId,
                    date = date,
                    fromTo = fromTo,
                    details = details,
                    isFee = isFee,
                    isReward = isReward,
                    rewardType = rewardType,
                    rewardValue = rewardValue,
                    isTransfer = isTransfer,
                    transferLinkId = transferLinkId,
                    isInterest = isInterest,
                    category = category,
                    username = targetUsername,
                    isSyncDirty = true,
                    updatedAt = System.currentTimeMillis()
                )
            }

            fun parseFuture(obj: JsonObject): FutureExpenseEntity {
                val id = getString(obj, "id", "_id", "ID") ?: "fut-${UUID.randomUUID()}"
                val desc = getString(obj, "description", "desc", "title", "name") ?: "Bill"
                val amount = getDouble(obj, "amount", "cost", "value") ?: 0.0
                val rawDueDate = getString(obj, "dueDate", "due_date", "date")
                val dueDate = rawDueDate?.let { cleanDate(it) }

                return FutureExpenseEntity(
                    id = id,
                    description = desc,
                    amount = amount,
                    dueDate = dueDate,
                    username = targetUsername,
                    isSyncDirty = true
                )
            }

            when (rootElement) {
                is JsonObject -> {
                    val targetObj = if (rootElement["data"] is JsonObject) rootElement["data"] as JsonObject else rootElement

                    // Extract cards / accounts
                    val cardsArray = (targetObj["cards"] ?: targetObj["accounts"] ?: targetObj["creditCards"] ?: targetObj["credit_cards"]) as? JsonArray
                    cardsArray?.forEach { el ->
                        if (el is JsonObject) cardsToInsert.add(parseCard(el))
                    }

                    // Extract expenses / transactions
                    val expensesArray = (targetObj["expenses"] ?: targetObj["transactions"] ?: targetObj["items"] ?: targetObj["records"]) as? JsonArray
                    val defaultCardId = cardsToInsert.firstOrNull()?.id ?: "card-default"
                    expensesArray?.forEach { el ->
                        if (el is JsonObject) expensesToInsert.add(parseExpense(el, defaultCardId))
                    }

                    // Extract future expenses / bills
                    val futureArray = (targetObj["futureExpenses"] ?: targetObj["future_expenses"] ?: targetObj["bills"]) as? JsonArray
                    futureArray?.forEach { el ->
                        if (el is JsonObject) futureToInsert.add(parseFuture(el))
                    }

                    // If neither cards nor expenses were arrays, check if the object itself is a single expense or card
                    if (cardsToInsert.isEmpty() && expensesToInsert.isEmpty() && futureToInsert.isEmpty()) {
                        if (targetObj.containsKey("amount") || targetObj.containsKey("description") || targetObj.containsKey("merchant")) {
                            expensesToInsert.add(parseExpense(targetObj, "card-default"))
                        } else if (targetObj.containsKey("name") && (targetObj.containsKey("openDate") || targetObj.containsKey("open_date"))) {
                            cardsToInsert.add(parseCard(targetObj))
                        }
                    }
                }
                is JsonArray -> {
                    rootElement.forEach { el ->
                        if (el is JsonObject) {
                            if (el.containsKey("amount") || el.containsKey("category") || el.containsKey("description") || el.containsKey("merchant") || el.containsKey("payee")) {
                                expensesToInsert.add(parseExpense(el, "card-default"))
                            } else if (el.containsKey("name") || el.containsKey("card_name") || el.containsKey("account_name")) {
                                cardsToInsert.add(parseCard(el))
                            }
                        }
                    }
                }
                else -> {
                    throw IllegalArgumentException("Expected JSON object or array, but got: ${rootElement::class.simpleName}")
                }
            }

            if (cardsToInsert.isEmpty() && expensesToInsert.isEmpty() && futureToInsert.isEmpty()) {
                throw IllegalArgumentException("No recognizable accounts or expenses found in the JSON. Please check that keys like 'cards'/'accounts' or 'expenses'/'transactions' exist.")
            }

            // Ensure every card referenced by an expense exists
            val existingCards = cardDao.getAllCards(targetUsername)
            val knownCardIds = (existingCards.map { it.id } + cardsToInsert.map { it.id }).toSet()
            val referencedCardIds = expensesToInsert.map { it.creditCardId }.distinct()
            for (refId in referencedCardIds) {
                if (refId !in knownCardIds) {
                    val cleanCardName = refId
                        .replace("card-", "")
                        .replace("acc-", "")
                        .replace("-", " ")
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    cardsToInsert.add(
                        CreditCardEntity(
                            id = refId,
                            name = cleanCardName.ifBlank { "Account" },
                            openDate = LocalDate.now().toString(),
                            username = targetUsername,
                            isSyncDirty = true
                        )
                    )
                }
            }

            if (cardsToInsert.isNotEmpty()) {
                cardDao.insertCards(cardsToInsert)
            }
            if (expensesToInsert.isNotEmpty()) {
                expenseDao.insertExpenses(expensesToInsert)
            }
            if (futureToInsert.isNotEmpty()) {
                futureExpenseDao.insertFutureExpenses(futureToInsert)
            }

            ImportResult(
                cardsCount = cardsToInsert.size,
                expensesCount = expensesToInsert.size,
                futureExpensesCount = futureToInsert.size
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
