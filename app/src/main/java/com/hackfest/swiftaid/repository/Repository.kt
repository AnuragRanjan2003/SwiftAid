package com.hackfest.swiftaid.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.models.OpenStreetResponse
import com.hackfest.swiftaid.models.Request
import retrofit2.Response
import java.util.*
import kotlin.math.floor

class Repository {
    private val auth = Firebase.auth
    private val dRequestRef = Firebase.database.getReference("requests")
    suspend fun getPlaces(latLng: LatLng): Response<OpenStreetResponse> {
        val vb =
            "${latLng.longitude - 0.3},${latLng.latitude - 0.3},${latLng.longitude + 0.3},${latLng.latitude + 0.3}"
        return OSMApi.instance.getPlaces("hospital", "json", "1", vb)
    }

    fun postRequest(request: Request, context: Context, onComplete: () -> Unit) {
        val requestId = UUID.randomUUID().toString()
        request.requestID = requestId
        FirebaseDatabase.getInstance(context.getString(R.string.firebaseUrl))
            .getReference("request").child(requestId).setValue(request)
            .addOnSuccessListener {
                Log.e("request", "done")
                onComplete()
            }.addOnFailureListener {
                Log.e("error", it.message.toString())
            }

    }

    fun getRequestList(
        ambLoc: LatLng,
        context: Context,
        comprator: (Request, LatLng) -> Boolean,
        onComplete: (ArrayList<Request>) -> Unit
    ) {
        FirebaseDatabase.getInstance(context.getString(R.string.firebaseUrl))
            .getReference("request").child(toNode(ambLoc.latitude)).child(toNode(ambLoc.longitude))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<Request>()
                    for (snap in snapshot.children) {
                        snap.getValue(Request::class.java)?.let { it1 -> list.add(it1) }
                    }
                    onComplete(list)

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("fail", error.message.toString())
                }
            })


    }

    fun getAssignedRequest(ambId: String,onComplete:(String?)->Unit) {
        val ref = FirebaseDatabase.getInstance("https://swiftaid-hackfest-default-rtdb.firebaseio.com/")
            .getReference("ambulance").child(ambId).child("AssignedTo")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reqId = snapshot.getValue(String::class.java)
                    onComplete(reqId)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("get req id", error.message.toString())
                }
            })
    }

    fun getSingleRequest(reqId : String,onComplete: (Request?) -> Unit){
        val ref = FirebaseDatabase.getInstance("https://swiftaid-hackfest-default-rtdb.firebaseio.com/")
            .getReference("request").child(reqId).get().addOnSuccessListener {
                val request = it.getValue(Request::class.java)
                onComplete(request)
            }.addOnFailureListener {
                Log.e("req", it.message.toString())
            }
    }

    private fun toNode(d: Double): String {
        return floor(d).toInt().toString()
    }

}
