package com.example.myvpnclient.data

import com.example.myvpnclient.domain.SessionData
import kotlinx.serialization.Serializable

@Serializable
data class NewSessionData(
    override val cipher: String,
    override val clientId: Int,
    override val key: String,
    override val firstNonce: Int,
    override val tunIp: String,
    override val port: Int,
): SessionData