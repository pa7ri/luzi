package com.master.iot.luzi.ui.petrol

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.domain.MTPetrolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PetrolViewModel @Inject constructor(
    private val repository: MTPetrolRepository
) : ViewModel() {


    val petrolPrices = MutableLiveData<MTPetrolPrices>().apply {
        value = MTPetrolPricesLoading()
    }

    private val compositeDisposable = CompositeDisposable()

    fun clearDisposables() = compositeDisposable.clear()

    fun updateData(idProvince: Int) {
        petrolPrices.value = MTPetrolPricesLoading()
        compositeDisposable.add(
            repository.getPetrolPricesByProvince(idProvince).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ petrolPrices.value = it },
                    { petrolPrices.value = MTPetrolPricesError(it.localizedMessage ?: "") }
                )
        )
    }

}