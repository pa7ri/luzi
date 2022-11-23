package com.master.iot.luzi.ui.petrol

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnCircleAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.master.iot.luzi.PREFERENCES_PETROL_ID_PROVINCE_DEFAULT
import com.master.iot.luzi.PREFERENCES_PETROL_PROVINCE
import com.master.iot.luzi.databinding.FragmentPetrolBinding
import com.master.iot.luzi.domain.dto.MTPetrolStationData
import com.master.iot.luzi.domain.utils.PriceIndicatorUtils
import com.master.iot.luzi.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PetrolFragment : Fragment() {
    private val petrolViewModel: PetrolViewModel by viewModels()

    private lateinit var binding: FragmentPetrolBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetrolBinding.inflate(inflater, container, false)

        setUpListeners()
        setUpObservables()
        setUpMap()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUpData()
    }

    override fun onStop() {
        super.onStop()
        petrolViewModel.clearDisposables()
    }

    private fun setUpListeners() {
        binding.fabSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
    }

    private fun setUpObservables() {
        petrolViewModel.petrolPrices.observe(viewLifecycleOwner) { response ->
            when (response) {
                is MTPetrolPricesLoading -> {
                    renderLoading()
                }
                is MTPetrolPricesError -> {
                    renderError(response.description)
                }
                is MTPetrolPricesReady -> {
                    addAnnotationsLayer(response.prices)
                }
            }
        }
    }

    private fun renderLoading() {
        Log.e("ANNOTATIONS", "Loading")
    }

    private fun renderError(description: String) {
        Log.e("ANNOTATIONS", "Error $description")
    }

    private fun addAnnotationsLayer(prices: List<MTPetrolStationData>) {
        val pointAnnotationManager =
            binding.mvPetrol.annotations.createCircleAnnotationManager(binding.mvPetrol)

        prices.forEach { item ->
            val pointAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
                .withPoint(item.point)
                .withCircleColor(
                    PriceIndicatorUtils.getStringColor(
                        requireContext(),
                        PriceIndicatorUtils.getIndicatorColor(item.indicator)
                    )
                )
                .withData(Gson().toJsonTree(item))

            pointAnnotationManager.create(pointAnnotationOptions)
        }

        pointAnnotationManager.apply {
            addClickListener(
                OnCircleAnnotationClickListener {
                    val pricesData = Gson().fromJson(it.getData(), MTPetrolStationData::class.java)
                    PetrolItemDialog(requireContext(), pricesData).show()
                    false
                }
            )
        }
    }

    private fun setUpMap() {
        binding.mvPetrol.apply {
            val style =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && resources.configuration.isNightModeActive) {
                    Style.DARK
                } else {
                    Style.LIGHT
                }
            getMapboxMap().loadStyleUri(style)
            //TODO: set initial location
        }
    }

    private fun setUpData() {
        val idProvince = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(PREFERENCES_PETROL_PROVINCE, PREFERENCES_PETROL_ID_PROVINCE_DEFAULT)
            .toString()
        petrolViewModel.updateData(idProvince)
    }
}