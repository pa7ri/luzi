package com.master.iot.luzi.domain.utils

import com.master.iot.luzi.domain.dto.MTPetrolProductItem
import com.master.iot.luzi.domain.dto.MTPetrolStationData

fun List<MTPetrolStationData>.getAveragePrice(selectedProduct: String): Double =
    flatMap { it.products.filter { it.id == selectedProduct }.map { it.price } }.average()

fun List<MTPetrolStationData>.getGasStationsByMunicipality(idMunicipality: String): List<MTPetrolStationData> =
    filter { it.idMunicipality == idMunicipality }

fun List<MTPetrolStationData>.getCheapestGasStation(selectedProduct: String): MTPetrolStationData? {
    return minByOrNull { it.products.firstOrNull { it.id == selectedProduct }?.price ?: it.products.first().price }
}

fun List<MTPetrolProductItem>.containsProduct(selectedProduct: String): Boolean =
    !none { it.id == selectedProduct }
