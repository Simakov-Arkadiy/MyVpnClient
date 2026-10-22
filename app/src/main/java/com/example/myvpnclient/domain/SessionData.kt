package com.example.myvpnclient.domain

interface SessionData {
    val cipher: String
    val clientId: Byte
    val key: ByteArray
    val firstNonce: Byte
    val tunIp: String
    val port: Int
}