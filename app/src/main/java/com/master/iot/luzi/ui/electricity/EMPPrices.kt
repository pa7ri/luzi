package com.master.iot.luzi.ui.electricity

import com.master.iot.luzi.R
import com.master.iot.luzi.domain.dto.EMPData
import com.master.iot.luzi.domain.dto.EMPItem
import java.util.*

sealed class EMPPrices

object EMPPricesInitial : EMPPrices()
class EMPPricesLoading(val title: Int = R.string.loading_data_title) : EMPPrices()
class EMPPricesReady(val data: EMPData) : EMPPrices()
class EMPPricesError(
    val title: String,
    val description: String,
    val imageId: Int = R.raw.anim_empty
) : EMPPrices()


fun EMPData.getCurrentTimeItem(): EMPItem? {
    val currentTime = Date().apply { time = Calendar.getInstance().timeInMillis }
    return items.firstOrNull { currentTime.hours==it.dateTime.hours }
}