package com.example.myvpnclient.domain

import javax.inject.Inject

class SavePreferenceUseCase @Inject constructor(
    val repository: PreferenceRepository,
){
    suspend operator fun <T:Any>invoke(key:String, value:T) {
        repository.savePreference(key,value)
    }
}