package com.example.kotlinomnicure.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.lang.StringBuilder
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESUtils {
    var plainText =
        "This is a plain text which need to be encrypted by Java AES 256 GCM Encryption Algorithm"
    val AES_KEY_SIZE = 256
    val GCM_IV_LENGTH = 12
    val GCM_TAG_LENGTH = 16
    val password = "Omnicure"
    val keyValue = "ShVmYq3t6w9y\$B&E)H@McQfTjWnZr4u7"
    val keyString = "056f1e1b03bd587161d7d4bacc1c1296a726081a668929bb8d43403238df64bb"
    val IV = ByteArray(GCM_IV_LENGTH)
    var key: SecretKey = SecretKeySpec(ByteArray(12), "AES")

    @Throws(Exception::class)
    fun getSecretKey(keyString: String): SecretKey {
        val byteVal = dataFromHexString(keyString)
        return SecretKeySpec(byteVal, "AES")
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun encryptData(data: String, key1: String): String? {
        return try {

            //            SecureRandom random = new SecureRandom();
            //            random.nextBytes(IV);
            val key = getSecretKey(key1)
            val cipherText = encrypt(data.toByteArray(), key, IV)
            println("Encrypted Text :data.getBytes() " + data.toByteArray())
            println("Encrypted Text : $cipherText")
            val cipherString = dataToHexString(cipherText)
            Log.e("Encrypt", "encryptData: cipherString-->$cipherString")
            //            byte[] cipherByte = dataFromHexString(cipherString);
            //            System.out.println("converted back to byte " + cipherByte);
            //            SecretKey key2 = getSecretKey(key1);
            //            String decryptedText = decryptData1(cipherByte, key2);
            //            String decryptedText = decrypt(cipherText, key, IV);

            //            System.out.println("DeCrypted Text : " + decryptedText);
            cipherString
        } catch (e: Exception) {
            Log.e("AESUtils", "error_encryptData")
            e.printStackTrace()
            ""
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun decryptData(data: String, key1: String): String? {
        return try {
            val key = getSecretKey(key1)
            //            data = "fb2314a45353d8f6520433d741d7480279b892069a5ea532394d8124491c8936eb70342585c111247ef9999734eca3455df82fbb2b349d1dbf9ff634803e3aeab11a8859ca7a4a7e6e5ab6bf3838c7b4958be79b855e50594cb184ecc281f3a67a9e827335aae523e59ca0ef93b1f59238cf35471e9af26fc5f7506896f925fc";
            val cipherByte = dataFromHexString(data)
            println("converted back to byte $data")
            val decryptedText = decrypt(cipherByte, key, IV)
            //            String decryptedText = decrypt(cipherText, key, IV);
            Log.d("TAG", "decryptData: $decryptedText")
            decryptedText
        } catch (e: Exception) {
            Log.e("AESUtils", "error_decryptData")
            e.printStackTrace()
            data
        }
    }

    @Throws(Exception::class)
    fun encrypt(
        plaintext: ByteArray?,
        key: SecretKey,
        IV: ByteArray?
    ): ByteArray {
        // Get Cipher Instance
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Create SecretKeySpec
        val keySpec =
            SecretKeySpec(key.encoded, "AES")

        // Create GCMParameterSpec
        val gcmParameterSpec =
            GCMParameterSpec(GCM_TAG_LENGTH * 8, IV)

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec)

        // Perform Encryption
        return cipher.doFinal(plaintext)
    }

    @Throws(Exception::class)
    fun decrypt(cipherText: ByteArray?, key: SecretKey, IV: ByteArray?): String {
        // Get Cipher Instance
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Create SecretKeySpec
        val keySpec = SecretKeySpec(key.encoded, "AES")

        // Create GCMParameterSpec
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, IV)

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec)

        // Perform Decryption
        val decryptedText = cipher.doFinal(cipherText)
        return String(decryptedText)
    }

    private fun dataToHexString(data: ByteArray): String {
        val str = StringBuilder()
        for (i in data.indices) str.append(String.format("%02x", data[i]))
        return str.toString()
    }

    @Throws(Exception::class)
    private fun dataFromHexString(hexString: String): ByteArray {
        var hexString = hexString
        hexString = hexString.trim { it <= ' ' }
        hexString = hexString.replace("[ ]".toRegex(), "")
        hexString = hexString.toLowerCase()
        val validHexChar = "abcdef0123456789"
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            val c1 = hexString[i]
            val c2 = hexString[i + 1]
            if (!validHexChar.contains("" + c1) || !validHexChar.contains("" + c2)) {
                throw Exception("Invalid encryption hex data")
            }
            data[i / 2] =
                ((Character.digit(c1, 16) shl 4) + Character.digit(c2, 16)).toByte()
            i += 2
        }
        return data
    }
}