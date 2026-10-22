package com.example.myvpnclient

import android.app.Application
import com.example.myvpnclient.presentation.DI.DaggerVpnComponent
import com.example.myvpnclient.presentation.DI.VpnComponent

class MyApp: Application() {
    lateinit var appComponent: VpnComponent
        private set
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerVpnComponent.factory().create(
            this
        )
    }
}