package com.master.iot.luzi.ui.rewards

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.master.iot.luzi.PERMISSION_CAMERA_REQUEST_CODE
import com.master.iot.luzi.R


class VerifierActivity : AppCompatActivity() {

    val imageLiveData = MutableLiveData<Bitmap>()

    val takePicturePreviewResult = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        Log.e("BITMAP", "Got picture: $bitmap")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_verifier)
        setUpToolbar()
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

    private fun setUpToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    private fun requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PERMISSION_CAMERA_REQUEST_CODE)
        } else {
            takePicturePreviewResult.launch()
        }
    }
}