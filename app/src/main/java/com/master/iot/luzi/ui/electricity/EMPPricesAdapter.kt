package com.master.iot.luzi.ui.electricity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.EmpPriceItemBinding
import com.master.iot.luzi.domain.dto.EMPItem
import com.master.iot.luzi.domain.dto.EMPItemIndicator
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.getHourFromDate


class EMPPricesAdapter(private var pricesList: List<EMPItem>) :
    RecyclerView.Adapter<EMPPricesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            EmpPriceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(pricesList[position]) {
                binding.tvHour.text = dateTime.getHourFromDate()
                binding.tvPriceValue.text = value.toBigDecimal().toPlainString()
                binding.vIndicator.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.binding.vIndicator.context,
                        getIndicatorColor(indicator)
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

    private fun getIndicatorColor(indicator: EMPItemIndicator): Int =
        when (indicator) {
            EMPItemIndicator.CHEAP -> R.color.green_200
            EMPItemIndicator.EXPENSIVE -> R.color.red_200
            else -> R.color.orange_200
        }

    inner class ViewHolder(val binding: EmpPriceItemBinding) : RecyclerView.ViewHolder(binding.root)

}