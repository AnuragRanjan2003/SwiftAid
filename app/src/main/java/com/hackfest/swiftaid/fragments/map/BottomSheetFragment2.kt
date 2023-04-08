package com.hackfest.swiftaid.fragments.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hackfest.swiftaid.databinding.FragmentBottomSheet2Binding


class BottomSheetFragment2 : BottomSheetDialogFragment() {
    private var drivername: String = ""
    private var driverNumber: String = ""
    private var vehicleNumber: String = ""
    private lateinit var binding: FragmentBottomSheet2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            drivername = it.getString("drivername")!!
            driverNumber = it.getString("drivernumber")!!
            vehicleNumber = it.getString("vehiclenumber")!!


        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheet2Binding.inflate(inflater, container, false)
        binding.drivername.text = "Driver name - " + drivername
        binding.drivernumber.text = "Driver number - " + driverNumber
        binding.vehiclenumber.text = "Vehicle number - " + vehicleNumber



        return binding.root
    }


}
