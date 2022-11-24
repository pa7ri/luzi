package com.master.iot.luzi.data.mtpetrol.mapper

import com.master.iot.luzi.data.ree.EMPPerHourResponse
import com.master.iot.luzi.data.ree.Value
import com.master.iot.luzi.domain.dto.EMPData
import com.master.iot.luzi.domain.dto.EMPItem
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.getDateFromString
import com.master.iot.luzi.domain.utils.PriceIndicatorUtils
import com.master.iot.luzi.domain.utils.getAveragePrice
import com.master.iot.luzi.ui.FeeType
import kotlin.math.roundToLong

class REEMapper {
    companion object {
        fun EMPPerHourResponse.toEMPData(feeType: FeeType): EMPData {
            val index = if (feeType == FeeType.PVPC) 0 else 1
            return EMPData(
                title = included[index].attributes.title,
                description = included[index].attributes.description ?: "",
                magnitude = included[index].attributes.magnitude,
                items = included[index].attributes.values.map { it.toEMPItem() }
            ).apply {
                val average = items.getAveragePrice()
                items.map { it.indicator = PriceIndicatorUtils.getIndicator(it.value, average) }
            }
        }


        private fun Value.toEMPItem(): EMPItem =
            EMPItem(
                value = value.roundToLong() / 1000.0,
                percentage = percentage,
                dateTime = getDateFromString(datetime)
            )
    }

}