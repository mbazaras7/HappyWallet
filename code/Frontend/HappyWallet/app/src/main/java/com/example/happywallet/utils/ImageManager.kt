package com.example.happywallet.utils

//Imports
import com.example.happywallet.api.RetrofitInstance
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

//Function to upload a preprocessed image of a receipt and return the ID of the receipt
suspend fun uploadImageToServer(context: Context, bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
    return@withContext try {
        val apiService = RetrofitInstance.getApiInstance(context)
        val processedBitmap = preprocessBitmap(bitmap)
        val byteArray = bitmapToByteArray(processedBitmap)
        val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val fileName = "receipt_${System.currentTimeMillis()}.jpg"
        val body = MultipartBody.Part.createFormData("image", fileName, requestFile)

        val response = apiService.processReceipt(image = body).execute()
        if (response.isSuccessful) {
            response.body()?.id?.toString()
        } else {
            null
        }

    } catch (e: Exception) {
        null
    }
}


//Function to convert the bitmap image into a byte array
fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    return outputStream.toByteArray()
}

//Function to preprocess image by resizing it while maintaining correct aspect ratio to optimise it for uploading
fun preprocessBitmap(bitmap: Bitmap, maxWidth: Int = 1080, maxHeight: Int = 1080): Bitmap {
    val ratioBitmap = bitmap.width.toFloat() / bitmap.height.toFloat()
    val targetWidth: Int
    val targetHeight: Int

    if (ratioBitmap > 1) {
        targetWidth = maxWidth
        targetHeight = (maxWidth / ratioBitmap).toInt()
    } else {
        targetHeight = maxHeight
        targetWidth = (maxHeight * ratioBitmap).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
}

//Function to create a temporary file to store the image
fun createTempImageFile(context: Context): Uri {
    val tempFile = File.createTempFile("temp_receipt", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "com.example.happywallet.fileprovider", tempFile)
}

//Function to convert an image to bitmap from a given URI
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
    }
}