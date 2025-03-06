package com.example.happywallet

//Imports
import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.happywallet.api.RetrofitInstance
import com.example.happywallet.models.BudgetReport
import com.example.happywallet.navigation.Screen
import com.example.happywallet.utils.saveExcelFile
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//UI for the budget report page
@Composable
fun BudgetReportPage(budgetId: String, navController: NavController) {
    var budgetReport by remember { mutableStateOf<BudgetReport?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    //Call getBudgetReport to get the report of a specific budget from the backend when the page loads
    LaunchedEffect(budgetId) {
        val apiService = RetrofitInstance.getApiInstance(context)
        apiService.getBudgetReport(budgetId).enqueue(object : Callback<BudgetReport> {
            override fun onResponse(call: Call<BudgetReport>, response: Response<BudgetReport>) {
                budgetReport = response.body()
            }

            override fun onFailure(call: Call<BudgetReport>, t: Throwable) {
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
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Budget Report", style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(16.dp))

            budgetReport?.let { report ->

                Text(report.budget.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                SpendingPieChart(report.categorySpending)
                Spacer(modifier = Modifier.height(16.dp))

                //Expandable sections to display details about the budget
                ExpandableSection(title = "Budget Summary") {
                    BudgetSummarySection(report)
                }

                ExpandableSection(title = "Spending Breakdown") {
                    SpendingBreakdownSection(report.categorySpending, report.categoryItems)
                }

                Spacer(modifier = Modifier.weight(1f))

                //Button to call downloadBudgetReport, used to download an excel report of the budget report
                Button(
                    onClick = { downloadBudgetReport(context, budgetId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Download Report (Excel)")
                }

            } ?: errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            } ?: CircularProgressIndicator()
        }
    }
}

//Function to display an overview of the budget, showing time period, budget limit, total amount spent
@Composable
fun BudgetSummarySection(report: BudgetReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Time Period:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${report.budget.startDate} - ${report.budget.endDate}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Total Limit:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "€${report.budget.limitAmount}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Total Spent:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "€${report.totalSpent}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (report.totalSpent > report.budget.limitAmount) Color.Red else Color.Green
            )
        }
    }
}

//Displays the spending breakdown of the budget
@Composable
fun SpendingBreakdownSection(categorySpending: Map<String, Double>, categoryItems: Map<String, Int>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            categorySpending.forEach { (category, amount) ->
                val itemCount = categoryItems[category] ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "€${amount}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$itemCount items",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

//Function to display a MPChart pie chart for the distribution of spending between the different categories
@Composable
fun SpendingPieChart(categorySpending: Map<String, Double>) {
    val pieEntries = categorySpending.map { (category, amount) ->
        PieEntry(amount.toFloat(), category)
    }

    val pieDataSet = PieDataSet(pieEntries, "").apply {
        colors = ColorTemplate.MATERIAL_COLORS.toList()
        valueTextSize = 16f
        valueTypeface = Typeface.DEFAULT_BOLD
    }
    val pieData = PieData(pieDataSet)

    //Use an android view to create the piechart
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                data = pieData
                description.isEnabled = false
                legend.apply {
                    isEnabled = true
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    textSize = 14f
                    form = Legend.LegendForm.CIRCLE
                }
                animateY(1000)
            }
        },
        modifier = Modifier.fillMaxWidth().height(250.dp)
    )
}

//Function to create dynamic expandable sections
@Composable
fun ExpandableSection(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse"
                )
            }

            if (expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    content()
                }
            }
        }
    }
}

//Function to download the budget report onto the phone as an excel file
fun downloadBudgetReport(context: Context, budgetId: String) {
    val apiService = RetrofitInstance.getApiInstance(context)
    apiService.downloadBudgetReport(budgetId).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful && response.body() != null) {
                saveExcelFile(context, response.body()!!, "budget_report_$budgetId.xlsx")
            } else {
                Toast.makeText(context, "Failed to download file: Empty response", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    })
}