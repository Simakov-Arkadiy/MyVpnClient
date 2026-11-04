package com.example.myvpnclient.domain

interface VpnPreference {
    val host: String
    val port: String
    val authCode: String
    val checkBoxState: List<Boolean>
}