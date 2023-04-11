package com.master.iot.luzi.ui.rewards.reports

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.master.iot.luzi.PREFERENCES_REWARD_HISTORY_ITEM_KEY
import com.master.iot.luzi.PREFERENCES_REWARD_HISTORY_TOTAL_DEFAULT
import com.master.iot.luzi.PREFERENCES_REWARD_HISTORY_TOTAL_KEY
import com.master.iot.luzi.domain.utils.DateFormatterUtils
import com.master.iot.luzi.ui.rewards.prizes.PrizeItem
import com.master.iot.luzi.ui.rewards.prizes.getPrizesList
import com.master.iot.luzi.ui.utils.Levels
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
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
        return reports.value.orEmpty().sumOf { it.amount }
    }


    fun getReports(preferences: SharedPreferences) {
        val localReports = mutableListOf<ReportItem>()
        val gson = Gson()
        val total = preferences.getInt(
            PREFERENCES_REWARD_HISTORY_TOTAL_KEY,
            PREFERENCES_REWARD_HISTORY_TOTAL_DEFAULT
        )

        for (i in 0..total) {
            val json = preferences.getString(PREFERENCES_REWARD_HISTORY_ITEM_KEY + i, "")
            if (json?.isNotEmpty()==true) {
                val report = gson.fromJson(json, ReportItem::class.java)
                localReports.add(report)
            }
        }

        val filteredValues = filterMonthlyReports(localReports)
        writeLocalReports(preferences, filteredValues)
        reports.value = filteredValues
    }

    private fun filterMonthlyReports(reports: List<ReportItem>): List<ReportItem> {
        return reports.filter {
            val localTime = LocalDate.parse(it.timestamp.subSequence(0, 23).toString(), DateFormatterUtils.formatterReport)
            localTime.month.value==LocalDate.now().month.value && localTime.year==LocalDate.now().year
        }
    }

    private fun writeLocalReports(preferences: SharedPreferences, reports: List<ReportItem>) {
        preferences.edit().putInt(PREFERENCES_REWARD_HISTORY_TOTAL_KEY, reports.size).apply()
        reports.forEachIndexed { index, reportItem ->
            val json = Gson().toJson(reportItem)
            preferences.edit().apply {
                putString(PREFERENCES_REWARD_HISTORY_ITEM_KEY + index, json)
            }.apply()
        }
    }
}