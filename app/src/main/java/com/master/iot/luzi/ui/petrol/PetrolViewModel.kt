package com.master.iot.luzi.ui.petrol

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.data.mtpetrol.MTPetrolPricesResponse
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


    val petrolPrices = MutableLiveData<MTPetrolPricesResponse>()

    private val compositeDisposable = CompositeDisposable()

    fun clearDisposables() = compositeDisposable.clear()

    fun getPetrolPricesByProvince() {
        compositeDisposable.add(
            repository.getPetrolPricesByProvince(28).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ petrolPrices.value = it },
                    { Log.e("TAG", "ERROR mapping") }
                )
        )
    }

}