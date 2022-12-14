package com.master.iot.luzi.domain

import com.master.iot.luzi.data.NetworkService
import com.master.iot.luzi.data.mtpetrol.mapper.MTPetrolMapper.Companion.toMTPetrolLocationData
import com.master.iot.luzi.data.mtpetrol.mapper.MTPetrolMapper.Companion.toMTPetrolPricesData
import com.master.iot.luzi.domain.dto.MTPetrolLocation
import com.master.iot.luzi.domain.dto.MTPetrolProductItem
import com.master.iot.luzi.ui.petrol.MTPetrolPrices
import com.master.iot.luzi.ui.petrol.MTPetrolPricesError
import com.master.iot.luzi.ui.petrol.MTPetrolPricesReady
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MTPetrolRepository {

    private val mtPetrolAPI = NetworkService.instance.getMTPetrolApi()

    fun getCCAAList(): Single<MTPetrolLocation> {
        return mtPetrolAPI.getCCAAList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> MTPetrolLocation(data = response.toMTPetrolLocationData()) }
            .onErrorReturn { MTPetrolLocation(emptyList()) }
    }

    fun getProvinceListByCCAA(idCcaa: String): Single<MTPetrolLocation> {
        return mtPetrolAPI.getProvinceListFilterByCCAA(idCCAA = idCcaa)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> MTPetrolLocation(data = response.toMTPetrolLocationData()) }
            .onErrorReturn { MTPetrolLocation(emptyList()) }
    }

    fun getMunicipalityListByProvince(idProvince: String): Single<MTPetrolLocation> {
        return mtPetrolAPI.getMunicipalityListFilterByProvince(idProvince = idProvince)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> MTPetrolLocation(data = response.toMTPetrolLocationData()) }
            .onErrorReturn { MTPetrolLocation(emptyList()) }
    }

    fun getPetrolPricesByProvince(idProvince: String, idProduct: String): Single<MTPetrolPrices> {
        return mtPetrolAPI.getPetrolPricesFilterByProvince(idProvince = idProvince)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> MTPetrolPricesReady(prices = response.toMTPetrolPricesData(idProduct)) as MTPetrolPrices }
            .onErrorReturn { MTPetrolPricesError(it.localizedMessage ?: "") }
    }

    fun getPetrolProducts(): Single<List<MTPetrolProductItem>> {
        return mtPetrolAPI.getPetrolProducts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.map { MTPetrolProductItem(it.NombreProducto, it.IDProducto) } }
            .onErrorReturn { emptyList() }
    }
}