package com.hackfest.swiftaid.fragments.map

import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.maps.android.SphericalUtil
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentBottomSheetBinding
import com.hackfest.swiftaid.fragments.maps.trackingfragment
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.models.NearestAmbulanceData
import com.hackfest.swiftaid.models.Request
import com.hackfest.swiftaid.repository.Repository
import com.hackfest.swiftaid.viewModels.UserAmbulanceViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.getInstance
import kotlin.collections.ArrayList
import kotlin.math.floor

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var placeName: String? = ""
    private var dest_Loc: LatLng? = null
    private var my_Loc: LatLng? = null
    private lateinit var mAuth: FirebaseAuth
    private var vn  =""
    private lateinit var trackingfragment:trackingfragment
    private lateinit var ambulance : NearestAmbulanceData
    private var amb = ArrayList<Ambulance>()

    private lateinit var binding: FragmentBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeName = it.getString("place")
            it.getDoubleArray("loc")?.apply {
                dest_Loc = LatLng(this[0], this[1])
            }
            it.getDoubleArray("my_loc")?.apply {
                my_Loc = LatLng(this[0], this[1])
            }

        }


        mAuth = Firebase.auth


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        ambulance  =NearestAmbulanceData()
        ambulance.getambulancelist {
            amb = it
        }
        binding.place.text = placeName
        binding.dist.text = getDistance(my_Loc, dest_Loc)
        trackingfragment = trackingfragment()
        ambulance  = NearestAmbulanceData()

        binding.btnRequest.setOnClickListener {

            val nc = findNavController()


            var uid: String = ""
            uid = if (mAuth.currentUser == null) ""
            else mAuth.uid!!
            val request = Request(
                latitude = my_Loc!!.latitude,
                longitude = my_Loc!!.longitude,
                ventilator = binding.vent.isChecked,
                ecgMonitor = binding.ecg.isChecked,
                suctionUnit = binding.suck.isChecked,
                userID = uid,
                destinationLat = dest_Loc!!.latitude,
                destinationLng = dest_Loc!!.longitude,
                date = getDate(),
                acceptedBy = ""
            )
            e("request", "$request")
            val repo = Repository()
            repo.postRequest(request, requireActivity().applicationContext) {

                Toast.makeText(context, "posted", Toast.LENGTH_LONG).show()
            }
            ambulance.getNearestAmbulance(my_Loc!!.latitude,my_Loc!!.longitude,binding.vent.isChecked,binding.suck.isChecked,amb){
                vn = it.vehicleNumber!!
                val bundle = Bundle()
                bundle.putString("vn",vn)
                bundle.putBoolean("ac", request.ecgMonitor)
                bundle.putBoolean("ventilator", request.ventilator)
                bundle.putString("request", request.requestID)
                trackingfragment.arguments = bundle

                nc.navigate(R.id.action_nearByFragment_to_trackingfragment, bundle)

            }




        }





        return binding.root
    }

    private fun getDistance(loc1: LatLng?, loc2: LatLng?): String {
        if (loc1 == null) return "unknown"
        if (loc2 == null) return "unknown"
        return format(SphericalUtil.computeDistanceBetween(loc1, loc2))
    }

    private fun format(d: Double): String {
        if (d < 1000) return "${floor(d)} m"
        val m = d / 1000
        return "${(floor((m * 100)) / 100)} Km"

    }

    private fun getDate(): String {
        val c: Date = getInstance().time

        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return df.format(c)
    }


}
