package com.rebeccablum.naggle.notif

import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rebeccablum.naggle.repo.NagRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

const val ACTION_REFRESH = "com.rebeccablum.naggle.ACTION_REFRESH"

class NagAlarmManager(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val nagRepository: NagRepository
) {
    private val alarmSchedulingScope = CoroutineScope(Job())

    fun scheduleNextRefresh() {
        alarmSchedulingScope.launch {
            nagRepository.getNextScheduledRefresh().collect {
                Timber.d("Scheduling next alarm for ${it.time}")
                alarmManager.setExact(RTC, it.timeInMillis, getAlarmPendingIntent())
            }
        }
    }

    private fun getAlarmPendingIntent(): PendingIntent {
        val alarmIntent =
            Intent(context, UpdateNotificationsReceiver::class.java).apply {
                action = ACTION_REFRESH
            }
        return PendingIntent.getBroadcast(
            context,
            NOTIFICATION_REQUEST_ID,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
