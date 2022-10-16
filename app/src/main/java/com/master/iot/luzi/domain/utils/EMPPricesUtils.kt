package com.master.iot.luzi.domain.utils

import com.master.iot.luzi.domain.dto.EMPItem

fun List<EMPItem>.getMinPrice(): Double = minOfOrNull { it.value } ?: 0.0
fun List<EMPItem>.getMaxPrice(): Double = maxOfOrNull { it.value } ?: 0.0
fun List<EMPItem>.getAveragePrice(): Double = map { it.value }.average()