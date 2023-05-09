package com.master.iot.luzi.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class TesseractRepository {
    private var path: String = ""
    private var language: String = "spa"
    private val tessOCR = TessBaseAPI()

    fun init(context: Context, lang: String = "spa") {
        path = "${context.filesDir.path}/ocrdata/"
        checkFileExist(context)
        language = lang
    }

    private fun checkFileExist(context: Context) {
        val dir = File("${path}tessdata/")
        val file = File("${path}tessdata/$language.traineddata")
        dir.mkdirs()
        if (!file.exists()) {
            copyFile(context, dir)
        }
    }

    private fun copyFile(context: Context, dir: File) {
        try {
            val file = File(dir, "$language.traineddata")
            if (file.createNewFile()) {
                val inputStream: InputStream = context.assets.open("$language.traineddata")
                val outputStream: OutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var read: Int = inputStream.read(buffer)
                while (read!=-1) {
                    outputStream.write(buffer, 0, read)
                    read = inputStream.read(buffer)
                }
            }
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.e("Error copying file", it) }
        }
    }

    fun processImageToText(imageData: Bitmap): String {
        tessOCR.init(path, language)
        tessOCR.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_OSD
        tessOCR.setImage(generateValidBitmap(imageData))
        return tessOCR.utF8Text
    }

    private fun generateValidBitmap(imageData: Bitmap): Bitmap {
        val width = imageData.width
        val height = imageData.height
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(imageData, 0f, 0f, null)
        return newBitmap
    }
}