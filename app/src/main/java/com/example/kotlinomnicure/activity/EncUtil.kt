package com.example.kotlinomnicure.activity

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.AssertionError
import java.lang.Exception
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.security.auth.x500.X500Principal

class EncUtil : AppCompatActivity() {
    private val TAG = "ENCUTIL"
    private val ANDROIDKEYSTORE = "AndroidKeyStore"
    private val AES_MODE = "AES/GCM/NoPadding"
    private val FIXED_IV =
        "randomizemsg" // to randomize the encrypted data( give any values to randomize)

    private val RSA_MODE =
        "RSA/ECB/PKCS1Padding" // RSA algorithm which has to be used for OS version less than M

    private val EXCEPTION = "EXCEPTION"
    private var keyStore: KeyStore? = null

    fun generateKey(context: Context?) {
        try {
            val KEY_ALIAS: String? = context?.let {
                PrefUtility().getStringInPref(
                    it,
                    Constants.SharedPrefConstants.ENCRYPTIONKEY,
                    ""
                )
            }
            keyStore = KeyStore.getInstance(ANDROIDKEYSTORE)
            keyStore!!.load(null)
            if (!keyStore!!.containsAlias(KEY_ALIAS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val keyGenerator =
                        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROIDKEYSTORE)
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
                        ANDROIDKEYSTORE
                    )
                    kpg.initialize(spec)
                    kpg.generateKeyPair()
                }
            } else {
                Log.d("keyStore", "key already available")
            }
        } catch (e: KeyStoreException) {
            Log.e(TAG, EXCEPTION, e.cause)
        } catch (e: IOException) {
            Log.e(TAG, EXCEPTION, e.cause)
        } catch (e: CertificateException) {
            Log.e(TAG, EXCEPTION, e.cause)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, EXCEPTION, e.cause)
        } catch (e: InvalidAlgorithmParameterException) {
            Log.e(TAG, EXCEPTION, e.cause)
        } catch (e: NoSuchProviderException) {
            Log.e(TAG, EXCEPTION, e.cause)
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
            c = Cipher.getInstance(AES_MODE)
            c.init(
                Cipher.ENCRYPT_MODE,
                getSecretKey(context),
                GCMParameterSpec(128, FIXED_IV.toByteArray())
            )
            val encodedBytes = c.doFinal(input.toByteArray())
            return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Exception3:", e.cause)
        } catch (e: NoSuchPaddingException) {
            Log.e(TAG, "Exception3:", e.cause)
        } catch (e: InvalidKeyException) {
            Log.e(TAG, "Exception3:", e.cause)
        } catch (e: InvalidAlgorithmParameterException) {
            Log.e(TAG, "Exception3:", e.cause)
        } catch (e: IllegalBlockSizeException) {
            Log.e(TAG, "Exception3:", e.cause)
        } catch (e: BadPaddingException) {
            Log.e(TAG, "Exception3:", e.cause)
        } catch (e: Exception) {
            Log.e(TAG, "Exception4:", e.cause)
        }
        return ""
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun decryptM(context: Context, encrypted: String): ByteArray? {
        var c: Cipher? = null
        try {
            c = Cipher.getInstance(AES_MODE)
            c.init(
                Cipher.DECRYPT_MODE,
                getSecretKey(context),
                GCMParameterSpec(128, FIXED_IV.toByteArray())
            )
            val barr = Base64.decode(encrypted, Base64.DEFAULT)
            return c.doFinal(barr)
        } catch (e: Exception) {
            Log.e(TAG, EXCEPTION, e.cause)
        }
        return null
    }

    @Throws(Exception::class)
    private fun rsaEncrypt(secret: ByteArray, context: Context): String? {
        val KEY_ALIAS: String? =
            PrefUtility().getStringInPref(context, Constants.SharedPrefConstants.ENCRYPTIONKEY, "")
        val privateKeyEntry = keyStore!!.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        // Encrypt the text
        val inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
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
        val KEY_ALIAS: String? =
            PrefUtility().getStringInPref(context, Constants.SharedPrefConstants.ENCRYPTIONKEY, "")
        val privateKeyEntry = keyStore!!.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
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

    fun decrypt(context: BiometricPrompt.AuthenticationCallback, text: String?): String? {
        try {
            var d: ByteArray? = ByteArray(0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                d = context?.let { text?.let { it1 -> EncUtil().decryptM(it, it1) } }
            } else {
                try {
                    d = context?.let { text?.let { it1 -> EncUtil().rsaDecrypt(it1, it) } }
                } catch (e: Exception) {
                    Log.e(TAG, EXCEPTION, e.cause)
                }
            }
            try {
                assert(d != null)
            } catch (e: AssertionError) {
            }
            return String(d!!, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Exception2:", e.cause)
        }
        return ""
    }

    fun encrypt(context: Context?, text: String?): String? {
        var text = text
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                text = context?.let { text?.let { it1 -> EncUtil().encryptM(it, it1) } }
            } else {
                text = context?.let { EncUtil().rsaEncrypt(text!!.toByteArray(), it) }
            }
            assert(text != null)
            return text
        } catch (e: Exception) {
            Log.e(TAG, "Exception1:", e.cause)
        }
        return ""
    }
}