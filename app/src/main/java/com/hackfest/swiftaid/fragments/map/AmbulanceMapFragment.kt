package com.hackfest.swiftaid.fragments.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.SphericalUtil
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.constants.MAP_ZOOM
import com.hackfest.swiftaid.databinding.FragmentAmbulanceMapBinding
import com.hackfest.swiftaid.repository.Repository
import com.hackfest.swiftaid.viewModels.AmbulanceMapFragmentViewModel
import com.hackfest.swiftaid.viewModels.factory.AmbulanceFactory
import kotlin.math.floor


class AmbulanceMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentAmbulanceMapBinding
    private var map: GoogleMap? = null
    private var ambuMarker: Marker? = null
    private var userMarker: Marker? = null
    private var destMarker: Marker? = null
    private lateinit var repository: Repository
    private lateinit var factory: AmbulanceFactory
    private var ambuId: String = ""
    private lateinit var viewModel: AmbulanceMapFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("ambId")?.let {
            ambuId = it
            e("ambId", it)
        }


    }

    private fun getDistance(loc1: LatLng, loc2: LatLng): CharSequence? {
        if (loc1 == null) return "unknown"
        if (loc2 == null) return "unknown"
        return format(SphericalUtil.computeDistanceBetween(loc1, loc2))

    }

    private fun format(d: Double): CharSequence? {
        if (d < 1000) return "${floor(d)} m"
        val m = d / 1000
        return "${(floor((m * 100)) / 100)} km"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAmbulanceMapBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        repository = Repository()
        factory = AmbulanceFactory(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[AmbulanceMapFragmentViewModel::class.java]

        prepLocationUpdates()
        viewModel.startLocationLiveUpdates()
        viewModel.getLocationLiveData().observe(viewLifecycleOwner) {
            e("loc", "$it")
            ambuMarker?.position = it
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, MAP_ZOOM))
            FirebaseDatabase.getInstance().getReference("ambulance").child(ambuId).child("lattitude").setValue(it.latitude.toString())
            FirebaseDatabase.getInstance().getReference("ambulance").child(ambuId).child("lonbgitude").setValue(it.longitude.toString())
        }

        viewModel.getAssignedRequest(ambuId)
        viewModel.observeReqId().observe(viewLifecycleOwner) {
            e("reqId", "$it")
            if (!it.isNullOrBlank()) {
                viewModel.getRequest(it)
            } else {
                userMarker?.isVisible = false
                destMarker?.isVisible = false
            }
        }

        viewModel.observeRequest().observe(viewLifecycleOwner) {
            e("req", "$it")
            if (it == null) {
                Toast.makeText(requireContext(),"no reqquest",Toast.LENGTH_SHORT).show()
            }
            it?.apply {

                userMarker?.apply {
                    position = LatLng(it.latitude,it.longitude)
                    e("loc1","$position")
                    isVisible = true
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM))
                }
                destMarker?.apply {
                    position = LatLng(it.destinationLat,it.destinationLng)
                    e("loc2","$position")
                    isVisible = true
                }
            }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map3) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }


    private fun prepLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                activity as AppCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) requestLocationUpdates()
        else requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestLocationUpdates() {
        viewModel.startLocationLiveUpdates()
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
        this.map = p0
        createMarker()
    }

    private fun createMarker() {
        map?.let { googleMap ->
            googleMap.addMarker(MarkerOptions().position(LatLng(0.00, 0.00)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulancemarker)))?.let {
                this.ambuMarker = it
                userMarker = googleMap.addMarker(
                    MarkerOptions().position(LatLng(0.00, 0.00))
                        .icon(BitmapDescriptorFactory.defaultMarker()).visible(false)
                )
                destMarker = googleMap.addMarker(
                    MarkerOptions().position(LatLng(0.00, 0.00))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.hospitalmarker)).visible(false)
                )
            }

        }
    }


}