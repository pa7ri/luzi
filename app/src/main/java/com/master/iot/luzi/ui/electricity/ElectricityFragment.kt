package com.master.iot.luzi.ui.electricity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.master.iot.luzi.EXTRA_SELECTED_DATE
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.FragmentElectricityBinding
import com.master.iot.luzi.domain.mapper.REEChartMapper.Companion.toBarData
import com.master.iot.luzi.domain.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ElectricityFragment : Fragment() {

    private val electricityViewModel: ElectricityViewModel by viewModels()

    private lateinit var binding: FragmentElectricityBinding
    private lateinit var adapter: EMPPricesAdapter

    private var selectedDate = Calendar.getInstance().apply { time = Date() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElectricityBinding.inflate(inflater, container, false)

        arguments?.let {
            val selectedDateExtra = it.getLong(EXTRA_SELECTED_DATE)
            selectedDate = Calendar.getInstance().apply {
                if (selectedDateExtra != 0L) timeInMillis = selectedDateExtra else time = Date()
            }
        }

        setUpAdapter()
        setUpChart()
        setUpListeners()
        setUpObservers()
        electricityViewModel.initData(selectedDate)

        return binding.root
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

    override fun onDestroy() {
        electricityViewModel.clearDisposables()
        super.onDestroy()
    }

    private fun setUpListeners() {
        binding.fab.setOnClickListener { electricityViewModel.switchRenderData() }
    }

    private fun setUpObservers() {
        electricityViewModel.viewMode.observe(viewLifecycleOwner) {
            binding.fab.setImageResource(electricityViewModel.getFabImageResource())
            when (it) {
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

        electricityViewModel.dataPrices.observe(viewLifecycleOwner) {
            resetVisibilityItems()
            when (it) {
                is EMPPricesLoading -> renderLoading(it)
                is EMPPricesReady -> renderData(it)
                is EMPPricesError -> renderError(it)
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
        binding.rvPrices.visibility = View.VISIBLE
        adapter.updateData(prices.data.items)
        // Render chart
        binding.chartPrices.data = prices.toBarData()
        binding.chartPrices.animateY(1500)

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