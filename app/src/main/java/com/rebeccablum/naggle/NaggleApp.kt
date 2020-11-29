package com.rebeccablum.naggle

import android.app.Application
import android.content.Intent
import com.rebeccablum.naggle.di.initKoin
import com.rebeccablum.naggle.notif.NagNotificationManager
import com.rebeccablum.naggle.notif.NagNotificationService
import org.koin.android.ext.android.inject

class NaggleApp : Application() {

    private val nagNotificationManager: NagNotificationManager by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin()

        NagNotificationService.enqueueWork(this, Intent())
    }

}
