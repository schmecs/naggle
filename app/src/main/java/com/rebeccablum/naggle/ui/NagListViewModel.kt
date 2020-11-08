package com.rebeccablum.naggle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.repo.NagRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NagListViewModel(private val repository: NagRepository) : ViewModel() {

    var nags = MutableLiveData<List<Nag>>()

    init {
        viewModelScope.launch {
            repository.insertNags()
        }
        updateAllNagsLiveData()
    }

    fun updateAllNagsLiveData() {
        viewModelScope.launch {
            repository.getAllNags().collect {
                nags.value = it
            }
        }
    }

}
