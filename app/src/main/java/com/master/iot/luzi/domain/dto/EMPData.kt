package com.master.iot.luzi.domain.dto

class EMPData(
    val title: String,
    val description: String,
    val magnitude: String,
    val items: List<EMPItem>
)

data class EMPItem(
    val value: Double,
    val percentage: Double,
    val dateTime: String //TODO change to Date
)