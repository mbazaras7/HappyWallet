package com.example.happywallet

//Imports
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.happywallet.navigation.AppNavHost
import com.example.happywallet.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateBudgetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    //Make sure the test starts in the CreateBudget page
    @Before
    fun setup() {
        composeTestRule.setContent {
            val testNavController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController = testNavController
            AppNavHost(navController)
        }
        composeTestRule.waitForIdle()

        composeTestRule.runOnIdle {
            navController.setCurrentDestination(Screen.CreateBudget.route)
        }
    }

    //Test that the text fields and title load in properly
    @Test
    fun testCreateBudgetUIElementsExist() {
        // Verify "Create Budget" header
        composeTestRule.onNodeWithText("Create Budget").assertExists()

        // Verify form fields exist
        composeTestRule.onNodeWithText("Budget Name").assertExists()
        composeTestRule.onNodeWithText("Limit Amount").assertExists()
        composeTestRule.onNodeWithText("Categories").assertExists()
        composeTestRule.onNodeWithText("Select Categories").assertExists()
        composeTestRule.onNodeWithText("Select Start Date").assertExists()
        composeTestRule.onNodeWithText("Select End Date").assertExists()
        composeTestRule.onNodeWithText("Save Budget").assertExists()
    }

    //Test that the input fields can be used and work as intended
    @Test
    fun testCreateBudgetFormInput() {
        composeTestRule.onNodeWithText("Budget Name").performTextInput("Test Budget")
        composeTestRule.onNodeWithText("Limit Amount").performTextInput("500.0")

        composeTestRule.onNodeWithText("Select Categories").performClick()
        composeTestRule.onNodeWithText("Meal").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNode(isPopup()).assertExists()
        composeTestRule.onNode(isPopup()).performClick()

        composeTestRule.onNodeWithText("Select Start Date").performClick()
        composeTestRule.onNodeWithText("Select End Date").performClick()

        composeTestRule.onNodeWithText("Save Budget").performClick()
    }
}