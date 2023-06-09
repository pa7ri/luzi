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
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.master.iot.luzi.*
import com.master.iot.luzi.databinding.FragmentRewardsBinding
import com.master.iot.luzi.domain.dto.EMPItem
import com.master.iot.luzi.domain.utils.PriceIndicator
import com.master.iot.luzi.domain.utils.getMaxPrice
import com.master.iot.luzi.domain.utils.toRegularPriceString
import com.master.iot.luzi.ui.ElectricityPreferences
import com.master.iot.luzi.ui.electricity.*
import com.master.iot.luzi.ui.getElectricityPreferences
import com.master.iot.luzi.ui.rewards.appliances.ApplianceItem
import com.master.iot.luzi.ui.rewards.appliances.ApplianceType
import com.master.iot.luzi.ui.rewards.appliances.AppliancesAdapter
import com.master.iot.luzi.ui.rewards.prizes.PrizesViewModel
import com.master.iot.luzi.ui.rewards.receipts.VerifierActivity
import com.master.iot.luzi.ui.rewards.reports.ReportsViewModel
import com.master.iot.luzi.ui.utils.DialogUtils.Companion.showCustomDialogWithOneButton
import com.master.iot.luzi.ui.utils.DialogUtils.Companion.showDialogWithOneButton
import com.master.iot.luzi.ui.utils.EventGenerator
import com.master.iot.luzi.ui.utils.EventGenerator.Companion.ACTION_REWARDS_CHECK_POINTS
import com.master.iot.luzi.ui.utils.EventGenerator.Companion.ACTION_REWARDS_CHECK_SAVING
import com.master.iot.luzi.ui.utils.EventGenerator.Companion.ACTION_REWARDS_CREATE_APPLIANCE_REPORT
import com.master.iot.luzi.ui.utils.EventGenerator.Companion.SCREEN_VIEW_VERIFIER
import com.master.iot.luzi.ui.utils.getCurrentLevel
import com.master.iot.luzi.ui.utils.getLevel
import com.master.iot.luzi.ui.utils.getNextLevel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*


@AndroidEntryPoint
class RewardsFragment : Fragment() {
    private lateinit var binding: FragmentRewardsBinding
    private val reportsViewModel: ReportsViewModel by viewModels()
    private val prizesViewModel: PrizesViewModel by viewModels()
    private val electricityViewModel: ElectricityViewModel by viewModels()

