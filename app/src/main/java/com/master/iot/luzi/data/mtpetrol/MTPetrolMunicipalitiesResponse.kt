package com.master.iot.luzi.data.mtpetrol

class MTPetrolMunicipalitiesResponse : ArrayList<MTPetrolMunicipalitiesItem>()

data class MTPetrolMunicipalitiesItem(
    val CCAA: String,
    val IDCCAA: String,
    val IDMunicipio: String,
    val IDProvincia: String,
    val Municipio: String,
    val Provincia: String
)