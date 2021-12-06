package com.example.kotlinomnicure.media

import java.io.ByteArrayOutputStream
import java.lang.Exception

class DynamicKey {
    @Throws(Exception::class)
    fun generate(
        appID: String,
        appCertificate: String,
        channelName: String,
        unixTs: Int,
        randomInt: Int
    ): String {
        val unixTsStr =
            ("0000000000" + Integer.toString(unixTs)).substring(Integer.toString(unixTs).length)
        val randomIntStr =
            ("00000000" + Integer.toHexString(randomInt)).substring(Integer.toHexString(randomInt).length)
        val signature =
            generateSignature(appID, appCertificate, channelName, unixTsStr, randomIntStr)
        return String.format("%s%s%s%s", signature, appID, unixTsStr, randomIntStr)
    }

    @Throws(Exception::class)
    private fun generateSignature(
        appID: String,
        appCertificate: String,
        channelName: String,
        unixTsStr: String,
        randomIntStr: String
    ): String? {
        val baos = ByteArrayOutputStream()
        baos.write(appID.toByteArray())
        baos.write(unixTsStr.toByteArray())
        baos.write(randomIntStr.toByteArray())
        baos.write(channelName.toByteArray())
        val sign: ByteArray? = DynamicKeyUtil().encodeHMAC(appCertificate, baos.toByteArray())
        return sign?.let { DynamicKeyUtil().bytesToHex(it) }
    }

}
