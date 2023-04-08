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
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.constants.MAP_ZOOM
import com.hackfest.swiftaid.databinding.FragmentTrackingfragmentBinding
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.models.NearestAmbulanceData
import com.hackfest.swiftaid.repository.Repository
import com.hackfest.swiftaid.viewModels.AmbulanceViewModel
import com.hackfest.swiftaid.viewModels.MapsViewModel
import com.hackfest.swiftaid.viewModels.factory.MapViewModelFactory

class trackingfragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {
    private lateinit var binding: FragmentTrackingfragmentBinding
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var map: GoogleMap
    private lateinit var myMarker: Marker
    private lateinit var factory: MapViewModelFactory
    private lateinit var repository: Repository
    private lateinit var nearestambulance: Ambulance
    private lateinit var nearestAmbulanceData: NearestAmbulanceData
    private lateinit var ambulanceViewmodel: AmbulanceViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var ac: Boolean = false
    private var ventilator: Boolean = false
    private lateinit var bottomSheetFragment2: BottomSheetFragment2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        arguments?.let {
            ac = it.getBoolean("ac")
            ventilator = it.getBoolean("ventilator")
        }
        bottomSheetFragment2 = BottomSheetFragment2()
        binding = FragmentTrackingfragmentBinding.inflate(inflater, container, false)
        repository = Repository()

        factory = MapViewModelFactory(repository, requireActivity().application)

        database = Firebase.database
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        auth = Firebase.auth

        ambulanceViewmodel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[AmbulanceViewModel::class.java]
        // getting the list of ambulances


        nearestAmbulanceData = NearestAmbulanceData()


        // handling location
        prepLocationUpdates()
        mapsViewModel.startLocationUpdates()

        // getting live location nd setting it to map
        mapsViewModel.getLocationLiveData().observe(viewLifecycleOwner) {
            e("loc", "$it")
            myMarker.position = it
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, MAP_ZOOM))
            ambulanceViewmodel.getAmbulancelist {
                ambulanceViewmodel.getNearestAmbulance(
                    myMarker!!.position.latitude,
                    myMarker!!.position.longitude,
                    ac,
                    ventilator
                )
                myMarker.position.latitude
                myMarker.position.longitude
                nearestambulance = ambulanceViewmodel.getNearestAmbulanceLiveData().value!!
                Log.e("nearestaaa", nearestambulance.toString())
//                map.clear()
                nearestAmbulanceData.marknearestambulance(nearestambulance, map)
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

        ambulanceViewmodel.getAmbulancelist {
            ambulanceViewmodel.getNearestAmbulance(
                myMarker!!.position.latitude,
                myMarker!!.position.longitude,
                ac,
                ventilator
            )


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
        var list = ambulanceViewmodel.getNearestAmbulanceLiveData().value
        val bundle = Bundle()
        bundle.putString("drivername", list!!.driverName)
        bundle.putString("drivernumber", list!!.driverNumber)
        bundle.putString("vehiclenumber", list!!.vehicleNumber)
        bottomSheetFragment2.arguments = bundle
        bottomSheetFragment2.show(childFragmentManager, bottomSheetFragment2.tag)
        return true
    }

}
