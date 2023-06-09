package com.master.iot.luzi.ui.rewards.receipts

import java.time.LocalDate

sealed class ReceiptValidationStatus

class ReceiptValidationSuccess(val totalAmount: Double, val date: LocalDate) : ReceiptValidationStatus()

object ReceiptValidationInvalid : ReceiptValidationStatus()

class ReceiptValidationError(
    val title: String = "Error verifying...",
    val description: String = ""
) : ReceiptValidationStatus()