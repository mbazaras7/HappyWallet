package com.example.happywallet.utils

//Imports
import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.NavController
import com.example.happywallet.api.APIServices
import com.example.happywallet.api.RetrofitInstance
import io.mockk.*
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginManagerTest {

    private lateinit var loginManager: LoginManager
    private lateinit var mockContext: Context
    private lateinit var mockNavController: NavController
    private lateinit var mockApiService: APIServices
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    //Mock the necessary attributes before any tests are done
    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockNavController = mockk(relaxed = true)
        mockApiService = mockk(relaxed = true)
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        every { mockContext.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE) } returns mockSharedPreferences
        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.apply() } just Runs

        mockkObject(RetrofitInstance)
        every { RetrofitInstance.getApiInstance(mockContext) } returns mockApiService

        loginManager = LoginManager(mockContext)
    }

    //Check if the LoginUser success case works
    @Test
    fun testLoginUserSuccess() {
        val email = "test@example.com"
        val password = "password123"
        val responseBody = mapOf("Authorization" to "Bearer fake_token")
        val mockCall: Call<Map<String, String>> = mockk()

        every { mockApiService.loginUser(any()) } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<Map<String, String>>>()
            callback.onResponse(mockCall, Response.success(responseBody))
        }

        var successCalled = false
        var failureMessage: String? = null

        loginManager.loginUser(email, password, mockContext, mockNavController,
            onSuccess = { successCalled = true },
            onFailure = { failureMessage = it }
        )

        assertTrue(successCalled)
        assertNull(failureMessage)
        verify { mockEditor.putString("AUTH_HEADER", "Bearer fake_token") }
        verify { mockEditor.apply() }
    }

    //Check if the LoginUser fails properly
    @Test
    fun testLoginUserFailure() {
        val email = "test@example.com"
        val password = "wrongpassword"
        val mockCall: Call<Map<String, String>> = mockk()

        every { mockApiService.loginUser(any()) } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<Map<String, String>>>()
            callback.onResponse(mockCall, Response.error(401, mockk<ResponseBody>(relaxed = true)))
        }

        var successCalled = false
        var failureMessage: String? = null

        loginManager.loginUser(email, password, mockContext, mockNavController,
            onSuccess = { successCalled = true },
            onFailure = { failureMessage = it }
        )

        assertFalse(successCalled)
        assertNotNull(failureMessage)
        assertTrue(failureMessage!!.contains("Login failed"))
    }

    //Check if the LogoutUser works correctly
    @Test
    fun testLogoutUserSuccess() {
        val mockCall: Call<Void> = mockk()

        every { mockApiService.logout() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<Void>>()
            callback.onResponse(mockCall, Response.success(null))
        }
        every { mockEditor.remove("AUTH_HEADER") } returns mockEditor

        var successCalled = false
        var failureMessage: String? = null

        loginManager.logoutUser(
            onSuccess = { successCalled = true },
            onFailure = { failureMessage = it }
        )

        assertTrue("Logout should succeed", successCalled)
        assertNull("Failure message should be null", failureMessage)
        verify { mockEditor.remove("AUTH_HEADER") }
        verify { mockEditor.apply() }
    }

    //Check if LogoutUser Fails properly
    @Test
    fun testLogoutUserFailure() {
        val mockCall: Call<Void> = mockk()

        every { mockApiService.logout() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<Void>>()
            callback.onResponse(mockCall, Response.error(500, mockk<ResponseBody>(relaxed = true)))
        }

        var successCalled = false
        var failureMessage: String? = null

        loginManager.logoutUser(
            onSuccess = { successCalled = true },
            onFailure = { failureMessage = it }
        )

        assertFalse(successCalled)
        assertNotNull(failureMessage)
        assertTrue(failureMessage!!.contains("Logout failed"))
    }
}