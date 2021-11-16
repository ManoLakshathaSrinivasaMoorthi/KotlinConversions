package com.example.kotlinomnicure.utils

import android.content.Context
import android.util.Log
import java.io.*
import java.util.*

class PrefUtility {
    private val TAG = PrefUtility::class.java.simpleName

    private fun removeFromPref(context: Context, key: String?) {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove(key)
        editor.apply()
    }

    /**
     * This method save  a String in Application prefs

     */
    fun saveStringInPref(context: Context, key: String?, value: String?) {
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
            //saveEncryptedStringInPref(context,key,value);
        } catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
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
        var defaultSetValue = defaultSetValue
        defaultSetValue = HashSet()
        try {
            val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            return prefs.getStringSet(key, defaultSetValue)
        } catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
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
            try {
                if (os != null) {
                    os.flush()
                    os.close()
                }
                if (fos != null) {
                    fos.flush()
                    fos.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
        } finally {
            try {
                fis?.close()
                `is`?.close()
            } catch (e: Exception) {
                e.printStackTrace()
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
                e.printStackTrace()
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
    }

    fun clearRedirectValidation(context: Context) {
        removeFromPref(context, Constants.redirectValidation.EMAIL)
        removeFromPref(context, Constants.redirectValidation.PASSWORD)
    }

   /* fun saveUserData(context: Context, `object`: Any?) {
        if (`object` != null) {
            if (`object` is Provider) {
                val provider: Provider = `object` as Provider
                PrefUtility.saveLongInPref(
                    context,
                    Constants.SharedPrefConstants.USER_ID,
                    provider.getId()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.USER_MOBILE_NO,
                    provider.getPhone()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.TOKEN,
                    provider.getToken()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.NAME,
                    provider.getName()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.FIRST_NAME,
                    provider.getFname()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.LAST_NAME,
                    provider.getLname()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.HOSPITAL_NAME,
                    provider.getHospital()
                )
                if (provider.getPassword() != null) {
                    PrefUtility.saveStringInPref(
                        context,
                        Constants.SharedPrefConstants.PASSWORD,
                        provider.getPassword()
                    )
                }
                if (provider.getHospitalId() != null) {
                    PrefUtility.saveLongInPref(
                        context,
                        Constants.SharedPrefConstants.HOSPITAL_ID,
                        provider.getHospitalId()
                    )
                }
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.ROLE,
                    provider.getRole()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.LCP_TYPE,
                    provider.getLcpType()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.PROVIDER_STATUS,
                    provider.getStatus()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.PROFILE_IMG_URL,
                    provider.getProfilePicUrl()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.EMAIL,
                    provider.getEmail()
                )
            } else if (`object` is omnicure.mvp.com.loginEndpoints.model.Provider) {
                val provider: omnicure.mvp.com.loginEndpoints.model.Provider =
                    `object` as omnicure.mvp.com.loginEndpoints.model.Provider
                PrefUtility.saveLongInPref(
                    context,
                    Constants.SharedPrefConstants.USER_ID,
                    provider.getId()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                    provider.getRemoteProviderType()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.USER_MOBILE_NO,
                    provider.getPhone()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.TOKEN,
                    provider.getToken()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.NAME,
                    provider.getName()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.FIRST_NAME,
                    provider.getFname()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.LAST_NAME,
                    provider.getLname()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.HOSPITAL_NAME,
                    provider.getHospital()
                )
                if (provider.getHospitalId() != null) {
                    PrefUtility.saveLongInPref(
                        context,
                        Constants.SharedPrefConstants.HOSPITAL_ID,
                        provider.getHospitalId()
                    )
                }
                if (provider.getPassword() != null) {
                    PrefUtility.saveStringInPref(
                        context,
                        Constants.SharedPrefConstants.PASSWORD,
                        provider.getPassword()
                    )
                }
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.LCP_TYPE,
                    provider.getLcpType()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.ROLE,
                    provider.getRole()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.PROVIDER_STATUS,
                    provider.getStatus()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.PROFILE_IMG_URL,
                    provider.getProfilePicUrl()
                )
                PrefUtility.saveStringInPref(
                    context,
                    Constants.SharedPrefConstants.EMAIL,
                    provider.getEmail()
                )
            }
           // saveProviderObject(context, `object`)
        }
    }*/

    /**
     * Method to save provider object in preference
     *
     * @param context
     * @param object
     */
   /* private fun saveProviderObject(context: Context, `object`: Any) {
        try {
            //{"countryCode":"+1","email":"alok@clicbrics.com","fcmKey":"dX1ou2IlSoKz3SsqVuJ7pG:APA91bGrZjb3m7KHJsgKpjMFRls02ee8biSaLNdi-w2ytIk8-ubAbKQ2BugK2aWKr-p2oUGhIcy-mVPvhwxAaFQmToSI5tl20S1JpAoXhVdKnwrrU4cRO_vJGsz-hmJl_0fm5fMP2ZL1","healthMonitoringTime":"1589180350211","hospital":"VA New York Harbor Healthcare System","id":"1","joiningTime":"1588162910953","name":"Alok Soni","osType":"ANDROID","otp":"3248","password":"Y+Mh4DqMlwUC5rbx3MgOyMfht6+a8K8a","phone":"8377944971","profilePicUrl":"https://firebasestorage.googleapis.com/v0/b/omnicure-backend.appspot.com/o/xUOUCcGncSf9MZDuCCf33HO6hHj2%2F1%2FProfile%2Fimage_1588856832221?alt=media&token=59092c1d-282a-487a-9ad0-7066080c3f6b","role":"BD","screenName":"LoginActivity","status":"Active","token":"441898667215891803502110318992105"}
            Log.d(TAG, "saveProviderObject:$`object`")
            val providerObj: String = JsonParser().parse(`object`.toString()).toString()
            PrefUtility.saveStringInPref(
                context,
                Constants.SharedPrefConstants.PROVIDER_OBJECT,
                providerObj
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getProviderObject(context: Context?): Provider? {
        try {
            val providerStr: String = PrefUtility.getStringInPref(
                context,
                Constants.SharedPrefConstants.PROVIDER_OBJECT,
                ""
            )
            val jsonObject: JsonObject = JsonParser().parse(providerStr).getAsJsonObject()
            val provider = Provider()
            if (jsonObject.get("countryCode") != null && jsonObject.get("countryCode")
                    .getAsString() != null
            ) {
                provider.setCountryCode(jsonObject.get("countryCode").getAsString())
            }
            if (jsonObject.get("email") != null && jsonObject.get("email").getAsString() != null) {
                provider.setEmail(jsonObject.get("email").getAsString())
            }
            if (jsonObject.get("fcmKey") != null && jsonObject.get("fcmKey")
                    .getAsString() != null
            ) {
                provider.setFcmKey(jsonObject.get("fcmKey").getAsString())
            }
            if (jsonObject.get("healthMonitoringTime") != null) {
                provider.setHealthMonitoringTime(jsonObject.get("healthMonitoringTime").getAsLong())
            }
            if (jsonObject.get("hospital") != null && jsonObject.get("hospital")
                    .getAsString() != null
            ) {
                provider.setHospital(jsonObject.get("hospital").getAsString())
            }
            if (jsonObject.get("hospitalId") != null && jsonObject.get("hospitalId")
                    .getAsString() != null
            ) {
                provider.setHospitalId(jsonObject.get("hospitalId").getAsLong())
            }
            if (jsonObject.get("id") != null) {
                provider.setId(jsonObject.get("id").getAsLong())
            }
            if (jsonObject.get("joiningTime") != null) {
                provider.setJoiningTime(jsonObject.get("joiningTime").getAsLong())
            }
            if (jsonObject.get("name") != null && jsonObject.get("name").getAsString() != null) {
                provider.setName(jsonObject.get("name").getAsString())
            }
            if (jsonObject.get("fname") != null && jsonObject.get("fname").getAsString() != null) {
                provider.setFname(jsonObject.get("fname").getAsString())
            }
            if (jsonObject.get("lname") != null && jsonObject.get("lname").getAsString() != null) {
                provider.setLname(jsonObject.get("lname").getAsString())
            }
            if (jsonObject.get("osType") != null && jsonObject.get("osType")
                    .getAsString() != null
            ) {
                provider.setOsType(jsonObject.get("osType").getAsString())
            }
            if (jsonObject.get("otp") != null && jsonObject.get("otp").getAsString() != null) {
                provider.setOtp(jsonObject.get("otp").getAsString())
            }
            if (jsonObject.get("phone") != null && jsonObject.get("phone").getAsString() != null) {
                provider.setPhone(jsonObject.get("phone").getAsString())
            }
            if (jsonObject.get("profilePicUrl") != null && jsonObject.get("profilePicUrl")
                    .getAsString() != null
            ) {
                provider.setProfilePicUrl(jsonObject.get("profilePicUrl").getAsString())
            }
            if (jsonObject.get("role") != null && jsonObject.get("role").getAsString() != null) {
                provider.setRole(jsonObject.get("role").getAsString())
            }
            if (jsonObject.get("status") != null && jsonObject.get("status")
                    .getAsString() != null
            ) {
                provider.setStatus(jsonObject.get("status").getAsString())
            }
            if (jsonObject.get("token") != null && jsonObject.get("token").getAsString() != null) {
                provider.setToken(jsonObject.get("token").getAsString())
            }
            if (jsonObject.get("lcpType") != null && jsonObject.get("lcpType")
                    .getAsString() != null
            ) {
                provider.setLcpType(jsonObject.get("lcpType").getAsString())
            }
            return provider
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        return null
    }
*/
    fun getProviderId(context: Context): Long? {
        return getLongInPref(context, Constants.SharedPrefConstants.USER_ID, -1)
    }

    fun getFireBaseUid(context: Context): String? {
        return getStringInPref(context, Constants.SharedPrefConstants.FIREBASE_UID, "")
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
/*

    fun savePatientAcuityResetCounter(context: Context?, map: HashMap<Long?, Long?>?) {
        val str: String = Gson().toJson(map)
        PrefUtility.saveStringInPref(
            context,
            Constants.SharedPrefConstants.RESET_ACUITY_PATIENT_MAP,
            str
        )
    }

    fun getPatientAcuityResetCounter(context: Context): HashMap<Long?, Long?>? {
        try {
            val str =
                getStringInPref(context, Constants.SharedPrefConstants.RESET_ACUITY_PATIENT_MAP, "")
            return Gson().fromJson(str, object : TypeToken<HashMap<Long?, Long?>?>() {}.getType())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
*/

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    */
    /**
     * This method returns the string value stored in Application prefs against a key

     *//*
    public static String getEncryptedStringInPref(Context context, String key, String defaultValue) {
        try {
            SharedPreferences prefs = getSharedPref(context);
            return prefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }*/
}



