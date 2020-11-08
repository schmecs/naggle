package com.rebeccablum.naggle.repo

import com.rebeccablum.naggle.db.NagDao
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.models.Priority.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

class NagRepository(private val dao: NagDao) {

    val testNagList = mutableListOf(
        Nag(
            1,
            "first nag",
            HIGH,
            OffsetDateTime.now()
        ),
        Nag(
            2,
            "second nag",
            MEDIUM,
            OffsetDateTime.now().minusDays(1)
        ),
        Nag(
            3,
            "third nag",
            IMMEDIATELY,
            OffsetDateTime.now().minusDays(7)
        )
    )

    fun getAllNags(): Flow<List<Nag>> = dao.getAllNags()

    suspend fun getNagToNotify(): Flow<Nag> = withContext(Dispatchers.IO) {
        dao.getAllNags()
            .map {
                it.sortedWith(
                    // TODO make this a comparator on the Nag model
                    compareByDescending<Nag> { nag -> nag.priority.ordinal }
                        .thenBy { nag -> nag.startingAt }
                        .thenByDescending { nag -> nag.timesDismissed }
                ).first()
            }
    }

    suspend fun insertNags() {
        withContext(Dispatchers.IO) {
            testNagList.forEach {
                dao.insert(it)
            }
        }
    }

    suspend fun markNagDismissed(nag: Nag) {
        val newNag = nag.copy(timesDismissed = nag.timesDismissed + 1)
        updateNag(newNag)
    }

    suspend fun updateNag(nag: Nag) {
        dao.insert(nag)
    }
}
