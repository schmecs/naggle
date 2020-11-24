package com.rebeccablum.naggle.notif

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.repo.NagRepository
import com.rebeccablum.naggle.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

const val NAG_ID = "nag_id"
const val NOTIFICATION_REQUEST_ID = 1000
const val CHANNEL_ID = "naggle"
const val CHANNEL_NAME = "com.rebeccablum.naggle"
const val CHANNEL_DESCRIPTION = "Notify user of their tasks"
const val ACTION_DISMISS_NAG = "com.rebeccablum.naggle.ACTION_DISMISS"

class NagNotificationManager(
    private val nagRepository: NagRepository,
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    private val notificationJob = Job()
    private val coroutineScope = CoroutineScope(notificationJob + Dispatchers.Main)
    private lateinit var receiver: BroadcastReceiver

    fun start() {
        createNotificationChannel()
        registerReceiver()
        displayNotificationOnNextNag()
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter(ACTION_DISMISS_NAG)
        receiver = NotificationDismissedReceiver()
        context.registerReceiver(receiver, intentFilter)
    }

    private fun displayNotificationOnNextNag() {
        coroutineScope.launch {
            nagRepository.getNagToNotify()
                .filterNotNull()
                .collect {
                    sendNotification(it)
                }
        }
    }

    private fun sendNotification(nag: Nag) {
        notificationManager.notify(NOTIFICATION_REQUEST_ID, createNotification(nag))
    }

    private fun createNotification(nag: Nag): Notification {

        val pendingIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.run { PendingIntent.getActivity(context, 0, this, 0) }

        val onDismissPendingIntent =
            Intent(ACTION_DISMISS_NAG).apply {
                putExtra(NAG_ID, nag.id)
            }.run {
                PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_REQUEST_ID,
                    this,
                    FLAG_CANCEL_CURRENT
                )
            }

        return Notification.Builder(context, CHANNEL_ID)
            .setChannelId(CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(onDismissPendingIntent)
            .setContentTitle("Do This Next")
            .setContentText(nag.description)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(false)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class NotificationDismissedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_DISMISS_NAG) {
                val nagId = intent.getIntExtra(NAG_ID, -1)
                coroutineScope.launch {
                    nagRepository.markNagDismissed(nagId)
                }
            }
        }
    }
}
