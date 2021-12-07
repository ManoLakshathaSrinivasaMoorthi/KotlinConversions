package com.example.kotlinomnicure.fcm

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.*
import android.graphics.*
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.kotlinomnicure.OmnicureApp
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.*
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.apiRetrofit.ProviderEndpoints
import com.example.kotlinomnicure.backend.EndPointBuilder
import com.example.kotlinomnicure.helper.NotificationHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.videocall.openvcall.ui.RingingActivity
import com.example.kotlinomnicure.videocall.openvcall.ui.layout.VideoViewAdapterUtil
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mvp.omnicure.kotlinactivity.requestbodys.SaveAuditCallRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.SendMsgNotifyRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.UpdateFcmkKeyRequestBody
import omnicurekotlin.example.com.loginEndpoints.model.CommonResponse
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.SocketTimeoutException
import java.net.URL

class FirebaseMessageService: FirebaseMessagingService() {
    // Variables
    private val TAG = FirebaseMessagingService::class.java.simpleName
    private val REQUEST_CODE = 1

    //private static final int NOTIFICATION_ID = 6578;
    var NOTIFICATION_CHANNEL_ID = "com.mvp.omnicure"
    var channelName = "Video call omnicure"
    var mAlarmManager: AlarmManager? = null
    private var notifyId = 0

    // Based on received remote message json object, multiple handling are done here
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Based on the messageType received, the notification handling is differentiated
        val messageType = remoteMessage.data["messageType"]
        //        Log.i(TAG, "onMessageReceived " + remoteMessage.getData());
        val providerId: Long? = PrefUtility().getProviderId(this@FirebaseMessageService)

        // Retrieving flag to disable/stop showing push notification to the user, when session expired
        // If false - notification can be displayed
        // If true - Notification will not be displayed
        val sessionExpired: Boolean = PrefUtility().getBooleanInPref(this,
            Constants.SharedPrefConstants.DISABLE_NOTIFICATION,
            false
        )
        //        Log.e(TAG, "onMessageReceived: sessionExpired-->"+sessionExpired);
        if (!sessionExpired) {
            if (messageType != null) {
                when (messageType) {
                    Constants.FCMMessageType.NEW_MESSAGE -> {

                        // When messageType == NEW_MESSAGE, Show the notification to the user
                        sendMessageNotification(remoteMessage)
                    }
                    Constants.FCMMessageType.VIDEO_CALL -> {
                        if (providerId != null) {
                            val providerObj = getProvider(remoteMessage)
                            var pId: Long? = null
                            if (providerObj != null) {
                                pId = if (providerObj.has("id")) providerObj["id"].asLong else 0L
                                //                        if (pId != providerId) {
                                if (pId != providerId) {
                                    // sendCall method  triggered with "VIDEO_CALL" and with remote object
                                    sendCall(
                                        remoteMessage,
                                        false,
                                        Constants.FCMMessageType.VIDEO_CALL
                                    )
                                }
                            }
                        }
                    }
                    Constants.FCMMessageType.AUDIO_CALL -> {
                        if (providerId != null) {
                            val providerObj = getProvider(remoteMessage)
                            var pId: Long? = null
                            if (providerObj != null) {
                                pId = if (providerObj.has("id")) providerObj["id"].asLong else 0L
                                if (pId != providerId) {
                                    // sendCall method  triggered with "AUDIO_CALL" and with remote object
                                    sendCall(
                                        remoteMessage,
                                        false,
                                        Constants.FCMMessageType.AUDIO_CALL
                                    )
                                }
                            }
                        }
                    }
                    Constants.FCMMessageType.CALLER_BUSY -> {
                        sendCallerBroadCast(remoteMessage, "caller-busy")
                    }
                    Constants.FCMMessageType.CALLER_REJECT -> {
                        sendCallerBroadCast(remoteMessage, "caller-reject")
                    }
                    Constants.FCMMessageType.CALLER_NOT_ANSWER -> {
                        sendCallerBroadCast(remoteMessage, "caller-not-answer")
                    }
                    Constants.FCMMessageType.PATIENT_ASSIGNED -> {
                        val title = getTitle(remoteMessage)
                        // Handling normal notification based on the remote message object
                        sendNotification(remoteMessage, title)
                    }
                    Constants.FCMMessageType.INVITATION_DELETE -> {

                        // Handling Invite hide notification
                        hideInviteNotification(remoteMessage)
                    }
                    Constants.FCMMessageType.INVITATION_ACCEPT -> {

                        // Handling Invite accepted notification
                        sendAcceptNotification(remoteMessage)
                    }
                    Constants.FCMMessageType.INVITATION -> {

                        // Handling Invite accepted notification
                        sendAcceptNotification(remoteMessage)
                    }
                    Constants.FCMMessageType.DISCHARGED -> {

                        // Handling Discharge notification
                        sendDischargeNotification(remoteMessage)
                    }
                    Constants.FCMMessageType.HANDOFF_INITIATED -> {

                        // Handling the Hand-Off notificatio
                        sendHandOffNotification(remoteMessage)
                    }
                    Constants.FCMMessageType.PATIENT_TRANSFER -> {

                        // Handling the patient transfer notification
                        sendPatientTransferNotification(remoteMessage)
                    }
                    Constants.FCMMessageType.SYSTEM_ALERT -> {

                        // Handling the system alert notification
                        sendSystemAlertNotification(remoteMessage)
                    }
                    Constants.FCMMessageType.DENIED_USER -> {
                        Handler(Looper.getMainLooper()).post { // Sending denied data intent to Notification activity
                            sendDeniedUserNotification(remoteMessage)
                        }
                    }
                    Constants.FCMMessageType.LOGOUT_EXCEEDED -> {
                        Handler(Looper.getMainLooper()).post {
                            val topic: String? = UtilityMethods().getFCMTopic()
                            topic?.let {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(it)
                                    .addOnCompleteListener {
                                        //                                                if (task.isSuccessful()) {
                                        //                                                    // Sign in success, update UI with the signed-in user's information
                                        ////                                                    Log.d(TAG, "topic subscribe:success notification");
                                        //                                                } else {
                                        //                                                    // If sign in fails, display a message to the user.
                                        ////                                                    Log.w(TAG, "topic subscribe:failure", task.getException());
                                        //                                                }
                                    }
                            }
                            // Sending denied data intent to Notification activity
                            sendDeniedUserNotification(remoteMessage)
                        }
                    }
                    Constants.FCMMessageType.LOCKED_USER -> {
                        Handler(Looper.getMainLooper()).post { // Sending denied data intent to Notification activity
                            sendDeniedUserNotification(remoteMessage)
                        }
                    }
                    Constants.FCMMessageType.SOS -> {
                        sendCall(remoteMessage, true, Constants.FCMMessageType.VIDEO_CALL)
                    }
                    Constants.FCMMessageType.SOS_DISMISS -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val ns = NOTIFICATION_SERVICE
                            val nMgr =
                                applicationContext.getSystemService(ns) as NotificationManager
                            nMgr.cancel(notifyId)
                            try {
                                unregisterReceiver(refreshButtonBroadcastReceiver)
                            } catch (e: IllegalArgumentException) {
//                                Log.e(TAG, "Exception:", e.getCause());
                            }
                        }
                        // Dismiss SoS Call with the remote object received
                        dismissSOSCall(remoteMessage)
                    }
                    else -> {
                    }
                }
            }
        }
