package com.example.myvpnclient.domain

interface SessionData {
    val cipher: String
    val clientId: Int
    val key: String
    val firstNonce: Int
    val tunIp: String
    val port: Int
}