package com.master.iot.luzi.ui.electricity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.FragmentElectricityBinding
import com.master.iot.luzi.domain.utils.getMinPrice

class ElectricityFragment : Fragment() {

    private lateinit var binding: FragmentElectricityBinding

    private lateinit var electricityViewModel: ElectricityViewModel
    private lateinit var adapter: EMPPricesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElectricityBinding.inflate(inflater, container, false)
        electricityViewModel = ViewModelProvider(this)[ElectricityViewModel::class.java]

        setUpAdapter()
        setUpListeners()
        setUpObservers()
        updateData()

        return binding.root
    }

    private fun setUpAdapter() { // create  layoutManager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvPrices.layoutManager = layoutManager
        adapter = EMPPricesAdapter(emptyList())
        binding.rvPrices.adapter = adapter
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
        binding.rvPrices.visibility = View.VISIBLE
        binding.tvMinPriceValue.text = prices.data.items.getMinPrice().toBigDecimal().toPlainString()
        adapter.updateData(prices.data.items)
    }

    private fun renderError(error: EMPPricesError) {
        binding.ltError.group.visibility = View.VISIBLE
    }

    private fun renderLoading(loading: EMPPricesLoading) {
        binding.ltLoading.group.visibility = View.VISIBLE
        binding.ltLoading.tvLoading.text =
            loading.title.ifEmpty { getString(R.string.loading_data_title) }
    }

    private fun resetVisibilityItems() {
        binding.rvPrices.visibility = View.GONE
        binding.ltLoading.group.visibility = View.GONE
        binding.ltError.group.visibility = View.GONE
    }

    private fun updateData() {
        electricityViewModel.getReeApiData()
    }
}