package com.example.kotlinomnicure.helper

import android.app.Activity
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
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.*
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalStateException
import java.net.SocketTimeoutException
import java.util.*

class AppStatusHelper:ActivityLifecycleCallbacks {

    private val isOpen = false
    private val isClose = false
    private val isfirsttime = true
    private var resumed = 0
    private var paused = 0
    private var started = 0
    private var stopped = 0
    private var hmTimer: Timer? = null
    private var hmTimerTask: TimerTask? = null
    private var isTimerStarted = false
    private var currentActivity: Activity? = null
    private var authpopupstatus = false
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

//        Log.i(TAG, "onActivityCreated: " + activity.getClass().getSimpleName());
        currentActivity = activity
        (activity as? MyDashboardActivity)?.let { onAppForeground(it) }
    }

    override fun onActivityStarted(activity: Activity) {


        val isAutoLogout: Boolean = isLogoutNeeded(activity)
        val isInternetConnected = true
        currentActivity = activity
        ++started

        val myKM = activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    }

    override fun onActivityResumed(activity: Activity) {

        /*if(isDiscarded(activity)){
            return;
        }*/currentActivity = activity
        ++resumed
        //        Log.e(TAG, "onActivityResumed " + resumed + " " + paused + "isOpen "+isOpen + "isClose "+isClose +"isfirsttime "+isfirsttime);
        var blur = false
        if (resumed > paused) {
            if (AppStatusHelper.isOpen && !AppStatusHelper.isClose) {
                AppStatusHelper.isOpen = false
                //                Log.i(TAG, "Application in Foreground__");
                blur = true
                onAppForeground(activity)
                blurIt(activity, blur)
            } else if (AppStatusHelper.isfirsttime) {
                AppStatusHelper.isfirsttime = false
                //                Log.i(TAG, "Application in Foreground");
                onAppForeground(activity)
                blurIt(activity, blur)
            }
            if (activity !is NotificationActivity && activity !is LoginActivity) {
                authpopupstatus = true
                sendHealthMonitoring(activity, blur)
                // sessionInvalidate(activity);
            }
            AppStatusHelper.isClose = true
        }
    }

    override fun onActivityPaused(activity: Activity) {

        /*if(isDiscarded(activity)){
            return;
        }*/++paused
    }

    override fun onActivityStopped(activity: Activity) {

        /*if(isDiscarded(activity)){
            return;
        }*/++stopped

        if (started == stopped && AppStatusHelper.isClose) {
            AppStatusHelper.isOpen = true
            AppStatusHelper.isClose = false
            onAppBackground(activity)
        }
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
        val isAutoLogout: Boolean = isLogoutNeeded(activity)
        //        boolean isInternetConnected = UtilityMethods.isInternetConnected(activity);
        val isInternetConnected = true

//        Log.i(TAG, "Internet connectivity " + isInternetConnected);
        // isAutoLogout is always false below else if is never gonna work
        /*if (isAutoLogout) {
            doAutoLogout(activity);
            return;
        }*/
        // isInternetConnected is always true below else if is never gonna work
        /*else if (!isInternetConnected) {
            ((BaseActivity) activity).showInternetDialog();
            return;
        }*/if (isDiscarded(activity)) {
            return
        }
        val userID: Long = PrefUtility.getProviderId(activity)
        if (userID == -1L) {
//            Log.i(TAG, "onAppForeground: No health Monitor timer as user not sign-in");
            return
        }
        // isAutoLogout is always false below if is never gonna work
        /*if (isAutoLogout) {
            doAutoLogout(activity);
        } else {*/startTimer(activity)

//        }
    }

    private fun onAppBackground(activity: Activity) {
        (activity.applicationContext as OmnicureApp).setAppInBackground(true)
        if (isDiscarded(activity)) {
            return
        }
        stopTimer()
    }

    private fun startTimer(activity: Activity) {
        if (isTimerStarted) {
//            Log.i(TAG, "startTimer: Timer is already running...");
            stopTimer()
            //            return;
        }
        //        stopTimer();
        hmTimer = Timer()
        val repeatInterval: Long = PrefUtility.getLongInPref(
            activity,
            Constants.SharedPrefConstants.HEALTH_MONITOR_TIMER,
            -1
        )
        val delay: Long = getTimerDelayTime(activity, repeatInterval)
        //        Log.i(TAG, "Health Monitor Timer will start after " + delay + " miliseconds");
//        Log.i(TAG, "Health Monitor Timer Interval : " + repeatInterval + " miliseconds");
        initializeTimerTask(activity)
        //        Log.i(TAG, "Health Monitoring timer started at " + ChatUtils.getDateFormat(System.currentTimeMillis(), "dd-MMM-yyyy,HH:mm a"));
        if (repeatInterval >= 0) {
            hmTimer.scheduleAtFixedRate(hmTimerTask, delay, repeatInterval)
        }
        isTimerStarted = true
    }

    private fun stopTimer() {
//        Log.i(TAG, "stopTimer ");
        try {
            if (hmTimer != null) {
                hmTimer.cancel()
                hmTimer.purge()
                hmTimer = null
            }
            if (hmTimerTask != null) {
                hmTimerTask.cancel()
                hmTimerTask = null
            }
            isTimerStarted = false
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    private fun initializeTimerTask(activity: Activity) {
        hmTimerTask = object : TimerTask() {
            var counter = 0
            override fun run() {
//                Log.i(TAG, "Timer task is running " + ++counter);
                if (authpopupstatus) {
                    authpopupstatus = false
                } else {
                    sendHealthMonitoring(activity, false)
                }


                //  sessionInvalidate(activity);
            }
        }
    }

    private fun isLogoutNeeded(activity: Activity): Boolean {
//        long appActiveTime = PrefUtility.getLongInPref(activity, Constants.SharedPrefConstants.APP_ACTIVE_TIME, -1);
//        long timeDiff;
//        if (appActiveTime == -1) {
//            timeDiff = 0;
//        } else {
//            timeDiff = (System.currentTimeMillis() - appActiveTime) / (1000 * 60);
//        }
//        Log.i(TAG, "User in-active duration " + timeDiff + " minutes");
//        Long autoLogOutTime = PrefUtility.getLongInPref(activity, Constants.SharedPrefConstants.AUTO_LOGOUT_TIME, 30);
//
//        Log.i(TAG, "Auto logout time " + autoLogOutTime);
//        if (appActiveTime != -1 && timeDiff >= autoLogOutTime) {
//            return true;
//        }
//        return PrefUtility.getBooleanInPref(activity, Constants.SharedPrefConstants.IS_AUTO_LOGOUT, false);
        return false
    }

    private fun getTimerDelayTime(activity: Activity, repeatInterval: Long): Long {
        val appActiveTime: Long =
            PrefUtility.getLongInPref(activity, Constants.SharedPrefConstants.APP_ACTIVE_TIME, -1)
        var delay: Long
        delay = if (appActiveTime == -1L) {
            0
        } else {
            repeatInterval - (System.currentTimeMillis() - appActiveTime)
        }
        if (delay < 0 || delay > repeatInterval) {
//            Log.i(TAG, "Delay is negative " + delay);
            delay = 0
        }
        return delay
    }

    fun blurIt(activity: Activity, blurFlag: Boolean) {
        if (activity is SplashActivity) {
            return
        }
        val isError: Boolean =
            PrefUtility.getBooleanInPref(activity, Constants.SharedPrefConstants.IS_ERROR, false)
        //        Log.i(TAG, "run: blur vals " + hasToBlur(activity) + " " + blurFlag + " " + isError);
        if (hasToBlur(activity) && (blurFlag || isError)) {
            activity.runOnUiThread {
                activity.findViewById<View>(R.id.blurView).visibility =
                    View.VISIBLE
            }
        }
    }

    private fun sendHealthMonitoring(activity: Activity, blurFlag: Boolean) {
//        if (isLogoutNeeded(activity)) {
//            doAutoLogout(activity);
//            return;
//        }
        Thread {}.start()

//        Log.d("blureview", blurFlag.toString());
        val id: Long = PrefUtility.getProviderId(activity)
        val token: String = PrefUtility.getToken(activity)
        if (id == -1L) {
//            Log.i(TAG, "onAppForeground: user id is null, no health monitoring api called");
            return
        }
        if (!UtilityMethods.isInternetConnected(activity)) {
            return
        }
        val errorMsg = arrayOfNulls<String>(1)
        ApiClient.getApiProviderEndpoints(true, true).sendHealthMonitorEvent(
            HealthMonitorEventRequestBody(
                currentActivity!!.javaClass.simpleName,
                token, id.toString()
            )
        )
            .enqueue(object : Callback<HealthMonitoring?> {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onResponse(
                    call: Call<HealthMonitoring?>,
                    response: Response<HealthMonitoring?>
                ) {
//                        Log.d("AppStatusHelper", "onResponse: "+response.code());
//                        Log.d("AppStatusHelper", "onResponse: errorMessafe"+response.body());


                    // To get the refresh token
                    if (response.code() == 703) {
                        // API call To get the refresh token
                        // If fails - Session expired intent will be triggered via Notification activity
                        refreshToken(activity)
                    }
                    if (response.isSuccessful()) {
                        val commonResponse: HealthMonitoring? = response.body()
                        //                            Log.d("AppStatusHelper", "onResponse: healthMonitorRes "+new Gson().toJson(commonResponse));
//                            Log.d("AppStatusHelper", "onResponse: healthMonitorRes utilErrorEmpty->"+TextUtils.isEmpty(commonResponse.getErrorMessage()));
//                            Log.d("AppStatusHelper", "onResponse: healthMonitorRes utilErrorNotEmpty-->"+!TextUtils.isEmpty(commonResponse.getErrorMessage()));
                        if (commonResponse != null && TextUtils.isEmpty(commonResponse.getErrorMessage())) {
//                            if (commonResponse != null && !TextUtils.isEmpty(commonResponse.getErrorMessage())) {
//                                Log.d("AppStatusHelper", "onResponse: healthMonitorRes Status-> "+commonResponse.getStatus());
//                                Log.d("AppStatusHelper", "onResponse: healthMonitorRes errorId-> "+commonResponse.getErrorId());
                            if (commonResponse.getStatus()) {
                                val time: Long = PrefUtility.getLongInPref(
                                    activity,
                                    Constants.SharedPrefConstants.APP_ACTIVE_TIME,
                                    0
                                )
                                val autoLogOutTIme: Long = PrefUtility.getLongInPref(
                                    activity,
                                    Constants.SharedPrefConstants.AUTO_LOGOUT_TIME,
                                    30
                                )

//                                    Log.i(TAG, "Health monitoring values " + System.currentTimeMillis() + " " + autoLogOutTIme + " " + time);
                                /*  if (time != 0 && (System.currentTimeMillis() - time) >= autoLogOutTIme) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        doAutoLogout(activity);
                                    }
                                });
                            }*/

                                // Update the global notification enable/disable flag for acuity IAP notification
                                if (commonResponse.getNotificationRequests() != null && commonResponse.getNotificationRequests()
                                        .get(0) != null
                                ) {
//                                        Log.d(TAG, "SendHealthMonitorEvent: getnotificationEnabled-->" + commonResponse.getNotificationRequests().get(0).getNotificationEnabled());
//                                        Log.d(TAG, "SendHealthMonitorEvent: getnotificationAcuity-->" + commonResponse.getNotificationRequests().get(0).getAcuity().replace("[", "").replace("]", "").replace("\"", ""));
                                    // Saving the current status of Acuity score & notification enable/disabling in shared preference dynamically from backend
                                    PrefUtility.saveStringInPref(
                                        currentActivity,
                                        Constants.SharedPrefConstants.ALERT_ACUITY,
                                        java.lang.String.valueOf(
                                            commonResponse.getNotificationRequests().get(0)
                                                .getAcuity().replace("[", "").replace("]", "")
                                                .replace("\"", "")
                                        )
                                    )
                                    PrefUtility.saveStringInPref(
                                        currentActivity,
                                        Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                                        java.lang.String.valueOf(
                                            commonResponse.getNotificationRequests().get(0)
                                                .getNotificationEnabled()
                                        )
                                    )
                                }
                                PrefUtility.saveLongInPref(
                                    currentActivity,
                                    Constants.SharedPrefConstants.APP_ACTIVE_TIME,
                                    System.currentTimeMillis()
                                )
                                PrefUtility.saveBooleanInPref(
                                    currentActivity,
                                    Constants.SharedPrefConstants.IS_ERROR,
                                    false
                                )
                                //                                    Log.i(TAG, "Health Monitoring sent successfully...");
                                if (hasToBlur(activity)) {
                                    activity.runOnUiThread {
                                        if (activity.findViewById<View>(R.id.blurView).visibility == View.VISIBLE) {
                                            activity.findViewById<View>(R.id.blurView).visibility =
                                                View.GONE
                                        }
                                    }
                                }
                            } else {

//                                    Log.e(TAG, "onResponse:status falsecheck--> "+ commonResponse.getStatus());
//                                    Log.e(TAG, "onResponse:status falseErrorid--> "+ commonResponse.getErrorId());
                                if (activity is SplashActivity) {
                                    PrefUtility.saveBooleanInPref(
                                        currentActivity,
                                        Constants.SharedPrefConstants.IS_ERROR,
                                        true
                                    )
                                    return
                                }
                                PrefUtility.saveBooleanInPref(
                                    currentActivity,
                                    Constants.SharedPrefConstants.IS_ERROR,
                                    true
                                )
                                Handler(Looper.getMainLooper()).post {
                                    if (commonResponse.getErrorId() !== 104) {
                                        val topic: String = UtilityMethods.getFCMTopic()
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                                            .addOnCompleteListener {
                                                //                                                            if (task.isSuccessful()) {
                                                //                                                                // Sign in success, update UI with the signed-in user's information
                                                ////                                                                Log.d(TAG, "topic subscribe:success");
                                                //                                                            } else {
                                                //                                                                // If sign in fails, display a message to the user.
                                                ////                                                                Log.w(TAG, "topic subscribe:failure", task.getException());
                                                //                                                            }
                                            }
                                    }
                                    var title = "Error"
                                    if (commonResponse.getTitle() != null) {
                                        title = commonResponse.getTitle()
                                    }

//                                        Log.d(TAG, "commonResponse.getErrorId() : " + commonResponse.getErrorId());
                                    if (commonResponse.getErrorId() === 0) {
                                        if (currentActivity is NotificationActivity) {
                                            (currentActivity as NotificationActivity).stopFP()
                                            currentActivity.finish()
                                        }
                                        PrefUtility.saveStringInPref(
                                            currentActivity,
                                            Constants.SharedPrefConstants.SESSION_TIMEOUT_MESSAGE,
                                            commonResponse.getErrorMessage()
                                        )
                                        PrefUtility.saveStringInPref(
                                            currentActivity,
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
                                            (currentActivity as NotificationActivity).stopFP()
                                            currentActivity.finish()
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
                        } else if (commonResponse != null && !TextUtils.isEmpty(commonResponse.getErrorMessage())) {
//                            } else if (commonResponse != null && TextUtils.isEmpty(commonResponse.getErrorMessage())) {
//                                errorMsg[0] = commonResponse.getErrorMessage();
                            if (commonResponse.getStatus()) {
                                val time: Long = PrefUtility.getLongInPref(
                                    activity,
                                    Constants.SharedPrefConstants.APP_ACTIVE_TIME,
                                    0
                                )
                                val autoLogOutTIme: Long = PrefUtility.getLongInPref(
                                    activity,
                                    Constants.SharedPrefConstants.AUTO_LOGOUT_TIME,
                                    30
                                )

//                                    Log.i(TAG, "Health monitoring values " + System.currentTimeMillis() + " " + autoLogOutTIme + " " + time);
                                /*  if (time != 0 && (System.currentTimeMillis() - time) >= autoLogOutTIme) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        doAutoLogout(activity);
                                    }
                                });
                            }*/

                                // Update the global notification enable/disable flag for acuity IAP notification
                                if (commonResponse.getNotificationRequests() != null && commonResponse.getNotificationRequests()
                                        .get(0) != null
                                ) {
//                                        Log.d(TAG, "SendHealthMonitorEvent: getnotificationEnabled-->" + commonResponse.getNotificationRequests().get(0).getNotificationEnabled());
//                                        Log.d(TAG, "SendHealthMonitorEvent: getnotificationAcuity-->" + commonResponse.getNotificationRequests().get(0).getAcuity().replace("[", "").replace("]", "").replace("\"", ""));
                                    // Saving the current status of Acuity score & notification enable/disabling in shared preference dynamically from backend
                                    PrefUtility.saveStringInPref(
                                        currentActivity,
                                        Constants.SharedPrefConstants.ALERT_ACUITY,
                                        java.lang.String.valueOf(
                                            commonResponse.getNotificationRequests().get(0)
                                                .getAcuity().replace("[", "").replace("]", "")
                                                .replace("\"", "")
                                        )
                                    )
                                    PrefUtility.saveStringInPref(
                                        currentActivity,
                                        Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                                        java.lang.String.valueOf(
                                            commonResponse.getNotificationRequests().get(0)
                                                .getNotificationEnabled()
                                        )
                                    )
                                }
                                PrefUtility.saveLongInPref(
                                    currentActivity,
                                    Constants.SharedPrefConstants.APP_ACTIVE_TIME,
                                    System.currentTimeMillis()
                                )
                                PrefUtility.saveBooleanInPref(
                                    currentActivity,
                                    Constants.SharedPrefConstants.IS_ERROR,
                                    false
                                )
                                //                                    Log.i(TAG, "Health Monitoring sent successfully...");
                                if (hasToBlur(activity)) {
                                    activity.runOnUiThread {
                                        if (activity.findViewById<View>(R.id.blurView).visibility == View.VISIBLE) {
                                            activity.findViewById<View>(R.id.blurView).visibility =
                                                View.GONE
                                        }
                                    }
                                }
                            } else {

//                                    Log.e(TAG, "onResponse:status falsecheck--> "+ commonResponse.getStatus());
//                                    Log.e(TAG, "onResponse:status falseErrorid--> "+ commonResponse.getErrorId());
                                if (activity is SplashActivity) {
                                    PrefUtility.saveBooleanInPref(
                                        currentActivity,
                                        Constants.SharedPrefConstants.IS_ERROR,
                                        true
                                    )
                                    return
                                }
                                PrefUtility.saveBooleanInPref(
                                    currentActivity,
                                    Constants.SharedPrefConstants.IS_ERROR,
                                    true
                                )
                                Handler(Looper.getMainLooper()).post {
                                    if (commonResponse.getErrorId() !== 104) {
                                        val topic: String = UtilityMethods.getFCMTopic()
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                                            .addOnCompleteListener {
                                                //                                                            if (task.isSuccessful()) {
                                                //                                                                // Sign in success, update UI with the signed-in user's information
                                                ////                                                                Log.d(TAG, "topic subscribe:success");
                                                //                                                            } else {
                                                //                                                                // If sign in fails, display a message to the user.
                                                ////                                                                Log.w(TAG, "topic subscribe:failure", task.getException());
                                                //                                                            }
                                            }
                                    }
                                    var title = "Error"
                                    if (commonResponse.getTitle() != null) {
                                        title = commonResponse.getTitle()
                                    }

//                                        Log.d(TAG, "commonResponse.getErrorId() : " + commonResponse.getErrorId());
                                    if (commonResponse.getErrorId() === 0) {
                                        if (currentActivity is NotificationActivity) {
                                            (currentActivity as NotificationActivity).stopFP()
                                            currentActivity.finish()
                                        }
                                        PrefUtility.saveStringInPref(
                                            currentActivity,
                                            Constants.SharedPrefConstants.SESSION_TIMEOUT_MESSAGE,
                                            commonResponse.getErrorMessage()
                                        )
                                        PrefUtility.saveStringInPref(
                                            currentActivity,
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
                                            (currentActivity as NotificationActivity).stopFP()
                                            currentActivity.finish()
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
                }

                override fun onFailure(call: Call<HealthMonitoring?>, t: Throwable) {
//                        Log.e("AppstatusHelper", "onFailure: " +t.toString());
                    if (t is SocketTimeoutException) {
//                            Log.e(TAG, "Exception:", t.getCause());
                        errorMsg[0] = activity.getString(R.string.lost_internet_retry)
                    }
                    if (t is GoogleJsonResponseException) {
//                            Log.e(TAG, "Exception:", t.getCause());
                        val e: GoogleJsonResponseException = t as GoogleJsonResponseException
                        //                            System.out.println("firebase error code " + e.getStatusCode());
                        if (e.getStatusCode() === 704) {
                            sessionInvalidate(activity)
                            return
                        }
                        val loginRequest = LoginRequest()
                        loginRequest.setToken(
                            PrefUtility.getStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                                ""
                            )
                        )
                        //                        omnicure.mvp.com.userEndpoints.model.CommonResponse commonResponse = EndPointBuilder.getUserEndpoints()
//                                .renewIdToken(loginRequest)
//                                .execute();
                        val commonResponse: Array<omnicure.mvp.com.userEndpoints.model.CommonResponse?> =
                            arrayOf<omnicure.mvp.com.userEndpoints.model.CommonResponse?>(
                                CommonResponse()
                            )

//                            ApiClient.getApiUserEndpoints(false,false).renewIdToken(loginRequest).enqueue(new Callback<omnicure.mvp.com.userEndpoints.model.CommonResponse>() {
                        ApiClient.getApiUserEndpoints(true, true).renewIdToken(loginRequest)
                            .enqueue(object :
                                Callback<omnicure.mvp.com.userEndpoints.model.CommonResponse?> {
                                override fun onResponse(
                                    call: Call<omnicure.mvp.com.userEndpoints.model.CommonResponse?>,
                                    response: Response<omnicure.mvp.com.userEndpoints.model.CommonResponse?>
                                ) {
//                                    Log.d(TAG, "onResponse: "+response.code());
                                    if (response.isSuccessful()) {
                                        commonResponse[0] = response.body()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<omnicure.mvp.com.userEndpoints.model.CommonResponse?>,
                                    t: Throwable
                                ) {
//                                    Log.e(TAG, "onFailure: "+t.toString() );
                                }
                            })

//                            System.out.println("firebase commonresponse " + new Gson().toJson(commonResponse[0]));
                        if (commonResponse[0].getRefreshToken() != null) {
                            PrefUtility.saveStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                                commonResponse[0].getRefreshToken()
                            )
                        }
                        if (commonResponse[0].getIdToken() != null) {
                            val encKey: String = PrefUtility.getAESAPIKey(activity)
                            PrefUtility.saveStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_IDTOKEN,
                                AESUtils.decryptData(
                                    commonResponse[0].getIdToken(), encKey
                                )
                            )

//                                PrefUtility.saveStringInPref(activity, Constants.SharedPrefConstants.FIREBASE_IDTOKEN, commonResponse[0].getIdToken());
                            sendHealthMonitoring(activity, blurFlag)
                        } else {
                            sessionInvalidate(activity)
                        }
                        errorMsg[0] = activity.getString(R.string.health_monitor_err_msg)

//                            // API call To get the refresh token
//                            // If fails - Session expired intent will be triggered via Notification activity
//                            refreshToken(activity);
                    }
                    if (t is IllegalStateException) {
//                            Log.e(TAG, "IllegalStateException:SendMonitor", t.getCause());
//                            // API call To get the refresh token
//                            // If fails - Session expired intent will be triggered via Notification activity
//                            refreshToken(activity);
                    }
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
            //            Log.i(TAG, "Health monitoring api error " + errorMsg);
        }
    }

    // To get the refresh token

    // To get the refresh token
    private fun refreshToken(activity: Activity) {
        val errorMsg = arrayOfNulls<String>(1)
        val loginRequest = LoginRequest()
        loginRequest.setToken(
            PrefUtility.getStringInPref(
                activity,
                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                ""
            )
        )
        //                        omnicure.mvp.com.userEndpoints.model.CommonResponse commonResponse = EndPointBuilder.getUserEndpoints()
//                                .renewIdToken(loginRequest)
//                                .execute();

//        final omnicure.mvp.com.userEndpoints.model.CommonResponse[] commonResponse = {new omnicure.mvp.com.userEndpoints.model.CommonResponse()};
        val commonResponse: omnicure.mvp.com.userEndpoints.model.CommonResponse = CommonResponse()

//        ApiClient.getApiUserEndpoints(false,false).renewIdToken(loginRequest).enqueue(new Callback<omnicure.mvp.com.userEndpoints.model.CommonResponse>() {
        ApiClient.getApiUserEndpoints(true, true).renewIdToken(loginRequest)
            .enqueue(object : Callback<omnicure.mvp.com.userEndpoints.model.CommonResponse?> {
                override fun onResponse(
                    call: Call<omnicure.mvp.com.userEndpoints.model.CommonResponse?>,
                    response: Response<omnicure.mvp.com.userEndpoints.model.CommonResponse?>
                ) {
//                Log.d(TAG, "onResponse:renewIdToken--> "+response.code());
//                Log.d(TAG, "onResponse:renewIdTokenRes--> "+new Gson().toJson(response.body()));
                    if (response.isSuccessful()) {
//                    commonResponse[0] = response.body();
                        val commonResponse1: CommonResponse = response.body()!!
                        if (commonResponse1.getRefreshToken() != null && commonResponse1.getIdToken() != null) {
                            PrefUtility.saveStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                                commonResponse1.getRefreshToken()
                            )
                            val encKey: String = PrefUtility.getAESAPIKey(activity)
                            PrefUtility.saveStringInPref(
                                activity,
                                Constants.SharedPrefConstants.FIREBASE_IDTOKEN,
                                AESUtils.decryptData(commonResponse1.getIdToken(), encKey)
                            )
                            sendHealthMonitoring(activity, true)

//                        PrefUtility.saveStringInPref(activity, Constants.SharedPrefConstants.FIREBASE_IDTOKEN, commonResponse1.getIdToken());
                        } else {
                            // Session invalidate expiry popup
                            sessionInvalidate(activity)
                        }

//                    if (commonResponse1.getIdToken() != null) {
//                        PrefUtility.saveStringInPref(activity, Constants.SharedPrefConstants.FIREBASE_IDTOKEN, commonResponse1.getIdToken());
//                    }
//                    else{
//                        // Session invalidate expiry popup
//                        sessionInvalidate(activity);
//                    }
                    }
                }

                override fun onFailure(
                    call: Call<omnicure.mvp.com.userEndpoints.model.CommonResponse?>,
                    t: Throwable
                ) {
//                Log.e(TAG, "onFailure: "+t.toString() );
                    if (t is SocketTimeoutException) {
//                    Log.e(TAG, "SocketTimeoutException:", t.getCause());
                        errorMsg[0] = activity.getString(R.string.lost_internet_retry)
                        // Session invalidate expiry popup
                        sessionInvalidate(activity)
                    }
                    if (t is IllegalStateException) {
//                    Log.e(TAG, "refreshToken IllegalStateException:", t.getCause());
                        // Session invalidate expiry popup
                        sessionInvalidate(activity)
                    }
                }
            })
        errorMsg[0] = activity.getString(R.string.health_monitor_err_msg)
    }
}