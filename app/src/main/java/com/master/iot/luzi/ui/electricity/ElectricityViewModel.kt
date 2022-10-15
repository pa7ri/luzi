package com.master.iot.luzi.ui.electricity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.R
import com.master.iot.luzi.data.NetworkService
import com.master.iot.luzi.data.ree.Post
import com.master.iot.luzi.ui.electricity.ElectricityViewMode.CHART_VIEW
import com.master.iot.luzi.ui.electricity.ElectricityViewMode.LIST_VIEW
import retrofit2.Callback
import retrofit2.Response


class ElectricityViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is electricity Fragment"
    }
    val text: LiveData<String> = _text

    val viewMode = MutableLiveData<ElectricityViewMode>().apply {
        value = LIST_VIEW
    }

    fun getReeApiData() {
        NetworkService.instance
            .getReeApi()
            .getPostWithID(1)
            .enqueue(object : Callback<Post> {
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

    fun switchRenderData() {
        viewMode.value = if (viewMode.value == LIST_VIEW) CHART_VIEW else LIST_VIEW
    }

    fun getFabImageResource(): Int =
        when (viewMode.value) {
            LIST_VIEW -> R.drawable.ic_chart
            else -> R.drawable.ic_list
        }
}