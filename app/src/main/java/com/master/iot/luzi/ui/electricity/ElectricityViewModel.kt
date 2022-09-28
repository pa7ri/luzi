package com.master.iot.luzi.ui.electricity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ElectricityViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is electricity Fragment"
    }
    val text: LiveData<String> = _text
}