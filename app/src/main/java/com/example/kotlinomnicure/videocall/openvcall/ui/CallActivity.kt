package com.example.kotlinomnicure.videocall.openvcall.ui

import android.content.*
import android.content.res.AssetFileDescriptor
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.customview.CircularImageView
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.videocall.openvcall.model.DuringCallEventHandler
import com.example.kotlinomnicure.videocall.openvcall.ui.layout.GridVideoViewContainer
import com.example.kotlinomnicure.videocall.openvcall.ui.layout.InChannelMessageListAdapter
import com.example.kotlinomnicure.videocall.openvcall.ui.layout.SmallVideoViewAdapter
import com.example.kotlinomnicure.videocall.openvcall.ui.layout.SmallVideoViewDecoration
import com.example.kotlinomnicure.videocall.propeller.UserStatusData
import com.example.kotlinomnicure.videocall.propeller.ui.RtlLinearLayoutManager
import com.example.kotlinomnicure.viewmodel.CallActivityViewModel
import com.google.gson.Gson
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.IRtcEngineEventHandler.AudioVolumeInfo
import io.agora.rtc.IRtcEngineEventHandler.RemoteVideoStats
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.*

class CallActivity: BaseActivity(), DuringCallEventHandler {

    val LAYOUT_TYPE_DEFAULT = 0

    //    public static final int LAYOUT_TYPE_DEFAULT = 1;
    val LAYOUT_TYPE_SMALL = 1
    private val log = LoggerFactory.getLogger(CallActivity::class.java)
    private val CALL_OPTIONS_REQUEST = 3222

    // should only be modified under UI thread
    private val mUidsList = HashMap<Int, SurfaceView?>() // uid = 0 || uid == EngineConfig.getmUid()

    private val mUIHandler = Handler()
    private var mLayoutType = LAYOUT_TYPE_SMALL //LAYOUT_TYPE_DEFAULT;

    var mBroadcastReceiver: BroadcastReceiver? = null
    var player: MediaPlayer? = null
    var connecting_profile_pic: CircularImageView? = null
    var connecting_image_text: TextView? = null
    var mHandlerCall: Handler? = null
    var mNoAnswerRunnable: Runnable? = null
    var mProviderList: ArrayList<Provider?>? = ArrayList()
    var sosCall = false
    private var mGridVideoViewContainer: GridVideoViewContainer? = null
    private var mSmallVideoViewDock: RelativeLayout? = null

    @Volatile
    private var mVideoMuted = false

    @Volatile
    private var mAudioMuted = false

    @Volatile
    private var mMixingAudio = false

    @Volatile
    private var mAudioRouting = Constants.AUDIO_ROUTE_DEFAULT

    @Volatile
    private var mFullScreen = false
    private var mIsLandscape = false
    private val mIsAudioCall = true
    private var mMsgAdapter: InChannelMessageListAdapter? = null
    private var mMsgList: ArrayList<Message>? = null
    private var mSmallVideoViewAdapter: SmallVideoViewAdapter? = null
    private var callViewModel: CallActivityViewModel? = null
    private var chronometer: Chronometer? = null
    private var isCallDurationStarted = false
    private val timer: Timer? = null
    private val seconds = 0
    private  var minutes = 0
    private  var hour= 0

//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_call, menu);
//        return true;
//    }

