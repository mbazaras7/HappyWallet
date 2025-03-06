package com.example.happywallet.navigation

//Imports
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.happywallet.*

//The different screens used for navigation
sealed class Screen(val route: String) {
    object Start : Screen("start")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Scanner : Screen("scanner")
    object Details : Screen("details")
    object Budget : Screen("budget")
    object CreateBudget : Screen("createBudget")

    object ReceiptDetail : Screen("receiptDetail/{receiptId}") {
        fun createRoute(receiptId: String) = "receiptDetail/$receiptId"
    }

    object BudgetDetails : Screen("budgetDetails/{budgetId}") {
        fun createRoute(budgetId: String) = "budgetDetails/$budgetId"
    }

    object BudgetReport : Screen("budgetReport/{budgetId}") {
        fun createRoute(budgetId: String) = "budgetReport/$budgetId"
    }
}

//The NavHost function which controls navigation throughout the app
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Start.route) {
        composable(Screen.Start.route) { StartPage(navController) }
        composable(Screen.Main.route) { MainPage(navController) }
        composable(Screen.Login.route) { LoginPage(navController) }
        composable(Screen.Register.route) { RegisterPage(navController) }
        composable(Screen.Scanner.route) { ReceiptScannerUI(navController) }
        composable(Screen.Details.route) { ReceiptDetailsPage(navController) }
        composable(Screen.Budget.route) { BudgetsPage(navController) }
        composable(Screen.CreateBudget.route) { CreateBudgetPage(navController) }
        composable(Screen.ReceiptDetail.route) { backStackEntry ->
            val receiptId = requireNotNull(backStackEntry.arguments?.getString("receiptId"))
            SingleReceiptDetailPage(receiptId, navController)
        }
        composable(Screen.BudgetDetails.route) { backStackEntry ->
            val budgetId = requireNotNull(backStackEntry.arguments?.getString("budgetId"))
            BudgetDetailsPage(budgetId, navController)
        }
        composable(Screen.BudgetReport.route) { backStackEntry ->
            val budgetId = requireNotNull(backStackEntry.arguments?.getString("budgetId"))
            BudgetReportPage(budgetId, navController)
        }
    }
}