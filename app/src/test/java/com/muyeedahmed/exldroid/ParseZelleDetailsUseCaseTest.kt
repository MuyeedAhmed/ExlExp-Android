package com.muyeedahmed.exldroid

import com.muyeedahmed.exldroid.domain.usecase.ParseZelleDetailsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ParseZelleDetailsUseCaseTest {

    private lateinit var useCase: ParseZelleDetailsUseCase

    @Before
    fun setup() {
        useCase = ParseZelleDetailsUseCase()
    }

    @Test
    fun testFormatZelleSendingMoney() {
        val formatted = useCase.formatZelle(
            direction = "To",
            name = "Jane Smith",
            memo = "Dinner split",
            rawAmount = 45.0
        )

        assertEquals("Zelle To Jane Smith", formatted.description)
        assertEquals("Zelle", formatted.fromTo)
        assertEquals("Zelle To Jane Smith (Dinner split)", formatted.details)
        assertEquals(-45.0, formatted.amount, 0.001)
    }

    @Test
    fun testFormatZelleReceivingMoneyWithoutMemo() {
        val formatted = useCase.formatZelle(
            direction = "From",
            name = "Bob Johnson",
            memo = null,
            rawAmount = 120.0
        )

        assertEquals("Zelle From Bob Johnson", formatted.description)
        assertEquals("Zelle", formatted.fromTo)
        assertEquals("Zelle From Bob Johnson", formatted.details)
        assertEquals(120.0, formatted.amount, 0.001)
    }

    @Test
    fun testReverseRegexParsing() {
        val dataWithMemo = useCase.parseZelleDetails("Zelle To Jane Smith (Dinner split)")
        assertNotNull(dataWithMemo)
        assertEquals("To", dataWithMemo?.direction)
        assertEquals("Jane Smith", dataWithMemo?.recipientOrSender)
        assertEquals("Dinner split", dataWithMemo?.memo)

        val dataSimple = useCase.parseZelleDetails("Zelle From Bob Johnson")
        assertNotNull(dataSimple)
        assertEquals("From", dataSimple?.direction)
        assertEquals("Bob Johnson", dataSimple?.recipientOrSender)
        assertNull(dataSimple?.memo)

        val nonZelle = useCase.parseZelleDetails("Payment to Grocery Store")
        assertNull(nonZelle)
    }
}
