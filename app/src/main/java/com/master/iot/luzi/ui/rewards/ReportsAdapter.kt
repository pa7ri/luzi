package com.master.iot.luzi.ui.rewards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.databinding.ReportItemBinding

class ReportsAdapter(private var reports: List<ReportItem>) :
    RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reports[position]
        with(holder) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.timestamp
            ContextCompat.getDrawable(holder.binding.root.context, item.resourceId)?.let {
                binding.ivIcon.setImageDrawable(it)
            }
        }
    }

    override fun getItemCount(): Int = reports.size

    fun updateReports(newReports: List<ReportItem>) {
        reports = newReports
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ReportItemBinding) : RecyclerView.ViewHolder(binding.root)

}