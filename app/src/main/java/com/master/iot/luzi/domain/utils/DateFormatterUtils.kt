package com.master.iot.luzi.domain.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class DateFormatterUtils {
    companion object {
        private val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        private val formatterLong: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        private val formatterFull: SimpleDateFormat =
            SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.US)
        private val formatterHour: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)

        var formatterReport: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

        fun getDateFromString(date: String): Date {
            val shortDate = date.split('+')[0]
            return formatterLong.parse(shortDate) ?: Date()
        }

        fun Date.getStringFromDate(): String = formatter.format(this)

        fun Date.getHourFromDate(): String = formatterHour.format(this)

        fun Date.getFullMonthYearHourFromDate(): String = formatterFull.format(this)

        fun Date.getRangeHourFromDate(): String {
            val copyDate = this
            return getHourFromDate() + " - " + copyDate.apply { hours += 1 }.getHourFromDate()
        }

        fun LocalDateTime.getReportDateTime(): String =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(this)
    }
}

enum class DateType {
    PAST, FUTURE
}