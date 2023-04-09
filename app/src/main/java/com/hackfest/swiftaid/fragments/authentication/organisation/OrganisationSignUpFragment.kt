package com.hackfest.swiftaid.fragments.authentication.organisation

import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentOrganisationSignUpBinding
import com.hackfest.swiftaid.models.Organisation


class OrganisationSignUpFragment : Fragment() {

    private lateinit var binding: FragmentOrganisationSignUpBinding
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val dbRef by lazy {
        FirebaseDatabase.getInstance("https://swiftaid-hackfest-default-rtdb.firebaseio.com/").getReference("Organisations")
    }

    private var name: String? = null
    private var address: String? = null
    private var affiliationCode: String? = null
    private lateinit var orgID: String
    private var orgAuthID: String? = null
    private val bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        orgID = dbRef.push().key!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentOrganisationSignUpBinding.inflate(inflater, container, false)

        binding.btnSignUp.setOnClickListener {
            val email = binding.emailIdLabel.text.toString()
            val password = binding.passwordLabel.text.toString()
            name = binding.nameOrgLabel.text.toString()
            address = binding.orgAddressLabel.text.toString()
            affiliationCode = binding.affCodeLabel.text.toString()
            initAuth(email, password)

        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        orgAuthID = currentUser?.uid
        if (currentUser != null) {
            reload(orgAuthID)
        }
    }

    private fun initAuth(email: String, password: String) {
        createAccount(email, password)
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("VerificationResult", "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("VerificationResult", "createUserWithEmail:failure", task.exception)
                }
            }
        // [END create_user_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            orgID = dbRef.push().key!!
            orgAuthID = user.uid
            writeNewUserWithTaskListeners(orgAuthID, name, address, affiliationCode)
            findNavController().navigate(R.id.action_organisationSignUpFragment_to_organisationAmbulancesFragment, bundle.apply {
                putString("orgAuthID", orgAuthID)
            })
        }
    }

    private fun reload(orgAuthID: String?) {
        findNavController().navigate(R.id.action_organisationSignUpFragment_to_organisationAmbulancesFragment, bundle.apply {
            putString("orgAuthID", orgAuthID)
        })
    }

    fun writeNewUserWithTaskListeners(
        orgAuthID: String?,
        name: String?,
        address: String?,
        affiliationCode: String?
    ) {

        val organisation = Organisation(orgAuthID, name, address, affiliationCode)

        // [START rtdb_write_new_user_task]
        dbRef.child(orgID).setValue(organisation)
            .addOnSuccessListener {
                e("message: SignUp","Success")
            }
            .addOnFailureListener {
                // Write failed
                // ...
                e("message:SignUp",it.message.toString())
            }
        // [END rtdb_write_new_user_task]
    }


}
