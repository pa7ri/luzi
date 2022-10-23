package com.master.iot.luzi.ui.electricity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.R
import com.master.iot.luzi.domain.REERepository
import com.master.iot.luzi.ui.electricity.ElectricityViewMode.CHART_VIEW
import com.master.iot.luzi.ui.electricity.ElectricityViewMode.LIST_VIEW
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ElectricityViewModel @Inject constructor(
    private val repository: REERepository
) : ViewModel() {

    val viewMode = MutableLiveData<ElectricityViewMode>().apply {
        value = LIST_VIEW
    }
    val dataPrices = MutableLiveData<EMPPrices>().apply {
        value = EMPPricesLoading()
    }

    private val compositeDisposable = CompositeDisposable()

    init {
        getReeApiData()
    }


    fun clearDisposables() = compositeDisposable.clear()

    fun getReeApiData() {
        compositeDisposable.add(
            repository.getEMPPerHour()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { dataPrices.value = it }
        )
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