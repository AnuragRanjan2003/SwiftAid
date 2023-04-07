package com.hackfest.swiftaid.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OSMApi {
    val instance: OSMInterface by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(OSMInterface::class.java)
    }

}
