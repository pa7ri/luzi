package com.master.iot.luzi.ui.petrol

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.master.iot.luzi.R
import com.master.iot.luzi.domain.dto.MTPetrolPricesData

class PetrolItemDialog(context: Context, private val items: MTPetrolPricesData) : Dialog(context),
    View.OnClickListener {

    private lateinit var petrolStationTV: TextView
    private lateinit var pricesRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_petrol_item)

        setUpAdapter()
    }

    private fun setUpAdapter() {
        petrolStationTV = findViewById(R.id.tvPetrolStation)
        pricesRV = findViewById(R.id.rvPrices)

        petrolStationTV.text = items.petrolStationName
        pricesRV.adapter = MTPricesAdapter(items.products)
    }

    override fun onClick(view: View) {
        dismiss()
    }

}
