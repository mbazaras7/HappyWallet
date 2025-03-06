package com.example.happywallet

//Imports
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.happywallet.navigation.AppNavHost
import com.example.happywallet.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BudgetDetailsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    //Make sure the test starts in the BudgetDetails page
    @Before
    fun setup() {
        composeTestRule.setContent {
            val testNavController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController = testNavController

            AppNavHost(navController = navController)
        }
        composeTestRule.waitForIdle()

        composeTestRule.runOnIdle {
            navController.navigate(Screen.BudgetDetails.createRoute("1"))
        }
    }

    //Test that the budgets details screen loads in properly
    @Test
    fun testBudgetDetailsUIElementsExist() {
        composeTestRule.onNodeWithText("Budget Details").assertExists()

    }
}