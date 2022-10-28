package com.master.iot.luzi

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.master.iot.luzi.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()
        setUpNavigationBar()
        setUpDatePickerListener()
    }

    private fun setUpNavigationBar() {
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_electricity,
                R.id.navigation_gas,
                R.id.navigation_petrol,
                R.id.navigation_rewards
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }


    private fun setUpToolbar() {
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setCustomView(R.layout.toolbar)
            elevation = 0f
        }
    }

    private fun setUpDatePickerListener() {
        val materialDatePicker = MaterialDatePicker.Builder.datePicker().apply {
            title = "Select a date"
        }.build()
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.setOnClickListener {
            materialDatePicker.addOnPositiveButtonClickListener {
                toolbarTitle.text = materialDatePicker.headerText
                var bundle = Bundle().apply {
                    putLong(EXTRA_SELECTED_DATE, materialDatePicker.selection ?: 0)
                }
                navController.navigate(R.id.navigation_electricity, bundle)
            }
            materialDatePicker.show(supportFragmentManager, "TAG")
        }
    }
}