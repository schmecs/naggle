package com.rebeccablum.naggle.repo

import com.rebeccablum.naggle.db.NagDao
import com.rebeccablum.naggle.models.Nag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Collections.min

class NagRepository(private val dao: NagDao) {

    fun getAllNags(): Flow<List<Nag>> = dao.getAllNags()

    suspend fun getNagToNotify(): Flow<Nag?> = withContext(Dispatchers.IO) {
        dao.getAllNags()
            .map { allNags ->
                allNags.filter {
                    it.timesDismissed == getMinimumTimesDismissed(allNags)
                }.sortedWith(
                    // TODO make this a comparator on the Nag model
                    compareByDescending<Nag> { nag -> nag.priority.ordinal }
                        .thenBy { nag -> nag.startingAt }
                ).firstOrNull()
            }
    }

    private fun getMinimumTimesDismissed(allNags: List<Nag>): Int {
        return min(allNags.map { it.timesDismissed })
    }

    suspend fun addOrEditNag(nag: Nag) {
        withContext(Dispatchers.IO) {
            dao.insert(nag)
        }
    }

    suspend fun markNagDismissed(nagId: Int) {
        withContext(Dispatchers.IO) {
            dao.getNag(nagId)?.let {
                val newNag = it.copy(timesDismissed = it.timesDismissed + 1)
                addOrEditNag(newNag)
            }
        }
    }

    suspend fun deleteNag(id: Int) {
        dao.deleteNag(id)
    }
}
