package com.master.iot.luzi.ui.electricity

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.databinding.EmpPriceItemBinding
import com.master.iot.luzi.domain.dto.EMPItem
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.getHourFromDate
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
                binding.root.setOnClickListener { addAlertToCalendar(this) }
                binding.tvHour.text = dateTime.getHourFromDate()
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

    private fun addAlertToCalendar(item : EMPItem) {
        //Toast.makeText(context, "Adding Event To Your Calendar...", Toast.LENGTH_SHORT).show()

        /* Create calendar event */
        val event = ContentValues()
        val startDate: Calendar = Calendar.getInstance().apply { time = item.dateTime }
        val endDate: Calendar = Calendar.getInstance().apply {
            val endTime = Date().apply { item.dateTime.time + 60000L }
            time = endTime
        }
        event.put(CalendarContract.Events.CALENDAR_ID, 1)
        event.put(CalendarContract.Events.TITLE, "Precio de la luz")
        event.put(CalendarContract.Events.DESCRIPTION, "La alarma que configurada")
        //event.put(CalendarContract.Events.EVENT_LOCATION, EventLocation)
        event.put(CalendarContract.Events.DTSTART, startDate.timeInMillis)
        event.put(CalendarContract.Events.DTEND, endDate.timeInMillis)
        event.put(CalendarContract.Events.ALL_DAY, false)
        event.put(CalendarContract.Events.HAS_ALARM, true)
        event.put(CalendarContract.Events.RRULE, "ONCE")
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "GMT-05:00")

        /* Add reminder */
        val url = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, event)
        val eventId: Long = url?.lastPathSegment?.toLong() ?: 0L
        val reminder = ContentValues()
        reminder.put(CalendarContract.Reminders.EVENT_ID, eventId)
        reminder.put(CalendarContract.Reminders.MINUTES, 10)
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminder)

        //Toast.makeText(this, "Event Added To Your Calendar!", Toast.LENGTH_SHORT).show()
    }

    inner class ViewHolder(val binding: EmpPriceItemBinding) : RecyclerView.ViewHolder(binding.root)

}