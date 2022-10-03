package com.master.iot.luzi.data.ree

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Post {
    @SerializedName("userId")
    @Expose
    var userId = 0

    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("body")
    @Expose
    var body: String? = null
}