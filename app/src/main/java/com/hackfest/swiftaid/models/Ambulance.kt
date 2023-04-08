package com.hackfest.swiftaid.models

data class Ambulance(
    val vehicleNumber: String = "",
    val driverNumber: String = "",
    val lonbgitude: String = "0.0",
    val lattitude: String = "0.0",
    val driverName: String = "",
    val busy: Boolean = false,
    val ecgMonitor: Boolean = false,
    val suctionUnit: Boolean = false,
    val ventilator: Boolean = false,
    val orgAuthID: String = ""
)

