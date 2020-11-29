package com.rebeccablum.naggle.notif

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import org.koin.android.ext.android.inject

private const val JOB_NEXT_NOTIFICATION = 1000

class NagNotificationService : JobIntentService() {

    private val nagNotificationManager: NagNotificationManager by inject()

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, NagNotificationService::class.java, JOB_NEXT_NOTIFICATION, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        nagNotificationManager.start()
        if (intent.action == ACTION_DISMISS_NAG) {
            val nagId = intent.getIntExtra(NAG_ID, -1)
            nagNotificationManager.onNotificationDismissed(nagId)
        }
    }
}
