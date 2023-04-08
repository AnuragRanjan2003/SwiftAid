package com.hackfest.swiftaid.models

data class Ambulance(
    val orgAuthID:String? = null,
    var vehicleNumber:String? = null,
    var driverName:String? = null,
    var driverNumber:String? = null,
    var busy:Boolean? = null,
    var lattitude:String? = null,
    var lonbgitude:String? = null,
    var ventilator:Boolean? = null,
    var suctionUnit:Boolean? = null,
    var ecgMonitor:Boolean? = null

)

