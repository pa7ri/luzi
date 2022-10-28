package com.master.iot.luzi.domain.utils

import com.master.iot.luzi.domain.dto.EMPItem
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.getHourFromDate
import java.math.BigDecimal

fun List<EMPItem>.getMinPrice(): Double = minOfOrNull { it.value } ?: 0.0
fun List<EMPItem>.getMaxPrice(): Double = maxOfOrNull { it.value } ?: 0.0
fun List<EMPItem>.getAveragePrice(): Double = map { it.value }.average()

fun Double.toPriceString(): String =
    toBigDecimal().setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()

fun List<EMPItem>.getMinHour(): String =
    get(indexOfFirst { it.value == getMinPrice() }).dateTime.getHourFromDate()

fun List<EMPItem>.getMaxHour(): String =
    get(indexOfFirst { it.value == getMaxPrice() }).dateTime.getHourFromDate()