package com.example.happywallet

//Imports
import com.example.happywallet.api.APIServices
import com.example.happywallet.models.*
import com.example.happywallet.utils.LoginRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.mock.Calls

//Fake api Class to act as APIServices using fake values for Instrumented testing.
class FakeApiService : APIServices {

    override fun registerUser(user: User): Call<User> {
        return Calls.response(user)
    }

    override fun loginUser(loginRequest: LoginRequest): Call<Map<String, String>> {
        return Calls.response(mapOf("token" to "fake_token"))
    }

    override fun logout(): Call<Void> {
        return Calls.response(null)
    }

    override fun processReceipt(image: MultipartBody.Part): Call<Receipt> {
        val fakeReceipt = Receipt(
            id = 1,
            userId = 1,
            budgetId = 1,
            imageUrl = "https://example.com/fake-receipt.jpg",
            merchant = "Fake Store",
            totalAmount = 50.0,
            transactionDate = "2025-02-17",
            parsedItems = listOf(),
            receiptCategory = "Groceries",
            uploadedAt = "2025-02-17T12:00:00Z"
        )
        return Calls.response(fakeReceipt)
    }

    override fun getReceipt(): Call<List<Receipt>> {
        val fakeReceipts = listOf(
            Receipt(
                id = 1,
                userId = 1,
                budgetId = 1,
                imageUrl = "https://example.com/fake-receipt.jpg",
                merchant = "Fake Store",
                totalAmount = 50.0,
                transactionDate = "2025-02-17",
                parsedItems = listOf(),
                receiptCategory = "Groceries",
                uploadedAt = "2025-02-17T12:00:00Z"
            )
        )
        return Calls.response(fakeReceipts)
    }

    override fun updateReceipt(receiptId: String, updateData: Map<String, String>): Call<Receipt> {
        val updatedReceipt = Receipt(
            id = receiptId.toInt(),
            userId = 1,
            budgetId = 1,
            imageUrl = "https://example.com/fake-receipt.jpg",
            merchant = "Updated Store",
            totalAmount = 75.0,
            transactionDate = "2025-02-17",
            parsedItems = listOf(),
            receiptCategory = updateData["category"] ?: "Unknown",
            uploadedAt = "2025-02-17T12:00:00Z"
        )
        return Calls.response(updatedReceipt)
    }

    override fun createBudget(budget: Budget): Call<Budget> {
        return Calls.response(budget)
    }

    override fun getBudgets(): Call<List<Budget>> {
        val fakeBudgets = listOf(
            Budget(id = 1,
                userId = 1,
                name = "Test Budget",
                limitAmount = 1000.0,
                filterCategories = listOf("Supplies"),
                startDate = "2025-02-15",
                endDate = "2025-02-19")
        )
        return Calls.response(fakeBudgets)
    }

    override fun deleteBudget(budgetId: String): Call<Void> {
        return Calls.response(Response.success(null))
    }

    override fun getBudgetById(budgetId: String): Call<Budget> {
        val fakeBudget = Budget(
            id = 1, userId = 1,
            name = "Test Budget",
            limitAmount = 1000.0,
            filterCategories = listOf("Supplies"),
            currentSpending = 500.0,
            startDate = "2025-02-15",
            endDate = "2025-02-19",
            receipts = emptyList(),
        )
        return Calls.response(fakeBudget)
    }

    override fun getBudgetReport(budgetId: String): Call<BudgetReport> {
        val fakeReport = BudgetReport(
            budget = Budget(id = 1, userId = 1, name = "Test Budget", filterCategories = listOf("Supplies"), limitAmount = 500.0, startDate = "2025-02-15", endDate = "2025-02-19"),
            totalSpent = 200.0,
            categorySpending = mapOf("Food" to 100.0, "Entertainment" to 50.0),
            totalItems = 10,
            categoryItems = mapOf("Groceries" to 5, "Movies" to 2)
        )
        return Calls.response(fakeReport)
    }

    override fun downloadBudgetReport(budgetId: String): Call<ResponseBody> {
        return Calls.failure(UnsupportedOperationException("Download not supported in Fake API"))
    }
}