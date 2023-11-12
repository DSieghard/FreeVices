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
