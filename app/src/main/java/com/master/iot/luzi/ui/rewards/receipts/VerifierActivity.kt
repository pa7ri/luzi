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
import androidx.preference.PreferenceManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.master.iot.luzi.*
import com.master.iot.luzi.data.ImageVerificationError
import com.master.iot.luzi.data.ImageVerificationProcessing
import com.master.iot.luzi.data.ImageVerificationSuccess
import com.master.iot.luzi.databinding.ActivityVerifierBinding
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.formatterReceipt
import com.master.iot.luzi.domain.utils.toRegularPriceString
import com.master.iot.luzi.ui.utils.DialogUtils.Companion.showDialogWithOneButton
import com.master.iot.luzi.ui.utils.DialogUtils.Companion.showDialogWithOneButtonAndAction
import com.master.iot.luzi.ui.utils.EventGenerator
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@AndroidEntryPoint
class VerifierActivity : AppCompatActivity() {
    companion object {
        const val IMAGE_CAPTURE_CODE = 1000
    }

    private val verifierViewModel: VerifierViewModel by viewModels()

    private lateinit var binding: ActivityVerifierBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
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
        binding.tlDate.setEndIconOnClickListener {
            showCalendar()
        }
        binding.btSave.setOnClickListener {
            val amount = binding.etTotalAmount.text.toString().replace("€", "").toDouble()
            val litres = binding.etLitre.text.toString().toDouble()
            val date = binding.etDate.text.toString()
            EventGenerator.sendActionEvent(firebaseAnalytics, EventGenerator.ACTION_REWARDS_CREATE_RECEIPT_REPORT)
            registerReceipt(amount, litres, LocalDate.parse(date, formatterReceipt))
        }
        binding.btRetry.setOnClickListener {
            requestCameraPermissions()
        }
    }

    private fun showCalendar() {
        val materialDatePicker = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(getString(R.string.date_selection))
        }.build()
        materialDatePicker.addOnPositiveButtonClickListener {
            materialDatePicker.selection?.let { selectedDate ->
                Instant.ofEpochMilli(selectedDate).atZone(ZoneId.systemDefault()).toLocalDate()?.let {
                    binding.etDate.setText(it.format(formatterReceipt))
                }
            }
        }
        materialDatePicker.show(supportFragmentManager, TAG)
    }

    private fun registerReceipt(amount: Double, litres: Double, date: LocalDate) {
        val idProvince = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(PREFERENCES_PETROL_PROVINCE, PREFERENCES_PETROL_ID_PROVINCE_DEFAULT)
            .toString()
        val idProduct = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(PREFERENCES_PETROL_PRODUCT_TYPE, PREFERENCES_PETROL_ID_PRODUCT_TYPE_DEFAULT)
            .toString()
        verifierViewModel.checkReceiptValidity(idProvince, idProduct, amount, litres, date)
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
                    binding.etTotalAmount.setText(getString(R.string.price_amount, it.totalAmount.toRegularPriceString()))
                    binding.etDate.setText(formatterReceipt.format(it.date))
                }
            }
        }
        verifierViewModel.validationStatus.observe(this) {
            val name = binding.etName.text.toString()
            val amount = binding.etTotalAmount.text.toString().replace("€", "").toDouble()
            val date = binding.etDate.text.toString()
            when (it) {
                is ReceiptValidationSuccess -> {
                    registerReceipt(name, date, amount, true)
                    showDialogWithOneButtonAndAction(this, R.string.dialog_validation_receipt_success, R.string.dialog_validation_receipt_success_description) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                }

                is ReceiptValidationInvalid -> {
                    registerReceipt(name, date, amount, false)
                    showDialogWithOneButtonAndAction(this, R.string.dialog_validation_receipt_success, R.string.dialog_validation_receipt_success_description_no_points) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                }

                else -> {
                    showDialogWithOneButton(this, R.string.error_error_server_title, R.string.error_error_server_description)
                }
            }
        }
    }

    private fun registerReceipt(name: String, date: String, amount: Double, withPoints: Boolean) {
        val preferences = getSharedPreferences(getString(R.string.preference_reports_file), Context.MODE_PRIVATE)
        val receiptReport = ReceiptItem(
            name,
            timestamp = date.format(formatterReceipt),
            points = if (withPoints) 2 else 0,
            amountSpend = amount,
            amountSaved = 0.0
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