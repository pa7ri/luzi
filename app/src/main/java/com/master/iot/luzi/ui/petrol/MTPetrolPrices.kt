package com.master.iot.luzi.ui.petrol

import com.master.iot.luzi.R
import com.master.iot.luzi.domain.dto.MTPetrolStationData

sealed class MTPetrolPrices

class MTPetrolPricesLoading(val title: Int = R.string.loading_petrol_data_title) : MTPetrolPrices()
class MTPetrolPricesReady(val prices: List<MTPetrolStationData>) : MTPetrolPrices()
class MTPetrolPricesError(val description: String) : MTPetrolPrices()