package com.hackfest.swiftaid.fragments.maps

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.constants.MAP_ZOOM
import com.hackfest.swiftaid.databinding.FragmentTrackingfragmentBinding
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.models.Failure
import com.hackfest.swiftaid.models.Success

import com.hackfest.swiftaid.viewModels.MapsViewModel
import com.hackfest.swiftaid.viewModels.UserAmbulanceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class trackingfragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {
    private lateinit var binding: FragmentTrackingfragmentBinding
    private  val mapsViewModel by viewModels<MapsViewModel>()
    private lateinit var map: GoogleMap
    private lateinit var myMarker: Marker
    private  var nearestambulance: Ambulance? = null

    private val ambulanceViewModel by viewModels<UserAmbulanceViewModel>()

    private var vn: String =""

    private var ac: Boolean = false
    private var ventilator: Boolean = false
    private var reqId: String = ""
    private lateinit var bottomSheetFragment2: BottomSheetFragment2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        arguments?.apply {
            ac = getBoolean("ac")
            ventilator = getBoolean("ventilator")
            reqId = getString("request", "")

        }
        bottomSheetFragment2 = BottomSheetFragment2()
        binding = FragmentTrackingfragmentBinding.inflate(inflater, container, false)


        // handling location
        prepLocationUpdates()
        mapsViewModel.startLocationUpdates()

        // getting live location nd setting it to map
        mapsViewModel.getLocationLiveData().observe(viewLifecycleOwner) { latLng ->
            e("loc", "$latLng")
            myMarker.position = latLng
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM))
            mapsViewModel.getambulancelist {
                ambulanceViewModel.getNearestAmbulance(
                    myMarker!!.position.latitude,
                    myMarker!!.position.longitude,
                    ac,
                    ventilator
                )
                myMarker.position.latitude
                myMarker.position.longitude
                ambulanceViewModel.getNearestAmbulanceLiveData().observe(viewLifecycleOwner) {it->
                    if(it is Success<Ambulance>) nearestambulance = it.data
                    else {
                        nearestambulance = null
                        e("no ambulance found" , " not found" )
                    }

                    Log.e("nearestaaa", nearestambulance.toString())
                }

                val myref =
                    FirebaseDatabase.getInstance().getReference("ambulance")
                nearestambulance?.vehicleNumber?.let {it->
                    val myref =
                        FirebaseDatabase.getInstance().getReference("ambulance")
                    myref.child(it).child("AssignedTo").setValue(reqId)
                    myref.child(it).child("busy").setValue(true)

                }
                nearestambulance?.vehicleNumber?.let {
                   vn = it
                }




//                map.clear()
                nearestambulance?.let { it -> mapsViewModel.markNearestAmbulance(map,it) }

                //     createMarker(myMarker!!.position.latitude,myMarker!!.position.longitude)

            }

        }

        // initialize map view
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root


    }

    private fun prepLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                activity as AppCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        ) requestLocationUpdates()
        else requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestLocationUpdates() {
        mapsViewModel.startLocationUpdates()
    }

    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationUpdates()
            } else {
                Toast.makeText(activity, "no gps", Toast.LENGTH_LONG).show()
            }
        }


    override fun onMapReady(p0: GoogleMap) {
        this.map = p0  // getting instance of map

        createMarker(0.0, 0.0)

        ambulanceViewModel.getAmbulancelist {
            ambulanceViewModel.getNearestAmbulance(
                myMarker!!.position.latitude,
                myMarker!!.position.longitude,
                ac,
                ventilator
            )

            val myref =
                FirebaseDatabase.getInstance().getReference("ambulance")

        }

        map.setOnMarkerClickListener(this)

    }

    private fun createMarker(lat: Double, lon: Double) {
        map?.let { googleMap ->
            googleMap.addMarker(MarkerOptions().position(LatLng(lat, lon)))?.let {
                this.myMarker = it
                it.tag = "userLocation"

            }
        }

    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val list = ambulanceViewModel.getNearestAmbulanceLiveData().value
        if(list is Failure<Ambulance>) return  false
        val bundle = Bundle()
        val ambu = (list as Success<Ambulance>).data
        bundle.putString("drivername", ambu.driverName)
        bundle.putString("drivernumber", ambu.driverNumber)
        bundle.putString("vehiclenumber", ambu.vehicleNumber)
        bottomSheetFragment2.arguments = bundle
        bottomSheetFragment2.show(childFragmentManager, bottomSheetFragment2.tag)
        return true
    }

}
