package com.example.kotlinomnicure.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.Typeface
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.*
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricConstants.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.biometric.BiometricManager.from

import com.example.kotlinomnicure.OmnicureApp
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityLoginBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.viewmodel.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging

import com.google.gson.Gson
import omnicurekotlin.example.com.loginEndpoints.model.CommonResponse
import omnicurekotlin.example.com.loginEndpoints.model.Provider
import java.security.KeyStore
import java.util.*
import java.util.concurrent.Executor
import javax.crypto.Cipher

class LoginActivity : BaseActivity(), View.OnClickListener {
    private val TAG = LoginActivity::class.java.simpleName
    var signInApiFlag = false
    private var binding: ActivityLoginBinding? = null
    private var viewModel: LoginViewModel? = null

    // Firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null
    private var fcmToken: String? = null
    private var strFeedbackForm = ""
   var versions = ""

    //fingerprint
    private val keyStore: KeyStore? = null
    private val cipher: Cipher? = null
    private val KEY_NAME = "AndroidKey"
    var dialogView: View? = null
    var alertDialog: AlertDialog? = null

    //fp
    var biometricPrompt: BiometricPrompt? = null
    var i = 0
    var fingerprintdata = "no"
    var context:Activity =LoginActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setView()
        setOnclickListener()
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            versions = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "exception", e.cause)
        }

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance()
        fcmToken = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FCM_TOKEN, "")
        if (TextUtils.isEmpty(fcmToken)) {
            FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener { instanceIdResult ->
                    fcmToken = instanceIdResult.getToken()
                    Log.d("Login Activity", "fcmToken Login$fcmToken")
                    PrefUtility().saveStringInPref(this@LoginActivity,
                        Constants.SharedPrefConstants.FCM_TOKEN, fcmToken)
                }
        }
        checkButton(true)
    }

    override fun onResume() {
        super.onResume()
        // To get the AES key anytime by calling the version Info APi
//        getAppConfig();
        // To get the AES key anytime by calling the version Info API
        val aesKey: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.AES_API_KEY, "")
        Log.e(TAG, "onResume: aesKey$aesKey")
        if (aesKey == null || TextUtils.isEmpty(aesKey)) {
            getAppConfig()
        }
    }

    /**
     * Get version info API call
     */
    private fun getAppConfig() {
        if (!UtilityMethods().isInternetConnected(this)!!) {
            return
        }
        viewModel?.getVersionInfo()?.observe(this) { versionInfoResponse ->
            Log.i(TAG, "getversioninfo response $versionInfoResponse")
            if (versionInfoResponse != null && versionInfoResponse.status != null && versionInfoResponse.status!!) {
                onSuccessVersionInfoAPI(versionInfoResponse)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    versionInfoResponse?.errorMessage, Constants.API.getVersionInfo)
                Log.i(TAG, "getAppConfig Error : $errMsg")
            }
        }
    }

    private fun onSuccessVersionInfoAPI(response: VersionInfoResponse) {
        Log.i(TAG, "onSuccessVersionInfoAPI: Response " + Gson().toJson(response.getAppConfig()))
        if (response.getAppConfig() != null) {

            if (response.getAppConfig().getLogoutServerTimerinMilli() != null) {

                val serverTimer: String = response.getAppConfig().getLogoutServerTimerinMilli()
                Log.i(TAG, "Auto Logout Server Time : $serverTimer")
                PrefUtility().saveLongInPref(this,
                    Constants.SharedPrefConstants.AUTO_LOGOUT_TIME, serverTimer.toLong())
            }

            if (response.getAppConfig().getLogoutAppTimerinMilli() != null) {

                val appTimer: String = response.getAppConfig().getLogoutAppTimerinMilli()
                Log.i(TAG, "Health monitoring timer : $appTimer")
                PrefUtility().saveLongInPref(this,
                    Constants.SharedPrefConstants.HEALTH_MONITOR_TIMER, appTimer.toLong())
            }
            if (response.getAesKey() != null) {
                val aesKey: String = response.getAesKey()
                Log.i(TAG, "Health monitoring aesKey : $aesKey")
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.AES_API_KEY, aesKey)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnclickListener() {
        binding?.signinBtn?.setOnClickListener(this)
        binding?.signinAuth?.setOnClickListener(this)
        binding?.idForgotPassword?.setOnClickListener(this)
        binding?.idSignupText?.setOnClickListener(this)
        binding?.editTextPassword?.setTypeface(Typeface.DEFAULT)
        binding?.passwordInfo?.setOnClickListener(View.OnClickListener {
            ValidationUtil().showPasswordValidationPopup(this@LoginActivity,
                binding?.editTextPassword?.text.toString())
        })
        binding?.passwordVisibility?.setOnClickListener(View.OnClickListener {
            binding?.editTextPassword?.let { it1 ->
                ValidationUtil().passwordVisibility(it1, binding?.passwordVisibility!!) } })

        binding?.editTextPassword?.setOnEditorActionListener(OnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                signInFirebase()
                return@OnEditorActionListener true
            }
            false
        })

    }

    fun checkEmail(showError: Boolean) {
        val isValidPhone: Boolean =
            ValidationUtil().isValidPhone(binding?.editTextUserId?.text.toString())
        val validEmail = ValidationUtil().checkEmail(
            binding?.editTextUserId?.text.toString()
        ) == true || TextUtils.isDigitsOnly(binding?.editTextUserId?.text.toString()) && isValidPhone
        if (!validEmail) {
            if (showError) {
                binding?.editTextUserId?.setErrorMessage(getString(R.string.email_phone_invalid))
            }
            binding?.editTextUserId?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            binding?.editTextUserId?.setErrorMessage("")
            binding?.editTextUserId?.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.ic_checkmark_edittext, 0)

        }
    }

    fun checkPassword(showError: Boolean) {
        if (ValidationUtil().checkPassword(binding?.editTextPassword?.text.toString(), binding) != null
        ) {
            if (showError) {
                binding?.editTextPassword?.setErrorMessage(
                    ValidationUtil().checkPassword(binding?.editTextPassword?.text.toString(), binding))
                binding?.passwordLayout?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                binding?.passwordInfo?.visibility = View.VISIBLE
            }
            binding?.passwordVerified?.visibility = View.GONE
            stopAuth()
        } else {
            binding?.editTextPassword?.setErrorMessage("")
            binding?.passwordLayout?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
            binding?.passwordInfo?.visibility = View.GONE
            binding?.passwordVerified?.visibility = View.VISIBLE
            //                        binding.idPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0);
        }
        stopAuth()
    }

    fun checkPasswordforpopup(showError: Boolean) {
        if (showError) {
            binding?.editTextPassword?.setErrorMessage(
                ValidationUtil().checkPassword(binding?.editTextPassword?.text.toString(), binding))
            binding?.passwordLayout?.background = resources.getDrawable(R.drawable.error_edittext_bg)
            binding?.passwordInfo?.visibility = View.VISIBLE
        }
        binding?.passwordVerified?.visibility = View.GONE
    }

    fun checkButton(statusofcheck: Boolean) {
        val isValidPhone: Boolean =
            ValidationUtil().isValidPhone(binding?.editTextUserId?.text.toString())
        val validEmail = ValidationUtil().checkEmail(
            binding?.editTextUserId?.text.toString()
        ) == true || TextUtils.isDigitsOnly(binding?.editTextUserId?.text.toString()) && isValidPhone
        val validPass: Boolean =
            ValidationUtil().checkPassword(binding?.editTextPassword?.text.toString()) == true
        if (validEmail && validPass) {
            binding?.signinBtn?.isEnabled = true
            if (fingerprintenableordisable()) {
                if (fingerprintlockemailchange()) {
                } else {
                    PrefUtility().saveBooleanInPref(this@LoginActivity,
                        Constants.SharedPrefConstants.LOCKFPemailchange, true)
                }
            } else {
                fingerprintlock5times()
                val LOCKFPemailchange: Boolean = PrefUtility().getBooleanInPref(this,
                    Constants.SharedPrefConstants.LOCKFPemailchange, false)
                if (LOCKFPemailchange) {
                    stopAuth()
                    PrefUtility().saveBooleanInPref(this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                    val passworddata: String? = PrefUtility().getStringInPref(this,
                        Constants.SharedPrefConstants.PASSWORD, "")
                    PrefUtility().saveStringInPref(this,
                        Constants.SharedPrefConstants.DUMMYPASSWORD, passworddata)
                    PrefUtility().saveStringInPref(this,
                        Constants.SharedPrefConstants.PASSWORD, "")
                    if (fingerprintlockemailchange()) {
                        val mypass: String? = PrefUtility().getStringInPref(this,
                            Constants.SharedPrefConstants.DUMMYPASSWORD, "")
                        PrefUtility().saveStringInPref(
                            this,
                            Constants.SharedPrefConstants.PASSWORD,
                            mypass
                        )
                        PrefUtility().saveStringInPref(
                            this,
                            Constants.SharedPrefConstants.DUMMYPASSWORD,
                            ""
                        )
                        PrefUtility().saveBooleanInPref(
                            this,
                            Constants.SharedPrefConstants.FINGERPRINTFLAG,
                            true
                        )
                    } else {
                    }
                } else {
                    fingerprintlock5times()
                }
            }
        } else {
            binding?.signinBtn?.isEnabled = false
            binding?.signinAuth?.isEnabled = false
            if (statusofcheck) {
                if (fingerprintenableordisable()) {
                    binding?.signinAuth?.isEnabled = true
                } else {
                    binding?.signinAuth?.isEnabled = false
                }
            } else {
                if (validEmail && validPass) {
                } else {
                    binding?.signinBtn?.isEnabled = false
                    val email: String? = PrefUtility().getStringInPref(
                        this,
                        Constants.SharedPrefConstants.EMAIL,
                        ""
                    )
                    val LOCKFP: Boolean = PrefUtility().getBooleanInPref(
                        this,
                        Constants.SharedPrefConstants.LOCKFP,
                        false
                    )
                    val finerprintstate: Boolean = PrefUtility().getBooleanInPref(
                        this,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false
                    )
                    var decryptpassword = ""
                    if (finerprintstate) {
                        val password: String? = PrefUtility().getStringInPref(
                            this,
                            Constants.SharedPrefConstants.PASSWORD,
                            ""
                        )
                        decryptpassword = EncUtil().decrypt(this, password).toString()
                    }
                    if (finerprintstate) {


                        if (validEmail) {

                            if (binding?.editTextPassword?.text.toString().trim().length > 0
                                && decryptpassword != binding?.editTextPassword?.text.toString()) {
                                binding?.signinAuth?.isEnabled = false
                            } else if (email == binding?.editTextUserId?.text.toString()) {
                                if (LOCKFP) {
                                    binding?.signinAuth?.isEnabled = false
                                } else {
                                    binding?.signinAuth?.isEnabled = true
                                }
                            } else {
                                if (email == "") {
                                    if (LOCKFP) {
                                        binding?.signinAuth?.isEnabled = false
                                    } else {
                                        binding?.signinAuth?.isEnabled = true
                                    }
                                } else {
                                    binding?.signinAuth?.isEnabled = false
                                    stopAuth()

                                }
                            }
                        } else {
                            binding?.signinAuth?.isEnabled = false
                        }
                    } else {
                        binding?.signinAuth?.isEnabled = false
                    }
                }
            }
        }
    }


    fun fingerprintpasswordcheck(): Boolean {
        val finerprintstate1: Boolean = PrefUtility().getBooleanInPref(
            this,
            Constants.SharedPrefConstants.FINGERPRINTFLAG,
            false
        )
        var decryptpassword1 = ""
        if (finerprintstate1) {
            val password1: String? = PrefUtility().getStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD, "")
            decryptpassword1 = EncUtil().decrypt(this, password1).toString()
        }
        return if (binding?.editTextPassword?.text.toString().trim()
                .length > 0 && decryptpassword1 != binding?.editTextPassword?.text.toString()
        ) {
            binding?.signinAuth?.isEnabled = true
            true
        } else {
            if (decryptpassword1 == "") {
                binding?.signinAuth?.isEnabled = true
                true
            } else {
                binding?.signinAuth?.isEnabled = false
                false
            }
        }
    }

    fun fingerprintenableordisable(): Boolean {
        val finerprintstate: Boolean = PrefUtility().getBooleanInPref(this,
            Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
        return if (finerprintstate) {
            binding?.signinAuth?.isEnabled = true
            true
        } else {
            binding?.signinAuth?.isEnabled = false
            false
        }
    }

    fun fingerprintlock5times(): Boolean {
        val LOCKFP: Boolean = PrefUtility().getBooleanInPref(this,
            Constants.SharedPrefConstants.LOCKFP, false)
        return if (LOCKFP) {
            binding?.signinAuth?.isEnabled = false
            false
        } else {
            binding?.signinAuth?.isEnabled = true
            true
        }
    }

    fun fingerprintlockemailchange(): Boolean {
        val email: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        val finerprintstate1: Boolean = PrefUtility().getBooleanInPref(this,
            Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
        var decryptpassword1 = getString(R.string.value_n)
        if (finerprintstate1) {
            val password1: String? = PrefUtility().getStringInPref(
                this,
                Constants.SharedPrefConstants.PASSWORD,
                "n"
            )
            decryptpassword1 = EncUtil().decrypt(this, password1).toString()
        }
        return if (email == binding?.editTextUserId?.text
                .toString() && decryptpassword1 == binding?.editTextPassword?.text.toString()
        ) {
            binding?.signinAuth?.isEnabled = true
            true
        } else {
            if (email == "") {
                binding?.signinAuth?.isEnabled = true
                true
            } else {
                binding?.signinAuth?.isEnabled = false
                false
            }
        }
    }

    fun belowversionV9() {
        binding?.signinAuth?.setVisibility(View.VISIBLE)
        val fingerprintManager2 = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
        if (fingerprintManager2 == null) {
            PrefUtility().saveBooleanInPref(
                this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false
            )
            PrefUtility().saveStringInPref(
                this,
                Constants.SharedPrefConstants.PASSWORD,
                ""
            )
            binding?.signinAuth?.visibility = View.GONE
            binding?.signinBtn?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        } else if (!fingerprintManager2.isHardwareDetected) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD, "")
            binding?.signinAuth?.visibility = View.GONE
            //   Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
            binding?.signinBtn?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        } else if (!fingerprintManager2.hasEnrolledFingerprints()) {
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
            PrefUtility().saveStringInPref(this,
                Constants.SharedPrefConstants.PASSWORD, "")
            // User hasn't enrolled any fingerprints to authenticate with
            binding?.signinAuth?.visibility = View.GONE
            binding?.signinBtn?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        } else {
            binding?.signinAuth?.visibility = View.VISIBLE
            checkfingerprint()
        }
    }

    private fun setView() {

        //appname
        binding?.idWelcomeText?.text =
            java.lang.String.format(getString(R.string.welcome_to_omnicurenow), Buildconfic().value())
        binding?.signinAuth?.let { handleMultipleClicknew(it) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //generateKey();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                checkfingerprintabove29()
            } else {
                belowversionV9()
            }
        }


