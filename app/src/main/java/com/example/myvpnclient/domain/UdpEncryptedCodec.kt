package com.example.myvpnclient.domain

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


class UdpEncryptedCodec(
    cipherName: String,
    keyBytes: ByteArray,
    private val clientId: Byte,
    private val ownPrefix: Byte,
) {
    companion object { const val NONCE = 12; const val TAG = 16; const val HEADER = 29 }

    private val chacha = cipherName == "ChaCha20Poly1305"
    private val key = SecretKeySpec(keyBytes, if (chacha) "ChaCha20" else "AES")
    private val transformation = if (chacha) "ChaCha20/Poly1305/NoPadding" else "AES/GCM/NoPadding"
    private val enc = Cipher.getInstance(transformation)
    private val dec = Cipher.getInstance(transformation)

    private fun params(nonce: ByteArray): AlgorithmParameterSpec =
        if (chacha) IvParameterSpec(nonce) else GCMParameterSpec(128, nonce)

    fun encode(nonce: ByteArray, plain: ByteArray, len: Int): ByteArray {
        enc.init(Cipher.ENCRYPT_MODE, key, params(nonce))
        val out = enc.doFinal(plain, 0, len)
        val ctLen = out.size - TAG
        return ByteBuffer.allocate(HEADER + ctLen)
            .put(nonce)
            .put(out, ctLen, TAG)
            .put(clientId)
            .put(out, 0, ctLen)
            .array()
    }

    fun decode(packet: ByteArray, len: Int): ByteArray? {
        if (len <= HEADER) return null
        if (packet[0] == ownPrefix) return null
        val nonce = packet.copyOfRange(0, NONCE)
        val ctLen = len - HEADER
        val input = ByteArray(ctLen + TAG)
        System.arraycopy(packet, HEADER, input, 0, ctLen)
        System.arraycopy(packet, NONCE, input, ctLen, TAG)
        return try {
            dec.init(Cipher.DECRYPT_MODE, key, params(nonce))
            dec.doFinal(input)
        } catch (e: GeneralSecurityException) {
            null
        }
    }
}
