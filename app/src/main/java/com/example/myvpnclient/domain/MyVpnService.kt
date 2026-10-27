package com.example.myvpnclient.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myvpnclient.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import okio.ByteString.Companion.decodeBase64
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.net.PortUnreachableException
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import javax.inject.Inject
import android.util.Base64

class MyVpnService() : VpnService() {
    @Inject
    lateinit var repository: VpnRepository
    private var tunnelJob: Job? = null

    @Volatile
    private var tun: ParcelFileDescriptor? = null

    @Volatile
    private var ch: DatagramChannel? = null

    @Volatile
    private var stopping = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        (application as MyApp).appComponent.inject(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("VPNLog", "Service запущен.")
        startForeground(1, buildNotification())

        when (intent?.action) {
            null -> activateVpn(intent)
            "STOP" -> inActivateVpn()
        }
        return START_NOT_STICKY
    }

    override fun onRevoke() {
        inActivateVpn()
        super.onRevoke()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun tunToUdp(
        tun: ParcelFileDescriptor,
        ch: DatagramChannel,
        codec: UdpEncryptedCodec,
        nonces: NonceCounter,
    ) = runInterruptible {
        val input = FileInputStream(tun.fileDescriptor)
        val buf = ByteArray(32767)
        while (true) {
            val n = input.read(buf)
            if (n < 0) break
            if (n == 0) continue
            val packet = codec.encode(nonces.next(), buf, n)
            try {
                ch.write(ByteBuffer.wrap(packet))
            } catch (e: PortUnreachableException) {
            }
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
            val n = try {
                ch.read(bb)
            } catch (e: PortUnreachableException) {
                continue
            }
            if (n <= 0) continue
            val plain = codec.decode(buf, n) ?: continue
            output.write(plain)
        }
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel("vpn", "VPN", NotificationManager.IMPORTANCE_LOW)
            )
        }
        return NotificationCompat.Builder(this, "vpn")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("VPN")
            .build()
    }

    private fun activateVpn(intent: Intent?) {
        val intent = intent
            ?: throw NullPointerException("Пустой Intent")

        val authCode = intent.getStringExtra("authCode")
        val serverHost = intent.getStringExtra("serverHost")
        val serverPort = intent.getIntExtra("serverPort", 0)
        Log.d("VPNLog", "$authCode $serverPort $serverHost")

        tunnelJob = scope.launch {

            val result = repository.shakeHands(authCode, serverHost, serverPort)

            if (result.isSuccess) {
                val channel = DatagramChannel.open()
                ch = channel
                protect(channel.socket())
                channel.connect(InetSocketAddress(serverHost, result.getOrThrow().port))

                val builder = Builder()
                val localTunnel = builder
                    .addAddress(result.getOrThrow().tunIp, 32)
                    .addRoute("0.0.0.0", 0)
                    .setBlocking(true)
                    .establish()
                if (localTunnel == null) {
                    throw NullPointerException("Не удалось создать tunAdapter.")
                }
                tun = localTunnel
                val cipher = UdpEncryptedCodec(
                    result.getOrThrow().cipher,
                    Base64.decode(result.getOrThrow().key, Base64.DEFAULT),
                    result.getOrThrow().clientId.toByte(),
                    result.getOrThrow().firstNonce.toByte(),
                )

                val nonce = NonceCounter(result.getOrThrow().firstNonce.toByte())

                coroutineScope {
                    launch { tunToUdp(localTunnel, channel, cipher, nonce) }
                    launch { udpToTun(channel, localTunnel, cipher) }
                }
            }
        }
    }

    private fun inActivateVpn() {
        stopping = true
        tunnelJob?.cancel()
        synchronized(this) {
            runCatching { tun?.close() }
            runCatching { ch?.close() }
            tun = null
            ch = null
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}