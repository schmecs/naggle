package com.rebeccablum.naggle.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class UpdateNotificationsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("onReceive notification naggle")
        if (intent != null && context != null) {
            if (intent.action == ACTION_DISMISS_NAG) {
                NagNotificationService.enqueueWork(context, intent)
            }
        }
    }
}
