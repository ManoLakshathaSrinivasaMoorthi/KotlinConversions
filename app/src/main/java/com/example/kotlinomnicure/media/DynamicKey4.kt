package com.example.kotlinomnicure.media

import java.io.ByteArrayOutputStream

class DynamicKey4 {

    private val PUBLIC_SHARING_SERVICE = "APSS"
    private val RECORDING_SERVICE = "ARS"
    private val MEDIA_CHANNEL_SERVICE = "ACS"

    /**
     * Generate Dynamic Key for Public Sharing Service
     * @param appID App IDassigned by Agora
     * @param appCertificate App Certificate assigned by Agora
     * @param channelName name of channel to join, limited to 64 bytes and should be printable ASCII characters
     * @param unixTs unix timestamp in seconds when generating the Dynamic Key
     * @param randomInt salt for generating dynamic key
     * @param uid user id, range from 0 - max uint32
     * @param expiredTs should be 0
     * @return String representation of dynamic key
     * @throws Exception if any error occurs
     */
    @Throws(Exception::class)
    fun generatePublicSharingKey(
        appID: String,
        appCertificate: String,
        channelName: String,
        unixTs: Int,
        randomInt: Int,
        uid: Long,
        expiredTs: Int
    ): String? {
        return doGenerate(
            appID,
            appCertificate,
            channelName,
            unixTs,
            randomInt,
            uid,
            expiredTs,
            PUBLIC_SHARING_SERVICE
        )
    }


    /**
     * Generate Dynamic Key for recording service
     * @param appID Vendor key assigned by Agora
     * @param appCertificate Sign key assigned by Agora
     * @param channelName name of channel to join, limited to 64 bytes and should be printable ASCII characters
     * @param unixTs unix timestamp in seconds when generating the Dynamic Key
     * @param randomInt salt for generating dynamic key
     * @param uid user id, range from 0 - max uint32
     * @param expiredTs should be 0
     * @return String representation of dynamic key
     * @throws Exception if any error occurs
     */
    @Throws(Exception::class)
    fun generateRecordingKey(
        appID: String,
        appCertificate: String,
        channelName: String,
        unixTs: Int,
        randomInt: Int,
        uid: Long,
        expiredTs: Int
    ): String? {
        return doGenerate(
            appID,
            appCertificate,
            channelName,
            unixTs,
            randomInt,
            uid,
            expiredTs,
            RECORDING_SERVICE
        )
    }

    /**
     * Generate Dynamic Key for media channel service
     * @param appID Vendor key assigned by Agora
     * @param appCertificate Sign key assigned by Agora
     * @param channelName name of channel to join, limited to 64 bytes and should be printable ASCII characters
     * @param unixTs unix timestamp in seconds when generating the Dynamic Key
     * @param randomInt salt for generating dynamic key
     * @param uid user id, range from 0 - max uint32
     * @param expiredTs service expiring timestamp. After this timestamp, user will not be able to stay in the channel.
     * @return String representation of dynamic key
     * @throws Exception if any error occurs
     */
    @Throws(Exception::class)
    fun generateMediaChannelKey(
        appID: String,
        appCertificate: String,
        channelName: String,
        unixTs: Int,
        randomInt: Int,
        uid: Long,
        expiredTs: Int
    ): String? {
        return doGenerate(
            appID,
            appCertificate,
            channelName,
            unixTs,
            randomInt,
            uid,
            expiredTs,
            MEDIA_CHANNEL_SERVICE
        )
    }

    @Throws(Exception::class)
    private fun doGenerate(
        appID: String,
        appCertificate: String,
        channelName: String,
        unixTs: Int,
        randomInt: Int,
        uid: Long,
        expiredTs: Int,
        serviceType: String
    ): String? {
        var uid = uid
        val version = "004"
        val unixTsStr =
            ("0000000000" + Integer.toString(unixTs)).substring(Integer.toString(unixTs).length)
        val randomIntStr =
            ("00000000" + Integer.toHexString(randomInt)).substring(Integer.toHexString(randomInt).length)
        uid = uid and 0xFFFFFFFFL
        val uidStr =
            ("0000000000" + java.lang.Long.toString(uid)).substring(java.lang.Long.toString(uid).length)
        val expiredTsStr =
            ("0000000000" + Integer.toString(expiredTs)).substring(Integer.toString(expiredTs).length)
        val signature = generateSignature4(
            appID,
            appCertificate,
            channelName,
            unixTsStr,
            randomIntStr,
            uidStr,
            expiredTsStr,
            serviceType
        )
        return String.format(
            "%s%s%s%s%s%s",
            version,
            signature,
            appID,
            unixTsStr,
            randomIntStr,
            expiredTsStr
        )
    }

    @Throws(Exception::class)
    private fun generateSignature4(
        appID: String,
        appCertificate: String,
        channelName: String,
        unixTsStr: String,
        randomIntStr: String,
        uidStr: String,
        expiredTsStr: String,
        serviceType: String
    ): String? {
        val baos = ByteArrayOutputStream()
        baos.write(serviceType.toByteArray())
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
