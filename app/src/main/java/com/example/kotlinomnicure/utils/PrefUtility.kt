package com.example.kotlinomnicure.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import java.io.*
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.*

class PrefUtility {

    private val TAG = PrefUtility::class.java.simpleName

    fun removeFromPref(context: Context, key: String?) {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove(key)
        editor.apply()
    }

    /**
     * This method save  a String in Application prefs
     *
     * @param context
     * @param key
     * @param value
     */
    fun saveStringInPref(context: Context, key: String?, value: String?) {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.commit()
            //saveEncryptedStringInPref(context,key,value);
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * This method returns the string value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    fun getStringInPref(context: Context, key: String?, defaultValue: String?): String? {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            return prefs.getString(key, defaultValue)
            //return getEncryptedStringInPref(context,key,defaultValue);
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
        return ""
    }

    /**
     * This method save  a String in Application prefs
     *
     * @param context
     * @param key
     * @param stringSet
     */
    fun saveStringSetInPref(context: Context, key: String?, stringSet: Set<String?>?) {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putStringSet(key, stringSet)
            editor.commit()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * This method returns the string value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultSetValue
     * @return
     */
    fun getStringSetInPref(
        context: Context,
        key: String?,
        defaultSetValue: Set<String?>?
    ): Set<String?>? {
//        defaultSetValue = new HashSet<>();
        val defSetValue: Set<String> = HashSet()
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            return prefs.getStringSet(key, defSetValue)
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
        return defaultSetValue
    }

    /**
     * This method save  a Int in Application prefs
     *
     * @param context
     * @param key
     * @param value
     */
    fun saveIntInPref(context: Context, key: String?, value: Int) {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putInt(key, value)
            editor.commit()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * This method returns the int value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    fun getIntInPref(context: Context, key: String?, defaultValue: Int): Int {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return prefs.getInt(key, defaultValue)
    }

    /**
     * This method save  a Long in Application prefs
     *
     * @param context
     * @param key
     * @param value
     */
    fun saveLongInPref(context: Context, key: String?, value: Long) {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putLong(key, value)
            editor.commit()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * This method returns the Long value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     */
    fun getLongInPref(context: Context, key: String?, defaultValue: Long): Long {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            return prefs.getLong(key, defaultValue)
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
        return 0
    }


    fun saveFloatInPref(context: Context, key: String?, value: Float) {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putFloat(key, value)
            editor.commit()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * This method returns the Long value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     */
    fun getFloatInPref(context: Context, key: String?, defaultValue: Float): Float {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return prefs.getFloat(key, defaultValue)
    }

    fun saveDoubleInPref(context: Context, key: String?, value: Double) {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
            editor.commit()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * This method returns the Long value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     */
    fun getDoubleInPref(context: Context, key: String?, defaultValue: Long): Double {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return java.lang.Double.longBitsToDouble(prefs.getLong(key, defaultValue))
    }


    fun getBooleanInPref(context: Context, key: String?, defaultValue: Boolean): Boolean {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, defaultValue)
    }

    fun saveBooleanInPref(context: Context, key: String?, value: Boolean) {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(key, value)
            editor.commit()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun writeObject(context: Context, obj: Any?, fileName: String?) {
        var fos: FileOutputStream? = null
        var os: ObjectOutputStream? = null
        try {
            Log.i(TAG, "writeObject: =====Exits")
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            os = ObjectOutputStream(fos)
            os.writeObject(obj)
        } catch (ex: Exception) {
            Log.i(TAG, "writeObject: exception$ex")
        } finally {
            if (os != null) {
                try {
                    os.flush()
                } catch (e: IOException) {
                    Log.i(TAG, "writeObject: exception$e")
                } finally {
                    try {
                        os.close()
                    } catch (e: IOException) {
                        Log.i(TAG, "writeObject: exception$e")
                    }
                }
            }
            if (fos != null) {
                try {
                    fos.flush()
                } catch (e: IOException) {
                    Log.i(TAG, "writeObject: exception$e")
                } finally {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        Log.i(TAG, "writeObject: exception$e")
                    }
                }
            }
        }
    }

    fun readObject(context: Context, fileName: String?): Any? {
        var obj: Any? = null
        var fis: FileInputStream? = null
        var `is`: ObjectInputStream? = null
        try {
            fis = context.openFileInput(fileName)
            `is` = ObjectInputStream(fis)
            obj = `is`.readObject()
        } catch (ex: Exception) {
            Log.e(TAG, "Exception:", ex.cause)
        } finally {
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Exception:", e.cause)
                }
            }
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Exception:", e.cause)
                }
            }
        }
        return obj
    }

    fun deleteFile(context: Context?, fileName: String): Boolean {
        var deleted = false
        if (context != null) {
            try {
                val file = File(context.filesDir.absolutePath + "/" + fileName)
                if (file.exists()) {
                    deleted = file.delete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception:", e.cause)
            }
        }
        return deleted
    }

    fun clearAllData(context: Context) {
        Log.d(TAG, "Clear Prefs data" + context.javaClass.simpleName)
        removeFromPref(context, Constants.SharedPrefConstants.USER_ID)
        removeFromPref(context, Constants.SharedPrefConstants.USER_MOBILE_NO)
        removeFromPref(context, Constants.SharedPrefConstants.PASSWORD)
        removeFromPref(context, Constants.SharedPrefConstants.NAME)
        removeFromPref(context, Constants.SharedPrefConstants.HOSPITAL_NAME)
        removeFromPref(context, Constants.SharedPrefConstants.ROLE)
        removeFromPref(context, Constants.SharedPrefConstants.R_PROVIDER_TYPE)
        removeFromPref(context, Constants.SharedPrefConstants.FCM_TOKEN)
        removeFromPref(context, Constants.SharedPrefConstants.TOKEN)
        removeFromPref(context, Constants.SharedPrefConstants.PROVIDER_STATUS)
        removeFromPref(context, Constants.SharedPrefConstants.PROFILE_IMG_URL)
        removeFromPref(context, Constants.SharedPrefConstants.EMAIL)
        removeFromPref(context, Constants.SharedPrefConstants.APP_ACTIVE_TIME)
        removeFromPref(context, Constants.SharedPrefConstants.IS_ERROR)
        removeFromPref(context, Constants.SharedPrefConstants.PROVIDER_OBJECT)
        removeFromPref(context, Constants.SharedPrefConstants.FIREBASE_IDTOKEN)
        removeFromPref(context, Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN)
        removeFromPref(context, Constants.SharedPrefConstants.LOGIN_TIME)
    }

    fun clearRedirectValidation(context: Context) {
        removeFromPref(context, Constants.redirectValidation.EMAIL)
        removeFromPref(context, Constants.redirectValidation.PASSWORD)
    }

    fun saveUserData(context: Context, `object`: Any?) {
        if (`object` != null) {
            if (`object` is Provider) {
                val provider: Provider = `object` as Provider
                Log.e(TAG, "saveUserData:provider--> " + Gson().toJson(provider))
                provider.getId()?.let {
                    PrefUtility().saveLongInPref(
                        context,
                        Constants.SharedPrefConstants.USER_ID,
                        it
                    )
                }
                provider.getUserId()?.let {
                    PrefUtility().saveLongInPref(
                        context,
                        Constants.SharedPrefConstants.USER_ID_PRIMARY,
                        it
                    )
                }
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.USER_MOBILE_NO,
                    provider.getPhone()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.TOKEN,
                    provider.getToken()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.NAME,
                    provider.getName()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.FIRST_NAME,
                    provider.getFname()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.LAST_NAME,
                    provider.getLname()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.HOSPITAL_NAME,
                    provider.getHospital()
                )
                if (provider.getPassword() != null) {
                    PrefUtility().saveStringInPref(
                        context,
                        Constants.SharedPrefConstants.PASSWORD,
                        provider.getPassword()
                    )
                }
                if (provider.getHospitalId() != null) {
                    PrefUtility().saveLongInPref(
                        context,
                        Constants.SharedPrefConstants.HOSPITAL_ID,
                        provider.getHospitalId()!!
                    )
                }
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.ROLE,
                    provider.getRole()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.LCP_TYPE,
                    provider.getLcpType()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.PROVIDER_STATUS,
                    provider.getStatus()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.PROFILE_IMG_URL,
                    provider.getProfilePicUrl()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.EMAIL,
                    provider.getEmail()
                )
            } else if (`object` is Provider) {
                val provider: Provider =
                    `object` as Provider
                Log.e(TAG, "saveUserData:provider 2--> " + Gson().toJson(provider))
                provider.getId()?.let {
                    PrefUtility().saveLongInPref(
                        context,
                        Constants.SharedPrefConstants.USER_ID,
                        it
                    )
                }
                provider.getUserId()?.let {
                    PrefUtility().saveLongInPref(
                        context,
                        Constants.SharedPrefConstants.USER_ID_PRIMARY,
                        it
                    )
                }
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                    provider.getRemoteProviderType()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.USER_MOBILE_NO,
                    provider.getPhone()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.TOKEN,
                    provider.getToken()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.NAME,
                    provider.getName()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.FIRST_NAME,
                    provider.getFname()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.LAST_NAME,
                    provider.getLname()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.HOSPITAL_NAME,
                    provider.getHospital()
                )
                if (provider.getHospitalId() != null) {
                    PrefUtility().saveLongInPref(
                        context,
                        Constants.SharedPrefConstants.HOSPITAL_ID,
                        provider.getHospitalId()!!
                    )
                }
                if (provider.getPassword() != null) {
                    PrefUtility().saveStringInPref(
                        context,
                        Constants.SharedPrefConstants.PASSWORD,
                        provider.getPassword()
                    )
                }
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.LCP_TYPE,
                    provider.getLcpType()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.ROLE,
                    provider.getRole()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.PROVIDER_STATUS,
                    provider.getStatus()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.PROFILE_IMG_URL,
                    provider.getProfilePicUrl()
                )
                PrefUtility().saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.EMAIL,
                    provider.getEmail()
                )
            }
            saveProviderObject(context, `object`)
        }
    }

    /**
     * Method to save provider object in preference
     *
     * @param context
     * @param object
     */
    private fun saveProviderObject(context: Context, `object`: Any) {
        try {
            //{"countryCode":"+1","email":"alok@clicbrics.com","fcmKey":"dX1ou2IlSoKz3SsqVuJ7pG:APA91bGrZjb3m7KHJsgKpjMFRls02ee8biSaLNdi-w2ytIk8-ubAbKQ2BugK2aWKr-p2oUGhIcy-mVPvhwxAaFQmToSI5tl20S1JpAoXhVdKnwrrU4cRO_vJGsz-hmJl_0fm5fMP2ZL1","healthMonitoringTime":"1589180350211","hospital":"VA New York Harbor Healthcare System","id":"1","joiningTime":"1588162910953","name":"Alok Soni","osType":"ANDROID","otp":"3248","password":"Y+Mh4DqMlwUC5rbx3MgOyMfht6+a8K8a","phone":"8377944971","profilePicUrl":"https://firebasestorage.googleapis.com/v0/b/omnicure-backend.appspot.com/o/xUOUCcGncSf9MZDuCCf33HO6hHj2%2F1%2FProfile%2Fimage_1588856832221?alt=media&token=59092c1d-282a-487a-9ad0-7066080c3f6b","role":"BD","screenName":"LoginActivity","status":"Active","token":"441898667215891803502110318992105"}
            Log.d(TAG, "saveProviderObject:$`object`")
            //            String providerObj = new JsonParser().parse(object.toString()).toString();
            val providerObj = Gson().toJson(`object`)
            PrefUtility().saveStringInPref(
                context,
                Constants.SharedPrefConstants.PROVIDER_OBJECT,
                providerObj
            )
            Log.d(TAG, "getProviderObject:" + getProviderObject(context))
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    fun getProviderObject(context: Context?): Provider? {
        try {
            val providerStr: String? = context?.let {
                PrefUtility().getStringInPref(
                    it,
                    Constants.SharedPrefConstants.PROVIDER_OBJECT,
                    ""
                )
            }
            val jsonObject = JsonParser().parse(providerStr).asJsonObject
            val provider = Provider()
            if (jsonObject["countryCode"] != null && jsonObject["countryCode"].asString != null) {
                provider.setCountryCode(jsonObject["countryCode"].asString)
            }
            if (jsonObject["email"] != null && jsonObject["email"].asString != null) {
                provider.setEmail(jsonObject["email"].asString)
            }
            if (jsonObject["fcmKey"] != null && jsonObject["fcmKey"].asString != null) {
                provider.setFcmKey(jsonObject["fcmKey"].asString)
            }
            if (jsonObject["healthMonitoringTime"] != null) {
                provider.setHealthMonitoringTime(jsonObject["healthMonitoringTime"].asLong)
            }
            if (jsonObject["hospital"] != null && jsonObject["hospital"].asString != null) {
                provider.setHospital(jsonObject["hospital"].asString)
            }
            if (jsonObject["hospitalId"] != null && jsonObject["hospitalId"].asString != null) {
                provider.setHospitalId(jsonObject["hospitalId"].asLong)
            }
            if (jsonObject["id"] != null) {
                provider.setId(jsonObject["id"].asLong)
            }
            if (jsonObject["joiningTime"] != null) {
                provider.setJoiningTime(jsonObject["joiningTime"].asLong)
            }
            if (jsonObject["name"] != null && jsonObject["name"].asString != null) {
                provider.setName(jsonObject["name"].asString)
            }
            if (jsonObject["fname"] != null && jsonObject["fname"].asString != null) {
                provider.setFname(jsonObject["fname"].asString)
            }
            if (jsonObject["lname"] != null && jsonObject["lname"].asString != null) {
                provider.setLname(jsonObject["lname"].asString)
            }
            if (jsonObject["osType"] != null && jsonObject["osType"].asString != null) {
                provider.setOsType(jsonObject["osType"].asString)
            }
            if (jsonObject["otp"] != null && jsonObject["otp"].asString != null) {
                provider.setOtp(jsonObject["otp"].asString)
            }
            if (jsonObject["phone"] != null && jsonObject["phone"].asString != null) {
                provider.setPhone(jsonObject["phone"].asString)
            }
            if (jsonObject["profilePicUrl"] != null && jsonObject["profilePicUrl"].asString != null) {
                provider.setProfilePicUrl(jsonObject["profilePicUrl"].asString)
            }
            if (jsonObject["role"] != null && jsonObject["role"].asString != null) {
                provider.setRole(jsonObject["role"].asString)
            }
            if (jsonObject["status"] != null && jsonObject["status"].asString != null) {
                provider.setStatus(jsonObject["status"].asString)
            }
            if (jsonObject["token"] != null && jsonObject["token"].asString != null) {
                provider.setToken(jsonObject["token"].asString)
            }
            if (jsonObject["lcpType"] != null && jsonObject["lcpType"].asString != null) {
                provider.setLcpType(jsonObject["lcpType"].asString)
            }
            return provider
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "Exception:", e.cause)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Exception:", e.cause)
        }
        return null
    }

    fun getLoginTime(context: Context): Long {
        return getLongInPref(context, Constants.SharedPrefConstants.LOGIN_TIME, 0)
    }

    fun setLoginTime(context: Context?, time: Long) {
        context?.let { PrefUtility().saveLongInPref(it, Constants.SharedPrefConstants.LOGIN_TIME, time) }
    }

    fun getProviderId(context: Context): Long? {
        return getLongInPref(context, Constants.SharedPrefConstants.USER_ID, -1)
    }

    fun getUserId(context: Context): Long? {
        return getLongInPref(context, Constants.SharedPrefConstants.USER_ID_PRIMARY, -1)
    }

    fun getFireBaseUid(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.FIREBASE_UID, "")
    }

    fun getAgoraAppId(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.AGORA_APP_ID, "")
    }

    fun getAgoraCertificateId(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.AGORA_CERTIFICATE, "")
    }

    fun getAESKey(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.AES_KEY, "")
    }

    fun getAESAPIKey(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.AES_API_KEY, "")
    }


    fun getHeaderIdToken(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.FIREBASE_IDTOKEN, "")
    }

    fun getProviderStatus(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.PROVIDER_STATUS, "")
    }

    fun getProvderPhone(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.USER_MOBILE_NO, "")
    }

    fun getRole(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.ROLE, "")
    }

    fun getToken(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.TOKEN, "")
    }

    fun savePatientAcuityResetCounter(context: Context?, map: HashMap<Long?, Long?>?) {
        val str = Gson().toJson(map)
        context?.let {
            PrefUtility().saveStringInPref(it,
                Constants.SharedPrefConstants.RESET_ACUITY_PATIENT_MAP, str)
        }
    }

    fun getPatientAcuityResetCounter(context: Context): HashMap<Long, Long>? {
        try {
            val str = getStringInPref(
                context,
                Constants.SharedPrefConstants.RESET_ACUITY_PATIENT_MAP,
                ""
            )
            return Gson().fromJson(str, object : TypeToken<HashMap<Long?, Long?>?>() {}.type)
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
        return null
    }

    /*public static SharedPreferences getSharedPref(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            SharedPreferences sharedPreferences = EncryptedSharedPreferences
                    .create(
                            context,
                            "secret_shared_prefs_file",
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
            return sharedPreferences;
        } catch (Exception e) {
            Log.e(TAG, "Exception:", e.getCause());
        }
        return null;
    }

    public static void saveEncryptedStringInPref(Context context, String key, String value) {
        try {
            SharedPreferences prefs = getSharedPref(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    */
    /**
     * This method returns the string value stored in Application prefs against a key
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     *//*
    public static String getEncryptedStringInPref(Context context, String key, String defaultValue) {
        try {
            SharedPreferences prefs = getSharedPref(context);
            return prefs.getString(key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG, "Exception:", e.getCause());
        }
        return "";
    }*/
}
