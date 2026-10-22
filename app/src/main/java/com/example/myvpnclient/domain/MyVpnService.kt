package com.example.myvpnclient.domain

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.example.myvpnclient.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import javax.inject.Inject

class MyVpnService @Inject constructor(val repository: VpnRepository) : VpnService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        (application as MyApp).appComponent.inject(this)

        val intent = intent
            ?: throw NullPointerException("Пустой Intent")

        val authCode = intent.getStringExtra("authCode")
        val serverHost = intent.getStringExtra("serverHost")
        val serverPort = intent.getIntExtra("serverPort", 0)

        scope.launch {
            VpnService.prepare((application as MyApp))

            val result = repository.shakeHands(authCode, serverHost, serverPort)
            if (result.isSuccess) {
                val channel = DatagramChannel.open()
                protect(channel.socket())
                channel.connect(InetSocketAddress(serverHost, result.getOrThrow().port ))

                val builder = Builder()
                val localTunnel = builder
                    .addAddress(result.getOrThrow().tunIp, 32)
                    .addRoute("0.0.0.0", 0)
                    .setBlocking(true)
                    .establish()
                if (localTunnel == null){
                    throw NullPointerException("Не удалось создать tunAdapter.")
                }
                val input = FileInputStream(localTunnel.fileDescriptor)
                val buffer = ByteArray(32767)
                val cipher = UdpEncryptedCodec(result.getOrThrow().key, result.getOrThrow().clientId)

                val nonce = NonceCounter(result.getOrThrow().firstNonce)

                coroutineScope {
                    launch { tunToUdp(localTunnel, channel, cipher, nonce.next(result.getOrThrow().firstNonce).toByte() }
                    launch { udpToTun(channel, localTunnel, cipher) }
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun tunToUdp(
        tun: ParcelFileDescriptor,
        ch: DatagramChannel,
        codec: UdpEncryptedCodec,
        nonces: Byte,
    ) = runInterruptible {
        val input = FileInputStream(tun.fileDescriptor)
        val buf = ByteArray(32767)
        while (true) {
            val n = input.read(buf)
            if (n < 0) break
            if (n == 0) continue
            val packet = codec.encode(nonces, buf, n)
            ch.write(ByteBuffer.wrap(packet))
        }
    }

    private suspend fun udpToTun(
        ch: DatagramChannel,
        tun: ParcelFileDescriptor,
        codec: UdpEncryptedCodec,
    ) = runInterruptible {
        val output = FileOutputStream(tun.fileDescriptor)
        val buf = ByteArray(32767)
        val bb = ByteBuffer.wrap(buf)
        while (true) {
            bb.clear()
            val n = ch.read(bb)
            val plain = codec.decode(buf, n) ?: continue
            output.write(plain)
        }
    }
}