    private lateinit var preferences: SharedPreferences
    private lateinit var electricityPreferences: ElectricityPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var objectType: ApplianceType = ApplianceType.OTHER
    private var isFabExpanded: Boolean = false

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseAnalytics = Firebase.analytics
        binding = FragmentRewardsBinding.inflate(inflater, container, false)
        preferences = requireActivity().getSharedPreferences(getString(R.string.preference_reports_file), Context.MODE_PRIVATE)
        electricityPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getElectricityPreferences()
        setUpToolbar()
        setUpObservers()
        renderHeader()
        setUpListeners()
        return binding.root
    }

    private fun renderHeader() {
        reportsViewModel.getReports(preferences)
        prizesViewModel.getPrizes()
        with(binding) {
            val points = reportsViewModel.getTotalPoints()
            tvPoints.text = points.toString()
            tvProgressSavedAmount.text = reportsViewModel.getTotalSavedAmount().toRegularPriceString()
            val level = getLevel(points).currentLevel
            tvLevel.text = getString(R.string.rewards_level_tag, level)

            preferences.edit().putInt(PREFERENCES_REWARD_LEVEL_KEY, level).apply()
            (binding.vpRewards.adapter as RewardsViewPagerAdapter).updateLevel(level.getCurrentLevel())
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
                val appliancesView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_select_appliance, null, false)
                val rvAppliances = appliancesView.findViewById<RecyclerView>(R.id.rvAppliances)
                rvAppliances.layoutManager = GridLayoutManager(requireContext(), 2)
                val dialog = showCustomDialogWithOneButton(requireContext(), appliancesView, R.string.dialog_select_appliance)
                val adapter = AppliancesAdapter {
                    getElectricityPrices(it)
                    dialog.dismiss()
                }
                rvAppliances.adapter = adapter
                EventGenerator.sendActionEvent(firebaseAnalytics, ACTION_REWARDS_CREATE_APPLIANCE_REPORT)
            }
            fabExpenseReceipt.setOnClickListener {
                handleFabStatus()
                EventGenerator.sendScreenViewEvent(firebaseAnalytics, SCREEN_VIEW_VERIFIER)
                startActivity(Intent(requireContext(), VerifierActivity::class.java))
            }
            pbPoints.setOnClickListener {
                val points = reportsViewModel.getTotalPoints()
                val nextLevel = getLevel(points).getNextLevel()
                val message = if (nextLevel == null) {
                    resources.getString(R.string.dialog_points_message_max, reportsViewModel.getTotalPoints())
                } else {
                    resources.getString(R.string.dialog_points_message, reportsViewModel.getTotalPoints(), nextLevel.rangeVal - points)
                }
                EventGenerator.sendActionEvent(firebaseAnalytics, ACTION_REWARDS_CHECK_POINTS)
                showDialogWithOneButton(requireContext(), getString(R.string.dialog_points), message)
            }
            pbSavedMoney.setOnClickListener {
                EventGenerator.sendActionEvent(firebaseAnalytics, ACTION_REWARDS_CHECK_SAVING)
                showDialogWithOneButton(requireContext(), getString(R.string.dialog_money),
                    getString(R.string.dialog_money_message,
                        reportsViewModel.getTotalSavedAmount().toRegularPriceString(),
                        reportsViewModel.getTotalSpendAmount().toRegularPriceString()))
            }
        }
    }

    private fun setUpToolbar() {
        with(binding) {
            vpRewards.adapter = RewardsViewPagerAdapter(requireActivity())
            TabLayoutMediator(tLRewards, vpRewards) { tab, position ->
                tab.text = if (position == 0) getString(R.string.title_rewards)
                else getString(R.string.title_reports)
            }.attach()
        }
    }

    private fun setUpObservers() {
        electricityViewModel.dataPrices.observe(viewLifecycleOwner) {
            binding.ltLoading.group.visibility = View.GONE
            when (it) {
                is EMPPricesInitial -> {}
                is EMPPricesLoading -> renderLoading(it.title)
                is EMPPricesReady -> {
                    val item = it.data.getCurrentTimeItem()
                    if (item != null) {
                        registerAppliance(item, it.data.items.getMaxPrice(), shouldGeneratePoints(item))
                    } else {
                        renderError(R.string.dialog_validation_error_network)
                    }
                }
                is EMPPricesError -> renderError(R.string.dialog_validation_error_network)
            }
        }
    }

    private fun renderLoading(@StringRes loadingId: Int) {
        binding.ltLoading.group.visibility = View.VISIBLE
        binding.ltLoading.tvLoading.text = getString(loadingId)
    }

    private fun renderError(@StringRes errorDescriptionId: Int) {
        showDialogWithOneButton(requireContext(), R.string.dialog_validation_error, errorDescriptionId)
    }

    /**
     * Check if the item should give points
     *  1. The time is a cheap slot
     *  2. Only during that particular time frame can register one appliance
     *  3. Only once a day that particular appliance
     */
    private fun shouldGeneratePoints(item: EMPItem): Boolean =
        (item.indicator == PriceIndicator.CHEAP
                && !reportsViewModel.anyReportRegisterDuringCurrentHour()
                && !reportsViewModel.hasSameReportRegisteredToday(objectType))

    private fun registerAppliance(electricityItem: EMPItem, maxPrice: Double, withPoints: Boolean = false) {
        val spendAmount = objectType.consumption * electricityItem.value / 1000
        val maxAmount = objectType.consumption * maxPrice / 1000
        val applianceReport = ApplianceItem(
            timestamp = LocalDateTime.now().toString(),
            points = if (withPoints) objectType.points else 0,
            amountSaved = maxAmount - spendAmount,
            amountSpend = spendAmount,
            type = objectType
        )

        val total = preferences.getInt(
            PREFERENCES_REWARD_HISTORY_APPLIANCE_TOTAL_KEY,
            PREFERENCES_REWARD_HISTORY_TOTAL_DEFAULT
        )

        val json = Gson().toJson(applianceReport)
        preferences.edit().apply {
            putString(PREFERENCES_REWARD_HISTORY_APPLIANCE_ITEM_KEY + total, json)
            putInt(PREFERENCES_REWARD_HISTORY_APPLIANCE_TOTAL_KEY, total + 1)
        }.apply()

        renderHeader()
        (binding.vpRewards.adapter as RewardsViewPagerAdapter).updateReports()

        val description = if (withPoints) {
            getString(R.string.dialog_validation_success_description, objectType.points)
        } else {
            getString(R.string.dialog_validation_success_description_no_points)
        }
        showDialogWithOneButton(requireContext(), getString(R.string.dialog_validation_appliance_success), description)
    }

    private fun getElectricityPrices(appliance: ApplianceType) {
        objectType = appliance
        electricityViewModel.updateData(Calendar.getInstance().apply { time = Date() }, electricityPreferences)
    }
}