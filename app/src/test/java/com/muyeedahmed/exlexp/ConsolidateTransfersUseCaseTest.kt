package com.muyeedahmed.exlexp

import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.domain.usecase.ConsolidateTransfersUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConsolidateTransfersUseCaseTest {

    private lateinit var useCase: ConsolidateTransfersUseCase

    @Before
    fun setup() {
        useCase = ConsolidateTransfersUseCase()
    }

    @Test
    fun testIncomingTransferIdentification() {
        val outgoingLeg = Expense(
            id = "out-1",
            description = "Transfer to Savings",
            amount = -500.0,
            creditCardId = "c1",
            date = "2026-09-01",
            isTransfer = true,
            category = "Transfer"
        )
        val incomingLeg = Expense(
            id = "in-1",
            description = "Transfer from Checking",
            amount = 500.0,
            creditCardId = "c2",
            date = "2026-09-01",
            isTransfer = true,
            category = "Transfer"
        )

        assertFalse(useCase.isIncomingTransfer(outgoingLeg))
        assertTrue(useCase.isIncomingTransfer(incomingLeg))
    }

    @Test
    fun testConsolidateTransfersHidesIncomingLegInMasterFeed() {
        val c1 = CreditCard(id = "c1", name = "Checking", isChecking = true, openDate = "2020-01-01")
        val c2 = CreditCard(id = "c2", name = "Savings", isSaving = true, openDate = "2020-01-01")

        val expenses = listOf(
            Expense(id = "1", description = "Target", amount = -50.0, creditCardId = "c1", date = "2026-09-01"),
            Expense(id = "2", description = "Transfer to Savings", amount = -200.0, creditCardId = "c1", date = "2026-09-02", isTransfer = true, category = "Transfer"),
            Expense(id = "3", description = "Transfer from Checking", amount = 200.0, creditCardId = "c2", date = "2026-09-02", isTransfer = true, category = "Transfer")
        )

        val displayList = useCase.toDisplayTransactions(expenses, listOf(c1, c2), consolidateTransfers = true)

        // Incoming leg should be excluded, leaving only Target and Transfer to Savings
        assertEquals(2, displayList.size)
        assertEquals("Target", displayList[0].expense.description)
        assertEquals("Transfer to Savings", displayList[1].expense.description)
    }
}
