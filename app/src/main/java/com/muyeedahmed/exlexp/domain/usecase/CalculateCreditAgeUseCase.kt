package com.muyeedahmed.exlexp.domain.usecase

import com.muyeedahmed.exlexp.domain.model.CreditAge
import com.muyeedahmed.exlexp.domain.model.CreditCard
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

class CalculateCreditAgeUseCase @Inject constructor() {

    fun calculateCreditAge(openDateStr: String?, targetDate: LocalDate = LocalDate.now()): CreditAge {
        if (openDateStr.isNullOrBlank() || !openDateStr.matches(Regex("""^\d{4}-\d{2}-\d{2}$"""))) {
            return CreditAge(0, 0, 0, "0 mos")
        }
        val parts = openDateStr.split("-").map { it.toInt() }
        val open = try {
            LocalDate.of(parts[0], parts[1], parts[2])
        } catch (e: Exception) {
            return CreditAge(0, 0, 0, "0 mos")
        }

        if (open.isAfter(targetDate)) return CreditAge(0, 0, 0, "0 mos")

        val period = Period.between(open, targetDate)
        val totalMonths = period.years * 12 + period.months
        val years = totalMonths / 12
        val months = totalMonths % 12

        val formatted = when {
            years > 0 && months > 0 -> "$years yr${if (years > 1) "s" else ""} $months mo${if (months > 1) "s" else ""}"
            years > 0 -> "$years yr${if (years > 1) "s" else ""}"
            else -> "$months mo${if (months > 1) "s" else ""}"
        }
        return CreditAge(years, months, totalMonths, formatted)
    }

    fun calculateAverageCreditAge(openCards: List<CreditCard>, targetDate: LocalDate = LocalDate.now()): String {
        if (openCards.isEmpty()) return "N/A"

        val totalMonthsSum = openCards.sumOf { card ->
            calculateCreditAge(card.openDate, targetDate).totalMonths
        }
        val avgMonths = totalMonthsSum / openCards.size
        val years = avgMonths / 12
        val months = avgMonths % 12

        return when {
            years > 0 && months > 0 -> "$years yr${if (years > 1) "s" else ""} $months mo${if (months > 1) "s" else ""}"
            years > 0 -> "$years yr${if (years > 1) "s" else ""}"
            else -> "$months mo${if (months > 1) "s" else ""}"
        }
    }
}