//        switch (messageType) {
//            case Constants.FCMMessageType.NEW_MESSAGE: {
//                // When messageType == NEW_MESSAGE, Show the notification to the user
//                sendMessageNotification(remoteMessage);
//            }
//            break;
//            // When messageType == VIDEO_CALL, video call related object is triggered to sendCall method
//            case Constants.FCMMessageType.VIDEO_CALL: {
//                if (providerId != null) {
//                    final JsonObject providerObj = getProvider(remoteMessage);
//
//                    Long pId = null;
//                    if (providerObj != null) {
//                        pId = providerObj.has("id") ? providerObj.get("id").getAsLong() : 0L;
////                        if (pId != providerId) {
//                        if (!pId.equals(providerId)) {
//                            // sendCall method  triggered with "VIDEO_CALL" and with remote object
//                            sendCall(remoteMessage, false, Constants.FCMMessageType.VIDEO_CALL);
//                        }
//                    }
//                }
//
//            }
//            break;
//            // When messageType == AUDIO_CALL, video call related object is triggered to sendCall method
//            case Constants.FCMMessageType.AUDIO_CALL: {
//                if (providerId != null) {
//                    final JsonObject providerObj = getProvider(remoteMessage);
//
//                    Long pId = null;
//                    if (providerObj != null) {
//                        pId = providerObj.has("id") ? providerObj.get("id").getAsLong() : 0L;
////                    if (pId != providerId) {
//                        if (!pId.equals(providerId)) {
//                            // sendCall method  triggered with "AUDIO_CALL" and with remote object
//                            sendCall(remoteMessage, false, Constants.FCMMessageType.AUDIO_CALL);
//                        }
//                    }
//                }
//            }
//            break;
//            // Sending "CALLER_BUSY" as Call status via broadcast receiver
//            case Constants.FCMMessageType.CALLER_BUSY: {
//                sendCallerBroadCast(remoteMessage, "caller-busy");
//            }
//            break;
//            // Sending "CALLER_REJECT" as Call status via broadcast receiver
//            case Constants.FCMMessageType.CALLER_REJECT: {
//
//                sendCallerBroadCast(remoteMessage, "caller-reject");
//            }
//            break;
//            // Sending "CALLER_NOT_ANSWER" as Call status via broadcast receiver
//            case Constants.FCMMessageType.CALLER_NOT_ANSWER: {
//                sendCallerBroadCast(remoteMessage, "caller-not-answer");
//            }
//            break;
//            // Sending notification as "PATIENT_ASSIGNED" type
//            case Constants.FCMMessageType.PATIENT_ASSIGNED: {
//                String title = getTitle(remoteMessage);
//                // Handling normal notification based on the remote message object
//                sendNotification(remoteMessage, title);
//            }
//            break;
//            // Sending notification as "INVITATION_DELETE" type
//            case Constants.FCMMessageType.INVITATION_DELETE: {
//                // Handling Invite hide notification
//                hideInviteNotification(remoteMessage);
//            }
//            break;
//            // Sending notification as "INVITATION_ACCEPT" type
//            case Constants.FCMMessageType.INVITATION_ACCEPT: {
//                // Handling Invite accepted notification
//                sendAcceptNotification(remoteMessage);
//            }
//            break;
//            // Sending notification as "INVITATION" type
//            case Constants.FCMMessageType.INVITATION: {
//                // Handling Invite accepted notification
//                sendAcceptNotification(remoteMessage);
//            }
//            break;
//            // Sending notification as "DISCHARGED" type
//            case Constants.FCMMessageType.DISCHARGED: {
//                // Handling Discharge notification
//                sendDischargeNotification(remoteMessage);
//            }
//            break;
//            // Sending notification as "HANDOFF_INITIATED" type
//            case Constants.FCMMessageType.HANDOFF_INITIATED: {
//                // Handling the Hand-Off notificatio
//                sendHandOffNotification(remoteMessage);
//            }
//            break;
//            // Sending notification as "PATIENT_TRANSFER" type
//            case Constants.FCMMessageType.PATIENT_TRANSFER: {
//                // Handling the patient transfer notification
//                sendPatientTransferNotification(remoteMessage);
//            }
//            break;
//            // Sending notification as "SYSTEM_ALERT" type
//            case Constants.FCMMessageType.SYSTEM_ALERT: {
//                // Handling the system alert notification
//                sendSystemAlertNotification(remoteMessage);
//            }
//            break;
//            // Sending denied data intent to Notification activity
//            case Constants.FCMMessageType.DENIED_USER: {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Sending denied data intent to Notification activity
//                        sendDeniedUserNotification(remoteMessage);
//                    }
//                });
//
//            }
//            break;
//            // Sending denied data intent to Notification activity for "LOGOUT_EXCEEDED" type
//            case Constants.FCMMessageType.LOGOUT_EXCEEDED: {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        String topic = UtilityMethods.getFCMTopic();
//                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            // Sign in success, update UI with the signed-in user's information
//                                            Log.d(TAG, "topic subscribe:success notification");
//                                        } else {
//                                            // If sign in fails, display a message to the user.
//                                            Log.w(TAG, "topic subscribe:failure", task.getException());
//                                        }
//                                    }
//                                });
//                        // Sending denied data intent to Notification activity
//                        sendDeniedUserNotification(remoteMessage);
//                    }
//                });
//
//            }
//            break;
//            // Sending denied data intent to Notification activity
//            case Constants.FCMMessageType.LOCKED_USER: {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Sending denied data intent to Notification activity
//                        sendDeniedUserNotification(remoteMessage);
//                    }
//                });
//
//            }
//            break;
//            // Sending SoS data intent to Ringing activity
//            case Constants.FCMMessageType.SOS: {
//                sendCall(remoteMessage, true, Constants.FCMMessageType.VIDEO_CALL);
//            }
//            break;
//            // SoS dismiss triggere and unregister "refreshButtonBroadcastReceiver"
//            case Constants.FCMMessageType.SOS_DISMISS: {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    String ns = Context.NOTIFICATION_SERVICE;
//                    NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
//                    nMgr.cancel(notifyId);
//                    try{
//                        this.unregisterReceiver(refreshButtonBroadcastReceiver);
//                    }catch (IllegalArgumentException e){
//                        Log.e(TAG, "Exception:", e.getCause());
//                    }
//                }
//                // Dismiss SoS Call with the remote object received
//                dismissSOSCall(remoteMessage);
//            }
//            break;
//
//            default:
//                break;
//        }
    }


    /**
     * Parsing the intent data based on the "FCMMessageType" received
     * @param intent
     * @param remoteMessage
     */
    private fun putIntentExtra(intent: Intent, remoteMessage: RemoteMessage) {
        when (getMessageType(remoteMessage)) {
            Constants.FCMMessageType.INVITATION, Constants.FCMMessageType.HANDOFF_INITIATED -> {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending")
            }
            Constants.FCMMessageType.DISCHARGED -> {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "completed")
            }
            Constants.FCMMessageType.PATIENT_ASSIGNED, Constants.FCMMessageType.INVITATION_ACCEPT -> {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "active")
            }
        }
    }
