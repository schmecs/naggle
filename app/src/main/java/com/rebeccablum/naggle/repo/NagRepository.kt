package com.rebeccablum.naggle.repo

import com.rebeccablum.naggle.db.NagDao
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.models.Priority.ASAP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.util.Collections.min

class NagRepository(private val dao: NagDao) {

    fun getTodoList(): Flow<List<Nag>> = dao.getTodoList()

    suspend fun getNagToNotify(): Flow<Nag?> = withContext(Dispatchers.IO) {
        dao.getTodoList()
            .map { allNags ->
                allNags.filter {
                    shouldNotify(it, getMinimumTimesDismissed(allNags))
                }.sortedWith(
                    compareByDescending<Nag> { nag -> nag.priority.ordinal }
                        .thenBy { nag -> nag.startingAt }
                ).firstOrNull()
            }
    }

    private fun shouldNotify(nag: Nag, minDismissals: Int): Boolean {
        return nag.startingAt < OffsetDateTime.now() && (nag.priority == ASAP || nag.timesDismissed <= minDismissals)
    }

    private fun getMinimumTimesDismissed(allNags: List<Nag>): Int {
        return min(allNags.map { it.timesDismissed })
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
