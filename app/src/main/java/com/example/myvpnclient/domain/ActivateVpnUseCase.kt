package com.example.myvpnclient.domain

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import javax.inject.Inject

class ActivateVpnUseCase @Inject constructor() {
    operator fun invoke(
        authCode: String,
        serverHost: String,
        serverPort: Int,
        application: Application,
        allowedPackages:ArrayList<String>
    ) {
        val intent =
            Intent(application, MyVpnService::class.java).apply {
                putExtra("authCode", authCode)
                putExtra("serverHost", serverHost)
                putExtra("serverPort", serverPort)
                putExtra("allowedPackages", allowedPackages.toTypedArray())
            }
        ContextCompat.startForegroundService(application, intent)
        Log.d("VPNLog",allowedPackages.toTypedArray()[0])
    }
}