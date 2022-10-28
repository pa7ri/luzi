package com.master.iot.luzi.domain

import com.master.iot.luzi.data.NetworkService
import com.master.iot.luzi.domain.mapper.REEMapper.Companion.toEMPData
import com.master.iot.luzi.domain.utils.DateFormatterUtils
import com.master.iot.luzi.ui.electricity.EMPPrices
import com.master.iot.luzi.ui.electricity.EMPPricesError
import com.master.iot.luzi.ui.electricity.EMPPricesReady
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class REERepository {

    private val networkService = NetworkService.instance

    fun getEMPPerHour(selectedDate: Calendar): io.reactivex.Observable<EMPPrices> {
        return networkService.getReeApi()
            .getElectricityMarketPriceByHour(
                startDate = getStartDate(selectedDate),
                endDate = getEndDate(selectedDate)
            ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> EMPPricesReady(data = response.toEMPData()) as EMPPrices }
            .onErrorReturn {
                EMPPricesError(
                    it.message ?: "Network issue",
                    it.localizedMessage ?: ""
                )
            }
    }


    private fun getStartDate(calendar: Calendar): String {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }
        return DateFormatterUtils.getStringFromDate(calendar.time)
    }

    private fun getEndDate(calendar: Calendar): String {
        calendar.apply {
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }
        return DateFormatterUtils.getStringFromDate(calendar.time)
    }
}