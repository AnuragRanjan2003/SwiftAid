package com.hackfest.swiftaid.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.repository.AmbulanceRepository

class AmbulanceViewModel(orgAuthID:String?):ViewModel() {

    private val repository:AmbulanceRepository
    private val _allAmbulances = MutableLiveData<List<Ambulance>>()
    val allAmbulances:LiveData<List<Ambulance>> = _allAmbulances

    init {
        repository = AmbulanceRepository(orgAuthID).getInstance()
        repository.loadAmbulances(_allAmbulances)
    }
}