package com.hackfest.swiftaid.fragments

import android.os.Bundle
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

class AmbulanceDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAmbulanceDetalisBinding
    private val dbRef by lazy {
        FirebaseDatabase.getInstance("https://swiftaid-46a45-default-rtdb.firebaseio.com/").getReference("ambulance")
    }

    private var orgAuthID:String? = null
    private var vehicleNumber:String? = null
    private var driverName:String? = null
    private var driverNumber:String? = null
    private var busy:Boolean? = false
    private var lattitude:String? = "0"
    private var lonbgitude:String? = "0"
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
        binding = FragmentAmbulanceDetalisBinding.inflate(inflater,container,false)

        orgAuthID = arguments?.getString("orgAuthID").toString()

        binding.fabAdd.setOnClickListener {
            vehicleNumber = binding.vehicleNumberEditText.text.toString()
            driverName = binding.driverNameEditText.text.toString()
            driverNumber = binding.driverNumberEditText.text.toString()
            writeNewUserWithTaskListeners(orgAuthID,vehicleNumber, driverName, driverNumber)
            findNavController().navigate(R.id.organisationAmbulancesFragment)
        }

        return binding.root
    }

    fun writeNewUserWithTaskListeners(orgAuthID:String?,vehicleNumber: String?, driverName: String?, driverNumber:String?) {
//        val ambuID = dbRef.push().key!!
        if(binding.chipVentilator.isChecked){
            ventilator = true
        }
        if(binding.chipSuctionUnit.isChecked){
            suctionUnit = true
        }
        if(binding.chipECG.isChecked){
            ecgMonitor = true
        }
        val ambulance = Ambulance(orgAuthID,vehicleNumber,driverName,driverNumber,busy,lattitude, lonbgitude,ventilator,suctionUnit,ecgMonitor)

        // [START rtdb_write_new_user_task]
        dbRef.child(vehicleNumber!!).setValue(ambulance)
            .addOnSuccessListener {
                // Write was successful!
                // ...
                binding.vehicleNumberEditText.text?.clear()
                binding.driverNameEditText.text?.clear()
                binding.driverNumberEditText.text?.clear()
                Toast.makeText(activity,"Ambulance details added",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Write failed
                // ...
                Toast.makeText(activity,"Ambulance details not added",Toast.LENGTH_SHORT).show()
            }
        // [END rtdb_write_new_user_task]
    }

}
