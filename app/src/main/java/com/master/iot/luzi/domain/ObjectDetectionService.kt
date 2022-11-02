package com.master.iot.luzi.domain

import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class ObjectDetectionService {
    companion object {
        /**
         * This configuration will handle a single image and track multiple objects in it
         */
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        val objectDetector = ObjectDetection.getClient(options)
    }
}

sealed class ImageVerificationStatus

class ImageVerificationProcessing(val title : String = "Processing...") : ImageVerificationStatus()
class ImageVerificationSuccess(val label: String, val index: Int, val confidence: Float) :
    ImageVerificationStatus()
class ImageVerificationError(val title : String = "Error verifying...", val description: String = "") : ImageVerificationStatus()