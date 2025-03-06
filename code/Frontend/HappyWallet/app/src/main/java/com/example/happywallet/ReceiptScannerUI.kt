package com.example.happywallet

//Imports
import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.happywallet.navigation.Screen
import com.example.happywallet.utils.createTempImageFile
import com.example.happywallet.utils.getBitmapFromUri
import com.example.happywallet.utils.uploadImageToServer
import kotlinx.coroutines.*


//Main UI for scanning receipt page
@Composable
fun ReceiptScannerUI(navController: NavHostController) {
    val context = LocalContext.current
    val uri = remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val captureBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    //Launcher allows camera to be used and the picture taken to be saved
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            uri.value?.let {
                captureBitmap.value = getBitmapFromUri(context, it)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { navController.navigate(Screen.Main.route) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp)
                .padding(4.dp)
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.Black)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Scan Receipt", style = MaterialTheme.typography.displayMedium)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //Button to take the picture, launches camera and creates a temporary file to store the image
        Button(onClick = {
            uri.value = createTempImageFile(context)
            cameraLauncher.launch(uri.value!!)
        },colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ), modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Take a Picture of the Receipt")
        }
        Spacer(modifier = Modifier.height(16.dp))

        captureBitmap.value?.let { bitmap ->
            Text(text = "Image Captured")

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Receipt Image",
                modifier = Modifier
                    .size(300.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))

            //Button calls the uploadImageToServer function which uploads the image to the backend and navigates to the receipts details
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch(Dispatchers.IO) {
                        val newReceiptId = uploadImageToServer(context, bitmap)
                        withContext(Dispatchers.Main) {
                            isLoading = false
                            if (!newReceiptId.isNullOrEmpty()) {
                                navController.navigate(Screen.ReceiptDetail.createRoute("$newReceiptId"))
                            } else {
                                Toast.makeText(context, "Upload failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = "Upload Image")
                }
            }
        }
    }
}