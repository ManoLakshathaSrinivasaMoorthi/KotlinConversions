package com.example.kotlinomnicure.media

import com.google.android.exoplayer2.decoder.DecoderException
import com.google.android.gms.common.util.Hex
import java.lang.Exception
import java.util.*

class DynamicKey5 {
    val version = "005"
    val noUpload = "0"
    val audioVideoUpload = "3"

    // ServiceType
    val MEDIA_CHANNEL_SERVICE: Short = 1
    val RECORDING_SERVICE: Short = 2
    val PUBLIC_SHARING_SERVICE: Short = 3
    val IN_CHANNEL_PERMISSION: Short = 4

    // InChannelPermissionKey
    val ALLOW_UPLOAD_IN_CHANNEL: Short = 1
    private val TAG = "DynamicKey"

    private var content: DynamicKey5Content? = null

    fun fromString(key: String): Boolean {
        if (key.substring(0, 3) != version) {
            return false
        }
        var rawContent = ByteArray(0)
        try {
            rawContent = Base64().decode(key.substring(3))
        } catch (e: DecoderException) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        if (rawContent.size == 0) {
            return false
        }
        content = DynamicKey5Content()
        val buffer = ByteBuf(rawContent)
        content!!.unmarshall(buffer)
        return true
    }

    @Throws(Exception::class)
    fun generateSignature(
        appCertificate: String,
        service: Short,
        appID: String,
        unixTs: Int,
        salt: Int,
        channelName: String?,
        uid: Long,
        expiredTs: Int,
        extra: TreeMap<Short?, String?>?
    ): String {
        // decode hex to avoid case problem
        val hex = Hex()
        val rawAppID: ByteArray = hex().decode(appID.toByteArray())
        val rawAppCertificate: ByteArray = hex.decode(appCertificate.toByteArray())
        val m = Message(
            service, rawAppID, unixTs, salt, channelName,
            (uid and 0xFFFFFFFFL).toInt(), expiredTs, extra
        )
        val toSign = pack(m)
        return String(Hex.encodeHex(DynamicKeyUtil.encodeHMAC(rawAppCertificate, toSign)))
    }

    @Throws(Exception::class)
    fun generateDynamicKey(
        appID: String,
        appCertificate: String,
        channel: String?,
        ts: Int,
        salt: Int,
        uid: Long,
        expiredTs: Int,
        extra: TreeMap<Short?, String?>?,
        service: Short
    ): String? {
        val signature = generateSignature(
            appCertificate,
            service,
            appID,
            ts,
            salt,
            channel,
            uid,
            expiredTs,
            extra
        )
        val content = DynamicKey5Content(
            service,
            signature,
            Hex().decode(appID.toByteArray()),
            ts,
            salt,
            expiredTs,
            extra
        )
        val bytes = pack(content)
        val encoded: ByteArray = Base64().encode(bytes)
        val base64 = String(encoded)
        return version + base64
    }

    private fun pack(content: Packable): ByteArray? {
        val buffer = ByteBuf(rawContent)
        content.marshal(buffer)
        return buffer.asBytes()
    }

    @Throws(Exception::class)
    fun generatePublicSharingKey(
        appID: String,
        appCertificate: String,
        channel: String?,
        ts: Int,
        salt: Int,
        uid: Long,
        expiredTs: Int
    ): String? {
        return generateDynamicKey(
            appID,
            appCertificate,
            channel,
            ts,
            salt,
            uid,
            expiredTs,
            TreeMap(),
            PUBLIC_SHARING_SERVICE
        )
    }

    @Throws(Exception::class)
    fun generateRecordingKey(
        appID: String,
        appCertificate: String,
        channel: String?,
        ts: Int,
        salt: Int,
        uid: Long,
        expiredTs: Int
    ): String? {
        return generateDynamicKey(
            appID,
            appCertificate,
            channel,
            ts,
            salt,
            uid,
            expiredTs,
            TreeMap(),
            RECORDING_SERVICE
        )
    }

    @Throws(Exception::class)
    fun generateMediaChannelKey(
        appID: String,
        appCertificate: String,
        channel: String?,
        ts: Int,
        salt: Int,
        uid: Long,
        expiredTs: Int
    ): String? {
        return generateDynamicKey(
            appID,
            appCertificate,
            channel,
            ts,
            salt,
            uid,
            expiredTs,
            TreeMap(),
            MEDIA_CHANNEL_SERVICE
        )
    }

    @Throws(Exception::class)
    fun generateInChannelPermissionKey(
        appID: String,
        appCertificate: String,
        channel: String?,
        ts: Int,
        salt: Int,
        uid: Long,
        expiredTs: Int,
        permission: String?
    ): String? {
        val extra = TreeMap<Short?, String?>()
        extra[ALLOW_UPLOAD_IN_CHANNEL] = permission
        return generateDynamicKey(
            appID,
            appCertificate,
            channel,
            ts,
            salt,
            uid,
            expiredTs,
            extra,
            IN_CHANNEL_PERMISSION
        )
    }

    internal class Message(
        private val serviceType: Short,
        private val appID: ByteArray,
        private val unixTs: Int,
        private val salt: Int,
        private val channelName: String?,
        private val uid: Int,
        private val expiredTs: Int,
        private val extra: TreeMap<Short?, String?>?
    ) :
        Packable {
        fun marshal(out: ByteBuf): ByteBuf {
            return out.put(serviceType)!!.put(appID)!!.put(unixTs)!!.put(salt)!!
                .put(channelName!!)!!.put(uid)!!
                .put(
                    expiredTs
                ).put(extra)
        }
    }

    class DynamicKey5Content : Packable {
        private var serviceType: Short = 0
        private var signature: String? = null
        private var appID: ByteArray
        private var unixTs = 0
        private var salt = 0
        private var expiredTs = 0
        private var extra: TreeMap<Short?, String?>? = null

        constructor() {}
        constructor(
            serviceType: Short,
            signature: String?,
            appID: ByteArray,
            unixTs: Int,
            salt: Int,
            expiredTs: Int,
            extra: TreeMap<Short?, String?>?
        ) {
            this.serviceType = serviceType
            this.signature = signature
            this.appID = appID
            this.unixTs = unixTs
            this.salt = salt
            this.expiredTs = expiredTs
            this.extra = extra
        }

        fun marshal(out: ByteBuf): ByteBuf {
            return out.put(serviceType)!!.put(signature!!)!!.put(appID)!!.put(unixTs)!!.put(salt)!!
                .put(expiredTs).put(extra)
        }

        fun unmarshall(`in`: ByteBuf) {
            serviceType = `in`.readShort()
            signature = `in`.readString()
            appID = `in`.readBytes()
            unixTs = `in`.readInt()
            salt = `in`.readInt()
            expiredTs = `in`.readInt()
            extra = `in`.readMap()
        }
    }
}