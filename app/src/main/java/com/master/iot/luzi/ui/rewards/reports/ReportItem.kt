package com.master.iot.luzi.ui.rewards.reports

open class ReportItem(
    open val timestamp: String = "",
    open val points: Int = 0,
    open val amountSpend: Double = 0.0,
    open val amountSaved: Double = 0.0
)