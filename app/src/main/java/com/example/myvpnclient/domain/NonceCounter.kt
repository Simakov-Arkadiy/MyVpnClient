package com.example.myvpnclient.domain

class NonceCounter(private val prefix: Byte) {
    private var counter = 0L

    @Synchronized
    fun next(): ByteArray {
        counter++
        val nonce = ByteArray(12)
        nonce[0] = prefix
        for (i in 0 until 8) {
            nonce[11 - i] = (counter ushr (8 * i)).toByte()
        }
        return nonce
    }
}