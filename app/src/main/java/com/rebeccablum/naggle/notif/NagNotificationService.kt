package com.rebeccablum.naggle.notif

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.rebeccablum.naggle.repo.NagRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private const val JOB_NEXT_NOTIFICATION = 1000

class NagNotificationService : JobIntentService() {

    private val nagRepository: NagRepository by inject()
    private val coroutineScope = CoroutineScope(Job())

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, NagNotificationService::class.java, JOB_NEXT_NOTIFICATION, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val nagId = intent.getIntExtra(NAG_ID, -1)
        if (intent.action == ACTION_DISMISS) {
            coroutineScope.launch {
                nagRepository.markNagDismissed(nagId)
            }
        } else if (intent.action == ACTION_MARK_COMPLETE) {
            coroutineScope.launch {
                nagRepository.markNagCompleted(nagId)
            }
        }
    }
}
