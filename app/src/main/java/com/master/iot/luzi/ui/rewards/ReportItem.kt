package com.master.iot.luzi.ui.rewards

class ReportItem(
    val type: ObjectType,
    val timestamp: String,
    val points: Int,
    val amount: Double = 0.0
)

enum class ObjectType() {
    WASHING_MACHINE, DISHWASHER, OVEN, OTHER
}