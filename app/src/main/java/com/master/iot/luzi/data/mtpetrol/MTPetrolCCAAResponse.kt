package com.master.iot.luzi.data.mtpetrol

class MTPetrolCCAAResponse : ArrayList<MTPetrolCCAAItem>()

data class MTPetrolCCAAItem(
    val CCAA: String,
    val IDCCAA: String
)