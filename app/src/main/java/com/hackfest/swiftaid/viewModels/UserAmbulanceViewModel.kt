package com.hackfest.swiftaid.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.models.NearestAmbulanceData
import com.hackfest.swiftaid.models.Resource
import com.hackfest.swiftaid.models.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class UserAmbulanceViewModel @Inject constructor(val auth: FirebaseAuth,val nearestAmbulanceData : NearestAmbulanceData ) : ViewModel() {

    private val ambulancelist = MutableLiveData<ArrayList<Ambulance>>()
    private val ambulanceList = MutableLiveData<ArrayList<Ambulance>>()
    private val nearestAmbulance = MutableLiveData<Resource<Ambulance>>()

    fun getAmbulanceList(
        map: GoogleMap,
        marker: Marker
    ): MutableLiveData<ArrayList<Ambulance>> {
        nearestAmbulanceData.getAmbulanceList(marker, map) { list ->
            ambulanceList.value = list
        }
        return ambulanceList


    }

    fun getAmbulancelist(onComplete: () -> Unit) {
        nearestAmbulanceData.getambulancelist() { list ->
            ambulancelist.value = list
            Log.e("ambulancelist12333", ambulancelist.value.toString())
            if (list.isNotEmpty()) {
                onComplete()
            }

        }


    }

    fun getNearestAmbulance(lat: Double, lon: Double, ac: Boolean, ventilator: Boolean) {
        ambulancelist.value?.let { list ->
            nearestAmbulanceData.getNearestAmbulance(
                lat,
                lon,
                ac,
                ventilator,
                ambulancelist.value!!
            ) { res ->
                nearestAmbulance.value = res
            }
        }
    }

    fun marknearestambulance(ambulance: Ambulance, map: GoogleMap) {

        nearestAmbulanceData.marknearestambulance(ambulance, map)

    }

    fun getAmbulanceListLiveData(): LiveData<ArrayList<Ambulance>> {
        if (ambulanceList == null) {
            Log.e("null list", "null list")
        }
        return ambulanceList
    }

    fun getNearestAmbulanceLiveData(): LiveData<Resource<Ambulance>>  =  nearestAmbulance


}
