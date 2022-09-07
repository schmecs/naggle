package com.rebeccablum.naggle.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class UpdateNotificationsReceiver : BroadcastReceiver(), KoinComponent {

    private val nagNotificationManager: NagNotificationManager by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("onReceive notification naggle")
        if (intent != null && context != null) {
            if (intent.action in listOf(ACTION_DISMISS, ACTION_MARK_COMPLETE, ACTION_REFRESH)) {
                nagNotificationManager.start()
                if (intent.action == ACTION_MARK_COMPLETE) {
                    nagNotificationManager.cancelNotification()
                }
                if (intent.action in listOf(ACTION_DISMISS, ACTION_MARK_COMPLETE)) {
                    val workManager = WorkManager.getInstance(context)
                    val workRequest = NagNotificationWorker.buildWorkRequest(
                        intent.action!!,
                        intent.getIntExtra(NAG_ID, -1)
                    )
                    workManager.enqueueUniqueWork(
                        JOB_NEXT_NOTIFICATION,
                        ExistingWorkPolicy.KEEP,
                        workRequest
                    )
                }
            }
        }
    }
}
