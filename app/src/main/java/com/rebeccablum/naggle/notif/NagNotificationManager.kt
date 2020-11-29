package com.rebeccablum.naggle.notif

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES
import android.os.Build
import android.os.Bundle
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
import timber.log.Timber

const val NAG_ID = "nag_id"
const val NOTIFICATION_REQUEST_ID = 1000
const val CHANNEL_ID = "naggle"
const val CHANNEL_NAME = "com.rebeccablum.naggle"
const val CHANNEL_DESCRIPTION = "Notify user of their tasks"
const val ACTION_DISMISS_NAG = "com.rebeccablum.naggle.ACTION_DISMISS"
const val ACTION_GO_TO_NAG = "com.rebeccablum.naggle.ACTION_EDIT"
const val ACTION_MARK_COMPLETE = "com.rebeccablum.naggle.ACTION_MARK_COMPLETE"

class NagNotificationManager(
    private val nagRepository: NagRepository,
    private val context: Context,
    private val notificationManager: NotificationManager
) {
    private val notificationJob = Job()
    private val coroutineScope = CoroutineScope(notificationJob + Dispatchers.Main)

    private var isStarted = false

    fun start() {
        if (!isStarted) {
            createNotificationChannel()
            isStarted = true
            getNextNotification()
        }
    }

    private fun getNextNotification() {
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

    fun onNotificationDismissed(nagId: Int) {
        coroutineScope.launch {
            Timber.d("onNotificationDismissed: $nagId")
            nagRepository.markNagDismissed(nagId)
        }
    }

    private fun createNotification(nag: Nag): Notification {

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtras(Bundle().apply { putInt(NAG_ID, nag.id) })
        intent.addFlags(FLAG_INCLUDE_STOPPED_PACKAGES)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val onDismissIntent =
            Intent(context, UpdateNotificationsReceiver::class.java).apply {
                action = ACTION_DISMISS_NAG
                putExtra(NAG_ID, nag.id)
                addFlags(FLAG_INCLUDE_STOPPED_PACKAGES)
            }
        val onDismissPendingIntent =
            PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_ID,
                onDismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

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
}
