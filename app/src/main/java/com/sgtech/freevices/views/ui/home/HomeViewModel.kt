package com.sgtech.freevices.views.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val pieChartData = MutableLiveData<Map<String, Float>>()
    private val totalData = MutableLiveData<Map<String, Float>>()

    fun getPieChartData(): LiveData<Map<String, Float>> {
        return pieChartData
    }

    fun getTotalData(): LiveData<Map<String, Float>> {
        return totalData
    }

    fun updatePieChartData(newData: Map<String, Float>) {
        pieChartData.value = newData
    }

    fun updateTotalData(newData: Map<String, Float>) {
        totalData.value = newData
    }
}