package com.example.kotlinomnicure.helper

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.security.auth.x500.X500Principal

class EncUtil{

    private val AndroidKeyStore = "AndroidKeyStore"
    private val AES_MODE = "AES/CBC/PKCS5Padding"
    private val FIXED_IV = "randomizemsg" // to randomize the encrypted data( give any values to randomize)

    private val RSA_MODE =
        "RSA/ECB/PKCS1Padding" // RSA algorithm which has to be used for OS version less than M

    private val TAG = "EncUtil"
    private var keyStore: KeyStore? = null

    fun generateKey(context: Context?) {
        try {
            val KEY_ALIAS: String? = context?.let {
                PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.ENCRYPTIONKEY, "")
            }
            keyStore = KeyStore.getInstance(AndroidKeyStore)
            keyStore?.load(null)
            if (!keyStore?.containsAlias(KEY_ALIAS)!!) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val keyGenerator =
                        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore)
                    keyGenerator.init(
                        KEY_ALIAS?.let {
                            KeyGenParameterSpec.Builder(
                                it,
                                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                            )
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setRandomizedEncryptionRequired(false)
                                .build()
                        }
                    )
                    keyGenerator.generateKey()
                } else {
                    // Generate a key pair for encryption
                    val start = Calendar.getInstance()
                    val end = Calendar.getInstance()
                    end.add(Calendar.YEAR, 30)
                    var spec: KeyPairGeneratorSpec? = null
                    spec = KEY_ALIAS?.let {
                        KeyPairGeneratorSpec.Builder(context!!)
                            .setAlias(it)
                            .setSubject(X500Principal("CN=$KEY_ALIAS"))
                            .setSerialNumber(BigInteger.TEN)
                            .setStartDate(start.time)
                            .setEndDate(end.time)
                            .build()
                    }
                    val kpg = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA,
                        AndroidKeyStore
                    )
                    kpg.initialize(spec)
                    kpg.generateKeyPair()
                }
            } else {
//                Log.d("keyStore","key already available");
            }
        } catch (e: KeyStoreException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: IOException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: CertificateException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: NoSuchAlgorithmException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: InvalidAlgorithmParameterException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: NoSuchProviderException) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    @Throws(Exception::class)
    private fun getSecretKey(context: Context): Key? {
        val KEY_ALIAS: String? =
            PrefUtility().getStringInPref(context, Constants.SharedPrefConstants.ENCRYPTIONKEY, "")
        return keyStore!!.getKey(KEY_ALIAS, null)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun encryptM(context: Context, input: String): String? {
        var c: Cipher? = null
        try {
//            c = Cipher.getInstance(AES_MODE);
//            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c = Cipher.getInstance("AES/GCM/NoPadding")
            c.init(
                Cipher.ENCRYPT_MODE,
                getSecretKey(context),
                GCMParameterSpec(128, FIXED_IV.toByteArray())
            )
            val encodedBytes = c.doFinal(input.toByteArray())
            return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
        } catch (e: NoSuchAlgorithmException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: NoSuchPaddingException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: InvalidKeyException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: InvalidAlgorithmParameterException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: IllegalBlockSizeException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: BadPaddingException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return ""
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun decryptM(context: Context, encrypted: String): ByteArray? {
        var c: Cipher? = null
        try {
//            c = Cipher.getInstance(AES_MODE);
//            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c = Cipher.getInstance("AES/GCM/NoPadding")
            c.init(
                Cipher.DECRYPT_MODE,
                getSecretKey(context),
                GCMParameterSpec(128, FIXED_IV.toByteArray())
            )
            val barr = Base64.decode(encrypted, Base64.DEFAULT)
            return c.doFinal(barr)
        } catch (e: NoSuchAlgorithmException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: NoSuchPaddingException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: InvalidKeyException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: InvalidAlgorithmParameterException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: IllegalBlockSizeException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: BadPaddingException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return null
    }

    @Throws(Exception::class)
    private fun rsaEncrypt(secret: ByteArray, context: Context): String? {
        val KEY_ALIAS: String? =
            PrefUtility().getStringInPref(context, Constants.SharedPrefConstants.ENCRYPTIONKEY, "")
        val privateKeyEntry = keyStore!!.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        // Encrypt the text
//        Cipher inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
//        Cipher inputCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
        val inputCipher =
            Cipher.getInstance("RSA/None/OAEPWITHSHA-256ANDMGF1PADDING", "AndroidOpenSSL")
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)
        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()
        val vals = outputStream.toByteArray()
        return Base64.encodeToString(vals, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    private fun rsaDecrypt(encrypted: String, context: Context): ByteArray? {
        val KEY_ALIAS: String = PrefUtility().getStringInPref(context, Constants.SharedPrefConstants.ENCRYPTIONKEY, "")
        val privateKeyEntry = keyStore!!.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        //        Cipher output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
//        Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
        val output = Cipher.getInstance("RSA/None/OAEPWITHSHA-256ANDMGF1PADDING", "AndroidOpenSSL")
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)
        val barr = Base64.decode(encrypted, Base64.DEFAULT)
        val cipherInputStream = CipherInputStream(
            ByteArrayInputStream(barr), output
        )
        val values = ArrayList<Byte>()
        var nextByte: Int
        while (cipherInputStream.read().also { nextByte = it } != -1) {
            values.add(nextByte.toByte())
        }
        val bytes = ByteArray(values.size)
        for (i in bytes.indices) {
            bytes[i] = values[i]
        }
        return bytes
    }

    fun decrypt(context: Context?, text: String?): String? {
        try {
            var d = ByteArray(0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                d = context?.let { text?.let { it1 -> EncUtil().decryptM(it, it1) } }!!
            } else {
                try {
                    d = text?.let { context?.let { it1 -> EncUtil().rsaDecrypt(it, it1) } }!!
                } catch (e: Exception) {
//                    Log.e(TAG, "Exception:", e.getCause());
                }
            }
            assert(d != null)
            return String(d, charset("UTF-8"))
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return ""
    }

    fun encrypt(context: Context?, text: String?): String? {
        var text = text
        try {
            val d = ByteArray(0)
            text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context?.let { text?.let { it1 -> EncUtil().encryptM(it, it1) } }
            } else {
                context?.let { EncUtil().rsaEncrypt(text!!.toByteArray(), it) }
            }
            assert(text != null)
            return text
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return ""
    }
}

