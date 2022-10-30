package com.master.iot.luzi.ui.electricity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.google.android.material.datepicker.MaterialDatePicker
import com.master.iot.luzi.R
import com.master.iot.luzi.TAG
import com.master.iot.luzi.databinding.FragmentElectricityBinding
import com.master.iot.luzi.domain.mapper.REEChartMapper.Companion.toBarData
import com.master.iot.luzi.domain.utils.*
import com.master.iot.luzi.ui.ElectricityPreferences
import com.master.iot.luzi.ui.SettingsActivity
import com.master.iot.luzi.ui.getElectricityPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class ElectricityFragment : Fragment() {

    private val electricityViewModel: ElectricityViewModel by viewModels()

    private lateinit var binding: FragmentElectricityBinding
    private lateinit var adapter: EMPPricesAdapter

    private val selectedDate = MutableLiveData<Calendar>()
    private lateinit var preferences: ElectricityPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElectricityBinding.inflate(inflater, container, false)
        setUpAdapter()
        setUpChart()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initDate()
        initPreferences()
        setUpListeners()
        setUpObservers()
    }

    private fun initPreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getElectricityPreferences()
    }

    private fun initDate() {
        selectedDate.value = Calendar.getInstance().apply { time = Date() }
    }

    private fun setUpAdapter() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvPrices.layoutManager = layoutManager
        adapter = EMPPricesAdapter(emptyList())
        binding.rvPrices.adapter = adapter
    }

    private fun setUpChart() {
        binding.chartPrices.description.isEnabled = false
        binding.chartPrices.setPinchZoom(false)
        binding.chartPrices.setDrawBarShadow(false)
        binding.chartPrices.setDrawGridBackground(false)

        binding.chartPrices.xAxis.apply {
            position = XAxisPosition.BOTTOM
            setDrawGridLines(false)
        }
        binding.chartPrices.axisLeft.setDrawGridLines(false)
    }

    override fun onStop() {
        electricityViewModel.clearDisposables()
        super.onStop()
    }

    private fun setUpListeners() {
        binding.fab.setOnClickListener { electricityViewModel.switchRenderData() }
        val materialDatePicker = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(getString(R.string.date_selection))
        }.build()
        binding.tvLocation.text = getString(R.string.location_at, preferences.location)
        binding.toolbar.ivMore.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        binding.toolbar.tvTitle.setOnClickListener {
            materialDatePicker.addOnPositiveButtonClickListener {
                binding.toolbar.tvTitle.text = materialDatePicker.headerText
                materialDatePicker.selection?.let {
                    selectedDate.value = Calendar.getInstance().apply { timeInMillis = it }
                }
            }
            materialDatePicker.show(parentFragmentManager, TAG)
        }
    }

    private fun setUpObservers() {
        electricityViewModel.viewMode.observe(viewLifecycleOwner) {
            binding.fab.setImageResource(electricityViewModel.getFabImageResource())
            setUpDataVisibility(it)
        }
        selectedDate.observe(viewLifecycleOwner) {
            electricityViewModel.updateData(it, preferences)
        }
        electricityViewModel.dataPrices.observe(viewLifecycleOwner) {
            resetVisibilityItems()
            when (it) {
                is EMPPricesLoading -> renderLoading(it)
                is EMPPricesReady -> renderData(it)
                is EMPPricesError -> renderError(it)
            }
        }
    }

    private fun setUpDataVisibility(viewMode: ElectricityViewMode) {
        when (viewMode) {
            ElectricityViewMode.LIST_VIEW -> {
                binding.rvPrices.visibility = View.VISIBLE
                binding.chartPrices.visibility = View.GONE
            }
            else -> {
                binding.rvPrices.visibility = View.GONE
                binding.chartPrices.visibility = View.VISIBLE
                binding.chartPrices.animateY(1500)
            }
        }
    }

    private fun renderData(prices: EMPPricesReady) {
        // Render header
        binding.layoutMaxPrice.root.visibility = View.VISIBLE
        binding.layoutAveragePrice.root.visibility = View.VISIBLE
        binding.layoutMaxPrice.apply {
            tvTitle.text = getString(R.string.price_maximum)
            tvPriceValue.text = prices.data.items.getMaxPrice().toPriceString()
            tvPriceHour.text = getString(R.string.hour_format, prices.data.items.getMaxHour())
        }
        binding.layoutAveragePrice.apply {
            tvTitle.text = getString(R.string.price_average)
            tvPriceValue.text = prices.data.items.getAveragePrice().toPriceString()
            tvPriceHour.visibility = View.GONE
        }
        binding.tvMinPriceValue.text = prices.data.items.getMinPrice().toPriceString()
        binding.tvMinPriceHour.text =
            getString(R.string.hour_format, prices.data.items.getMinHour())
        // Render list
        adapter.updateData(prices.data.items)
        // Render chart
        binding.chartPrices.data = prices.toBarData()
        setUpDataVisibility(electricityViewModel.viewMode.value ?: ElectricityViewMode.LIST_VIEW)
    }

    private fun renderError(error: EMPPricesError) {
        binding.ltError.group.visibility = View.VISIBLE
        binding.ltError.tvError.text = error.title
    }

    private fun renderLoading(loading: EMPPricesLoading) {
        binding.ltLoading.group.visibility = View.VISIBLE
        binding.ltLoading.tvLoading.text = getString(loading.title)
    }

    private fun resetVisibilityItems() {
        binding.layoutMaxPrice.root.visibility = View.GONE
        binding.layoutAveragePrice.root.visibility = View.GONE
        binding.chartPrices.visibility = View.GONE
        binding.rvPrices.visibility = View.GONE
        binding.ltLoading.group.visibility = View.GONE
        binding.ltError.group.visibility = View.GONE
    }
}