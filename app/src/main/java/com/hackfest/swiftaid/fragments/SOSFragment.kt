package com.hackfest.swiftaid.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentSOSBinding


class SOSFragment : Fragment() {
    private lateinit var binding: FragmentSOSBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val nc = findNavController()
        binding = FragmentSOSBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            nc.navigate(R.id.splashFragment)
        }

        binding.btnSos.setOnClickListener {
            nc.navigate(R.id.nearByFragment)
        }

        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.animSos.visibility = View.VISIBLE
                binding.animSos.playAnimation()

            }, 2000
        )

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call your function to exit the app
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }


}