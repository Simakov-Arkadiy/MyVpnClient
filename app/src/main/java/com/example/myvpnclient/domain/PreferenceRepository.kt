package com.example.myvpnclient.domain

import android.content.Context
import android.content.pm.ApplicationInfo
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    suspend fun getListInstalledApplication(context: Context): List<ApplicationInfo>
    suspend fun <T:Any>savePreference(key:String,value:T)
    suspend fun getPreference(): Flow<VpnPreference>
}