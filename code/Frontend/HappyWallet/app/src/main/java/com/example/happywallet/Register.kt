package com.example.happywallet

//Imports
import android.os.Handler
import android.os.Looper
import com.example.happywallet.api.RetrofitInstance
import com.example.happywallet.models.User
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.happywallet.navigation.Screen
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Set RegisterPage as the activity/screen.
class Register : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterPage(navController = rememberNavController())
        }
    }
}

//Main UI for the Register Page
@Composable
fun RegisterPage(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    val dateOfBirthState = remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", style = MaterialTheme.typography.displayLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
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
            value = dateOfBirthState.value,
            onValueChange = { newValue ->
                dateOfBirthState.value = formatDOB(newValue)
            },
            label = { Text("Date of Birth (YYYY-MM-DD)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
        }


        //Button to allow user to submit the entered details, creating a User object.
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || fullName.isBlank()) {
                    errorMessage = "All fields are required"
                    return@Button
                }

                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }

                val user = User(null, email, fullName, dateOfBirthState.value.text, password)
                val apiService = RetrofitInstance.getApiInstance(context)

                //Calls registerUser function to send the details to the backend endpoint
                apiService.registerUser(user).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Handler(Looper.getMainLooper()).post {
                                navController.navigate(Screen.Login.route)
                            }
                        } else {
                            errorMessage = registerError(response.errorBody()?.string() ?: "")
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        errorMessage = "Network error: ${t.message}"
                    }
                })
            }
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.Login.route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

    }
}

//Parses API errors to allow them to be displayed nicely to the user
fun registerError(errorString: String): String {
    return try {
        val errorJson = JSONObject(errorString)
        val errorMessages = mutableListOf<String>()

        if (errorJson.has("email")) {
            val emailErrors = errorJson.optJSONArray("email")
            emailErrors?.let { errorMessages.add("A ${it.getString(0)}") }
        }

        if (errorJson.has("date_of_birth")) {
            val dobErrors = errorJson.optJSONArray("date_of_birth")
            dobErrors?.let { errorMessages.add("Date of Birth Error: ${it.getString(0)}") }
        }

        errorMessages.joinToString("\n")
    } catch (e: Exception) {
        "Error catching register error: ${e.message}"
    }
}

//Makes sure that the date of birth input is correctly formatted to be handled by the backend
fun formatDOB(input: TextFieldValue): TextFieldValue {
    val nums = input.text.replace(Regex("\\D"), "").take(8)
    val formatted = StringBuilder()
    var offset = 0

    for (i in nums.indices) {
        if (i == 4 || i == 6) {
            formatted.append("-")
            if (i < input.selection.start) offset++
        }
        formatted.append(nums[i])
    }

    val newCursorPos = minOf(formatted.length, input.selection.start + offset)

    return TextFieldValue(
        text = formatted.toString(),
        selection = TextRange(newCursorPos)
    )
}