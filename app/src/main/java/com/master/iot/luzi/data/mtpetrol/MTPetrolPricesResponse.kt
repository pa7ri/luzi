package com.master.iot.luzi.data.mtpetrol

data class MTPetrolPricesResponse(
    val Fecha: String,
    val ListaEESSPrecio: List<ListaEESSPrecio>,
    val Nota: String,
    val ResultadoConsulta: String
)