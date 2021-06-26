package com.rebeccablum.naggle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.repo.NagRepository
import com.rebeccablum.naggle.util.SingleLiveEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

const val NEW_ITEM = -1

class NagListViewModel(private val repository: NagRepository) : ViewModel() {

    val nags = MutableLiveData<List<Nag>>()
    val actionAddEditNag = SingleLiveEvent<Int>()

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
        actionAddEditNag.value = NEW_ITEM
    }

    fun onNagSelected(nag: Nag) {
        actionAddEditNag.value = nag.id
    }
}
