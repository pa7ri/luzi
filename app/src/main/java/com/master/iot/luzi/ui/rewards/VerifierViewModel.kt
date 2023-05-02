package com.master.iot.luzi.ui.rewards

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.master.iot.luzi.data.*
import com.master.iot.luzi.domain.MTPetrolRepository
import com.master.iot.luzi.domain.utils.PriceIndicatorUtils
import com.master.iot.luzi.ui.petrol.MTPetrolPrices
import com.master.iot.luzi.ui.petrol.MTPetrolPricesError
import com.master.iot.luzi.ui.petrol.MTPetrolPricesLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class VerifierViewModel @Inject constructor(
    private val repository: MTPetrolRepository
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

    fun processTextImage(imageData: InputImage?) {
        imageData?.let {
            TextRecognitionService.textRecognizer.process(it)
                .addOnSuccessListener {
                    var text = ""
                    for (block in it.textBlocks) {
                        text += block.text + "\n"
                        Log.e("TEXT", text)
                    }
                    val totalAmount = 10.0
                    val litres = 10
                    verificationStatus.value = ImageVerificationSuccess(totalAmount, litres)
                    //TODO: extract data from receipts
                    // val receipts = receiptsViewModel.getReceipts(text)
                }
                .addOnFailureListener { e ->
                    verificationStatus.value =
                        ImageVerificationError(title = e.localizedMessage ?: "")
                }
        }
    }


    fun processObjectImage(imageData: InputImage?) {
        imageData?.let {
            ObjectDetectionService.objectDetector.process(it)
                .addOnSuccessListener { detectedObjects ->
                    for (detectedObject in detectedObjects) {
                        for (label in detectedObject.labels) {
//                            verificationStatus.value = ImageVerificationSuccess(
//                                label = label.text,
//                                index = label.index,
//                                confidence = label.confidence
//                            )
                            Log.e("OD text:", label.text)
                            Log.e("OD index:", label.index.toString())
                            Log.e("OD confidence:", label.confidence.toString())
                        }
                    }
                }
                .addOnFailureListener { e ->
                    verificationStatus.value = ImageVerificationError(title = e.localizedMessage ?: "")
                }
        }
    }
}