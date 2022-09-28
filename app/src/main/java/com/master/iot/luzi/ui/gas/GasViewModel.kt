package com.master.iot.luzi.ui.gas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gas Fragment"
    }
    val text: LiveData<String> = _text
}