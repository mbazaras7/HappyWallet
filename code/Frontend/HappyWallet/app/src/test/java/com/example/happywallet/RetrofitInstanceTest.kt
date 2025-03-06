package com.example.happywallet

//Imports
import android.content.Context
import com.example.happywallet.api.APIServices
import com.example.happywallet.api.RetrofitInstance
import io.mockk.*
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class RetrofitInstanceTest {
    private lateinit var mockContext: Context
    private lateinit var apiService: APIServices

    //Mock Context and pass it to RetrofitInstance before starting tests
    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        apiService = RetrofitInstance.getApiInstance(mockContext)
    }

    //Check if API service is being setup correctly
    @Test
    fun testRetrofitInstanceNotNull() {
        assertNotNull(apiService)
    }
}
