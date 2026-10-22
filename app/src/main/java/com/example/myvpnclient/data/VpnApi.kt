package com.example.myvpnclient.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface VpnApi {
    @POST("session/{authCode}")
    suspend fun shakeHands(
        @Path("authCode") authCode: String?,
        @Body algorithms: List<String>,
    ): Response<NewSessionData>
}