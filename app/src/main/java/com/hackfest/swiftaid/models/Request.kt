package com.hackfest.swiftaid.models

data class Request(
    var requestID:String = "",
    val latitude: Double=0.00,
    val longitude: Double=0.00,
    val ventilation:Boolean=false,
    val ac : Boolean=false,
    val userID:String="",
    var destName:String = "",
    val destinationLat:Double=0.00,
    val destinationLng: Double=0.00,
    val date: String="",
    val acceptedBy:String=""
)
