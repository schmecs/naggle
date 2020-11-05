package com.rebeccablum.naggle.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rebeccablum.naggle.models.Nag

@Dao
interface NagDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(nag: Nag): Long

    @Query("SELECT * FROM nag LIMIT 100")
    fun getAllNags(): LiveData<List<Nag>>

    @Update
    suspend fun update(nag: Nag)

    @Query("DELETE FROM nag WHERE id == :id")
    suspend fun deleteNag(id: Int)
}
