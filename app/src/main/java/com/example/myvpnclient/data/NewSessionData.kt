package com.example.myvpnclient.data

import com.example.myvpnclient.domain.SessionData
import kotlinx.serialization.Serializable

@Serializable
data class NewSessionData(
    override val cipher: String,
    override val clientId: Byte,
    override val key: ByteArray,
    override val firstNonce: Byte,
    override val tunIp: String,
    override val port: Int,
): SessionData