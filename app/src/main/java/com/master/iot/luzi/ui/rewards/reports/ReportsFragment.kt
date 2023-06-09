package com.master.iot.luzi.ui.rewards.reports

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.FragmentReportsBinding
import com.master.iot.luzi.ui.utils.Levels
import com.master.iot.luzi.ui.utils.getLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment(private var updateRequired: (level: Levels) -> Unit) : Fragment() {
    private val viewModel: ReportsViewModel by viewModels()

    private lateinit var binding: FragmentReportsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportsBinding.inflate(inflater, container, false)
        setUpObservables()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    fun updateData() {
        viewModel.getReports(requireActivity().getSharedPreferences(getString(R.string.preference_reports_file), Context.MODE_PRIVATE))
    }

    private fun setUpObservables() {
        viewModel.reports.observe(viewLifecycleOwner) { reports ->
            binding.list.adapter = ReportsAdapter(reports)
            updateRequired(getLevel(viewModel.getTotalPoints()))
        }
    }
}