package com.rebeccablum.naggle

import android.app.Application
import com.rebeccablum.naggle.di.initKoin
import com.rebeccablum.naggle.notif.NagAlarmManager
import com.rebeccablum.naggle.notif.NagNotificationManager
import org.koin.android.ext.android.inject
import timber.log.Timber

class NaggleApp : Application() {

    private val nagNotificationManager: NagNotificationManager by inject()
    private val nagAlarmManager: NagAlarmManager by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin()

        Timber.plant(Timber.DebugTree())

        nagNotificationManager.start()
        nagAlarmManager.scheduleRefreshes()
    }

}
