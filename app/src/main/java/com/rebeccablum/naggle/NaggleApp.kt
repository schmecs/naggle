package com.rebeccablum.naggle

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel

class NaggleApp : Application() {

    private val notificationBuilder: Notification.Builder by lazy {
        Notification.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
    }

    override fun onCreate() {
        super.onCreate()
        notificationBuilder
            .setContentTitle("Perma-Notification")
            .setAutoCancel(false)
            .setContentText("Here is some text.")
            .setOngoing(true)
            .build()
    }
}
