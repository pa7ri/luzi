package com.master.iot.luzi.ui.rewards.prizes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.PREFERENCES_REWARD_LEVEL_DEFAULT
import com.master.iot.luzi.PREFERENCES_REWARD_LEVEL_KEY
import com.master.iot.luzi.R
import com.master.iot.luzi.ui.utils.Levels
import com.master.iot.luzi.ui.utils.getCurrentLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrizesFragment : Fragment() {
    private val viewModel: PrizesViewModel by viewModels()
    private var prizesAdapter = PrizesAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferences = requireActivity().getSharedPreferences(getString(R.string.preference_reports_file), Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.fragment_reports, container, false)
        if (view is RecyclerView) {
            recyclerView = view
            recyclerView.adapter = prizesAdapter
            setUpObservables()
            updateData()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val level = preferences.getInt(PREFERENCES_REWARD_LEVEL_KEY, PREFERENCES_REWARD_LEVEL_DEFAULT)
        updateLevel(level.getCurrentLevel())
    }

    private fun updateData() {
        viewModel.getPrizes()
    }

    fun updateLevel(level: Levels) {
        prizesAdapter.updateLevel(level)
    }

    private fun setUpObservables() {
        viewModel.prizes.observe(viewLifecycleOwner) { prizes ->
            prizesAdapter.updatePrizes(prizes)
        }
    }
}