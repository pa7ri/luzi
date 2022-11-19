package com.master.iot.luzi.ui.petrol

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.camera
import com.master.iot.luzi.databinding.FragmentPetrolBinding
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
        setUpData()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        petrolViewModel.clearDisposables()
    }

    private fun setUpListeners() {
        binding.fabSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
    }

    private fun setUpObservables() {
        petrolViewModel.petrolPrices.observe(viewLifecycleOwner) { list ->
            //TODO: handle list
        }
    }

    private fun setUpMap() {
        binding.mvPetrol.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && resources.configuration.isNightModeActive) {
                getMapboxMap().loadStyleUri(Style.DARK)
            } else {
                getMapboxMap().loadStyleUri(Style.LIGHT)
            }
            //TODO: set initial location
        }
    }

    private fun setUpData() {
        petrolViewModel.getPetrolPricesByProvince()
    }
}