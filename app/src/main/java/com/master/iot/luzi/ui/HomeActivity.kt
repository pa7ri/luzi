package com.master.iot.luzi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.ActivityHomeBinding
import com.master.iot.luzi.ui.utils.EventGenerator
import com.master.iot.luzi.ui.utils.EventGenerator.Companion.SCREEN_VIEW_ELECTRICITY
import com.master.iot.luzi.ui.utils.EventGenerator.Companion.SCREEN_VIEW_PETROL
import com.master.iot.luzi.ui.utils.EventGenerator.Companion.SCREEN_VIEW_REWARDS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var navController: NavController

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setUpNavigationBar()
    }

    private fun setUpNavigationBar() {
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_electricity,
                R.id.navigation_petrol,
                R.id.navigation_rewards
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val eventName = when (destination.id) {
                R.id.navigation_electricity -> SCREEN_VIEW_ELECTRICITY
                R.id.navigation_petrol -> SCREEN_VIEW_PETROL
                else -> SCREEN_VIEW_REWARDS
            }
            EventGenerator.sendScreenViewEvent(firebaseAnalytics, eventName)
        }
        binding.navView.setupWithNavController(navController)
    }
}