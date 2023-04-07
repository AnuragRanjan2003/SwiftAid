package com.hackfest.swiftaid.models

data class OpenStreetResponseItem(
    val boundingbox: List<String>,
    val `class`: String,
    val display_name: String,
    val icon: String,
    val importance: Double,
    val lat: String,
    val licence: String,
    val lon: String,
    val osm_id: String,
    val osm_type: String,
    val place_id: String,
    val type: String
)


