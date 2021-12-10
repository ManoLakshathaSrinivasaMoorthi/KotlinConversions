package com.example.kotlinomnicure

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.example.kotlinomnicure.helper.AppStatusHelper

import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.videocall.openvcall.model.AGEventHandler
import com.example.kotlinomnicure.videocall.openvcall.model.CurrentUserSettings
import com.example.kotlinomnicure.videocall.openvcall.model.EngineConfig
import com.example.kotlinomnicure.videocall.openvcall.model.MyEngineEventHandler
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.agora.rtc.Constants

import io.agora.rtc.RtcEngine
import org.slf4j.LoggerFactory
import java.util.*

class OmnicureApp : Application() {
    private val mVideoSettings: CurrentUserSettings = CurrentUserSettings()
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var mRtcEngine: RtcEngine? = null
    private var mConfig: EngineConfig? = null
    private var mEventHandler: MyEngineEventHandler? = null
    private var currentContext: Context? = null
    private var isAppInBackground = false
    var crashlytics: FirebaseCrashlytics? = null
    protected var context: Context? = null

    //    private Context context;
    // For Locale
    private var changeLanguage = ""

    fun rtcEngine(): RtcEngine? {
        return mRtcEngine
    }

    fun config(): EngineConfig? {
        return mConfig
    }

    fun userSettings(): CurrentUserSettings? {
        return mVideoSettings
    }

    fun addEventHandler(handler: AGEventHandler?) {
        handler?.let { mEventHandler?.addEventHandler(it) }
    }

    fun remoteEventHandler(handler: AGEventHandler?) {
        handler?.let { mEventHandler?.removeEventHandler(it) }
    }

    override fun onCreate() {
        super.onCreate()
        //        Fabric.with(this, new Crashlytics());
        context = applicationContext
        crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics?.sendUnsentReports()
        FirebaseApp.initializeApp(this)
        //        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //FirebaseDatabase.getInstance().sxetLogLevel(com.google.firebase.database.Logger.Level.DEBUG);
        registerActivityLifecycleCallbacks(AppStatusHelper())
        createRtcEngine()

        // If changeLanguage is null, Save english as a default locale in shared preference
        if (PrefUtility().getStringInPref(this,
                com.example.kotlinomnicure.utils.Constants.SharedPrefConstants.CHANGE_LANGUAGE,"") == null ||
            PrefUtility().getStringInPref(this,
                com.example.kotlinomnicure.utils.Constants.SharedPrefConstants.CHANGE_LANGUAGE, "")?.isEmpty() == true
        ) {
            PrefUtility().saveStringInPref(this,
                com.example.kotlinomnicure.utils.Constants.SharedPrefConstants.CHANGE_LANGUAGE, "en")
        }
        // Getting the locale of the user from the shared preference
        changeLanguage = PrefUtility().getStringInPref(this,
            com.example.kotlinomnicure.utils.Constants.SharedPrefConstants.CHANGE_LANGUAGE,
            "").toString()

        // Changing locale of the app when user changed the language
        setLocale(changeLanguage)
    }

    fun createRtcEngine() {
        val context = applicationContext

        val appId: String? = PrefUtility().getAgoraAppId(context)?.trim()
        //        System.out.println("agora_appid "+appId+" "+appId.length());
        if (TextUtils.isEmpty(appId)) {
            return
        }
        mEventHandler = MyEngineEventHandler()
        try {
            mRtcEngine = RtcEngine.create(context, appId, mEventHandler)
            mEventHandler!!.setEngine(applicationContext, mRtcEngine)
        } catch (e: Exception) {
            log.error(Log.getStackTraceString(e))
            throw RuntimeException(
                """
                NEED TO check rtc sdk init fatal error
                ${Log.getStackTraceString(e)}
                """.trimIndent()
            )
        }
        mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        mRtcEngine?.enableVideo()
        mRtcEngine?.enableAudioVolumeIndication(200, 3, false)
        mConfig = EngineConfig()
    }

    fun getCurrentContext(): Context? {
        return currentContext
    }

    fun setCurrentContext(context: Context?) {
        currentContext = context
    }

    fun getAppContext(): Context? {
        return context
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    fun isAppInBackground(): Boolean {
        return isAppInBackground
    }

    fun setAppInBackground(appInBackground: Boolean) {
        isAppInBackground = appInBackground
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        Log.e(TAG, "onConfigurationChanged: "+newConfig);
    }


    /**
     * Changing local of the app when user changed the language
     * @param lang
     */
    private fun setLocale(lang: String) {
        val resources = resources
        val configuration = resources.configuration
        val dm = resources.displayMetrics
        val locale = Locale(lang)
        //        Log.e(TAG, "setLocale: "+locale );
//        Log.e(TAG, "conf.locale: "+configuration.locale );
        if (configuration.locale != locale) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale)
            }
            resources.updateConfiguration(configuration, dm)
        }
    }
}
