package com.master.iot.luzi.ui.rewards

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.PrizeItemBinding

class PrizesAdapter(private var currentLevel: Int, private var prizes: List<PrizeItem>) :
    RecyclerView.Adapter<PrizesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PrizeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = prizes[position]
        with(holder) {
            if (currentLevel < item.level) {
                val color = holder.binding.root.context.getColor(R.color.blue_charcoal_200)
                binding.tvTitle.text = item.title
                binding.tvTitle.setTextColor(color)
                binding.tvDescription.text =
                    holder.binding.root.context.getString(R.string.reward_not_available, item.level)
                binding.tvDescription.setTextColor(color)
                binding.ivIcon.backgroundTintList =
                    ColorStateList.valueOf(binding.root.context.getColor(R.color.purple_100_40))
                ContextCompat.getDrawable(holder.binding.root.context, item.resourceId)?.let {
                    it.setTint(Color.WHITE)
                    binding.ivIcon.setImageDrawable(it)
                }
            } else {
                binding.tvTitle.text = item.title
                binding.tvDescription.text = item.description
                binding.ivIcon.backgroundTintList =
                    ColorStateList.valueOf(binding.root.context.getColor(R.color.purple_300))
                ContextCompat.getDrawable(holder.binding.root.context, item.resourceId)?.let {
                    it.setTint(Color.WHITE)
                    binding.ivIcon.setImageDrawable(it)
                }
            }
        }
    }

    override fun getItemCount(): Int = prizes.size


    fun updatePrizes(newLevel: Int, newPrizes: List<PrizeItem>) {
        currentLevel = newLevel
        prizes = newPrizes
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: PrizeItemBinding) : RecyclerView.ViewHolder(binding.root)

}