package com.muyeedahmed.exldroid.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Excel / Spreadsheet Core Slate Theme
val PrimarySlate = Color(0xFF0F172A)      // Slate 900
val DarkSlate = Color(0xFF1E293B)         // Slate 800
val TextSlate = Color(0xFF334155)         // Slate 700
val LabelSlate = Color(0xFF475569)        // Slate 600
val NeutralGray = Color(0xFF64748B)       // Slate 500
val SubtitleGray = Color(0xFF5D5D5D)      // Line 2 Account text
val BorderTable = Color(0xFFCBD5E1)       // Slate 300 - Standard grid border
val BorderGray = Color(0xFFCBD5E1)        // Alias for BorderTable
val HeaderRowBg = Color(0xFFE2E8F0)       // Slate 200 - Grid header
val SubHeaderRowBg = Color(0xFFF1F5F9)    // Slate 100
val BackgroundLight = Color(0xFFFFFFFF)   // Pure white
val CardBackgroundLight = Color(0xFFFFFFFF)
val RowAltBg = Color(0xFFF8FAFC)          // Slate 50

// Accents
val ExcelGreen = Color(0xFF16A34A)        // Green 600
val PositiveGreen = Color(0xFF16A34A)     // Green 600 (deposits, checking, positive balances)
val NegativeRed = Color(0xFFDC2626)       // Red 600 (debt, outflows)
val ActiveBlue = Color(0xFF2563EB)        // Blue 600 (selected bar chart, links)
val ActiveSelectionBg = Color(0xFFEFF6FF) // Blue 50

// Account Type Badge Colors
val CheckingBadgeBg = Color(0xFFDCFCE7)
val CheckingBadgeText = Color(0xFF15803D)
val SavingBadgeBg = Color(0xFFDCFCE7)
val SavingBadgeText = Color(0xFF166534)
val BrokerageBadgeBg = Color(0xFFF3E8FF)
val BrokerageBadgeText = Color(0xFF7E22CE)
val CreditCardBadgeBg = Color(0xFFFFEDD5)
val CreditCardBadgeText = Color(0xFFC2410C)

// Danger Buttons
val DangerBg = Color(0xFFFEE2E2)
val DangerBorder = Color(0xFFFCA5A5)

// Standard Category Palette
val CategoryRent = Color(0xFF6366F1)          // Indigo
val CategoryUtilities = Color(0xFF0284C7)     // Sky Blue
val CategoryTransport = Color(0xFF8B5CF6)     // Purple
val CategoryGas = Color(0xFFEC4899)           // Pink
val CategoryGrocery = Color(0xFF10B981)       // Emerald Green
val CategoryFood = Color(0xFFF59E0B)          // Amber
val CategoryNecessary = Color(0xFF14B8A6)     // Teal
val CategoryLuxury = Color(0xFFEC4899)        // Pink
val CategoryEntertainment = Color(0xFFF97316) // Orange
val CategorySubscriptions = Color(0xFFA855F7) // Purple
val CategoryHealth = Color(0xFFEF4444)        // Red
val CategoryTravel = Color(0xFF06B6D4)        // Cyan
val CategoryFee = Color(0xFFB45309)           // Amber Brown
val CategoryOthers = Color(0xFF64748B)        // Slate Gray
val CategorySalary = Color(0xFF16A34A)        // Green
val CategoryTransfer = Color(0xFF3B82F6)      // Blue

private val PALETTE = listOf(
    Color(0xFF0284C7), // Sky Blue
    Color(0xFF10B981), // Emerald
    Color(0xFFF59E0B), // Amber
    Color(0xFF8B5CF6), // Purple
    Color(0xFFEC4899), // Pink
    Color(0xFF06B6D4), // Cyan
    Color(0xFFF97316), // Orange
    Color(0xFF6366F1), // Indigo
    Color(0xFF14B8A6), // Teal
    Color(0xFFEF4444), // Red
    Color(0xFF84CC16), // Lime
    Color(0xFFA855F7), // Violet
    Color(0xFF64748B)  // Slate
)

fun categoryToColor(category: String): Color {
    return when (category.trim().lowercase()) {
        "rent", "housing" -> CategoryRent
        "utilities", "utility", "bills" -> CategoryUtilities
        "car payment", "transportation", "transport" -> CategoryTransport
        "gas" -> CategoryGas
        "grocery", "groceries", "grocery / food" -> CategoryGrocery
        "food", "eating out", "dining", "restaurant" -> CategoryFood
        "necessary purchases", "necessary" -> CategoryNecessary
        "luxary purchases", "luxury purchases", "luxury", "shopping" -> CategoryLuxury
        "entertainment" -> CategoryEntertainment
        "subscriptions", "subscription" -> CategorySubscriptions
        "health", "healthcare", "medical" -> CategoryHealth
        "travel" -> CategoryTravel
        "fee", "annual fee", "fees" -> CategoryFee
        "salary" -> CategorySalary
        "transfer" -> CategoryTransfer
        "others", "other" -> CategoryOthers
        else -> {
            if (category.isBlank()) return CategoryOthers
            var hash = 0
            for (ch in category) {
                hash = ch.code + ((hash shl 5) - hash)
            }
            val index = abs(hash) % PALETTE.size
            PALETTE[index]
        }
    }
}
