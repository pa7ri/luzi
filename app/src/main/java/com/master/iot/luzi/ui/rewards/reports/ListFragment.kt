package com.master.iot.luzi.ui.rewards.reports

import android.content.Context
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
import com.master.iot.luzi.ui.rewards.prizes.PrizesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {
    companion object {
        const val ARG_ITEM_TYPE = "ITEM_TYPE"

        enum class ItemType(val value: Int) {
            REPORT(1000), PRICE(1001)
        }

        @JvmStatic
        fun newInstance(itemType: ItemType) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_TYPE, itemType.value)
                }
            }
    }

    private var itemType = ItemType.REPORT
    private val viewModel: ListViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemType = ItemType.values().first { type -> type.value==it.getInt(ARG_ITEM_TYPE) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reports, container, false)
        if (view is RecyclerView) {
            recyclerView = view
            setUpObservables()
            updateData()
        }
        return view
    }

    fun updateData() {
        if (itemType==ItemType.REPORT) {
            viewModel.getReports(requireActivity().getPreferences(Context.MODE_PRIVATE))
        } else {
            viewModel.getPrizes()
        }
    }

    private fun setUpObservables() {
        if (itemType==ItemType.REPORT) {
            viewModel.reports.observe(viewLifecycleOwner) { reports ->
                recyclerView.adapter = ReportsAdapter(reports)
            }
        } else {
            viewModel.prizes.observe(viewLifecycleOwner) { prizes ->
                val currentLevel = requireActivity().getPreferences(Context.MODE_PRIVATE)
                    ?.getInt(PREFERENCES_REWARD_LEVEL_KEY, 2)
                    ?: PREFERENCES_REWARD_LEVEL_DEFAULT
                recyclerView.adapter = PrizesAdapter(currentLevel, prizes)
            }
        }
    }
}