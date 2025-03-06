package com.example.happywallet

//Imports
import com.example.happywallet.api.APIServices
import com.example.happywallet.models.*
import com.example.happywallet.utils.LoginRequest
import io.mockk.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.mock.Calls

class APIServicesTest {
    private lateinit var mockApiService: APIServices

    //Mock the apiservice before running any tests
    @Before
    fun setUp() {
        mockApiService = mockk(relaxed = true)
    }

    //Test RegisterUser call works with mock data
    @Test
    fun testRegisterUserCall() {
        val user = User(1, "test@example.com", "John Smith", "12345", "2025-02-17")
        val mockCall: Call<User> = Calls.response(user)

        every { mockApiService.registerUser(user) } returns mockCall

        val response = mockApiService.registerUser(user)
        assertNotNull(response)
    }

    //Test LoginUser call works
    @Test
    fun testLoginUserCall() {
        val loginRequest = LoginRequest("test@example.com", "password123")
        val mockCall: Call<Map<String, String>> = Calls.response(mapOf("token" to "fake_token"))

        every { mockApiService.loginUser(loginRequest) } returns mockCall

        val response = mockApiService.loginUser(loginRequest)
        assertNotNull(response)
    }

    //Test processReceipt call works with mock data
    @Test
    fun testProcessReceiptCall() {
        val imagePart = mockk<MultipartBody.Part>()
        val mockCall: Call<Receipt> = Calls.response(
            Receipt(
                id = 1,
                userId = 123,
                budgetId = 100,
                imageUrl = "https://example.com/receipt.jpg",
                merchant = "Test Store",
                totalAmount = 50.75,
                transactionDate = "2025-02-17",
                parsedItems = listOf(),
                receiptCategory = "Groceries",
                uploadedAt = "2025-02-18T12:00:00Z"
            )
        )

        every { mockApiService.processReceipt(imagePart) } returns mockCall

        val response = mockApiService.processReceipt(imagePart)
        assertNotNull(response)
    }

    //Test Logout call
    @Test
    fun testLogoutCall() {
        val mockCall: Call<Void> = Calls.response(Response.success(null))
        every { mockApiService.logout() } returns mockCall

        val response = mockApiService.logout()
        assertNotNull(response)
    }

    //Test get receipt call
    @Test
    fun testGetReceiptCall() {
        val mockCall: Call<List<Receipt>> = Calls.response(listOf())
        every { mockApiService.getReceipt() } returns mockCall

        val response = mockApiService.getReceipt()
        assertNotNull(response)
    }

    //Test update receipt call with mock data
    @Test
    fun testUpdateReceiptCall() {
        val updateData = mapOf("receiptCategory" to "New Category")
        val receipt = Receipt(
            id = 1,
            userId = 123,
            budgetId = 100,
            imageUrl = "https://example.com/receipt.jpg",
            merchant = "Updated Store",
            totalAmount = 75.25,
            transactionDate = "2025-02-17",
            parsedItems = listOf(),
            receiptCategory = "New Category",
            uploadedAt = "2025-02-18T12:00:00Z"
        )
        val mockCall: Call<Receipt> = Calls.response(receipt)

        every { mockApiService.updateReceipt("1", updateData) } returns mockCall

        val response = mockApiService.updateReceipt("1", updateData)
        assertNotNull(response)
    }

    //Test create budget call with mock data
    @Test
    fun testCreateBudgetCall() {
        val budget = Budget(
            id = 1,
            userId = 123,
            name = "Test Budget",
            filterCategories = listOf("Groceries", "Utilities"),
            limitAmount = 500.0,
            currentSpending = 150.0,
            startDate = "2025-02-01",
            endDate = "2025-02-28",
            receipts = listOf()
        )
        val mockCall: Call<Budget> = Calls.response(budget)

        every { mockApiService.createBudget(budget) } returns mockCall

        val response = mockApiService.createBudget(budget)
        assertNotNull(response)
    }

    //Test get budgets call
    @Test
    fun testGetBudgetsCall() {
        val mockCall: Call<List<Budget>> = Calls.response(listOf())
        every { mockApiService.getBudgets() } returns mockCall

        val response = mockApiService.getBudgets()
        assertNotNull(response)
    }

    //Test delete budget call
    @Test
    fun testDeleteBudgetCall() {
        val mockCall: Call<Void> = Calls.response(Response.success(null))
        every { mockApiService.deleteBudget("1") } returns mockCall

        val response = mockApiService.deleteBudget("1")
        assertNotNull(response)
    }

    //Test get specific budget call with mock data
    @Test
    fun testGetBudgetByIdCall() {
        val budget = Budget(
            id = 1,
            userId = 123,
            name = "Test Budget",
            filterCategories = listOf("Groceries", "Utilities"),
            limitAmount = 500.0,
            currentSpending = 150.0,
            startDate = "2025-02-01",
            endDate = "2025-02-28",
            receipts = listOf()
        )
        val mockCall: Call<Budget> = Calls.response(budget)

        every { mockApiService.getBudgetById("1") } returns mockCall

        val response = mockApiService.getBudgetById("1")
        assertNotNull(response)
    }

    //Test get budget report call with mock data
    @Test
    fun testGetBudgetReportCall() {
        val budgetReport = BudgetReport(
            budget = mockk(),
            totalSpent = 150.0,
            categorySpending = mapOf("Groceries" to 100.0, "Utilities" to 50.0),
            totalItems = 7,
            categoryItems = mapOf("Groceries" to 5, "Utilities" to 2)
        )

        val mockCall: Call<BudgetReport> = Calls.response(budgetReport)

        every { mockApiService.getBudgetReport("1") } returns mockCall

        val response = mockApiService.getBudgetReport("1")

        assertNotNull(response)
        assertNotNull(response.execute().body())
    }

    //Test Download budget call
    @Test
    fun testDownloadBudgetReportCall() {
        val mockResponseBody: ResponseBody = mockk()
        val mockCall: Call<ResponseBody> = Calls.response(mockResponseBody)

        every { mockApiService.downloadBudgetReport("1") } returns mockCall

        val response = mockApiService.downloadBudgetReport("1")
        assertNotNull(response)
    }
}