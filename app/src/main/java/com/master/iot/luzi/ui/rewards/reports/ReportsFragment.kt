package com.master.iot.luzi.ui.rewards.reports

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R
import com.master.iot.luzi.ui.utils.Levels
import com.master.iot.luzi.ui.utils.getLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment(private var updateRequired: (level: Levels) -> Unit) : Fragment() {
    private val viewModel: ReportsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reports, container, false)
        if (view is RecyclerView) {
            recyclerView = view
            setUpObservables()
        }
        return view
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
            recyclerView.adapter = ReportsAdapter(reports)
            updateRequired(getLevel(viewModel.getTotalPoints()))
        }
    }
}