/*
    private void putIntentExtra(Intent intent, RemoteMessage remoteMessage){
        String messageType = getMessageType(remoteMessage);

        switch (messageType) {
            case Constants.FCMMessageType.INVITATION: {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending");
            }
            break;
            case Constants.FCMMessageType.DISCHARGED: {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "completed");
            }
            break;
            case Constants.FCMMessageType.HANDOFF_INITIATED: {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending");
            }
        }
    }
*/

    /*
    private void putIntentExtra(Intent intent, RemoteMessage remoteMessage){
        String messageType = getMessageType(remoteMessage);

        switch (messageType) {
            case Constants.FCMMessageType.INVITATION: {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending");
            }
            break;
            case Constants.FCMMessageType.DISCHARGED: {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "completed");
            }
            break;
            case Constants.FCMMessageType.HANDOFF_INITIATED: {
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending");
            }
        }
    }
*/
    /**
     * Dismiss SoS Call with the remote object received
     * @param remoteMessage
     */
    private fun dismissSOSCall(remoteMessage: RemoteMessage) {
        //{Provider={"id":5746980898734080,"name":"mehar chand","email":"mchand@unifytech.com","role":"BD","phone":"8209024857"}, messageType=new_message, Patient={"id":5765243099676672,"name":"John Doe","bdProviderId":5746980898734080,"bdProviderName":"Mehar","gender":"Male","email":"john.doe@gmail.com","dob":1086122563000,"address":"Gurgaon","phone":"9999988888","countryCode":"91","hospital":"Madigan Army Medical Center","hospitalId":5077875795427328,"Status":"Active","joiningTime":1587189432342,"bed":"101"}, message=ggh}
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val channel = getMessage(remoteMessage)
            var providerName: String? = null
            var providerId: Long? = null
            if (providerObj != null) {
                providerName = if (providerObj.has("name")) providerObj["name"].asString else ""
                providerId = if (providerObj.has("id")) providerObj["id"].asLong else 0L
                val hospitalName =
                    if (providerObj.has("hospital")) providerObj["hospital"].asString else ""
                val intent = Intent("sos-dismiss")
                intent.putExtra("providerName", providerName)
                intent.putExtra("hospitalName", hospitalName)
                intent.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME, channel)
                sendBroadcast(intent)
                //check notification for android 10
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val notificationHelper = NotificationHelper(applicationContext, null)
                    if (providerId != null) {
                        notificationHelper.clearNotification(providerId.toInt())
                    }
                }
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Sending the values of the Call status via broadcast
     * @param remoteMessage
     * @param action
     */
    private fun sendCallerBroadCast(remoteMessage: RemoteMessage, action: String) {
        //{Provider={"id":5746980898734080,"name":"mehar chand","email":"mchand@unifytech.com","role":"BD","phone":"8209024857"}, messageType=new_message, Patient={"id":5765243099676672,"name":"John Doe","bdProviderId":5746980898734080,"bdProviderName":"Mehar","gender":"Male","email":"john.doe@gmail.com","dob":1086122563000,"address":"Gurgaon","phone":"9999988888","countryCode":"91","hospital":"Madigan Army Medical Center","hospitalId":5077875795427328,"Status":"Active","joiningTime":1587189432342,"bed":"101"}, message=ggh}
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val providerList = getProviderList(remoteMessage)
            val channel = getMessage(remoteMessage)
            if (providerList != null && providerList.length() <= 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val ns = NOTIFICATION_SERVICE
                    val nMgr = applicationContext.getSystemService(ns) as NotificationManager
                    nMgr.cancel(notifyId)
                    try {
                        unregisterReceiver(refreshButtonBroadcastReceiver)
                    } catch (e: IllegalArgumentException) {
//                        Log.e(TAG, "Exception:", e.getCause());
                    }
                }
            }
            // Intent input values
            var providerName: String? = null
            if (providerObj != null) {
                providerName = if (providerObj.has("name")) providerObj["name"].asString else ""
                val providerId = if (providerObj.has("id")) providerObj["id"].asLong else 0L
                val hospitalName =
                    if (providerObj.has("hospital")) providerObj["hospital"].asString else ""
                var picUrl: String? = ""
                if (providerObj.has("profilePicUrl")) {
                    picUrl = providerObj["profilePicUrl"].asString
                }
                val intent = Intent(action)
                intent.putExtra("providerName", providerName)
                intent.putExtra("hospitalName", hospitalName)
                intent.putExtra("profilePicUrl", picUrl)
                intent.putExtra("providerId", providerId)
                if (providerList != null) {
                    intent.putExtra("providerList", providerList.toString())
                }
                intent.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME, channel)
                var patientId: Long? = null
                if (patientObj != null) {
                    patientId = if (patientObj.has("id")) patientObj["id"].asLong else 0L
                    val patientName =
                        if (patientObj.has("name")) patientObj["name"].asString else ""
                    val patientAge = if (patientObj.has("dob")) patientObj["dob"].asLong else 0L
                    val gender = if (patientObj.has("gender")) patientObj["gender"].asString else ""
                    if (patientId != null) {
                        intent.putExtra("patientId", patientId)
                        intent.putExtra("patientName", patientName)
                        intent.putExtra("patientAge", patientAge)
                        intent.putExtra("gender", gender)
                    }
                }
                // Sending the broadcast receiver
                sendBroadcast(intent)
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Updating the new token to the server
     * @param s
     */
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        //Todo : send token to server
//        System.out.println("new firebase token "+s);
        PrefUtility().saveStringInPref(this, Constants.SharedPrefConstants.FCM_TOKEN, s)

        // Update fcm key via "updateFcmkKey" API call
        try {
            updateTokenOnServer()
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Update fcm key via "updateFcmkKey" API call
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun updateTokenOnServer() {
        val userID: Long =
            PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, -1)
        if (userID != -1L) {
            val fcm: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FCM_TOKEN, "")
            //            Log.e(TAG, "updateTokenOnServer: " );
//            CommonResponse response = EndPointBuilder.getLoginEndpoints().updateFcmkKey(userID, fcm).execute();
//            ApiClient.getApi(false,false).commonresponse(userID,fcm).enqueue(new Callback<CommonResponse>() {

            //sending body through data class
            val requestBody = UpdateFcmkKeyRequestBody(fcm, userID)
            ApiClient().getApi(true, true)?.commonresponse(requestBody)
                ?.enqueue(object : Callback<CommonResponse?> {
                    override fun onResponse(
                        call: Call<CommonResponse?>,
                        response: Response<CommonResponse?>
                    ) {
//                    Log.d(TAG, "onResponse: commonresponse "+response.code());
                    }

                    override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
//                    Log.e(TAG, "onFailure: commonresponse "+t.toString() );
                    }
                })
        }
    }

    /**
     * Getting the provider data JSON object
     * @param remoteMessage
     * @return
     */
    private fun getProvider(remoteMessage: RemoteMessage): JsonObject? {
        try {
            val provider = remoteMessage.data["Provider"]
            val parser = JsonParser()
            if (!TextUtils.isEmpty(provider)) {
                var `object`: JsonObject? = null
                if (provider != null) {
                    `object` = parser.parse(provider).asJsonObject
                }
                return `object`
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return null
    }

    /**
     * Getting the patient data JSON object
     * @param remoteMessage
     * @return
     */
    private fun getPatient(remoteMessage: RemoteMessage): JsonObject? {
        try {
            val patient = remoteMessage.data["Patient"]
            val parser = JsonParser()
            if (!TextUtils.isEmpty(patient)) {
                return parser.parse(patient).asJsonObject
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return null
    }

    /**
     * Getting the provider list data JSON object
     * @param remoteMessage
     * @return
     */
    private fun getProviderList(remoteMessage: RemoteMessage): JSONArray? {
        try {
            val patient = remoteMessage.data["ProviderList"]
            val parser = JsonParser()
            if (!TextUtils.isEmpty(patient)) {
                return JSONArray(patient)
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return null
    }

    /**
     * Getting the message data as string value from the Remote message
     * @param remoteMessage
     * @return
     */
    private fun getMessage(remoteMessage: RemoteMessage): String? {
        var message: String? = null
        try {
            message = remoteMessage.data["message"]
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return message
    }

    private fun getAuditId(remoteMessage: RemoteMessage): String? {
        var auditId: String? = null
        try {
            auditId = remoteMessage.data["auditId"]
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return auditId
    }

    /**
     * Getting the message type as string value from the Remote message
     * @param remoteMessage
     * @return
     */
    private fun getMessageType(remoteMessage: RemoteMessage): String? {
        var title: String? = null
        title = try {
            remoteMessage.data["messageType"]
        } catch (e: Exception) {
            return ""
        }
        return title
    }

    /**
     * Getting the title as string value from the Remote message
     * @param remoteMessage
     * @return
     */
    private fun getTitle(remoteMessage: RemoteMessage): String? {
        var title: String? = null
        try {
            title = remoteMessage.data["title"]
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return title
    }

    /**
     * Showing the message notification based on the remote mesage object received
     * @param remoteMessage
     */
    private fun sendMessageNotification(remoteMessage: RemoteMessage) {
        //{Provider={"id":5746980898734080,"name":"mehar chand","email":"mchand@unifytech.com","role":"BD","phone":"8209024857"}, messageType=new_message, Patient={"id":5765243099676672,"name":"John Doe","bdProviderId":5746980898734080,"bdProviderName":"Mehar","gender":"Male","email":"john.doe@gmail.com","dob":1086122563000,"address":"Gurgaon","phone":"9999988888","countryCode":"91","hospital":"Madigan Army Medical Center","hospitalId":5077875795427328,"Status":"Active","joiningTime":1587189432342,"bed":"101"}, message=ggh}
        //If chat screen is already open,don't send message notification
        try {
            val currentContext: Context? = (applicationContext as OmnicureApp).getCurrentContext()
            if (currentContext != null && currentContext is ChatActivity) {
                return
            }
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            if (patientObj != null) {
                val message = getMessage(remoteMessage)
                val title = getTitle(remoteMessage)

                // Input intent values
                if (providerObj != null) {
                    val providerId = if (providerObj.has("name")) providerObj["id"].asLong else 0
                    val providerName =
                        if (providerObj.has("name")) providerObj["name"].asString else ""
                }
                val patientId =
                    if (patientObj.has("patientId")) patientObj["patientId"].asLong else 0
                val consultId = if (patientObj.has("id")) patientObj["id"].asLong else 0
                val patientName = if (patientObj.has("name")) patientObj["name"].asString else ""
                val dob = if (patientObj.has("dob")) patientObj["dob"].asLong else 0
                val gender = if (patientObj.has("gender")) patientObj["gender"].asString else ""
                val status = if (patientObj.has("Status")) patientObj["Status"].asString else ""
                val notes = if (patientObj.has("note")) patientObj["note"].asString else ""
                val teamName =
                    if (patientObj.has("teamName")) patientObj["teamName"].asString else ""

//            String title = "Message from " + providerName;

                // Intent redirected to Home Activity class
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("consultProviderId", consultId.toString())
                intent.putExtra("consultProviderPatientId", patientId.toString())
                intent.putExtra("uid", patientId)
                intent.putExtra("consultProviderText", message)
                intent.putExtra("consultProviderName", patientName)
                intent.putExtra("teamNameConsult", "Team $teamName")
                intent.putExtra("dob", dob)
                intent.putExtra("gender", gender)
                intent.putExtra("note", notes)
                intent.putExtra("status", status)
                if (status.equals(Constants.PatientStatus.Invited.toString(), ignoreCase = true)) {
                    intent.putExtra("invitation", true)
                } else if (status.equals(
                        Constants.PatientStatus.Completed.toString(),
                        ignoreCase = true
                    )
                ) {
                    intent.putExtra("completed", true)
                }
                intent.putExtra("path", "consults/$patientId")
                intent.putExtra("forward", ChatActivity::class.java.getSimpleName())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                val pendingIntent = PendingIntent.getActivity(
                    this, REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                val notificationHelper = NotificationHelper(applicationContext, null)
                notificationHelper.sendNotification(
                    pendingIntent,
                    title,
                    message,
                    Constants.NotificationIds.MSG_NOTIFICATION_ID
                )
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    var refreshButtonBroadcastReceiver: BroadcastReceiver? = null

    /**
     * Sending the remote message object with provider detail to ringing activity,
     * where the user get notified regarding the new video call
     * @param remoteMessage
     * @param sos
     * @param callType
     */
    private fun sendCall(remoteMessage: RemoteMessage, sos: Boolean?, callType: String) {
        //{Provider={"id":5746980898734080,"name":"mehar chand","email":"mchand@unifytech.com","role":"BD","phone":"8209024857"}, messageType=new_message, Patient={"id":5765243099676672,"name":"John Doe","bdProviderId":5746980898734080,"bdProviderName":"Mehar","gender":"Male","email":"john.doe@gmail.com","dob":1086122563000,"address":"Gurgaon","phone":"9999988888","countryCode":"91","hospital":"Madigan Army Medical Center","hospitalId":5077875795427328,"Status":"Active","joiningTime":1587189432342,"bed":"101"}, message=ggh}
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val channel = getMessage(remoteMessage)
            val auditId = getAuditId(remoteMessage)
            var providerName: String? = null
            var providerId: Long? = null
            var hospitalName: String? = null
            var providerType: String? = null
            var picUrl: String? = ""
            if (providerObj != null) {
                providerName = if (providerObj.has("name")) providerObj["name"].asString else ""
                providerId = if (providerObj.has("id")) providerObj["id"].asLong else 0
                hospitalName =
                    if (providerObj.has("hospital")) providerObj["hospital"].asString else ""
                providerType =
                    if (providerObj.has("remoteProviderType")) providerObj["remoteProviderType"].asString else ""
                if (providerObj.has("profilePicUrl")) {
                    picUrl = providerObj["profilePicUrl"].asString
                }
            }

//            Log.i(TAG, "provider details " + providerObj);

            // Intent with data to ringing activity
            val intent = Intent(this, RingingActivity::class.java)
            intent.putExtra("providerId", providerId)
            intent.putExtra("providerName", providerName)
            intent.putExtra("hospitalName", hospitalName)
            intent.putExtra("providerType", providerType)
            intent.putExtra("profilePicUrl", picUrl)
            intent.putExtra("call", callType)
            intent.putExtra("providerList", remoteMessage.data["ProviderList"])
            if (sos!!) {
                intent.putExtra("sos", true)
            }

//            System.out.println("auditidnotify "+auditId);
            intent.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME, channel)
            intent.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
            intent.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                resources.getStringArray(R.array.encryption_mode_values)[0]
            )
            intent.putExtra(Constants.IntentKeyConstants.AUDIT_ID, auditId)
            var patientId: Long? = null

            // If the patient object has non - null value
            if (patientObj != null) {
                patientId = patientObj["id"].asLong
                val patientName = patientObj["name"].asString
                val patientAge = patientObj["dob"].asLong
                val gender = patientObj["gender"].asString
                if (patientId != null) {
                    intent.putExtra("patientId", patientId)
                    intent.putExtra("patientName", patientName)
                    intent.putExtra("patientAge", patientAge)
                    intent.putExtra("gender", gender)
                }
            }


            //check current call
//            boolean checkCallFree = checkCurrentCall(channel, patientId, providerId, auditId);
            // Triggering the pending intent and refresh button broadcast receiver
            val checkCallFree = true
            if (checkCallFree) {
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val mNotificationManager =
                        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    val callAnswerIntent = PendingIntent.getBroadcast(
                        this, 0,
                        Intent("OnCallAnswer"), 0
                    )
                    val callCancelIntent = PendingIntent.getBroadcast(
                        this, 0,
                        Intent("OnCallCancel"), 0
                    )
                    val callNotAnswerIntent = PendingIntent.getBroadcast(
                        this, 0,
                        Intent("callNotAnswer"), 0
                    )
                    val callDefaultIntent = PendingIntent.getBroadcast(
                        this, 0,
                        Intent("OnDialogDismiss"), 0
                    )

                    //Register Receiver and receive the values of the user input
                    // Based on the status of the user, the scenarios types are sent to back- end via API call
                    // Types are "OnCallCancel", "callNotAnswer", "OnDialogDismiss"
                    val finalProviderId = providerId
                    refreshButtonBroadcastReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context, i: Intent) {
                            if (i.action.equals("OnCallAnswer", ignoreCase = true)) {
                                intent.setClass(context, CallActivity::class.java)
                                context.startActivity(intent)
                                sendAuditIdRetro(auditId, context, "", true)
                                context.unregisterReceiver(this)
                            } else if (i.action.equals("OnCallCancel", ignoreCase = true)) {
                                // Call reject action API call
                                sendCallResponseMessage(
                                    context,
                                    Constants.FCMMessageType.CALLER_REJECT,
                                    finalProviderId,
                                    channel,
                                    patientObj?.get("id")?.asLong,
                                    auditId
                                )
                                context.unregisterReceiver(this)
                                sendAuditIdRetro(
                                    auditId,
                                    context,
                                    Constants.FCMMessageType.CALLER_REJECT,
                                    false
                                )
                            } else if (i.action.equals("callNotAnswer", ignoreCase = true)) {
                                // Call not answer action API call
                                sendCallResponseMessage(
                                    context,
                                    Constants.FCMMessageType.CALLER_NOT_ANSWER,
                                    finalProviderId,
                                    channel,
                                    patientObj?.get("id")?.asLong,
                                    auditId
                                )
                                context.unregisterReceiver(this)
                                sendAuditIdRetro(
                                    auditId,
                                    context,
                                    Constants.FCMMessageType.CALLER_NOT_ANSWER,
                                    false
                                )
                            } else if (i.action.equals("OnDialogDismiss", ignoreCase = true)) {
                                // Unregistering the receiver
//                                System.out.println("coming ehre "+i.getAction());
                                intent.setClass(context, RingingActivity::class.java)
                                context.startActivity(intent)
                                context.unregisterReceiver(this)
                            }
                            // Enabling the stopForeground to "true"
                            stopForeground(true)
                            mNotificationManager.cancel(finalProviderId!!.toInt())
                            if (mAlarmManager != null) {
                                mAlarmManager!!.cancel(callNotAnswerIntent)
                                mAlarmManager = null
                            }
                        }
                    }

                    // Intent filter's are classified and added  with refresh button broadcast receiver
                    val callIntentFilter = IntentFilter("OnCallAnswer")
                    callIntentFilter.addAction("OnCallCancel")
                    callIntentFilter.addAction("callNotAnswer")
                    callIntentFilter.addAction("OnDialogDismiss")
                    this.registerReceiver(refreshButtonBroadcastReceiver, callIntentFilter)

                    //Notification channel intialization and relevant notification settings done here
                    var mChannel: NotificationChannel? = null
                    mChannel = NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        channelName,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    // Configure the notification channel.
                    mChannel.description = "Incoming call from $providerName"
                    mChannel.enableLights(true)
                    mChannel.lightColor = Color.RED
                    val vibrate = longArrayOf(
                        100,
                        200,
                        300,
                        400,
                        500,
                        400,
                        300,
                        200,
                        400,
                        800,
                        1600,
                        3200,
                        6400,
                        12800,
                        25600
                    )
                    mChannel.vibrationPattern = vibrate
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                    val soundUri =
                        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.ringtone)
                    mChannel.setSound(soundUri, audioAttributes)
                    mNotificationManager.createNotificationChannel(mChannel)
                    val remoteViews = RemoteViews(packageName, R.layout.customnotification)

                    // Notification builder initialization
                    val builder: NotificationCompat.Builder =
                        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setLargeIcon(
                                BitmapFactory.decodeResource(
                                    this.resources,
                                    R.drawable.ic_notification_color
                                )
                            )
                            .setAutoCancel(true)
                            .setContentIntent(callDefaultIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setAutoCancel(true)
                            .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/raw/ringtone.mp3"))
                            .setFullScreenIntent(callDefaultIntent, true)
                            .setChannelId(NOTIFICATION_CHANNEL_ID)
                            .setOngoing(true)
                            .setShowWhen(true)
                            .setContent(remoteViews)
                    remoteViews.setOnClickPendingIntent(R.id.decline, callCancelIntent)
                    remoteViews.setOnClickPendingIntent(R.id.accept, callAnswerIntent)
                    var callTypeStr = ""
                    callTypeStr = if (sos != null && sos) {
                        "Incoming SOS Call"
                    } else {
                        if (callType.equals(
                                Constants.FCMMessageType.AUDIO_CALL,
                                ignoreCase = true
                            )
                        ) {
                            "Incoming Audio Call"
                        } else {
                            "Incoming Video Call"
                        }
                    }
                    remoteViews.setTextViewText(R.id.call_type, callTypeStr)
                    remoteViews.setTextViewText(R.id.hospital_name, hospitalName)
                    remoteViews.setTextViewText(
                        R.id.provider_name,
                        providerName?.let { VideoViewAdapterUtil().getTrimmedText(it, 20) }
                    )
                    remoteViews.setViewVisibility(R.id.caller_image_text, View.VISIBLE)
                    remoteViews.setTextViewText(
                        R.id.caller_image_text,
                        providerName?.let { UtilityMethods().getNameText(it) }
                    )
                    if (!TextUtils.isEmpty(picUrl)) {
                        val bitmap = getCircleBitmap(picUrl)
                        if (bitmap != null) {
                            remoteViews.setImageViewBitmap(R.id.caller_image_view, bitmap)
                            remoteViews.setViewVisibility(R.id.caller_image_text, View.INVISIBLE)
                            remoteViews.setViewVisibility(R.id.caller_image_view, View.VISIBLE)
                        }
                    }
                    //                startForeground(providerId.intValue(), builder.build());
                    //                RemoteViews remoteViewSmall = new RemoteViews(FirebaseMessageService.this.getPackageName(),R.layout.notification_ringing_small);
                    //
                    //                RemoteViews remoteViewExpand = new RemoteViews(FirebaseMessageService.this.getPackageName(),R.layout.notification_ringing);
                    //                remoteViewExpand.setOnClickPendingIntent(R.id.accept,callAnswerIntent);
                    //                remoteViewExpand.setOnClickPendingIntent(R.id.decline,callCancelIntent);
                    //                NotificationCompat.Builder notificationBuilder =
                    //                        new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    //                                .setSmallIcon(R.drawable.video_icon)
                    //                                .setContentTitle("Incoming call from "+providerName)
                    //                                .setContentText(hospitalName)
                    ////                                .setCustomContentView(remoteViewSmall)
                    ////                                .setCustomBigContentView(remoteViewExpand)
                    ////                                .setContent(remoteViewExpand)
                    ////                                .addAction(R.drawable.ic_close, "Cancel", callCancelIntent)
                    ////                                .addAction(R.drawable.ic_answer, "Answer", callAnswerIntent)
                    //                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                    //                                .setCategory(NotificationCompat.CATEGORY_CALL)
                    //                                .setAutoCancel(true)
                    //                                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/ringtone.mp3"))
                    //                                .setContentIntent(callDefaultIntent)
                    //                                // setting style to DecoratedCustomViewStyle() is necessary for custom views to display
                    //                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    //                                .setFullScreenIntent(callDefaultIntent, true);
                    //
                    //                 Notification incomingCallNotification = notificationBuilder.build();
                    //
                    //                startForeground(providerId.intValue(), incomingCallNotification);
                    // retrieves android.app.NotificationManager

                    // Setting the alarm manager
                    val notificationManager =
                        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    if (providerId != null) {
                        notifyId = providerId.toInt()
                        notificationManager.notify(providerId.toInt(), builder.build())
                    }
                    mAlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    mAlarmManager!!.setExact(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 50 * 1000,
                        callNotAnswerIntent
                    )
                } else {
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception-issue:", e.getCause());
        }
    }

    /**
     * Getting the circle bitmap with Image URL received
     * @param imageURL
     * @return
     */
    private fun getCircleBitmap(imageURL: String?): Bitmap? {
        var bitmap: Bitmap?
        try {
            val connection = URL(imageURL).openConnection()
            connection.connectTimeout = 10000
            bitmap = BitmapFactory.decodeStream(connection.getInputStream())
        } catch (e: Exception) {
            bitmap = null
            //            Log.e(TAG, "Exception:", e.getCause());
        }
        var output: Bitmap? = null
        if (bitmap != null) {
            val srcRect: Rect
            val dstRect: Rect
            val r: Float
            val width = bitmap.width
            val height = bitmap.height
            if (width > height) {
                output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888)
                val left = (width - height) / 2
                val right = left + height
                srcRect = Rect(left, 0, right, height)
                dstRect = Rect(0, 0, height, height)
                r = height / 2f
            } else {
                output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
                val top = (height - width) / 2
                val bottom = top + width
                srcRect = Rect(0, top, width, bottom)
                dstRect = Rect(0, 0, width, width)
                r = width / 2f
            }
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawCircle(r, r, r, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, srcRect, dstRect, paint)

            //draw boarder
            val boarderPaint = Paint()
            boarderPaint.color = Color.parseColor("#3E61A4")
            boarderPaint.strokeWidth = 10f
            boarderPaint.style = Paint.Style.STROKE
            boarderPaint.isAntiAlias = true
            boarderPaint.isDither = true
            canvas.drawCircle(r, r, r - 5, boarderPaint)


            //draw boarder end
            bitmap.recycle()
        }
        return output
    }


    /*   private boolean checkCurrentCall(String channel, Long patientId, Long providerId, boolean sos, String auditId) {
        String token = PrefUtility.getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "");
        if (!sos && RingingActivity.screenOn) {
            sendBusyMessage(PrefUtility.getProviderId(this), token, providerId, patientId, channel, auditId);
            return false;
        } else if (!sos && mAlarmManager != null) {
            sendBusyMessage(PrefUtility.getProviderId(this), token, providerId, patientId, channel, auditId);
            return false;
        }

//        String currentChannel = ((OmnicureApp) getApplication()).config().mChannel;
        String currentChannel = ((OmnicureApp) getApplication()).config().getmChannel();
        if (currentChannel != null && currentChannel.equalsIgnoreCase(channel)) {
            //he is on same channel do nothing
            return false;
        } else if (currentChannel != null) {
            //Busy on another call send fcm message to caller
            if (!sos) {
                sendBusyMessage(PrefUtility.getProviderId(this), token, providerId, patientId, channel, auditId);
            }
            return false;
        } else {
            return true;
        }
    } */

    /*   private boolean checkCurrentCall(String channel, Long patientId, Long providerId, boolean sos, String auditId) {
        String token = PrefUtility.getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "");
        if (!sos && RingingActivity.screenOn) {
            sendBusyMessage(PrefUtility.getProviderId(this), token, providerId, patientId, channel, auditId);
            return false;
        } else if (!sos && mAlarmManager != null) {
            sendBusyMessage(PrefUtility.getProviderId(this), token, providerId, patientId, channel, auditId);
            return false;
        }

//        String currentChannel = ((OmnicureApp) getApplication()).config().mChannel;
        String currentChannel = ((OmnicureApp) getApplication()).config().getmChannel();
        if (currentChannel != null && currentChannel.equalsIgnoreCase(channel)) {
            //he is on same channel do nothing
            return false;
        } else if (currentChannel != null) {
            //Busy on another call send fcm message to caller
            if (!sos) {
                sendBusyMessage(PrefUtility.getProviderId(this), token, providerId, patientId, channel, auditId);
            }
            return false;
        } else {
            return true;
        }
    } */
    /**
     * Sending busy message as status of the user to back-end via API
     * @param callerId
     * @param token
     * @param receiverId
     * @param patientId
     * @param channel
     */
    private fun sendBusyMessage(
        callerId: Long,
        token: String,
        receiverId: Long,
        patientId: Long,
        channel: String,
        auditId: String
    ) {
        val errMsg = arrayOfNulls<String>(1)
        val requestBody = SendMsgNotifyRequestBody()
        requestBody.setProviderId(callerId) //id key
        requestBody.setToken(token)
        requestBody.setReceiverId(receiverId)
        requestBody.setMessage(channel)
        requestBody.setType(Constants.FCMMessageType.CALLER_BUSY)
        requestBody.setPatientId(patientId)
        ApiClient().getApiProviderEndpoints(true, true)?.sendMessageNotification(requestBody)
           ?.enqueue(object : Callback<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?> {
                override fun onResponse(
                    call: Call<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>,
                    response: Response<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>
                ) {
//                Log.e(TAG, "onResponse: sendMessageNotification "+ response.toString());
                    if (response.body() != null) {
//                    Log.e(TAG, "onResponse: sendMessageNotification status "+ response.body().getStatus());
                    }
                }

                override fun onFailure(
                    call: Call<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>,
                    t: Throwable
                ) {
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString() else if (t is Exception) errMsg[0] =
                        Constants.API_ERROR
                }
            })

        /*new Thread(new Runnable() {
            String errMsg = "";

            @Override
            public void run() {
                try {
                    final ProviderEndpoints.SendMessageNotification apiCall = EndPointBuilder.getProviderEndpoints()
                            .sendMessageNotification(callerId, token, receiverId, channel, Constants.FCMMessageType.CALLER_BUSY, auditId);
                    apiCall.setPatientsId(patientId);
                    omnicure.mvp.com.providerEndpoints.model.CommonResponse commonResponse = apiCall.execute();
                } catch (SocketTimeoutException e) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString();
                } catch (Exception e) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR;

                }

            }
        }).start();*/
    }

    /**
     * Sending the user's call response to the back-end via API
     * @param context
     * @param messageType
     * @param receiverId
     * @param channelName
     * @param patientId
     */
    private fun sendCallResponseMessage(
        context: Context,
        messageType: String,
        receiverId: Long?,
        channelName: String?,
        patientId: Long?,
        auditId: String?
    ) {
        val token: String? =
            PrefUtility().getStringInPref(context, Constants.SharedPrefConstants.TOKEN, "")
        val errMsg = arrayOfNulls<String>(1)
        val requestBody = SendMsgNotifyRequestBody()
        requestBody.setProviderId(PrefUtility().getProviderId(context)) //id key
        requestBody.setToken(token)
        requestBody.setReceiverId(receiverId)
        requestBody.setMessage(channelName)
        requestBody.setType(messageType)
        requestBody.setPatientId(patientId)
        ApiClient().getApiProviderEndpoints(true, true)?.sendMessageNotification(requestBody)
            ?.enqueue(object : Callback<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?> {
                override fun onResponse(
                    call: Call<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>,
                    response: Response<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>
                ) {
//                Log.e(TAG, "onResponse: sendMessageNotification "+ response.toString());
                    if (response.body() != null) {
//                    Log.e(TAG, "onResponse: sendMessageNotification status "+ response.body().getStatus());
                    }
                }

                override fun onFailure(
                    call: Call<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>,
                    t: Throwable
                ) {
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString() else if (t is Exception) errMsg[0] =
                        Constants.API_ERROR
                }
            })

        /*new Thread(new Runnable() {
            String errMsg = "";

            @Override
            public void run() {
                try {
                    String token = PrefUtility.getStringInPref(context, Constants.SharedPrefConstants.TOKEN, "");
                    final ProviderEndpoints.SendMessageNotification apiCall = EndPointBuilder.getProviderEndpoints()
                            .sendMessageNotification(PrefUtility.getProviderId(context), token, receiverId, channelName, messageType, auditId);

                    if (patientId != null) {
                        apiCall.setPatientsId(patientId);
                    }
                    omnicure.mvp.com.providerEndpoints.model.CommonResponse commonResponse = apiCall.execute();
                } catch (SocketTimeoutException e) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString();
                } catch (Exception e) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR;

                }
            }
        }).start();*/
    }

    private fun sendAuditIdRetro(auditId: String?, context: Context, type: String, flag: Boolean) {
        val providerID: String = java.lang.String.valueOf(PrefUtility().getProviderId(context))

        // Parsing params in body
        /*HashMap<String, String> bodyValues = new HashMap<>();
        bodyValues.put("providerId", providerID);
        bodyValues.put("auditId", auditId);
        bodyValues.put("callStatus", String.valueOf(flag));
        bodyValues.put("type", type);*/
        val requestBody = auditId?.let { SaveAuditCallRequestBody(it, flag, type, providerID) }
        ApiClient().getApiProviderEndpoints(
            true,
            true
        ) //                .saveAuditCall(auditId,flag,providerID,type).enqueue(new Callback<CommonResponseRetro>() {
            ?.saveAuditCall(requestBody)?.enqueue(object : Callback<CommonResponseRetro?> {
                override fun onResponse(
                    call: Call<CommonResponseRetro?>,
                    response: Response<CommonResponseRetro?>
                ) {
//                Log.d(TAG, "onResponse: "+response.code());
                }

                override fun onFailure(call: Call<CommonResponseRetro?>, t: Throwable) {
//                Log.e(TAG, "onFailure: " + t.toString() );
                }
            })
    }

    private fun sendAuditId(auditId: String, context: Context, type: String?, flag: Boolean) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val providerID: String =
                        java.lang.String.valueOf(PrefUtility().getProviderId(context))
                    val providerResponse: ProviderEndpoints.SendAuditData =
                        EndPointBuilder().getProviderEndpoints()
                            .sendAuditId(auditId, flag, providerID)
                    if (type != null && !TextUtils.isEmpty(type)) {
                        providerResponse.setType(type)
                    }
                    val commonResponse: omnicurekotlin.example.com.providerEndpoints.model.CommonResponse =
                        providerResponse.execute()
                    //                    System.out.println("callauditResponseNotification "+type+" "+auditId+" "+flag+" "+commonResponse);
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR
                }
            }
        }).start()
    }

    private fun sendInviteNotification(remoteMessage: RemoteMessage) {
        //{Provider={"id":2,"name":"Alok Soni","email":"alok@clicbrics.com","role":"BD","phone":"8377944971","hospital":"Madigan Army Medical Center"}, messageType=Invitation, Patient={"id":5744149642870784,"name":"Patient test","note":"patient test notes","bdProviderId":2,"rdProviderId":6,"rdProviderName":"sarva daman singh","gender":"Male","dob":1587321000000,"phone":"1234567891","Status":"Invited","joiningTime":1587381036354}, message=Alok Soni need help for a patient Patient test}
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val message = getMessage(remoteMessage)
            val title = getTitle(remoteMessage)
            //            String title = getString(R.string.invitation);
            var patientId = 0
            if (patientObj != null) {
                patientId = patientObj["id"].asInt
            }
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.IS_INVITE_NOTIFICATION, true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(
                this, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationHelper = NotificationHelper(applicationContext, null)
            notificationHelper.sendNotification(pendingIntent, title, message, patientId)
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Handling Invite hide notification
     * @param remoteMessage
     */
    private fun hideInviteNotification(remoteMessage: RemoteMessage) {
        //{Provider={"id":2,"name":"Alok Soni","email":"alok@clicbrics.com","role":"BD","phone":"8377944971","hospital":"Madigan Army Medical Center"}, messageType=Invitation, Patient={"id":5744149642870784,"name":"Patient test","note":"patient test notes","bdProviderId":2,"rdProviderId":6,"rdProviderName":"sarva daman singh","gender":"Male","dob":1587321000000,"phone":"1234567891","Status":"Invited","joiningTime":1587381036354}, message=Alok Soni need help for a patient Patient test}
        try {
            val patientObj = getPatient(remoteMessage)
            var patientId = 0
            if (patientObj != null) {
                patientId = patientObj["id"].asInt
            }
            val notificationHelper = NotificationHelper(applicationContext, null)
            notificationHelper.clearNotification(patientId)
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Handling Invite accepted notification
     * Redirected to home activity
     * @param remoteMessage
     */
    private fun sendAcceptNotification(remoteMessage: RemoteMessage) {
        //{Provider={"id":51,"name":"Atul Soni","email":"atul@gmail.com","role":"BD","phone":"1231231230","hospital":"VA New York Harbor Healthcare System"}, messageType=InvitationAccept, Patient={"id":5744149642870784,"name":"Patient test","note":"patient test notes","bdProviderId":2,"rdProviderId":51,"rdProviderName":"Atul Soni","gender":"Male","dob":1587321000000,"phone":"1234567891","Status":"Active","joiningTime":1587381036354}, message=Atul Soni accepted your invitation for a patient Patient test}
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val message = getMessage(remoteMessage)
            val title = getTitle(remoteMessage)
            //            String title = getString(R.string.invitation_accepted);
            val intent = Intent(this, HomeActivity::class.java)
            putIntentExtra(intent, remoteMessage)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(
                this, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationHelper = NotificationHelper(applicationContext, null)
            notificationHelper.sendNotification(
                pendingIntent,
                title,
                message,
                Constants.NotificationIds.NOTIFICATION_ID
            )
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Handling Discharge notification
     * @param remoteMessage
     */
    private fun sendDischargeNotification(remoteMessage: RemoteMessage) {
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val message = getMessage(remoteMessage)
            val title = getTitle(remoteMessage)
            //            String title = getString(R.string.consulation_completed);
            val intent = Intent(this, HomeActivity::class.java)
            putIntentExtra(intent, remoteMessage)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(
                this, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationHelper = NotificationHelper(applicationContext, null)
            notificationHelper.sendNotification(
                pendingIntent,
                title,
                message,
                Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID
            )
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Handling the Hand-Off notification
     * @param remoteMessage
     */
    private fun sendHandOffNotification(remoteMessage: RemoteMessage) {
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val message = getMessage(remoteMessage)
            val title = getTitle(remoteMessage)
            //            String title = getString(R.string.handoff_initiated);
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putIntentExtra(intent, remoteMessage)
            val pendingIntent = PendingIntent.getActivity(
                this, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationHelper = NotificationHelper(applicationContext, null)
            notificationHelper.sendNotification(
                pendingIntent,
                title,
                message,
                Constants.NotificationIds.MSG_NOTIFICATION_ID
            )
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Handling the patient transfer notification
     * @param remoteMessage
     */
    private fun sendPatientTransferNotification(remoteMessage: RemoteMessage) {
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val message = getMessage(remoteMessage)
            val title = getTitle(remoteMessage)
            //            String title = getString(R.string.patient_transfer);
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(
                this, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationHelper = NotificationHelper(applicationContext, null)
            notificationHelper.sendNotification(
                pendingIntent,
                title,
                message,
                Constants.NotificationIds.MSG_NOTIFICATION_ID
            )
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Handling the system alert notification
     * @param remoteMessage
     */
    private fun sendSystemAlertNotification(remoteMessage: RemoteMessage) {
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val message = getMessage(remoteMessage)
            val title = getTitle(remoteMessage)
            //            String title = getString(R.string.system_alerts);
            val intent = Intent(this, SystemAlertActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationHelper = NotificationHelper(applicationContext, null)
            notificationHelper.sendNotification(pendingIntent, title, message, Constants.NotificationIds.MSG_NOTIFICATION_ID)
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Handling normal notification based on the remote message object
     * Redirected to home activity
     * @param remoteMessage
     * @param title
     */
    private fun sendNotification(remoteMessage: RemoteMessage, title: String?) {
        try {
            val providerObj = getProvider(remoteMessage)
            val patientObj = getPatient(remoteMessage)
            val message = getMessage(remoteMessage)
            val intent = Intent(this, HomeActivity::class.java)
            putIntentExtra(intent, remoteMessage)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationHelper = NotificationHelper(applicationContext, null)
            notificationHelper.sendNotification(pendingIntent, title, message, Constants.NotificationIds.MSG_NOTIFICATION_ID)
            //            Log.d(TAG, "Patient Assigned Title" + title);
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /**
     * Checking the application is in foreground
     * Returns the boolean value
     * @return
     */
    private fun applicationInForeground(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.runningAppProcesses
        var isActivityFound = false
        if (services[0].processName
                .equals(packageName, ignoreCase = true) && services[0].importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        ) {
            isActivityFound = true
        }
        return isActivityFound
    }

    private fun getTopActivity(): String? {
        val am =
            getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val cn = am.getRunningTasks(1)[0].topActivity
        return cn!!.shortClassName
    }

    /**
     * Sending data intent to Notification activity
     * @param remoteMessage
     */
    private fun sendDeniedUserNotification(remoteMessage: RemoteMessage) {
        try {
            val message = getMessage(remoteMessage)
            val messageType = remoteMessage.data["messageType"]

//            String title = "";
//            if(messageType.equals(Constants.FCMMessageType.LOGOUT_EXCEEDED)){
//                title = getString(R.string.login_exceeded);
//            }else if(messageType.equals(Constants.FCMMessageType.LOCKED_USER)){
//                title = getString(R.string.user_locked_title);
//            }else {
//                title = getString(R.string.denied_user);
//            }
            val title = getTitle(remoteMessage)

            // If application not in foreground(Background) - Send notification
            if (!applicationInForeground()) {
                sendNotification(remoteMessage, title)
                return
            }
            //            if (getTopActivity().contains("NotificationActivity")) {
//                return;
//            }

            // Redirected to Notification activity if the message type is in the form of denial
            val dialogIntent = Intent(this, NotificationActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            dialogIntent.putExtra("remoteTitle", title)
            dialogIntent.putExtra("remoteMessage", message)
            dialogIntent.putExtra("messageType", messageType)
            startActivity(dialogIntent)
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

}
