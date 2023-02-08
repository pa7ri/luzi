package com.master.iot.luzi.ui.rewards

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.master.iot.luzi.PREFERENCES_REWARD_LEVEL_DEFAULT
import com.master.iot.luzi.PREFERENCES_REWARD_LEVEL_KEY
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.FragmentRewardsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardsFragment : Fragment() {

    private lateinit var binding: FragmentRewardsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardsBinding.inflate(inflater, container, false)
        setUpToolbar()
        setUpListeners()
        return binding.root
    }

    private fun setUpListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), VerifierActivity::class.java))
        }
    }

    private fun setUpToolbar() {
        with(binding) {
            val level = requireActivity().getPreferences(Context.MODE_PRIVATE)?.getInt(
                PREFERENCES_REWARD_LEVEL_KEY, 2
            )
            tvLevel.text = getString(R.string.rewards_level_tag, level)
            vpRewards.adapter = RewardsViewPagerAdapter(requireActivity())
            TabLayoutMediator(tLRewards, vpRewards) { tab, position ->
                tab.text = if (position == 0) getString(R.string.title_rewards)
                else getString(R.string.title_reports)
            }.attach()
        }
        binding.vpRewards.adapter = RewardsViewPagerAdapter(requireActivity())
    }
}