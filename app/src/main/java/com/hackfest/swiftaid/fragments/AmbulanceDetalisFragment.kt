package com.hackfest.swiftaid.fragments

import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentAmbulanceDetalisBinding
import com.hackfest.swiftaid.models.Ambulance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmbulanceDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAmbulanceDetalisBinding
    private val dbRef by lazy {
        FirebaseDatabase.getInstance("https://swiftaid-hackfest-default-rtdb.firebaseio.com/")
            .getReference("ambulance")
    }

    private var orgAuthID: String? = null
    private var vehicleNumber: String? = null
    private var driverName: String? = null
    private var driverNumber: String? = null
    private var busy: Boolean? = false
    private var lattitude: String? = "0"
    private var lonbgitude: String? = "0"
    var ventilator = false
    var suctionUnit = false
    var ecgMonitor = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAmbulanceDetalisBinding.inflate(inflater, container, false)

        orgAuthID = arguments?.getString("orgAuthID").toString()

        binding.btnContinue.setOnClickListener {
            vehicleNumber = binding.vehicleNumberEditText.text.toString()
            driverName = binding.driverNameEditText.text.toString()
            driverNumber = binding.driverNumberEditText.text.toString()
            writeNewUserWithTaskListeners(orgAuthID, vehicleNumber, driverName, driverNumber)
            val bundle = Bundle()
            bundle.putString("orgAuthID", orgAuthID)
            findNavController().navigate(R.id.organisationAmbulancesFragment, bundle)
        }

//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                // Call your function to exit the app
//                requireActivity().finish()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    fun writeNewUserWithTaskListeners(
        orgAuthID: String?,
        vehicleNumber: String?,
        driverName: String?,
        driverNumber: String?
    ) {
//        val ambuID = dbRef.push().key!!
        if (binding.chipVentilator.isChecked) {
            ventilator = true
        }
        if (binding.chipSuctionUnit.isChecked) {
            suctionUnit = true
        }
        if (binding.chipECG.isChecked) {
            ecgMonitor = true
        }
        val ambulance = Ambulance(
            orgAuthID,
            vehicleNumber,
            driverName,
            driverNumber,
            false,
            lattitude,
            lonbgitude,
            ventilator,
            suctionUnit,
            ecgMonitor
        )
        e("amb", "$ambulance")
        // [START rtdb_write_new_user_task]
        dbRef.child(vehicleNumber!!).setValue(ambulance)
            .addOnSuccessListener {
                // Write was successful!
                // ...
                binding.vehicleNumberEditText.text?.clear()
                binding.driverNameEditText.text?.clear()
                binding.driverNumberEditText.text?.clear()
                Toast.makeText(activity, "Ambulance details added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Write failed
                // ...
                Toast.makeText(activity, "Ambulance details not added", Toast.LENGTH_SHORT).show()
            }
        // [END rtdb_write_new_user_task]
    }

}
