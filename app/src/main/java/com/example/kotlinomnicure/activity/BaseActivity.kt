package com.example.kotlinomnicure.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.broadcast.InternetConnReceiver
import com.example.kotlinomnicure.customview.CustomDialog
import com.example.kotlinomnicure.customview.CustomProgressDialog
import com.example.kotlinomnicure.helper.NotificationHelper
import com.example.kotlinomnicure.interfaces.OnInternetConnChangeListener
import com.example.kotlinomnicure.interfaces.OnNetConnectedListener
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.viewmodel.ChatActivityViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.providerEndpoints.model.GroupCall
import omnicurekotlin.example.com.providerEndpoints.model.Members
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

open class BaseActivity : AppCompatActivity(), OnInternetConnChangeListener {
    private val TAG = BaseActivity::class.java.simpleName
    protected var mHandler: Handler? = null
    var toast: Toast? = null
    private var progressDialog: CustomProgressDialog? = null
    private var activity: Activity? = null
    private var internetConnReceiver: InternetConnReceiver? = null
    private var netConnectedListener: OnNetConnectedListener? = null
    private var internetDialog: CustomDialog? = null
    private var autoLogoutDialog: CustomDialog? = null
    private var mCurrentLocale: Locale? = null

    open fun getLocale(context: Context?): Locale {
        val lang: String? =
            context?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.CHANGE_LANGUAGE, "") }
        Log.e(TAG, "getLocale:lang-> $lang")
        return Locale(lang)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
        }

