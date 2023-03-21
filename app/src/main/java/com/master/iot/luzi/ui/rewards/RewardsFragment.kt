package com.master.iot.luzi.ui.rewards

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.master.iot.luzi.PREFERENCES_REWARD_HISTORY_ITEM_KEY
import com.master.iot.luzi.PREFERENCES_REWARD_HISTORY_TOTAL_DEFAULT
import com.master.iot.luzi.PREFERENCES_REWARD_HISTORY_TOTAL_KEY
import com.master.iot.luzi.R
import com.master.iot.luzi.databinding.FragmentRewardsBinding
import com.master.iot.luzi.domain.utils.toRegularPriceString
import com.master.iot.luzi.ui.utils.getNextLevel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime


@AndroidEntryPoint
class RewardsFragment : Fragment() {

    private lateinit var binding: FragmentRewardsBinding
    private val viewModel: ListViewModel by viewModels()

    private val openAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val closeAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_fab_animation
        )
    }
    private val toAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_fab_anim
        )
    }

    private var isFabExpanded: Boolean = false

    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardsBinding.inflate(inflater, container, false)
        preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        setUpToolbar()
        renderHeader()
        setUpListeners()
        return binding.root
    }

    fun renderHeader() {
        viewModel.getReports(preferences)
        viewModel.getPrizes()
        with(binding) {
            val points = viewModel.getTotalPoints()
            tvPoints.text = points.toString()
            tvProgressSavedAmount.text = viewModel.getTotalSavedAmount().toRegularPriceString()
            tvLevel.text = getString(R.string.rewards_level_tag, viewModel.getLevel(points).currentLevel)
        }
    }

    private fun setFabVisibility() {
        with(binding) {
            fabExpenseManual.visibility = if (!isFabExpanded) View.VISIBLE else View.GONE
            fabExpenseReceipt.visibility = if (!isFabExpanded) View.VISIBLE else View.GONE
        }
    }

    private fun setFabClickable() {
        with(binding) {
            fabExpenseManual.isClickable = !isFabExpanded
            fabExpenseReceipt.isClickable = !isFabExpanded
        }
    }

    private fun setFabAnimations() {
        with(binding) {
            if (!isFabExpanded) {
                fabAdd.startAnimation(openAnim)
                fabExpenseManual.startAnimation(fromAnim)
                fabExpenseReceipt.startAnimation(fromAnim)
            } else {
                fabAdd.startAnimation(closeAnim)
                fabExpenseManual.startAnimation(toAnim)
                fabExpenseReceipt.startAnimation(toAnim)
            }
        }
    }

    private fun handleFabStatus() {
        setFabVisibility()
        setFabAnimations()
        setFabClickable()
        isFabExpanded = !isFabExpanded
    }

    private fun setUpListeners() {
        with(binding) {
            fabAdd.setOnClickListener { handleFabStatus() }
            fabExpenseManual.setOnClickListener {
                handleFabStatus()
                // TODO: guardar un registro nuevo
                val testReport = ReportItem(
                    ObjectType.WASHING_MACHINE,
                    LocalDateTime.now().toString(),
                    10,
                    0.12
                )

                val total = preferences.getInt(
                    PREFERENCES_REWARD_HISTORY_TOTAL_KEY,
                    PREFERENCES_REWARD_HISTORY_TOTAL_DEFAULT
                )

                val json = Gson().toJson(testReport)
                preferences.edit().apply {
                    putString(PREFERENCES_REWARD_HISTORY_ITEM_KEY + total, json)
                    putInt(PREFERENCES_REWARD_HISTORY_TOTAL_KEY, total + 1)
                }.apply()
            }
            fabExpenseReceipt.setOnClickListener {
                handleFabStatus()
                startActivity(Intent(requireContext(), VerifierActivity::class.java))
            }
            pbPoints.setOnClickListener {
                val points = viewModel.getTotalPoints()
                val nextLevel = viewModel.getLevel(points).getNextLevel()
                val message = if (nextLevel==null) {
                    resources.getString(R.string.dialog_points_message_max, viewModel.getTotalPoints())
                } else {
                    resources.getString(R.string.dialog_points_message, viewModel.getTotalPoints(), nextLevel.rangeVal - points)
                }
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.dialog_points))
                    .setMessage(message)
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
            pbSavedMoney.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.dialog_money))
                    .setMessage(resources.getString(R.string.dialog_money_message, viewModel.getTotalSavedAmount().toString()))
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
        }
    }

    private fun setUpToolbar() {
        with(binding) {
            vpRewards.adapter = RewardsViewPagerAdapter(requireActivity())
            TabLayoutMediator(tLRewards, vpRewards) { tab, position ->
                tab.text = if (position==0) getString(R.string.title_rewards)
                else getString(R.string.title_reports)
            }.attach()
        }
    }
}