    //    @Override
    //    public boolean onCreateOptionsMenu(final Menu menu) {
    //        MenuInflater inflater = getMenuInflater();
    //        inflater.inflate(R.menu.menu_call, menu);
    //        return true;
    //    }
    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        // Handle presses on the action bar items
    //        switch (item.getItemId()) {
    //            case R.id.action_options:
    //                showCallOptions();
    //                return true;
    //            default:
    //                return super.onOptionsItemSelected(item);
    //        }
    //    }
    private var auditId: String? = ""
    private var outgoing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeActivityContentShownUnderStatusBar()
        setContentView(R.layout.activity_call)
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        callViewModel = ViewModelProviders.of(this)[CallActivityViewModel::class.java]
        registerCallerBusyBroadCast()
        showOrHideStatusBar(true)
        val ab = supportActionBar
        if (ab != null) {
            ab.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            ab.setCustomView(R.layout.ard_agora_actionbar_with_title)
        }
        val textView = findViewById(R.id.connection_message) as TextView
        connecting_profile_pic = findViewById(R.id.connecting_profile_pic) as CircularImageView
        connecting_image_text = findViewById(R.id.connecting_image_text) as TextView
        chronometer = findViewById(R.id.txt_call_duration) as Chronometer
        if (intent.hasExtra("sos")) {
            textView.setText(R.string.finding_a_doctor)
            sosCall = true
        } else {
            textView.setText(R.string.connecting)
        }
        var notAnswerWatchDog = false
        var receiverName = intent.getStringExtra("providerName")
        var providerHospitalName = intent.getStringExtra("providerHospitalName")
        var profilePicUrl = intent.getStringExtra("profilePicUrl")
        auditId =
            intent.getStringExtra(Constants.IntentKeyConstants.AUDIT_ID)
        auditId = if (auditId == null) "" else auditId
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
                    if (providerObj.has("role")) {
                        provider.setRole(providerObj.getString("role"))
                    }
                    mProviderList!!.add(provider)
                }
            } catch (e: Exception) {
//                Log.e(TAG, "Exception:", e.getCause());
            }
            //            System.out.println("providerlistval "+mProviderList);
            if (mProviderList != null) {
                if (mProviderList!!.size == 2 && !sosCall) {
                    notAnswerWatchDog = true
                    if (TextUtils.isEmpty(receiverName)) {
                        val selfId: Long = PrefUtility().getProviderId(this)
                        for (provider in mProviderList!!) {
                            if (selfId != provider!!.getId().longValue()) {
                                receiverName = provider.getName()
                                providerHospitalName = provider.getHospital()
                                profilePicUrl = provider.getProfilePicUrl()
                                break
                            }
                        }
                    }
                    val connectingProfileName =
                        findViewById(R.id.connecting_profile_name) as TextView
                    connectingProfileName.text = receiverName
                    val connectingProfileHospital =
                        findViewById(R.id.connecting_hospital_name) as TextView
                    connectingProfileHospital.text = providerHospitalName
                    connecting_profile_pic!!.setVisibility(View.GONE)
                    connecting_image_text!!.visibility = View.VISIBLE
                    connecting_image_text?.setText(UtilityMethods().getNameText(receiverName))
                    if (!TextUtils.isEmpty(profilePicUrl)) {
                        connecting_profile_pic.setVisibility(View.VISIBLE)
                        connecting_image_text!!.visibility = View.GONE
                        Glide.with(this)
                            .load(profilePicUrl)
                            .into(connecting_profile_pic)
                    }
                } else {
                    //group call
                    val connectingProfileName =
                        findViewById(R.id.connecting_profile_name) as TextView
                    if (sosCall) {
                        connectingProfileName.setText(R.string.sos_call)
                    } else {
                        connectingProfileName.setText(R.string.group_call)
                    }
                    val connectingProfileHospital =
                        findViewById(R.id.connecting_hospital_name) as TextView
                    connectingProfileHospital.text = ""
                    var drawable: Drawable? = null
                    drawable = if (sosCall) {
                        resources.getDrawable(R.drawable.ic_sos_big_red)
                    } else {
                        resources.getDrawable(R.drawable.ic_conference)
                    }
                    try {
                        connecting_profile_pic.setVisibility(View.VISIBLE)
                        connecting_image_text!!.visibility = View.GONE
                        connecting_profile_pic.setBorderWidth(5)
                        val bitmap: Bitmap
                        bitmap = Bitmap.createBitmap(
                            drawable.intrinsicWidth,
                            drawable.intrinsicWidth,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                        connecting_profile_pic.setImageBitmap(bitmap)
                        connecting_profile_pic.setFillColor(Color.WHITE)
                    } catch (e: OutOfMemoryError) {
                        // Handle the error
                        return
                    }
                }
            }
        } else {
            // handle directory call
            notAnswerWatchDog = true
            val connectingProfileName = findViewById(R.id.connecting_profile_name) as TextView
            connectingProfileName.text = receiverName
            val connectingProfileHospital = findViewById(R.id.connecting_hospital_name) as TextView
            connectingProfileHospital.text = providerHospitalName
            if (intent.hasExtra("profilePicUrl") && !TextUtils.isEmpty(intent.getStringExtra("profilePicUrl"))) {
                connecting_profile_pic.setVisibility(View.VISIBLE)
                connecting_image_text!!.visibility = View.GONE
                Glide.with(this)
                    .load(intent.getStringExtra("profilePicUrl"))
                    .into(connecting_profile_pic)
            } else {
                connecting_profile_pic.setVisibility(View.GONE)
                connecting_image_text!!.visibility = View.VISIBLE
                connecting_image_text.setText(UtilityMethods.getNameText(receiverName))
            }
        }
        if (intent.hasExtra("callType") && intent.getStringExtra("callType")
                .equalsIgnoreCase("outgoing")
        ) {
            outgoing = true
            var afd: AssetFileDescriptor? = null
            try {
                afd = assets.openFd("outgoing.mp3")
                if (player == null) {
                    player = MediaPlayer()
                }
                player!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                player!!.prepare()
                player!!.isLooping = true
                player!!.start()
            } catch (e: Exception) {
//                Log.e(TAG, "Exception:", e.getCause());
            } finally {
                if (afd != null) {
                    try {
                        afd.close()
                    } catch (e: IOException) {
//                        Log.e("error", e.getMessage());
                    }
                }
            }
            //            if(notAnswerWatchDog){
            mNoAnswerRunnable = Runnable {
                val connectingError = findViewById(R.id.connection_error) as TextView
                if (sosCall) {
                    connectingError.text = getString(R.string.did_not_find_doctor)
                } else {
                    connectingError.text = intent.getStringExtra("providerName")
                        .toString() + " " + getString(R.string.did_not_answer)
                }
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
                    player!!.setDataSource(afd!!.fileDescriptor, afd!!.startOffset, afd!!.length)
                    player!!.prepare()
                    player!!.isLooping = false
                    player!!.start()
                } catch (e: Exception) {
                    //                        Log.e(TAG, "Exception:", e.getCause());
                } finally {
                    if (afd != null) {
                        try {
                            afd!!.close()
                        } catch (e: IOException) {
                            //                                Log.e("error", e.getMessage());
                        }
                    }
                }
                mHandlerCall!!.postDelayed({ finish() }, 2000)
            }
            mHandlerCall = Handler()
            mHandlerCall!!.postDelayed(mNoAnswerRunnable, 60000)
            //            }
        }
    }

    override fun onDestroy() {
        if (player != null) {
            player!!.stop()
            player!!.stop()
            player!!.reset()
            player = null
        }
        if (mHandlerCall != null && mNoAnswerRunnable != null) {
            mHandlerCall!!.removeCallbacks(mNoAnswerRunnable!!)
        }
        unregisterCallerBusyBroadCast()
        super.onDestroy()
    }

    override fun initUIandEvent() {
        supportActionBar!!.hide()
        addEventHandler(this)
        val channelName = intent.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME)
        val ab = supportActionBar
        if (ab != null) {
            val channelNameView = findViewById(R.id.ovc_page_title) as TextView
            //            channelNameView.setText(channelName);
        }

        // programmatically layout ui below of status bar/action bar
        val eopsContainer: LinearLayout = findViewById(R.id.extra_ops_container)
        val eofmp = eopsContainer.layoutParams as MarginLayoutParams
        eofmp.topMargin =
            getStatusBarHeight() + getActionBarHeight() + resources.getDimensionPixelOffset(R.dimen.activity_vertical_margin) / 2 // status bar + action bar + divider
        val encryptionKey = intent.getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY)
        val encryptionMode = intent.getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE)
        doConfigEngine(encryptionKey, encryptionMode)
        mGridVideoViewContainer =
            findViewById(R.id.grid_video_view_container) as GridVideoViewContainer
        mGridVideoViewContainer!!.setItemEventHandler(object : OnItemClickListener() {
            fun onItemClick(view: View, position: Int) {
                onBigVideoViewClicked(view, position)
            }

            fun onItemLongClick(view: View?, position: Int) {}
            fun onItemDoubleClick(view: View?, position: Int) {
//                onBigVideoViewDoubleClicked(view, position);
            }
        })
        mVideoMuted = if (intent.hasExtra("call") && intent.getStringExtra("call")
                .equalsIgnoreCase(com.mvp.omnicure.utils.Constants.FCMMessageType.AUDIO_CALL)
        ) {
            true
        } else {
            false
        }
        val videoMuteView = findViewById(R.id.btn_camera) as ImageView
        rtcEngine()!!.enableLocalVideo(!mVideoMuted) //rtcEngine.enableVideo();
        //handelVideoIcon(videoMuteView,false);
        videoMuteView.setImageResource(if (mVideoMuted) R.drawable.ic_video_mute else R.drawable.ic_video)
        videoMuteView.background =
            if (mVideoMuted) resources.getDrawable(R.drawable.ic_call_icon_bg) else resources.getDrawable(
                R.drawable.transparent_bg
            )
        val surfaceV = RtcEngine.CreateRendererView(applicationContext)
        preview(true, surfaceV, 0)
        surfaceV.setZOrderOnTop(false)
        surfaceV.setZOrderMediaOverlay(false)
        val selfId: Long = PrefUtility.getProviderId(this@CallActivity)
        //        System.out.println("selfid "+selfId);
        if (getProviderFromList(selfId.toInt()) == null) {
            val tmpProvider = Provider()
            tmpProvider.setId(selfId)
            val name: String = PrefUtility.getStringInPref(
                this,
                com.mvp.omnicure.utils.Constants.SharedPrefConstants.NAME,
                ""
            )
            tmpProvider.setName(name)
            val profilePic: String = PrefUtility.getStringInPref(
                this,
                com.mvp.omnicure.utils.Constants.SharedPrefConstants.PROFILE_IMG_URL,
                ""
            )
            tmpProvider.setProfilePicUrl(profilePic)
            mProviderList!!.add(tmpProvider)
        }
        mUidsList[selfId.toInt()] = surfaceV // get first surface view

