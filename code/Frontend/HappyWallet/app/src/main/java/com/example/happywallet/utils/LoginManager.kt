package com.example.happywallet.utils

//Imports
import android.content.Context
import androidx.navigation.NavController
import com.example.happywallet.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Function to handle user login/logout
class LoginManager(private val context: Context) {

    //Function to login user
    fun loginUser(
        email: String,
        password: String,
        context: Context,
        navController: NavController,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val apiService = RetrofitInstance.getApiInstance(context)
        val loginRequest = LoginRequest(email, password)

        //Call loginUser with user details to login the user and get authorization token from header
        apiService.loginUser(loginRequest).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val authHeader = responseBody?.get("Authorization")

                    if (authHeader != null) {
                        val sharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                        sharedPreferences.edit()
                            .putString("AUTH_HEADER", authHeader)
                            .apply()

                        onSuccess()
                    } else {
                        onFailure("Failed to retrieve authentication token")
                    }
                } else {
                    onFailure("Login failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                onFailure("Network error: ${t.message}")
            }
        })
    }

    //Function to logout user
    fun logoutUser(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val apiService = RetrofitInstance.getApiInstance(context)

        //Call logout function to logout user and clear credentials
        apiService.logout().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    clearStoredCredentials()
                    onSuccess()
                } else {
                    onFailure("Logout failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure("Network error: ${t.message}")
            }
        })
    }

    //Helper function to remove authentication token
    private fun clearStoredCredentials() {
        val sharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .remove("AUTH_HEADER")
            .apply()
    }
}