package com.hackfest.swiftaid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentAskBinding


class AskFragment : Fragment() {
    private lateinit var binding: FragmentAskBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAskBinding.inflate(inflater, container, false)
        val nc = findNavController()

        binding.userButton.setOnClickListener {
            nc.navigate(R.id.userLoginFragment)
        }

        binding.orgButton.setOnClickListener {
            nc.navigate(R.id.organisationAskFragment)
        }

        // Inflate the layout for this fragment
        return binding.root
    }


}