package com.master.iot.luzi.ui.petrol

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PetrolViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is petrol Fragment"
    }
    val text: LiveData<String> = _text
}