package com.master.iot.luzi.ui.petrol

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.databinding.MtPriceItemBinding
import com.master.iot.luzi.domain.dto.MTPetrolProductItem


class MTPricesAdapter(private var pricesList: List<MTPetrolProductItem>) :
    RecyclerView.Adapter<MTPricesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MtPriceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(pricesList[position]) {
                binding.tvPetrolName.text = name
                binding.tvPrice.text = price.toString()
            }
        }
    }

    override fun getItemCount(): Int = pricesList.size

    inner class ViewHolder(val binding: MtPriceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}