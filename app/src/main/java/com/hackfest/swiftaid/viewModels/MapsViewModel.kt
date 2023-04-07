package com.hackfest.swiftaid.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.hackfest.swiftaid.models.LocationLiveData
import com.hackfest.swiftaid.models.OpenStreetResponse
import com.hackfest.swiftaid.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MapsViewModel(application: Application, private val repo: Repository) :
    ViewModel() {
    private val locationLiveData = LocationLiveData(application)
    private var places= MutableLiveData<OpenStreetResponse>()

    fun getLocationLiveData() = locationLiveData

    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }

    fun getPlaces(latLng: LatLng) {
        val res = viewModelScope.async {
            repo.getPlaces(latLng).body()
        }
        viewModelScope.launch(Dispatchers.Main) {
            places.value=  res.await()
        }


    }

    fun observePlaces(): MutableLiveData<OpenStreetResponse> {
        return places
    }


}