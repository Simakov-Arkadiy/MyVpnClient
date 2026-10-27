package com.example.myvpnclient.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface VpnApi {
    @POST
    suspend fun shakeHands(
        @Url url:String,
        @Body algorithms: List<String>,
    ): Response<NewSessionData>
}