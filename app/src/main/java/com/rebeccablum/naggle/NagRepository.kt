package com.rebeccablum.naggle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

object NagRepository {

    val testNagList = mutableListOf(Nag("first nag", 1), Nag("second nag", 2), Nag("third nag", 3))
    val getAllNags = MutableLiveData<List<Nag>>()
    val currentNag = Transformations.map(getAllNags) { it.firstOrNull() }

    fun refreshAllNags() {
        getAllNags.value = testNagList
    }

    fun switchFirstTwoNags() {
        if (testNagList.size >= 2) {
            val newSecond = testNagList[0]
            val newFirst = testNagList[1]
            testNagList[0] = newFirst
            testNagList[1] = newSecond
        }
        refreshAllNags()
    }
}
