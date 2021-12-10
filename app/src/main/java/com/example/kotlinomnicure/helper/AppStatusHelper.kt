package com.example.kotlinomnicure.helper

import android.app.Activity
import android.app.ActivityManager
import android.app.Application.ActivityLifecycleCallbacks
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import com.example.kotlinomnicure.OmnicureApp
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.*
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods

import com.example.kotlinomnicure.activity.ActivityConsultChartRemote
import com.example.kotlinomnicure.model.HealthMonitoring
import com.example.kotlinomnicure.utils.AESUtils
import com.mvp.omnicure.kotlinactivity.requestbodys.HealthMonitorEventRequestBody

import omnicurekotlin.example.com.loginEndpoints.model.LoginRequest
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalStateException
import java.net.SocketTimeoutException
import java.util.*

class AppStatusHelper{
  /*  private val TAG = AppStatusHelper::class.java.simpleName
    private var isOpen = false
    private var isClose = false
    private var isfirsttime = true
    private var resumed = 0
    private var paused = 0
    private var started = 0
    private var stopped = 0
    private var hmTimer: Timer? = null
    private var hmTimerTask: TimerTask? = null
    private var isTimerStarted = false
    private var currentActivity: Activity? = null
    private var authpopupstatus = false

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
//        Log.i(TAG, "onActivityCreated: " + activity.getClass().getSimpleName());
        currentActivity = activity
        (activity as? MyDashboardActivity)?.let { onAppForeground(it) }
    }

    override fun onActivityStarted(activity: Activity) {
        *//*if(isDiscarded(activity)){
            return;
        }*//*

//        Log.d(TAG, "Activity Started");
        val isAutoLogout = isLogoutNeeded(activity)
        //        boolean isInternetConnected = UtilityMethods.isInternetConnected(activity);
        val isInternetConnected = true
        currentActivity = activity
        ++started
        //        Log.i(TAG, "onActivityStarted: Name " + activity.getClass().getSimpleName());
        val myKM = activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    }

    override fun onActivityResumed(activity: Activity) {

        currentActivity = activity
        ++resumed
        //        Log.e(TAG, "onActivityResumed " + resumed + " " + paused + "isOpen "+isOpen + "isClose "+isClose +"isfirsttime "+isfirsttime);
        var blur = false
        if (resumed > paused) {
            if (isOpen && !isClose) {
                isOpen = false
                //                Log.i(TAG, "Application in Foreground__");
                blur = true
                onAppForeground(activity)
                blurIt(activity, blur)
            } else if (isfirsttime) {
                isfirsttime = false
                //                Log.i(TAG, "Application in Foreground");
                onAppForeground(activity)
                blurIt(activity, blur)
            }
            if (activity !is NotificationActivity && activity !is LoginActivity) {
                authpopupstatus = true
                sendHealthMonitoring(activity, blur)
                // sessionInvalidate(activity);
            }
            isClose = true
        }
//        Log.i(TAG, "onActivityResumed: Name " + activity.getClass().getSimpleName());
    }

    override fun onActivityPaused(activity: Activity) {
            *//*if(isDiscarded(activity)){
                return;
            }*//*
            ++paused
//        Log.i(TAG, "onActivityPaused: Name " + activity.getClass().getSimpleName());
        }




    override fun onActivityStopped(activity: Activity) {
        *//*if(isDiscarded(activity)){
            return;
        }*//*
        ++stopped
        if (started == stopped && isClose) {
            isOpen = true
            isClose = false
            onAppBackground(activity)
            //            Log.i(TAG, "Application in Background");
        }
//        Log.i(TAG, "onActivityStopped: Name " + activity.getClass().getSimpleName());
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
//        Log.i(TAG, "onActivityDestroyed: Name " + activity.getClass().getSimpleName());
            if (activity is HomeActivity) {
                stopTimer()
            }
        }




    private fun onAppForeground(activity: Activity) {
        (activity.applicationContext as OmnicureApp).setAppInBackground(false)
        val isAutoLogout = isLogoutNeeded(activity)
        //        boolean isInternetConnected = UtilityMethods.isInternetConnected(activity);
        val isInternetConnected = true
        *//*if (isDiscarded(activity)) {
            return
        }*//*
        val userID: Long? = PrefUtility().getProviderId(activity)
        if (userID == -1L) {
//            Log.i(TAG, "onAppForeground: No health Monitor timer as user not sign-in");
            return
        }
       startTimer(activity)

//        }
    }

    private fun onAppBackground(activity: Activity) {
        (activity.applicationContext as OmnicureApp).setAppInBackground(true)
       *//* if (isDiscarded(activity)) {
            return
        }*//*
        stopTimer()
    }



    private fun startTimer(activity: Activity) {
        if (isTimerStarted) {

            stopTimer()
            //            return;
        }
        //        stopTimer();
        hmTimer = Timer()
        val repeatInterval: Long = PrefUtility().getLongInPref(
            activity,
            Constants.SharedPrefConstants.HEALTH_MONITOR_TIMER,
            -1)
        val delay = getTimerDelayTime(activity, repeatInterval)

        initializeTimerTask(activity)

        if (repeatInterval >= 0) {
            hmTimer!!.scheduleAtFixedRate(hmTimerTask, delay, repeatInterval)
        }
        isTimerStarted = true
    }

    private fun stopTimer() {
//        Log.i(TAG, "stopTimer ");
        try {
            if (hmTimer != null) {
                hmTimer!!.cancel()
                hmTimer!!.purge()
                hmTimer = null
            }
            if (hmTimerTask != null) {
                hmTimerTask!!.cancel()
                hmTimerTask = null
            }
            isTimerStarted = false
        } catch (e: Exception) {

        }
    }

    private fun initializeTimerTask(activity: Activity) {
        hmTimerTask = object : TimerTask() {
            var counter = 0
            override fun run() {

                if (authpopupstatus) {
                    authpopupstatus = false
                } else {
                    sendHealthMonitoring(activity, false)
                }


            }
        }
    }

    *//**
     * Method check the active time of user and return weather user logout needed or not
     *
     * @param activity - acitivity instance
     * @return : true - if Logout needed(user is inactive for 30 minutes or more, else false
     *//*
    private fun isLogoutNeeded(activity: Activity): Boolean {

        return false
    }

    private fun getTimerDelayTime(activity: Activity, repeatInterval: Long): Long {
        val appActiveTime: Long =
            PrefUtility().getLongInPref(activity, Constants.SharedPrefConstants.APP_ACTIVE_TIME, -1)
        var delay: Long
        delay = if (appActiveTime == -1L) {
            0
        } else {
            repeatInterval - (System.currentTimeMillis() - appActiveTime)
        }
        if (delay < 0 || delay > repeatInterval) {

            delay = 0
        }
        return delay
    }

    private fun blurIt(activity: Activity, blurFlag: Boolean) {
        if (activity is SplashActivity) {
            return
        }
        val isError: Boolean =
            PrefUtility().getBooleanInPref(activity, Constants.SharedPrefConstants.IS_ERROR, false)

        if (hasToBlur(activity) && (blurFlag || isError)) {
            activity.runOnUiThread {
                activity.findViewById<View>(R.id.blurView).visibility =
                    View.VISIBLE
            }
        }
    }

    private fun sendHealthMonitoring(activity: Activity, blurFlag: Boolean) {

        Thread {}.start()


        val id: Long? = PrefUtility().getProviderId(activity)
        val token: String? = PrefUtility().getToken(activity)
        if (id == -1L) {

            return
        }
        if (!UtilityMethods().isInternetConnected(activity)!!) {
            return
        }
        val errorMsg = arrayOfNulls<String>(1)
        ApiClient().getApiProviderEndpoints(true, true)?.sendHealthMonitorEvent(HealthMonitorEventRequestBody(
                currentActivity!!.javaClass.simpleName, token, id.toString()))
           ?.enqueue(object : Callback<HealthMonitoring?> {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onResponse(
                    call: Call<HealthMonitoring?>,
                    response: Response<HealthMonitoring?>) {
//
                    if (response.code() == 703) {
                        refreshToken(activity)
                    }
                    if (response.isSuccessful()) {
                        val commonResponse: HealthMonitoring? = response.body()

                        if (commonResponse != null && TextUtils.isEmpty(commonResponse.getErrorMessage())) {
//
                            if (commonResponse.getStatus()) {
                                val time: Long = PrefUtility().getLongInPref(activity,
                                    Constants.SharedPrefConstants.APP_ACTIVE_TIME, 0)
                                val autoLogOutTIme: Long = PrefUtility().getLongInPref(activity,
                                    Constants.SharedPrefConstants.AUTO_LOGOUT_TIME, 30)

//

                                // Update the global notification enable/disable flag for acuity IAP notification
                                if (commonResponse.getNotificationRequests() != null && commonResponse.getNotificationRequests()!!
                                        .get(0) != null
                                ) {
//                                        Log.d(TAG, "SendHealthMonitorEvent: getnotificationEnabled-->" + commonResponse.getNotificationRequests().get(0).getNotificationEnabled());
//                                        Log.d(TAG, "SendHealthMonitorEvent: getnotificationAcuity-->" + commonResponse.getNotificationRequests().get(0).getAcuity().replace("[", "").replace("]", "").replace("\"", ""));
                                    // Saving the current status of Acuity score & notification enable/disabling in shared preference dynamically from backend
                                    currentActivity?.let {
                                        PrefUtility().saveStringInPref(it, Constants.SharedPrefConstants.ALERT_ACUITY,
                                            java.lang.String.valueOf(
                                                commonResponse.getNotificationRequests()!!.get(0)
                                                .getAcuity().replace("[", "").replace("]", "").replace("\"", "")))
                                    }
                                    currentActivity?.let {
                                        PrefUtility().saveStringInPref(it, Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                                            java.lang.String.valueOf(
                                                commonResponse.getNotificationRequests()!!.get(0)
                                                    .getNotificationEnabled()))
                                    }
                                }
                                PrefUtility().saveLongInPref(
                                    currentActivity!!, Constants.SharedPrefConstants.APP_ACTIVE_TIME,
                                    System.currentTimeMillis())
                                PrefUtility().saveBooleanInPref(currentActivity!!, Constants.SharedPrefConstants.IS_ERROR, false)

                                if (hasToBlur(activity)) {
                                    activity.runOnUiThread {
                                        if (activity.findViewById<View>(R.id.blurView).visibility == View.VISIBLE) {
                                            activity.findViewById<View>(R.id.blurView).visibility =
                                                View.GONE
                                        }
                                    }
                                }
                            } else {

//
                                if (activity is SplashActivity) {
                                    currentActivity?.let {
                                        PrefUtility().saveBooleanInPref(it, Constants.SharedPrefConstants.IS_ERROR, true)
                                    }
                                    return
                                }
                                currentActivity?.let {
                                    PrefUtility().saveBooleanInPref(it, Constants.SharedPrefConstants.IS_ERROR, true)
                                }
                                Handler(Looper.getMainLooper()).post {
                                    if (commonResponse.getErrorId() !== 104) {
                                        val topic: String? = UtilityMethods().getFCMTopic()
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                                            .addOnCompleteListener {
                                                                                                     }
                                            }
                                    }
                                    var title = "Error"
                                    if (commonResponse.getTitle() != null) {
                                        title = commonResponse.getTitle()!!
                                    }

//                                        Log.d(TAG, "commonResponse.getErrorId() : " + commonResponse.getErrorId());
                                    if (commonResponse.getErrorId() === 0) {
                                        if (currentActivity is NotificationActivity) {
                                            (currentActivity as NotificationActivity?).stopFP()
                                            currentActivity!!.finish()
                                        }
                                        PrefUtility().saveStringInPref(
                                            currentActivity!!,
                                            Constants.SharedPrefConstants.SESSION_TIMEOUT_MESSAGE,
                                            commonResponse.getErrorMessage()
                                        )
                                        PrefUtility().saveStringInPref(
                                            currentActivity!!,
                                            Constants.SharedPrefConstants.SESSION_TIMEOUT_TITLE,
                                            title
                                        )
                                        //                                            Log.i(TAG, "Auto Logout Health Monitor");
                                        doAutoLogout(activity)
                                    } else if (commonResponse.getErrorId() === 101) {
                                        val dialogIntent =
                                            Intent(activity, NotificationActivity::class.java)
                                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.denied_user));
                                        dialogIntent.putExtra("remoteTitle", title)
                                        dialogIntent.putExtra(
                                            "remoteMessage",
                                            commonResponse.getErrorMessage()
                                        )
                                        dialogIntent.putExtra(
                                            "messageType",
                                            Constants.FCMMessageType.DENIED_USER
                                        )
                                        activity.startActivity(dialogIntent)

//                                    ((BaseActivity) activity).providerDeniedPopup(commonResponse.getErrorMessage(), activity.getString(R.string.denied_user));
                                    } else if (commonResponse.getErrorId() === 102) {
                                        if (currentActivity is NotificationActivity) {
                                            (currentActivity as NotificationActivity?).stopFP()
                                            currentActivity!!.finish()
                                        }
                                        val dialogIntent =
                                            Intent(activity, NotificationActivity::class.java)
                                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.login_exceeded));
                                        dialogIntent.putExtra("remoteTitle", title)
                                        dialogIntent.putExtra(
                                            "remoteMessage",
                                            commonResponse.getErrorMessage()
                                        )
                                        dialogIntent.putExtra(
                                            "messageType",
                                            Constants.FCMMessageType.LOGOUT_EXCEEDED
                                        )
                                        activity.startActivity(dialogIntent)

//                                    ((BaseActivity) activity).providerDeniedPopup(commonResponse.getErrorMessage(), activity.getString(R.string.login_exceeded));
                                    } else if (commonResponse.getErrorId() === 103) {
                                        if (currentActivity is LoginActivity) {
                                           // return@post
                                        }
                                        val dialogIntent =
                                            Intent(activity, NotificationActivity::class.java)
                                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.user_locked_title));
                                        dialogIntent.putExtra("remoteTitle", title)
                                        dialogIntent.putExtra(
                                            "remoteMessage",
                                            commonResponse.getErrorMessage()
                                        )
                                        dialogIntent.putExtra(
                                            "messageType",
                                            Constants.FCMMessageType.LOCKED_USER
                                        )
                                        activity.startActivity(dialogIntent)

//                                    ((BaseActivity) activity).providerDeniedPopup(commonResponse.getErrorMessage(), activity.getString(R.string.login_exceeded));
                                    } else if (commonResponse.getErrorId() === 104) {
                                       *//* if (!getTopActivity()?.contains("NotificationActivity")!! && !getTopActivity()?.contains(
                                                "CallActivity") && !getTopActivity()?.contains("RingingActivity")!!
                                        ) {
                                            val dialogIntent =
                                                Intent(activity, NotificationActivity::class.java)
                                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            // dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.user_locked_title));
                                            dialogIntent.putExtra("remoteTitle", title)
                                            dialogIntent.putExtra(
                                                "remoteMessage",
                                                commonResponse.getErrorMessage()
                                            )
                                            dialogIntent.putExtra(
                                                "messageType",
                                                Constants.FCMMessageType.PASSWORD_LOCK
                                            )
                                            activity.startActivity(dialogIntent)
                                        }*//*

                                    }
                                }
                            }
                         else if (commonResponse != null && !TextUtils.isEmpty(commonResponse.getErrorMessage())) {
//
                            if (commonResponse.getStatus()) {
                                val time: Long = PrefUtility().getLongInPref(
                                    activity,
                                    Constants.SharedPrefConstants.APP_ACTIVE_TIME,
                                    0
                                )
                                val autoLogOutTIme: Long = PrefUtility().getLongInPref(
                                    activity,
                                    Constants.SharedPrefConstants.AUTO_LOGOUT_TIME,
                                    30)

//

                                // Update the global notification enable/disable flag for acuity IAP notification
                                if (commonResponse.getNotificationRequests() != null && commonResponse.getNotificationRequests()!!
                                        .get(0) != null) {
//
                                 *//*   PrefUtility().saveStringInPref(
                                        currentActivity!!,
                                        Constants.SharedPrefConstants.ALERT_ACUITY,
                                        java.lang.String.valueOf(
                                            commonResponse.getNotificationRequests()!!.get(0)
                                                .getAcuity().replace("[", "").replace("]", "")
                                                .replace("\"", "")
                                        )
                                    )
                                    PrefUtility().saveStringInPref(
                                        currentActivity!!,
                                        Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                                        java.lang.String.valueOf(
                                            commonResponse.getNotificationRequests()!!.get(0)
                                                .getNotificationEnabled()
                                        )
                                    )*//*
                                }
                                PrefUtility().saveLongInPref(
                                    currentActivity!!,
                                    Constants.SharedPrefConstants.APP_ACTIVE_TIME,
                                    System.currentTimeMillis()
                                )
                                PrefUtility().saveBooleanInPref(
                                    currentActivity!!,
                                    Constants.SharedPrefConstants.IS_ERROR,
                                    false)

                              *//*  if (hasToBlur(activity)) {
                                    activity.runOnUiThread {
                                        if (activity.findViewById<View>(R.id.blurView).visibility == View.VISIBLE) {
                                            activity.findViewById<View>(R.id.blurView).visibility =
                                                View.GONE
                                        }
                                    }
                                }
                            } else {

                                if (activity is SplashActivity) {
                                    PrefUtility().saveBooleanInPref(
                                        currentActivity!!,
                                        Constants.SharedPrefConstants.IS_ERROR,
                                        true)
                                    return
                                }*//*
                                PrefUtility().saveBooleanInPref(
                                    currentActivity!!,
                                    Constants.SharedPrefConstants.IS_ERROR,
                                    true
                                )
                                Handler(Looper.getMainLooper()).post {
                                    if (commonResponse.getErrorId() !== 104) {
                                        val topic: String? = UtilityMethods().getFCMTopic()
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                                            .addOnCompleteListener {

                                            }
                                    }
                                    var title = "Error"
                                    if (commonResponse.getTitle() != null) {
                                        title = commonResponse.getTitle()!!
                                    }

//                                        Log.d(TAG, "commonResponse.getErrorId() : " + commonResponse.getErrorId());
                                    if (commonResponse.getErrorId() === 0) {
                                        if (currentActivity is NotificationActivity) {
                                            (currentActivity as NotificationActivity?).stopFP()
                                            currentActivity!!.finish()
                                        }
                                        currentActivity?.let {
                                            PrefUtility().saveStringInPref(
                                                it,
                                                Constants.SharedPrefConstants.SESSION_TIMEOUT_MESSAGE,
                                                commonResponse.getErrorMessage()
                                            )
                                        }
                                        currentActivity?.let {
                                            PrefUtility().saveStringInPref(
                                                it,
                                                Constants.SharedPrefConstants.SESSION_TIMEOUT_TITLE,
                                                title
                                            )
                                        }
                                        //                                            Log.i(TAG, "Auto Logout Health Monitor");
                                        doAutoLogout(activity)
                                    } else if (commonResponse.getErrorId() === 101) {
                                        val dialogIntent =
                                            Intent(activity, NotificationActivity::class.java)
                                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.denied_user));
                                        dialogIntent.putExtra("remoteTitle", title)
                                        dialogIntent.putExtra(
                                            "remoteMessage",
                                            commonResponse.getErrorMessage()
                                        )
                                        dialogIntent.putExtra(
                                            "messageType",
                                            Constants.FCMMessageType.DENIED_USER
                                        )
                                        activity.startActivity(dialogIntent)

//                                    ((BaseActivity) activity).providerDeniedPopup(commonResponse.getErrorMessage(), activity.getString(R.string.denied_user));
                                    } else if (commonResponse.getErrorId() === 102) {
                                        if (currentActivity is NotificationActivity) {
                                            (currentActivity as NotificationActivity?)?.stopFP()
                                            currentActivity!!.finish()
                                        }
                                        val dialogIntent =
                                            Intent(activity, NotificationActivity::class.java)
                                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.login_exceeded));
                                        dialogIntent.putExtra("remoteTitle", title)
                                        dialogIntent.putExtra(
                                            "remoteMessage",
                                            commonResponse.getErrorMessage()
                                        )
                                        dialogIntent.putExtra(
                                            "messageType",
                                            Constants.FCMMessageType.LOGOUT_EXCEEDED
                                        )
                                        activity.startActivity(dialogIntent)

//                                    ((BaseActivity) activity).providerDeniedPopup(commonResponse.getErrorMessage(), activity.getString(R.string.login_exceeded));
                                    } else if (commonResponse.getErrorId() === 103) {
                                        if (currentActivity is LoginActivity) {
                                            return@post
                                        }
                                        val dialogIntent =
                                            Intent(activity, NotificationActivity::class.java)
                                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.user_locked_title));
                                        dialogIntent.putExtra("remoteTitle", title)
                                        dialogIntent.putExtra(
                                            "remoteMessage",
                                            commonResponse.getErrorMessage()
                                        )
                                        dialogIntent.putExtra(
                                            "messageType",
                                            Constants.FCMMessageType.LOCKED_USER
                                        )
                                        activity.startActivity(dialogIntent)

//                                    ((BaseActivity) activity).providerDeniedPopup(commonResponse.getErrorMessage(), activity.getString(R.string.login_exceeded));
                                    } else if (commonResponse.getErrorId() === 104) {
                                        if (!getTopActivity().contains("NotificationActivity") && !getTopActivity().contains(
                                                "CallActivity"
                                            ) && !getTopActivity().contains("RingingActivity")
                                        ) {
                                            val dialogIntent =
                                                Intent(activity, NotificationActivity::class.java)
                                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            // dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.user_locked_title));
                                            dialogIntent.putExtra("remoteTitle", title)
                                            dialogIntent.putExtra(
                                                "remoteMessage",
                                                commonResponse.getErrorMessage()
                                            )
                                            dialogIntent.putExtra(
                                                "messageType",
                                                Constants.FCMMessageType.PASSWORD_LOCK
                                            )
                                            activity.startActivity(dialogIntent)
                                        }
                                        //                                    ((BaseActivity) activity).providerDeniedPopup(commonResponse.getErrorMessage(), activity.getString(R.string.login_exceeded));
                                    }
                                }
                            }
                        } else {
                            errorMsg[0] = "Could not send health monitoring"
                        }
                    }

               override fun onFailure(call: Call<HealthMonitoring?>, t: Throwable) {
                   TODO("Not yet implemented")
               }
           }

                override fun onFailure(call: Call<HealthMonitoring?>, t: Throwable) {
//                        Log.e("AppstatusHelper", "onFailure: " +t.toString());
                    if (t is SocketTimeoutException) {
//                            Log.e(TAG, "Exception:", t.getCause());
                        errorMsg[0] = activity.getString(R.string.lost_internet_retry)
                    }
                  *//*  if (t is GoogleJsonResponseException) {
//                            Log.e(TAG, "Exception:", t.getCause());
                        val e: GoogleJsonResponseException = t as GoogleJsonResponseException
                        //                            System.out.println("firebase error code " + e.getStatusCode());
                        if (e.getStatusCode() === 704) {
                            sessionInvalidate(activity)
                            return
                        }
                        val loginRequest = LoginRequest()
                        loginRequest.setToken(
                            PrefUtility().getStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                                ""
                            )
                        )
*//*
                        val commonResponse: Array<CommonResponse?> =
                            arrayOf<CommonResponse?>(
                                CommonResponse()
                            )

//                            ApiClient.getApiUserEndpoints(false,false).renewIdToken(loginRequest).enqueue(new Callback<omnicure.mvp.com.userEndpoints.model.CommonResponse>() {
                        ApiClient().getApiUserEndpoints(true, true)?.renewIdToken(loginRequest)
                            ?.enqueue(object :
                                Callback<CommonResponse?> {
                                override fun onResponse(
                                    call: Call<CommonResponse?>,
                                    response: Response<CommonResponse?>) {
//
                                    if (response.isSuccessful()) {
                                        commonResponse[0] = response.body()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<CommonResponse?>,
                                    t: Throwable) {
//
                                }
                            })

//                            System.out.println("firebase commonresponse " + new Gson().toJson(commonResponse[0]));
                        if (commonResponse[0]?.refreshToken != null) {
                            PrefUtility().saveStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                                commonResponse[0]?.refreshToken
                            )
                        }
                       *//* if (commonResponse[0]?.idToken != null) {
                            val encKey: String?= PrefUtility().getAESAPIKey(activity)
                            PrefUtility().saveStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_IDTOKEN,
                                commonResponse[0]?.idToken?.let {
                                    if (encKey != null) {
                                        AESUtils().decryptData(
                                            it, encKey
                                        )
                                    }
                                }
                            )*//*

   *//*                         sendHealthMonitoring(activity, blurFlag)
                        } else {
                            sessionInvalidate(activity)
                        }
                        errorMsg[0] = activity.getString(R.string.health_monitor_err_msg)

//                            // API call To get the refresh token
//                            // If fails - Session expired intent will be triggered via Notification activity
//                            refreshToken(activity);
                    }
                    if (t is IllegalStateException) {

                    }

            })
        if (!TextUtils.isEmpty(errorMsg[0])) {
            if (hasToBlur(activity) && blurFlag) {
                activity.runOnUiThread {
                    if (activity.findViewById<View>(R.id.blurView).visibility == View.VISIBLE) {
                        activity.findViewById<View>(R.id.blurView).visibility =
                            View.GONE
                    }
                }
            }

        }*//*




    // To get the refresh token

    // To get the refresh token
    fun refreshToken(activity: Activity) {
        val errorMsg = arrayOfNulls<String>(1)
        val loginRequest = LoginRequest()
        loginRequest.setToken(
            PrefUtility().getStringInPref(
                activity,
                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                ""
            )
        )

        val commonResponse: CommonResponse = CommonResponse()


        ApiClient().getApiUserEndpoints(true, true)?.renewIdToken(loginRequest)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>) {

                    if (response.isSuccessful()) {
//                    commonResponse[0] = response.body();
                        val commonResponse1: CommonResponse = response.body()!!
                        if (commonResponse1.refreshToken != null && commonResponse1.idToken != null) {
                            PrefUtility().saveStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                                commonResponse1.refreshToken
                            )
                            val encKey: String? = PrefUtility().getAESAPIKey(activity)
                            PrefUtility().saveStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_IDTOKEN,
                                encKey?.let {
                                    AESUtils().decryptData(commonResponse1.idToken!!,
                                        it
                                    )
                                }
                            )


                        } else {
                            // Session invalidate expiry popup
                           // sessionInvalidate(activity)
                        }


                    }
                }

                override fun onFailure(
                    call: Call<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?>,
                    t: Throwable) {

                    if (t is SocketTimeoutException) {

                        errorMsg[0] = activity.getString(R.string.lost_internet_retry)
                        // Session invalidate expiry popup
                        //sessionInvalidate(activity)
                    }
                    if (t is IllegalStateException) {

                        // Session invalidate expiry popup
                       // sessionInvalidate(activity)
                    }
                }
            })
        errorMsg[0] = activity.getString(R.string.health_monitor_err_msg)
    }


    fun sessionInvalidate(activity: Activity) {

        // Saving flag to disable/stop showing push notification to the user, when session expired
        // If false - notification can be displayed
        // If true - Notification will not be displayed
        PrefUtility().saveBooleanInPref(activity, Constants.SharedPrefConstants.DISABLE_NOTIFICATION, true)
        val dialogIntent = Intent(activity, NotificationActivity::class.java)
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //                                        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.login_exceeded));
        dialogIntent.putExtra("remoteTitle", activity.getString(R.string.session_expired))
        dialogIntent.putExtra("remoteMessage", activity.getString(R.string.session_invalidated_message_token))
        dialogIntent.putExtra("messageType", Constants.FCMMessageType.LOGOUT_EXCEEDED)
        activity.startActivity(dialogIntent)
    }

    fun getTopActivity(): String? {
        val am = currentActivity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val cn = am.getRunningTasks(1)[0].topActivity
        return cn?.shortClassName
    }


   *//* fun doAutoLogout(activity: Activity) {
        if (isActivityDestroyed(activity)) {
            return
        }
        if (activity is BaseActivity) {
            (activity as BaseActivity).showAutoLogoutDialog()
        }
    }*//*

    fun isActivityDestroyed(activity: Activity?): Boolean {
        return activity == null || activity.isFinishing ||
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed
    }

    *//**
     * We are considering app in foreground only if user logged in else we consider app in background only
     *
     * @param activity - Current activity
     * @return - true or false
     *//*
    fun isDiscarded(activity: Activity): Boolean {
        return (activity is SplashActivity || activity is LoginActivity || activity is RegistrationActivity
                || activity is OTPActivity || activity is EmailOTPActivity || activity is RegistrationSuccessActivity
                || activity is SignupActivity || activity is PatientAppointmentActivity || activity is AppointmentSuccessActivity)
    }

    fun hasToBlur(activity: Activity): Boolean {
        return (activity is MyDashboardActivity || activity is HomeActivity || activity is ChatActivity
                || activity is ActivityConsultChart || activity is ActivityPatientCensusWard
                || activity is ActivityConsultChartRemote)
    }*/
}
