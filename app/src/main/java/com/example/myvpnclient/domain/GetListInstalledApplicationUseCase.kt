package com.example.myvpnclient.domain

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import javax.inject.Inject

class GetListInstalledApplicationUseCase @Inject constructor(
    val repository: PreferenceRepository,
) {
    suspend operator fun invoke(context: Context): List<InstalledApp> {
        val pm = context.packageManager
        return repository.getListInstalledApplication(context).mapNotNull { info ->
            InstalledApp(
                info.packageName,
                info.loadLabel(pm).toString(),
                info.loadIcon(pm).toBitmap().asImageBitmap()
            )
        }
    }
}