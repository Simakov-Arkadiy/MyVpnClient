package com.example.myvpnclient.data

import android.util.Log
import com.example.myvpnclient.domain.VpnRepository
import javax.inject.Inject

class VpnRepositoryImpl @Inject constructor(val vpnApi: VpnApi) : VpnRepository {
    override suspend fun shakeHands(
        authCode: String?,
        serverHost: String?,
        serverPort: Int?,
    ): Result<NewSessionData> {
        Log.d("VPNLog", "shakeHands вызван!")
        return runCatching {
            val response = vpnApi.shakeHands("http://${serverHost}:${serverPort.toString()}/session/${authCode}", listOf("ChaCha20Poly1305", "AesGcm"))

            Log.d("VPNLog", "${response.code()}")

            val body = response.body()
                ?: throw IllegalStateException(
                    "Empty body in shakeHands response, code=${response.code()}"
                )

            val newSessionData = NewSessionData(
                body.cipher,
                body.clientId,
                body.key,
                body.firstNonce,
                body.tunIp,
                body.port,
            )
            newSessionData
        }.onFailure { e ->
            Log.d("VPNLog", "shakeHands не удался", e)
        }
    }
}