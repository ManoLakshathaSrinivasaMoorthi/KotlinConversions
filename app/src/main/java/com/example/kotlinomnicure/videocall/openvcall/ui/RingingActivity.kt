package com.example.kotlinomnicure.videocall.openvcall.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent

import android.content.IntentFilter

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Buildconfic
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.videocall.openvcall.model.CurrentUserSettings
import com.example.kotlinomnicure.viewmodel.CallActivityViewModel
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.requestbodys.SendMsgNotifyRequestBody
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*

class RingingActivity:BaseActivity() {
    private val log = LoggerFactory.getLogger(RingingActivity::class.java)
    private val TAG = "RingingAct"
    private var screenOn = false
    var player: MediaPlayer? = null
    private var mHandlerRinging: Handler? = null
    private var mRunnable: Runnable? = null
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var mReceiver: ScreenReceiver? = null
    var mProviderList: ArrayList<Provider> = ArrayList<Provider>()
    private var mAcceptClickTime = System.currentTimeMillis()
    var auditId: String? = null

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringing)
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        supportActionBar!!.hide()
        val ab = supportActionBar
        if (ab != null) {
            ab.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            ab.setCustomView(R.layout.ard_agora_actionbar)
        }
        val sosImageView = findViewById<ImageView>(R.id.sos_icon)
        val patient_title = findViewById<TextView>(R.id.patient_title)

        //appname
        val header = findViewById<TextView>(R.id.header)
        header.text = java.lang.String.format(getString(R.string.omnicure_now), Buildconfic().value())
        val patientInfoLayout = findViewById<LinearLayout>(R.id.patient_info_layout)
        if (intent.hasExtra("sos")) {
            sosImageView.visibility = View.VISIBLE
            patientInfoLayout.setBackgroundResource(R.color.sos_red)
            patient_title.setText(R.string.sos_alert)
        } else {
            sosImageView.visibility = View.GONE
            patientInfoLayout.setBackgroundResource(R.drawable.blue_gradient)
            patient_title.setText(R.string.patient_information)
        }
        val imageButton = findViewById<ImageButton>(R.id.ringing_call_icon)
        if (intent.hasExtra("call") && intent.getStringExtra("call")
                .equals(Constants.FCMMessageType.AUDIO_CALL)
        ) {
            imageButton.setImageResource(R.drawable.ic_audio_call_white)
        } else {
            imageButton.setImageResource(R.drawable.video_icon)
        }
        val caller_image_view: CircularImageView =
            findViewById(R.id.caller_image_view)
        caller_image_view.visibility = View.GONE
        val caller_image_text = findViewById<TextView>(R.id.caller_image_text)
        val caller_name = findViewById<TextView>(R.id.caller_name)
        val providerName = intent.getStringExtra("providerName")
        val providerType = intent.getStringExtra("providerType")
        var callType = "Incoming call"
        if (intent.hasExtra("call")) {
            if (intent.getStringExtra("call")
                    .equals(Constants.FCMMessageType.AUDIO_CALL)
            ) {
                callType = "Audio call"
            } else if (intent.getStringExtra("call")
                    .equals(Constants.FCMMessageType.VIDEO_CALL)
            ) {
                callType = "Video call"
            }
        }
        if (intent.hasExtra(Constants.IntentKeyConstants.AUDIT_ID)) {
            auditId = intent.getStringExtra(Constants.IntentKeyConstants.AUDIT_ID)
            auditId = if (auditId == null) "" else auditId
        }
        var pt = ""
        if (!TextUtils.isEmpty(providerType)) {
            pt = ", $providerType"
        }
        val callerTitle =
            """${if (intent.hasExtra("sos")) "SOS " + callType.lowercase(Locale.getDefault()) + " " else callType} from 
$providerName$pt"""

