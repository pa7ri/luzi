package com.master.iot.luzi.ui.electricity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.R
import com.master.iot.luzi.data.NetworkService
import com.master.iot.luzi.data.ree.EMPPerHourJson
import com.master.iot.luzi.ui.electricity.ElectricityViewMode.CHART_VIEW
import com.master.iot.luzi.ui.electricity.ElectricityViewMode.LIST_VIEW
import retrofit2.Call
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
            .getElectricityMarketPriceByHour(
                startDate = "2022-10-15T00:00",
                endDate = "2022-10-16T00:00"
            )
            .enqueue(object : Callback<EMPPerHourJson> {
                override fun onResponse(
                    call: Call<EMPPerHourJson>,
                    response: Response<EMPPerHourJson>
                ) {
                    val post = response.body()
                    post?.let {
                        it.included[0].attributes.values[0].toString()
                    }
                }

                override fun onFailure(call: Call<EMPPerHourJson>, throwable: Throwable) {
                    //_text.value = "Fallito :)"
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