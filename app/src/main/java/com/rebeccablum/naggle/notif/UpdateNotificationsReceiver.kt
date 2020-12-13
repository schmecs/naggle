package com.rebeccablum.naggle.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class UpdateNotificationsReceiver : BroadcastReceiver(), KoinComponent {

    private val nagNotificationManager: NagNotificationManager by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("onReceive notification naggle")
        if (intent != null && context != null) {
            if (intent.action in listOf(ACTION_DISMISS_NAG, ACTION_MARK_COMPLETE)) {
                if (intent.action == ACTION_MARK_COMPLETE) {
                    nagNotificationManager.cancelNotification()
                }
                NagNotificationService.enqueueWork(context, intent)
            }
        }
    }
}
