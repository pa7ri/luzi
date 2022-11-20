package com.master.iot.luzi.domain.utils

enum class PriceIndicator {
    CHEAP, NORMAL, EXPENSIVE
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
    }
}