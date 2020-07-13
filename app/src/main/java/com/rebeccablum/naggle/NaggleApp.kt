package com.rebeccablum.naggle

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*
import kotlin.concurrent.timerTask

const val CHANNEL_ID = "my channel"

class NaggleApp : Application() {

    private val notificationBuilder: Notification.Builder by lazy {
        Notification.Builder(this, CHANNEL_ID)
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        Timer().schedule(timerTask {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, getNotification())
        }, 3000)
    }

    private fun getNotification(): Notification {
        return notificationBuilder
            .setContentTitle("Perma-Notification")
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(false)
            .setContentText("Here is some text.")
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "my channel"
            val descriptionText = "my channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
