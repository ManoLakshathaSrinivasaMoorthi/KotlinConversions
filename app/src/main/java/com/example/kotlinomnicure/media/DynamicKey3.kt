package com.example.kotlinomnicure.media

import java.io.ByteArrayOutputStream
import java.lang.Exception

class DynamicKey3 {
    @Throws(Exception::class)
    fun generate(appID: String, appCertificate: String,
        channelName: String,
        unixTs: Int,
        randomInt: Int,
        uid: Long,
        expiredTs: Int
    ): String? {
        var uid = uid
        val version = "003"
        val unixTsStr =
            ("0000000000" + Integer.toString(unixTs)).substring(Integer.toString(unixTs).length)
        val randomIntStr =
            ("00000000" + Integer.toHexString(randomInt)).substring(Integer.toHexString(randomInt).length)
        uid = uid and 0xFFFFFFFFL
        val uidStr =
            ("0000000000" + java.lang.Long.toString(uid)).substring(java.lang.Long.toString(uid).length)
        val expiredTsStr =
            ("0000000000" + Integer.toString(expiredTs)).substring(Integer.toString(expiredTs).length)
        val signature = generateSignature3(
            appID,
            appCertificate,
            channelName,
            unixTsStr,
            randomIntStr,
            uidStr,
            expiredTsStr
        )
        return String.format(
            "%s%s%s%s%s%s%s",
            version,
            signature,
            appID,
            unixTsStr,
            randomIntStr,
            uidStr,
            expiredTsStr
        )
    }

    @Throws(Exception::class)
    private fun generateSignature3(
        appID: String,
        appCertificate: String,
        channelName: String,
        unixTsStr: String,
        randomIntStr: String,
        uidStr: String,
        expiredTsStr: String
    ): String? {
        val baos = ByteArrayOutputStream()
        baos.write(appID.toByteArray())
        baos.write(unixTsStr.toByteArray())
        baos.write(randomIntStr.toByteArray())
        baos.write(channelName.toByteArray())
        baos.write(uidStr.toByteArray())
        baos.write(expiredTsStr.toByteArray())
        val sign: ByteArray? = DynamicKeyUtil().encodeHMAC(appCertificate, baos.toByteArray())
        return sign?.let { DynamicKeyUtil().bytesToHex(it) }
    }
}
