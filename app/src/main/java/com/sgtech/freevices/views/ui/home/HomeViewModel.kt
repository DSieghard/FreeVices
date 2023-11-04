package com.sgtech.freevices.views.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgtech.freevices.utils.FirebaseUtils

class HomeViewModel : ViewModel() {
    private val pieChartData = MutableLiveData<Map<String, Float>>()
    private val totalData = MutableLiveData<Map<String, Float>>()
    private val errorData = MutableLiveData<Exception>()
    private val partiesData = MutableLiveData<Map<String, Float>>()
    private val othersData = MutableLiveData<Map<String, Float>>()
    private val alcoholData = MutableLiveData<Map<String, Float>>()
    private val tobaccoData = MutableLiveData<Map<String, Float>>()

    companion object {
        private const val DEFAULT_OPTION = "default"
    }

    fun getPieChartData(): LiveData<Map<String, Float>> {
        return pieChartData
    }

    fun getTotalData(): LiveData<Map<String, Float>> {
        return totalData
    }

    fun getErrorData(): LiveData<Exception> {
        return errorData
    }

    fun getDataFromFirestore(context: Context) {
        FirebaseUtils.getDataFromFirestoreForLastWeek(context, DEFAULT_OPTION, { newData ->
            pieChartData.value = newData.toMap()
        }, { e ->
            errorData.value = e
        })
    }

    fun updatePieChartData(newData: Map<String, Float>) {
        pieChartData.value = newData
    }

    fun updateTotalData(newData: Map<String, Float>) {
        totalData.value = newData
    }

    fun getAlcoholData(context: Context) : LiveData<Map<String, Float>> {
        FirebaseUtils.getDataFromFirestoreForLastWeek(context, "alcohol", { newData ->
            totalData.value = newData.toMap()
        }, { e ->
            errorData.value = e
        })
        return alcoholData
    }

    fun getTobaccoData(context: Context) : LiveData<Map<String, Float>>  {
        FirebaseUtils.getDataFromFirestoreForLastWeek(context, "tobacco", { newData ->
            totalData.value = newData.toMap()
        }, { e ->
            errorData.value = e
        })
        return tobaccoData
    }

    fun getPartiesData(context: Context) : LiveData<Map<String, Float>> {
        FirebaseUtils.getDataFromFirestoreForLastWeek(context, "parties", { newData ->
            totalData.value = newData.toMap()
        }, { e ->
            errorData.value = e
        })
        return partiesData
    }

    fun getOthersData(context: Context) : LiveData<Map<String, Float>> {
        FirebaseUtils.getDataFromFirestoreForLastWeek(context, "others", { newData ->
            totalData.value = newData.toMap()
        }, { e ->
            errorData.value = e
        })
        return othersData
    }
}

