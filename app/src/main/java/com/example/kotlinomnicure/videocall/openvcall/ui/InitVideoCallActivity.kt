package com.example.kotlinomnicure.videocall.openvcall.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.videocall.openvcall.model.CurrentUserSettings
import org.slf4j.LoggerFactory

class InitVideoCallActivity: BaseActivity() {

    private val log = LoggerFactory.getLogger(InitVideoCallActivity::class.java)
    var mProviderName: String? = null
    var mProviderHospitalName:kotlin.String? = null
    var mChannelName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init_video_call)
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        supportActionBar!!.hide()
        val ab = supportActionBar
        if (ab != null) {
            ab.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            ab.setCustomView(R.layout.ard_agora_actionbar)
        }
        //        mProviderName = getIntent().getStringExtra("providerName");
//        mProviderHospitalName = getIntent().getStringExtra("providerHospitalName");
//        mChannelName = getIntent().getStringExtra("channelName");
        forwardToRoom()
    }

    override fun initUIandEvent() {}

    override fun deInitUIandEvent() {}

    fun onClickAccept(view: View?) {
        forwardToRoom()
    }

    fun onClickDecline(view: View?) {
        finish()
    }

    fun onClickSettings(view: View?) {
        val i = Intent(this, SettingsActivity::class.java)
        startActivity(i)
    }

    fun forwardToRoom() {
        /*CurrentUserSettings.mChannelName = getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);//"omnicuretest";
        CurrentUserSettings.mEncryptionKey = "";*/
        //changed to private fields
        intent.getStringExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME)?.let {
            CurrentUserSettings().setmChannelName(
                it
            )
        } //"omnivores";
        CurrentUserSettings().setmEncryptionKey("")
        val i = Intent(this@InitVideoCallActivity, CallActivity::class.java)
        i.putExtra(
            ConstantApp().ACTION_KEY_CHANNEL_NAME,
            intent.getStringExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME)
        )
        i.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
        //        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, getResources().getStringArray(R.array.encryption_mode_values)[CurrentUserSettings.mEncryptionModeIndex]);
        i.putExtra(
            ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
            resources.getStringArray(R.array.encryption_mode_values)[CurrentUserSettings().getmEncryptionModeIndex()]
        )
        if (intent.hasExtra("providerList")) {
            i.putStringArrayListExtra(
                "providerList",
                intent.getStringArrayListExtra("providerList")
            )
        }
        i.putExtra("providerName", intent.getStringExtra("providerName"))
        i.putExtra("providerHospitalName", intent.getStringExtra("hospitalName"))
        if (intent.hasExtra("patientId")) {
            i.putExtra("patientId", intent.getStringExtra("patientId"))
        }
        if (intent.hasExtra("profilePicUrl")) {
            i.putExtra("profilePicUrl", intent.getStringExtra("profilePicUrl"))
        }
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
        finish()
    }


}
