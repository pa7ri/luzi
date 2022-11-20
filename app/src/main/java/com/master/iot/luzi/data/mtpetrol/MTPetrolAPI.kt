package com.master.iot.luzi.data.mtpetrol

import com.master.iot.luzi.PREFERENCES_PETROL_ID_CCAA_DEFAULT
import com.master.iot.luzi.PREFERENCES_PETROL_ID_PROVINCE_DEFAULT
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * public API for Minetur del Gobierno de Espana: https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes
 * Query basic format
 * GET /ServiciosRESTCarburantes/PreciosCarburantes/{baseData}/{filter}/{optionalFilter}/{id}
 **/
interface MTPetrolAPI {
    /**
     * Get list of ccaa available
     **/
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ComunidadesAutonomas/")
    fun getCCAAList(): Single<MTPetrolCCAAResponse>

    /**
     * Get list of provinces by ccaa available
     **/
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProvinciasPorComunidad/{idCCAA}")
    fun getProvinceListFilterByCCAA(
        @Path("idCCAA") idCCAA: String = PREFERENCES_PETROL_ID_CCAA_DEFAULT
    ): Single<MTPetrolProvincesResponse>

    /**
     * Get list of municipalities by province available
     **/
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/MunicipiosPorProvincia/{idProvince}")
    fun getMunicipalityListFilterByProvince(
        @Path("idProvince") idProvince: String = PREFERENCES_PETROL_ID_PROVINCE_DEFAULT
    ): Single<MTPetrolMunicipalitiesResponse>

    /**
     * Get list of prices for petrol filtered by province
     **/
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroProvincia/{idProvince}")
    fun getPetrolPricesFilterByProvince(
        @Path("idProvince") idProvince: String = PREFERENCES_PETROL_ID_PROVINCE_DEFAULT
    ): Single<MTPetrolPricesResponse>

}