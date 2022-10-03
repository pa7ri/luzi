package com.master.iot.luzi.data.ree

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * public API for Red Electrica de Espana: https://www.ree.es/en/apidatos
 * Query basic format
 * GET /{lang}/datos/{category}/{widget}?[query]
 **/
interface ReeAPI {
    @GET("/posts/{id}")
    fun getPostWithID(@Path("id") id: Int): Call<Post>


}