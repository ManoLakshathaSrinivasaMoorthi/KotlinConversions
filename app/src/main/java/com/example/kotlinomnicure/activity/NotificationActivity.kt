package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.BadTokenException
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dailytasksamplepoc.kotlinomnicure.activity.BaseActivity
import com.example.dailytasksamplepoc.kotlinomnicure.activity.EncUtil
import com.example.dailytasksamplepoc.kotlinomnicure.customview.CustomProgressDialog
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.helper.LogoutHelper
import com.example.kotlinomnicure.utils.*

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.example.kotlinomnicure.viewmodel.LoginViewModel

import java.lang.Exception
import java.security.KeyStore
import java.util.concurrent.Executor
import javax.crypto.Cipher

class NotificationActivity : BaseActivity(){
    // Variables, layouts, viewmodel
    private val TAG = BaseActivity::class.java.simpleName
    var alertDialog: AlertDialog? = null
    var linearBg: LinearLayout? = null
    var container: LinearLayout? = null
    var editTextPassword: EditText? = null
    var dialogView: View? = null
    private var dialogTitle: String? = ""
    private var dialogMessage: String? = ""
    private var messageType: String? = ""
    private var context: Activity? = null
    var biometricPrompt: BiometricPrompt? = null
    private var viewModel: LoginViewModel? = null
    private val fingerprintManager: FingerprintManager? = null
    private val keyguardManager: KeyguardManager? = null
    private val keyStore: KeyStore? = null
    private val cipher: Cipher? = null
    private val KEY_NAME = "AndroidKey"
    private var ctx:Context= NotificationActivity()
    override fun onCreate(savedInstanceState: Intent?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Context initialization
        context = this
        // Layout initialization
        linearBg = findViewById(R.id.llBackground) as LinearLayout
        container = findViewById(R.id.container) as LinearLayout
        // View model initialization
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // Handling the intent
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handling the new intent
        handleIntent(intent)
    }

    /**
     * Getting the intent and show the pop up based on the input values received
     * @param intent
     */
    private fun handleIntent(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            dialogTitle = extras.getString("remoteTitle")
            dialogMessage = extras.getString("remoteMessage")
            messageType = extras.getString("messageType")
        }
        if (messageType != null && (messageType == Constants.FCMMessageType.DENIED_USER || messageType == Constants.FCMMessageType.LOGOUT_EXCEEDED || messageType == Constants.FCMMessageType.PASSWORD_LOCK || messageType == Constants.FCMMessageType.TEMP_LOCK || messageType == Constants.FCMMessageType.LOCKED_USER)) {
            linearBg!!.visibility = View.VISIBLE
        } else {
            linearBg!!.visibility = View.GONE
        }


