package com.master.iot.luzi.ui.electricity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.master.iot.luzi.databinding.FragmentElectricityBinding

class ElectricityFragment : Fragment() {

    private lateinit var binding: FragmentElectricityBinding

    private lateinit var electricityViewModel: ElectricityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElectricityBinding.inflate(inflater, container, false)
        electricityViewModel = ViewModelProvider(this)[ElectricityViewModel::class.java]

        setUpListeners()
        setUpObservers()

        electricityViewModel.getReeApiData()

        return binding.root
    }

    private fun setUpListeners() {
        binding.fab.setOnClickListener { electricityViewModel.switchRenderData() }
    }

    private fun setUpObservers() {
        electricityViewModel.viewMode.observe(viewLifecycleOwner) {
            binding.fab.setImageResource(electricityViewModel.getFabImageResource())
        }
    }
}