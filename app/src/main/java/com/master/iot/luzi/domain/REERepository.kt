package com.master.iot.luzi.domain

import androidx.lifecycle.MutableLiveData
import com.master.iot.luzi.data.NetworkService
import com.master.iot.luzi.domain.dto.EMPData
import com.master.iot.luzi.domain.mapper.REEMapper.Companion.toEMPData
import com.master.iot.luzi.domain.utils.DateFormatterUtils
import com.master.iot.luzi.domain.utils.DateType
import com.master.iot.luzi.ui.electricity.EMPPrices
import com.master.iot.luzi.ui.electricity.EMPPricesError
import com.master.iot.luzi.ui.electricity.EMPPricesLoading
import com.master.iot.luzi.ui.electricity.EMPPricesReady
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class REERepository {

    private val networkService = NetworkService.instance

    fun getEMPPerHour(dateType: DateType = DateType.TODAY): Single<EMPPrices> {
        return networkService.getReeApi()
            .getElectricityMarketPriceByHour(
                startDate = "2022-10-15T00:00",
                endDate = "2022-10-16T00:00"
            ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                    response -> EMPPricesReady(data = response.toEMPData()) as EMPPrices
            }
            .onErrorReturnItem(EMPPricesError("NETWORK ISSUE", 404, "Couldn't reach any data"))
    }


    private fun getStartDate(dateType: DateType): String {
        val calendar = Calendar.getInstance().apply {
            time = Date()
        }
        return DateFormatterUtils.getStringFromDate(calendar.time)
    }

    private fun getEndDate(dateType: DateType): String {
        val calendar = Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DATE, 1)
        }
        return DateFormatterUtils.getStringFromDate(calendar.time)
    }
}