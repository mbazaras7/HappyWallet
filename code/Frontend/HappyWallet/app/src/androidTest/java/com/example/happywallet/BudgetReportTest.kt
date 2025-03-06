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
class BudgetReportTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    //Make sure test starts at the Budget Report Screen
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
            navController.navigate(Screen.BudgetReport.createRoute("1"))
        }
    }

    //Test if it navigated to a Buget Report page and it is loading properly
    @Test
    fun testBudgetReportUIElementsExist() {
        composeTestRule.onNodeWithText("Budget Report").assertExists()
    }
}