package com.master.iot.luzi.domain.dto

data class MTPetrolLocation(val data: List<MTPetrolLocationItem>)

data class MTPetrolLocationItem(
    val idCcaa: String,
    val idMunicipality: String = "",
    val idProvince: String = "",
    val ccaa: String,
    val municipality: String = "",
    val province: String = ""
)