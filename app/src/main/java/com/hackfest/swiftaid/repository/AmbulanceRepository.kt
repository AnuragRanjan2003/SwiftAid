package com.hackfest.swiftaid.repository

import androidx.lifecycle.MutableLiveData
import com.hackfest.swiftaid.models.Ambulance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AmbulanceRepository(orgAuthID:String?) {

    private val orgAuthID:String? = orgAuthID
    private val dbRef by lazy{
        FirebaseDatabase.getInstance("https://swiftaid-46a45-default-rtdb.firebaseio.com/").getReference("ambulance")
    }

    @Volatile
    private var INSTANCE:AmbulanceRepository? = null

    fun getInstance():AmbulanceRepository{
        return INSTANCE?: synchronized(this){
            val instance = AmbulanceRepository(orgAuthID)
            INSTANCE = instance
            instance
        }
    }

    fun loadAmbulances(ambulanceList:MutableLiveData<List<Ambulance>>){
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _ambulanceList:List<Ambulance> = snapshot.children.map { dataSnapshot ->

                        dataSnapshot.getValue(Ambulance::class.java)!!

                    }
                    ambulanceList.postValue(_ambulanceList)

                }catch (e : Exception){

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}