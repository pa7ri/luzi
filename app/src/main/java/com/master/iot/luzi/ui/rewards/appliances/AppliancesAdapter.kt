package com.master.iot.luzi.ui.rewards.appliances

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.ApplianceItemBinding
import com.master.iot.luzi.ui.rewards.reports.ApplianceType

class AppliancesAdapter(private var onClickListener: (expenseItem: ApplianceType) -> Unit) :
    RecyclerView.Adapter<AppliancesAdapter.ViewHolder>() {

    private val appliances: List<ApplianceType> = ApplianceType.values().toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ApplianceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appliances[position]
        with(holder) {
            binding.apply {
                tvName.text = root.context.getString(item.nameId)
                tvPoints.text = root.context.getString(R.string.title_points, item.points)
                ivIcon.setImageResource(item.drawable)
                root.setOnClickListener { onClickListener(item) }
            }
        }
    }

    override fun getItemCount(): Int = appliances.size

    inner class ViewHolder(val binding: ApplianceItemBinding) : RecyclerView.ViewHolder(binding.root)
}