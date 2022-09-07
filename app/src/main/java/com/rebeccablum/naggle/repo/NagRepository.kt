package com.rebeccablum.naggle.repo

import com.rebeccablum.naggle.db.NagDao
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.models.Priority.ASAP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Collections.min

class NagRepository(private val dao: NagDao) {

    fun getTodoList(): Flow<List<Nag>> = dao.getTodoList()

    suspend fun getNagToNotify(): Flow<Nag?> = withContext(Dispatchers.IO) {
        dao.getTodoList()
            .map { allNags ->
                allNags.filter { it.started }.sortedWith(
                    compareByDescending<Nag> { nag -> nag.priority.ordinal }
                        .thenBy { nag -> nag.timesDismissed }
                        .thenBy { nag -> nag.startingAt }
                ).firstOrNull()
            }
    }

    fun nextScheduledRefresh(): Flow<Calendar> {
        return dao.getTodoList()
            .map { allNags ->
                allNags.filter {
                    !it.started
                }.map { it.startingAt }.minOrNull()?.let { nextStartTime ->
                    minOf(nextStartTime, nextDailyRefresh())
                } ?: nextDailyRefresh()
            }
            .map {
                Calendar.getInstance().apply {
                    this.timeInMillis = it.toInstant().toEpochMilli()
                }
            }
    }

    private fun nextDailyRefresh(): OffsetDateTime {
        val now = Calendar.getInstance().time
        val next9am = Calendar.getInstance().apply {
            set(HOUR_OF_DAY, 9)
            if (time < now) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }.timeInMillis
        return OffsetDateTime.from(
            Instant.ofEpochMilli(next9am).atZone(ZoneId.of("UTC"))
        )
    }

    suspend fun getNag(id: Int): Nag? {
        return withContext(Dispatchers.IO) {
            dao.getNag(id)
        }
    }

    suspend fun addOrEditNag(nag: Nag) {
        withContext(Dispatchers.IO) {
            dao.insert(nag)
        }
    }

    suspend fun markNagDismissed(id: Int) {
        withContext(Dispatchers.IO) {
            dao.getNag(id)?.let {
                val newNag = it.copy(timesDismissed = it.timesDismissed + 1)
                addOrEditNag(newNag)
            }
        }
    }

    suspend fun markNagCompleted(id: Int) {
        withContext(Dispatchers.IO) {
            dao.getNag(id)?.let {
                val newNag = it.copy(completed = true)
                addOrEditNag(newNag)
            }
        }
    }

    suspend fun deleteNag(id: Int) {
        dao.deleteNag(id)
    }
}
