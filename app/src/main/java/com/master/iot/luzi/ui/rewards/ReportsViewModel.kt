package com.master.iot.luzi.ui.rewards

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.R
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.getFullMonthYearHourFromDate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor() : ViewModel() {
    val reports = MutableLiveData<List<ReportItem>>().apply {
        value = emptyList()
    }
    val prizes = MutableLiveData<List<ReportItem>>().apply {
        value = emptyList()
    }

    fun getReports(preferences: SharedPreferences) {
        reports.value = listOf(
            ReportItem(
                "Lavadora",
                "",
                R.drawable.ic_petrol,
                Calendar.getInstance().apply { time = Date() }.time.getFullMonthYearHourFromDate()
            ),
            ReportItem(
                "Microondas",
                "",
                R.drawable.ic_bulb,
                Calendar.getInstance().apply { time = Date() }.time.getFullMonthYearHourFromDate()
            )
        )
    }

    fun getPrizes() {
        prizes.value = listOf(
            ReportItem(
                "Descuento 2% con Endesa",
                "Usa el código LUZI.2${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com. Este cupon no es acumulable y tiene uso unico.",
                R.mipmap.star_1,
                level = 1
            ),
            ReportItem(
                "Recupera 1x20€",
                "Solo para nuevos usuarios: Por cada 20€ gastados en tu factura de la luz, recupera 1€, registrando el codigo el código LUZI.1x20${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com. Este cupon no es acumulable y tiene uso unico.",
                R.mipmap.star_1,
                level = 1
            ),
            ReportItem(
                "Descuento 5% con Endesa",
                "Usa el código LUZI.5${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com",
                R.mipmap.star_2,
                level = 2
            ),

            ReportItem(
                "Recupera 2x20€",
                "Solo para nuevos usuarios: Por cada 20€ gastados en tu factura de la luz, recupera 2€, registrando el codigo el código LUZI.2x20${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com. Este cupon no es acumulable y tiene uso unico.",
                R.mipmap.star_2,
                level = 2
            ),
            ReportItem(
                "Descuento 7% con Endesa",
                "Usa el código LUZI.7${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com",
                R.mipmap.star_3,
                level = 3
            ),
            ReportItem(
                "Descuento 10% con Endesa",
                "Usa el código LUZI.10${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com",
                R.mipmap.star_4,
                level = 4
            ),

            ReportItem(
                "Recupera 3x20€",
                "Solo para nuevos usuarios: Por cada 20€ gastados en tu factura de la luz, recupera 3€, registrando el codigo el código LUZI.3x20${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com. Este cupon no es acumulable y tiene uso unico.",
                R.mipmap.star_4,
                level = 4
            ),
            ReportItem(
                "Descuento 20% con Endesa",
                "Usa el código LUZI.20${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com",
                R.mipmap.star_5,
                level = 5
            )
        )
    }

    private fun generateUniqueCode(): String {
        return "AG78TDF22"
    }
}