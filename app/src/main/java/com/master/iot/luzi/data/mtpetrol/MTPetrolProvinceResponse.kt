package com.master.iot.luzi.data.mtpetrol

class MTPetrolProvincesResponse : ArrayList<MTPetrolProvinceItem>()

data class MTPetrolProvinceItem(
    val CCAA: String,
    val IDCCAA: String,
    val IDPovincia: String, // Report typo bug to mtpetrol
    val Provincia: String
)