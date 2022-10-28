package com.master.iot.luzi.data.ree

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * public API for Red Electrica de Espana: https://www.ree.es/en/apidatos
 * Query basic format
 * GET /{lang}/datos/{category}/{widget}?[query]
 **/
interface ReeAPI {
    /**
     * Get electricity market prices in real time
     * with customize parameters:
     * [lang] by default es - spanish
     * [geoLimit] by default peninsula
     * [timeTrunc] by default hour
     * [startDate] starting date and time to track data - format: 2018-01-01T00:00
     * [endDate] end date and time to track data - format: 2018-01-01T00:00
     **/
    @GET("/{lang}/datos/mercados/precios-mercados-tiempo-real")
    fun getElectricityMarketPriceByHour(
        @Path("lang") lang: String = "es",
        @Query("geo_limit") geoLimit: String = "peninsula",
        @Query("time_trunc") timeTrunc: String = "hour",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Single<EMPPerHourResponse>

}