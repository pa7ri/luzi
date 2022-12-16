package com.master.iot.luzi.domain.utils

import java.text.SimpleDateFormat
import java.util.*

class DateFormatterUtils {
    companion object {
        private val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        private val formatterLong: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        private val formatterHour: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)

        fun getStringFromDate(date: Date): String = formatter.format(date)

        fun getDateFromString(date: String): Date {
            val shortDate = date.split('+')[0]
            return formatterLong.parse(shortDate) ?: Date()
        }

        fun Date.getHourFromDate(): String = formatterHour.format(this)

    }
}

enum class DateType {
    PAST, FUTURE
}