//        if(getIntent().hasExtra("call") && getIntent().getStringExtra("call").equalsIgnoreCase(com.mvp.omnicure.utils.Constants.FCMMessageType.AUDIO_CALL)) {
//
//        }else{
        mGridVideoViewContainer!!.initViewContainer(
            this,
            selfId.toInt(),
            if (mVideoMuted) 1 else 0,
            if (mAudioMuted) 1 else 0,
            mUidsList,
            mIsLandscape,
            mProviderList
        ) // first is now full view
        //        }
        initMessageList()
        notifyMessageChanged(
            Message(
                User(selfId.toInt(), null),
                "start join " + channelName + " as " + (config().getmUid() and 0xFFFFFFFFL)
            )
        )
        joinChannel(channelName, PrefUtility.getProviderId(this).intValue())
        //        hideLocalView(mVideoMuted);
        rtcEngine()!!.addHandler(object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                super.onUserJoined(uid, elapsed)
            }
        })
        optional()
    }


    override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        runOnUiThread { updateAudioMuteView(uid, state == 0) }
    }

    override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        if (state == 0 && reason == 5) {
            //remote video muted
            runOnUiThread { doHideTargetView(uid, true) }
        } else if (state == 2 && reason == 6) {
            runOnUiThread { doHideTargetView(uid, false) }
        }
    }

    private fun onBigVideoViewClicked(view: View, position: Int) {
//        log.debug("onItemClick " + view + " " + position + " " + mLayoutType);
        toggleFullscreen()
    }

    private fun onBigVideoViewDoubleClicked(view: View, position: Int) {
//        log.debug("onItemDoubleClick " + view + " " + position + " " + mLayoutType);
        if (mUidsList.size < 2) {
            return
        }
        val user = mGridVideoViewContainer!!.getItem(position)
        val uid = if (user!!.getmUid() === 0) config().getmUid() else user!!.getmUid()
        //        Log.d("TAG", "user.getmUid()= " + user.getmUid() + "; config().getmUid()=" + config().getmUid());
        if (mLayoutType == LAYOUT_TYPE_DEFAULT && mUidsList.size != 1) {
            switchToSmallVideoView(uid, -1)
        } else {
            switchToDefaultVideoView()
        }
    }

    private fun onSmallVideoViewDoubleClicked(view: View, position: Int) {
//        log.debug("onItemDoubleClick small " + view + " " + position + " " + mLayoutType);
        switchToDefaultVideoView()
    }

    private fun makeActivityContentShownUnderStatusBar() {
        // https://developer.android.com/training/system-ui/status
        // May fail on some kinds of devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val decorView = window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = resources.getColor(R.color.agora_blue)
            }
        }
    }

    private fun showOrHideStatusBar(hide: Boolean) {
        // May fail on some kinds of devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val decorView = window.decorView
            val uiOptions = decorView.systemUiVisibility

//            if (hide) {
//                uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//            } else {
//                uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
//            }
            decorView.systemUiVisibility = uiOptions
        }
    }

    private fun toggleFullscreen() {
//        mFullScreen = !mFullScreen;
//        Log.d(TAG, "toggleFullscreen: actual value "+mFullScreen);
        togglemFullScreen()
        //        Log.d(TAG, "toggleFullscreen: after toggle value "+mFullScreen);
        showOrHideCtrlViews(mFullScreen)
        mUIHandler.postDelayed({ showOrHideStatusBar(true) }, 200) // action bar fade duration
    }

    @Synchronized
    fun togglemFullScreen() {
        mFullScreen = !mFullScreen
    }

    private fun showOrHideCtrlViews(hide: Boolean) {
//        ActionBar ab = getSupportActionBar();
//        if (ab != null) {
//            if (hide) {
//                ab.hide();
//            } else {
//                ab.show();
//            }
//        }
        findViewById(R.id.extra_ops_container).setVisibility(if (hide) View.INVISIBLE else View.VISIBLE)
        findViewById(R.id.bottom_action_container).setVisibility(if (hide) View.INVISIBLE else View.VISIBLE)
        //        findViewById(R.id.msg_list).setVisibility(hide ? View.INVISIBLE : (Constant.DEBUG_INFO_ENABLED ? View.VISIBLE : View.INVISIBLE));
        findViewById(R.id.msg_list).setVisibility(if (hide) View.INVISIBLE else if (Constant.isDebugInfoEnabled()) View.VISIBLE else View.INVISIBLE)
    }

    private fun relayoutForVirtualKeyPad(orientation: Int) {
        val virtualKeyHeight = virtualKeyHeight()
        val eopsContainer: LinearLayout = findViewById(R.id.extra_ops_container)
        val eofmp = eopsContainer.layoutParams as MarginLayoutParams
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            eofmp.rightMargin = virtualKeyHeight
            eofmp.leftMargin = 0
        } else {
            eofmp.leftMargin = 0
            eofmp.rightMargin = 0
        }
        val bottomContainer: LinearLayout = findViewById(R.id.bottom_container)
        val fmp = bottomContainer.layoutParams as MarginLayoutParams
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fmp.bottomMargin = 0
            fmp.rightMargin = virtualKeyHeight
            fmp.leftMargin = 0
        } else {
            fmp.bottomMargin = virtualKeyHeight
            fmp.leftMargin = 0
            fmp.rightMargin = 0
        }
    }

    @Synchronized
    fun showCallOptions() {
        val i = Intent(this, CallOptionsActivity::class.java)
        startActivityForResult(i, CALL_OPTIONS_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CALL_OPTIONS_REQUEST) {
            val msgListView = findViewById(R.id.msg_list) as RecyclerView
            msgListView.visibility =
                if (Constant.isDebugInfoEnabled()) View.VISIBLE else View.INVISIBLE
        }
    }

    fun onClickHideIME(view: View?) {
//        log.debug("onClickHideIME " + view);
        closeIME(findViewById(R.id.msg_content))
        findViewById(R.id.msg_input_container).setVisibility(View.GONE)
        findViewById(R.id.bottom_action_container).setVisibility(View.VISIBLE)
    }

    private fun initMessageList() {
        mMsgList = ArrayList()
        val msgListView = findViewById(R.id.msg_list) as RecyclerView
        //msgListView.setVisibility(View.GONE);
        mMsgAdapter = InChannelMessageListAdapter(this, mMsgList)
        mMsgAdapter!!.setHasStableIds(true)
        //msgListView.setAdapter(mMsgAdapter);
        //msgListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        //msgListView.addItemDecoration(new MessageListDecoration());
    }

    private fun notifyMessageChanged(msg: Message) {
        mMsgList!!.add(msg)
        //        System.out.println("messageValue "+msg.toString());
//        System.out.println("messageList "+mMsgList.size());
        val MAX_MESSAGE_COUNT = 16
        if (mMsgList!!.size > MAX_MESSAGE_COUNT) {
            val toRemove = mMsgList!!.size - MAX_MESSAGE_COUNT
            for (i in 0 until toRemove) {
                mMsgList!!.removeAt(i)
            }
        }
        mMsgAdapter!!.notifyDataSetChanged()
    }

    private fun optional() {
        volumeControlStream = AudioManager.STREAM_VOICE_CALL
    }

    private fun optionalDestroy() {}

    private fun getVideoEncResolutionIndex(): Int {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var videoEncResolutionIndex = pref.getInt(
            ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION,
            ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX
        )
        //        if (videoEncResolutionIndex > ConstantApp.VIDEO_DIMENSIONS.length - 1) {
        if (videoEncResolutionIndex > ConstantApp.getVideoDimensions().length - 1) {
            videoEncResolutionIndex = ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX

            // save the new value
            val editor = pref.edit()
            editor.putInt(
                ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION,
                videoEncResolutionIndex
            )
            editor.apply()
        }
        return videoEncResolutionIndex
    }

    private fun getVideoEncFpsIndex(): Int {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var videoEncFpsIndex = pref.getInt(
            ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS,
            ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX
        )
        //        if (videoEncFpsIndex > ConstantApp.VIDEO_FPS.length - 1) {
        if (videoEncFpsIndex > ConstantApp.getVideoFps().length - 1) {
            videoEncFpsIndex = ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX

            // save the new value
            val editor = pref.edit()
            editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, videoEncFpsIndex)
            editor.apply()
        }
        return videoEncFpsIndex
    }

    private fun doConfigEngine(encryptionKey: String?, encryptionMode: String?) {
        /*VideoEncoderConfiguration.VideoDimensions videoDimension = ConstantApp.VIDEO_DIMENSIONS[getVideoEncResolutionIndex()];
        VideoEncoderConfiguration.FRAME_RATE videoFps = ConstantApp.VIDEO_FPS[getVideoEncFpsIndex()];*/
        val videoDimension: VideoDimensions =
            ConstantApp.getVideoDimensions().get(getVideoEncResolutionIndex())
        val videoFps: FRAME_RATE = ConstantApp.getVideoFps().get(getVideoEncFpsIndex())
        configEngine(videoDimension, videoFps, encryptionKey, encryptionMode)
    }

    fun onSwitchCameraClicked(view: View?) {
        val rtcEngine = rtcEngine()
        rtcEngine!!.switchCamera()
        val iv = findViewById(R.id.switch_camera_id) as ImageView
        if (iv.tag == null || (iv.tag as String).equals("front", ignoreCase = true)) {
            iv.background = resources.getDrawable(R.drawable.ic_call_icon_bg)
            iv.tag = "back"
        } else {
            iv.background = resources.getDrawable(R.drawable.transparent_bg)
            iv.tag = "front"
        }
    }

    fun onSwitchSpeakerClicked(view: View?) {
        if (mAudioRouting == Constants.AUDIO_ROUTE_DEFAULT) {
            mAudioRouting = Constants.AUDIO_ROUTE_SPEAKERPHONE
        }
        val rtcEngine = rtcEngine()
        rtcEngine!!.setEnableSpeakerphone(mAudioRouting != Constants.AUDIO_ROUTE_SPEAKERPHONE)
    }

    fun onFilterClicked(view: View) {
//        Constant.BEAUTY_EFFECT_ENABLED = !Constant.BEAUTY_EFFECT_ENABLED;
        Constant.setBeautyEffectEnabled(!Constant.isBeautyEffectEnabled())
        if (Constant.isBeautyEffectEnabled()) {
            setBeautyEffectParameters(
                Constant.BEAUTY_EFFECT_DEFAULT_LIGHTNESS,
                Constant.BEAUTY_EFFECT_DEFAULT_SMOOTHNESS,
                Constant.BEAUTY_EFFECT_DEFAULT_REDNESS
            )
            enablePreProcessor()
        } else {
            disablePreProcessor()
        }
        val iv = view as ImageView

//        iv.setImageResource(Constant.BEAUTY_EFFECT_ENABLED ? R.drawable.btn_filter : R.drawable.btn_filter_off);
        iv.setImageResource(if (Constant.isBeautyEffectEnabled()) R.drawable.btn_filter else R.drawable.btn_filter_off)
    }

    override fun deInitUIandEvent() {
        optionalDestroy()
        doLeaveChannel()
        removeEventHandler(this)
        mUidsList.clear()
    }

    private fun doLeaveChannel() {
        leaveChannel(config().getmChannel())
        preview(false, null, 0)
    }

    fun onHangupClicked(view: View?) {
//        log.info("onHangupClicked " + view + " " + mUidsList.size());
        chronometer!!.stop()
        if (intent.hasExtra("sos")) {
            sosDismiss(false)
        } else {
            sendCallResponseMessage(com.mvp.omnicure.utils.Constants.FCMMessageType.CALLER_REJECT)
        }
    }

    private fun sendCallResponseMessage(messageType: String) {
        showProgressBar(getString(R.string.disconnecting))

        /*String token = PrefUtility.getStringInPref(context, com.mvp.omnicure.utils.Constants.SharedPrefConstants.TOKEN, "");

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
        val errMsg = arrayOfNulls<String>(1)
        val token: String = PrefUtility.getStringInPref(
            this@CallActivity,
            com.mvp.omnicure.utils.Constants.SharedPrefConstants.TOKEN,
            ""
        )
        val requestBody = SendMsgNotifyRequestBody()
        requestBody.setProviderId(PrefUtility.getProviderId(this@CallActivity)) //id key
        requestBody.setToken(token)
        requestBody.setReceiverId(intent.getLongExtra("providerId", 0L))
        requestBody.setMessage(intent.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME))
        requestBody.setType(messageType)
        if (intent.hasExtra("patientId")) {
            requestBody.setPatientId(intent.getLongExtra("patientId", 0L))
        }
        if (auditId != null) {
            requestBody.setAuditId(auditId)
        }
        ApiClient.getApiProviderEndpoints(true, true).sendMessageNotification(requestBody)
            .enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
//                Log.e(TAG, "onResponse: sendMessageNotification "+ response.toString());
//                System.out.println("callrespon_se "+response.body());
                    if (response.body() != null) {
//                    Log.e("response", "response for busy notification : "+ response.body().getStatus());
                    }
                    //                Log.i("response", "response for busy notification : " + response.body().getStatus());
                    Handler(Looper.getMainLooper()).post { sendAuditId(false, messageType) }
                }

                override fun onFailure(
                    call: Call<omnicure.mvp.com.providerEndpoints.model.CommonResponse?>,
                    t: Throwable
                ) {
                    if (t is SocketTimeoutException) errMsg[0] =
                        com.mvp.omnicure.utils.Constants.APIErrorType.SocketTimeoutException.toString() else if (t is Exception) errMsg[0] =
                        com.mvp.omnicure.utils.Constants.API_ERROR.toString()
                    finish()
                }
            })

        /*new Thread(new Runnable() {
            String errMsg = "";

            @Override
            public void run() {
                try {
                    String token = PrefUtility.getStringInPref(CallActivity.this, com.mvp.omnicure.utils.Constants.SharedPrefConstants.TOKEN, "");
                    final ProviderEndpoints.SendMessageNotification apiCall = EndPointBuilder.getProviderEndpoints()
                            .sendMessageNotification(PrefUtility.getProviderId(CallActivity.this),
                                    token,
                                    getIntent().getLongExtra("providerId", 0l),
                                    getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME),
                                    messageType,
                                    auditId);

                    if (getIntent().hasExtra("patientId")) {
                        apiCall.setPatientsId(getIntent().getLongExtra("patientId", 0l));
                    }
                    Log.i("CallActivity", "values for api " + PrefUtility.getProviderId(CallActivity.this) + " " + getIntent().getLongExtra("providerId", 0l));
                    CommonResponse commonResponse = apiCall.execute();

                    finish();
                    System.out.println("callrespon_se "+commonResponse);

                    Log.i("response", "response for busy notification : " + commonResponse.getStatus());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            sendAuditId(false, messageType);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    errMsg = com.mvp.omnicure.utils.Constants.APIErrorType.SocketTimeoutException.toString();
                } catch (Exception e) {
//                    errMsg = com.mvp.omnicure.utils.Constants.APIErrorType.Exception.toString();
                    Log.e(TAG, "Exception:", e.getCause());
                    errMsg = com.mvp.omnicure.utils.Constants.API_ERROR.toString();

                }

                finish();
            }
        }).start();*/
    }

    /*
         result = {RtcEngineImpl@14564}
         lastOrientationTs = 0
         mChannelProfile = 1
         mClientRole = 2
         mContext = {WeakReference@14579}
         mDefaultRtcChannel = {RtcChannelImpl@14580}
         mExAudioChannels = 0
         mExAudioSampleRate = 0
         mLocalVideoEnabled = false
         mNativeHandle = 482113016960
         mRtcChannels = {LinkedList@14581}  size = 0
         mRtcHandlers = {ConcurrentHashMap@14584}  size = 1
         mRtcStats = {IRtcEngineEventHandler$RtcStats@14587}
         mTotalRotation = 1000
         mUseLocalView = true
         mVideoSourceType = 1
         mWifiLock = null
         shadow$_klass_ = {Class@12399} "class io.agora.rtc.internal.RtcEngineImpl"
         shadow$_monitor_ = 0
         */

    /*
         result = {RtcEngineImpl@14564}
         lastOrientationTs = 0
         mChannelProfile = 1
         mClientRole = 2
         mContext = {WeakReference@14579}
         mDefaultRtcChannel = {RtcChannelImpl@14580}
         mExAudioChannels = 0
         mExAudioSampleRate = 0
         mLocalVideoEnabled = false
         mNativeHandle = 482113016960
         mRtcChannels = {LinkedList@14581}  size = 0
         mRtcHandlers = {ConcurrentHashMap@14584}  size = 1
         mRtcStats = {IRtcEngineEventHandler$RtcStats@14587}
         mTotalRotation = 1000
         mUseLocalView = true
         mVideoSourceType = 1
         mWifiLock = null
         shadow$_klass_ = {Class@12399} "class io.agora.rtc.internal.RtcEngineImpl"
         shadow$_monitor_ = 0
         */
    @Synchronized
    fun togglemVideoMuted() {
        mVideoMuted = !mVideoMuted
    }

    fun onVideoMuteClicked(view: View) {
        /*if(mUidsList.size() == 1){
            return;
        }*/
//        log.info("onVoiceChatClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted);
        if (mUidsList.size == 0) {
            return
        }
        val surfaceV = getLocalView()
        var parent: ViewParent?
        if (surfaceV == null || surfaceV.parent.also { parent = it } == null) {
//            log.warn("onVoiceChatClicked " + view + " " + surfaceV);
            return
        }
        val rtcEngine = rtcEngine()
        togglemVideoMuted()
        //        mVideoMuted = !mVideoMuted;

//        rtcEngine().stopPreview();
//        rtcEngine().muteLocalVideoStream(mVideoMuted);
//        preview(false,null,config().getmUid());

//
//        if (mVideoMuted) {
//            rtcEngine.disableVideo();
//        } else {
//            rtcEngine.enableVideo();
//        }
        if (mVideoMuted) {
            rtcEngine!!.enableLocalVideo(false)
        } else {
            rtcEngine!!.enableLocalVideo(true) //rtcEngine.enableVideo();
        }
        val iv = view as ImageView

        //handelVideoIcon(iv,true);
        iv.setImageResource(if (mVideoMuted) R.drawable.ic_video_mute else R.drawable.ic_video)
        iv.background =
            if (mVideoMuted) resources.getDrawable(R.drawable.ic_call_icon_bg) else resources.getDrawable(
                R.drawable.transparent_bg
            )
        hideLocalView(mVideoMuted)
    }

    private fun getLocalView(): SurfaceView? {
        for ((key, value) in mUidsList) {
            if (key == 0 || key === config().getmUid()) {
                return value
            }
        }
        return null
    }

    private fun hideLocalView(hide: Boolean) {
//        Log.d("TAG", "hideLocalView hide: " + hide);
        val uid = config().getmUid()
        doHideTargetView(uid, hide)
    }

    private fun updateAudioMuteView(targetUid: Int, audioMute: Boolean) {
//        Log.d("TAG", "updateAUdioMuteView targetUid:" + targetUid + " audioMute: " + audioMute);
        if (mSmallVideoViewAdapter == null) {
            return
        }
        val userList = mSmallVideoViewAdapter!!.getUserList()
        val status = HashMap<Int?, Int?>()
        val audioStatus = HashMap<Int?, Int?>()
        if (userList != null) {
            for (userStatusData in userList) {
                if (userStatusData.getmUid() === targetUid) {
                    audioStatus[targetUid] =
                        if (audioMute) UserStatusData.AUDIO_MUTED else UserStatusData.DEFAULT_STATUS
                } else {
                    audioStatus[userStatusData.getmUid()] = userStatusData.getmAudioStatus()
                }
                status[userStatusData.getmUid()] = userStatusData.getmStatus()
            }
        }
        val bigBgUser = mGridVideoViewContainer!!.getItem(0)
        val slice = HashMap<Int?, SurfaceView?>(1)
        slice[bigBgUser!!.getmUid()] = mUidsList[bigBgUser.getmUid()]
        val iterator: Iterator<SurfaceView?> = mUidsList.values.iterator()
        while (iterator.hasNext()) {
            val s = iterator.next()
            if (s != null) {
                s.setZOrderOnTop(true)
                s.setZOrderMediaOverlay(true)
            }
        }
        if (slice[bigBgUser.getmUid()] != null) {
            slice[bigBgUser.getmUid()]!!.setZOrderOnTop(false)
            slice[bigBgUser.getmUid()]!!.setZOrderMediaOverlay(false)
        }
        mGridVideoViewContainer!!.notifyUiChanged(
            slice,
            bigBgUser.getmUid(),
            status,
            audioStatus,
            null
        )
        mSmallVideoViewAdapter!!.setLocalUid(config().getmUid())
        run { // find target view in small video view list
//            log.warn("SmallVideoViewAdapter call notifyUiChanged " + mUidsList + " " + (bigBgUser.getmUid() & 0xFFFFFFFFL) + " target: " + (targetUid & 0xFFFFFFFFL) + "==" + targetUid + " " + status + " " + mUidsList.size());
            if (mSmallVideoViewAdapter != null) mSmallVideoViewAdapter!!.notifyUiChanged(
                mUidsList,
                bigBgUser.getmUid(),
                status,
                audioStatus,
                null
            )
        }
//        }
    }


    private fun doHideTargetView(targetUid: Int, hide: Boolean) {
        try {
            Log.d("TAG", "doHideTargetView targetUid:$targetUid hide: $hide")
            var userList: List<UserStatusData>? = null
            if (mSmallVideoViewAdapter != null) {
                userList = mSmallVideoViewAdapter!!.getUserList()
            }
            val status = HashMap<Int?, Int?>()
            val audioStatus = HashMap<Int?, Int?>()
            if (userList != null) {
                for (userStatusData in userList) {
                    if (userStatusData.getmUid() === targetUid) {
                        status[targetUid] =
                            if (hide) UserStatusData.VIDEO_MUTED else UserStatusData.DEFAULT_STATUS
                    } else {
                        status[userStatusData.getmUid()] = userStatusData.getmStatus()
                    }
                    audioStatus[userStatusData.getmUid()] = userStatusData.getmAudioStatus()
                }
            }
            //        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
//            mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
//        } else if (mLayoutType == LAYOUT_TYPE_SMALL) {
            val bigBgUser = mGridVideoViewContainer!!.getItem(0)
            if (bigBgUser!!.getmUid() === targetUid) { // big background is target view
                val slice = HashMap<Int?, SurfaceView?>(1)
                slice[targetUid] = mUidsList[targetUid]
                mGridVideoViewContainer!!.notifyUiChanged(
                    slice,
                    targetUid,
                    status,
                    audioStatus,
                    null
                )
                //                mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
            } else {
                val slice = HashMap<Int?, SurfaceView?>(1)
                slice[bigBgUser!!.getmUid()] = mUidsList[bigBgUser.getmUid()]
                mGridVideoViewContainer!!.notifyUiChanged(
                    slice,
                    bigBgUser.getmUid(),
                    status,
                    audioStatus,
                    null
                )
            }
            run { // find target view in small video view list
//                log.warn("SmallVideoViewAdapter call notifyUiChanged " + mUidsList + " " + (bigBgUser.getmUid() & 0xFFFFFFFFL) + " target: " + (targetUid & 0xFFFFFFFFL) + "==" + targetUid + " " + status);
                if (mSmallVideoViewAdapter != null) mSmallVideoViewAdapter!!.notifyUiChanged(
                    mUidsList,
                    bigBgUser!!.getmUid(),
                    status,
                    audioStatus,
                    null
                )
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
//        }
    }

    fun onVoiceMuteClicked(view: View) {
//        log.info("onVoiceMuteClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted);
        if (mUidsList.size == 0) {
            return
        }
        val rtcEngine = rtcEngine()
        //        rtcEngine.muteLocalAudioStream(mAudioMuted = !mAudioMuted);
        toggleAudioMuted()
        rtcEngine!!.muteLocalAudioStream(mAudioMuted)
        val iv = view as ImageView
        iv.setImageResource(if (mAudioMuted) R.drawable.ic_mic_off else R.drawable.ic_mic)
        iv.background =
            if (mAudioMuted) resources.getDrawable(R.drawable.ic_call_icon_bg) else resources.getDrawable(
                R.drawable.transparent_bg
            )
        updateAudioMuteView(config().getmUid(), mAudioMuted)
    }

    @Synchronized
    fun toggleAudioMuted() {
        mAudioMuted = !mAudioMuted
    }

    fun onMixingAudioClicked(view: View) {
//        log.info("onMixingAudioClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted + " mixing_audio: " + mMixingAudio);
        if (mUidsList.size == 0) {
            return
        }

//        mMixingAudio = !mMixingAudio;
        toggleMixingAudio()
        val rtcEngine = rtcEngine()
        if (mMixingAudio) {
            rtcEngine!!.startAudioMixing(Constant.MIX_FILE_PATH, false, false, -1)
        } else {
            rtcEngine!!.stopAudioMixing()
        }
        val iv = view as ImageView
        iv.setImageResource(if (mMixingAudio) R.drawable.btn_audio_mixing else R.drawable.btn_audio_mixing_off)
    }

    @Synchronized
    fun toggleMixingAudio() {
        mMixingAudio = !mMixingAudio
    }

    override fun onUserJoined(uid: Int) {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handelVideoIcon(((ImageView)findViewById(R.id.btn_camera)),true);
            }
        });*/
        Log.d("TAG", "onUser-Joined " + mUidsList.size + " " + config().getmChannel() + " " + uid)
        if (!isCallDurationStarted) {
            chronometer!!.base = SystemClock.elapsedRealtime()
            chronometer!!.start()
            chronometer!!.format = "%s"
            isCallDurationStarted = true
        }
        if (player != null) {
            player!!.stop()
            player!!.stop()
            player!!.reset()
            player = null
        }
        if (mHandlerCall != null && mNoAnswerRunnable != null) {
            mHandlerCall!!.removeCallbacks(mNoAnswerRunnable!!)
        }
        runOnUiThread {
            findViewById(R.id.connecting_ui).setVisibility(View.INVISIBLE)
            notifyMessageChanged(
                Message(
                    User(0, null),
                    "user " + (uid and 0xFFFFFFFFL) + " joined"
                )
            )
        }

