package com.example.happywallet.utils

//Imports
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.widget.Toast
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Function to save the downloaded excel file to the user's local storage
fun saveExcelFile(context: Context, body: ResponseBody, fileName: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            val inputStream: InputStream = body.byteStream()
            val outputStream: OutputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                showDownloadNotification(context, file)
                Toast.makeText(context, "File downloaded: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Error saving file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


//Function to show a push notification to the user which will allow them to click it to access the downloaded file
fun showDownloadNotification(context: Context, file: File) {
    val channelId = "download_channel"
    val notificationId = 1001

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
    }

    val channel = NotificationChannel(
        channelId,
        "Download Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
    )

    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)

    val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val openFileIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        context, 0, openFileIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle("Download Complete")
        .setContentText("Tap to open: ${file.name}")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
}