package com.muyeedahmed.exldroid.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Analytics", Icons.Filled.Analytics, Icons.Outlined.Analytics)
    object Accounts : Screen("accounts", "Accounts", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance)
    object CreditCards : Screen("credit_cards", "Cards", Icons.Filled.CreditCard, Icons.Outlined.CreditCard)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    object AllTransactions : Screen("all_transactions", "All Transactions", Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Filled.List)
}

val BottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Accounts,
    Screen.CreditCards,
    Screen.Settings
)