//         To disable the screen capturing and screen recording
        if (!UtilityMethods().isDemoTestServer() && !UtilityMethods().isQaTestServer() && !UtilityMethods.isNetccnAutoTestServer()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        // True time initialization
        AsyncTask.execute { //TODO your background code
            try {
                TrueTime.build().initialize()
            } catch (e: IOException) {
                Log.e(TAG, "Exception:", e.cause)
            }
        }
        activity = this
        mHandler = Handler()
        // Registering the internet broadcast
        registerInternetBroadcast()
    }

    /**
     * Getting the time from the True time
     *
     * @return
     */
    open fun getTime(): Long {
        var time = 0L
        try {
            time = TrueTime.now().getTime()
            Log.i(TAG, "current time $time")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error getting current time " + e.message)
            time = System.currentTimeMillis()
        }
        return time
    }

    /**
     * Close the activity if "android.R.id.home" is cliked
     *
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                run { finish() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Hide key board
     */
    open fun hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    open fun showSoftKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    protected open fun addToolbar(toolbar: Toolbar?) {
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE)
            setSupportActionBar(toolbar)
        }
    }

    /**
     * Add back button in action bar
     */
    protected open fun addBackButton() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    open fun addMandatoryText(tv: TextView) {
        val labelColor = resources.getColor(R.color.error_color)
        val colorString = String.format("%X", labelColor).substring(2)
        val htmlText = String.format(tv.text.toString() + "<font color='#%s'>*</font>", colorString)
        tv.text = Html.fromHtml(htmlText)
    }

    /**
     * Showing the progress bar - loader with text
     *
     * @param text
     */
    open fun showProgressBar(text: String?) {
        runOnUiThread {
            dismissProgressBar()
            try {
                progressDialog = CustomProgressDialog(this)
                progressDialog!!.setText(text)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setCanceledOnTouchOutside(false)
                var isDestyoed = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    isDestyoed = isDestroyed
                }
                if (!isFinishing && !isDestyoed) {
                    if (progressDialog != null && !progressDialog!!.isShowing) {
                        progressDialog!!.show()
                    }
                }
                //                if (progressDialog != null) {
//                    progressDialog.dismiss();
//                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception:", e.cause)
            }
        }
    }

    /**
     * Getting the firebase id token and calling "renewToken" API to update the same in back-end
     */
    open fun getFirebaseIdToken() {
        val refreshToken: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN, "")
        SplashViewModel().renewToken(refreshToken).observe(this) { commonResponse ->
            Log.d(TAG, "Redirect Response: " + Gson().toJson(commonResponse))
            if (commonResponse.getIdToken() != null) {
                PrefUtility().saveStringInPref(this, Constants.SharedPrefConstants.FIREBASE_IDTOKEN,
                    commonResponse.getIdToken())
            }
            if (commonResponse.getRefreshToken() != null) {
                PrefUtility().saveStringInPref(
                    this,
                    Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                    commonResponse.getRefreshToken()
                )
            }
        }
    }

    open fun showProgressBar() {
        runOnUiThread {
            dismissProgressBar()
            try {
                progressDialog = CustomProgressDialog(this)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setCanceledOnTouchOutside(false)
                var isDestyoed = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    isDestyoed = isDestroyed
                }
                if (!isFinishing && !isDestyoed) {
                    if (progressDialog != null && !progressDialog!!.isShowing) {
                        progressDialog!!.show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception:", e.cause)
            }
        }
//        dismissProgressBar();
//        try {
//            progressDialog = new CustomProgressDialog(this);
//            progressDialog.setCancelable(false);
//            boolean isDestyoed = false;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                isDestyoed = isDestroyed();
//            }
//            if (!isFinishing() && !isDestyoed) {
//                if (progressDialog != null && !progressDialog.isShowing()) {
//                    progressDialog.show();
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Exception:", e.getCause());
//        }
    }

    /**
     * Showing the team member's dialog
     *
     * @param context
     * @param members
     * @param consultProvider
     */
    open fun showTeamMembersDialog(
        context: Context?,
        members: List<Members?>?,
        consultProvider: ConsultProvider
    ) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        val inflater = this.layoutInflater
        val mDialogView = inflater.inflate(R.layout.contact_team_dialog, null)
        dialog.setContentView(mDialogView)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val close = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val membersRecycler = dialog.findViewById<View>(R.id.membersRecycler) as RecyclerView
        val grpAudio = dialog.findViewById<View>(R.id.grpAudioCall) as Button
        val grpVideo = dialog.findViewById<View>(R.id.grpVideoCall) as Button

        // Start call method is triggered
        val adapter = MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
            fun onClickCallItem(provider: Members, callType: String?) {
                startCall(context, provider, callType, mDialogView, consultProvider.getId())
            }
        })
        // Group audio method is triggered
        grpAudio.setOnClickListener {
            checkSelfPermissionsMediaCheck()
            groupCall(context, Constants.FCMMessageType.AUDIO_CALL, consultProvider, mDialogView)
        }
        // Group video method is triggered
        grpVideo.setOnClickListener {
            checkSelfPermissionsMediaCheck()
            groupCall(context, Constants.FCMMessageType.VIDEO_CALL, consultProvider, mDialogView)
        }
        //Close click listener
        close.setOnClickListener { dialog.dismiss() }
        // Setting the members adapter
        membersRecycler.adapter = adapter
        adapter.notifyDataSetChanged()
        dialog.show()
    }

    /**
     * Start call method triggered with "startCall" API call and redirected to Call activity
     *
     * @param context
     * @param provider
     * @param callType
     * @param container
     */
    open fun startCall(
        context: Context?,
        provider: Members,
        callType: String?,
        container: View?,
        patientId: Long?
    ) {
        showProgressBar()
        val providerID: Long? = context?.let { PrefUtility().getProviderId(it) }
        val token: String? =
            context?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.TOKEN, "") }
        HomeViewModel().startCall(
            providerID,
            token,
            provider.getProviderId()!!.toLong(),
            patientId,
            callType
        ).observe(this) { commonResponse ->
            dismissProgressBar()
            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {

                //Redirected to Call activity with required inputs
                val callScreen = Intent(context, CallActivity::class.java)
                callScreen.putExtra("providerName", provider.getProviderName())
                callScreen.putExtra("providerHospitalName", "Team " + provider.getTeamName())
                callScreen.putExtra("providerId", provider.getProviderId()!!.toLong())
                callScreen.putExtra("profilePicUrl", provider.getProfilePic())
                callScreen.putExtra(
                    ConstantApp().ACTION_KEY_CHANNEL_NAME,
                    providerID.toString() + "-" + provider.getProviderId()
                )
                callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                callScreen.putExtra(
                    ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                    resources.getStringArray(R.array.encryption_mode_values)[0]
                )
                callScreen.putExtra(
                    Constants.IntentKeyConstants.AUDIT_ID,
                    commonResponse.getAuditId()
                )
                callScreen.putExtra("callType", "outgoing")
                callScreen.putExtra("patientId", patientId)

                //callScreen.putExtra("call", Constants.FCMMessageType.AUDIO_CALL);
                callScreen.putExtra("call", callType)
                val gson = Gson()
                val providerList: MutableList<Provider> =
                    ArrayList<Provider>()
                val selfVal = Provider()
                selfVal.setId(provider.getId()!!.toLong())
                selfVal.setName(provider.getProviderName())
                selfVal.setHospital("Team " + provider.getTeamName())
                selfVal.setRole(provider.getRpType())
                providerList.add(selfVal)
                val selfProvider = Provider()
                selfProvider.setId(providerID)
                selfProvider.setName(
                    context?.let {
                        PrefUtility().getStringInPref(
                            it,
                            Constants.SharedPrefConstants.NAME,
                            ""
                        )
                    }
                )
                selfProvider.setProfilePicUrl(
                    context?.let {
                        PrefUtility().getStringInPref(
                            it,
                            Constants.SharedPrefConstants.PROFILE_IMG_URL,
                            ""
                        )
                    }
                )
                selfProvider.setHospital(
                    context?.let {
                        PrefUtility().getStringInPref(
                            it,
                            Constants.SharedPrefConstants.HOSPITAL_NAME,
                            ""
                        )
                    }
                )
                selfProvider.setRole(
                    context?.let {
                        PrefUtility().getStringInPref(
                            it,
                            Constants.SharedPrefConstants.ROLE,
                            ""
                        )
                    }
                )
                providerList.add(selfProvider)
                callScreen.putExtra("providerList", gson.toJson(providerList))
                startActivity(callScreen)
            } else {
                val errMsg: String? = context?.let {
                    ErrorMessages().getErrorMessage(
                        it,
                        commonResponse.getErrorMessage(),
                        Constants.API.startCall
                    )
                }
                //                UtilityMethods.showErrorSnackBar(container, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    container,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )!!
                    .show()
            }
        }
    }

    /**
     * Start the group call method triggered with "multipleCall" API call and redirected to Call activity
     *
     * @param context
     * @param callType
     * @param mConsultProvider
     * @param container
     */
    open fun groupCall(
        context: Context?,
        callType: String?,
        mConsultProvider: ConsultProvider,
        container: View?
    ) {
        showProgressBar()
        val providerID: Long? = context?.let { PrefUtility().getProviderId(it) }
        val token: String? =
            context?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.TOKEN, "") }
        val content = GroupCall()
        content.setId(providerID.toString())
        content.setMessage("")
        content.setPatientsId(java.lang.String.valueOf(mConsultProvider.getPatientsId()))
        content.setToken(token)
        content.setType(callType)
        //        content.setReceiverIds(receiverIds.toArray(new String[receiverIds.size()]));
        ChatActivityViewModel().multipleCall(content)?.observe(this) { commonResponse ->
            dismissProgressBar()
            println("callresponse $commonResponse")
            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()!!) {
                //Redirected to Call activity with required inputs
                val callScreen = Intent(context, CallActivity::class.java)
                callScreen.putExtra(
                    "providerName",
                    context?.let { PrefUtility().getStringInPref(it, Constants.SharedPrefConstants.NAME, "") }
                )
                callScreen.putExtra(
                    "providerHospitalName",
                    context?.let {
                        PrefUtility().getStringInPref(
                            it,
                            Constants.SharedPrefConstants.HOSPITAL_NAME,
                            ""
                        )
                    }
                )

//                callScreen.putExtra("providerId", provider.getId());
//                String chName = "";
//                for(int i=0; i< commonResponse.getProviderList().size(); i++){
//                    Provider pr = commonResponse.getProviderList().get(i);
//                    chName = "-"+pr.getId();
//                }
//                callScreen.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, mConsultProvider.getId()+chName);
                callScreen.putExtra(
                    ConstantApp.ACTION_KEY_CHANNEL_NAME,
                    "" + mConsultProvider.getPatientsId()
                )
                callScreen.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "")
                callScreen.putExtra(
                    ConstantApp.ACTION_KEY_ENCRYPTION_MODE,
                    resources.getStringArray(R.array.encryption_mode_values)[0]
                )
                callScreen.putExtra("patientId", mConsultProvider.getPatientsId())
                callScreen.putExtra(
                    Constants.IntentKeyConstants.AUDIT_ID,
                    commonResponse.getAuditId()
                )
                var receiverId = 0L
                if (commonResponse.getProviderList().size() <= 2) {
                    for (i in 0 until commonResponse.getProviderList().size()) {
                        val pr: Provider = commonResponse.getProviderList().get(i)
                        if (pr.getId() != null && !pr.getId()
                                .equals(PrefUtility.getProviderId(context))
                        ) {
                            receiverId = pr.getId()
                            break
                        }
                    }
                }
                if (receiverId != 0L) {
                    callScreen.putExtra("providerId", receiverId)
                }
                //                callScreen.putExtra("patientId", Long.parseLong(mConsultProvider.getPatientId()));
                callScreen.putExtra("callType", "outgoing")
                callScreen.putExtra("call", callType)
                Log.i(
                    TAG,
                    "call params " + commonResponse.getProviderList().toString() + " " + receiverId
                )
                val gson = Gson()
                callScreen.putExtra("providerList", gson.toJson(commonResponse.getProviderList()))
                startActivity(callScreen)
            } else {
                val errMsg: String = ErrorMessages.getErrorMessage(
                    context,
                    commonResponse.getErrorMessage(),
                    Constants.API.startCall
                )
                //                UtilityMethods.showErrorSnackBar(container, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    container,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )!!
                    .show()
            }
        }
    }

    /**
     * Dismiss progress bar
     */
    open fun dismissProgressBar() {
        try {
            var isDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (isDestroyed()) {
                    isDestroyed = true
                }
            }
            if (!isFinishing && !isDestroyed && progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            progressDialog = null
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * Setting the title in action bar
     *
     * @param str
     */
    protected open fun setTitle(str: String?) {
        if (supportActionBar != null) {
            supportActionBar!!.title = str
        }
    }

    /**
     * Unregister internet broadcast receiver
     */
    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcastReceiver()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
        Log.i(TAG, "onDestroy: " + getCurrentActivityName())
        // Saving flag to disable/stop showing push notification to the user, when session expired
        // If false - notification can be displayed
        // If true - Notification will not be displayed
        PrefUtility.saveBooleanInPref(
            activity,
            Constants.SharedPrefConstants.DISABLE_NOTIFICATION,
            false
        )
    }

    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/
    /**
     * Internet broadcast listener
     */
    open fun registerInternetBroadcast() {
        try {
            val internetRecFilter = IntentFilter()
            internetRecFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            internetConnReceiver = InternetConnReceiver(this)
            registerReceiver(internetConnReceiver, internetRecFilter)
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * Unregister internet broadcast receiver
     */
    private open fun unregisterBroadcastReceiver() {
        unregisterReceiver(internetConnReceiver)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: " + getCurrentActivityName())
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        //Toast.makeText(this,getCurrentActivityName(),Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart: " + getCurrentActivityName())
        // Getting the current locale
        mCurrentLocale = resources.configuration.locale
    }

    override fun onRestart() {
        super.onRestart()
        // Localization check and update
        val locale = getLocale(this)
        if (locale != mCurrentLocale) {
            mCurrentLocale = locale
            recreate()
        }
        Log.e(TAG, "onRestart:--> ")
//        getFirebaseIdToken();
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: " + getCurrentActivityName())
    }

    /**
     * Get the current activity name
     *
     * @return
     */
    open fun getCurrentActivityName(): String {
        if (activity is SplashActivity) {
            return SplashActivity::class.java.simpleName
        } else if (activity is LoginActivity) {
            return LoginActivity::class.java.simpleName
        } else if (activity is RegistrationActivity) {
            return RegistrationActivity::class.java.simpleName
        } else if (activity is OTPActivity) {
            return OTPActivity::class.java.simpleName
        } else if (activity is HomeActivity) {
            return HomeActivity::class.java.simpleName
        } else if (activity is ChatActivity) {
            return ChatActivity::class.java.simpleName
        } else if (activity is AddPatientActivity) {
            return AddPatientActivity::class.java.simpleName
        } else if (activity is AddPatientVitalsActivity) {
            return AddPatientVitalsActivity::class.java.simpleName
        } else if (activity is ResetPasswordActivity) {
            return ResetPasswordActivity::class.java.getSimpleName()
        } else if (activity is TeamGroupChatActivity) {
            return TeamGroupChatActivity::class.java.getSimpleName()
        } else if (activity is TrainingMaterialActivity) {
            return TrainingMaterialActivity::class.java.getSimpleName()
        } else if (activity is SystemAlertActivity) {
            return SystemAlertActivity::class.java.getSimpleName()
        } else if (activity is GroupCallActivity) {
            return GroupCallActivity::class.java.getSimpleName()
        } else if (activity is PatientDetailActivity) {
            return PatientDetailActivity::class.java.simpleName
        } else if (activity is TransferPatientActivity) {
            return TransferPatientActivity::class.java.simpleName
        } else if (activity is MyVirtualTeamsActivity) {
            return MyVirtualTeamsActivity::class.java.getSimpleName()
        } else if (activity is HandOffPatientsActivity) {
            return HandOffPatientsActivity::class.java.simpleName
        } else if (activity is RemoteHandOffActivity) {
            return RemoteHandOffActivity::class.java.simpleName
        } else if (activity is FilterActivity) {
            return FilterActivity::class.java.getSimpleName()
        } else if (activity is RegistrationSuccessActivity) {
            return RegistrationSuccessActivity::class.java.simpleName
        } else if (activity is InitVideoCallActivity) {
            return InitVideoCallActivity::class.java.getSimpleName()
        } else if (activity is RingingActivity) {
            return RingingActivity::class.java.getSimpleName()
        } else if (activity is NetworkTestActivity) {
            return NetworkTestActivity::class.java.getSimpleName()
        } else if (activity is CallActivity) {
            return CallActivity::class.java.simpleName
        } else if (activity is CallOptionsActivity) {
            return CallOptionsActivity::class.java.getSimpleName()
        } else if (activity is SettingsActivity) {
            return SettingsActivity::class.java.getSimpleName()
        }
        return activity!!.javaClass.simpleName
    }

    /**
     * Internet Connection change listener
     *
     * @param intent
     */
    override fun onConnectionChanged(intent: Intent?) {
        val isConnected: Boolean = activity?.let { UtilityMethods().isInternetConnected(it) } == true
        Log.i(TAG, "onConnectionChanged: is Internet connected...$isConnected")
        if (activity is SplashActivity) {
            netConnectedListener = this as SplashActivity
        }
        if (isConnected) {
            // Dismiss internet dialog if connected
            dismissInternetDialog()
            if (netConnectedListener != null) {
                netConnectedListener!!.onConnected()
            }
        } else {
            // Showing the internet dialog if no internet connection found
            showInternetDialog()
        }
    }

    /**
     * Showing the internet dialog if no internet connection found
     */
    open fun showInternetDialog() {
        try {
            if (activity == null) {
                return
            }
            if ((activity!!.applicationContext as OmnicureApp).isAppInBackground()) {
                return
            }
            //if logout dialog is already showing , don't show internet dialog
            if (autoLogoutDialog != null && autoLogoutDialog!!.isShowing) {
                return
            }
            if (internetDialog != null && internetDialog!!.isShowing) {
                dismissInternetDialog()
            }
            val isDestroyed = AtomicBoolean(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed.set(false)
            }
            val positiveBtnListener = label@ View.OnClickListener { view: View? ->
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        isDestroyed.set(isDestroyed())
                    }
                    val isInternet: Boolean = UtilityMethods().isInternetConnected(this)
                    if (!isInternet) {
                        if (toast != null) {
                            toast!!.cancel()
                            toast = null
                        }
                        toast = Toast.makeText(
                            this,
                            resources.getString(R.string.no_internet_connectivity),
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                        return@label
                    }
                    if (toast != null) {
                        toast!!.cancel()
                        toast = null
                    }
                    if (isInternet && internetDialog != null && internetDialog!!.isShowing && !isDestroyed.get() && !isFinishing) {
                        internetDialog!!.dismiss()
                        internetDialog = null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception:", e.cause)
                }
            }
            val message = getString(R.string.no_internet_dialog_message)
            if ((internetDialog == null || !internetDialog!!.isShowing) && !isFinishing && !isDestroyed.get()) {
                internetDialog = UtilityMethods().showDialog(
                    this as ActivityConsultChart,
                    getString(R.string.no_internet_dialog_title),
                    message,
                    false,
                    R.string.try_again,
                    positiveBtnListener,
                    -1,
                    null,
                    -1,
                    true
                )
                internetDialog!!.setPositiveButtonDrawable(R.drawable.dialog_single_btn_drawable)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    open fun getVersion(): String? {
        val v: String
        v = try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            if (UtilityMethods().isTestServer()) {
                "v " + packageInfo.versionName + " - DEV"
            } else if (UtilityMethods().isDemoTestServer()) {
                "v " + packageInfo.versionName + " - Demo"
            } else if (UtilityMethods().isQaTestServer()) {
                "v " + packageInfo.versionName + " - INT"
            } else if (UtilityMethods().isPilotServer()) {
                "v " + packageInfo.versionName + " - Pilot"
            } else if (UtilityMethods().isStagingServer()) {
                "v " + packageInfo.versionName + " - Stage"
            } else {
                "v " + packageInfo.versionName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Exception:", e.cause)
            "v "
        }
        return v
    }

    /**
     * Show auto logout dialog
     */
    open fun showAutoLogoutDialog() {
        try {
            if (activity == null || getCurrentActivityName() == "SplashActivity") {
                return
            }
            if ((activity!!.applicationContext as OmnicureApp).isAppInBackground()) {
                return
            }
            if (autoLogoutDialog != null && autoLogoutDialog!!.isShowing) {
//                autoLogoutDialog.dismiss();
                return
            }
            val isDestroyed = AtomicBoolean(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed.set(isDestroyed())
            }
            /* String message = activity.getString(R.string.auto_logout_message);
            long logoutTimeInMillis = PrefUtility.getLongInPref(activity, Constants.SharedPrefConstants.AUTO_LOGOUT_TIME, Constants.AUTO_LOGOUT_TIME_IN_MILLIS);
            int logoutTimeInMinutes = (int) logoutTimeInMillis / (60 * 1000);
            int logoutTimeInHours = (int) (logoutTimeInMinutes / 60);
            Log.d(TAG, "LOGOUT_TIMER" + logoutTimeInMinutes);
//            message = message + " " + logoutTimeInMinutes + " " + getString(R.string.minutes_of_inactivity);
            message = message + " " + logoutTimeInHours + " " + getString(R.string.hours_of_inactivity);*/
            val message: String? = PrefUtility().getStringInPref(
                activity!!,
                Constants.SharedPrefConstants.SESSION_TIMEOUT_MESSAGE,
                getString(R.string.session_logged_out)
            )
            val title: String? = PrefUtility().getStringInPref(
                activity!!,
                Constants.SharedPrefConstants.SESSION_TIMEOUT_TITLE,
                activity!!.getString(R.string.logout)
            )
            //Based on the message received
            val positiveBtnListener =
                View.OnClickListener { view: View? ->
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            isDestroyed.set(isDestroyed())
                        }
                        if (autoLogoutDialog != null && autoLogoutDialog!!.isShowing && !isDestroyed.get() && !isFinishing) {
                            autoLogoutDialog!!.dismiss()
                            autoLogoutDialog = null
                            //                        launchLoginActivity();
                            val userId: Long? = PrefUtility().getProviderId(this)
                            val token: String? = PrefUtility().getToken(this)
                            //                        String token = PrefUtility.getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "");
                            Log.i(TAG, "LOGOUT_TOKEN----$userId-----$token")

                            // Logout helper class is triggered based on the message received
                            LogoutHelper(this, null).doLogout()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception:", e.cause)
                    }
                }
            if ((autoLogoutDialog == null || !autoLogoutDialog!!.isShowing) && !isFinishing && !isDestroyed.get()) {
                if (internetDialog != null && internetDialog!!.isShowing) {
                    internetDialog!!.dismiss()
                }
                autoLogoutDialog = message?.let {
                    title?.let { it1 ->
                        UtilityMethods().showDialog(
                            this as ActivityConsultChart,
                            it1,
                            it,
                            false,
                            R.string.ok,
                            positiveBtnListener,
                            -1,
                            null,
                            -1,
                            true
                        )
                    }
                }
                autoLogoutDialog!!.setPositiveButtonDrawable(R.drawable.dialog_single_btn_drawable)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * Clearing the prefenrence values
     */
    open fun clearPrefs() {
        NotificationHelper(activity).clearAllNotification()
        activity?.let { PrefUtility().clearAllData(it) }
        activity?.let { PrefUtility().clearRedirectValidation(it) }
    }

    /**
     * Dismiss internet dialog if connected
     */
    open fun dismissInternetDialog() {
        try {
            var isDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed = isDestroyed()
            }
            if (internetDialog != null && internetDialog!!.isShowing && !isDestroyed && !isFinishing) {
                internetDialog!!.dismiss()
                internetDialog = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    /**
     * Attching the base context and setting up the locale
     *
     * @param base
     */
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
        Log.e(TAG, "attachBaseContext: ")
        // Setting the locale after config is changed////            setLocale(overrideConfiguration);
        setLocale()
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // update overrideConfiguration with default locale
            setLocale()
        }
        Log.e(TAG, "configuration: " + overrideConfiguration.locale)
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.e(TAG, "onConfigurationChanged: " + newConfig.locale)
    }

    /**
     * Setting the locale after config is changed
     */
    open fun setLocale() {
        val resources = resources
        val configuration = resources.configuration
        val dm = resources.displayMetrics
        val locale = getLocale(this)
        // Log.e(TAG, "setLocale:base "+locale );
        // Log.e(TAG, "setLocale:basedispLang "+locale.getLanguage() );
        // Log.e(TAG, "configuration.locale:Base "+configuration.locale );
        if (configuration.locale != locale) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale)
            }
            resources.updateConfiguration(configuration, dm)
        }
    }

    /**
     * Updating the base context locale
     *
     * @param context
     * @return
     */
    open fun updateBaseContextLocale(context: Context): Context? {

        // If changeLanguage is null, Save english as a default locale in shared preference
        if (PrefUtility().getStringInPref(
                context,
                Constants.SharedPrefConstants.CHANGE_LANGUAGE,
                ""
            ) == null || PrefUtility().getStringInPref(
                context,
                Constants.SharedPrefConstants.CHANGE_LANGUAGE,
                ""
            )?.isEmpty() == true
        ) {
            PrefUtility().saveStringInPref(
                this,
                Constants.SharedPrefConstants.CHANGE_LANGUAGE,
                "en"
            )
        }
        // Helper method to get saved language from SharedPreferences and setting up the locale
        val language: String? =
            PrefUtility().getStringInPref(context, Constants.SharedPrefConstants.CHANGE_LANGUAGE, "")
        val locale = Locale(language)
        Locale.setDefault(locale)
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            updateResourcesLocale(context, locale)
        } else updateResourcesLocaleLegacy(context, locale)
    }

    /**
     * Setting up the locale for new config
     *
     * @param context
     * @param locale
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    open fun updateResourcesLocale(context: Context, locale: Locale): Context? {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    /**
     * Updating the configuration based on the locale
     *
     * @param context
     * @param locale
     * @return
     */
    open fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context? {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    open fun checkSelfPermissionsMediaCheck(): Boolean {
        return checkSelfPermissionGrantedCheck(
            Manifest.permission.RECORD_AUDIO,
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO
        ) &&
                checkSelfPermissionGrantedCheck(
                    Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA
                )
    }

    open fun checkSelfPermissionGrantedCheck(permission: String, requestCode: Int): Boolean {
        Log.i("checkSelfPermission ", "$permission $requestCode")
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(
            "onRequestPermissions", requestCode.toString() + " " + Arrays.toString(permissions)
                    + " " + Arrays.toString(grantResults)
        )
        when (requestCode) {
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO -> {
                checkSelfPermissionGrantedCheck(
                    Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA
                )
            }
            ConstantApp().PERMISSION_REQ_ID_CAMERA -> {
            }
            else -> {
            }
        }
    }
}