        // If the message type == Constants.FCMMessageType.PASSWORD_LOCK, then show password pop up or else Show error pop up
        if (messageType != null && messageType == Constants.FCMMessageType.PASSWORD_LOCK) {
            if (!getTopActivity().contains("CallActivity") && !getTopActivity().contains("RingingActivity")) {
                val finerprintstate: Boolean =
                    com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility().getBooleanInPref(this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false)
                if (finerprintstate) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //generateKey();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            //need to check

                            checkfingerprintabove29()
                        } else {
                            belowversionV9()
                        }
                    }
                } else {
                    showPasswordPopup()
                }
            } else {
                if (alertDialog != null) {
                    alertDialog!!.dismiss()
                }
            }
        } else {
            showErrorPopup()
        }
    }

    fun checkfingerprintabove29() {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            showPasswordPopup()
        } else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            showPasswordPopup()
        } else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            showPasswordPopupfingerprint()
        }
    }

    @SuppressLint("MissingPermission")
    fun belowversionV9() {
        val fingerprintManager2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        if (fingerprintManager2 == null) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            showPasswordPopup()
        } else if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !fingerprintManager2.isHardwareDetected
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
          PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            showPasswordPopup()
        } else if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !fingerprintManager2.hasEnrolledFingerprints()
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD,
                "")
            showPasswordPopup()
        } else {
            showPasswordPopupfingerprint()
        }
    }

    private fun getTopActivity(): String {
        val am =
            getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val cn = am.getRunningTasks(1)[0].topActivity
        return cn!!.shortClassName
    }

    override fun onPause() {
        if (getTopActivity().contains("CallActivity") || getTopActivity().contains("RingingActivity")) {

            stopAuth()
            finish()
        } else {
        }
        super.onPause()
    }

    fun stopFP() {
        stopAuth()
    }

    override fun onResume() {
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
        if (messageType != null && messageType == Constants.FCMMessageType.PASSWORD_LOCK) {
            if (!getTopActivity().contains("CallActivity") && !getTopActivity().contains("RingingActivity")) {
                val finerprintstate: Boolean =
                    PrefUtility().getBooleanInPref(this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false)
                if (finerprintstate) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //generateKey();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            //need to check
                            //checkfingerprint();
                            checkfingerprintabove29()
                        } else {
                            belowversionV9()
                        }
                    }
                } else {
                    showPasswordPopup()
                }
            } else {
                if (alertDialog != null) {
                    alertDialog!!.dismiss()
                }
            }
        } else {
            showErrorPopup()
        }
        super.onResume()
    }

    /**
     * This error popup makes the user to log out
     */
    private fun showErrorPopup() {
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
        stopAuth()
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.custom_alert_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.text = dialogTitle
        alertMsg.text = dialogMessage
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)



        // Ok button click listener
        buttonOk.setOnClickListener {
            // If messageType == LOGOUT_EXCEEDED or DENIED_USER or LOCKED_USER
            //  Clear the preference values and redirect to login activity, signout from the firebase as well
            if (messageType == Constants.FCMMessageType.LOGOUT_EXCEEDED) {
                val email: String? =
                    context?.let { it1 -> PrefUtility().getStringInPref(it1, Constants.SharedPrefConstants.EMAIL, "") }
                val password: String? =
                    context?.let { it1 -> PrefUtility().getStringInPref(it1, Constants.SharedPrefConstants.PASSWORD, "") }
                val finerprintstate: Boolean = PrefUtility().getBooleanInPref(context as NotificationActivity,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    false)
                clearPrefs()

                if (finerprintstate) {
                    PrefUtility().saveStringInPref(ctx,
                        Constants.SharedPrefConstants.PASSWORD,
                        password)
                    PrefUtility().saveStringInPref(ctx,
                        Constants.SharedPrefConstants.EMAIL,
                        email)
                    PrefUtility().saveBooleanInPref(context,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        finerprintstate)
                } else {
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                FirebaseAuth.getInstance().signOut()
                // To disable/stop showing push notification to the user, when session expired
                // If false - notfication can be displayed
                // If true - Notification will not be displayed
                PrefUtility().saveBooleanInPref(this,
                    Constants.SharedPrefConstants.DISABLE_NOTIFICATION,
                    false)
            } else if (messageType == Constants.FCMMessageType.DENIED_USER) {
                val email: String? =
                    context?.let { it1 -> PrefUtility().getStringInPref(it1, Constants.SharedPrefConstants.EMAIL, "") }
                val password: String? =
                    context?.let { it1 -> PrefUtility().getStringInPref(it1, Constants.SharedPrefConstants.PASSWORD, "") }
                val finerprintstate: Boolean = PrefUtility().getBooleanInPref(context as NotificationActivity,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    false)
                PrefUtility().saveBooleanInPref(context,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    false)
                clearPrefs()
                
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                FirebaseAuth.getInstance().signOut()

                // To disable/stop showing push notification to the user, when session expired
                // If false - notfication can be displayed
                // If true - Notification will not be displayed
                PrefUtility().saveBooleanInPref(this,
                    Constants.SharedPrefConstants.DISABLE_NOTIFICATION,
                    false)
            } else {
                LogoutHelper(this, null).doLogout()
                // To disable/stop showing push notification to the user, when session expired
                // If false - notfication can be displayed
                // If true - Notification will not be displayed
                PrefUtility().saveBooleanInPref(this,
                    Constants.SharedPrefConstants.DISABLE_NOTIFICATION,
                    false)
            }
            finish()
        }
        if (!(context is Activity && (context as Activity).isFinishing)) {
            try {
                if (!alertDialog!!.isShowing) {
                    alertDialog!!.show()
                }
            } catch (e: BadTokenException) {
=
            }
        }

    }

    private fun onSuccessLogout() {
        val topic: String? = UtilityMethods().getFCMTopic()
        if (topic != null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener {

                }
        }
    }

    private fun showPasswordPopupfingerprint() {

        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.fingerprint_dialog, viewGroup, false)
    
        val btnContinue = dialogView!!.findViewById<Button>(R.id.buttonContinue)
        val btnSignOut = dialogView!!.findViewById<Button>(R.id.buttonSignOut)
        val fpLayout = dialogView!!.findViewById<LinearLayout>(R.id.fingerprintLayout)
        editTextPassword = dialogView!!.findViewById(R.id.editTextPassword)
        val mHeadingLabel = dialogView!!.findViewById<TextView>(R.id.headingLabel)
        val mFingerprintImage = dialogView!!.findViewById<ImageView>(R.id.fingerprintImage)
        mFingerprintImage.setImageResource(R.drawable.fingerprint)
        val mParaLabel = dialogView!!.findViewById<TextView>(R.id.paraLabel)
        mParaLabel.text = "Click the image and get fingerprint access"
        mFingerprintImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val finerprintstate: Boolean =
                    PrefUtility().getBooleanInPref(this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false)
                if (finerprintstate) {
                    biometric()
                }


            }
        }
        val passwordVisibility = dialogView!!.findViewById<ImageView>(R.id.password_visibility)
        passwordVisibility.setImageResource(R.drawable.ic_visibility)
        editTextPassword!!.setTypeface(Typeface.DEFAULT)
        builder.setView(dialogView)
        passwordVisibility.setOnClickListener {
            ValidationUtil().passwordVisibility(editTextPassword!!,
                passwordVisibility)
        }
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //   generateKey();
            val finerprintstate: Boolean = PrefUtility().getBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false)
            if (finerprintstate) {
                biometric()
            }


        }
        if (!UtilityMethods().isDemoTestServer() && !UtilityMethods().isQaTestServer() && !UtilityMethods().isNetccnAutoTestServer()) {
            alertDialog!!.window!!.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE)
        }
        btnContinue.setOnClickListener {
            handleMultipleClick(btnContinue)

            if (ValidationUtil().checkEdittext(editTextPassword) == true) {
                checkPasswordApifingerprint(editTextPassword!!.getText().toString())
            }
        }
        btnSignOut.setOnClickListener {
            handleMultipleClick(btnSignOut)
            LogoutHelper(this, null).doLogout()
        }
        alertDialog!!.show()
    }

    private fun getMainThreadExecutor(): Executor? {
        return MainThreadExecutor()
    }

    private class MainThreadExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(r: Runnable) {
            handler.post(r)
        }
    }

    fun stopAuth() {
        if (biometricPrompt != null) biometricPrompt!!.cancelAuthentication()
    }

    fun biometric() {
        //   Executor executor = Executors.newSingleThreadExecutor();
        val activity: FragmentActivity = this
        biometricPrompt = BiometricPrompt(activity,
            getMainThreadExecutor()!!, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT) {
                        /**
                         * The device does not have a biometric sensor.
                         */
                        /**
                         * The device does not have a biometric sensor.
                         */
                        PrefUtility().saveBooleanInPref(this,
                            Constants.SharedPrefConstants.FINGERPRINTFLAG,
                            false)
                        PrefUtility().saveStringInPref(this,
                            Constants.SharedPrefConstants.PASSWORD,
                            "")
                        handledisable()
                        //  menufingerprint.setVisibility(View.GONE);
                    } else if (errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE) {
                        /**
                         * The hardware is unavailable. Try again later.
                         */
                        /**
                         * The hardware is unavailable. Try again later.
                         */
                    } else if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS) {
                        /**
                         * The user does not have any biometrics enrolled.
                         */
                        /**
                         * The user does not have any biometrics enrolled.
                         */
                        //   menufingerprint.setVisibility(View.GONE);
                        PrefUtility().saveBooleanInPref(this,
                            Constants.SharedPrefConstants.FINGERPRINTFLAG,
                            false)
                        PrefUtility().saveStringInPref(this,
                            Constants.SharedPrefConstants.PASSWORD,
                            "")
                        handledisable()
                    } else if (errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT) {
                        /**
                         * The operation was canceled because ERROR_LOCKOUT occurred too many times.
                         * Biometric authentication is disabled until the user unlocks with strong authentication
                         * (PIN/Pattern/Password)
                         */
                        /**
                         * The operation was canceled because ERROR_LOCKOUT occurred too many times.
                         * Biometric authentication is disabled until the user unlocks with strong authentication
                         * (PIN/Pattern/Password)
                         */
                    } else if (errorCode == BiometricPrompt.ERROR_VENDOR) {
                        /**
                         * Hardware vendors may extend this list if there are conditions that do not fall under one of
                         * the above categories. Vendors are responsible for providing error strings for these errors.
                         * These messages are typically reserved for internal operations such as enrollment, but may be
                         * used to express vendor errors not otherwise covered. Applications are expected to show the
                         * error message string if they happen, but are advised not to rely on the message id since they
                         * will be device and vendor-specific
                         */
                        /**
                         * Hardware vendors may extend this list if there are conditions that do not fall under one of
                         * the above categories. Vendors are responsible for providing error strings for these errors.
                         * These messages are typically reserved for internal operations such as enrollment, but may be
                         * used to express vendor errors not otherwise covered. Applications are expected to show the
                         * error message string if they happen, but are advised not to rely on the message id since they
                         * will be device and vendor-specific
                         */
                    } else if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                        /**
                         * The operation was canceled because the API is locked out due to too many attempts.
                         * This occurs after 5 failed attempts, and lasts for 30 seconds.
                         */
                        /**
                         * The operation was canceled because the API is locked out due to too many attempts.
                         * This occurs after 5 failed attempts, and lasts for 30 seconds.
                         */
                        CustomSnackBar.make(dialogView,
                            this,
                            CustomSnackBar.WARNING,
                            getString(R.string.fpdisable),
                            CustomSnackBar.TOP,
                            300)?.show()


                        stopAuth()
                        //  handledisable();

                      
                    } else if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // user clicked negative button

                        //   fingerprintonoff.setChecked(false);
                        //handledisable();
                    } else if (errorCode == BiometricPrompt.ERROR_TIMEOUT) {
                        /**
                         * Error state returned when the current request has been running too long. This is intended to
                         * prevent programs from waiting for the biometric sensor indefinitely. The timeout is platform
                         * and sensor-specific, but is generally on the order of 30 seconds.
                         */
                        /**
                         * Error state returned when the current request has been running too long. This is intended to
                         * prevent programs from waiting for the biometric sensor indefinitely. The timeout is platform
                         * and sensor-specific, but is generally on the order of 30 seconds.
                         */
                        CustomSnackBar.make(dialogView,
                            this,
                            CustomSnackBar.WARNING,
                            getString(R.string.fpdisable),
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()


                        stopAuth()

                    } else if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {


                    } else if (errorCode == BiometricPrompt.ERROR_CANCELED) {


                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val password: String? = PrefUtility().getStringInPref(this,
                        Constants.SharedPrefConstants.PASSWORD,
                        "")
                    EncUtil().generateKey(activity)
                    val decryptpassword: String? =
                        EncUtil().decrypt(this, password)
                    checkPasswordApifingerprint(decryptpassword)
                    super.onAuthenticationSucceeded(result)

                }

                override fun onAuthenticationFailed() {
                    val finerprintstate: Boolean =
                        PrefUtility().getBooleanInPref(this,
                            Constants.SharedPrefConstants.FINGERPRINTFLAG,
                            false)
                    if (finerprintstate) {
                        CustomSnackBar.make(dialogView,
                            this,
                            CustomSnackBar.WARNING,
                            getString(R.string.Authfaild),
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()

                    }
                    super.onAuthenticationFailed()
                    
                }
            })
        val promptInfo = PromptInfo.Builder()
            .setTitle("Authentication Required")
            .setNegativeButtonText("Cancel")
            .build()
        biometricPrompt!!.authenticate(promptInfo)
    }

    private fun handledisable() {
        mHandler = Handler()
        mHandler!!.postDelayed(Runnable {
            if (alertDialog != null) {
                alertDialog!!.dismiss()
                finish()
            }
            //Do something after 100ms
        }, 1000)
    }



    private fun showPasswordPopup() {

        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.password_dialog, viewGroup, false)

        val btnContinue = dialogView!!.findViewById<Button>(R.id.buttonContinue)
        val btnSignOut = dialogView!!.findViewById<Button>(R.id.buttonSignOut)
        editTextPassword = dialogView!!.findViewById(R.id.editTextPassword)
        val passwordVisibility = dialogView!!.findViewById<ImageView>(R.id.password_visibility)
        passwordVisibility.setImageResource(R.drawable.ic_visibility)
        editTextPassword!!.setTypeface(Typeface.DEFAULT)
        builder.setView(dialogView)

        // Pasword visibility click listener
        passwordVisibility.setOnClickListener {
            ValidationUtil().passwordVisibility(editTextPassword!!,
                passwordVisibility)
        }
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        if (!UtilityMethods().isDemoTestServer() && !UtilityMethods().isQaTestServer() && !UtilityMethods().isNetccnAutoTestServer()) {
            alertDialog!!.window!!.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE)
        }

        // Continue button click listener once after entering the password
        btnContinue.setOnClickListener {
            handleMultipleClick(btnContinue)

            if (ValidationUtil().checkEdittext(editTextPassword) == true) {
                checkPasswordApi(editTextPassword!!.getText().toString())
            }
           
        }

        // Sign out button click listener
        btnSignOut.setOnClickListener {
            // Multi click issue handling method - This retricts the user to click the signout button for multiple times
            handleMultipleClick(btnSignOut)
            // Logging out the user
            LogoutHelper(this, null).doLogout()
        }
        if (!(context is Activity && (context as Activity).isFinishing)) {
            try {
                if (!alertDialog!!.isShowing) {
                    alertDialog!!.show()
                }
            } catch (e: BadTokenException) {

            }
        }

    }

    /**
     * Multi click issue handling method - This retricts the user to click the dedicated view button for multiple times
     * @param view
     */
    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    fun checkPasswordFirebase(password: String) {
        if (!isValid()) {
            return
        }
        val user = FirebaseAuth.getInstance().currentUser
        val email: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")

        showProgressBar(getString(R.string.password_verify))
        val credential = email?.let {
            EmailAuthProvider
                .getCredential(it, password)
        }

        // Prompt the user to re-provide their sign-in credentials
        if (credential != null) {
            user!!.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkPasswordApi(password)
                    } else {
                        dismissProgressBar()
                        CustomSnackBar.make(dialogView,
                            this,
                            CustomSnackBar.WARNING,
                            getString(R.string.authentication_failed_password),
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()

                    }
                }
        }
    }

    /**
     * Checking the password API call with password as param
     * @param password
     */
    private fun checkPasswordApi(password: String) {
        if (!isValid()) {
            return
        }
        val token: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.TOKEN,
            "")
        val strEmail: String?=
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        viewModel.checkPassword(strEmail, password, token).observe(this) { commonResponse ->
            dismissProgressBar()

            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                val auth = "Authentication Permission Granted"
                CustomSnackBar.make(dialogView,
                    this,
                    CustomSnackBar.SUCCESS,
                    auth,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()

                handledisable()
                //Do nothing
            } else {

                val errMsg: String?= ErrorMessages().getErrorMessage(this,
                    commonResponse.getErrorMessage(),
                    Constants.API.getDocBoxPatientList)

                if (commonResponse.getErrorId() === 0) {
                    CustomSnackBar.make(dialogView,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0).show()

                } else if (commonResponse.getErrorId() === 106) {
                    //finger print
                    val errMsg1 = getString(R.string.temporarily_locked)
                    CustomSnackBar.make(dialogView,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()

                    PrefUtility().saveBooleanInPref(this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false)
                    PrefUtility().saveStringInPref(this, Constants.SharedPrefConstants.PASSWORD, "")
                    alertDialog!!.dismiss()

                    val intent =
                        Intent(this, NotificationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    intent.putExtra("remoteTitle", getString(R.string.user_locked_title))
                    intent.putExtra("remoteMessage", commonResponse.getErrorMessage())
                    intent.putExtra("messageType", Constants.FCMMessageType.TEMP_LOCK)
                    startActivity(intent)
                } else {
                    CustomSnackBar.make(dialogView,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()

                }
            }
        }
    }


    // Showing loader for this activity
    fun checkPasswordApifingerprint(password: String?) {
        val token: String?= PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.TOKEN,
            "")
        val strEmail: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        viewModel.checkPassword(strEmail, password, token).observe(this) { commonResponse ->
            dismissProgressBar()

            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                EncUtil().generateKey(this)
                val encryptpassword: String? =
                    EncUtil().encrypt(this, password)
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.PASSWORD,
                    encryptpassword)
                PrefUtility().saveBooleanInPref(this,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    true)
                val auth = "Authentication Permission Granted"
                CustomSnackBar.make(dialogView,
                    this,
                    CustomSnackBar.SUCCESS,
                    auth,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
                handledisable()
                //Do nothing

            } else {

                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    commonResponse.getErrorMessage(),
                    Constants.API.getDocBoxPatientList)

                if (commonResponse.getErrorId() === 0) {
                    CustomSnackBar.make(dialogView,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                } else if (commonResponse.getErrorId() === 106) {
                    //finger print
                    val errMsg1 = getString(R.string.temporarily_locked)
                    CustomSnackBar.make(dialogView,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                    PrefUtility().saveBooleanInPref(this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false)
                    PrefUtility().saveStringInPref(this, Constants.SharedPrefConstants.PASSWORD, "")
                    alertDialog!!.dismiss()

                    val intent =
                        Intent(this, NotificationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    intent.putExtra("remoteTitle", getString(R.string.user_locked_title))
                    intent.putExtra("remoteMessage", commonResponse.getErrorMessage())
                    intent.putExtra("messageType", Constants.FCMMessageType.TEMP_LOCK)
                    startActivity(intent)
                } else {
                    CustomSnackBar.make(dialogView,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()

                }


            }
        }
    }

    var progressDialog: CustomProgressDialog? = null

   fun showProgressBar(string: String) {
        dismissProgressBar()
        try {
            progressDialog = CustomProgressDialog(this)
            progressDialog!!.setCancelable(false)
            var isDestyoed = false
            isDestyoed = isDestroyed
            if (!isFinishing && !isDestyoed) {
                if (progressDialog != null && !progressDialog!!.isShowing()) {
                    progressDialog!!.show()
                }
            }
        } catch (e: Exception) {

        }
    }

    /**
     * Password validation done here
     * @return the flag as true / false based on the input password string
     */
    private fun isValid(): Boolean {
        val errMsg = getString(R.string.password_empty)
        if (TextUtils.isEmpty(editTextPassword!!.text.toString())) {

            CustomSnackBar.make(dialogView,
                this,
                CustomSnackBar.WARNING,
                errMsg,
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return false
        }
        return true
    }

    override fun onDestroy() {
        alertDialog!!.dismiss()
        super.onDestroy()
    }
}