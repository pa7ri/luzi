package com.master.iot.luzi.ui.petrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.master.iot.luzi.databinding.FragmentPetrolBinding

class PetrolFragment : Fragment() {

    private var _binding: FragmentPetrolBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val petrolViewModel =
            ViewModelProvider(this)[PetrolViewModel::class.java]

        _binding = FragmentPetrolBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPetrol
        petrolViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}