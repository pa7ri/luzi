package com.master.iot.luzi.ui.rewards

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.master.iot.luzi.ui.rewards.prizes.PrizesFragment
import com.master.iot.luzi.ui.rewards.reports.ReportsFragment
import com.master.iot.luzi.ui.utils.Levels

class RewardsViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private var reportsFragment: ReportsFragment? = null
    private var priceFragment: PrizesFragment = PrizesFragment()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        reportsFragment = ReportsFragment(::updateLevel)
        return when (position) {
            0 -> priceFragment
            else -> reportsFragment!!
        }
    }

    fun updateReports() {
        reportsFragment?.let {
            if (it.isVisible) {
                it.updateData()
            }
        }
    }

    fun updateLevel(level: Levels) {
        if (priceFragment.isVisible) {
            priceFragment.updateLevel(level)
        }
    }
}