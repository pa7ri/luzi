package com.master.iot.luzi.domain.mapper

import com.master.iot.luzi.data.mtpetrol.MTPetrolCCAAResponse
import com.master.iot.luzi.data.mtpetrol.MTPetrolMunicipalitiesResponse
import com.master.iot.luzi.data.mtpetrol.MTPetrolProvincesResponse
import com.master.iot.luzi.domain.dto.MTPetrolLocationItem

class MTPetrolMapper {
    companion object {
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
    }
}