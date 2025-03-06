package com.example.happywallet

//Import
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
class BudgetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    //Make sure the test starts at the Budget screen
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
            navController.setCurrentDestination(Screen.Budget.route)
        }
    }


    //Test that Budgets UI loads properly, and the no budgets message is present
    @Test
    fun testReceiptDetailsUIElementsExist() {
        composeTestRule.onNodeWithText("Budgets").assertExists()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("No budgets available. Create a new budget!").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNode(hasText("Create New Budget") and hasClickAction()).performClick()
    }
}