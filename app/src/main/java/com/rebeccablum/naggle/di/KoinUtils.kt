package com.rebeccablum.naggle.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import com.rebeccablum.naggle.db.NaggleDatabase
import com.rebeccablum.naggle.notif.NagNotificationManager
import com.rebeccablum.naggle.repo.NagRepository
import com.rebeccablum.naggle.ui.AddEditNagViewModel
import com.rebeccablum.naggle.ui.NagListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

const val DB_NAME = "naggle_db"

fun Application.initKoin() {
    startKoin {
        androidContext(this@initKoin)
        modules(listOf(applicationModule(), dataModule(), repositoryModule(), viewModelModule()))
    }
}

private fun applicationModule() = module() {
    single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    single {
        NagNotificationManager(
            nagRepository = get(),
            context = androidContext(),
            notificationManager = get()
        )
    }
}

private fun dataModule() = module {
    single {
        Room.databaseBuilder(androidContext(), NaggleDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
            .nagDao()
    }
}

private fun repositoryModule() = module {
    single { NagRepository(dao = get()) }
}

private fun viewModelModule() = module {
    viewModel { NagListViewModel(repository = get()) }
    viewModel { AddEditNagViewModel(repository = get()) }
}
