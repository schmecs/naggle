package com.rebeccablum.naggle.repo

import androidx.lifecycle.Transformations
import com.rebeccablum.naggle.db.NagDao
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.models.Priority.*
import kotlinx.coroutines.Dispatchers
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
    val getAllNags = dao.getAllNags()
    val currentNag = Transformations.map(getAllNags) { it.firstOrNull() }

    suspend fun insertNags() {
        withContext(Dispatchers.IO) {
            testNagList.forEach {
                dao.insert(it)
            }
        }
    }
}