//        caller_name.setText(getIntent().getStringExtra("providerName"));
        caller_name.text = callerTitle
        caller_image_text.visibility = View.VISIBLE
        caller_image_text.text = intent.getStringExtra("providerName")?.let {
            UtilityMethods().getNameText(
                it
            )
        }
        val caller_hospital = findViewById<TextView>(R.id.caller_hospital)
        caller_hospital.text = intent.getStringExtra("hospitalName")
        caller_hospital.visibility = View.GONE
        val patient = findViewById<TextView>(R.id.id_patient_name)
        val patient_layout = findViewById<LinearLayout>(R.id.patient_layout)
        if (intent.hasExtra("patientAge")) {
            patient_layout.visibility = View.VISIBLE
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getDefault()
            calendar.timeInMillis = intent.getLongExtra("patientAge", 0)
            val currentCalendar = Calendar.getInstance()
            currentCalendar.timeZone = TimeZone.getDefault()
            currentCalendar.timeInMillis = System.currentTimeMillis()
            val age = currentCalendar[Calendar.YEAR] - calendar[Calendar.YEAR]
            val gender = intent.getStringExtra("gender")
            patient.text = intent.getStringExtra("patientName")
                .toString() + " " + "\u2022" + " " + age + " " + gender
        } else {
            patient_layout.visibility = View.INVISIBLE
        }
        var afd: AssetFileDescriptor? = null
        try {
            afd = assets.openFd("ringtone.mp3")
            player = MediaPlayer()
            player!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player!!.prepare()
            player!!.isLooping = true
            player!!.start()
        } catch (e: Exception) {
//            Log.e("error", e.getMessage());
        } finally {
            if (afd != null) {
                try {
                    afd.close()
                } catch (e: IOException) {
//                    Log.e("error", e.getMessage());
                }
            }
        }
        if (intent.hasExtra("profilePicUrl") && !TextUtils.isEmpty(intent.getStringExtra("profilePicUrl"))) {
            val imageUrl = intent.getStringExtra("profilePicUrl")
            caller_image_view.visibility = View.VISIBLE
            caller_image_text.visibility = View.GONE
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(caller_image_view)
                //                new RingingActivity.ImageLoader(RingingActivity.this, imageUrl).execute();
            }
        }
        mHandlerRinging = Handler()
        mRunnable = Runnable {
            sendCallResponseMessage(Constants.FCMMessageType.CALLER_NOT_ANSWER)
            sendAuditId(Constants.FCMMessageType.CALLER_NOT_ANSWER, false)
        }
        mHandlerRinging!!.postDelayed(mRunnable!!, 60 * 1000L)
        // INITIALIZE RECEIVER
        registerScreenBroadCast()
        registerBroadCast()
    }

    private fun registerBroadCast() {
        if (mReceiver == null) {
            mReceiver = ScreenReceiver()
        }
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        filter.addAction("caller-reject")
        filter.addAction("caller-not-answer")
        filter.addAction("sos-dismiss")
        registerReceiver(mReceiver, filter)
    }

    private fun registerScreenBroadCast() {
//        if (mBroadcastReceiver == null) {
//            mBroadcastReceiver = new ScreenReceiver();
//        }
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        filter.addAction("caller-reject");
//        filter.addAction("caller-not-answer");
//        filter.addAction("sos-dismiss");
//
//        registerReceiver(mBroadcastReceiver, filter);
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    val providerName = intent.getStringExtra("providerName")
                    if (intent.hasExtra("providerList")) {
                        val gson = Gson()
                        val providerListStr = intent.getStringExtra("providerList")
                        try {
                            val providerArray = JSONArray(providerListStr)
                            for (i in 0 until providerArray.length()) {
                                val providerObj = providerArray[i] as JSONObject
                                val provider = Provider()
                                provider.setId(providerObj.getLong("id"))
                                provider.setName(providerObj.getString("name"))
                                if (providerObj.has("profilePicUrl")) {
                                    provider.setProfilePicUrl(providerObj.getString("profilePicUrl"))
                                }
                                if (providerObj.has("hospital")) {
                                    provider.setHospital(providerObj.getString("hospital"))
                                }
                                provider.setRole(providerObj.getString("role"))
                                mProviderList.add(provider)
                            }
                        } catch (e: Exception) {
//                            Log.e(TAG, "Exception:", e.getCause());
                        }
                    }
                    val providerObjString = intent.getStringExtra("providerList")
                    val providerListNotification: ArrayList<Provider> = ArrayList<Provider>()
                    try {
                        val providerArray = JSONArray(providerObjString)
                        for (i in 0 until providerArray.length()) {
                            val providerObj = providerArray[i] as JSONObject
                            val provider = Provider()
                            provider.setId(providerObj.getLong("id"))
                            provider.setName(providerObj.getString("name"))
                            if (providerObj.has("profilePicUrl")) {
                                provider.setProfilePicUrl(providerObj.getString("profilePicUrl"))
                            }
                            if (providerObj.has("hospital")) {
                                provider.setHospital(providerObj.getString("hospital"))
                            }
                            if (providerObj.has("role")) {
                                provider.setRole(providerObj.getString("role"))
                            }
                            providerListNotification.add(provider)
                        }
                    } catch (e: Exception) {
//                        Log.e(TAG, "Exception:", e.getCause());
                    }
                    var groupCall = false
                    //                    Log.i("RingingActivity", "provider_list " + mProviderList);
                    if (providerListNotification.size > 2) {
                        groupCall = true
                    } else if (getIntent().hasExtra("sos")) {
                        groupCall = true
                    }
                    when (action) {
                        "caller-busy" -> {
                            if (!groupCall) {
                                var afd: AssetFileDescriptor? = null
                                try {
                                    if (player != null) {
                                        player!!.stop()
                                        player!!.reset()
                                        player = null
                                    }
                                    afd = assets.openFd("busytone.mp3")
                                    if (player == null) {
                                        player = MediaPlayer()
                                    }
                                    player!!.setDataSource(
                                        afd.fileDescriptor,
                                        afd.startOffset,
                                        afd.length
                                    )
                                    player!!.prepare()
                                    player!!.isLooping = false
                                    player!!.start()
                                } catch (e: Exception) {
//                                    Log.e(TAG, "Exception:", e.getCause());
                                } finally {
                                    if (afd != null) {
                                        try {
                                            afd.close()
                                        } catch (e: IOException) {
//                                            Log.e("error", e.getMessage());
                                        }
                                    }
                                }
                                val connection_message = findViewById<TextView>(R.id.connection_message)
                                Handler().postDelayed({ finish() }, 2000)
                            }
                        }
                        "caller-reject" -> {
                            if (!groupCall) {
                                var afd: AssetFileDescriptor? = null
                                try {
                                    if (player != null) {
                                        player!!.stop()
                                        player!!.stop()
                                        player!!.reset()
                                        player = null
                                    }
                                    afd = assets.openFd("busytone.mp3")
                                    if (player == null) {
                                        player = MediaPlayer()
                                    }
                                    player!!.setDataSource(
                                        afd.fileDescriptor,
                                        afd.startOffset,
                                        afd.length
                                    )
                                    player!!.prepare()
                                    player!!.isLooping = false
                                    player!!.start()
                                } catch (e: Exception) {
//                                    Log.e(TAG, "Exception:", e.getCause());
                                } finally {
                                    if (afd != null) {
                                        try {
                                            afd.close()
                                        } catch (e: IOException) {
//                                            Log.e("error", e.getMessage());
                                        }
                                    }
                                }
                                val connection_message =
                                    findViewById(R.id.connection_message) as TextView
                                sendAuditId(Constants.FCMMessageType.CALLER_REJECT, false)
                                Handler().postDelayed({ finish() }, 2000)
                            }
                        }
                        "caller-not-answer" -> {
                            if (!groupCall) {
                                var afd: AssetFileDescriptor? = null
                                try {
                                    if (player != null) {
                                        player!!.stop()
                                        player!!.stop()
                                        player!!.reset()
                                        player = null
                                    }
                                    afd = assets.openFd("busytone.mp3")
                                    if (player == null) {
                                        player = MediaPlayer()
                                    }
                                    player!!.setDataSource(
                                        afd.fileDescriptor,
                                        afd.startOffset,
                                        afd.length
                                    )
                                    player!!.prepare()
                                    player!!.isLooping = false
                                    player!!.start()
                                } catch (e: Exception) {
//                                    Log.e(TAG, "Exception:", e.getCause());
                                } finally {
                                    if (afd != null) {
                                        try {
                                            afd.close()
                                        } catch (e: IOException) {
//                                            Log.e("error", e.getMessage());
                                        }
                                    }
                                }
                                val connection_message =
                                    findViewById(R.id.connection_message) as TextView
                                Handler().postDelayed({ finish() }, 2000)
                            }
                        }
                    }
                }
            }
            val filter1 = IntentFilter("caller-busy")
            filter1.addAction("caller-reject")
            filter1.addAction("caller-not-answer")
            registerReceiver(mBroadcastReceiver, filter1)
        }
    }

    private fun unregisterScreenBroadCast() {
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver)
            mBroadcastReceiver = null
        }
    }

    private fun unregisterBroadCast() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
            mReceiver = null
        }
    }

    private fun sendAuditId(type: String, flag: Boolean) {
//        System.out.println("coming hereee");
        val providerID: String =
            java.lang.String.valueOf(PrefUtility().getProviderId(this@RingingActivity))
        auditId?.let {
            CallActivityViewModel().sendAuditId(it, flag, providerID, type)
                ?.observe(this@RingingActivity) { commonResponse ->
    //            System.out.println("callauditResponseRinging "+auditId+" "+commonResponse);
                    dismissProgressBar()
                    if (commonResponse != null && commonResponse.getStatus()) {
                        //Do nothing
                    } else {
    //                log.info("sos dismiss message: " + commonResponse.getErrorMessage());
                    }
                }
        }
    }

    override fun onStart() {
        screenOn = true
        super.onStart()
    }

    override fun initUIandEvent() {}

    override fun deInitUIandEvent() {}

    fun onClickAccept(view: View?) {
        if (System.currentTimeMillis() - mAcceptClickTime < 1000) {
            return
        }
        mAcceptClickTime = System.currentTimeMillis()
        Handler(Looper.getMainLooper()).post { sendAuditId("", true) }
        forwardToRoom()
    }

    fun onClickDecline(view: View?) {
        sendCallResponseMessage(Constants.FCMMessageType.CALLER_REJECT)
        sendAuditId(Constants.FCMMessageType.CALLER_REJECT, false)
    }

    private fun sendCallResponseMessage(messageType: String) {
        showProgressBar(getString(R.string.disconnecting))

        /* String token = PrefUtility.getStringInPref(context, Constants.SharedPrefConstants.TOKEN, "");

        Call<CommonResponse> call = ApiClient.getApiProviderEndpoints(false, false).sendCallResponseMessage(PrefUtility.getProviderId(context), token, receiverId, channelName, messageType);
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<omnicure.mvp.com.providerEndpoints.model.CommonResponse> call, Response<CommonResponse> response) {

                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<omnicure.mvp.com.providerEndpoints.model.CommonResponse> call, Throwable t) {

            }
        });*/
        val token: String? = PrefUtility().getStringInPref(
            this@RingingActivity,
            Constants.SharedPrefConstants.TOKEN,
            ""
        )
        val errMsg = arrayOfNulls<String>(1)
        val requestBody = SendMsgNotifyRequestBody()
        requestBody.setProviderId(PrefUtility().getProviderId(this@RingingActivity)) //id key
        requestBody.setToken(token)
        requestBody.setReceiverId(intent.getLongExtra("providerId", 0L))
        requestBody.setMessage(intent.getStringExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME))
        requestBody.setType(messageType)
        if (intent.hasExtra("patientId")) {
            requestBody.setPatientId(intent.getLongExtra("patientId", 0L))
        }
        ApiClient().getApiProviderEndpoints(encrypt = true, decrypt = true)?.sendMessageNotification(requestBody)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
