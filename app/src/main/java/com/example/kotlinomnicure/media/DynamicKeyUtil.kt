package com.example.kotlinomnicure.media

import java.lang.StringBuilder
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class DynamicKeyUtil {

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun encodeHMAC(key: String, message: ByteArray?): ByteArray? {
        return encodeHMAC(key.toByteArray(), message)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun encodeHMAC(key: ByteArray?, message: ByteArray?): ByteArray? {
        val keySpec = SecretKeySpec(key, "HmacSHA1")
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(keySpec)
        return mac.doFinal(message)
    }

    fun bytesToHex(`in`: ByteArray): String? {
        val builder = StringBuilder()
        for (b in `in`) {
            builder.append(String.format("%02x", b))
        }
        return builder.toString()
    }
}
