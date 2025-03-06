package com.example.happywallet.models

//Imports
import com.google.gson.annotations.SerializedName

//Budget Model
data class Budget(
    val id: Int? = null,
    @SerializedName("user") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("filter_categories") val filterCategories: List<String>,
    @SerializedName("limit_amount") val limitAmount: Double,
    @SerializedName("current_spending") val currentSpending: Double = 0.0,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("receipts") val receipts: List<Receipt> = emptyList()
)