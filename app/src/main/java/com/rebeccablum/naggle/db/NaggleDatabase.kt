package com.rebeccablum.naggle.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rebeccablum.naggle.models.Nag

@Database(entities = [Nag::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NaggleDatabase : RoomDatabase() {

    abstract fun nagDao(): NagDao

}
