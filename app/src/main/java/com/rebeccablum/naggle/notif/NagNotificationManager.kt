package com.rebeccablum.naggle.notif

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.repo.NagRepository
import com.rebeccablum.naggle.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

const val NAG_ID = "nag_id"
const val NOTIFICATION_REQUEST_ID = 1000
const val CHANNEL_ID = "naggle"
const val CHANNEL_NAME = "com.rebeccablum.naggle"
const val CHANNEL_DESCRIPTION = "Notify user of their tasks"
const val ACTION_DISMISS = "com.rebeccablum.naggle.ACTION_DISMISS"
const val ACTION_EDIT_NAG = "com.rebeccablum.naggle.ACTION_EDIT"
const val ACTION_MARK_COMPLETE = "com.rebeccablum.naggle.ACTION_MARK_COMPLETE"

class NagNotificationManager(
    private val nagRepository: NagRepository,
    private val context: Context,
    private val notificationManager: NotificationManager
) {
    private val notificationManagerJob = Job()
    private var notificationJob: Job? = null
    private val coroutineScope = CoroutineScope(notificationManagerJob + Dispatchers.Main)

    fun start() {
        createNotificationChannel()
        getNextNotification()
    }

    private fun getNextNotification() {
        notificationJob?.cancel()
        notificationJob = coroutineScope.launch {
            nagRepository.getNagToNotify()
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    sendNotification(it)
                }
        }
    }

    private fun sendNotification(nag: Nag) {
        notificationManager.notify(NOTIFICATION_REQUEST_ID, createNotification(nag))
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_REQUEST_ID)
    }

    private fun createNotification(nag: Nag): Notification {
        val pendingIntentFlags = FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE

//        val specificItemIntent = Intent(context, MainActivity::class.java)
//        specificItemIntent.putExtra(NAG_ID, nag.id)
//        val editPendingIntent = getBroadcast(
//            context,
//            NOTIFICATION_REQUEST_ID,
//            specificItemIntent,
//            pendingIntentFlags
//        )
//        val editAction = Notification.Action.Builder(null, "Edit item", editPendingIntent).build()

        val listIntent = Intent(context, MainActivity::class.java)
        val listPendingIntent =
            getActivity(context, 0, listIntent, pendingIntentFlags)

        val onDismissIntent =
            Intent(context, UpdateNotificationsReceiver::class.java).apply {
                action = ACTION_DISMISS
                putExtra(NAG_ID, nag.id)
            }
        val onDismissPendingIntent =
            getBroadcast(
                context,
                NOTIFICATION_REQUEST_ID,
                onDismissIntent,
                pendingIntentFlags
            )

        val completeIntent = Intent(context, UpdateNotificationsReceiver::class.java).apply {
            action = ACTION_MARK_COMPLETE
            putExtra(NAG_ID, nag.id)
        }
        val completePendingIntent = getBroadcast(
            context,
            NOTIFICATION_REQUEST_ID,
            completeIntent,
            pendingIntentFlags
        )
        val completeAction =
            Notification.Action.Builder(null, "Mark complete", completePendingIntent).build()

        return Notification.Builder(context, CHANNEL_ID)
            .setChannelId(CHANNEL_ID)
            .setContentIntent(listPendingIntent)
            .setDeleteIntent(onDismissPendingIntent)
            .setContentTitle("Do This Next")
            .setContentText(nag.description)
            .addAction(completeAction)
//            .addAction(editAction)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(false)
            .build()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESCRIPTION
        }
        notificationManager.createNotificationChannel(channel)
    }
}
