package com.master.iot.luzi.domain.dto

import com.mapbox.geojson.Point
import com.master.iot.luzi.domain.utils.PriceIndicator

class MTPetrolPricesData(
    val petrolStationName: String,
    val point: Point,
    val products: List<MTPetrolProductItem>,
    var indicator: PriceIndicator = PriceIndicator.NORMAL
)

data class MTPetrolProductItem(
    val name: String,
    val price: Double
)