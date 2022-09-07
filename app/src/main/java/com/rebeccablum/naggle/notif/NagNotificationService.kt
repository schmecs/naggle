package com.rebeccablum.naggle.notif

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.work.*
import com.rebeccablum.naggle.repo.NagRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

const val JOB_NEXT_NOTIFICATION = "next_notification"

// TODO update Koin and use CoroutineWorker
class NagNotificationWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters), KoinComponent {

    private val nagRepository: NagRepository by inject(NagRepository::class.java)
    private val coroutineScope = CoroutineScope(Job())

    companion object {
        private const val ACTION = "action"
        fun buildWorkRequest(action: String, nagId: Int): OneTimeWorkRequest {
            val data = Data.Builder().putString(ACTION, action).putInt(NAG_ID, nagId).build()
            return OneTimeWorkRequestBuilder<NagNotificationWorker>().apply { setInputData(data) }
                .build()
        }
    }

    override fun doWork(): Result {
        val action: String? = inputData.getString(ACTION)
        val nagId: Int = inputData.getInt(NAG_ID, -1)
        if (action != null && nagId != -1) {
            if (action == ACTION_DISMISS) {
                coroutineScope.launch {
                    nagRepository.markNagDismissed(nagId)
                    Timber.d("Dismissed ID $nagId")
                }
            } else if (action == ACTION_MARK_COMPLETE) {
                coroutineScope.launch {
                    nagRepository.markNagCompleted(nagId)
                    Timber.d("Marked complete ID $nagId")
                }
            }
            return Result.success()
        }
        Timber.d("Missing input data: action $action, nagId $nagId")
        return Result.failure()
    }
}
