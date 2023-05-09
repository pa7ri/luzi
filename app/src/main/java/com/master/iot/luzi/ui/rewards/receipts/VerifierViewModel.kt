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
import java.time.LocalDateTime
import java.util.regex.Matcher
import java.util.regex.Pattern
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

    fun checkReceiptValidity(amount: Double, litres: Int, date: LocalDateTime): Boolean {
        // TODO: check receipt validity
        // compute value
        val pricePerLitre = amount / litres
        val currentDate = LocalDateTime.now()
        return DateFormatterUtils.areSameDay(date, currentDate)
    }

    fun processTextImageTesseract(imageData: Bitmap?, context: Context) {
        imageData?.let {
            ocr.init(context)
            ocr.processImageToText(it).let { result ->
                val totalAmount = getMaxAmount(result)
                verificationStatus.postValue(ImageVerificationSuccess(totalAmount, 5))
            }
        }
    }

    private fun getMaxAmount(text: String): Double {
        val patternDot = "(\\d{1,3}(\\.|,)\\d{3}(\\.|,)\\d{2})|(\\d+(\\.|,)\\d{2})".toRegex() //"\\d+[,.]?\\d*".toRegex()
        var maxAmount = 0.0
        var amount: Double

        patternDot.findAll(text).forEach {
            var amountString = it.value
            amountString = amountString.replace(".", "").replace(",", ".") // Replace any commas with periods
            amount = amountString.toDouble()
            if (amount > maxAmount) {
                maxAmount = amount
            }
        }
        return maxAmount
    }

    private fun getDate(text: List<String>): String {
        val totalAmountPattern: Pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}"); // Regex pattern for dates in MM/dd/yyyy format
        val dates = mutableListOf<String>()
        text.map {
            val matcher: Matcher = totalAmountPattern.matcher(it)
            if (matcher.find()) {
                dates.add(matcher.group())
            }
        }
        return dates.firstOrNull() ?: ""
    }
}