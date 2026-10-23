package com.example.myvpnclient.domain

import android.app.Application
import android.content.Intent
import javax.inject.Inject

class InactivateVpnUseCase @Inject constructor(){
    operator fun invoke(
        application: Application
    ) {
        val intent = Intent(application, MyVpnService::class.java).apply {
            action = "STOP"
        }
        application.startService(intent)
    }
}