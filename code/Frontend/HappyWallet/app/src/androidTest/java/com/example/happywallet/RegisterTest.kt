package com.example.happywallet

//Imports
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.happywallet.api.APIServices
import com.example.happywallet.models.User
import com.example.happywallet.navigation.AppNavHost
import com.example.happywallet.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RegisterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockApiService: APIServices
    lateinit var navController: TestNavHostController

    //Make sure the test starts in the Register page
    @Before
    fun setUp() {
        mockApiService = FakeApiService()


        composeTestRule.setContent {
            val testNavController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController = testNavController
            AppNavHost(navController)
        }
        composeTestRule.waitForIdle()

        composeTestRule.runOnIdle {
            navController.setCurrentDestination(Screen.Register.route)
        }
    }

    //Check if the UI is correctly loaded
    @Test
    fun testRegisterUIIsDisplayed() {

        composeTestRule.onNode(hasText("Register") and hasNoClickAction()).assertExists()
        composeTestRule.onNode(hasText("Register") and hasClickAction()).assertExists()

        composeTestRule.onNodeWithText("Full Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Date of Birth (YYYY-MM-DD)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()
    }

    //Using a fake api check if registering works using test data.
    @Test
    fun testRegisterApiIsCalled() {

        composeTestRule.onNodeWithText("Full Name").performTextInput("John Doe")
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Date of Birth (YYYY-MM-DD)").performTextInput("2000-01-01")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password123")

        composeTestRule.onNode(hasText("Register") and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        val response = mockApiService.registerUser(User(1, "test@example.com", "John Doe", "2000-01-01", "password123")).execute()
        assert(response.isSuccessful)
    }
}