package com.master.iot.luzi.data

import com.master.iot.luzi.data.ree.ReeAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {
    companion object {
        val instance: NetworkService = NetworkService()
    }

    private val BASE_URL = "https://jsonplaceholder.typicode.com"

    private val retrofitInstance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getReeApi(): ReeAPI = retrofitInstance.create(ReeAPI::class.java)

}