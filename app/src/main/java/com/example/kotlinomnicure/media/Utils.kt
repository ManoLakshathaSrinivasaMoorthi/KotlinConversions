package com.example.kotlinomnicure.media

import android.text.format.DateFormat
import org.ocpsoft.prettytime.PrettyTime
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.CRC32
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec



class Utils {
    val HMAC_SHA256_LENGTH: Long = 32
    val VERSION_LENGTH = 3
    val APP_ID_LENGTH = 32

    @Throws(InvalidKeyException::class, NoSuchAlgorithmException::class)
    fun hmacSign(keyString: String, msg: ByteArray?): ByteArray? {
        val keySpec = SecretKeySpec(keyString.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)
        return mac.doFinal(msg)
    }

    /*fun pack(packableEx: PackableEx): ByteArray? {
        val buffer = ByteBuf(data)
        packableEx.marshal(buffer)
        return buffer.asBytes()
    }*/

    fun unpack(data: ByteArray?, packableEx: PackableEx) {
        val buffer = ByteBuf(data)
        packableEx.unmarshal(buffer)
    }

    fun getTimeAgo(time: Long): String? {
        return try {
            val strDate: String? = Utils().timestampToDate(time)
            val dateFormat = SimpleDateFormat("MM-dd-yy hh:mma")
            val objDate = dateFormat.parse(strDate)
            val p = PrettyTime()
            println("pretty time " + objDate + " " + p.format(objDate))
            val timeAgo = p.format(objDate)
            timeAgo.replace("moments", "a moment")
        } catch (e: ParseException) {
            e.printStackTrace()
            timestampToDate(time)
        }
    }

    fun timestampToDate(timeStamp: Long): String? {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timeStamp
        var date = DateFormat.format("MM-dd-yy hh:mma", cal).toString()
        date = date.replace("AM", "am").replace("PM", "pm")
        return date
    }

/*    fun base64Encode(data: ByteArray?): String? {
        val encodedBytes: ByteArray = Base64.encodeBase64(data)
        return String(encodedBytes)
    }

    fun base64Decode(data: String): ByteArray? {
        return Base64.decodeBase64(data.toByteArray())
    }*/

    fun crc32(data: String): Int {
        // get bytes from string
        val bytes = data.toByteArray()
        return crc32(bytes)
    }

    fun crc32(bytes: ByteArray?): Int {
        val checksum = CRC32()
        checksum.update(bytes)
        return checksum.value.toInt()
    }

    fun getTimestamp(): Int {
        return (Date().time / 1000).toInt()
    }

    fun randomInt(): Int {
        return SecureRandom().nextInt()
    }

    fun isUUID(uuid: String): Boolean {
        return if (uuid.length != 32) {
            false
        } else uuid.matches("\\p{XDigit}+")
    }


    private fun String.matches(regex: String): Boolean {
        return  true
    }
}


