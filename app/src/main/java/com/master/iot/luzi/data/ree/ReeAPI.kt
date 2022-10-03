package com.master.iot.luzi.data.ree

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ReeAPI {
    @GET("/posts/{id}")
    fun getPostWithID(@Path("id") id: Int): Call<Post>
}