package com.master.iot.luzi.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.PREFERENCES_PETROL_ID_CCAA_DEFAULT
import com.master.iot.luzi.PREFERENCES_PETROL_ID_PROVINCE_DEFAULT
import com.master.iot.luzi.domain.MTPetrolRepository
import com.master.iot.luzi.domain.dto.MTPetrolLocationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: MTPetrolRepository
) : ViewModel() {

    val ccaaData = MutableLiveData<List<MTPetrolLocationItem>>().apply {
        value = emptyList()
    }

    val provincesData = MutableLiveData<List<MTPetrolLocationItem>>().apply {
        value = emptyList()
    }

    val municipalitiesData = MutableLiveData<List<MTPetrolLocationItem>>().apply {
        value = emptyList()
    }

    private val compositeDisposable = CompositeDisposable()

    fun clearDisposables() = compositeDisposable.clear()

    fun getCCAAPreferences() {
        compositeDisposable.add(
            repository.getCCAAList().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ ccaaData.value = it.data },
                    { ccaaData.value = emptyList() }
                )
        )
    }

    fun getProvincePreferences(idCCAA: String = PREFERENCES_PETROL_ID_CCAA_DEFAULT) {
        compositeDisposable.add(
            repository.getProvinceByCCAAList(Integer.valueOf(idCCAA))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ provincesData.value = it.data },
                    { provincesData.value = emptyList() }
                )
        )
    }

    fun getMunicipalityPreferences(idProvince: String = PREFERENCES_PETROL_ID_PROVINCE_DEFAULT) {
        compositeDisposable.add(
            repository.getMunicipalityByProvinceList(Integer.valueOf(idProvince))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ municipalitiesData.value = it.data },
                    { municipalitiesData.value = emptyList() }
                )
        )
    }

}