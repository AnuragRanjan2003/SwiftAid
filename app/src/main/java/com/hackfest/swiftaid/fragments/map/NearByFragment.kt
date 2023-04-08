package com.hackfest.swiftaid.fragments.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.constants.MAP_ZOOM
import com.hackfest.swiftaid.databinding.FragmentNearByBinding
import com.hackfest.swiftaid.models.OpenStreetResponseItem
import com.hackfest.swiftaid.repository.Repository
import com.hackfest.swiftaid.viewModels.MapsViewModel
import com.hackfest.swiftaid.viewModels.factory.MapViewModelFactory


class NearByFragment : Fragment(),OnMapReadyCallback {
    private lateinit var binding: FragmentNearByBinding
    private lateinit var mapsViewModel: MapsViewModel
    private var map: GoogleMap? = null
    private var myMarker: Marker? = null
    private lateinit var repository: Repository
    private lateinit var factory: MapViewModelFactory
    private lateinit var bottomSheetFragment: BottomSheetFragment
    private val namelist = ArrayList<String>()
    private val placelist = ArrayList<OpenStreetResponseItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNearByBinding.inflate(inflater, container, false)
        repository = Repository()
        factory = MapViewModelFactory(repository, requireActivity().application)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]
        bottomSheetFragment = BottomSheetFragment()

        // handling location
        prepLocationUpdates()
        mapsViewModel.startLocationUpdates()


        createMarker()


        binding.search.setAdapter(
            ArrayAdapter<String>(
                requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                namelist
            )
        )


        binding.search.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val pos = namelist.indexOf(binding.search.text.toString())
                Log.e("pos", "$pos")
                val bundlebtm = Bundle()
                bundlebtm.putString("place", namelist[pos])
                bundlebtm.putDoubleArray(
                    "loc",
                    doubleArrayOf(placelist[pos].lat.toDouble(), placelist[pos].lon.toDouble())
                )
                myMarker?.position?.let {
                    bundlebtm.putDoubleArray("my_loc", doubleArrayOf(it.latitude, it.longitude))
                }
               bottomSheetFragment.arguments = bundlebtm
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)

            }

        }


        // getting live location nd setting it to map
        mapsViewModel.getLocationLiveData().observe(viewLifecycleOwner) {
            Log.e("loc", "$it")
            myMarker?.position = it
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, MAP_ZOOM))
            mapsViewModel.getPlaces(LatLng(it.latitude, it.longitude))
        }

        mapsViewModel.observePlaces().observe(viewLifecycleOwner) {
            // e("places", "$it")
            namelist.clear()
            placelist.clear()
            it?.apply {
                for (place in it) {
                    placelist.add(place)
                    val s = place.display_name.split(",")
                    namelist.add(s[0])
                }
            }
            Log.e("places", "$namelist")
            map?.apply {
                placeMarkers(it, this)
            }

        }


        // initialize map view
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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
        createMarker()


    }

    private fun createMarker() {
        map?.let { googleMap ->
            googleMap.addMarker(MarkerOptions().position(LatLng(0.00, 0.00)))?.let {
                this.myMarker = it
            }
        }
    }

    private fun placeMarkers(
        openStreetResponse: ArrayList<OpenStreetResponseItem>,
        map: GoogleMap
    ) {
        for (place in openStreetResponse) {
            val marker = map.addMarker(
                MarkerOptions().position(
                    LatLng(
                        place.lat.toDouble(),
                        place.lon.toDouble()
                    )
                ).title(place.display_name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
        }
    }

    }


