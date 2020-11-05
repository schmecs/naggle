package com.rebeccablum.naggle.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rebeccablum.naggle.models.Nag
import com.rebeccablum.naggle.repo.NagRepository
import kotlinx.coroutines.launch

class NagListViewModel(private val repository: NagRepository) : ViewModel() {

    var nags: LiveData<List<Nag>> = repository.getAllNags

    init {
        viewModelScope.launch {
            repository.insertNags()
        }
    }
}
