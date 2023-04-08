package com.hackfest.swiftaid.fragments.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.SphericalUtil
import com.hackfest.swiftaid.databinding.FragmentBottomSheetBinding
import com.hackfest.swiftaid.models.Request
import com.hackfest.swiftaid.repository.Repository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor


class BottomSheetFragment : BottomSheetDialogFragment() {
    private var placeName: String? = ""
    private var dest_Loc: LatLng? = null
    private var my_Loc: LatLng? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentBottomSheetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.placeName = it.getString("place")
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
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        binding.place.text = placeName
        binding.dist.text = getDistance(my_Loc, dest_Loc)

        binding.btnRequest.setOnClickListener {
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
                destName = placeName!!,
                destinationLat = dest_Loc!!.latitude,
                destinationLng = dest_Loc!!.longitude,
                date = getDate(),
                acceptedBy = ""
            )
            Log.e("request", "$request")
            val repo = Repository()
            repo.postRequest(request, requireActivity().applicationContext) {
                dismiss()
                Toast.makeText(context, "posted", Toast.LENGTH_LONG).show()
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
        val c: Date = Calendar.getInstance().time

        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return df.format(c)
    }


}