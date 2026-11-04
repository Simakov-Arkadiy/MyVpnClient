package com.example.myvpnclient.data

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myvpnclient.dataStore
import com.example.myvpnclient.domain.PreferenceRepository
import com.example.myvpnclient.domain.VpnPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(val application: Application) :
    PreferenceRepository {

    var keys: MutableList<Preferences.Key<Boolean>> = mutableListOf()
    var host = stringPreferencesKey("server_host")
    var port = stringPreferencesKey("server_port")
    var authCode = stringPreferencesKey("auth_code")


    override suspend fun getListInstalledApplication(context: Context): List<ApplicationInfo> {
        Log.d("VPNLog", "я зашел сюда")
        val pm = context.packageManager
        val a = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            .filter { pkg ->
                pkg.requestedPermissions?.contains(Manifest.permission.INTERNET) == true
            }
            .filter { pkg ->
                pm.getLaunchIntentForPackage(pkg.packageName) != null
            }
            .filter { pkg ->
                pkg.packageName != context.packageName
            }
            .mapNotNull { pkg ->
                pkg.applicationInfo
            }
            .onEach { info ->
                keys.add(booleanPreferencesKey(info.packageName))
            }


        return a
    }

    override suspend fun getPreference(): Flow<VpnPreference> {

        return application.dataStore.data.map { pref ->
            Log.d("VPNLog", pref[host] ?: "ничего нет")
            VpnPreferenceImpl(
                pref[host] ?: "",
                pref[port] ?: "",
                pref[authCode] ?: "",
                keys.map { pref[it] ?: false })
        }
    }

    override suspend fun <T : Any> savePreference(key: String, value: T) {
        when (value) {
            is String -> {
                application.dataStore.edit { it[stringPreferencesKey(key)] = value }
                Log.d("VPNLog", value)
            }

            is Boolean -> {
                application.dataStore.edit {
                    it[booleanPreferencesKey(key)] = value
                }
                Log.d("VPNLog", value.toString())
            }

            else -> {
                Log.d("VPNLog", "значение не строка и не булево")
            }
        }
    }
}
