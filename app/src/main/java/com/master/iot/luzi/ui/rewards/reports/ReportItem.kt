package com.master.iot.luzi.ui.rewards.reports

import com.master.iot.luzi.R

class ReportItem(
    val type: ApplianceType,
    val timestamp: String,
    val points: Int,
    val amountSaved: Double = 0.0,
    val amountSpend: Double = 0.0
)

/**
 * Consumption data is provided by Classiﬁcation of Household Devices byElectricity Usage Proﬁles
 * research, where we will use the mean consumption of power usage when a device is in use (Wh)
 *
 * Appliances such as fridge or freezer have been discarted since they are turned on the 24/7
 */
enum class ApplianceType(val nameId: Int, val drawable: Int, val consumption: Double, val points: Int) {
    DISHWASHER(R.string.type_dishwasher, R.mipmap.ic_dishwasher, 365.13, 4),
    OVEN(R.string.type_oven, R.mipmap.ic_oven, 328.79, 4),
    WASHING_MACHINE(R.string.type_washing_machine, R.mipmap.ic_washing_machine, 324.14, 4),
    HEATER(R.string.type_immersion_heater, R.mipmap.ic_heater, 201.80, 3),
    KETTLE(R.string.type_kettle, R.mipmap.ic_electric_kettle, 84.94, 2),
    TV(R.string.type_tv, R.mipmap.ic_television, 39.36, 1),
    COMPUTER(R.string.type_computer, R.mipmap.ic_laptop, 33.13, 1),
    OTHER(R.string.type_other, R.mipmap.ic_unknown, 0.0, 0)
}