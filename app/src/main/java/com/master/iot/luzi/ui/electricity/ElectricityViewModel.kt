package com.master.iot.luzi.ui.electricity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.R
import com.master.iot.luzi.domain.REERepository
import com.master.iot.luzi.ui.electricity.ElectricityViewMode.CHART_VIEW
import com.master.iot.luzi.ui.electricity.ElectricityViewMode.LIST_VIEW
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers


class ElectricityViewModel : ViewModel() {

    val viewMode = MutableLiveData<ElectricityViewMode>().apply {
        value = LIST_VIEW
    }

    val dataPrices = MutableLiveData<EMPPrices>().apply {
        value = EMPPricesLoading("Loading data")
    }

    private val repository: REERepository = REERepository()
    private val compositeDisposable = CompositeDisposable()

    fun clearDisposables() = compositeDisposable.clear()

    fun getReeApiData() {
        compositeDisposable.add(
            repository.getEMPPerHour()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { dataPrices.value = it })
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