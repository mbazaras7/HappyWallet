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
class ReceiptDetailsUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    //Make sure the test starts in the ReceiptDetails page
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
            navController.setCurrentDestination(Screen.Details.route)
        }
    }

    //Test that the title, no receipt message and button have loaded in
    @Test
    fun testReceiptDetailsUIElementsExist() {
        composeTestRule.onNodeWithText("Receipts").assertExists()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("No receipts available. Add a new one!").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNode(hasText("Scan a Receipt") and hasClickAction()).performClick()
    }
}