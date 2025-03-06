package com.example.happywallet.models

//Imports
import com.google.gson.annotations.SerializedName

//Budget Report Model
data class BudgetReport(
    @SerializedName("budget") val budget: Budget,
    @SerializedName("total_spent") val totalSpent: Double,
    @SerializedName("category_spending") val categorySpending: Map<String, Double>,
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("category_items") val categoryItems: Map<String, Int>
)