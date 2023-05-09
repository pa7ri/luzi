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
import com.master.iot.luzi.ui.rewards.appliances.ApplianceItem
import com.master.iot.luzi.ui.rewards.receipts.ReceiptItem
import java.time.LocalDateTime

class ReportsAdapter(private var reports: List<ReportItem>) :
    RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    enum class CategoryType {
        APPLIANCE_TYPE, RECEIPT_TYPE, HEADER_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return when(reports[position]) {
            is ApplianceItem -> CategoryType.APPLIANCE_TYPE.ordinal
            is ReceiptItem -> CategoryType.RECEIPT_TYPE.ordinal
            else -> CategoryType.HEADER_TYPE.ordinal
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            when(itemViewType) {
                CategoryType.APPLIANCE_TYPE.ordinal -> {
                    val item = reports[position] as ApplianceItem
                    binding.apply {
                        tvTitle.text = holder.binding.root.context.getString(item.type.nameId)
                        tvTimestamp.text = getFormattedDateTime(item.timestamp.subSequence(0, 23).toString())
                        tvAmount.text = root.context.getString(R.string.price_amount, item.amountSpend.toRegularPriceString())
                        tvPoints.visibility = if (item.points > 0) View.VISIBLE else View.GONE
                        tvPoints.text = root.context.getString(R.string.title_points, item.points)
                    }
                }
                CategoryType.RECEIPT_TYPE.ordinal -> {
                    val item = reports[position] as ReceiptItem
                    binding.apply {
                        tvTitle.text = "Gasolinera 1"
                        tvTimestamp.text = getFormattedDateTime(item.timestamp.subSequence(0, 23).toString())
                        tvAmount.text = item.amountSpend.toRegularPriceString()
                        tvPoints.visibility = View.GONE
                    }
                }
                CategoryType.HEADER_TYPE.ordinal -> {
                    val item = reports[position] as HeaderItem
                    binding.apply {
                        root.setBackgroundColor(root.resources.getColor(R.color.orange_100_40))
                        tvTitle.text = item.title
                        tvTimestamp.visibility = View.GONE
                        tvAmount.visibility = View.GONE
                        tvPoints.visibility = View.GONE
                    }
                }
                else -> {}
            }
        }
    }

    override fun getItemCount(): Int = reports.size

    fun updateReports(newReports: List<ApplianceItem>) {
        reports = newReports
        notifyDataSetChanged()
    }

    private fun getFormattedDateTime(date: String): String =
        LocalDateTime.parse(date, DateFormatterUtils.formatterReport).getReportDateTime()

    inner class ViewHolder(val binding: ReportItemBinding) : RecyclerView.ViewHolder(binding.root)
}