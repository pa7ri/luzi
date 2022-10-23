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
import com.master.iot.luzi.databinding.FragmentElectricityBinding
import com.master.iot.luzi.domain.mapper.REEChartMapper.Companion.toBarData
import com.master.iot.luzi.domain.utils.getMinPrice
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ElectricityFragment : Fragment() {

    private val electricityViewModel: ElectricityViewModel by viewModels()

    private lateinit var binding: FragmentElectricityBinding
    private lateinit var adapter: EMPPricesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElectricityBinding.inflate(inflater, container, false)

        setUpAdapter()
        setUpChart()
        setUpListeners()
        setUpObservers()

        return binding.root
    }

    private fun setUpAdapter() { // create  layoutManager
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
        // Render list
        binding.rvPrices.visibility = View.VISIBLE
        binding.tvMinPriceValue.text =
            prices.data.items.getMinPrice().toBigDecimal().toPlainString()
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
        binding.chartPrices.visibility = View.GONE
        binding.rvPrices.visibility = View.GONE
        binding.ltLoading.group.visibility = View.GONE
        binding.ltError.group.visibility = View.GONE
    }
}