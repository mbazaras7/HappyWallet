package com.example.happywallet

//Import
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
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

//UI of the budget details page
@Composable
fun BudgetDetailsPage(budgetId: String, navController: NavController) {
    var budget by remember { mutableStateOf<Budget?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteMessage by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    //Call getBudgetById to get the details of a specific budget when the page loads
    LaunchedEffect(budgetId) {
        val apiService = RetrofitInstance.getApiInstance(context)
        apiService.getBudgetById(budgetId).enqueue(object : Callback<Budget> {
            override fun onResponse(call: Call<Budget>, response: Response<Budget>) {
                budget = response.body()
            }

            override fun onFailure(call: Call<Budget>, t: Throwable) {
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Budget Details", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(16.dp))

        budget?.let { budget ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    //If spending has exceeded the limit, show user red warning message
                    if(budget.currentSpending > budget.limitAmount){
                        Text(
                            text = "Spending amount has exceeded the Limit",
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = budget.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )

                        //Show the red delete button to allow the user to delete the budget
                        IconButton(
                            onClick = { showDeleteMessage = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Receipt",
                                tint = Color.Red
                            )
                        }
                    }

                    Text(text = "Category: ${budget.filterCategories.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Limit: ${budget.limitAmount}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Current Spending: ${budget.currentSpending}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Start Date: ${budget.startDate}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "End Date: ${budget.endDate}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    //Button to navigate the user to the budget report page
                    Button(
                        onClick = { navController.navigate(Screen.BudgetReport.createRoute("${budget.id}")) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("View Budget Report")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    //Button to navigate the user back to all budgets
                    Button(
                        onClick = { navController.navigate(Screen.Budget.route) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Budgets")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Receipts in this Budget", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            //If there is no receipts associated with the budget show a message
            if (budget.receipts.isEmpty()) {
                Text("No receipts found for this budget.", color = MaterialTheme.colorScheme.secondary)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(budget.receipts) { receipt ->
                        ExpandableReceiptCard(receipt)
                    }
                }
            }
        } ?: errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        } ?: CircularProgressIndicator()
    }

    //The popup confirmation message when deleting a budget
    if (showDeleteMessage) {
        AlertDialog(
            onDismissRequest = { showDeleteMessage = false },
            title = { Text("Delete Budget") },
            text = { Text("Are you sure you want to delete this budget?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleting = true
                        val apiService = RetrofitInstance.getApiInstance(context)
                        apiService.deleteBudget(budget?.id.toString()).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                isDeleting = false
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Budget deleted successfully.", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.Budget.route)
                                } else {
                                    Toast.makeText(context, "Failed to delete budget.", Toast.LENGTH_SHORT).show()
                                }
                                showDeleteMessage = false
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                isDeleting = false
                                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                                showDeleteMessage = false
                            }
                        })
                    }
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            color = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Delete", color = Color.Red)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteMessage = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}