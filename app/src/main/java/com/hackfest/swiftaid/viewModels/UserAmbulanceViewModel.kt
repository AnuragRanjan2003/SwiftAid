package com.hackfest.swiftaid.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.models.NearestAmbulanceData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class UserAmbulanceViewModel(application: Application) : AndroidViewModel(application) {

    private val nearestAmbulanceData = NearestAmbulanceData()
    private val ambulancelist = MutableLiveData<ArrayList<Ambulance>>()
    private val ambulanceList = MutableLiveData<ArrayList<Ambulance>>()
    private val nearestAmbulance = MutableLiveData<Ambulance>()

    fun getAmbulanceList(auth: FirebaseAuth,map:GoogleMap,marker: Marker): MutableLiveData<ArrayList<Ambulance>> {
        nearestAmbulanceData.getAmbulanceList(auth,marker,map) { list ->
            ambulanceList.value = list

        }
        return ambulanceList


    }
    fun getAmbulancelist(onComplete:()-> Unit) {
        nearestAmbulanceData.getambulancelist() { list ->
            ambulancelist.value = list
            Log.e("ambulancelist12333",ambulancelist.value.toString())
            if(list.isNotEmpty()){
                onComplete()
            }

        }



    }
    fun getNearestAmbulance(lat:Double,lon:Double,ac:Boolean,ventilator:Boolean) {
        ambulancelist.value?.let { list ->
            nearestAmbulanceData.getNearestAmbulance(lat,lon,ac,ventilator, ambulancelist.value!!) { nearestambulance ->
                nearestAmbulance.value = nearestambulance
            }
        }
    }
    fun marknearestambulance(ambulance: Ambulance,map: GoogleMap){

        nearestAmbulanceData.marknearestambulance(ambulance,map)

    }

    fun getAmbulanceListLiveData(): MutableLiveData<ArrayList<Ambulance>> {
        if (ambulanceList==null){
            Log.e("null list","null list")
        }
        return ambulanceList
    }

    fun getNearestAmbulanceLiveData(): MutableLiveData<Ambulance> {
        return nearestAmbulance
    }

}
