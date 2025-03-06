package com.example.happywallet.api

//Imports
import com.example.happywallet.models.*
import com.example.happywallet.utils.LoginRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

//Main API calls for the app
interface APIServices {

    //POST call to Register User
    @POST("api/users/")
    fun registerUser(
        @Body user: User
    ): Call<User>

    //POST call to Login user
    @POST("login/")
    fun loginUser(
        @Body loginRequest: LoginRequest
    ): Call<Map<String, String>>

    //POST call to logout user
    @POST("logout/")
    fun logout(): Call<Void>

    //POST call to send image to the backend to be processed, returns a receipt object
    @Multipart
    @POST("api/process-receipt/")
    fun processReceipt(
        @Part image: MultipartBody.Part
    ): Call<Receipt>

    //GET call to get a list of all receipts from backend
    @GET("api/receipts/")
    fun getReceipt(): Call<List<Receipt>>

    //PATCH call to update the category of a specific receipt
    @PATCH("api/receipts/{id}/")
    fun updateReceipt(
        @Path("id") receiptId: String,
        @Body updateData: Map<String, String>
    ): Call<Receipt>

    //POST call to create a budget in the backend
    @POST("api/budgets/")
    fun createBudget(
        @Body budget: Budget
    ): Call<Budget>

    //GET call to get a list of all budgets
    @GET("api/budgets/")
    fun getBudgets(): Call<List<Budget>>

    //DELETE call to delete a specific budget
    @DELETE("api/budgets/{id}/")
    fun deleteBudget(
        @Path("id") budgetId: String
    ): Call<Void>

    //GET call to get a specific budget
    @GET("api/budgets/{id}")
    fun getBudgetById(
        @Path("id") budgetId: String
    ): Call<Budget>

    //GET call to get the budget report of a specific budget
    @GET("api/budget-report/{budget_id}/")
    fun getBudgetReport(
        @Path("budget_id") budgetId: String
    ): Call<BudgetReport>

    //GET call to get a download link from the backend of the budget report
    @GET("api/budget-report/{budget_id}/xlsx/")
    @Streaming
    fun downloadBudgetReport(
        @Path("budget_id") budgetId: String
    ): Call<ResponseBody>

}