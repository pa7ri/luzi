package com.master.iot.luzi.ui.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_NAME

class EventGenerator {
    companion object {
        const val SCREEN_VIEW_ELECTRICITY = "electricity-screen"
        const val SCREEN_VIEW_PETROL = "petrol-screen"
        const val SCREEN_VIEW_REWARDS = "rewards-screen"
        const val SCREEN_VIEW_SETTINGS = "settings-screen"
        const val SCREEN_VIEW_VERIFIER = "verifier-screen"

        const val ACTION_ELECTRICITY_SHOW_CHART = "electricity-show-chart"
        const val ACTION_ELECTRICITY_SHOW_LIST = "electricity-show-list"
        const val ACTION_ELECTRICITY_CHANGE_DATE = "electricity-change-date"
        const val ACTION_ELECTRICITY_CHANGE_LOCATION = "electricity-change-location"
        const val ACTION_ELECTRICITY_CHANGE_FEE = "electricity-change-fee"
        const val ACTION_ELECTRICITY_ENABLE_PUSH_NOTIFICATION = "electricity-enable-push-notification"
        const val ACTION_ELECTRICITY_DISABLE_PUSH_NOTIFICATION = "electricity-disable-push-notification"

        const val ACTION_PETROL_CHANGE_LOCATION = "petrol-change-location"
        const val ACTION_PETROL_CHANGE_PRODUCT = "petrol-change-product"

        const val ACTION_REWARDS_CHECK_POINTS = "rewards-check-points"
        const val ACTION_REWARDS_CHECK_SAVING = "rewards-check-saving"
        const val ACTION_REWARDS_CREATE_APPLIANCE_REPORT = "rewards-create-appliance-report"
        const val ACTION_REWARDS_CREATE_RECEIPT_REPORT = "rewards-create-receipt-report"

        private fun createSimpleEvent(name: String): Bundle {
            return Bundle().apply {
                putString(ITEM_NAME, name)
            }
        }

        fun sendScreenViewEvent(firebaseAnalytics: FirebaseAnalytics, action: String) {
            firebaseAnalytics.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                createSimpleEvent(action)
            )
        }

        fun sendActionEvent(firebaseAnalytics: FirebaseAnalytics, action: String) {
            firebaseAnalytics.logEvent(
                FirebaseAnalytics.Event.SELECT_CONTENT,
                createSimpleEvent(action)
            )
        }
    }

}