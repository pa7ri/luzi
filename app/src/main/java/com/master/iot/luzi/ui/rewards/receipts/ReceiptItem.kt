package com.master.iot.luzi.ui.rewards.receipts

import com.google.gson.annotations.SerializedName
import com.master.iot.luzi.ui.rewards.reports.ReportItem

class ReceiptItem(
    @SerializedName("receiptName")
    val name: String,
    @SerializedName("receiptTimestamp")
    override val timestamp: String,
    @SerializedName("receiptPoints")
    override val points: Int,
    @SerializedName("receiptSpend")
    override val amountSpend: Double = 0.0,
    @SerializedName("receiptSaved")
    override val amountSaved: Double = 0.0
) : ReportItem()