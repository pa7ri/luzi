package com.master.iot.luzi.ui.rewards.reports

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.master.iot.luzi.*
import com.master.iot.luzi.domain.utils.DateFormatterUtils
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.filterMonthlyAppliances
import com.master.iot.luzi.domain.utils.DateFormatterUtils.Companion.filterMonthlyReceipts
import com.master.iot.luzi.ui.rewards.appliances.ApplianceItem
import com.master.iot.luzi.ui.rewards.appliances.ApplianceType
import com.master.iot.luzi.ui.rewards.receipts.ReceiptItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor() : ViewModel() {
    val reports = MutableLiveData<List<ReportItem>>().apply {
        value = emptyList()
    }

    fun getTotalPoints(): Int {
        return reports.value.orEmpty().sumOf { it.points }
    }

    fun getTotalSavedAmount(): Double {
        return reports.value.orEmpty().sumOf { it.amountSaved }
    }

    fun getTotalSpendAmount(): Double {
        return reports.value.orEmpty().sumOf { it.amountSpend }
    }

    fun getReports(preferences: SharedPreferences) {
        val localReports = mutableListOf<ReportItem>()

        getApplianceReports(preferences).let {
            if (it.isNotEmpty()) {
                localReports.add(HeaderItem("Appliances"))
                localReports.addAll(it)
            }
        }

        getReceiptReports(preferences).let {
            if (it.isNotEmpty()) {
                localReports.add(HeaderItem("Receipts"))
                localReports.addAll(it)
            }
        }

        reports.value = localReports
    }

    private fun getApplianceReports(preferences: SharedPreferences): List<ApplianceItem> {
        val appliances = mutableListOf<ApplianceItem>()
        val gson = Gson()
        val total = preferences.getInt(
            PREFERENCES_REWARD_HISTORY_APPLIANCE_TOTAL_KEY,
            PREFERENCES_REWARD_HISTORY_TOTAL_DEFAULT
        )

        for (i in 0..total) {
            val json = preferences.getString(PREFERENCES_REWARD_HISTORY_APPLIANCE_ITEM_KEY + i, "")
            if (json?.isNotEmpty() == true) {
                appliances.add(gson.fromJson(json, ApplianceItem::class.java))
            }
        }

        val filteredValues = filterMonthlyAppliances(appliances)
        writeLocalReports(preferences, filteredValues, PREFERENCES_REWARD_HISTORY_APPLIANCE_TOTAL_KEY, PREFERENCES_REWARD_HISTORY_APPLIANCE_ITEM_KEY)
        return filteredValues
    }

    private fun getReceiptReports(preferences: SharedPreferences): List<ReceiptItem> {
        val receipts = mutableListOf<ReceiptItem>()
        val gson = Gson()
        val total = preferences.getInt(
            PREFERENCES_REWARD_HISTORY_RECEIPT_TOTAL_KEY,
            PREFERENCES_REWARD_HISTORY_TOTAL_DEFAULT
        )

        for (i in 0..total) {
            val json = preferences.getString(PREFERENCES_REWARD_HISTORY_RECEIPT_ITEM_KEY + i, "")
            if (json?.isNotEmpty() == true) {
                val report = gson.fromJson(json, ReceiptItem::class.java)
                receipts.add(report)
            }
        }

        val filteredValues = filterMonthlyReceipts(receipts)
        writeLocalReports(preferences, filteredValues, PREFERENCES_REWARD_HISTORY_RECEIPT_TOTAL_KEY, PREFERENCES_REWARD_HISTORY_RECEIPT_ITEM_KEY)
        return filteredValues
    }

    fun anyReportRegisterDuringCurrentHour(): Boolean {
        val currentTime = LocalDateTime.now()
        return reports.value?.filterIsInstance<ApplianceItem>()?.any {
            val localTime = LocalDateTime.parse(it.timestamp.subSequence(0, 23).toString(), DateFormatterUtils.formatterReport)
            DateFormatterUtils.areSameDay(localTime, currentTime) && localTime.hour == currentTime.hour
        } ?: false
    }

    fun hasSameReportRegisteredToday(objectType: ApplianceType): Boolean {
        val currentTime = LocalDateTime.now()
        return reports.value?.filterIsInstance<ApplianceItem>()?.any {
            val localTime = LocalDateTime.parse(it.timestamp.subSequence(0, 23).toString(), DateFormatterUtils.formatterReport)
            DateFormatterUtils.areSameDay(localTime, currentTime) && objectType == it.type
        } ?: false
    }

    private fun writeLocalReports(preferences: SharedPreferences, localReports: List<ReportItem>, totalKey: String, prefixKey: String) {
        preferences.edit().putInt(totalKey, localReports.size).apply()
        localReports.forEachIndexed { index, reportItem ->
            val json = Gson().toJson(reportItem)
            preferences.edit().apply {
                putString(prefixKey + index, json)
            }.apply()
        }
    }
}