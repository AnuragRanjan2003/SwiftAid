package com.hackfest.swiftaid.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.models.LocationLiveData
import com.hackfest.swiftaid.models.NearestAmbulanceData
import com.hackfest.swiftaid.models.OpenStreetResponse
import com.hackfest.swiftaid.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    application: Application,
    private val repo: Repository,
    val nearestAmbulanceData: NearestAmbulanceData
) :
    ViewModel() {
    private val locationLiveData = LocationLiveData(application)
    private var places = MutableLiveData<OpenStreetResponse>()

    fun getLocationLiveData() = locationLiveData

    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }

    fun getPlaces(latLng: LatLng) {
        val res = viewModelScope.async {
            repo.getPlaces(latLng).body()
        }
        viewModelScope.launch(Dispatchers.Main) {
            places.value = res.await()
        }


    }

    fun observePlaces(): MutableLiveData<OpenStreetResponse> {
        return places
    }

    fun getambulancelist(

        onResult: (ArrayList<Ambulance>) -> Unit,

    ) {
        nearestAmbulanceData.getambulancelist { list -> onResult(list) }
    }

    fun markNearestAmbulance(map: GoogleMap , amb: Ambulance){
        nearestAmbulanceData.marknearestambulance(amb,map)
    }


}