package com.master.iot.luzi.ui.electricity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.master.iot.luzi.databinding.FragmentElectricityBinding

class ElectricityFragment : Fragment() {

    private var _binding: FragmentElectricityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val electricityViewModel =
            ViewModelProvider(this)[ElectricityViewModel::class.java]

        _binding = FragmentElectricityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textElectricity
        electricityViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}