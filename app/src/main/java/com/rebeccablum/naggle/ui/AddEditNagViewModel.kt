package com.rebeccablum.naggle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.models.Priority
import com.rebeccablum.naggle.repo.NagRepository
import com.rebeccablum.naggle.util.SingleLiveEvent
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

open class AddEditNagViewModel(private val repository: NagRepository) : ViewModel() {

    val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
    val timeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    private val combinedFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")

    val id = MutableLiveData<Int>()
    val description = MutableLiveData<String>()
    val priorityString = MutableLiveData<String>()
    val startDateText = MutableLiveData<String>(dateFormatter.format(OffsetDateTime.now()))
    val startTimeText = MutableLiveData<String>(timeFormatter.format(OffsetDateTime.now()))

    val actionEditDate = SingleLiveEvent<Unit>()
    val actionEditTime = SingleLiveEvent<Unit>()
    val actionDismiss = SingleLiveEvent<Unit>()
    val errorDisplay = SingleLiveEvent<String>()

    fun onEditDate() {
        actionEditDate.call()
    }

    fun onEditTime() {
        actionEditTime.call()
    }

    fun onDateSelected(dateMs: Long) {
        val offsetDateTime = OffsetDateTime.from(
            Instant.ofEpochMilli(dateMs).atZone(ZoneId.of("UTC"))
        )
        startDateText.value = dateFormatter.format(offsetDateTime)
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val amPm = if (hour >= 12) "PM" else "AM"
        val mod = hour % 12
        val clockHour = if (mod == 0) 12 else mod
        startTimeText.value = String.format("%02d:%02d %s", clockHour, minute, amPm)
    }

    fun onSave() {
        val valid = validate()
        if (valid) {
            try {
                val nag = getNagFromInputs()
                saveLocally(nag)
            } catch (e: Exception) {
                errorDisplay.value = e.message
            }
        }
    }

    fun onCancel() {
        actionDismiss.call()
    }

    fun onCompleted() {
        viewModelScope.launch {
            repository.markNagCompleted(id.value!!)
            actionDismiss.call()
        }
    }

    fun onDelete() {
        viewModelScope.launch {
            repository.deleteNag(id.value!!)
            actionDismiss.call()
        }
    }

    private fun getNagFromInputs(): Nag {
        val currentNag = Nag(
            description = description.value!!,
            priority = Priority.valueOf(priorityString.value!!),
            startingAt = OffsetDateTime.from(
                LocalDateTime.parse(
                    (startDateText.value!! + " " + startTimeText.value!!),
                    combinedFormatter
                ).atZone(ZoneId.systemDefault())
            )
        )
        return id.value?.let { return currentNag.copy(id = it) } ?: currentNag
    }

    private fun saveLocally(nag: Nag) {
        viewModelScope.launch {
            try {
                repository.addOrEditNag(nag)
                actionDismiss.call()
            } catch (e: Exception) {
                errorDisplay.value = e.message
            }
        }
    }

    private fun validate(): Boolean {
        if (description.value == null || priorityString.value == null) {
            errorDisplay.value = "Please fill out all required fields"
            return false
        }
        return true
    }
}
