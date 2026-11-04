package com.example.myvpnclient.data

import com.example.myvpnclient.domain.VpnPreference

data class VpnPreferenceImpl(
    override val host: String,
    override val port: String,
    override val authCode: String,
    override val checkBoxState: List<Boolean>,
) : VpnPreference {
}