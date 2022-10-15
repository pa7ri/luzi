package com.master.iot.luzi.data.ree

data class EMPPerHourResponse(
    val `data`: Data,
    val included: List<Included>
)

data class Data(
    val attributes: Attributes,
    val id: String,
    val meta: Meta,
    val type: String
)

data class Included(
    val attributes: AttributesX,
    val groupId: Any,
    val id: String,
    val type: String
)

data class Attributes(
    val description: Any,
    val `last-update`: String,
    val title: String
)

data class Meta(
    val `cache-control`: CacheControl
)

data class CacheControl(
    val cache: String
)

data class AttributesX(
    val color: String,
    val composite: Boolean,
    val description: String?,
    val `last-update`: String,
    val magnitude: String,
    val title: String,
    val type: String?,
    val values: List<Value>
)

data class Value(
    val datetime: String,
    val percentage: Double,
    val value: Double
)