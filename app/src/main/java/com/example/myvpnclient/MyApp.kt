package com.example.myvpnclient

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
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
val Context.dataStore by preferencesDataStore(name = "vpn_settings")