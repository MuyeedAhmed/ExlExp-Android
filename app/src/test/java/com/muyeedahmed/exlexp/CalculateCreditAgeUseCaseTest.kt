package com.muyeedahmed.exlexp

import com.muyeedahmed.exlexp.domain.model.CreditCard
import com.muyeedahmed.exlexp.domain.usecase.CalculateCreditAgeUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CalculateCreditAgeUseCaseTest {

    private lateinit var useCase: CalculateCreditAgeUseCase
    private val fixedToday = LocalDate.of(2026, 9, 5)

    @Before
    fun setup() {
        useCase = CalculateCreditAgeUseCase()
    }

    @Test
    fun testCreditAgeCalculation() {
        // 2 years 3 months ago (June 5, 2024 to Sep 5, 2026)
        val age1 = useCase.calculateCreditAge("2024-06-05", fixedToday)
        assertEquals(2, age1.years)
        assertEquals(3, age1.months)
        assertEquals(27, age1.totalMonths)
        assertEquals("2 yrs 3 mos", age1.formatted)

        // Exactly 1 year ago (Sep 5, 2025 to Sep 5, 2026)
        val age2 = useCase.calculateCreditAge("2025-09-05", fixedToday)
        assertEquals(1, age2.years)
        assertEquals(0, age2.months)
        assertEquals("1 yr", age2.formatted)

        // 5 months ago (Apr 5, 2026 to Sep 5, 2026)
        val age3 = useCase.calculateCreditAge("2026-04-05", fixedToday)
        assertEquals(0, age3.years)
        assertEquals(5, age3.months)
        assertEquals("5 mos", age3.formatted)

        // Invalid or null
        val ageNull = useCase.calculateCreditAge(null, fixedToday)
        assertEquals("0 mos", ageNull.formatted)
        val ageInvalid = useCase.calculateCreditAge("not-a-date", fixedToday)
        assertEquals("0 mos", ageInvalid.formatted)
    }

    @Test
    fun testAverageCreditAgeExcludesClosedCards() {
        val openCard1 = CreditCard(
            id = "c1",
            name = "Chase Sapphire",
            openDate = "2024-09-05" // 24 months
        )
        val openCard2 = CreditCard(
            id = "c2",
            name = "Amex Gold",
            openDate = "2025-09-05" // 12 months
        )
        val closedCard = CreditCard(
            id = "c3",
            name = "Discover It (Closed)",
            openDate = "2016-09-05" // 10 years (120 months) - should be excluded
        )

        // Filter open cards only as specified in section 4
        val openCards = listOf(openCard1, openCard2, closedCard).filter { !it.isClosed }
        assertEquals(2, openCards.size)

        // Average: (24 + 12) / 2 = 18 months -> 1 yr 6 mos
        val avg = useCase.calculateAverageCreditAge(openCards, fixedToday)
        assertEquals("1 yr 6 mos", avg)
    }
}
