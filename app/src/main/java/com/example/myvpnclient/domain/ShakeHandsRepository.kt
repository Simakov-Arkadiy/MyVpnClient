package com.example.myvpnclient.domain


interface ShakeHandsRepository {
    suspend fun shakeHands(
        authCode: String?,
        serverHost: String?,
        serverPort: Int?
    ): Result<SessionData>
}