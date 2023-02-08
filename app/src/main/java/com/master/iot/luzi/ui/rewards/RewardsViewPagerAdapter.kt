package com.master.iot.luzi.ui.rewards

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RewardsViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return ReportsFragment.newInstance(
            when (position) {
                0 -> ReportsFragment.Companion.ItemType.PRICE
                else -> ReportsFragment.Companion.ItemType.REPORT
            }
        )
    }
}