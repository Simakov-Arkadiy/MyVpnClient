package com.example.myvpnclient.domain

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import javax.inject.Inject

class ActivateVpnUseCase @Inject constructor() {
    operator fun invoke(
        authCode: String,
        serverHost: String,
        serverPort: Int,
        application: Application
    ) {
        val intent =
            Intent(application, MyVpnService::class.java).apply {
                putExtra("authCode", authCode)
                putExtra("serverHost", serverHost)
                putExtra("serverPort", serverPort)
            }
        ContextCompat.startForegroundService(application, intent)
    }
}