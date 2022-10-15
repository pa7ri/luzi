package com.master.iot.luzi.data

import com.master.iot.luzi.data.ree.ReeAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {
    companion object {
        val instance: NetworkService = NetworkService()
    }

    private val BASE_URL = "https://apidatos.ree.es"

    private val retrofitInstance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    fun getReeApi(): ReeAPI = retrofitInstance.create(ReeAPI::class.java)
}