package com.master.iot.luzi.ui.electricity

import com.master.iot.luzi.domain.dto.EMPData

sealed class EMPPrices

class EMPPricesLoading(val title: String) : EMPPrices()
class EMPPricesReady(val data: EMPData) : EMPPrices()
class EMPPricesError(val title: String, code: Int, description: String) : EMPPrices()