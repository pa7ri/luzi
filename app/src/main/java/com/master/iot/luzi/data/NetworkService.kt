package com.master.iot.luzi.data

import com.master.iot.luzi.data.mtpetrol.MTPetrolAPI
import com.master.iot.luzi.data.ree.ReeAPI
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {
    companion object {
        val instance: NetworkService = NetworkService()
    }

    private val BASE_REE_URL = "https://apidatos.ree.es"
    private val BASE_MTPETROL_URL = "https://sedeaplicaciones.minetur.gob.es"

    private fun retrofitInstance(baseURL: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    fun getReeApi(): ReeAPI = retrofitInstance(BASE_REE_URL).create(ReeAPI::class.java)

    fun getMTPetrolApi(): MTPetrolAPI =
        retrofitInstance(BASE_MTPETROL_URL).create(MTPetrolAPI::class.java)
}