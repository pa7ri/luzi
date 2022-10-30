package com.master.iot.luzi.ui

import android.content.SharedPreferences
import com.master.iot.luzi.PREFERENCES_FEE_DEFAULT
import com.master.iot.luzi.PREFERENCES_FEE_KEY
import com.master.iot.luzi.PREFERENCES_LOCATION_DEFAULT
import com.master.iot.luzi.PREFERENCES_LOCATION_KEY

class ElectricityPreferences(
    val location: String,
    val feeType: FeeType,
    val enableNotifications: Boolean
)

fun String?.toFeeType(): FeeType = when (this) {
    "autoconsumo" -> FeeType.AUTOCONSUMO
    else -> FeeType.PVPC
}

enum class FeeType {
    PVPC, AUTOCONSUMO
}


fun SharedPreferences.getElectricityPreferences(): ElectricityPreferences =
    ElectricityPreferences(
        this.getString(PREFERENCES_LOCATION_KEY, PREFERENCES_LOCATION_DEFAULT)
            ?: PREFERENCES_LOCATION_DEFAULT,
        this.getString(PREFERENCES_FEE_KEY, PREFERENCES_FEE_DEFAULT).toFeeType(),
        false
    )