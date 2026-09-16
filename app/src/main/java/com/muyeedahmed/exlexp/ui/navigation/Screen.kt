package com.muyeedahmed.exlexp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Analytics", Icons.Default.Analytics)
    object Accounts : Screen("accounts", "Accounts", Icons.Default.AccountBalance)
    object CreditCards : Screen("credit_cards", "Credit Cards", Icons.Default.CreditCard)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AllTransactions : Screen("all_transactions", "All Transactions", Icons.AutoMirrored.Filled.List)
}

val BottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Accounts,
    Screen.CreditCards,
    Screen.Settings
)
