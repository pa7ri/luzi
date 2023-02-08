package com.master.iot.luzi.ui.rewards

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.master.iot.luzi.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifierViewModel @Inject constructor() : ViewModel() {

    val verificationStatus = MutableLiveData<ImageVerificationStatus>().apply {
        value = ImageVerificationProcessing()
    }

    fun processTextImage(imageData: InputImage?) {
        imageData?.let {
            TextRecognitionService.textRecognizer.process(it)
                .addOnSuccessListener {
                    var text = ""
                    for (block in it.textBlocks)
                        text += block.text + "\n"
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
                            verificationStatus.value = ImageVerificationSuccess(
                                label = label.text,
                                index = label.index,
                                confidence = label.confidence
                            )
                            Log.e("OD text:", label.text)
                            Log.e("OD index:", label.index.toString())
                            Log.e("OD confidence:", label.confidence.toString())
                        }
                    }
                }
                .addOnFailureListener { e ->
                    verificationStatus.value =
                        ImageVerificationError(title = e.localizedMessage ?: "")
                }
        }
    }
}