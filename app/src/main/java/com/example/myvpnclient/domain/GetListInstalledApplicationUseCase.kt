package com.example.myvpnclient.domain

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import javax.inject.Inject

class GetListInstalledApplicationUseCase @Inject constructor(
    val repository: PreferenceRepository,
) {
    suspend operator fun invoke(context: Context): List<ApplicationInfo> {
        Log.d("VPNLog","я зашел в юскейс")
        return repository.getListInstalledApplication(context)
    }
}