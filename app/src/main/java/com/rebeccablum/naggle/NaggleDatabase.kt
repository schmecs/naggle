package com.rebeccablum.naggle

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Nag::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NaggleDatabase : RoomDatabase() {

    abstract fun nagDao(): NagDao

    companion object {
        @Volatile
        private var INSTANCE: NaggleDatabase? = null

        fun getInstance(context: Context): NaggleDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(NaggleDatabase::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NaggleDatabase::class.java,
                    "qstreak_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
