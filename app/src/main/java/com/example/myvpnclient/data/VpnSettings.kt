package com.example.myvpnclient.data

data class VpnSettings(val host: String, val port: String, val authCode: String, val checkBoxState: Set<String>) {
}