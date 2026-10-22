package com.example.myvpnclient.domain


interface VpnRepository {
    suspend fun shakeHands(authCode: String?, serverHost: String?, serverPort: Int?): Result<SessionData>
}