package com.rebeccablum.naggle

import android.app.Application
import android.app.Notification

class NaggleApp : Application() {

    // TODO koin
    private val nagDao: NagDao by lazy {
        NaggleDatabase.getInstance(this.applicationContext).nagDao()
    }
    private val nagRepository by lazy {
        NagRepository(nagDao)
    }
    private val nagNotificationManager by lazy {
        NagNotificationManager(nagRepository, this)
    }

    override fun onCreate() {
        super.onCreate()
        nagNotificationManager.start()
    }
}
