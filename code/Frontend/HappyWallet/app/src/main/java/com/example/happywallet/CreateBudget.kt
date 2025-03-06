package com.example.happywallet

//Imports
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.happywallet.api.RetrofitInstance
import com.example.happywallet.models.Budget
import com.example.happywallet.navigation.Screen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

//UI for page for creating a budget
@Composable
fun CreateBudgetPage(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var limitAmount by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = navController.context
    val categories = listOf(
        "Meal", "Supplies", "Hotel", "Fuel", "Transportation", "Communication", "Subscriptions", "Entertainment", "Training", "Healthcare", "Other"
    )
    var selectedCategories by remember { mutableStateOf<List<String>>(emptyList()) }

    val sharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getInt("USER_ID", -1)

    //Function to open an andriod date picker for the user to select from
    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    //Function to create a new budget, validates input and sends a request to create the budget
    fun createBudget() {
        if (limitAmount.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            errorMessage = "Please fill in all fields."
            return
        }

        isLoading = true

        val newBudget = Budget(
            name = name,
            userId = userId,
            filterCategories = selectedCategories,
            limitAmount = limitAmount.toDoubleOrNull() ?: 0.0,
            currentSpending = 0.0,
            startDate = startDate,
            endDate = endDate,
            receipts = emptyList()
        )

        //Call createBudget with the details entered to create a new budget
        val apiService = RetrofitInstance.getApiInstance(context)
        apiService.createBudget(newBudget).enqueue(object : Callback<Budget> {
            override fun onResponse(call: Call<Budget>, response: Response<Budget>) {
                isLoading = false
                navController.navigate(Screen.Budget.route)
            }

            override fun onFailure(call: Call<Budget>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Create Budget", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Budget Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = limitAmount,
            onValueChange = { limitAmount = it },
            label = { Text("Limit Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Categories")
        MultiSelectDropdownMenu(
            categories = categories,
            selectedCategories = selectedCategories,
            onCategoriesSelected = { selectedCategories = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = { showDatePicker { startDate = it } }) {
                Text(text = if (startDate.isNotEmpty()) "Start: $startDate" else "Select Start Date")
            }

            OutlinedButton(onClick = { showDatePicker { endDate = it } }) {
                Text(text = if (endDate.isNotEmpty()) "End: $endDate" else "Select End Date")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { createBudget() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Save Budget")
            }
        }
    }
}

//"Multi Select" dropdown menu to allow users to select multiple categories for the budget
@Composable
fun MultiSelectDropdownMenu(
    categories: List<String>,
    selectedCategories: List<String>,
    onCategoriesSelected: (List<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),)
        {
            Text(text = if (selectedCategories.isNotEmpty()) "Selected: ${selectedCategories.joinToString(", ")}" else "Select Categories")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                val isSelected = category in selectedCategories
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        val updatedSelection = if (isSelected) selectedCategories - category else selectedCategories + category
                        onCategoriesSelected(updatedSelection)
                    }
                )
            }
        }
    }
}