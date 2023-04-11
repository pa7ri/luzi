package com.master.iot.luzi.ui.rewards.prizes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PrizesViewModel @Inject constructor() : ViewModel() {
    val prizes = MutableLiveData<List<PrizeItem>>().apply {
        value = emptyList()
    }

    fun getPrizes() {
        prizes.value = getPrizesList()
    }
}