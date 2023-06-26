package com.hackfest.swiftaid.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.models.NearestAmbulanceData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hackfest.swiftaid.repository.AmbulanceRepository


class AmbulanceViewModel(orgAuthID:String?): ViewModel() {

    private val repository: AmbulanceRepository
    private val _allAmbulances = MutableLiveData<List<Ambulance>>()
    val allAmbulances: LiveData<List<Ambulance>> = _allAmbulances

    init {
        repository = AmbulanceRepository(orgAuthID).getInstance()
        repository.loadAmbulances(_allAmbulances)
    }
}



