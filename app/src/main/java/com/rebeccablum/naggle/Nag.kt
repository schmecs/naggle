package com.rebeccablum.naggle

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity
data class Nag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val priority: Priority,
    val startingAt: OffsetDateTime
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    TODAY,
    IMMEDIATELY
}
