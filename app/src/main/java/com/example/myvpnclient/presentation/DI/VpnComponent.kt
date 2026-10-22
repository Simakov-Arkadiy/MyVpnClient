package com.example.myvpnclient.presentation.DI

import android.app.Application
import com.example.myvpnclient.domain.MyVpnService
import com.example.myvpnclient.presentation.MainScreenViewModelFactory
import com.example.myvpnclient.presentation.MyVpnClientActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [VpnModule::class])
internal interface VpnComponent {
    fun inject(activity: MyVpnClientActivity)
    fun inject(service: MyVpnService)

    fun mainScreenViewModelFactory(): MainScreenViewModelFactory
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
        ): VpnComponent
    }
}