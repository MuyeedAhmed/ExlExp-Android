package com.muyeedahmed.exldroid.domain.model

data class DisplayTransaction(
    val id: String = "",
    val expense: Expense,
    val accountName: String,
    val accountType: AccountType = AccountType.CREDIT_CARD,
    val isIncomingTransfer: Boolean = false,
    val accountIcon: String = "💳",
    val displayAccount: String = "",
    val description: String = "",
    val dateMmDd: String = "",
    val formattedAmount: String = "",
    val isGreenColor: Boolean = false,
    val isRedColor: Boolean = false
) {
    val isTransfer: Boolean
        get() = expense.isTransfer || expense.category.equals("Transfer", ignoreCase = true)
}