//        if(mVideoMuted) {
        doRenderRemoteUi(uid)
        //            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    switchToSmallVideoView(uid);
//                }
//            },3000);

//        }
        if (intent.hasExtra("sos") && intent.hasExtra("callType") && intent.getStringExtra("callType")
                .equalsIgnoreCase("outgoing")
        ) {
            sosDismiss(true)
        }
    }

    private fun sosDismiss(isAttend: Boolean) {
        val providerID: Long = PrefUtility.getProviderId(this@CallActivity)
        val token: String = PrefUtility.getStringInPref(
            this@CallActivity,
            com.mvp.omnicure.utils.Constants.SharedPrefConstants.TOKEN,
            ""
        )
        //        System.out.println("channel name " + config().getmChannel()+" "+mUidsList);
        var receiverIds: String? = ""
        if (!isAttend) {
            val receiverArr = ArrayList<String?>()
            for ((key) in mUidsList) {
//                System.out.println("userjoined values " + entry.getKey());
                receiverArr.add(key.toString())
            }
            if (receiverArr.size >= 2) {
                receiverIds = TextUtils.join(",", receiverArr)
            }
        }
        //        System.out.println("receiverids "+receiverIds);
        callViewModel!!.sendSOSDismiss(
            providerID, token,
            config().getmChannel()!!, com.mvp.omnicure.utils.Constants.FCMMessageType.SOS_DISMISS,
            receiverIds!!, auditId!!
        )!!.observe(this@CallActivity, { commonResponse ->
            dismissProgressBar()
            //            Log.i(TAG, "sosDismiss: response "+commonResponse);
            if (commonResponse != null && commonResponse.getStatus()) {
                Handler(Looper.getMainLooper()).post {
                    sendAuditId(
                        false,
                        com.mvp.omnicure.utils.Constants.FCMMessageType.SOS_DISMISS
                    )
                }

                //Do nothing
            } else {
                finish()
                //                log.info("sos dismiss message: " + commonResponse.getErrorMessage());
            }
        })
    }

    private fun sendAuditId(callAttend: Boolean, type: String) {
        if (outgoing && callAttend) {
            return
        }
        val providerID: String =
            java.lang.String.valueOf(PrefUtility.getProviderId(this@CallActivity))
        //        System.out.println("coming here "+callAttend);
        callViewModel!!.sendAuditId(auditId!!, callAttend, providerID, type)!!
            .observe(this@CallActivity, { commonResponse ->
//            System.out.println("callauditResponse "+auditId+" "+commonResponse);
                dismissProgressBar()
                //            if (commonResponse != null && commonResponse.getStatus()) {
//                //Do nothing
//            } else {
////                log.info("sos dismiss message: " + commonResponse.getErrorMessage());
//            }
                finish()
            })
    }

    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
