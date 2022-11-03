package com.master.iot.luzi.domain

import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class ObjectDetectionService {
    companion object {

        /**
         * Load custom trained model to detect home appliances
         */
        val localModel = LocalModel.Builder()
            .setAssetFilePath("model.tflite") //TODO: create model
            .build()

        /**
         * This configuration will handle a single image and track multiple objects in it
         */
        val options = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()

        val objectDetector = ObjectDetection.getClient(options)
    }
}

sealed class ImageVerificationStatus

class ImageVerificationProcessing(val title : String = "Processing...") : ImageVerificationStatus()
class ImageVerificationSuccess(val label: String, val index: Int, val confidence: Float) :
    ImageVerificationStatus()
class ImageVerificationError(val title : String = "Error verifying...", val description: String = "") : ImageVerificationStatus()