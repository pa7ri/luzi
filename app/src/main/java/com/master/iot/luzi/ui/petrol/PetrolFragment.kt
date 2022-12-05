package com.master.iot.luzi.ui.petrol

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnCircleAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.master.iot.luzi.*
import com.master.iot.luzi.databinding.FragmentPetrolBinding
import com.master.iot.luzi.domain.dto.MTPetrolStationData
import com.master.iot.luzi.domain.utils.PriceIndicatorUtils
import com.master.iot.luzi.domain.utils.getCheapestGasStation
import com.master.iot.luzi.domain.utils.getGasStationsByMunicipality
import com.master.iot.luzi.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PetrolFragment : Fragment() {
    private val petrolViewModel: PetrolViewModel by viewModels()

    private lateinit var binding: FragmentPetrolBinding

    private lateinit var pointAnnotationManager: CircleAnnotationManager
    private var idSelectedProduct: String = PREFERENCES_PETROL_ID_PRODUCT_TYPE_DEFAULT
    private var idMunicipality: String = PREFERENCES_PETROL_ID_MUNICIPALITY_DEFAULT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetrolBinding.inflate(inflater, container, false)

        setUpListeners()
        setUpObservables()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUpMap()
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
            resetVisibilityItems()
            when (response) {
                is MTPetrolPricesLoading -> {
                    renderLoading(response.title)
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

    private fun renderLoading(description: Int) {
        binding.ltLoading.group.visibility = View.VISIBLE
        binding.ltLoading.tvLoading.text = getString(description)
    }

    private fun renderError(description: String) {
        binding.ltError.group.visibility = View.VISIBLE
        binding.ltError.tvError.text = description
    }

    private fun resetVisibilityItems() {
        binding.ltLoading.group.visibility = View.GONE
        binding.ltError.group.visibility = View.GONE
    }

    private fun addAnnotationsLayer(prices: List<MTPetrolStationData>) {
        binding.mvPetrol.clearAnimation()
        binding.mvPetrol.visibility = View.VISIBLE

        // render annotations with gas station
        pointAnnotationManager = binding.mvPetrol.annotations.createCircleAnnotationManager()

        prices.forEach { item ->
            val pointAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
                .withPoint(item.point)
                .withCircleRadius(7.0)
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

        // move camera to cheapest gas station in selected municipality
        val desiredGasStation = prices.getGasStationsByMunicipality(idMunicipality)
            .getCheapestGasStation(idSelectedProduct)
        if (desiredGasStation == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_gas_station_found),
                Toast.LENGTH_LONG
            ).show()
        } else {
            binding.mvPetrol.getMapboxMap().flyTo(cameraOptions {
                zoom(PREFERENCES_LOCATION_DEFAULT_ZOOM)
                center(desiredGasStation.point)
                bearing(PREFERENCES_LOCATION_DEFAULT_BEARING)
            }, mapAnimationOptions {
                duration(PREFERENCES_LOCATION_DEFAULT_ANIMATION)
            })
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
        }
    }

    private fun setUpData() {
        val idProvince = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(PREFERENCES_PETROL_PROVINCE, PREFERENCES_PETROL_ID_PROVINCE_DEFAULT)
            .toString()
        idSelectedProduct = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(PREFERENCES_PETROL_PRODUCT_TYPE, PREFERENCES_PETROL_ID_PRODUCT_TYPE_DEFAULT)
            .toString()
        idMunicipality = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(PREFERENCES_PETROL_MUNICIPALITY, PREFERENCES_PETROL_ID_MUNICIPALITY_DEFAULT)
            .toString()
        petrolViewModel.updateData(idProvince, idSelectedProduct)
    }
}