//        Log.d("TAG", "onFirstRemoteVideoDecoded");
//        log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);
        findViewById(R.id.connecting_ui).setVisibility(View.INVISIBLE)
        if (player != null) {
            player!!.stop()
            player!!.stop()
            player!!.reset()
            player = null
        }
        if (mHandlerCall != null && mNoAnswerRunnable != null) {
            mHandlerCall!!.removeCallbacks(mNoAnswerRunnable!!)
        }
        if (mUidsList.containsKey(uid)) {
            runOnUiThread { doHideTargetView(uid, false) }
        } else {
            doRenderRemoteUi(uid)
        }
    }

    private fun getProviderFromList(uid: Int): Provider? {
        for (provider in mProviderList!!) {
            if (provider != null && provider.getId().intValue() === uid) {
                return provider
            }
        }
        return null
    }

    private fun doRenderRemoteUi(uid: Int) {
//        Log.d("TAG", "doRenderRemoteUi_: " + uid);
        runOnUiThread(Runnable {
            //                System.out.println("isfinishing "+isFinishing());
            if (isFinishing) {
                return@Runnable
            }
            if (getProviderFromList(uid) == null) {
                Log.d(
                    "TAG",
                    "provider not found so first get provider from server $uid"
                )
                val id: Long = PrefUtility.getProviderId(this@CallActivity)
                val token: String = PrefUtility.getToken(this@CallActivity)
                callViewModel!!.getProviderById(id, token, uid.toLong())!!
                    .observe(this@CallActivity, { commonResponse ->
                        val gson = Gson()
                        Log.d("TAG", "provider_response " + gson.toJson(commonResponse))
                        if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus() && commonResponse.getProvider() != null) {
                            mProviderList!!.add(commonResponse.getProvider())
                            doRenderRemoteUi(commonResponse.getProvider()!!.getId().intValue())
                        } else {
                            val errMsg: String = ErrorMessages.getErrorMessage(
                                this@CallActivity,
                                commonResponse!!.getErrorMessage(),
                                com.mvp.omnicure.utils.Constants.API.getProviderById
                            )
                            //                            UtilityMethods.showErrorSnackBar(mGridVideoViewContainer, errMsg, Snackbar.LENGTH_LONG);
                            CustomSnackBar.make(
                                mGridVideoViewContainer,
                                this@CallActivity,
                                CustomSnackBar.WARNING,
                                errMsg,
                                CustomSnackBar.TOP,
                                3000,
                                0
                            ).show()
                        }
                    })
            } else {
                if (mUidsList.containsKey(uid)) {
                    return@Runnable
                }

//                    System.out.println("addid "+uid);
//                    System.out.println("providerlist_call "+mProviderList);
                val surfaceV = RtcEngine.CreateRendererView(applicationContext)
                mUidsList[uid] = surfaceV
                var count = 0
                for (uid in mUidsList.keys) {
                    count++
                    //                        System.out.println("mUidsList_call "+uid);
                    if (count > 0) break
                }

//                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;
                surfaceV.setZOrderOnTop(true)
                surfaceV.setZOrderMediaOverlay(true)
                rtcEngine()!!.setupRemoteVideo(
                    VideoCanvas(
                        surfaceV,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        uid
                    )
                )

//                if (useDefaultLayout) {
//                    log.debug("doRenderRemoteUi LAYOUT_TYPE_DEFAULT " + (uid & 0xFFFFFFFFL));
//                    switchToDefaultVideoView();
//                } else {
                val bigBgUid =
                    if (mSmallVideoViewAdapter == null) uid else mSmallVideoViewAdapter!!.getExceptedUid()
                //                    log.debug("doRenderRemoteUi LAYOUT_TYPE_SMALL " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
                switchToSmallVideoView(bigBgUid, uid)
                //                }
                notifyMessageChanged(
                    Message(
                        User(0, null),
                        "video from user " + (uid and 0xFFFFFFFFL) + " decoded"
                    )
                )
            }
        })
    }


    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
