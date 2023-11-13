package com.sgtech.freevices.views.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sgtech.freevices.R

class NewMainViewModel {

    private val _tobaccoLiveData = MutableLiveData<Float>()
    private val _alcoholLiveData = MutableLiveData<Float>()
    private val _partiesLiveData = MutableLiveData<Float>()
    private val _othersLiveData = MutableLiveData<Float>()

    val tobaccoLiveData: LiveData<Float> get() = _tobaccoLiveData
    val alcoholLiveData: LiveData<Float> get() = _alcoholLiveData
    val partiesLiveData: LiveData<Float> get() = _partiesLiveData
    val othersLiveData: LiveData<Float> get() = _othersLiveData

    fun updateLiveDataValues(context: Context, dataMap: Map<String, Float>) {
        _tobaccoLiveData.value = dataMap[context.getString(R.string.tobacco)] ?: 0f
        _alcoholLiveData.value = dataMap[context.getString(R.string.alcohol)] ?: 0f
        _partiesLiveData.value = dataMap[context.getString(R.string.parties)] ?: 0f
        _othersLiveData.value = dataMap[context.getString(R.string.others)] ?: 0f
    }

    private val _tobaccoTwoWeekData = MutableLiveData<Float>()
    private val _alcoholTwoWeekData = MutableLiveData<Float>()
    private val _partiesTwoWeekData = MutableLiveData<Float>()
    private val _othersTwoWeekData = MutableLiveData<Float>()

    val tobaccoTwoWeekData: LiveData<Float> get() = _tobaccoTwoWeekData
    val alcoholTwoWeekData: LiveData<Float> get() = _alcoholTwoWeekData
    val partiesTwoWeekData: LiveData<Float> get() = _partiesTwoWeekData
    val othersTwoWeekData: LiveData<Float> get() = _othersTwoWeekData

    fun updateTwoWeekLiveDataValues(context: Context, dataMap: Map<String, Float>) {
        _tobaccoTwoWeekData.value = dataMap[context.getString(R.string.tobacco)] ?: 0f
        _alcoholTwoWeekData.value = dataMap[context.getString(R.string.alcohol)] ?: 0f
        _partiesTwoWeekData.value = dataMap[context.getString(R.string.parties)] ?: 0f
        _othersTwoWeekData.value = dataMap[context.getString(R.string.others)] ?: 0f
    }

    private val _tobaccoOneMonthData = MutableLiveData<Float>()
    private val _alcoholOneMonthData = MutableLiveData<Float>()
    private val _partiesOneMonthData = MutableLiveData<Float>()
    private val _othersOneMonthData = MutableLiveData<Float>()

    val tobaccoOneMonthData: LiveData<Float> get() = _tobaccoOneMonthData
    val alcoholOneMonthData: LiveData<Float> get() = _alcoholOneMonthData
    val partiesOneMonthData: LiveData<Float> get() = _partiesOneMonthData
    val othersOneMonthData: LiveData<Float> get() = _othersOneMonthData

    fun updateOneMonthLiveDataValues(context: Context, dataMap: Map<String, Float>) {
        _tobaccoOneMonthData.value = dataMap[context.getString(R.string.tobacco)] ?: 0f
        _alcoholOneMonthData.value = dataMap[context.getString(R.string.alcohol)] ?: 0f
        _partiesOneMonthData.value = dataMap[context.getString(R.string.parties)] ?: 0f
        _othersOneMonthData.value = dataMap[context.getString(R.string.others)] ?: 0f
    }


}

object ViewModelProvider {
    private var newMainViewModel: NewMainViewModel? = null

    fun provideMainViewModel(): NewMainViewModel {
        if (newMainViewModel == null) {
            newMainViewModel = NewMainViewModel()
        }
        return newMainViewModel!!
    }
}
