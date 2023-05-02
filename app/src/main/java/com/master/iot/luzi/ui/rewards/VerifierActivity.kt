package com.master.iot.luzi.ui.rewards

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.master.iot.luzi.PERMISSION_CAMERA_REQUEST_CODE
import com.master.iot.luzi.data.ImageVerificationError
import com.master.iot.luzi.data.ImageVerificationProcessing
import com.master.iot.luzi.data.ImageVerificationSuccess
import com.master.iot.luzi.databinding.ActivityVerifierBinding


class VerifierActivity : AppCompatActivity() {

    private lateinit var imageData: InputImage

    private val verifierViewModel: VerifierViewModel by viewModels()
    private lateinit var binding: ActivityVerifierBinding

    private val takePicturePreviewResult =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                imageData = InputImage.fromBitmap(it, 0)
                binding.ivReceipt.setImageBitmap(bitmap)
                // for text recognition
                verifierViewModel.processTextImage(imageData)
                // for object detection
                //verifierViewModel.processObjectImage(imageData)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()
        setUpListeners()
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

    override fun onDestroy() {
        super.onDestroy()
        verifierViewModel.clearDisposables()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.contains(CAMERA) && grantResults.first()==PackageManager.PERMISSION_GRANTED)
            takePicturePreviewResult.launch()
        else {
            onBackPressed()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setUpListeners() {
        binding.btSave.setOnClickListener {
            // TODO: save item
            val amount = binding.tvTotalAmount.text.toString().toDouble()
            val liters = binding.etLiter.text.toString().toInt()
            // compute value
            val pricePerLiter = amount / liters

        }
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
                    binding.tvTotalAmount.text = it.totalAmount.toString()
                    binding.etLiter.setText(it.litres.toString())
                }
            }
        }
    }

    private fun requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, CAMERA)==PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PERMISSION_CAMERA_REQUEST_CODE)
        } else {
            takePicturePreviewResult.launch()
        }
    }
}