package com.example.kotlinomnicure.media

import android.util.Log


import java.io.ByteArrayOutputStream

import java.io.IOException

import java.util.TreeMap


class AccessToken(appId: String?, appCertificate: String?, channelName: String?, account: String?) {
    private val TAG = "AccessToken"

    enum class Privileges(value: Int) {
        kJoinChannel(1), kPublishAudioStream(2), kPublishVideoStream(3), kPublishDataStream(4),  // For RTM only
        kRtmLogin(1000);

        // The following privileges have not
        // been implemented yet.
        //kPublishAudiocdn(5),
        //kPublishVideoCdn(6),
        //kRequestPublishAudioStream(7),
        //kRequestPublishVideoStream(8),
        //kRequestPublishDataStream(9),
        //kInvitePublishAudioStream(10),
        //kInvitePublishVideoStream(11),
        //kInvitePublishDataStream(12),
        //kAdministrateChannel(101),
        val intValue: Short

        init {
            intValue = value.toShort()
        }
    }

    private val VER = "006"

    private var appId: String? = null
    private var appCertificate: String? = null
    private var channelName: String? = null
    private var uid: String? = null
    private lateinit var signature: ByteArray
    private lateinit var messageRawContent: ByteArray
    private var crcChannelName = 0
    private var crcUid = 0
    private var message: PrivilegeMessage? = null
    private val expireTimestamp = 0

    fun AccessToken(appId: String?, appCertificate: String?, channelName: String?, uid: String?) {
        this.appId = appId
        this.appCertificate = appCertificate
        this.channelName = channelName
        this.uid = uid
        crcChannelName = 0
        crcUid = 0
        message = PrivilegeMessage()
    }

    @Throws(Exception::class)
    fun build(): String? {
        if (!appId?.let { Utils().isUUID(it) }) {
            return ""
        }
        if (!appCertificate?.let { Utils().isUUID(it) }) {
            return ""
        }
        messageRawContent = message?.let { Utils().pack(it) }!!
        signature = generateSignature(
            appCertificate,
            appId, channelName, uid, messageRawContent
        )
        crcChannelName = crc32(channelName)
        crcUid = crc32(uid)
        val packContent = PackContent(signature, crcChannelName, crcUid, messageRawContent)
        val content: ByteArray? = Utils().pack(packContent)
        return getVersion() + appId + Utils().base64Encode(content)
    }

    fun addPrivilege(privilege: Privileges, expireTimestamp: Int) {
        message!!.messages!![privilege.intValue] = expireTimestamp
    }

    fun getVersion(): String {
        return VER
    }

    @Throws(Exception::class)
    fun generateSignature(
        appCertificate: String?,
        appID: String?, channelName: String?, uid: String?, message: ByteArray?
    ): ByteArray? {
        val baos = ByteArrayOutputStream()
        try {
            baos.write(appID!!.toByteArray())
            baos.write(channelName!!.toByteArray())
            baos.write(uid!!.toByteArray())
            baos.write(message)
        } catch (e: IOException) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return appCertificate?.let { Utils().hmacSign(it, baos.toByteArray()) }
    }

    fun fromString(token: String): Boolean {
        if (getVersion() != token.substring(0, Utils().VERSION_LENGTH)) {
            return false
        }
        try {
            appId =
                token.substring(Utils().VERSION_LENGTH, Utils().VERSION_LENGTH + Utils().APP_ID_LENGTH)
            val packContent = PackContent()
            Utils().unpack(Utils().base64Decode(
                    token.substring(
                        Utils().VERSION_LENGTH + Utils().APP_ID_LENGTH,
                        token.length
                    )
                ), packContent
            )
            signature = packContent.signature
            crcChannelName = packContent.crcChannelName
            crcUid = packContent.crcUid
            messageRawContent = packContent.rawMessage
            message?.let { Utils().unpack(messageRawContent, it) }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
            return false
        }
        return true
    }

    abstract class PrivilegeMessage : PackableEx {
        private var salt: Int
        private var ts: Int
        var messages: TreeMap<Short, Int>?
        private fun marshal(out: ByteBuf): ByteBuf? {
            return out.put(salt)!!.put(ts)!!.putIntMap(messages!!)
        }

        fun unmarshal(`in`: ByteBuf) {
            salt = `in`.readInt()
            ts = `in`.readInt()
            messages = `in`.readIntMap()
        }

        init {
            salt = Utils.randomInt()
            ts = Utils.getTimestamp() + 24 * 3600
            messages = TreeMap()
        }
    }

    class PackContent : PackableEx {
        var signature: ByteArray
        var crcChannelName = 0
        var crcUid = 0
        var rawMessage: ByteArray

        constructor() {
            // Nothing done
        }

        constructor(signature: ByteArray, crcChannelName: Int, crcUid: Int, rawMessage: ByteArray) {
            this.signature = signature
            this.crcChannelName = crcChannelName
            this.crcUid = crcUid
            this.rawMessage = rawMessage
        }

        fun marshal(out: ByteBuf): ByteBuf? {
            return out.put(signature)!!.put(crcChannelName)!!.put(crcUid)!!.put(rawMessage)
        }

        fun unmarshal(`in`: ByteBuf) {
            signature = `in`.readBytes()
            crcChannelName = `in`.readInt()
            crcUid = `in`.readInt()
            rawMessage = `in`.readBytes()
        }

        @JvmName("marshal1")
        override fun marshal(out: ByteBuf?): ByteBuf? {
            TODO("Not yet implemented")
        }
    }
}
