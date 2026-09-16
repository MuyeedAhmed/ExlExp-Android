package com.muyeedahmed.exlexp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ExlExp",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    BottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                ExtendedFloatingActionButton(
                    onClick = {
                        expenseToEdit = null
                        showLogModal = true
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log Expense"
                        )
                    },
                    text = {
                        Text(
                            text = "Log Expense",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
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
