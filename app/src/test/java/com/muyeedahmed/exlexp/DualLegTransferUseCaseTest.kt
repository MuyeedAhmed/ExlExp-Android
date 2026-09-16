package com.muyeedahmed.exlexp

import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.usecase.DualLegTransferUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DualLegTransferUseCaseTest {

    private lateinit var useCase: DualLegTransferUseCase

    @Before
    fun setup() {
        useCase = DualLegTransferUseCase()
    }

    @Test
    fun testTransferDepositToDeposit() {
        val checking = CreditCard(id = "acc-chk", name = "Checking", isChecking = true, openDate = "2020-01-01")
        val savings = CreditCard(id = "acc-sav", name = "Savings", isSaving = true, openDate = "2020-01-01")

        val (source, target) = useCase.createTransferLegs(
            sourceCard = checking,
            targetCard = savings,
            amount = 450.0,
            date = "2026-09-05",
            memo = "Emergency Fund"
        )

        // Same transfer link id
        assertEquals(source.transferLinkId, target.transferLinkId)
        assertTrue(source.transferLinkId!!.startsWith("tr-"))

        // Source is sender: -450
        assertEquals(-450.0, source.amount, 0.001)
        assertEquals("Transfer to Savings", source.description)
        assertEquals("acc-chk", source.creditCardId)

        // Target is receiver deposit: +450
        assertEquals(450.0, target.amount, 0.001)
        assertEquals("Transfer from Checking", target.description)
        assertEquals("acc-sav", target.creditCardId)
        assertEquals("Emergency Fund", target.details)
    }

    @Test
    fun testTransferToCreditCardBillPay() {
        val checking = CreditCard(id = "acc-chk", name = "Checking", isChecking = true, openDate = "2020-01-01")
        val creditCard = CreditCard(id = "card-cc", name = "Chase Sapphire", isChecking = false, openDate = "2020-01-01")

        val (source, target) = useCase.createTransferLegs(
            sourceCard = checking,
            targetCard = creditCard,
            amount = 300.0,
            date = "2026-09-05",
            isCcBillPay = true
        )

        assertEquals(source.transferLinkId, target.transferLinkId)

        // Source is -300 (leaving checking)
        assertEquals(-300.0, source.amount, 0.001)
        assertEquals("Credit Card Bill Pay - Chase Sapphire", source.details)

        // Target is -300 for Credit Card because bill payments reduce debt owed!
        assertEquals(-300.0, target.amount, 0.001)
        assertEquals("Transfer from Checking", target.description)
        assertEquals("Credit Card Bill Pay - Chase Sapphire", target.details)
    }
}
