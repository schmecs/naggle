package com.rebeccablum.naggle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rebeccablum.naggle.models.Nag
import java.time.Duration
import java.time.OffsetDateTime

class NagViewModel(val nag: Nag) : ViewModel() {
    private val daysBetweenCalc = Duration.between(nag.startingAt, OffsetDateTime.now())
    val daysOld = MutableLiveData<String>(daysBetweenCalc.toDays().toString() + " days old")
}
