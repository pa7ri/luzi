package com.master.iot.luzi.ui.electricity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.master.iot.luzi.PERMISSION_CALENDAR_REQUEST_CODE
import com.master.iot.luzi.R
import com.master.iot.luzi.TAG
import com.master.iot.luzi.data.mtpetrol.mapper.REEChartMapper.Companion.toBarData
import com.master.iot.luzi.databinding.FragmentElectricityBinding
import com.master.iot.luzi.domain.utils.*
import com.master.iot.luzi.ui.ElectricityPreferences
import com.master.iot.luzi.ui.getElectricityPreferences
import com.master.iot.luzi.ui.settings.SettingsActivity
import com.master.iot.luzi.ui.utils.SwipeCallback
import com.master.iot.luzi.ui.utils.SwipeType
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class ElectricityFragment : Fragment() {

    private val electricityViewModel: ElectricityViewModel by viewModels()

    private lateinit var binding: FragmentElectricityBinding
    private lateinit var adapter: EMPPricesAdapter
    private lateinit var preferences: ElectricityPreferences

    private val selectedDate = MutableLiveData<Calendar>()
    private val calendarPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.calendar_alert_created),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.calendar_permission_error_title))
                        .setMessage(getString(R.string.calendar_permission_error_description))
                        .show()
                }
            }
        }

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

    override fun onStop() {
        electricityViewModel.clearDisposables()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)==PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR)==PackageManager.PERMISSION_GRANTED
        )
            calendarPermissionResult.launch(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
            )
        else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.calendar_permission_error_title))
                .setMessage(getString(R.string.calendar_permission_error_description))
                .setPositiveButton("Ok", null)
                .show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        // Set up swipe
        val swipeHelper = ItemTouchHelper(object : SwipeCallback(requireContext(), SwipeType.NOTIFICATIONS) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.notifyItemChanged(viewHolder.adapterPosition)
                startActivity(
                    adapter.addCalendarEvent(
                        viewHolder.adapterPosition,
                        resources.getString(R.string.calendar_alert_title),
                        resources.getString(R.string.calendar_alert_description, adapter.getItemAtPosition(viewHolder.adapterPosition).value.toPriceString())
                    )
                )
            }
        })
        binding.rvPrices.adapter = adapter
        swipeHelper.attachToRecyclerView(binding.rvPrices)
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

    private fun setUpListeners() {
        binding.fab.setOnClickListener { electricityViewModel.switchRenderData() }
        binding.tvLocation.text = getString(R.string.location_at, preferences.location)
        binding.toolbar.ivMore.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        binding.toolbar.ivCalendar.setOnClickListener {
            showCalendar()
        }
        binding.toolbar.tvTitle.setOnClickListener {
            showCalendar()
        }
    }

    private fun showCalendar() {
        val materialDatePicker = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(getString(R.string.date_selection))
        }.build()
        materialDatePicker.addOnPositiveButtonClickListener {
            binding.toolbar.tvTitle.text = materialDatePicker.headerText
            materialDatePicker.selection?.let {
                resetVisibilityItems()
                val selected = Calendar.getInstance().apply { timeInMillis = it }
                when (isSelectedDateAvailable(selected)) {
                    DateType.FUTURE -> renderError(
                        EMPPricesError(
                            getString(R.string.error_electricity_not_available_title),
                            getString(R.string.error_electricity_not_available_decription)
                        )
                    )
                    DateType.PAST -> selectedDate.value = selected
                }
            }
        }
        materialDatePicker.show(parentFragmentManager, TAG)
    }

    private fun isSelectedDateAvailable(selectedDate: Calendar): DateType {
        val currentDate = Calendar.getInstance().apply { time = Date() }
        return if (currentDate < selectedDate) {
            DateType.FUTURE
        } else {
            DateType.PAST
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
        binding.ltError.tvErrorDescription.text = error.description
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

    private fun requestCalendarPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CALENDAR
            )==PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_CALENDAR
            )==PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                PERMISSION_CALENDAR_REQUEST_CODE
            )
        } else {
            calendarPermissionResult.launch(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
            )
        }
    }

}