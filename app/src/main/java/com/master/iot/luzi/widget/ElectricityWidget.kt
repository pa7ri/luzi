package com.master.iot.luzi.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.master.iot.luzi.R
import com.master.iot.luzi.domain.REERepository
import com.master.iot.luzi.domain.utils.*
import com.master.iot.luzi.ui.electricity.EMPPricesReady
import com.master.iot.luzi.ui.getElectricityPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ElectricityWidget : AppWidgetProvider() {
    companion object {
        const val ON_CLICK_ACTION = "onclick.action"
    }

    private lateinit var views: RemoteViews

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(ON_CLICK_ACTION)) {
            Toast.makeText(context, "CLICK!", Toast.LENGTH_LONG).show()
            refreshData(context)
        }
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        refreshData(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun refreshData(context: Context) {
        views = RemoteViews(context.packageName, R.layout.electricity_widget)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                ElectricityWidget::class.java
            )
        )
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val preferences =
            PreferenceManager.getDefaultSharedPreferences(context).getElectricityPreferences()
        views = RemoteViews(context.packageName, R.layout.electricity_widget)
        views.apply {
            setOnClickPendingIntent(R.id.ltLoading, getOnClickIntent(context))
            setViewVisibility(R.id.ltLoading, View.GONE)
            val prices = REERepository().getEMPPerHour(
                Calendar.getInstance().apply { time = Date() },
                preferences
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingGet()
            if (prices is EMPPricesReady) {
                setTextViewText(
                    R.id.tvMinPriceValue,
                    prices.data.items.getMinPrice().toPriceString()
                )
                setTextViewText(
                    R.id.tvMinPriceHour,
                    context.getString(R.string.hour_format, prices.data.items.getMinHour())
                )
                setTextViewText(
                    R.id.tvMaxPriceValue,
                    prices.data.items.getMaxPrice().toPriceString()
                )
                setTextViewText(
                    R.id.tvMaxPriceHour,
                    context.getString(R.string.hour_format, prices.data.items.getMaxHour())
                )
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getOnClickIntent(context: Context): PendingIntent {
        val intent = Intent(context, ElectricityWidget::class.java)
        intent.action = ON_CLICK_ACTION
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}