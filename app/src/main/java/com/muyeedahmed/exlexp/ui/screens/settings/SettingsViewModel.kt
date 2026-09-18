package com.muyeedahmed.exlexp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.repository.CardRepository
import com.muyeedahmed.exlexp.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class SettingsUiState(
    val cards: List<CreditCard> = emptyList(),
    val currentUsername: String = "local",
    val lastSyncTime: String = "Never",
    val isSyncing: Boolean = false,
    val statusMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    data class ImportDialogState(
        val isSuccess: Boolean,
        val title: String,
        val message: String
    )

    private val _importDialogState = MutableStateFlow<ImportDialogState?>(null)
    val importDialogState: StateFlow<ImportDialogState?> = _importDialogState.asStateFlow()

    fun dismissImportDialog() {
        _importDialogState.value = null
    }

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    val uiState: StateFlow<SettingsUiState> = combine(
        cardRepository.getAllCardsFlow(syncRepository.getCurrentUsername()),
        _statusMessage,
        _isSyncing
    ) { cards, msg, syncing ->
        val username = syncRepository.getCurrentUsername()
        val lastSync = syncRepository.getLastSyncTimestamp()?.let {
            java.time.Instant.ofEpochMilli(it)
                .atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        } ?: "Never"

        SettingsUiState(
            cards = cards,
            currentUsername = username,
            lastSyncTime = lastSync,
            isSyncing = syncing,
            statusMessage = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun addAccount(
        name: String,
        type: String, // "Checking", "Savings", "Brokerage", "Credit Card"
        openDate: String
    ) {
        viewModelScope.launch {
            val username = syncRepository.getCurrentUsername()
            val id = "acc-${UUID.randomUUID().toString().take(8)}"
            val card = CreditCard(
                id = id,
                name = name.trim(),
                isChecking = type == "Checking",
                isSaving = type == "Savings",
                isBrokerage = type == "Brokerage",
                openDate = openDate.trim().ifBlank { LocalDate.now().toString() },
                username = username,
                priority = uiState.value.cards.size,
                isSyncDirty = true
            )
            cardRepository.saveCard(card)
            _statusMessage.value = "Added ${card.name}"
        }
    }

    fun moveAccount(cardId: String, moveUp: Boolean) {
        viewModelScope.launch {
            val list = uiState.value.cards.toMutableList()
            val index = list.indexOfFirst { it.id == cardId }
            if (index == -1) return@launch

            val targetIndex = if (moveUp) index - 1 else index + 1
            if (targetIndex in list.indices) {
                val temp = list[index]
                list[index] = list[targetIndex]
                list[targetIndex] = temp
                cardRepository.reorderCards(list)
            }
        }
    }

    fun toggleVisibility(card: CreditCard) {
        viewModelScope.launch {
            cardRepository.saveCard(card.copy(isHidden = !card.isHidden))
        }
    }

    fun renameCard(card: CreditCard, newName: String) {
        viewModelScope.launch {
            cardRepository.saveCard(card.copy(name = newName.trim()))
        }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            cardRepository.deleteCard(cardId)
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = syncRepository.syncWithCloud()
            _isSyncing.value = false
            _statusMessage.value = if (res.isSuccess) "Sync completed successfully!" else "Sync failed: ${res.exceptionOrNull()?.message}"
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = syncRepository.login(email, pass)
            _isSyncing.value = false
            _statusMessage.value = if (res.isSuccess) "Logged in as ${res.getOrNull()}" else "Login failed: ${res.exceptionOrNull()?.message}"
        }
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = syncRepository.signUp(email, pass)
            _isSyncing.value = false
            _statusMessage.value = if (res.isSuccess) "Registered and signed in as ${res.getOrNull()}" else "Registration failed: ${res.exceptionOrNull()?.message}"
        }
    }

    fun logout() {
        viewModelScope.launch {
            syncRepository.logout()
            _statusMessage.value = "Switched to Guest / Local mode"
        }
    }

    suspend fun exportJson(): String {
        return syncRepository.exportDataAsJson(syncRepository.getCurrentUsername())
    }

    fun importData(jsonContent: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = syncRepository.importDataFromJson(jsonContent)
            _isSyncing.value = false
            result.fold(
                onSuccess = { res ->
                    _importDialogState.value = ImportDialogState(
                        isSuccess = true,
                        title = "Import Successful",
                        message = "Successfully imported:\n\n• ${res.cardsCount} accounts\n• ${res.expensesCount} transactions\n• ${res.futureExpensesCount} bills"
                    )
                },
                onFailure = { err ->
                    _importDialogState.value = ImportDialogState(
                        isSuccess = false,
                        title = "Import Failed",
                        message = "Import failed with error:\n\n${err.message ?: err.toString()}"
                    )
                }
            )
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
