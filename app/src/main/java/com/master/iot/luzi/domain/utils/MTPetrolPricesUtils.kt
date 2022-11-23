package com.master.iot.luzi.domain.utils

import com.master.iot.luzi.domain.dto.MTPetrolProductItem
import com.master.iot.luzi.domain.dto.MTPetrolStationData

fun  List<MTPetrolStationData>.getAveragePrice(selectedProduct: String): Double =
    flatMap { it.products.filter { it.id == selectedProduct }.map { it.price }}.average()

fun List<MTPetrolProductItem>.containsProduct(selectedProduct: String): Boolean =
    !none { it.id == selectedProduct }
