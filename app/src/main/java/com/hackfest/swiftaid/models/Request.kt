package com.hackfest.swiftaid.models

data class Request(
    var requestID:String = "",
    val latitude: Double=0.00,
    val longitude: Double=0.00,
    val ventilator:Boolean=false,
    val suctionUnit: Boolean=false,
    val ecgMonitor: Boolean=false,
    val userID:String="",
    var destName:String = "",
    val destinationLat:Double=0.00,
    val destinationLng: Double=0.00,
    val date: String="",
    val acceptedBy:String=""
)
