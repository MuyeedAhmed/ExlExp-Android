package com.muyeedahmed.exlexp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muyeedahmed.exlexp.ui.theme.BorderGray
import com.muyeedahmed.exlexp.ui.theme.NeutralGray
import com.muyeedahmed.exlexp.ui.theme.PrimarySlate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.muyeedahmed.exlexp.domain.model.Expense
import com.muyeedahmed.exlexp.ui.screens.accounts.AccountsScreen
import com.muyeedahmed.exlexp.ui.screens.accounts.AccountsViewModel
import com.muyeedahmed.exlexp.ui.screens.alltransactions.AllTransactionsScreen
import com.muyeedahmed.exlexp.ui.screens.alltransactions.AllTransactionsViewModel
import com.muyeedahmed.exlexp.ui.screens.creditcards.CreditCardsScreen
import com.muyeedahmed.exlexp.ui.screens.creditcards.CreditCardsViewModel
import com.muyeedahmed.exlexp.ui.screens.dashboard.DashboardScreen
import com.muyeedahmed.exlexp.ui.screens.dashboard.DashboardViewModel
import com.muyeedahmed.exlexp.ui.screens.logexpense.LogExpenseModal
import com.muyeedahmed.exlexp.ui.screens.logexpense.LogExpenseViewModel
import com.muyeedahmed.exlexp.ui.screens.settings.SettingsScreen
import com.muyeedahmed.exlexp.ui.screens.settings.SettingsViewModel
import com.muyeedahmed.exlexp.ui.theme.ExcelGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val logExpenseViewModel: LogExpenseViewModel = hiltViewModel()

    var showLogModal by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }

    val showBottomBar = currentRoute != Screen.AllTransactions.route

    Scaffold(
        topBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 12.dp)
                        .drawBehind {
                            drawLine(
                                color = Color(0xFFF1F5F9),
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ExlExp",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimarySlate
                    )
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                // React Native style spreadsheet tab strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color.White)
                        .drawBehind {
                            drawLine(
                                color = BorderGray,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                ) {
                    BottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isSelected) Color(0xFFF1F5F9) else Color.Transparent)
                                .drawBehind {
                                    if (isSelected) {
                                        drawLine(
                                            color = PrimarySlate,
                                            start = Offset(0f, size.height - 2.dp.toPx()),
                                            end = Offset(size.width, size.height - 2.dp.toPx()),
                                            strokeWidth = 2.dp.toPx()
                                        )
                                    }
                                }
                                .clickable {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = screen.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimarySlate else NeutralGray
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                // Centered floating pill: ➕ Log Expense
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(28.dp))
                        .background(PrimarySlate)
                        .clickable {
                            expenseToEdit = null
                            showLogModal = true
                        }
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "➕",
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Expense",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route
            ) {
                composable(Screen.Dashboard.route) {
                    val viewModel: DashboardViewModel = hiltViewModel()
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToAllTransactions = {
                            navController.navigate(Screen.AllTransactions.route)
                        },
                        onEditExpense = { exp ->
                            expenseToEdit = exp
                            showLogModal = true
                        }
                    )
                }

                composable(Screen.Accounts.route) {
                    val viewModel: AccountsViewModel = hiltViewModel()
                    AccountsScreen(
                        viewModel = viewModel,
                        onEditExpense = { exp ->
                            expenseToEdit = exp
                            showLogModal = true
                        }
                    )
                }

                composable(Screen.CreditCards.route) {
                    val viewModel: CreditCardsViewModel = hiltViewModel()
                    CreditCardsScreen(
                        viewModel = viewModel,
                        onEditExpense = { exp ->
                            expenseToEdit = exp
                            showLogModal = true
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    val viewModel: SettingsViewModel = hiltViewModel()
                    SettingsScreen(viewModel = viewModel)
                }

                composable(Screen.AllTransactions.route) {
                    val viewModel: AllTransactionsViewModel = hiltViewModel()
                    AllTransactionsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onEditExpense = { exp ->
                            expenseToEdit = exp
                            showLogModal = true
                        }
                    )
                }
            }
        }

        // Floating Modal for logging or editing expense / transfer
        if (showLogModal) {
            LogExpenseModal(
                viewModel = logExpenseViewModel,
                existingExpense = expenseToEdit,
                onDismiss = {
                    showLogModal = false
                    expenseToEdit = null
                }
            )
        }
    }
}
