package com.master.iot.luzi.data

import java.time.LocalDate

sealed class ImageVerificationStatus

class ImageVerificationProcessing(val title: String = "Processing...") : ImageVerificationStatus()
class ImageVerificationSuccess(val totalAmount: Double, val litres: Int, val date: LocalDate) : ImageVerificationStatus()

class ImageVerificationError(
    val title: String = "Error verifying...",
    val description: String = ""
) : ImageVerificationStatus()