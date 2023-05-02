package com.master.iot.luzi.ui.rewards.reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.ReportItemBinding
import com.master.iot.luzi.domain.utils.DateFormatterUtils
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.getReportDateTime
import com.master.iot.luzi.domain.utils.toRegularPriceString
import java.time.LocalDateTime

class ReportsAdapter(private var reports: List<ReportItem>) :
    RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reports[position]
        with(holder) {
            binding.apply {
                tvTitle.text = holder.binding.root.context.getString(item.type.nameId)
                tvTimestamp.text = getFormattedDateTime(item.timestamp.subSequence(0, 23).toString())
                tvAmount.text = root.context.getString(R.string.price_amount, item.amountSaved.toRegularPriceString())
                tvPoints.visibility = if (item.points > 0) View.VISIBLE else View.GONE
                tvPoints.text = root.context.getString(R.string.title_points, item.points)
            }
        }
    }

    override fun getItemCount(): Int = reports.size

    fun updateReports(newReports: List<ReportItem>) {
        reports = newReports
        notifyDataSetChanged()
    }

    private fun getFormattedDateTime(date: String): String =
        LocalDateTime.parse(date, DateFormatterUtils.formatterReport).getReportDateTime()

    inner class ViewHolder(val binding: ReportItemBinding) : RecyclerView.ViewHolder(binding.root)
}