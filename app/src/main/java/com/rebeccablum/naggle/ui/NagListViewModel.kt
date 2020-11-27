package com.rebeccablum.naggle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.repo.NagRepository
import com.rebeccablum.naggle.util.SingleLiveEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class NagListViewModel(private val repository: NagRepository) : AddEditNagViewModel(repository) {

    val nags = MutableLiveData<List<Nag>>()
    val actionAddEditNag = SingleLiveEvent<Unit>()

    init {
        updateAllNagsLiveData()
    }

    private fun updateAllNagsLiveData() {
        viewModelScope.launch {
            repository.getTodoList()
                .map { it.sortedByDescending { nag -> nag.daysOld } }
                .collect {
                    nags.value = it
                }
        }
    }

    fun onAddNagClicked() {
        clearValues()
        actionAddEditNag.call()
    }

    private fun clearValues() {
        id.value = null
        description.value = null
        priorityString.value = null
        startDateText.value = dateFormatter.format(OffsetDateTime.now())
        startTimeText.value = timeFormatter.format(OffsetDateTime.now())
    }

    fun onOpenToNag(id: Int) {
        viewModelScope.launch {
            repository.getNag(id)?.let { fillValues(it) }
            actionAddEditNag.call()
        }
    }

    fun onNagSelected(nag: Nag) {
        fillValues(nag)
        actionAddEditNag.call()
    }

    private fun fillValues(nag: Nag) {
        id.value = nag.id
        description.value = nag.description
        priorityString.value = nag.priority.name
        startDateText.value = dateFormatter.format(nag.startingAt)
        startTimeText.value = timeFormatter.format(nag.startingAt)
    }
}
