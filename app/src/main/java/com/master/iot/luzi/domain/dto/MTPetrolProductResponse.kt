package com.master.iot.luzi.domain.dto

class MTPetrolProductResponse : ArrayList<MTPetrolProductResponse.MTPetrolProductItemResponse>(){
    data class MTPetrolProductItemResponse(
        val IDProducto: String,
        val NombreProducto: String,
        val NombreProductoAbreviatura: String
    )
}