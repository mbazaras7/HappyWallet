package com.example.happywallet.models

//Imports
import com.google.gson.annotations.SerializedName

//Data class to hold a single item in a receipt
data class ParsedItem(
    val description: ValueWrapper,
    val total_price: ValueWrapper
)

//Wrapper to allow the JSON to be extracted properly
data class ValueWrapper(
    val value: String
)

//Receipt Model
data class Receipt(
    val id: Int,
    @SerializedName("user") val userId: Int,
    @SerializedName("budget") val budgetId: Int?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("merchant") val merchant: String?,
    @SerializedName("total_amount") val totalAmount: Double?,
    @SerializedName("transaction_date") val transactionDate: String?,
    @SerializedName("parsed_items") val parsedItems: List<ParsedItem>?,
    @SerializedName("receipt_category") val receiptCategory: String?,
    @SerializedName("uploaded_at") val uploadedAt: String
)