//                Log.e(TAG, "onResponse: sendMessageNotification "+ response.toString());
                    if (response.body() != null) {
//                    Log.e(TAG, "onResponse: sendMessageNotification status "+ response.body().getStatus());
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
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
                    String token = PrefUtility.getStringInPref(RingingActivity.this, Constants.SharedPrefConstants.TOKEN, "");
                    final ProviderEndpoints.SendMessageNotification apiCall = EndPointBuilder.getProviderEndpoints()
                            .sendMessageNotification(PrefUtility.getProviderId(RingingActivity.this), token, getIntent().getLongExtra("providerId", 0l), getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME), messageType, auditId);

                    if (getIntent().hasExtra("patientId")) {
                        apiCall.setPatientsId(getIntent().getLongExtra("patientId", 0l));
                    }
                    CommonResponse commonResponse = apiCall.execute();
                    Log.i("response", "response for busy notification : " + commonResponse.getStatus());
                } catch (SocketTimeoutException e) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString();
                } catch (Exception e) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR;

                }
                finish();
            }
        }).start();*/
    }

    fun onClickSettings(view: View?) {
        val i = Intent(this, SettingsActivity::class.java)
        startActivity(i)
    }
//    AssetFileDescriptor afd = getAssets().openFd("ringtone.mp3");

    //    AssetFileDescriptor afd = getAssets().openFd("ringtone.mp3");
    private fun forwardToRoom() {
        /*CurrentUserSettings.mChannelName = getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);//"omnicuretest";

        CurrentUserSettings.mEncryptionKey = "";*/
        //changed fields to private
        intent.getStringExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME)?.let {
            CurrentUserSettings().setmChannelName(
                it
            )
        } //"omnicuretest";
        CurrentUserSettings().setmEncryptionKey("")
        val intent = Intent(this@RingingActivity, CallActivity::class.java)
        intent.putExtra(
            ConstantApp().ACTION_KEY_CHANNEL_NAME,
            getIntent().getStringExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME)
        )
        intent.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
        intent.putExtra(
            ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
            resources.getStringArray(R.array.encryption_mode_values)[CurrentUserSettings().getmEncryptionModeIndex()]
        )
        intent.putExtra("providerName", getIntent().getStringExtra("providerName"))
        intent.putExtra(
            Constants.IntentKeyConstants.AUDIT_ID,
            getIntent().getStringExtra(Constants.IntentKeyConstants.AUDIT_ID)
        )
        intent.putExtra("providerHospitalName", getIntent().getStringExtra("hospitalName"))
        intent.putExtra("call", getIntent().getStringExtra("call"))
        intent.putExtra("providerList", getIntent().getStringExtra("providerList"))
        if (getIntent().hasExtra("patientId")) {
            intent.putExtra("patientId", getIntent().getLongExtra("patientId", 0L))
        }
        if (getIntent().hasExtra("sos")) {
            intent.putExtra("sos", getIntent().hasExtra("sos"))
        }
        if (getIntent().hasExtra("profilePicUrl")) {
            intent.putExtra("profilePicUrl", getIntent().getStringExtra("profilePicUrl"))
        }
        //        if(getIntent().hasExtra("sos")) {
