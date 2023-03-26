package com.master.iot.luzi.ui.rewards

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.master.iot.luzi.ui.rewards.reports.ListFragment

class RewardsViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private lateinit var reportsFragment: ListFragment

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        reportsFragment = ListFragment.newInstance(ListFragment.Companion.ItemType.REPORT)
        return when (position) {
            0 -> ListFragment.newInstance(ListFragment.Companion.ItemType.PRICE)
            else -> reportsFragment
        }
    }

    fun updateReports() {
        reportsFragment.updateData()
    }
}