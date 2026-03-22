package com.arijeet.quickfixapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.BatteryManager
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class IssueDetectionWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        detectIssues()
        return Result.success()
    }

    private fun detectIssues() {
        val batteryManager = applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        
        // Detect Battery issues
        if (batteryLevel < 20) {
            sendNotification(
                "QuickFix: Battery Alert",
                "Battery is critically low ($batteryLevel%). Tap to see manual fix steps.",
                "bat_drain"
            )
        }

        // Detect Storage issues
        val internalDir = applicationContext.filesDir
        val freeSpace = internalDir.freeSpace / (1024 * 1024) // MB
        if (freeSpace < 500) {
            sendNotification(
                "QuickFix: Storage Full",
                "Less than 500MB free. Follow our guide to clear space manually.",
                "store_full"
            )
        }

        // Detect high brightness
        try {
            val brightness = Settings.System.getInt(applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            if (brightness > 220) {
                sendNotification(
                    "QuickFix: Display Tip",
                    "High brightness detected. Lowering it can prevent heating.",
                    "bat_heat"
                )
            }
        } catch (e: Exception) {
            // Ignore if setting cannot be read
        }
    }

    private fun sendNotification(title: String, message: String, issueId: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "issue_detection"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, 
                "System Monitoring", 
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for detected phone issues"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SYSTEM)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(issueId.hashCode(), notification)
    }
}
