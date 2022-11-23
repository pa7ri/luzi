package com.master.iot.luzi.domain.mapper

import com.mapbox.geojson.Point
import com.master.iot.luzi.*
import com.master.iot.luzi.data.mtpetrol.*
import com.master.iot.luzi.domain.dto.MTPetrolLocationItem
import com.master.iot.luzi.domain.dto.MTPetrolProductItem
import com.master.iot.luzi.domain.dto.MTPetrolStationData
import com.master.iot.luzi.domain.utils.PriceIndicator
import com.master.iot.luzi.domain.utils.PriceIndicatorUtils
import com.master.iot.luzi.domain.utils.containsProduct
import com.master.iot.luzi.domain.utils.getAveragePrice
import java.text.NumberFormat
import java.util.*

class MTPetrolMapper {
    companion object {

        private val DOUBLE_FORMAT = NumberFormat.getInstance(Locale.FRANCE)

        fun MTPetrolCCAAResponse.toMTPetrolLocationData(): List<MTPetrolLocationItem> {
            return map {
                MTPetrolLocationItem(
                    idCcaa = it.IDCCAA,
                    ccaa = it.CCAA
                )
            }
        }

        fun MTPetrolProvincesResponse.toMTPetrolLocationData(): List<MTPetrolLocationItem> {
            return map {
                MTPetrolLocationItem(
                    idCcaa = it.IDCCAA,
                    ccaa = it.CCAA,
                    idProvince = it.IDPovincia,
                    province = it.Provincia
                )
            }
        }

        fun MTPetrolMunicipalitiesResponse.toMTPetrolLocationData(): List<MTPetrolLocationItem> {
            return map {
                MTPetrolLocationItem(
                    idCcaa = it.IDCCAA,
                    ccaa = it.CCAA,
                    idProvince = it.IDProvincia,
                    province = it.Provincia,
                    idMunicipality = it.IDMunicipio,
                    municipality = it.Municipio
                )
            }
        }

        fun MTPetrolPricesResponse.toMTPetrolPricesData(selectedProduct: String): List<MTPetrolStationData> {
            return ListaEESSPrecio.map {
                val products = it.getProductList()
                MTPetrolStationData(
                    petrolStationName = it.rotulo,
                    petrolStationId = it.iDEESS,
                    point = Point.fromLngLat(
                        DOUBLE_FORMAT.parse(it.longitud)?.toDouble() ?: 0.0,
                        DOUBLE_FORMAT.parse(it.latitud)?.toDouble() ?: 0.0
                    ),
                    products = products
                )
            }.apply {
                val averageValue = this.getAveragePrice(selectedProduct)
                map {
                    it.indicator =
                        if (!it.products.containsProduct(selectedProduct)) PriceIndicator.UNKNOWN
                        else PriceIndicatorUtils.getPetrolIndicator(
                            it.products.first { it.id == selectedProduct }.price,
                            averageValue
                        )
                }
            }
        }

        private fun MTPetrolPriceItem.getProductList(): List<MTPetrolProductItem> {
            val products = mutableListOf<MTPetrolProductItem>()
            if (precioBiodiesel.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_BIODIESEL, "8", DOUBLE_FORMAT.parse(precioBiodiesel).toDouble()
                    )
                )
            }
            if (precioBioetanol.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_BIOETHANOL, "16", DOUBLE_FORMAT.parse(precioBioetanol).toDouble()
                    )
                )
            }
            if (precioGasesLicuadosDelPetroleo.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GAS_LICUADO, "17",
                        DOUBLE_FORMAT.parse(precioGasesLicuadosDelPetroleo).toDouble()
                    )
                )
            }
            if (precioGasNaturalComprimido.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GAS_COMPRIMIDO, "18",
                        DOUBLE_FORMAT.parse(precioGasNaturalComprimido).toDouble()
                    )
                )
            }
            if (precioGasNaturalLicuado.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GAS_NATURAL_LICUADO, "19",
                        DOUBLE_FORMAT.parse(precioGasNaturalLicuado).toDouble()
                    )
                )
            }
            if (precioGasoleoA.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLEO_A, "4", DOUBLE_FORMAT.parse(precioGasoleoA).toDouble()
                    )
                )
            }
            if (precioGasoleoPremium.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLEO_PREMIUM,
                        "5",
                        DOUBLE_FORMAT.parse(precioGasoleoPremium).toDouble()
                    )
                )
            }
            if (precioGasoleoB.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLEO_B, "6", DOUBLE_FORMAT.parse(precioGasoleoB).toDouble()
                    )
                )
            }
            if (precioGasolina95E5.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_95_E5, "1",
                        DOUBLE_FORMAT.parse(precioGasolina95E5).toDouble()
                    )
                )
            }
            if (precioGasolina95E5Premium.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_95_E5_PREMIUM, "20",
                        DOUBLE_FORMAT.parse(precioGasolina95E5Premium).toDouble()
                    )
                )
            }
            if (precioGasolina95E10.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_95_E10,
                        "23",
                        DOUBLE_FORMAT.parse(precioGasolina95E10).toDouble()
                    )
                )
            }
            if (precioGasolina98E5.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_98_E5,
                        "3",
                        DOUBLE_FORMAT.parse(precioGasolina98E5).toDouble()
                    )
                )
            }
            if (precioGasolina98E10.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_98_E10,
                        "21",
                        DOUBLE_FORMAT.parse(precioGasolina98E10).toDouble()
                    )
                )
            }
            if (precioHidrogeno.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_HIDROGENO, "22", DOUBLE_FORMAT.parse(precioHidrogeno).toDouble()
                    )
                )
            }
            return products
        }
    }
}