package com.rebeccablum.naggle.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity
data class Nag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val priority: Priority,
    val startingAt: OffsetDateTime,
    val timesDismissed: Int = 0,
    val completed: Boolean = false
)

enum class Priority {
    OPTIONAL,
    LOW,
    NORMAL,
    HIGH,
    TODAY,
    IMMEDIATELY
}
