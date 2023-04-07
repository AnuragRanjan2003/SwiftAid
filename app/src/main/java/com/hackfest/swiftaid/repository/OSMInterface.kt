package com.hackfest.swiftaid.repository

import com.hackfest.swiftaid.models.OpenStreetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OSMInterface {

    @GET("search")
    suspend fun getPlaces(
        @Query("q") q: String,
        @Query("format") format: String,
        @Query("bounded") bounded: String,
        @Query("viewbox") viewbox: String
    ): Response<OpenStreetResponse>

}
