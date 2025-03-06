package com.example.happywallet

//Imports
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.happywallet.api.RetrofitInstance
import com.example.happywallet.models.Budget
import com.example.happywallet.navigation.Screen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//UI to display all budgets
@Composable
fun BudgetsPage(navController: NavController) {
    var budgets by remember { mutableStateOf<List<Budget>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    //Call getBudgets to get all budgets when the page loads
    LaunchedEffect(Unit) {
        val apiService = RetrofitInstance.getApiInstance(context)
        apiService.getBudgets().enqueue(object : Callback<List<Budget>> {
            override fun onResponse(call: Call<List<Budget>>, response: Response<List<Budget>>) {
                isLoading = false
                budgets = response.body() ?: emptyList()
            }

            override fun onFailure(call: Call<List<Budget>>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        IconButton(
            onClick = { navController.navigate(Screen.Main.route) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp)
                .padding(4.dp)
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.Black)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Budgets", style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )

                budgets.isEmpty() -> Text(
                    text = "No budgets available. Create a new budget!",
                    color = MaterialTheme.colorScheme.secondary
                )

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 52.dp)
                    )
                    {
                        items(budgets) { budget ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    //Make the card clickable, which would bring the user to the specific budget page
                                    .clickable { navController.navigate(Screen.BudgetDetails.createRoute("${budget.id}"))}
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            budget.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            "Category: ${budget.filterCategories.joinToString(", ")}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            "Limit: ${budget.limitAmount}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            "Current Spending: ${budget.currentSpending}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            "Start Date: ${budget.startDate}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            "End Date: ${budget.endDate}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    //If spending is exceeding the limit display a red warning icon to show this.
                                    if (budget.currentSpending > budget.limitAmount) {
                                        Icon(
                                            imageVector = Icons.Outlined.Warning,
                                            contentDescription = "Over Limit",
                                            tint = Color.Red,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate(Screen.CreateBudget.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)

            ) {
                Text("Create New Budget")
            }
    }}
}