package com.hackfest.swiftaid.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentAskBinding


class AskFragment : Fragment() {
    private lateinit var binding : FragmentAskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAskBinding.inflate(inflater,container,false)

        binding.ask.setOnClickListener {
            findNavController().navigate(R.id.nearByFragment)
        }
        return binding.root
    }


}