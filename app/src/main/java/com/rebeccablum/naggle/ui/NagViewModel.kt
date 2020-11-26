package com.rebeccablum.naggle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rebeccablum.naggle.models.Nag

class NagViewModel(val nag: Nag) : ViewModel() {
    val daysOld = MutableLiveData<String>(nag.daysOld.toString() + " days old")
}
