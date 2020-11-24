package com.rebeccablum.naggle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.models.Priority
import com.rebeccablum.naggle.repo.NagRepository
import com.rebeccablum.naggle.util.SingleLiveEvent
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

open class AddEditNagViewModel(private val repository: NagRepository) : ViewModel() {

    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    private val combinedFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")

    val id = MutableLiveData<Int>()
    val description = MutableLiveData<String>()
    val priorityString = MutableLiveData<String>()
    val startDateText = MutableLiveData<String>(dateFormatter.format(OffsetDateTime.now()))
    val startTimeText = MutableLiveData<String>(timeFormatter.format(OffsetDateTime.now()))

    val actionDismiss = SingleLiveEvent<Unit>()
    val errorDisplay = SingleLiveEvent<String>()

    fun onSave() {
        val valid = validate()
        if (valid) {
            val nag = getNagFromInputs()
            saveLocally(nag)
        }
    }

    fun onCancel() {
        actionDismiss.call()
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
