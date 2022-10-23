package com.master.iot.luzi.ui.electricity

import com.master.iot.luzi.R
import com.master.iot.luzi.domain.dto.EMPData

sealed class EMPPrices

class EMPPricesLoading(val title: Int = R.string.loading_data_title) : EMPPrices()
class EMPPricesReady(val data: EMPData) : EMPPrices()
class EMPPricesError(
    val title: String,
    val description: String,
    val code: Int,
    val imageId: Int = R.raw.anim_empty
) : EMPPrices()