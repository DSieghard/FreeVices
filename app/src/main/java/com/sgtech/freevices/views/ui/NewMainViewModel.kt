package com.sgtech.freevices.views.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sgtech.freevices.R
import com.sgtech.freevices.views.ui.theme.ThemeViewModel

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

    private val _tobaccoThirtyDaysData = MutableLiveData<Float>()
    private val _alcoholThirtyDaysData = MutableLiveData<Float>()
    private val _partiesThirtyDaysData = MutableLiveData<Float>()
    private val _othersThirtyDaysData = MutableLiveData<Float>()

    val tobaccoThirtyDaysData: LiveData<Float> get() = _tobaccoThirtyDaysData
    val alcoholThirtyDaysData: LiveData<Float> get() = _alcoholThirtyDaysData
    val partiesThirtyDaysData: LiveData<Float> get() = _partiesThirtyDaysData
    val othersThirtyDaysData: LiveData<Float> get() = _othersThirtyDaysData

    fun updateThirtyDaysLiveDataValues(context: Context, dataMap: Map<String, Float>) {
        _tobaccoThirtyDaysData.value = dataMap[context.getString(R.string.tobacco)] ?: 0f
        _alcoholThirtyDaysData.value = dataMap[context.getString(R.string.alcohol)] ?: 0f
        _partiesThirtyDaysData.value = dataMap[context.getString(R.string.parties)] ?: 0f
        _othersThirtyDaysData.value = dataMap[context.getString(R.string.others)] ?: 0f
    }
    
    private val _tobaccoSixtyDaysData = MutableLiveData<Float>()
    private val _alcoholSixtyDaysData = MutableLiveData<Float>()
    private val _partiesSixtyDaysData = MutableLiveData<Float>()
    private val _othersSixtyDaysData = MutableLiveData<Float>()
    
    val tobaccoSixtyDaysData: LiveData<Float> get() = _tobaccoSixtyDaysData
    val alcoholSixtyDaysData: LiveData<Float> get() = _alcoholSixtyDaysData
    val partiesSixtyDaysData: LiveData<Float> get() = _partiesSixtyDaysData
    val othersSixtyDaysData: LiveData<Float> get() = _othersSixtyDaysData
    
    fun updateSixtyDaysLiveDataValues(context: Context, dataMap: Map<String, Float>) {
        _tobaccoSixtyDaysData.value = dataMap[context.getString(R.string.tobacco)] ?: 0f
        _alcoholSixtyDaysData.value = dataMap[context.getString(R.string.alcohol)] ?: 0f
        _partiesSixtyDaysData.value = dataMap[context.getString(R.string.parties)] ?: 0f
        _othersSixtyDaysData.value = dataMap[context.getString(R.string.others)] ?: 0f
    }
    
    private val _tobaccoThreeMonthsData = MutableLiveData<Float>()
    private val _alcoholThreeMonthsData = MutableLiveData<Float>()
    private val _partiesThreeMonthsData = MutableLiveData<Float>()
    private val _othersThreeMonthsData = MutableLiveData<Float>()
    
    val tobaccoThreeMonthsData: LiveData<Float> get() = _tobaccoThreeMonthsData
    val alcoholThreeMonthsData: LiveData<Float> get() = _alcoholThreeMonthsData
    val partiesThreeMonthsData: LiveData<Float> get() = _partiesThreeMonthsData
    val othersThreeMonthsData: LiveData<Float> get() = _othersThreeMonthsData
    
    fun updateThreeMonthsLiveDataValues(context: Context, dataMap: Map<String, Float>) {
        _tobaccoThreeMonthsData.value = dataMap[context.getString(R.string.tobacco)] ?: 0f
        _alcoholThreeMonthsData.value = dataMap[context.getString(R.string.alcohol)] ?: 0f
        _partiesThreeMonthsData.value = dataMap[context.getString(R.string.parties)] ?: 0f
        _othersThreeMonthsData.value = dataMap[context.getString(R.string.others)] ?: 0f
    }
    
    private val _tobaccoSixMonthData = MutableLiveData<Float>()
    private val _alcoholSixMonthData = MutableLiveData<Float>()
    private val _partiesSixMonthData = MutableLiveData<Float>()
    private val _othersSixMonthData = MutableLiveData<Float>()
    
    val tobaccoSixMonthData: LiveData<Float> get() = _tobaccoSixMonthData
    val alcoholSixMonthData: LiveData<Float> get() = _alcoholSixMonthData
    val partiesSixMonthData: LiveData<Float> get() = _partiesSixMonthData
    val othersSixMonthData: LiveData<Float> get() = _othersSixMonthData
    
    fun updateSixMonthLiveDataValues(context: Context, dataMap: Map<String, Float>) {
        _tobaccoSixMonthData.value = dataMap[context.getString(R.string.tobacco)] ?: 0f
        _alcoholSixMonthData.value = dataMap[context.getString(R.string.alcohol)] ?: 0f
        _partiesSixMonthData.value = dataMap[context.getString(R.string.parties)] ?: 0f
        _othersSixMonthData.value = dataMap[context.getString(R.string.others)] ?: 0f
    }

    private val _displayName = MutableLiveData<String?> (null)
    val displayName: MutableLiveData<String?> = _displayName

    fun setDisplayName(name: String?) {
        if (name == null) {
            _displayName.value = ""
        } else {
            _displayName.value = name
        }
    }
}

object ViewModelProvider {
    private var newMainViewModel: NewMainViewModel? = null
    private var themeViewModel: ThemeViewModel? = null

    fun provideMainViewModel(): NewMainViewModel {
        if (newMainViewModel == null) {
            newMainViewModel = NewMainViewModel()
        }
        return newMainViewModel!!
    }

    fun provideThemeViewModel(): ThemeViewModel {
        if (themeViewModel == null) {
            themeViewModel = ThemeViewModel()
        }
        return themeViewModel!!
    }
}
