package com.master.iot.luzi.domain.mapper

import com.mapbox.geojson.Point
import com.master.iot.luzi.*
import com.master.iot.luzi.data.mtpetrol.*
import com.master.iot.luzi.domain.dto.MTPetrolLocationItem
import com.master.iot.luzi.domain.dto.MTPetrolPricesData
import com.master.iot.luzi.domain.dto.MTPetrolProductItem
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

        fun MTPetrolPricesResponse.toMTPetrolPricesData(): List<MTPetrolPricesData> {
            return ListaEESSPrecio.map {
                MTPetrolPricesData(
                    petrolStationName = it.rotulo,
                    point = Point.fromLngLat(
                        DOUBLE_FORMAT.parse(it.longitud)?.toDouble() ?: 0.0,
                        DOUBLE_FORMAT.parse(it.latitud)?.toDouble() ?: 0.0
                    ),
                    products = it.getProductList()
                )
            }
        }

        private fun MTPetrolPriceItem.getProductList(): List<MTPetrolProductItem> {
            val products = mutableListOf<MTPetrolProductItem>()
            if (precioBiodiesel.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_BIODIESEL, DOUBLE_FORMAT.parse(precioBiodiesel).toDouble()
                    )
                )
            } else if (precioBioetanol.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_BIOETHANOL, DOUBLE_FORMAT.parse(precioBioetanol).toDouble()
                    )
                )
            } else if (precioGasesLicuadosDelPetroleo.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GAS_LICUADO,
                        DOUBLE_FORMAT.parse(precioGasesLicuadosDelPetroleo).toDouble()
                    )
                )
            } else if (precioGasNaturalComprimido.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GAS_COMPRIMIDO,
                        DOUBLE_FORMAT.parse(precioGasNaturalComprimido).toDouble()
                    )
                )
            } else if (precioGasoleoA.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLEO_A, DOUBLE_FORMAT.parse(precioGasoleoA).toDouble()
                    )
                )
            } else if (precioGasoleoB.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLEO_B, DOUBLE_FORMAT.parse(precioGasoleoB).toDouble()
                    )
                )
            } else if (precioGasoleoPremium.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLEO_PREMIUM, DOUBLE_FORMAT.parse(precioGasoleoPremium).toDouble()
                    )
                )
            } else if (precioGasolina95E5.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_95_E5,
                        DOUBLE_FORMAT.parse(precioGasolina95E5).toDouble()
                    )
                )
            } else if (precioGasolina95E5Premium.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_95_E5_PREMIUM,
                        DOUBLE_FORMAT.parse(precioGasolina95E5Premium).toDouble()
                    )
                )
            } else if (precioGasolina95E10.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_95_E10, DOUBLE_FORMAT.parse(precioGasolina95E10).toDouble()
                    )
                )
            } else if (precioGasolina98E5.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_98_E5, DOUBLE_FORMAT.parse(precioGasolina98E5).toDouble()
                    )
                )
            } else if (precioGasolina98E10.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_GASOLINA_98_E10, DOUBLE_FORMAT.parse(precioGasolina98E10).toDouble()
                    )
                )
            } else if (precioHidrogeno.isNotEmpty()) {
                products.add(
                    MTPetrolProductItem(
                        PETROL_HIDROGENO, DOUBLE_FORMAT.parse(precioHidrogeno).toDouble()
                    )
                )
            }
            return products
        }
    }
}