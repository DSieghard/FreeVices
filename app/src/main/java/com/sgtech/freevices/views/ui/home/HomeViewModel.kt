package com.sgtech.freevices.views.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    val pieChartData = MutableLiveData<Map<String, Float>>()
    val totalData = MutableLiveData<Map<String, Float>>()

    fun updatePieChartData(newData: Map<String, Float>) {
        pieChartData.value = newData
    }

    fun updateTotalData(newData: Map<String, Float>) {
        totalData.value = newData
    }
}

