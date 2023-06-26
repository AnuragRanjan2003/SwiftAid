package com.hackfest.swiftaid.fragments.authentication.organisation

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentOrganisationLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrganisationLoginFragment : Fragment() {

    private lateinit var binding: FragmentOrganisationLoginBinding
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val bundle = Bundle()

    private lateinit var email: String
    private lateinit var password: String
    private var orgAuthID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrganisationLoginBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {

            email = binding.emailIdLabel.text.toString()
            password = binding.passwordLabel.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            }else{
                Toast.makeText(this.requireContext(),"Please fill the required details", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

//    public override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
////        if (currentUser != null) {
////            orgAuthID = currentUser.uid
////            reload(orgAuthID)
////        }
//    }

    private fun signIn(email: String, password: String) {

        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Verification", "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Verification", "signInWithEmail:failure", task.exception)
                    makeText(
                        activity, "Authentication failed.",
                        LENGTH_SHORT
                    ).show()

                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            orgAuthID = user.uid
            findNavController().navigate(
                R.id.action_organisationLoginFragment_to_organisationAmbulancesFragment,
                bundle.apply {
                    putString("orgAuthID", orgAuthID)
                })
        }
    }

    private fun reload(orgAuthID: String?) {
        findNavController().navigate(
            R.id.action_organisationLoginFragment_to_organisationAmbulancesFragment,
            bundle.apply {
                putString("orgAuthID", orgAuthID)
            })
    }


}
