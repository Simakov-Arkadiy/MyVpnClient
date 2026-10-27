package com.example.myvpnclient

import android.app.Application
import com.example.myvpnclient.presentation.di.DaggerVpnComponent
import com.example.myvpnclient.presentation.di.VpnComponent

class MyApp : Application() {
    internal lateinit var appComponent: VpnComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerVpnComponent.factory().create(
            this
        )
    }
}