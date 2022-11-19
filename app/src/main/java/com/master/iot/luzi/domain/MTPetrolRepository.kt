package com.master.iot.luzi.domain

import com.master.iot.luzi.data.NetworkService
import com.master.iot.luzi.data.mtpetrol.MTPetrolPricesResponse
import com.master.iot.luzi.domain.dto.MTPetrolLocation
import com.master.iot.luzi.domain.mapper.MTPetrolMapper.Companion.toMTPetrolLocationData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MTPetrolRepository {

    private val mtPetrolAPI = NetworkService.instance.getMTPetrolApi()

    fun getCCAAList(): Single<MTPetrolLocation> {
        return mtPetrolAPI.getCCAAList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> MTPetrolLocation(data = response.toMTPetrolLocationData()) }
            .onErrorReturn {
                MTPetrolLocation(emptyList())
            }
    }

    fun getProvinceListByCCAA(idCcaa: Int): Single<MTPetrolLocation> {
        return mtPetrolAPI.getProvinceListFilterByCCAA(idCCAA = idCcaa)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> MTPetrolLocation(data = response.toMTPetrolLocationData()) }
            .onErrorReturn {
                MTPetrolLocation(emptyList())
            }
    }

    fun getMunicipalityListByProvince(idProvince: Int): Single<MTPetrolLocation> {
        return mtPetrolAPI.getMunicipalityListFilterByProvince(idProvince = idProvince)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> MTPetrolLocation(data = response.toMTPetrolLocationData()) }
            .onErrorReturn {
                MTPetrolLocation(emptyList())
            }
    }

    fun getPetrolPricesByProvince(idProvince: Int): Single<MTPetrolPricesResponse> {
        return mtPetrolAPI.getPetrolPricesFilterByProvince(idProvince = idProvince)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}