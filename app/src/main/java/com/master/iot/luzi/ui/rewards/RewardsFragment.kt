package com.master.iot.luzi.ui.rewards

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.master.iot.luzi.databinding.FragmentRewardsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardsFragment : Fragment() {

    private val rewardsViewModel: RewardsViewModel by viewModels()
    private lateinit var binding: FragmentRewardsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardsBinding.inflate(inflater, container, false)

        setUpListeners()

        return binding.root
    }

    private fun setUpListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), VerifierActivity::class.java))
        }
    }
}