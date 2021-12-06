package com.example.kotlinomnicure.videocall.openvcall.ui

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlinomnicure.OmnicureApp
import com.example.kotlinomnicure.activity.BaseActivity
import com.example.kotlinomnicure.media.RtcTokenBuilder
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.videocall.openvcall.model.AGEventHandler
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.videocall.openvcall.model.CurrentUserSettings
import com.example.kotlinomnicure.videocall.openvcall.model.EngineConfig
import com.example.kotlinomnicure.videocall.propeller.Constant
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtc.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.RuntimeException

abstract class BaseActivity: BaseActivity() {

    private val log: Logger =
        LoggerFactory.getLogger(com.example.kotlinomnicure.videocall.openvcall.ui.BaseActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout: View = findViewById(Window.ID_ANDROID_CONTENT)
        val vto = layout.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    layout.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
                initUIandEvent()
            }
        })
    }

    protected abstract fun initUIandEvent()

    protected abstract fun deInitUIandEvent()

    protected open fun permissionGranted() {}

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Handler().postDelayed(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            val checkPermissionResult = checkSelfPermissions()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // so far we do not use OnRequestPermissionsResultCallback
            }
        }, 500)
    }

    open fun checkSelfPermissions(): Boolean {
        return checkSelfPermissionGrantedCheck(
            Manifest.permission.RECORD_AUDIO,
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO
        ) &&
                checkSelfPermissionGrantedCheck(
                    Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA
                ) &&
                checkSelfPermissionGrantedCheck(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
                )
    }

    override fun onDestroy() {
        deInitUIandEvent()
        super.onDestroy()
    }

    fun closeIME(v: View) {
        val mgr = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(v.windowToken, 0) // 0 force close IME
        v.clearFocus()
    }

    override fun checkSelfPermissionGrantedCheck(permission: String, requestCode: Int): Boolean {
//        log.debug("checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission),
                requestCode
            )
            return false
        }
        if (Manifest.permission.CAMERA == permission) {
            permissionGranted()
        }
        return true
    }

    protected open fun application(): OmnicureApp {
        return application as OmnicureApp
    }

    protected open fun rtcEngine(): RtcEngine? {
        return application().rtcEngine()
    }

    protected open fun config(): EngineConfig {
        return application().config()
    }

    protected open fun addEventHandler(handler: AGEventHandler?) {
        application().addEventHandler(handler)
    }

    protected open fun removeEventHandler(handler: AGEventHandler?) {
        application().remoteEventHandler(handler)
    }

    protected open fun vSettings(): CurrentUserSettings? {
        return application().userSettings()
    }

    fun showLongToast(msg: String?) {
        this.runOnUiThread {
            Toast.makeText(
                applicationContext,
                msg,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        log.debug("onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
        when (requestCode) {
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    checkSelfPermissionGrantedCheck(
                        Manifest.permission.CAMERA,
                        ConstantApp().PERMISSION_REQ_ID_CAMERA
                    )
                } else {
                    finish()
                }
            }
            ConstantApp().PERMISSION_REQ_ID_CAMERA -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    checkSelfPermissionGrantedCheck(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
                    )
                    permissionGranted()
                } else {
                    finish()
                }
            }
            ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                } else {
                    finish()
                }
            }
        }
    }

    protected open fun virtualKeyHeight(): Int {
        val hasPermanentMenuKey = ViewConfiguration.get(application).hasPermanentMenuKey()
        if (hasPermanentMenuKey) {
            return 0
        }

        // Also can use getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        val metrics = DisplayMetrics()
        val display: Display = windowManager.defaultDisplay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics)
        } else {
            display.getMetrics(metrics)
        }
        var fullHeight = metrics.heightPixels
        var fullWidth = metrics.widthPixels
        if (fullHeight < fullWidth) {
            fullHeight = fullHeight xor fullWidth
            fullWidth = fullWidth xor fullHeight
            fullHeight = fullHeight xor fullWidth
        }
        display.getMetrics(metrics)
        var newFullHeight = metrics.heightPixels
        var newFullWidth = metrics.widthPixels
        if (newFullHeight < newFullWidth) {
            newFullHeight = newFullHeight xor newFullWidth
            newFullWidth = newFullWidth xor newFullHeight
            newFullHeight = newFullHeight xor newFullWidth
        }
        var virtualKeyHeight = fullHeight - newFullHeight
        if (virtualKeyHeight > 0) {
            return virtualKeyHeight
        }
        virtualKeyHeight = fullWidth - newFullWidth
        return virtualKeyHeight
    }

    protected fun getStatusBarHeight(): Int {
        // status bar height
        var statusBarHeight = 0
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        if (statusBarHeight == 0) {
//            log.error("Can not get height of status bar");
        }
        return statusBarHeight
    }

    protected fun getActionBarHeight(): Int {
        // action bar height
        val actionBarHeight: Int
        val styledAttributes: TypedArray =
            this.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        if (actionBarHeight == 0) {
//            log.error("Can not get height of action bar");
        }
        return actionBarHeight
    }

    protected open fun preview(start: Boolean, view: SurfaceView?, uid: Int) {
        if (start) {
            rtcEngine()?.setupLocalVideo(VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            rtcEngine()?.startPreview()
        } else {
            rtcEngine()?.stopPreview()
        }
    }

    fun joinChannel(channel: String?, uid: Int) {
        val token = RtcTokenBuilder()
        val timestamp = (System.currentTimeMillis() / 1000 + 120).toInt()
        val appId: String? = PrefUtility().getAgoraAppId(this)
        val certId: String? = PrefUtility().getAgoraCertificateId(this)
        var accessToken: String? = token.buildTokenWithUid(
            appId, certId,
            channel, uid, RtcTokenBuilder.Role.Role_Publisher, timestamp
        )
        //        System.out.println("token agora "+accessToken);

//        Log.i("App","Agora id: "+appId+" "+channel);
        if (TextUtils.isEmpty(appId)) {
            throw RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/")
        }

//        String accessToken = getApplicationContext().getString(R.string.agora_access_token);
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(
                accessToken,
                "<#YOUR ACCESS TOKEN#>"
            )
        ) {
            accessToken = null // default, no token
        }
        rtcEngine()?.joinChannel(accessToken, channel, "OpenVCall", uid)
        rtcEngine()?.addHandler(mRtcEventHandler)
        /*config().getmChannel() = channel;
        config().getmUid() = uid;*/config().setmChannel(channel)
        config().setmUid(uid)
        enablePreProcessor()
//        log.debug("joinChannel " + channel + " " + uid);
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onTokenPrivilegeWillExpire(token: String) {
//            System.out.println("onTokenPrivilegeWillExpire_join "+config().getmChannel()+" "+config().getmUid()+" "+token);
            val newToken = RtcTokenBuilder()
            val timestamp = (System.currentTimeMillis() / 1000 + 120).toInt()
            val appId: String? = PrefUtility().getAgoraAppId(applicationContext)
            val certId: String? = PrefUtility().getAgoraCertificateId(applicationContext)

//            System.out.println("appid_agora "+appId);
            val accessToken: String? = newToken.buildTokenWithUid(
                appId,
                certId,
                config().getmChannel(),
                config().getmUid(),
                RtcTokenBuilder.Role.Role_Publisher,
                timestamp
            )
            //            System.out.println("renewed token agora "+accessToken);
            rtcEngine()?.renewToken(accessToken)
            super.onTokenPrivilegeWillExpire(token)
        }
    }


    fun leaveChannel(channel: String?) {
//        log.debug("leaveChannel " + channel);
//        config().getmChannel() = null;
        config().setmChannel(null)
        disablePreProcessor()
        rtcEngine()?.leaveChannel()
        config().reset()
    }

    protected open fun enablePreProcessor() {
//        if (Constant.BEAUTY_EFFECT_ENABLED) {
        if (Constant().isBeautyEffectEnabled()) {
            rtcEngine()?.setBeautyEffectOptions(true, Constant().BEAUTY_OPTIONS)
        }
    }

    fun setBeautyEffectParameters(lightness: Float, smoothness: Float, redness: Float) {
        Constant().BEAUTY_OPTIONS.lighteningLevel = lightness
        Constant().BEAUTY_OPTIONS.smoothnessLevel = smoothness
        Constant().BEAUTY_OPTIONS.rednessLevel = redness
    }

    protected open fun disablePreProcessor() {
        // do not support null when setBeautyEffectOptions to false
        rtcEngine()?.setBeautyEffectOptions(false, Constant().BEAUTY_OPTIONS)
    }

    protected open fun configEngine(
        videoDimension: VideoDimensions?,
        fps: FRAME_RATE?,
        encryptionKey: String?,
        encryptionMode: String?
    ) {
        if (!TextUtils.isEmpty(encryptionKey)) {
            rtcEngine()?.setEncryptionMode(encryptionMode)
            rtcEngine()?.setEncryptionSecret(encryptionKey)
        }

//        log.debug("configEngine " + videoDimension + " " + fps + " " + encryptionMode);
        rtcEngine()?.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                videoDimension,
                fps,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }
}
