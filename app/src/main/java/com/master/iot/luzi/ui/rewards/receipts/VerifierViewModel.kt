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
import com.master.iot.luzi.ui.petrol.MTPetrolPrices
import com.master.iot.luzi.ui.petrol.MTPetrolPricesError
import com.master.iot.luzi.ui.petrol.MTPetrolPricesLoading
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

    val petrolPrices = MutableLiveData<MTPetrolPrices>().apply {
        value = MTPetrolPricesLoading()
    }

    private val compositeDisposable = CompositeDisposable()

    fun clearDisposables() = compositeDisposable.clear()

    fun getPetrolPrices(idProvince: String, idProduct: String) {
        compositeDisposable.add(
            repository.getPetrolPricesByProvince(idProvince, idProduct)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ petrolPrices.value = it },
                    { petrolPrices.value = MTPetrolPricesError(it.localizedMessage ?: "") }
                )
        )
    }

    fun checkReceiptValidity(amount: Double, litres: Int, date: LocalDate): Boolean {
        // TODO: check receipt validity
        // compute value
        val pricePerLitre = amount / litres
        val currentDate = LocalDate.now()
        return DateFormatterUtils.areSameDay(date, currentDate)
    }

    fun processTextImageTesseract(imageData: Bitmap?, context: Context) {
        imageData?.let {
            ocr.init(context)
            ocr.processImageToText(it).let { result ->
                val totalAmount = getMaxAmount(result)
                val date = getDate(result)
                verificationStatus.postValue(ImageVerificationSuccess(totalAmount, 5, date))
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