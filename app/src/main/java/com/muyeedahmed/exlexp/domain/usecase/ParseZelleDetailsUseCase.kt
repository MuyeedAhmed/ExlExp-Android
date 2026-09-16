package com.muyeedahmed.exlexp.domain.usecase

import javax.inject.Inject
import kotlin.math.abs

data class ZelleData(
    val direction: String,        // "To" or "From"
    val recipientOrSender: String, // e.g. "John Doe"
    val memo: String?              // e.g. "Dinner split"
)

data class FormattedZelleTransaction(
    val description: String,
    val fromTo: String,
    val details: String,
    val amount: Double
)

class ParseZelleDetailsUseCase @Inject constructor() {

    private val zelleRegexWithMemo = Regex("""^Zelle (To|From) (.+?) \((.*?)\)$""")
    private val zelleRegexSimple = Regex("""^Zelle (To|From) (.+)$""")

    fun formatZelle(
        direction: String,           // "To" or "From"
        name: String,
        memo: String?,
        rawAmount: Double
    ): FormattedZelleTransaction {
        val cleanName = name.trim()
        val cleanMemo = memo?.trim()?.ifBlank { null }
        val dir = if (direction.equals("From", ignoreCase = true)) "From" else "To"

        val description = "Zelle $dir $cleanName"
        val details = if (cleanMemo != null) {
            "Zelle $dir $cleanName ($cleanMemo)"
        } else {
            "Zelle $dir $cleanName"
        }

        val signedAmount = if (dir == "From") {
            abs(rawAmount)
        } else {
            -abs(rawAmount)
        }

        return FormattedZelleTransaction(
            description = description,
            fromTo = "Zelle",
            details = details,
            amount = signedAmount
        )
    }

    fun parseZelleDetails(details: String?): ZelleData? {
        if (details.isNullOrBlank()) return null

        zelleRegexWithMemo.matchEntire(details)?.let { match ->
            val (dir, name, memo) = match.destructured
            return ZelleData(dir, name, memo.ifBlank { null })
        }

        zelleRegexSimple.matchEntire(details)?.let { match ->
            val (dir, name) = match.destructured
            return ZelleData(dir, name, null)
        }

        return null
    }
}
