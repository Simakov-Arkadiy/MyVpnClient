package com.example.myvpnclient.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPreferenceUseCase @Inject constructor(
    val repository: PreferenceRepository,
) {
    suspend operator fun invoke(): Flow<VpnPreference> {
        return repository.getPreference()
    }
}