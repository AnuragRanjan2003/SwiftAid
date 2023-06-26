package com.hackfest.swiftaid.models

import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.SphericalUtil
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.constants.MAP_ZOOM
import java.lang.IndexOutOfBoundsException

class NearestAmbulanceData(val auth: FirebaseAuth, val database: FirebaseDatabase) {

    fun getAmbulanceList(
        userLocationMarker: Marker,
        map: GoogleMap,
        onResult: (ArrayList<Ambulance>) -> Unit
    ) {
        val myRef =
            FirebaseDatabase.getInstance("https://swiftaid-hackfest-default-rtdb.firebaseio.com/")
                .getReference("ambulance")
        myRef.addValueEventListener(object : ValueEventListener {
            private val ambulanceMarkers = mutableListOf<Marker>()

            override fun onDataChange(snapshot: DataSnapshot) {
                val ambulanceList = ArrayList<Ambulance>()
                if (snapshot.exists()) {
                    for (datasnapshot in snapshot.children) {
                        if ((!ambulanceList.contains(datasnapshot.getValue(Ambulance::class.java)))) {
                            val ambulance = datasnapshot.getValue(Ambulance::class.java)
                            if (!ambulance!!.busy!!) {
                                ambulance?.let {
                                    ambulanceList.add(it)
                                }
                            }
                        }
                    }
                }
                onResult(ambulanceList)

                // Remove previous markers that are not the user location marker
                ambulanceMarkers.filterNot { it.tag == "userLocation" }
                    .forEach { it.remove() }
                ambulanceMarkers.retainAll { it.tag == "userLocation" }

                // Add markers for each ambulance
                for (ambulance in ambulanceList) {
                    val markerColor = BitmapDescriptorFactory.HUE_GREEN
                    val markerOptions = MarkerOptions()
                        .position(
                            LatLng(
                                ambulance.lattitude!!.toDouble(),
                                ambulance.lonbgitude!!.toDouble()
                            )
                        )
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulancemarker))
                        .title(ambulance.driverName)
                    val marker = map.addMarker(markerOptions)
                    ambulanceMarkers.add(marker!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(ArrayList())
                Log.e("getAmbulanceList", error.toString())
            }
        })
    }


    fun getambulancelist(onResult: (ArrayList<Ambulance>) -> Unit) {
        database
            .getReference("ambulance")
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val ambulanceList = ArrayList<Ambulance>()
//                if (snapshot.exists()) {
//                    for (datasnapshot in snapshot.children) {
//                        if ((!ambulanceList.contains(datasnapshot.getValue(Ambulance::class.java)))) {
//                            val ambulance = datasnapshot.getValue(Ambulance::class.java)
//                            if (!ambulance!!.busy!!) {
//                                ambulance.let {
//                                    ambulanceList.add(it)
//                                }
//                            }
//                        }
//                    }
//                }
//                onResult(ambulanceList)
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                onResult(ArrayList())
//                Log.e("getAmbulanceList", error.toString())
//            }
//        })
            .get().addOnSuccessListener { snapshot ->
                val ambulanceList = ArrayList<Ambulance>()
                if (snapshot.exists()) {
                    for (datasnapshot in snapshot.children) {
                        if ((!ambulanceList.contains(datasnapshot.getValue(Ambulance::class.java)))) {
                            val ambulance = datasnapshot.getValue(Ambulance::class.java)
                            if (!ambulance!!.busy!!) {
                                ambulance.let {
                                    ambulanceList.add(it)
                                }
                            }
                        }
                    }
                }
                onResult(ambulanceList)


            }

    }


    fun getNearestAmbulance(
        lat: Double,
        lon: Double,
        ac: Boolean,
        ventilator: Boolean,
        ambulanceList: ArrayList<Ambulance>,
        onResult: (Resource<Ambulance>) -> Unit

    ) {
        val lat = lat
        var nearambulance : Resource<Ambulance> = Failure<Ambulance>("not found")
        val lon = lon

        val userLocation = LatLng(lat.toDouble(), lon.toDouble())

        var distance = 100000000000.000
        for (a in ambulanceList) {
            if (a.ecgMonitor == ac && a.suctionUnit == ventilator) {
                if (distance > SphericalUtil.computeDistanceBetween(
                        userLocation,
                        LatLng(a.lattitude!!.toDouble(), a.lattitude!!.toDouble())
                    )
                ) {
                    distance = SphericalUtil.computeDistanceBetween(
                        userLocation,
                        LatLng(a.lattitude!!.toDouble(), a.lattitude!!.toDouble())
                    )
                    nearambulance = Success(a)
                }
            }
        }

//        val distanceComparator = Comparator<Ambulance> { ambulance1, ambulance2 ->
//            val location1 =
//                LatLng(ambulance1.lattitude.toDouble(), ambulance1.lonbgitude.toDouble())
//            val location2 =
//                LatLng(ambulance2.lattitude.toDouble(), ambulance2.lonbgitude.toDouble())
//            val distance1 = SphericalUtil.computeDistanceBetween(userLocation, location1)
//            val distance2 = SphericalUtil.computeDistanceBetween(userLocation, location2)
//            distance1.compareTo(distance2)
//        }
//        ambulanceList.sortWith(distanceComparator)
//        val nearestAmbulance = ambulanceList.firstOrNull()
        onResult(nearambulance)
    }

    fun marknearestambulance(nearambulance: Ambulance, map: GoogleMap) {
        database
            .getReference("ambulance")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e("vehiclenumber22", nearambulance.lonbgitude.toString())

                    if (snapshot.exists()) {
                        for (datasnapshot in snapshot.children) {
                            val ambulance = datasnapshot.getValue(Ambulance::class.java)
                            if (ambulance!!.vehicleNumber == nearambulance.vehicleNumber) {
                                val markerColor = BitmapDescriptorFactory.HUE_AZURE
                                val markerOptions = MarkerOptions()
                                    .position(
                                        LatLng(
                                            ambulance.lattitude!!.toDouble(),
                                            ambulance.lonbgitude!!.toDouble()
                                        )
                                    )
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulancemarker))
                                    .title(ambulance.driverName)
                                map.addMarker(markerOptions)Anurag
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    LatLng(ambulance.lattitude!!.toDouble(),ambulance.lonbgitude!!.toDouble()),
                                    MAP_ZOOM
                               ))
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("getAmbulanceList", error.toString())
                }
            })

    }

}

