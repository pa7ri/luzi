package com.master.iot.luzi.ui.rewards.receipts

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.master.iot.luzi.data.ImageVerificationProcessing
import com.master.iot.luzi.data.ImageVerificationStatus
import com.master.iot.luzi.data.ImageVerificationSuccess
import com.master.iot.luzi.domain.MTPetrolRepository
import com.master.iot.luzi.domain.TesseractRepository
import com.master.iot.luzi.domain.utils.DateFormatterUtils
import com.master.iot.luzi.domain.utils.PriceIndicator
import com.master.iot.luzi.domain.utils.PriceIndicatorUtils
import com.master.iot.luzi.domain.utils.getAveragePrice
import com.master.iot.luzi.ui.petrol.MTPetrolPrices
import com.master.iot.luzi.ui.petrol.MTPetrolPricesError
import com.master.iot.luzi.ui.petrol.MTPetrolPricesLoading
import com.master.iot.luzi.ui.petrol.MTPetrolPricesReady
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class VerifierViewModel @Inject constructor(
    private val repository: MTPetrolRepository,
    private val ocr: TesseractRepository
) : ViewModel() {

    val verificationStatus = MutableLiveData<ImageVerificationStatus>().apply {
        value = ImageVerificationProcessing()
    }

    val validationStatus = MutableLiveData<ReceiptValidationStatus>()

    private val compositeDisposable = CompositeDisposable()

    fun clearDisposables() = compositeDisposable.clear()

    fun checkReceiptValidity(idProvince: String, idProduct: String, amount: Double, litres: Double, date: LocalDate) {
        val pricePerLitre = amount / litres
        if (pricePerLitre <= 1) {
            validationStatus.value = ReceiptValidationInvalid
            return
        }
        val currentDate = LocalDate.now()
        compositeDisposable.add(
            repository.getPetrolPricesByProvince(idProvince, idProduct)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    validationStatus.value = when (it) {
                        is MTPetrolPricesReady -> {
                            val averageValue = it.prices.getAveragePrice(idProduct)
                            val indicator = PriceIndicatorUtils.getPetrolIndicator(pricePerLitre, averageValue)
                            if(indicator == PriceIndicator.CHEAP && DateFormatterUtils.areSameDay(date, currentDate)) {
                                ReceiptValidationSuccess(amount, date)
                            }
                            else {
                                ReceiptValidationInvalid
                            }
                        }
                        else -> ReceiptValidationError()
                    }
                },
                    { validationStatus.value = ReceiptValidationError() }
                )
        )
    }

    fun processTextImageTesseract(imageData: Bitmap?, context: Context) {
        imageData?.let {
            ocr.init(context)
            ocr.processImageToText(it).let { result ->
                val totalAmount = getMaxAmount(result)
                val date = getDate(result)
                verificationStatus.postValue(ImageVerificationSuccess(totalAmount, date))
            }
        }
    }

    private fun getMaxAmount(text: String): Double {
        val patternDot = """(\d{1,3}(,\d{3})*|\d+)(\.|\,)\d{1,2}""".toRegex()
        var maxAmount = 0.0
        var amount: Double

        patternDot.findAll(text).forEach {
            amount = it.value.replace(",", ".").toDouble()
            if (amount > maxAmount) {
                maxAmount = amount
            }
        }
        return maxAmount
    }

    private fun getDate(text: String): LocalDate {
        val patternDate = """\b((\d{1,2}[-/.]\d{1,2}[-/.]\d{4})|(\d{4}[-/.]\d{1,2}[-/.]\d{1,2})|(\d{1,2}[-/.]\d{4}[-/.]\d{1,2}))\b""".toRegex()
        val dateResult = patternDate.find(text)

        var detectedDate = LocalDate.now()
        dateResult?.value?.let {
            val dateFormatter = DateTimeFormatter.ofPattern("[dd/MM/yyyy][dd-MM-yyyy][dd.MM.yyyy][yyyy/MM/dd][yyyy-MM-dd][yyyy.MM.dd][dd/MM/yyyy][dd-MM-yyyy][dd.MM.yyyy][MM/dd/yyyy][MM-dd-yyyy][MM.dd.yyyy]" +
                    "[d/M/yyyy][d-M-yyyy][d.M.yyyy][yyyy/M/d][yyyy-M-d][yyyy.MM.d][d/M/yyyy][d-M-yyyy][d.M.yyyy][M/d/yyyy][M-d-yyyy][MM.d.yyyy]")
            detectedDate = LocalDate.parse(it, dateFormatter)
        }
        return detectedDate
    }
}