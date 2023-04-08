package com.hackfest.swiftaid.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
   private lateinit var binding: FragmentSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater,container,false)
        binding.getStarted.setOnClickListener {

            findNavController().navigate(R.id.askFragment)

        }

        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.httpsLotti.visibility = View.VISIBLE
                binding.httpsLotti.playAnimation()
                binding.yourPerson.visibility = View.VISIBLE
                binding.yourPerson.playAnimation()
            }, 2000
        )

        return binding.root
    }


}