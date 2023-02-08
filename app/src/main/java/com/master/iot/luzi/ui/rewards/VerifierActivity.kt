package com.master.iot.luzi.ui.rewards

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.master.iot.luzi.PERMISSION_CAMERA_REQUEST_CODE
import com.master.iot.luzi.R
import com.master.iot.luzi.data.ImageVerificationError
import com.master.iot.luzi.data.ImageVerificationProcessing
import com.master.iot.luzi.data.ImageVerificationSuccess


class VerifierActivity : AppCompatActivity() {

    private lateinit var imageData: InputImage

    private val verifierViewModel: VerifierViewModel by viewModels()

    private val takePicturePreviewResult =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                Log.e("BITMAP", "Captured picture: $bitmap")
                imageData = InputImage.fromBitmap(it, 0)
                // for text recognition
                verifierViewModel.processTextImage(imageData)
                // for object detection
                verifierViewModel.processObjectImage(imageData)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_verifier)
        setUpToolbar()
        setUpObservers()
        requestCameraPermissions()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.contains(CAMERA) && grantResults.first() == PackageManager.PERMISSION_GRANTED)
            takePicturePreviewResult.launch()
        else {
            onBackPressed()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setUpToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpObservers() {
        verifierViewModel.verificationStatus.observe(this) {
            when (it) {
                is ImageVerificationProcessing -> {}
                is ImageVerificationError -> {}
                is ImageVerificationSuccess -> {
                    Toast.makeText(
                        this,
                        it.label + " " + it.confidence.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PERMISSION_CAMERA_REQUEST_CODE)
        } else {
            takePicturePreviewResult.launch()
        }
    }
}