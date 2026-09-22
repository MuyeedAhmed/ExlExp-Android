package com.muyeedahmed.exldroid.domain.model

data class CreditCard(
    val id: String,
    val name: String,
    val isChecking: Boolean = false,
    val isSaving: Boolean = false,
    val isBrokerage: Boolean = false,
    val isHidden: Boolean = false,
    val priority: Int = 0,
    val openDate: String,
    val username: String = "local",
    val isSyncDirty: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val accountType: AccountType
        get() = when {
            isChecking -> AccountType.CHECKING
            isSaving -> AccountType.SAVINGS
            isBrokerage -> AccountType.BROKERAGE
            else -> AccountType.CREDIT_CARD
        }

    val isClosed: Boolean
        get() = name.contains("closed", ignoreCase = true) || name.contains("close", ignoreCase = true)
}
