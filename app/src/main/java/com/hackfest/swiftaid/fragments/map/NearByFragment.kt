package com.hackfest.swiftaid.fragments.authentication.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.databinding.FragmentNearByBinding
import com.hackfest.swiftaid.models.OpenStreetResponseItem
import com.hackfest.swiftaid.repository.Repository
import com.hackfest.swiftaid.viewModels.MapsViewModel
import com.hackfest.swiftaid.viewModels.factory.MapViewModelFactory


class NearByFragment : Fragment() {
    private lateinit var binding: FragmentNearByBinding
    private lateinit var mapsViewModel: MapsViewModel
    private var map: GoogleMap? = null
    private var myMarker: Marker? = null
    private lateinit var repository: Repository
    private lateinit var factory: MapViewModelFactory
    //private lateinit var bottomSheetFragment: BottomSheetFragment
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
        return inflater.inflate(R.layout.fragment_near_by, container, false)
    }


}