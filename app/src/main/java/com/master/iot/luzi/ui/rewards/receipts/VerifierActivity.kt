package com.master.iot.luzi.ui.rewards.receipts

import android.Manifest.permission.CAMERA
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.master.iot.luzi.*
import com.master.iot.luzi.data.ImageVerificationError
import com.master.iot.luzi.data.ImageVerificationProcessing
import com.master.iot.luzi.data.ImageVerificationSuccess
import com.master.iot.luzi.databinding.ActivityVerifierBinding
import com.master.iot.luzi.ui.utils.DialogUtils.Companion.showDialogWithOneButton
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime


@AndroidEntryPoint
class VerifierActivity : AppCompatActivity() {
    companion object {
        private const val RESULT_IMAGE_CAPTURE = 1001
        const val IMAGE_CAPTURE_CODE = 1000

        enum class StartFlow {
            NEW_RECEIPT, LOAD_RECEIPT
        }
    }

    private val verifierViewModel: VerifierViewModel by viewModels()
    private lateinit var binding: ActivityVerifierBinding
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()
        setUpListeners()
        setUpObservers()
        requestCameraPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)?.let {
                binding.ivReceipt.setImageBitmap(it)
                verifierViewModel.processTextImageTesseract(it, this)
            }
        }
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
        if (permissions.contains(CAMERA) && grantResults.first() == PackageManager.PERMISSION_GRANTED)
            takePicture()
        else {
            onBackPressed()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setUpListeners() {
        binding.btSave.setOnClickListener {
            val amount = binding.tvTotalAmount.text.toString().toDouble()
            val litres = binding.etLitre.text.toString().toInt()
            val date = LocalDateTime.now()
            registerReceipt(amount, litres, date)
        }
        binding.btRetry.setOnClickListener {
            requestCameraPermissions()
        }
    }

    private fun registerReceipt(amount: Double, litres: Int, date: LocalDateTime) {
        if (verifierViewModel.checkReceiptValidity(amount, litres, date)) {
            val preferences = getSharedPreferences(getString(R.string.preference_reports_file), Context.MODE_PRIVATE)
            val receiptReport = ReceiptItem(
                timestamp = date.toString(),
                amountSpend = amount,
                amountSaved = 0.0,
                litres
            )
            val total = preferences.getInt(
                PREFERENCES_REWARD_HISTORY_RECEIPT_TOTAL_KEY,
                PREFERENCES_REWARD_HISTORY_TOTAL_DEFAULT
            )

            val json = Gson().toJson(receiptReport)
            preferences.edit().apply {
                putString(PREFERENCES_REWARD_HISTORY_RECEIPT_ITEM_KEY + total, json)
                putInt(PREFERENCES_REWARD_HISTORY_RECEIPT_TOTAL_KEY, total + 1)
            }.apply()

            finish()
        } else {
            showDialogWithOneButton(this, R.string.dialog_validation_error, R.string.dialog_validation_error_receipt_description)
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
                    binding.etLitre.setText(it.litres.toString())
                }
            }
        }
    }

    private fun requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PERMISSION_CAMERA_REQUEST_CODE)
        } else {
            takePicture()
        }
    }

    private fun takePicture() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }
}