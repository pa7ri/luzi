package com.master.iot.luzi.domain.dto

import com.master.iot.luzi.domain.utils.PriceIndicator
import java.util.*

class EMPData(
    val title: String,
    val description: String,
    val magnitude: String,
    val items: List<EMPItem>
)

data class EMPItem(
    val value: Double,
    val percentage: Double,
    val dateTime: Date,
    var indicator: PriceIndicator = PriceIndicator.NORMAL
)