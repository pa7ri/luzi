package com.master.iot.luzi.ui.rewards

import com.master.iot.luzi.R

class ReportItem(
    val type: ObjectType,
    val timestamp: String,
    val points: Int,
    val amount: Double = 0.0
)

/**
 * Consumption data is provided by Classiﬁcation of Household Devices byElectricity Usage Proﬁles
 * research, where we will use the mean consumption of power usage when a device is in use (Wh)
 */
// mean 33.13 365.13 27.14 20.10 28.91 201.80 84.94 328.79 39.36 324.14
enum class ObjectType(val nameId: Int, val consumption: Double, val points: Int) {
    WASHING_MACHINE(R.string.type_washing_machine, 0.122, 4),
    DISHWASHER(R.string.type_dishwasher, 365.13, 4),
    HEATER(R.string.type_immersion_heater, 0.122, 3),
    OVEN(R.string.type_oven, 0.122, 3),
    KETTLE(R.string.type_kettle, 0.122, 2),
    TV(R.string.type_tv, 0.122, 2),
    FRIDGE(R.string.type_fridge, 20.10, 1),
    FREEZER(R.string.type_freezer, 27.14, 1),
    OTHER(R.string.type_other, 0.0, 0)
}