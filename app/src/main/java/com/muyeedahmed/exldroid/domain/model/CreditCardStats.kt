package com.muyeedahmed.exldroid.domain.model

data class CreditCardStats(
    val card: CreditCard,
    val spent: Double,
    val paid: Double,
    val rewards: Double,
    val fees: Double,
    val balanceDue: Double,
    val creditAge: CreditAge
)
