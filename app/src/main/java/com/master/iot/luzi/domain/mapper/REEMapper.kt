package com.master.iot.luzi.domain.mapper

import com.master.iot.luzi.data.ree.EMPPerHourResponse
import com.master.iot.luzi.data.ree.Value
import com.master.iot.luzi.domain.dto.EMPData
import com.master.iot.luzi.domain.dto.EMPItem
import com.master.iot.luzi.domain.dto.EMPItemIndicator
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.getDateFromString
import com.master.iot.luzi.domain.utils.getAveragePrice

class REEMapper {
    companion object {
        fun EMPPerHourResponse.toEMPData(): EMPData =
            EMPData(
                title = included[0].attributes.title,
                description = included[0].attributes.description ?: "",
                magnitude = included[0].attributes.magnitude,
                items = included[0].attributes.values.map { it.toEMPItem() }
            ).apply {
                val average = items.getAveragePrice()
                items.map { it.setIndicator(average) }
            }

        private fun Value.toEMPItem(): EMPItem =
            EMPItem(
                value = String.format("%.3f", value / 1000).toDouble(),
                percentage = percentage,
                dateTime = getDateFromString(datetime)
            )

        /**
         * Set 3 groups to measure if a price is cheap,expensive or normal
         * computing a range around the average price, in this case 15%
         **/
        private fun EMPItem.setIndicator(average: Double): EMPItem =
            apply {
                val bottomBoundaries = average - average*0.15
                val topBoundaries = average + average*0.15
                indicator = when(value) {
                    in topBoundaries..Double.MAX_VALUE -> EMPItemIndicator.EXPENSIVE
                    in Double.MIN_VALUE..bottomBoundaries -> EMPItemIndicator.CHEAP
                    else -> EMPItemIndicator.NORMAL
                }
            }
    }

}