//        log.debug("onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
    }

    override fun onUserOffline(uid: Int, reason: Int) {
//        log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        doRemoveRemoteUi(uid)
    }

    override fun onExtraCallback(type: Int, vararg data: Any?) {
        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            doHandleExtraCallback(type, *data)
        })
    }

    private fun doHandleExtraCallback(type: Int, vararg data: Any) {
        var peerUid: Int
        val muted: Boolean
        when (type) {
            AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_MUTED -> {
                peerUid = data[0] as Int
                muted = data[1] as Boolean
                Log.d(
                    "TAG",
                    "onExtraCallback EVENT_TYPE_ON_USER_VIDEO_MUTED: $muted UID: $peerUid"
                )
                doHideTargetView(peerUid, muted)
            }
            AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_STATS -> {
                val stats = data[0] as RemoteVideoStats
                if (Constant.SHOW_VIDEO_INFO) {
                    if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                        mGridVideoViewContainer!!.addVideoInfo(
                            stats.uid,
                            VideoInfoData(
                                stats.width,
                                stats.height,
                                stats.delay,
                                stats.rendererOutputFrameRate,
                                stats.receivedBitrate
                            )
                        )
                        val uid = config().getmUid()
                        val profileIndex = getVideoEncResolutionIndex()
                        val resolution =
                            resources.getStringArray(R.array.string_array_resolutions)[profileIndex]
                        val fps =
                            resources.getStringArray(R.array.string_array_frame_rate)[profileIndex]
                        val rwh = resolution.split("x").toTypedArray()
                        val width = Integer.valueOf(rwh[0])
                        val height = Integer.valueOf(rwh[1])
                        mGridVideoViewContainer!!.addVideoInfo(
                            uid, VideoInfoData(
                                if (width > height) width else height,
                                if (width > height) height else width,
                                0, Integer.valueOf(fps), Integer.valueOf(0)
                            )
                        )
                    }
                } else {
                    mGridVideoViewContainer!!.cleanVideoInfo()
                }
            }
            AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS -> {
                val infos = data[0] as Array<AudioVolumeInfo>
                if (infos.size == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break
                }
                if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                    val volume = HashMap<Int?, Int?>()
                    for (each in infos) {
                        peerUid = each.uid
                        val peerVolume = each.volume
                        if (peerUid == 0) {
                            continue
                        }
                        volume[peerUid] = peerVolume
                    }
                    val userList = mSmallVideoViewAdapter!!.getUserList()
                    val status = HashMap<Int?, Int?>()
                    val audioStatus = HashMap<Int?, Int?>()
                    if (userList != null) {
                        for (userStatusData in userList) {
                            status[userStatusData.getmUid()] = userStatusData.getmStatus()
                            audioStatus[userStatusData.getmUid()] = userStatusData.getmAudioStatus()
                        }
                    }
                    mGridVideoViewContainer!!.notifyUiChanged(
                        mUidsList,
                        config().getmUid(),
                        status,
                        audioStatus,
                        volume
                    )
                }
            }
            AGEventHandler.EVENT_TYPE_ON_APP_ERROR -> {
                val subType = data[0] as Int
                if (subType == ConstantApp.AppError.NO_CONNECTION_ERROR) {
                    val msg = getString(R.string.msg_connection_error)
                    notifyMessageChanged(Message(User(0, null), msg))
                    showLongToast(msg)
                }
            }
            AGEventHandler.EVENT_TYPE_ON_DATA_CHANNEL_MSG -> {
                peerUid = data[0] as Int
                val content = data[1] as ByteArray
                notifyMessageChanged(Message(User(peerUid, peerUid.toString()), String(content)))
                Log.d("TAG", "EVENT_TYPE_ON_DATA_CHANNEL_MSG UID: $peerUid")
            }
            AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR -> {
                val error = data[0] as Int
                val description = data[1] as String
                notifyMessageChanged(Message(User(0, null), "$error $description"))
            }
            AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED -> notifyHeadsetPlugged(data[0] as Int)
        }
    }

    private fun requestRemoteStreamType(currentHostCount: Int) {
//        log.debug("requestRemoteStreamType " + currentHostCount);
    }

    private fun doRemoveRemoteUi(uid: Int) {
//        Log.d("TAG", "doRemoveRemoteUi");
        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            val target = mUidsList.remove(uid) ?: return@Runnable

//                log.warn("SmallVideoViewAdapter notifyUiChanged-1 " + mUidsList.size());
            if (mUidsList.size < 2) {
                finish()
            }
            var bigBgUid = -1
            if (mSmallVideoViewAdapter != null) {
                bigBgUid = mSmallVideoViewAdapter!!.getExceptedUid()
            }
            if (mUidsList[bigBgUid] == null) {
                var count = 0
                for (uid in mUidsList.keys) {
                    count++
                    bigBgUid = uid
                    if (count > 0) break
                }
            }

//                log.debug("doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL) + " " + mLayoutType);

//                if (mLayoutType == LAYOUT_TYPE_DEFAULT || uid == bigBgUid) {
//                    switchToDefaultVideoView();
//                } else {
            switchToSmallVideoView(bigBgUid, -1)
            //                }
            notifyMessageChanged(Message(User(0, null), "user " + (uid and 0xFFFFFFFFL) + " left"))
        })
    }

    private fun switchToDefaultVideoView() {
        Log.d("TAG", "switchToDefaultVideoView")
        if (mSmallVideoViewDock != null) {
            mSmallVideoViewDock!!.visibility = View.GONE
        }
        mGridVideoViewContainer!!.initViewContainer(
            this,
            config().getmUid(),
            if (mVideoMuted) 1 else 0,
            if (mAudioMuted) 1 else 0,
            mUidsList,
            mIsLandscape,
            mProviderList
        )
        mLayoutType = LAYOUT_TYPE_DEFAULT
        var setRemoteUserPriorityFlag = false
        var sizeLimit = mUidsList.size
        if (sizeLimit > ConstantApp.MAX_PEER_COUNT + 1) {
            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1
        }
        for (i in 0 until sizeLimit) {
            val uid = mGridVideoViewContainer!!.getItem(i)!!.getmUid()
            if (config().getmUid() !== uid) {
                if (!setRemoteUserPriorityFlag) {
                    setRemoteUserPriorityFlag = true
                    rtcEngine()!!.setRemoteUserPriority(uid, Constants.USER_PRIORITY_HIGH)
                    //                    log.debug("setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                } else {
                    rtcEngine()!!.setRemoteUserPriority(uid, Constants.USER_PRIORITY_NORMAL)
                    //                    log.debug("setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                }
            }
        }
    }

    private fun switchToSmallVideoView(bigBgUid: Int, uid: Int) {
        Log.d("TAG", "switchToSmallVideoView")
        val slice = HashMap<Int, SurfaceView?>(1)
        slice[bigBgUid] = mUidsList[bigBgUid]
        val iterator: Iterator<SurfaceView?> = mUidsList.values.iterator()
        while (iterator.hasNext()) {
            val s = iterator.next()
            s!!.setZOrderOnTop(true)
            s.setZOrderMediaOverlay(true)
        }
        mUidsList[bigBgUid]!!.setZOrderOnTop(false)
        mUidsList[bigBgUid]!!.setZOrderMediaOverlay(false)
        mGridVideoViewContainer!!.initViewContainer(
            this,
            bigBgUid,
            if (mVideoMuted) 1 else 0,
            if (mAudioMuted) 1 else 0,
            slice,
            mIsLandscape,
            mProviderList
        )
        bindToSmallVideoView(bigBgUid, uid)
        mLayoutType = LAYOUT_TYPE_SMALL
        requestRemoteStreamType(mUidsList.size)
    }

    private fun bindToSmallVideoView(exceptUid: Int, uid: Int) {
        Log.d("TAG", "bindToSmallVideoView $uid")
        if (mSmallVideoViewDock == null) {
            val stub = findViewById(R.id.small_video_view_dock) as ViewStub
            mSmallVideoViewDock = stub.inflate() as RelativeLayout
        }
        val twoWayVideoCall = mUidsList.size == 2
        val recycler = findViewById(R.id.small_video_view_container) as RecyclerView
        var create = false
        if (mSmallVideoViewAdapter == null) {
            Log.d("TAG", "smallview adapter created")
            create = true
            mSmallVideoViewAdapter = SmallVideoViewAdapter(
                this,
                config().getmUid(),
                if (mVideoMuted) 1 else 0,
                if (mAudioMuted) 1 else 0,
                exceptUid,
                mUidsList,
                mProviderList
            )
            mSmallVideoViewAdapter!!.setHasStableIds(true)
            recycler.setHasFixedSize(true)

//            log.debug("bindToSmallVideoView " + twoWayVideoCall + " " + (exceptUid & 0xFFFFFFFFL));
            if (twoWayVideoCall) {
                recycler.layoutManager =
                    RtlLinearLayoutManager(
                        applicationContext,
                        RtlLinearLayoutManager.HORIZONTAL,
                        false
                    )
            } else {
                recycler.layoutManager =
                    LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            }
            recycler.addItemDecoration(SmallVideoViewDecoration())
            recycler.adapter = mSmallVideoViewAdapter
            recycler.addOnItemTouchListener(
                RecyclerItemClickListener(
                    baseContext,
                    object : OnItemClickListener() {
                        fun onItemClick(view: View, position: Int) {
                            onSmallVideoViewClicked(view, position)
                        }

                        fun onItemLongClick(view: View?, position: Int) {}
                        fun onItemDoubleClick(view: View?, position: Int) {
//                onSmallVideoViewDoubleClicked(view, position);
                        }
                    })
            )
            recycler.isDrawingCacheEnabled = true
            recycler.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_AUTO
        }
        var needToNotify = false
        if (!create) {
            needToNotify = true
        } else if (create && mUidsList.size > 1) {
            needToNotify = true
        }
        if (needToNotify) {
            Log.d("TAG", "smallview adapter not created")
            mSmallVideoViewAdapter!!.setLocalUid(config().getmUid())
            val status = HashMap<Int?, Int?>()
            status[exceptUid] = if (mVideoMuted) 1 else 0
            status[uid] = if (mVideoMuted) 1 else 0
            status[config().getmUid()] = if (mVideoMuted) 1 else 0
            val audioStatus = HashMap<Int?, Int?>()
            audioStatus[exceptUid] = if (mAudioMuted) 1 else 0
            audioStatus[uid] = if (mAudioMuted) 1 else 0
            audioStatus[config().getmUid()] = if (mAudioMuted) 1 else 0
            //            System.out.println("providerlist_call "+mProviderList);
            mSmallVideoViewAdapter!!.setmProviderList(mProviderList)
            mSmallVideoViewAdapter!!.notifyUiChanged(
                mUidsList,
                exceptUid,
                status,
                audioStatus,
                null
            )
        }
        for (tempUid in mUidsList.keys) {
            if (config().getmUid() !== tempUid) {
                if (tempUid == exceptUid) {
                    rtcEngine()!!.setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_HIGH)
                    //                    log.debug("setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                } else {
                    rtcEngine()!!.setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_NORMAL)
                    //                    log.debug("setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                }
            }
        }
        recycler.visibility = View.VISIBLE
        mSmallVideoViewDock!!.visibility = View.VISIBLE
    }

    private fun onSmallVideoViewClicked(view: View, position: Int) {
        //TODO: handle onSmallVideoViewClicked
        val bigBgUid = mSmallVideoViewAdapter!!.getItemUid(position)
        if (mSmallVideoViewAdapter != null && mSmallVideoViewAdapter!!.getExceptedUid() !== bigBgUid) {
            val slice = HashMap<Int?, SurfaceView?>(1)
            slice[bigBgUid] = mUidsList[bigBgUid]
            val iterator: Iterator<SurfaceView?> = mUidsList.values.iterator()
            while (iterator.hasNext()) {
                val s = iterator.next()
                s!!.setZOrderOnTop(true)
                s.setZOrderMediaOverlay(true)
            }
            val userList = mSmallVideoViewAdapter!!.getUserList()
            val status = HashMap<Int?, Int?>()
            val audioStatus = HashMap<Int?, Int?>()
            if (userList != null) {
                for (userStatusData in userList) {
                    status[userStatusData.getmUid()] = userStatusData.getmStatus()
                    audioStatus[userStatusData.getmUid()] = userStatusData.getmAudioStatus()
                }
            }
            slice[bigBgUid]!!.setZOrderOnTop(false)
            slice[bigBgUid]!!.setZOrderMediaOverlay(false)
            mGridVideoViewContainer!!.notifyUiChanged(slice, bigBgUid, status, audioStatus, null)
            //            mGridVideoViewContainer.initViewContainer(this, bigBgUid,status.get(bigBgUid), slice, mIsLandscape,mProviderList);
            if (mSmallVideoViewAdapter != null) {
                mSmallVideoViewAdapter!!.setLocalUid(config().getmUid())
                mSmallVideoViewAdapter!!.notifyUiChanged(
                    mUidsList,
                    bigBgUid,
                    status,
                    audioStatus,
                    null
                )
            }
        }
    }

    fun notifyHeadsetPlugged(routing: Int) {
//        log.info("notifyHeadsetPlugged " + routing + " " + mVideoMuted);
        mAudioRouting = routing
        val iv = findViewById(R.id.switch_speaker_id) as ImageView
        if (mAudioRouting == Constants.AUDIO_ROUTE_SPEAKERPHONE) {
            iv.setImageResource(R.drawable.ic_speaker)
            iv.background = resources.getDrawable(R.drawable.transparent_bg)
        } else {
            iv.setImageResource(R.drawable.ic_phone_speaker)
            iv.background = resources.getDrawable(R.drawable.ic_call_icon_bg)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mIsLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE

//        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
//            switchToDefaultVideoView();
//        } else if (mSmallVideoViewAdapter != null) {
        switchToSmallVideoView(mSmallVideoViewAdapter!!.getExceptedUid(), -1)
//        }
    }


    private fun registerCallerBusyBroadCast() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    val connectingError = findViewById(R.id.connection_error) as TextView
                    val providerName = intent.getStringExtra("providerName")
                    val providerObjString = intent.getStringExtra("providerList")
                    val providerListNotification = ArrayList<Provider>()
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
                    if (providerListNotification != null && providerListNotification.size > 2 || sosCall) {
                        groupCall = true
                    }
                    when (action) {
                        "caller-busy" -> {
                            if (TextUtils.isEmpty(connectingError.text)) {
                                connectingError.text =
                                    providerName + " " + getString(R.string.is_busy)
                            } else {
                                if (groupCall) {
                                    connectingError.text = """${connectingError.text}
$providerName ${getString(R.string.is_busy)}"""
                                } else {
                                    connectingError.text =
                                        providerName + " " + getString(R.string.is_busy)
                                }
                            }
                            connectingError.visibility = View.VISIBLE
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
                                val connection_message =
                                    findViewById(R.id.connection_message) as TextView
                                Handler().postDelayed({ finish() }, 2000)
                            }
                        }
                        "caller-reject" -> {
                            if (TextUtils.isEmpty(connectingError.text)) {
                                connectingError.text =
                                    providerName + " " + getString(R.string.declined)
                            } else {
                                if (groupCall) {
                                    connectingError.text = """${connectingError.text}
$providerName ${getString(R.string.declined)}"""
                                } else {
                                    connectingError.text =
                                        providerName + " " + getString(R.string.declined)
                                }
                            }
                            connectingError.visibility = View.VISIBLE
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
                        "caller-not-answer" -> {
                            if (TextUtils.isEmpty(connectingError.text)) {
                                connectingError.text =
                                    providerName + " " + getString(R.string.did_not_answer)
                            } else {
                                if (groupCall) {
                                    connectingError.text = """${connectingError.text}
$providerName ${getString(R.string.did_not_answer)}"""
                                } else {
                                    connectingError.text =
                                        providerName + " " + getString(R.string.did_not_answer)
                                }
                            }
                            connectingError.visibility = View.VISIBLE
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
            val filter = IntentFilter("caller-busy")
            filter.addAction("caller-reject")
            filter.addAction("caller-not-answer")
            registerReceiver(mBroadcastReceiver, filter)
        }
    }

    private fun unregisterCallerBusyBroadCast() {
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver)
            mBroadcastReceiver = null
        }
    }

    override fun onBackPressed() {}

    private fun handelVideoIcon(videoMuteView: ImageView, isEnable: Boolean) {
        videoMuteView.isEnabled = isEnable
        if (isEnable) {
            videoMuteView.setImageResource(if (mVideoMuted) R.drawable.ic_video_mute else R.drawable.ic_video)
            videoMuteView.background =
                if (mVideoMuted) resources.getDrawable(R.drawable.ic_call_icon_bg) else resources.getDrawable(
                    R.drawable.transparent_bg
                )
        } else {
            videoMuteView.setImageResource(if (mVideoMuted) R.drawable.ic_video_mute_disabled else R.drawable.ic_video_disabled)
            videoMuteView.background =
                if (mVideoMuted) resources.getDrawable(R.drawable.ic_call_icon_bg_disabled) else resources.getDrawable(
                    R.drawable.transparent_bg
                )
        }
    }
}
