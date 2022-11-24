package com.master.iot.luzi.data.mtpetrol.mapper

import android.graphics.Color
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.master.iot.luzi.ui.electricity.EMPPricesReady

class REEChartMapper {
    companion object {
        /**
         * Map valid REE prices to formatted data for chart view
         */
        fun EMPPricesReady.toBarData(): BarData {
            val values = ArrayList<BarEntry>()
            data.items.forEachIndexed { index, item ->
                values.add(BarEntry(index.toFloat(), item.value.toFloat()))
            }

            val set = BarDataSet(values, "REE prices")
            set.colors = listOf(Color.LTGRAY)
            set.setDrawValues(false)
            set.values = values

            val dataSets = java.util.ArrayList<IBarDataSet>()
            dataSets.add(set)

            return BarData(dataSets)
        }
    }
}