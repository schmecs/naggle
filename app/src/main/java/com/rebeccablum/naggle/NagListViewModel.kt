package com.rebeccablum.naggle

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NagListViewModel : ViewModel() {

    private lateinit var dao: NagDao
    private lateinit var repository: NagRepository
    lateinit var nags: LiveData<List<Nag>>

    // TODO DI
    fun init(context: Context) {
        dao = NaggleDatabase.getInstance(context).nagDao()
        repository = NagRepository(dao)
        nags = repository.getAllNags
        viewModelScope.launch {
            repository.insertNags()
        }
    }
}
