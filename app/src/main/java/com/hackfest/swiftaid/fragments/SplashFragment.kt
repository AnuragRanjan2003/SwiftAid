package com.hackfest.swiftaid.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    val auth = Firebase.auth
    val cu = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        binding.getStarted.setOnClickListener {
            if (cu != null) {
                val providedata = cu.providerData
                var hasG = false
                for (userInfo in providedata) {
                    if (userInfo.providerId == GoogleAuthProvider.PROVIDER_ID) {
                        hasG = true
                        break
                    }
                }
                if (hasG) {
                    findNavController().navigate(R.id.action_splashFragment_to_SOSFragment)
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_organisationAmbulancesFragment)
                }
            } else
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