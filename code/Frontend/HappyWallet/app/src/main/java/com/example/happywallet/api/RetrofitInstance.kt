package com.example.happywallet.api

//Imports
import android.content.Context
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//Class to configure Retrofit for API requests
class RetrofitInstance(context: Context) {
    //Backend base url
    private val BASE_URL = "https://testcloud-backend.azurewebsites.net/"

    //Function to get the authentication token from the header
    private fun getAuthHeader(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        return sharedPreferences.getString("AUTH_HEADER", null)
    }

    //Authentication interceptor to add token header to every request
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()

        val newRequest = originalRequest.newBuilder()

        if (!requestUrl.contains("api/users/")) {
            getAuthHeader(context)?.let { authHeader ->
                newRequest.addHeader("Authorization", authHeader)
            }
        }
        chain.proceed(newRequest.build())
    }

    //Client to add interceptors and modify the timout limits
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    //Retrofit instance to convert JSON responses into kotlin objects
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Companion object to make sure only one instance of API service is created
    companion object {
        @Volatile
        private var INSTANCE: APIServices? = null

        fun getApiInstance(context: Context): APIServices {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = RetrofitInstance(context).retrofit.create(APIServices::class.java)
                }
                INSTANCE!!
            }
        }
    }
}