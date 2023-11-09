package com.sgtech.freevices.views.ui.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OverviewViewModel : ViewModel() {
    val pieChartData = MutableLiveData<Map<String, Float>>()
    val totalData = MutableLiveData<Map<String, Float>>()

    fun updatePieChartData(newData: Map<String, Float>) {
        pieChartData.value = newData
    }

    fun updateTotalData(newData: Map<String, Float>) {
        totalData.value = newData
    }

    fun getTobaccoValue(tobaccoValue: Float): Float {
        return tobaccoValue
    }
}