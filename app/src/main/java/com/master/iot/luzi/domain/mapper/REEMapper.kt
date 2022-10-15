package com.master.iot.luzi.domain.mapper

import com.master.iot.luzi.data.ree.EMPPerHourResponse
import com.master.iot.luzi.data.ree.Value
import com.master.iot.luzi.domain.dto.EMPData
import com.master.iot.luzi.domain.dto.EMPItem
import com.master.iot.luzi.domain.utils.DateFormatterUtils

class REEMapper {
    companion object {
        fun EMPPerHourResponse.toEMPData(): EMPData =
            EMPData(
                title = included[0].attributes.title,
                description = included[0].attributes.description ?: "",
                magnitude = included[0].attributes.magnitude,
                items = included[0].attributes.values.map { it.toEMPItem() }
            )

        private fun Value.toEMPItem(): EMPItem =
            EMPItem(
                value = value,
                percentage = percentage,
                dateTime = datetime
            )
    }

}