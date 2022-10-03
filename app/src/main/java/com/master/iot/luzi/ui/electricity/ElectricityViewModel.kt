package com.master.iot.luzi.ui.electricity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.data.NetworkService
import com.master.iot.luzi.data.ree.Post
import retrofit2.Callback
import retrofit2.Response


class ElectricityViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is electricity Fragment"
    }
    val text: LiveData<String> = _text

    fun getReeApiData() {
        NetworkService.instance
            .getReeApi()
            .getPostWithID(1)
            .enqueue(object : Callback<Post>
            {
                override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                    val post = response.body()
                    post?.let {
                        _text.value = it.title.toString() + it.body.toString()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    _text.value = "Fallito :)"
                }

            })
    }
}