//finerprint end
        binding?.editTextUserId?.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                binding?.editTextUserId?.setText(binding?.editTextUserId?.text.toString().trim())
                binding?.editTextUserId?.addTextChangedListener(GenericTextWatcher(binding?.editTextUserId!!))
                checkEmail(true)
            }
        }
        binding?.editTextPassword?.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {

                if (binding?.editTextPassword?.getErrorMessage().equals("")) {
                    binding?.passwordLayout?.background = resources.getDrawable(R.drawable.border_black_edittext_bg)
                }
            } else {
                binding?.passwordLayout?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
                binding?.editTextPassword?.addTextChangedListener(GenericTextWatcher(binding?.editTextPassword!!))
                checkPassword(true)
            }
        }
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding?.idSignupText?.text = Html.fromHtml(
            String.format(
                "Don\'t have an account?" + "<font color='#%s'><b>" + " SIGN UP</b></font>",
                сolorString
            )
        )
        //detectKeyboard();
        //PhoneNumberUtils.formatNumber("8377944971","US")
        if (intent.hasExtra(Constants.IntentKeyConstants.MOBILE_NO)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.MOBILE_NO))
        ) {
            var mobileNo = intent.getStringExtra(Constants.IntentKeyConstants.MOBILE_NO)
            if (mobileNo!!.length == 10) {
                mobileNo = mobileNo.substring(0, 3) + "-" + mobileNo.substring(
                    3,
                    6
                ) + "-" + mobileNo.substring(6)
            }
            //            binding.editTextUserId.setText(mobileNo);
        }
        binding?.editTextUserId?.addTextChangedListener(ValidationTextWatcher(binding!!.editTextUserId))
        binding?.editTextPassword?.addTextChangedListener(ValidationTextWatcher(binding!!.editTextPassword))
    }

    @SuppressLint("WrongConstant")
    fun checkfingerprintabove29() {
        val biometricManager: androidx.biometric.BiometricManager = from(this)
        binding?.signinAuth?.visibility = View.VISIBLE
        if (biometricManager.canAuthenticate() === BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            PrefUtility().saveBooleanInPref(
                this@LoginActivity,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false
            )
            PrefUtility().saveStringInPref(
                this@LoginActivity,
                Constants.SharedPrefConstants.PASSWORD,
                ""
            )
            binding?.signinAuth?.visibility = View.GONE
            binding?.signinBtn?.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        } else if (biometricManager.canAuthenticate() === BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            PrefUtility().saveBooleanInPref(
                this@LoginActivity,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false
            )
            PrefUtility().saveStringInPref(
                this@LoginActivity,
                Constants.SharedPrefConstants.PASSWORD,
                ""
            )
            binding?.signinAuth?.visibility = View.GONE
            binding?.signinBtn?.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        } else if (biometricManager.canAuthenticate() === BiometricManager.BIOMETRIC_SUCCESS) {
            binding?.signinAuth?.visibility = View.VISIBLE
            checkfingerprint()
        }
    }

    fun checkfingerprintapi29(): Boolean {
        val pm = packageManager
        val hasFingerprint = pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        val hasFace = pm.hasSystemFeature(PackageManager.FEATURE_FACE)
        val hasIris = pm.hasSystemFeature(PackageManager.FEATURE_IRIS)
        return hasFingerprint
    }

    fun stopAuth() {
        //if (biometricPrompt != null) biometricPrompt?.cancelAuthentication()
    }

   /* fun biometric(status: Boolean, normal: String) {

                }

                fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (normal == "normal") {
                        val email: String? = PrefUtility().getStringInPref(context,
                            Constants.SharedPrefConstants.EMAIL, "")
                        val password: String? = PrefUtility().getStringInPref(context,
                            Constants.SharedPrefConstants.PASSWORD, "")
                        if (email == "" || password == "") {
                        } else {
                            EncUtil().generateKey(context)
                            val decryptpassword: String? =
                                EncUtil().decrypt(context, password)
                            binding?.editTextPassword?.setText(decryptpassword)
                            checkEmail(true)
                            onClickSignIn()
                        }
                    } else {
                        if (status) {
                            val email2: String? = PrefUtility().getStringInPref(context,
                                Constants.SharedPrefConstants.EMAIL, "")
                            val password2: String? = PrefUtility().getStringInPref(context,
                                Constants.SharedPrefConstants.PASSWORD, "")
                            if (email2 == "" || password2 == "") {
                            } else {
                                EncUtil().generateKey(context)
                                val decryptpassword: String? =
                                    EncUtil().decrypt(context, password2)
                                binding?.editTextPassword?.setText(decryptpassword)
                                fingerprintdata = "yes"
                                checkEmail(true)
                                onClickSignIn()
                            }
                        } else {
                            val email1: String = binding?.editTextUserId?.text.toString()
                            val password1: String = binding?.editTextPassword?.text.toString()
                            if (email1 == "" || password1 == "") {
                            } else {
                                fingerprintdata = "yes"
                                checkEmail(true)
                                onClickSignIn()
                            }
                        }
                    }
                    super.onAuthenticationSucceeded(result)

                }

                fun onAuthenticationFailed() {
                    val finerprintstate: Boolean = PrefUtility().getBooleanInPref(context,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                    if (finerprintstate) {
                        CustomSnackBar.make(binding?.rootLayout,
                            context, CustomSnackBar.WARNING,
                            getString(R.string.Authfaild), CustomSnackBar.TOP, 3000, 0)?.show()


                    }
                    super.onAuthenticationFailed()

                }*/




    private fun getMainThreadExecutor(): Executor? {
        return MainThreadExecutor()
    }

    private class MainThreadExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(r: Runnable) {
            handler.post(r)
        }
    }

    private fun handleMultipleClicknew(item: View) {
        val multipleclieck: Boolean = PrefUtility().getBooleanInPref(this,
            Constants.SharedPrefConstants.MUTILPLECLICK, false)
        if (multipleclieck) {
            // item.setEnabled(false);
            CustomSnackBar.make(binding?.rootLayout, this,
                CustomSnackBar.WARNING, getString(R.string.Authfaildnew),
                CustomSnackBar.TOP, 3000, 0)?.show()

            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.LOCKFP, true)
            PrefUtility().saveBooleanInPref(this,
                Constants.SharedPrefConstants.MUTILPLECLICK, false)
        } else {
        }
    }

    fun checkfingerprint() {
        val email: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        val password: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.PASSWORD, "")
        val finerprintstate: Boolean = PrefUtility().getBooleanInPref(
            this,
            Constants.SharedPrefConstants.FINGERPRINTFLAG,
            false
        )
        if (finerprintstate) {
            if (password == "") {
                binding?.signinAuth?.isEnabled = false
            } else {
                binding?.editTextUserId?.setText(email)
                checkEmail(true)
                binding?.signinAuth?.isEnabled = true
                //biometric(false, "normal")
            }
        } else {
            binding?.signinAuth?.isEnabled = false
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.signin_btn -> {
                stopAuth()
                onClickSignIn()
            }
            R.id.signin_auth -> {
                val FINGERPRINTFLAG: Boolean = PrefUtility().getBooleanInPref(
                    this,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    false
                )
                if (FINGERPRINTFLAG) {
                    if (binding?.editTextPassword?.text.toString().length > 0) {
                        checkPasswordforpopup(true)
                    } else {
                        //biometric(true, "n")
                    }
                } else {
                    checkPasswordforpopup(true)
                }
            }
            R.id.id_forgot_password -> {
                onClickForgotPassword()
            }
            R.id.id_signup_text -> {
                onClickSignUp()
            }
        }
    }


    private fun onClickSignIn() {
        binding?.signinBtn?.let { handleMultipleClick(it) }
        if (!isValid()) {
            return
        }
        if (signInApiFlag) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(binding?.rootLayout, this,
                CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.signin.toString()))
        val userIdStr: String = binding?.editTextUserId?.text.toString()
        //        String userIdStr = userIdText.replaceAll("\\+", "").replaceAll("-", "");
        if (TextUtils.isDigitsOnly(userIdStr)) {
            mobileFlag = true
        } else {
            mobileFlag = false
            emailText = userIdStr
        }
        println("mobileflag $mobileFlag")
        if (mobileFlag) {
            getEmail(userIdStr)
        } else {
            signInFirebase()
        }
    }

    private fun loginApi(overrideFlag: Boolean) {
        val password: String = binding?.editTextPassword?.text.toString()
        val email = emailText
        signInApiFlag = true
        Log.d(TAG, "loginApi: $password")
        showProgressBar()
        fcmToken?.let {
            viewModel?.login(email.lowercase(Locale.getDefault()), password, it, overrideFlag, version)
                ?.observe(this,
                    Observer<CommonResponse?> { commonResponse ->
                        dismissProgressBar()
                        Log.i(TAG, "Login response " + Gson().toJson(commonResponse))
                        signInApiFlag = false
                        if (commonResponse != null && commonResponse.status != null && commonResponse.status!!) {
                            PrefUtility().clearRedirectValidation(this)
                            if (commonResponse.feedbackForm != null) {
                                val encryptionKey1: String? = commonResponse.encryptionKey
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.ENCRYPTIONKEY,
                                    encryptionKey1
                                )
                                strFeedbackForm = commonResponse.feedbackForm!!
                                //fp
                                val LOCKFP: Boolean = PrefUtility().getBooleanInPref(
                                    this,
                                    Constants.SharedPrefConstants.LOCKFP,
                                    false
                                )
                                if (LOCKFP) {
                                    PrefUtility().saveBooleanInPref(
                                        this,
                                        Constants.SharedPrefConstants.LOCKFP,
                                        false
                                    )

                                    val encryptionKey: String? = commonResponse.encryptionKey
                                    PrefUtility().saveStringInPref(
                                        this,
                                        Constants.SharedPrefConstants.ENCRYPTIONKEY,
                                        encryptionKey
                                    )
                                }
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.FEEDBACK_URL,
                                    commonResponse.feedbackForm
                                )
                            }
                            if (commonResponse.tutorial_url != null) {
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.TUTORIAL_URL,
                                    commonResponse.tutorial_url
                                )
                            }
                            if (commonResponse.aesEncryptionKey != null) {
                                EncUtil().generateKey(this)
                                val encyptKey: String? = EncUtil().encrypt(this,
                                    commonResponse.aesEncryptionKey
                                )
                                PrefUtility().saveStringInPref(this,
                                    Constants.SharedPrefConstants.AES_KEY, encyptKey)
                            }
                            if (commonResponse.agoraAppCertificate!= null) {
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.AGORA_CERTIFICATE,
                                    commonResponse.agoraAppCertificate
                                )
                            }
                            if (commonResponse.agoraAppId != null) {
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.AGORA_APP_ID,
                                    commonResponse.agoraAppId
                                )
                            }
                            if (commonResponse.idToken!= null) {
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.FIREBASE_IDTOKEN,
                                    commonResponse.idToken
                                )
                            }
                            if (commonResponse.refreshToken != null) {
                                PrefUtility().saveStringInPref(
                                    this,
                                    Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                                    commonResponse.refreshToken
                                )
                            }
                            if (commonResponse.provider?.role
                                    .equals(Constants.ProviderRole.RD.toString(),ignoreCase = true)
                            ) {
                                val topic: String? = UtilityMethods().getFCMTopic()
                                if (topic != null) {
                                    FirebaseMessaging.getInstance().subscribeToTopic(topic)
                                        .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                            if (task.isSuccessful) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "topic subscribe:success")
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "topic subscribe:failure", task.exception)
                                                Toast.makeText(
                                                    this,
                                                    getString(R.string.authentication_failed),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        })
                                }
                            }
                            //encrypt
                            EncUtil().generateKey(this)
                            val encyuptpassword: String? = EncUtil().encrypt(this, password)
                            commonResponse.provider?.setPassword(encyuptpassword)
                            commonResponse.provider?.let { it1 -> onLoginSuccess(it1) }
                        } else {
                            var errorId = 0
                            if (commonResponse.errorId != null) {
                                errorId = commonResponse.errorId!!
                                if (errorId != 150) {
                                    binding?.editTextPassword?.setText("")
                                }
                            }
                            if (commonResponse.provider!= null) {
                                commonResponse.errorMessage?.let { it1 ->
                                    redirectPage(
                                        commonResponse.provider!!,
                                        errorId,
                                        it1
                                    )
                                }
                            }
                            if (commonResponse.errorMessage!= null && commonResponse.errorMessage
                                    .equals("redirect",ignoreCase = true)
                            ) {
                                val intent =
                                    Intent(this, RegistrationSuccessActivity::class.java)
                                intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
                                startActivity(intent)
                                return@Observer
                            }
                            if (commonResponse.errorMessage != null && commonResponse.errorMessage
                                    .equals("unauthorized",ignoreCase = true)
                            ) {
                                val intent =
                                    Intent(this, RegistrationSuccessActivity::class.java)
                                intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
                                startActivity(intent)
                                return@Observer
                            }
                            val errMsg1 = getString(R.string.temporarily_locked)
                            if (commonResponse.errorMessage!= null && commonResponse.errorMessage
                                    .equals(errMsg1)) {

                                binding?.editTextPassword?.setText("")
                                binding?.editTextUserId?.setText("")
                                if (alertDialog != null) {
                                    alertDialog!!.dismiss()
                                }
                            }
                            val errMsg2 = getString(R.string.invalid_creds)
                            if (commonResponse.errorMessage != null && commonResponse.errorMessage.equals(errMsg2)) {
                                binding?.editTextPassword?.setText("")
                                if (alertDialog != null) {
                                    alertDialog!!.dismiss()
                                }
                            }
                            if (errorId == 150) {
                                alreadyLoggedInPopup()
                            } else if (errorId == 103) {
                                val intent =
                                    Intent(this, RegistrationSuccessActivity::class.java)
                                intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
                                startActivity(intent)
                            } else {
                                if (errorId != 110) {
                                    val errMsg: String? = ErrorMessages().getErrorMessage(
                                        this,
                                        commonResponse.errorMessage,
                                        Constants.API.signin
                                    )

                                    errMsg?.let {
                                        CustomSnackBar.make(
                                            binding?.rootLayout,
                                            this,
                                            CustomSnackBar.WARNING,
                                            it,
                                            CustomSnackBar.TOP,
                                            3000,
                                            0
                                        )?.show()
                                    }
                                }
                            }
                        }
                    })
        }
    }

    private fun loginFailedApi() {
        val email = emailText
        val password: String = binding?.editTextPassword?.text.toString()
        viewModel?.loginFailed(email.lowercase(Locale.getDefault()), password)?.observe(this) { commonResponse ->
            dismissProgressBar()
            if (commonResponse != null && commonResponse.status != null && commonResponse.status!!) {
            } else {
                val errMsg: String? = commonResponse?.errorMessage
                if (showError) {
                    CustomSnackBar.make(
                        binding?.rootLayout,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
                //fp login
                val errMsg1 = getString(R.string.temporarily_locked)
                if (errMsg == errMsg1) {

                    binding?.editTextPassword?.setText("")
                    binding?.editTextUserId?.setText("")
                    if (alertDialog != null) {
                        alertDialog!!.dismiss()
                    }
                } else {
                    val errMsg2 = getString(R.string.invalid_creds)
                    if (errMsg == errMsg2) {
                        binding?.editTextPassword?.setText("")
                        if (alertDialog != null) {
                            alertDialog!!.dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun getEmail(phone: String) {
        viewModel?.getEmail(phone)?.observe(this) { commonResponse ->
            println("getEmailResponse $commonResponse")
            if (commonResponse?.status != null && commonResponse.status!!) {
                emailText = commonResponse.email.toString()
                signInFirebase()
            } else {
                dismissProgressBar()
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    commonResponse?.errorMessage,
                    Constants.API.updateProvider
                )
                CustomSnackBar.make(
                    binding?.rootLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
            }
        }
    }

    private fun redirectPage(provider: Provider, errorId: Int, errMsg: String) {
        if (errorId == 101) {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.id)
            intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO, provider.phone)
            intent.putExtra(Constants.IntentKeyConstants.COUNTRY_CODE, provider.countryCode)
            intent.putExtra(Constants.IntentKeyConstants.FROM_PAGE, "login")
            startActivity(intent)
        } else if (errorId == 102) {
            val intent = Intent(this, EmailOTPActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_EMAIL, provider.email)
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.id)
            intent.putExtra(Constants.IntentKeyConstants.FROM_PAGE, "login")
            startActivity(intent)
        } else if (errorId == 103) {
            val intent = Intent(this, RegistrationSuccessActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
            startActivity(intent)
        } else if (errorId == 110) {
            expirePasswordPopup(
                errMsg,
                provider.email,
                provider.fname,
                provider.lname,
                provider.token)
        }
    }

    private fun onClickForgotPassword() {
        binding?.idForgotPassword?.let { handleMultipleClick(it) }
        Log.i(TAG, "onClickForgotPassword: ")
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun onClickSignUp() {
        Log.i(TAG, "onClickSignUp: ")
        binding?.idSignupText?.let { handleMultipleClick(it) }
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun onLoginSuccess(provider: Provider) {
        (applicationContext as OmnicureApp).createRtcEngine()
        PrefUtility().setLoginTime(this, Date().time)
        if (fingerprintdata == "yes") {
            PrefUtility().saveBooleanInPref(this, Constants.SharedPrefConstants.FINGERPRINTFLAG, true)
        } else {
        }
        val email: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        if (email == binding?.editTextUserId?.text.toString()) {
        } else {
            if (email == "") {
            } else {
                PrefUtility().saveBooleanInPref(
                    this,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    false
                )
                PrefUtility().saveStringInPref(
                    this,
                    Constants.SharedPrefConstants.DUMMYPASSWORD,
                    ""
                )
                PrefUtility().saveStringInPref(
                    this,
                    Constants.SharedPrefConstants.PASSWORD,
                    ""
                )
            }
        }
        PrefUtility().saveLongInPref(
            this,
            Constants.SharedPrefConstants.APP_ACTIVE_TIME,
            System.currentTimeMillis()
        )
        PrefUtility().saveUserData(this, provider)
        Log.d(TAG, "onLoginSuccess: " + provider.userId)
        val intent = Intent(this, MyDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
//        signUpFirebase(provider.getPhone());
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        return errMsg?.isEmpty()!!
    }

    fun expirePasswordPopup(
        errMsg: String?,
        email: String?,
        strFirstName: String?,
        strLastName: String?,
        token: String?,
    ) {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.custom_alert_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.text = getString(R.string.password_expired_title)
        alertMsg.text = errMsg
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        buttonOk.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this, ExpirePasswordActivity::class.java)
            intent.putExtra("rbEmailAddress", "Email")
            intent.putExtra(Constants.IntentKeyConstants.EMAIL, email)
            intent.putExtra(Constants.IntentKeyConstants.FIRST_NAME, strFirstName)
            intent.putExtra(Constants.IntentKeyConstants.LAST_NAME, strLastName)
            intent.putExtra(Constants.IntentKeyConstants.OTP_TOKEN, token)
            startActivity(intent)
        }
        alertDialog.show()
    }

    private fun alreadyLoggedInPopup() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.alert_custom_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.text = getString(R.string.active_session)
        alertMsg.text = getString(R.string.active_session_content)
        val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
        val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
        buttonYes.text = getString(R.string.txt_continue)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        buttonYes.setOnClickListener {
            val finerprintstate: Boolean = PrefUtility().getBooleanInPref(
                this,
                Constants.SharedPrefConstants.FINGERPRINTFLAG,
                false
            )
            loginApi(true)
            alertDialog.dismiss()
        }
        buttonNo.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun detectKeyboard() {
        binding?.rootLayout?.viewTreeObserver?.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                var isKeyboardShowing = false
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding!!.rootLayout.getWindowVisibleDisplayFrame(r)
                    val screenHeight: Int = binding!!.rootLayout.rootView.height

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    val keypadHeight = screenHeight - r.bottom
                    Log.d(TAG, "keypadHeight = $keypadHeight")
                    if (keypadHeight > screenHeight * 0.15) {
                        // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true
                            onKeyboardVisibilityChanged(true)
                        }
                    } else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false
                            onKeyboardVisibilityChanged(false)
                        }
                    }
                }
            })
    }

    private fun onKeyboardVisibilityChanged(isShowing: Boolean) {
        if (isShowing) {
            binding?.idSignupText?.visibility = View.GONE
        } else {
            mHandler?.postDelayed(Runnable {
                binding?.idSignupText?.visibility = View.VISIBLE
            }, 100)
        }
    }

    var mobileFlag = false
    var emailText = ""
    var showError = true

    fun signInFirebase() {
        val email = emailText
        val pwd: String = binding?.editTextPassword?.text.toString()
        mFirebaseAuth!!.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(
            this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                val user1 = mFirebaseAuth!!.currentUser
                println("user_details " + user1!!.uid)
                PrefUtility().saveStringInPref(
                    this,
                    Constants.SharedPrefConstants.FIREBASE_UID,
                    user1.uid
                )
                user1.getIdToken(false).addOnSuccessListener { result ->
                    val idToken = result.token
                    if (task.isSuccessful) {
                        println("getToken $idToken")
                        PrefUtility().saveStringInPref(
                            this, Constants.SharedPrefConstants.FIREBASE_IDTOKEN, idToken)
                        loginApi(false)
                        // Send token to your backend via HTTPS
                        // ...
                    }
                    //Do whatever
                    Log.d(TAG, "GetTokenResult result = $idToken")
                }
            } else {

                Log.w(TAG, "signInWithEmail:taskUnSuccessfull" + task.exception)

            }

        }.addOnFailureListener { e ->
            binding?.editTextPassword?.setText("")
            var errMsg = ""

            if (e is FirebaseAuthInvalidUserException) {
                errMsg =
                    if (e.getLocalizedMessage() == "The user account has been disabled by an administrator.") {
                        "Provider Denied"
                    } else {
                        getString(R.string.user_not_exist)
                    }
                showError = false
                CustomSnackBar.make(
                    binding?.rootLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()

            } else if (e is FirebaseAuthInvalidCredentialsException) {
                errMsg = getString(R.string.invalid_creds)

            } else if (e is FirebaseTooManyRequestsException) {
                errMsg = getString(R.string.temporarily_locked)
            } else {
                errMsg = getString(R.string.user_not_exist)
                Log.e(TAG, e.message!!)
            }
            loginFailedApi()
            Log.w(TAG, "signInWithEmail:failure" + e.message + " " + e.javaClass.simpleName)
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHandler?.postDelayed(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            val checkPermissionResult = checkSelfPermissionsMediaCheck()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // so far we do not use OnRequestPermissionsResultCallback
            }
        }, 500)
    }

    override fun checkSelfPermissionsMediaCheck(): Boolean {
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


    override fun checkSelfPermissionGrantedCheck(permission: String, requestCode: Int): Boolean {
        Log.i("checkSelfPermission ", "$permission $requestCode")
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
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(
            "onRequestPermissions",
            requestCode.toString() + " " + Arrays.toString(permissions) + " " + Arrays.toString(
                grantResults
            )
        )
        when (requestCode) {
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO -> {

                checkSelfPermissionGrantedCheck(
                    Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA
                )
            }
            ConstantApp().PERMISSION_REQ_ID_CAMERA -> {


                checkSelfPermissionGrantedCheck(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
                )
            }
            ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE -> {
            }
        }
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun isNumberFormatRequired(str: String): Boolean {
        var s = str
        if (str.startsWith(Constants.US_COUNTRY_CODE.toString() + "-")) {
            s = str.replace(Constants.US_COUNTRY_CODE.toString() + "-", "")
        } else if (str.startsWith(Constants.US_COUNTRY_CODE)) {
            s = str.replace(Constants.US_COUNTRY_CODE, "")
        }
        return true
    }

    private class GenericTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.editTextUserId ->LoginActivity(). checkEmail(true)
                R.id.editTextPassword -> LoginActivity().checkPassword(true)
            }
        }

        init {
            this.view = view
        }
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
            val str = s.toString()
            if (view.id == R.id.editTextUserId) {
                if (str.length > 0 && str.contains(" ")) {
                  LoginActivity(). binding?.editTextUserId?.setText(s.toString().replace(" ", ""))
                    LoginActivity(). binding?.editTextUserId?.text?.let {
                        Selection.setSelection(
                            LoginActivity(). binding?.editTextUserId?.text, it.length
                        )
                    }
                }
            }
        }

        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.editTextUserId -> LoginActivity().checkEmail(false)
                R.id.editTextPassword -> LoginActivity().checkPassword(false)
            }
            LoginActivity().checkButton(false)
        }

        init {
            this.view = view
        }
    }



}
