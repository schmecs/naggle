package com.rebeccablum.naggle.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rebeccablum.naggle.models.Nag
import kotlinx.coroutines.flow.Flow

@Dao
interface NagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nag: Nag): Long

    @Query("SELECT * FROM nag") // TODO only nags that are not done
    fun getAllNags(): Flow<List<Nag>>

    @Query("SELECT * FROM nag WHERE id == :id LIMIT 1")
    suspend fun getNag(id: Int): Nag?

    @Update
    suspend fun update(nag: Nag)

    @Query("DELETE FROM nag WHERE id == :id")
    suspend fun deleteNag(id: Int)
}
