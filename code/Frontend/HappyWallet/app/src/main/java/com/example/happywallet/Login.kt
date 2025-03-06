package com.example.happywallet

//Imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.happywallet.navigation.Screen
import com.example.happywallet.utils.LoginManager
import org.json.JSONObject

//Set Login as the activity/screen
class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPage(navController = rememberNavController())
        }
    }
}

//UI for the login page
@Composable
fun LoginPage(navController: NavController) {
    val context = navController.context
    val loginManager = LoginManager(context)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        //Button to login user with the provided details through the loginUser function
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                isLoading = true
                errorMessage = null

                loginManager.loginUser(
                    email = email,
                    password = password,
                    context = context,
                    navController = navController,
                    onSuccess = {
                        isLoading = false
                        navController.navigate(Screen.Main.route)
                    },
                    onFailure = { errorResponse ->
                        isLoading = false

                        val rawErrorBody = errorResponse.trim()
                        errorMessage = if (rawErrorBody.isEmpty()) {
                            "Invalid email or password"
                        } else {
                            loginError(rawErrorBody)
                        }
                    }
                )
            }
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Login")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.Register.route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

//Function to parse the API error into a readable error message for the user.
fun loginError(errorBody: String?): String {
    return try {
        if (errorBody.isNullOrBlank()) return "Invalid email or password"

        val errorJson = JSONObject(errorBody)
        if (errorJson.has("error")) {
            return errorJson.getString("error")
        }

        "Unexpected error"
    } catch (e: Exception) {
        "Invalid email or password"
    }
}