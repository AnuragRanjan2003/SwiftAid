package com.hackfest.swiftaid.fragments.authentication.organisation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentOrganizationAskBinding
import com.hackfest.swiftaid.databinding.FragmentUserLoginBinding


class OrganisationAskFragment : Fragment() {

    private lateinit var binding: FragmentOrganizationAskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrganizationAskBinding.inflate(inflater, container, false)
        val nc = findNavController()
        binding.loginButton.setOnClickListener {
            nc.navigate(R.id.organisationLoginFragment)

        }

        binding.signUpButton.setOnClickListener {
            nc.navigate(R.id.organisationSignUpFragment)
        }
        // Inflate the layout for this fragment
        return binding.root
    }


}