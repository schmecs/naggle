package com.rebeccablum.naggle.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.OffsetDateTime

@Entity
data class Nag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val priority: Priority,
    val startingAt: OffsetDateTime,
    val timesDismissed: Int = 0,
    val completed: Boolean = false
) {
    @Ignore
    val started: Boolean = startingAt < OffsetDateTime.now()

    @Ignore
    val daysOld: Long = Duration.between(startingAt, OffsetDateTime.now()).toDays()
}

enum class Priority {
    NORMAL,
    ASAP
}
