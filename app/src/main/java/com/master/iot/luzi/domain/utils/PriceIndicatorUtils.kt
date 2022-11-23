package com.master.iot.luzi.domain.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.master.iot.luzi.R

enum class PriceIndicator {
    CHEAP, NORMAL, EXPENSIVE, UNKNOWN
}

class PriceIndicatorUtils {
    companion object {
        /**
         * Set 3 groups to measure if a price is cheap,expensive or normal
         * computing a range around the average price, in this case 15%
         **/
        fun getIndicator(value: Double, average: Double): PriceIndicator {
            val bottomBoundaries = average - average * 0.15
            val topBoundaries = average + average * 0.15
            return when (value) {
                in topBoundaries..Double.MAX_VALUE -> PriceIndicator.EXPENSIVE
                in Double.MIN_VALUE..bottomBoundaries -> PriceIndicator.CHEAP
                else -> PriceIndicator.NORMAL
            }
        }

        /**
         * Set 3 groups to measure if a price is cheap,expensive or normal
         * computing a range around the average price, in this case 2.5%
         **/
        fun getPetrolIndicator(value: Double, average: Double): PriceIndicator {
            val bottomBoundaries = average - average * 0.025
            val topBoundaries = average + average * 0.025
            return when (value) {
                in topBoundaries..Double.MAX_VALUE -> PriceIndicator.EXPENSIVE
                in Double.MIN_VALUE..bottomBoundaries -> PriceIndicator.CHEAP
                else -> PriceIndicator.NORMAL
            }
        }

        /**
         * Get indicator color depending on price
         **/
        fun getIndicatorColor(indicator: PriceIndicator): Int =
            when (indicator) {
                PriceIndicator.CHEAP -> R.color.green_200
                PriceIndicator.EXPENSIVE -> R.color.orange_400
                PriceIndicator.NORMAL -> R.color.yellow_400
                else -> R.color.blue_charcoal_200
            }

        /**
         * Get hex color string without transparency #%06x
         **/
        fun getStringColor(context: Context, color: Int): String =
            "#" + Integer.toHexString(ContextCompat.getColor(context, color)).substring(2)
    }
}