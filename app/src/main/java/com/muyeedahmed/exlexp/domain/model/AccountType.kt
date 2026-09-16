package com.muyeedahmed.exlexp.domain.model

enum class AccountType {
    CHECKING,
    SAVINGS,
    BROKERAGE,
    CREDIT_CARD;

    val isDeposit: Boolean
        get() = this == CHECKING || this == SAVINGS || this == BROKERAGE
}
