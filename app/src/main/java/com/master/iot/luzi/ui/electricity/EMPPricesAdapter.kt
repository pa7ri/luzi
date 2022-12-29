package com.master.iot.luzi.ui.electricity

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.EmpPriceItemBinding
import com.master.iot.luzi.domain.dto.EMPItem
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.getRangeHourFromDate
import com.master.iot.luzi.domain.utils.PriceIndicatorUtils
import com.master.iot.luzi.domain.utils.toPriceString
import java.util.*


class EMPPricesAdapter(private var pricesList: List<EMPItem>) :
    RecyclerView.Adapter<EMPPricesAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            EmpPriceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(pricesList[position]) {
                binding.tvHour.text = dateTime.getRangeHourFromDate()
                binding.tvPriceValue.text = value.toPriceString()
                binding.vIndicator.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.binding.vIndicator.context,
                        PriceIndicatorUtils.getIndicatorColor(indicator)
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = pricesList.size

    fun updateData(updatedPricesList: List<EMPItem>) {
        pricesList = updatedPricesList
        notifyDataSetChanged()
    }

    fun addCalendarEvent(position: Int): Intent {
        val item = pricesList[position]
        val startDate: Calendar = Calendar.getInstance().apply { time = item.dateTime }
        val endDate: Calendar = Calendar.getInstance().apply {
            time = Date().apply { time = item.dateTime.time + 3600000L }
        }
        return Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(
                CalendarContract.Events.TITLE,
                context.resources.getString(R.string.calendar_alert_title)
            )
            .putExtra(
                CalendarContract.Events.DESCRIPTION,
                context.resources.getString(R.string.calendar_alert_description)
            )
            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            .putExtra(CalendarContract.Events.HAS_ALARM, true)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.timeInMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.timeInMillis)
            .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
            .putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_FREE
            )
    }

    inner class ViewHolder(val binding: EmpPriceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}