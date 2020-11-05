package com.rebeccablum.naggle

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

const val NAG_ID = "nag_id"
const val CHANNEL_ID = "nag channel"

class NagNotificationManager(
    private val nagRepository: NagRepository,
    private val context: Context
) {

    private val notificationBuilder: Notification.Builder by lazy {
        Notification.Builder(context, CHANNEL_ID)
    }

    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun start() {
        createNotificationChannel()
        displayNotificationOnNextNag()
    }

    private fun displayNotificationOnNextNag() {
        nagRepository.currentNag.observeForever { nag ->
            nag?.let {
                sendNotification(it)
            }
        }
    }

    // TODO handle interaction with notification


    private fun sendNotification(nag: Nag) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            1,
            createNotification(nag)
        )
    }

    private fun createNotification(nag: Nag): Notification {
        val onDismissIntent = Intent(context, OnDismissBroadcastReceiver::class.java).apply {
            extras?.putInt(NAG_ID, nag.id)
        }
        val onDismissPendingIntent =
            PendingIntent.getBroadcast(context, 0, onDismissIntent, 0)

        return notificationBuilder
            .setDeleteIntent(onDismissPendingIntent)
            .setContentTitle("Next Nag")
            .setContentText(nag.description)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(false)
            .build()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // TODO constants
            val name = "nag channel"
            val descriptionText = "nag the heck out of the user"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }
}