//            i.putExtra("sos", getIntent().getBooleanExtra("sos",false));
//        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        if (player != null) {
            player!!.stop()
        }
        unregisterScreenBroadCast()
        unregisterBroadCast()
        if (mHandlerRinging != null && mRunnable != null) {
            mHandlerRinging!!.removeCallbacks(mRunnable!!)
        }
        super.onDestroy()
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_POWER || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            Log.i("", "Dispath event power");
            if (player != null) {
                player!!.stop()
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_POWER) {
//            Log.i("", "Dispath event power");
            if (player != null) {
                player!!.stop()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_POWER) {
//            Log.i("", "Dispath event power");
            if (player != null) {
                player!!.stop()
            }
        }
        return super.onKeyMultiple(keyCode, repeatCount, event)
    }

    override fun onStop() {
        screenOn = false
        super.onStop()
    }


    class ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                // do whatever you need to do here
                if (RingingActivity().player != null) {
                    RingingActivity().player?.stop()
                }
            } else if (intent.action.equals("sos-dismiss", ignoreCase = true)) {
                val channel = intent.getStringExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME)
                if (getIntent("")?.hasExtra("sos") == true && channel.equals(
                        getIntent("").getStringExtra(
                            ConstantApp().ACTION_KEY_CHANNEL_NAME
                        ), ignoreCase = true
                    )
                ) {
                   RingingActivity(). sendAuditId(Constants.FCMMessageType.CALLER_NOT_ANSWER, false)
                    Handler().postDelayed({ RingingActivity().finish() }, 1000)
                }
            }
        }
    }

}
