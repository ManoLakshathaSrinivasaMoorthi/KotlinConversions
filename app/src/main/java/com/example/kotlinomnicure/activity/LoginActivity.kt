package com.example.kotlinomnicure.activity

import android.Manifest
import android.annotation.SuppressLint
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
import android.telephony.TelephonyManager.CellInfoCallback.ERROR_TIMEOUT
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
import android.webkit.WebViewClient.ERROR_TIMEOUT
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityLoginBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.viewmodel.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
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
    var version = ""

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setView()
        setOnclickListener()
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            version = packageInfo.versionName
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
        if (!UtilityMethods().isInternetConnected(this)) {
            return
        }
        viewModel?.getVersionInfo().observe(this) { versionInfoResponse ->
            Log.i(TAG, "getversioninfo response $versionInfoResponse")
            if (versionInfoResponse != null && versionInfoResponse.getStatus() != null && versionInfoResponse.getStatus()) {
                onSuccessVersionInfoAPI(versionInfoResponse)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this@LoginActivity,
                    versionInfoResponse.getErrorMessage(), Constants.API.getVersionInfo)
                Log.i(TAG, "getAppConfig Error : $errMsg")
            }
        }
    }

    private fun onSuccessVersionInfoAPI(response: VersionInfoResponse) {
        Log.i(TAG, "onSuccessVersionInfoAPI: Response " + Gson().toJson(response.getAppConfig()))
        if (response.getAppConfig() != null) {
//            if (response.getAppConfig().get(Constants.VersionInfoKeys.AUTO_LOGOUT_TIME) != null) {
            if (response.getAppConfig().getLogoutServerTimerinMilli() != null) {
//                String serverTimer = response.getAppConfig().get(Constants.VersionInfoKeys.AUTO_LOGOUT_TIME).toString();
                val serverTimer: String = response.getAppConfig().getLogoutServerTimerinMilli()
                Log.i(TAG, "Auto Logout Server Time : $serverTimer")
                PrefUtility().saveLongInPref(this,
                    Constants.SharedPrefConstants.AUTO_LOGOUT_TIME, serverTimer.toLong())
            }
            //            if (response.getAppConfig().get(Constants.VersionInfoKeys.HEALTH_MONITOR_TIMER) != null) {
            if (response.getAppConfig().getLogoutAppTimerinMilli() != null) {
//                String appTimer = response.getAppConfig().get(Constants.VersionInfoKeys.HEALTH_MONITOR_TIMER).toString();
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
                binding?.editTextPassword?.getText().toString())
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

        //+1-8377944971
/*        binding.editTextUserId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                Log.d(TAG, "onFocusChange: " + isFocus);
                String text = binding.editTextUserId.getText().toString().trim();
                if (!isFocus && !TextUtils.isEmpty(text)) {
                    if (text.startsWith(Constants.US_COUNTRY_CODE + "-") && text.length() == 13 && TextUtils.isDigitsOnly(text.substring(3))) {
                        text = text.substring(0, 6) + "-" + text.substring(6, 9) + "-" + text.substring(9);
                    } else if (text.startsWith(Constants.US_COUNTRY_CODE) && text.length() == 12 && TextUtils.isDigitsOnly(text.substring(2))) {
                        text = text.substring(0, 5) + "-" + text.substring(5, 8) + "-" + text.substring(8);
                    } else if (text.length() == 10 && TextUtils.isDigitsOnly(text)) {
                        text = text.substring(0, 3) + "-" + text.substring(3, 6) + "-" + text.substring(6);
                    }
                    binding.editTextUserId.setText(text);
                }
            }
        });*/
    }

    fun checkEmail(showError: Boolean) {
        val isValidPhone: Boolean =
            ValidationUtil().isValidPhone(binding?.editTextUserId?.getText().toString())
        val validEmail = ValidationUtil().checkEmail(
            binding?.editTextUserId?.getText().toString()
        ) == true || TextUtils.isDigitsOnly(binding?.editTextUserId?.getText().toString()) && isValidPhone
        if (!validEmail) {
            if (showError) {
                binding?.editTextUserId?.setErrorMessage(getString(R.string.email_phone_invalid))
            }
            binding?.editTextUserId?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            binding?.editTextUserId?.setErrorMessage("")
            binding?.editTextUserId?.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.ic_checkmark_edittext, 0)
            //    boolean finerprintstate = PrefUtility.getBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//            if(finerprintstate){
//                PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//                PrefUtility.saveStringInPref(LoginActivity.this,Constants.SharedPrefConstants.PASSWORD,"");
//                binding.signinAuth.setEnabled(false);
//                PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAGONETIME,true);
//            }
        }
    }

    fun checkPassword(showError: Boolean) {
        if (ValidationUtil().checkPassword(binding?.editTextPassword?.getText().toString(), binding) != null
        ) {
            if (showError) {
                binding?.editTextPassword?.setErrorMessage(
                    ValidationUtil().checkPassword(binding?.editTextPassword?.getText().toString(), binding))
                binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.error_edittext_bg))
                binding?.passwordInfo?.setVisibility(View.VISIBLE)
            }
            binding?.passwordVerified?.setVisibility(View.GONE)
            stopAuth()
        } else {
            binding?.editTextPassword?.setErrorMessage("")
            binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
            binding?.passwordInfo?.setVisibility(View.GONE)
            binding?.passwordVerified?.setVisibility(View.VISIBLE)
            //                        binding.idPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0);
        }
        stopAuth()
    }

    fun checkPasswordforpopup(showError: Boolean) {
        if (ValidationUtil().checkPassword(
                binding?.editTextPassword?.getText().toString(),
                binding
            ) != null
        ) {
            if (showError) {
                binding?.editTextPassword?.setErrorMessage(
                    ValidationUtil().checkPassword(binding?.editTextPassword?.getText().toString(), binding))
                binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.error_edittext_bg))
                binding?.passwordInfo?.setVisibility(View.VISIBLE)
            }
            binding?.passwordVerified?.setVisibility(View.GONE)
        } else {
            binding?.editTextPassword?.setErrorMessage("")
            binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
            binding?.passwordInfo?.setVisibility(View.GONE)
            binding?.passwordVerified?.setVisibility(View.VISIBLE)
            biometric(false, "n")
            //                        binding.idPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0);
        }
    }

    fun checkButton(statusofcheck: Boolean) {
        val isValidPhone: Boolean =
            ValidationUtil().isValidPhone(binding?.editTextUserId?.getText().toString())
        val validEmail = ValidationUtil().checkEmail(
            binding?.editTextUserId?.getText().toString()
        ) == true || TextUtils.isDigitsOnly(binding?.editTextUserId?.getText().toString()) && isValidPhone
        val validPass: Boolean =
            ValidationUtil().checkPassword(binding?.editTextPassword?.getText().toString()) == true
        if (validEmail && validPass) {
            binding?.signinBtn?.setEnabled(true)
            if (fingerprintenableordisable()) {
                if (fingerprintlockemailchange()) {
                } else {
                    PrefUtility().saveBooleanInPref(this@LoginActivity,
                        Constants.SharedPrefConstants.LOCKFPemailchange, true)
                }
            } else {
                fingerprintlock5times()
                val LOCKFPemailchange: Boolean = PrefUtility().getBooleanInPref(this@LoginActivity,
                    Constants.SharedPrefConstants.LOCKFPemailchange, false)
                if (LOCKFPemailchange) {
                    stopAuth()
                    PrefUtility().saveBooleanInPref(this@LoginActivity,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                    val passworddata: String? = PrefUtility().getStringInPref(this@LoginActivity,
                        Constants.SharedPrefConstants.PASSWORD, "")
                    PrefUtility().saveStringInPref(this@LoginActivity,
                        Constants.SharedPrefConstants.DUMMYPASSWORD, passworddata)
                    PrefUtility().saveStringInPref(this@LoginActivity,
                        Constants.SharedPrefConstants.PASSWORD, "")
                    if (fingerprintlockemailchange()) {
                        val mypass: String? = PrefUtility().getStringInPref(this@LoginActivity,
                            Constants.SharedPrefConstants.DUMMYPASSWORD, "")
                        PrefUtility.saveStringInPref(
                            this@LoginActivity,
                            Constants.SharedPrefConstants.PASSWORD,
                            mypass
                        )
                        PrefUtility.saveStringInPref(
                            this@LoginActivity,
                            Constants.SharedPrefConstants.DUMMYPASSWORD,
                            ""
                        )
                        PrefUtility.saveBooleanInPref(
                            this@LoginActivity,
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
            binding.signinBtn.setEnabled(false)
            binding.signinAuth.setEnabled(false)
            if (statusofcheck) {
                if (fingerprintenableordisable()) {
                    binding.signinAuth.setEnabled(true)
                } else {
                    binding.signinAuth.setEnabled(false)
                }
            } else {
                if (validEmail && validPass) {
                } else {
                    binding?.signinBtn?.setEnabled(false)
                    val email: String? = PrefUtility().getStringInPref(
                        this@LoginActivity,
                        Constants.SharedPrefConstants.EMAIL,
                        ""
                    )
                    val LOCKFP: Boolean = PrefUtility().getBooleanInPref(
                        this@LoginActivity,
                        Constants.SharedPrefConstants.LOCKFP,
                        false
                    )
                    val finerprintstate: Boolean = PrefUtility().getBooleanInPref(
                        this@LoginActivity,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false
                    )
                    var decryptpassword = ""
                    if (finerprintstate) {
                        val password: String? = PrefUtility().getStringInPref(
                            this@LoginActivity,
                            Constants.SharedPrefConstants.PASSWORD,
                            ""
                        )
                        decryptpassword = EncUtil().decrypt(this@LoginActivity, password).toString()
                    }
                    if (finerprintstate) {

                        // binding.editTextUserId.setText(email);
//                binding.paraLabel1.setVisibility(View.VISIBLE);
//                binding.paraLabel.setVisibility(View.VISIBLE);
//                binding.fingerprintImage.setVisibility(View.VISIBLE);
                        if (validEmail) {
                            //  binding.signinAuth.setEnabled(true);
                            if (binding?.editTextPassword?.getText().toString().trim().length > 0
                                && decryptpassword != binding?.editTextPassword?.getText().toString()) {
                                binding?.signinAuth?.setEnabled(false)
                            } else if (email == binding?.editTextUserId?.getText().toString()) {
                                if (LOCKFP) {
                                    binding?.signinAuth?.setEnabled(false)
                                } else {
                                    binding?.signinAuth?.setEnabled(true)
                                }
                            } else {
                                if (email == "") {
                                    if (LOCKFP) {
                                        binding?.signinAuth?.setEnabled(false)
                                    } else {
                                        binding?.signinAuth?.setEnabled(true)
                                    }
                                } else {
                                    binding?.signinAuth?.setEnabled(false)
                                    stopAuth()
                                    //                            PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//                            PrefUtility.saveStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
                                }
                            }
                        } else {
                            binding.signinAuth.setEnabled(false)
                        }
                    } else {
                        binding.signinAuth.setEnabled(false)
                    }
                }
            }
        }
    }


    fun fingerprintpasswordcheck(): Boolean {
        val finerprintstate1: Boolean = PrefUtility().getBooleanInPref(
            this@LoginActivity,
            Constants.SharedPrefConstants.FINGERPRINTFLAG,
            false
        )
        var decryptpassword1 = ""
        if (finerprintstate1) {
            val password1: String? = PrefUtility().getStringInPref(this@LoginActivity,
                Constants.SharedPrefConstants.PASSWORD, "")
            decryptpassword1 = EncUtil().decrypt(this@LoginActivity, password1).toString()
        }
        return if (binding?.editTextPassword?.getText().toString().trim()
                .length > 0 && decryptpassword1 != binding?.editTextPassword?.getText().toString()
        ) {
            binding?.signinAuth?.setEnabled(true)
            true
        } else {
            if (decryptpassword1 == "") {
                binding?.signinAuth?.setEnabled(true)
                true
            } else {
                binding?.signinAuth?.setEnabled(false)
                false
            }
        }
    }

    fun fingerprintenableordisable(): Boolean {
        val finerprintstate: Boolean = PrefUtility().getBooleanInPref(this@LoginActivity,
            Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
        return if (finerprintstate) {
            binding?.signinAuth?.setEnabled(true)
            true
        } else {
            binding?.signinAuth?.setEnabled(false)
            false
        }
    }

    fun fingerprintlock5times(): Boolean {
        val LOCKFP: Boolean = PrefUtility().getBooleanInPref(this@LoginActivity,
            Constants.SharedPrefConstants.LOCKFP, false)
        return if (LOCKFP) {
            binding?.signinAuth?.setEnabled(false)
            false
        } else {
            binding?.signinAuth?.setEnabled(true)
            true
        }
    }

    fun fingerprintlockemailchange(): Boolean {
        val email: String? =
            PrefUtility().getStringInPref(this@LoginActivity, Constants.SharedPrefConstants.EMAIL, "")
        val finerprintstate1: Boolean = PrefUtility().getBooleanInPref(this@LoginActivity,
            Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
        var decryptpassword1 = getString(R.string.value_n)
        if (finerprintstate1) {
            val password1: String? = PrefUtility().getStringInPref(
                this@LoginActivity,
                Constants.SharedPrefConstants.PASSWORD,
                "n"
            )
            decryptpassword1 = EncUtil().decrypt(this@LoginActivity, password1).toString()
        }
        return if (email == binding?.editTextUserId?.getText()
                .toString() && decryptpassword1 == binding?.editTextPassword?.getText().toString()
        ) {
            binding?.signinAuth?.setEnabled(true)
            true
        } else {
            if (email == "") {
                binding?.signinAuth?.setEnabled(true)
                true
            } else {
                binding?.signinAuth?.setEnabled(false)
                false
            }
        }
    }

/*
//    void checkButton1() {
//
//        boolean isValidPhone = ValidationUtil.isValidPhone(binding.editTextUserId.getText().toString());
//        Boolean validEmail = (ValidationUtil.checkEmail(binding.editTextUserId.getText().toString()) || (TextUtils.isDigitsOnly(binding.editTextUserId.getText().toString()) && isValidPhone));
//        Boolean validPass = ValidationUtil.checkPassword(binding.editTextPassword.getText().toString());
//
//        boolean finerprintstate = PrefUtility.getBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//        String decryptpassword = "";
//        if(finerprintstate){
//            String password = PrefUtility.getStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
//            decryptpassword = EncUtil.decrypt(LoginActivity.this, password);
//        }
//
//        if (validEmail && validPass) {
//            binding.signinBtn.setEnabled(true);
//
//            boolean LOCKFP = PrefUtility.getBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.LOCKFP, false);
//            String email = PrefUtility.getStringInPref(LoginActivity.this, Constants.SharedPrefConstants.EMAIL, "");
//
//            if(LOCKFP){
//                binding.signinAuth.setEnabled(false);
//
//            }else {
//                binding.signinAuth.setEnabled(true);
//            }
//
//
//            if(binding.editTextPassword.getText().toString().trim().length() > 0 && !decryptpassword.equals( binding.editTextPassword.getText().toString()) ){
//                if(finerprintstate){
//                    binding.signinAuth.setEnabled(false);
//                }else {
//                    binding.signinAuth.setEnabled(true);
//                }
//            }else if(email.equals(binding.editTextUserId.getText().toString())){
//                if(LOCKFP){
//                    binding.signinAuth.setEnabled(false);
//                }else {
//                    binding.signinAuth.setEnabled(true);
//                }
//            }else {
//                if(email.equals("")){
//                    if(LOCKFP){
//                        binding.signinAuth.setEnabled(false);
//
//                    }else {
//                        binding.signinAuth.setEnabled(true);
//                    }
//                }else {
//                    binding.signinAuth.setEnabled(false);
//                    stopAuth();
//                    PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//                    PrefUtility.saveStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
//
//                }
//            }
//
//
//        } else {
//            binding.signinBtn.setEnabled(false);
//
//            String email = PrefUtility.getStringInPref(LoginActivity.this, Constants.SharedPrefConstants.EMAIL, "");
//            boolean LOCKFP = PrefUtility.getBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.LOCKFP, false);
//
//            if (finerprintstate) {
//
//                // binding.editTextUserId.setText(email);
////                binding.paraLabel1.setVisibility(View.VISIBLE);
////                binding.paraLabel.setVisibility(View.VISIBLE);
////                binding.fingerprintImage.setVisibility(View.VISIBLE);
//
//                if(validEmail){
//                    //  binding.signinAuth.setEnabled(true);
//                    if(binding.editTextPassword.getText().toString().trim().length() > 0 && !decryptpassword.equals( binding.editTextPassword.getText().toString()) ){
//                        if(finerprintstate){
//                            binding.signinAuth.setEnabled(false);
//                        }else {
//                            binding.signinAuth.setEnabled(true);
//                        }
//                    }else if(email.equals(binding.editTextUserId.getText().toString())){
//                        if(LOCKFP){
//                            binding.signinAuth.setEnabled(false);
//
//                        }else {
//                            binding.signinAuth.setEnabled(true);
//                        }
//                    }else {
//                        if(email.equals("")){
//                            if(LOCKFP){
//                                binding.signinAuth.setEnabled(false);
//
//                            }else {
//                                binding.signinAuth.setEnabled(true);
//                            }
//                        }else {
//                            binding.signinAuth.setEnabled(false);
//                            stopAuth();
////                            PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
////                            PrefUtility.saveStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
//
//                        }
//                    }
//                }else {
//                    binding.signinAuth.setEnabled(false);
//                }
//
//            } else {
//                if(validEmail){
//                    binding.signinAuth.setEnabled(false);
//                }else {
//                    binding.signinAuth.setEnabled(false);
//                }
//
//            }
//        }
//    }


    //    void checkButton1() {
    //
    //        boolean isValidPhone = ValidationUtil.isValidPhone(binding.editTextUserId.getText().toString());
    //        Boolean validEmail = (ValidationUtil.checkEmail(binding.editTextUserId.getText().toString()) || (TextUtils.isDigitsOnly(binding.editTextUserId.getText().toString()) && isValidPhone));
    //        Boolean validPass = ValidationUtil.checkPassword(binding.editTextPassword.getText().toString());
    //
    //        boolean finerprintstate = PrefUtility.getBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
    //        String decryptpassword = "";
    //        if(finerprintstate){
    //            String password = PrefUtility.getStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
    //            decryptpassword = EncUtil.decrypt(LoginActivity.this, password);
    //        }
    //
    //        if (validEmail && validPass) {
    //            binding.signinBtn.setEnabled(true);
    //
    //            boolean LOCKFP = PrefUtility.getBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.LOCKFP, false);
    //            String email = PrefUtility.getStringInPref(LoginActivity.this, Constants.SharedPrefConstants.EMAIL, "");
    //
    //            if(LOCKFP){
    //                binding.signinAuth.setEnabled(false);
    //
    //            }else {
    //                binding.signinAuth.setEnabled(true);
    //            }
    //
    //
    //            if(binding.editTextPassword.getText().toString().trim().length() > 0 && !decryptpassword.equals( binding.editTextPassword.getText().toString()) ){
    //                if(finerprintstate){
    //                    binding.signinAuth.setEnabled(false);
    //                }else {
    //                    binding.signinAuth.setEnabled(true);
    //                }
    //            }else if(email.equals(binding.editTextUserId.getText().toString())){
    //                if(LOCKFP){
    //                    binding.signinAuth.setEnabled(false);
    //                }else {
    //                    binding.signinAuth.setEnabled(true);
    //                }
    //            }else {
    //                if(email.equals("")){
    //                    if(LOCKFP){
    //                        binding.signinAuth.setEnabled(false);
    //
    //                    }else {
    //                        binding.signinAuth.setEnabled(true);
    //                    }
    //                }else {
    //                    binding.signinAuth.setEnabled(false);
    //                    stopAuth();
    //                    PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
    //                    PrefUtility.saveStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
    //
    //                }
    //            }
    //
    //
    //        } else {
    //            binding.signinBtn.setEnabled(false);
    //
    //            String email = PrefUtility.getStringInPref(LoginActivity.this, Constants.SharedPrefConstants.EMAIL, "");
    //            boolean LOCKFP = PrefUtility.getBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.LOCKFP, false);
    //
    //            if (finerprintstate) {
    //
    //                // binding.editTextUserId.setText(email);
    ////                binding.paraLabel1.setVisibility(View.VISIBLE);
    ////                binding.paraLabel.setVisibility(View.VISIBLE);
    ////                binding.fingerprintImage.setVisibility(View.VISIBLE);
    //
    //                if(validEmail){
    //                    //  binding.signinAuth.setEnabled(true);
    //                    if(binding.editTextPassword.getText().toString().trim().length() > 0 && !decryptpassword.equals( binding.editTextPassword.getText().toString()) ){
    //                        if(finerprintstate){
    //                            binding.signinAuth.setEnabled(false);
    //                        }else {
    //                            binding.signinAuth.setEnabled(true);
    //                        }
    //                    }else if(email.equals(binding.editTextUserId.getText().toString())){
    //                        if(LOCKFP){
    //                            binding.signinAuth.setEnabled(false);
    //
    //                        }else {
    //                            binding.signinAuth.setEnabled(true);
    //                        }
    //                    }else {
    //                        if(email.equals("")){
    //                            if(LOCKFP){
    //                                binding.signinAuth.setEnabled(false);
    //
    //                            }else {
    //                                binding.signinAuth.setEnabled(true);
    //                            }
    //                        }else {
    //                            binding.signinAuth.setEnabled(false);
    //                            stopAuth();
    ////                            PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
    ////                            PrefUtility.saveStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
    //
    //                        }
    //                    }
    //                }else {
    //                    binding.signinAuth.setEnabled(false);
    //                }
    //
    //            } else {
    //                if(validEmail){
    //                    binding.signinAuth.setEnabled(false);
    //                }else {
    //                    binding.signinAuth.setEnabled(false);
    //                }
    //
    //            }
    //        }
    //    }*/
    fun belowversionV9() {
        binding.signinAuth.setVisibility(View.VISIBLE)
        val fingerprintManager2 = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
        if (fingerprintManager2 == null) {
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
            binding?.signinAuth?.setVisibility(View.GONE)
            binding?.signinBtn?.setLayoutParams(
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            )
        } else if (!fingerprintManager2.isHardwareDetected) {
            PrefUtility().saveBooleanInPref(this@LoginActivity,
                Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
            PrefUtility().saveStringInPref(this@LoginActivity,
                Constants.SharedPrefConstants.PASSWORD, "")
            binding?.signinAuth?.setVisibility(View.GONE)
            //   Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
            binding?.signinBtn?.setLayoutParams(
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            )
        } else if (!fingerprintManager2.hasEnrolledFingerprints()) {
            PrefUtility().saveBooleanInPref(this@LoginActivity,
                Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
            PrefUtility().saveStringInPref(this@LoginActivity,
                Constants.SharedPrefConstants.PASSWORD, "")
            // User hasn't enrolled any fingerprints to authenticate with
            binding?.signinAuth?.setVisibility(View.GONE)
            binding?.signinBtn?.setLayoutParams(
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            )
        } else {
            binding?.signinAuth?.setVisibility(View.VISIBLE)
            checkfingerprint()
        }
    }

    private fun setView() {
        //finerprint

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //generateKey();
//            binding.signinAuth.setVisibility(View.VISIBLE);
//
//            checkfingerprint();
//
////        if (cipherInit()) {
////        }
//
//        }
//check fp
        //appname
        binding?.idWelcomeText?.setText(
            java.lang.String.format(getString(R.string.welcome_to_omnicurenow), Buildconfic.value())
        )
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
        binding?.editTextUserId?.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                binding?.editTextUserId?.setText(binding?.editTextUserId?.getText().toString().trim())
                binding?.editTextUserId?.addTextChangedListener(GenericTextWatcher(binding?.editTextUserId!!))
                checkEmail(true)
            }
        })
        binding?.editTextPassword?.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
//                    if (binding.editTextPassword.getErrorMessage() == "") {
                if (binding?.editTextPassword?.getErrorMessage().equals("")) {
                    binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.border_black_edittext_bg))
                }
            } else {
                binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
                binding?.editTextPassword?.addTextChangedListener(GenericTextWatcher(binding?.editTextPassword))
                checkPassword(true)
            }
        })
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding?.idSignupText?.setText(
            Html.fromHtml(
                String.format(
                    "Don\'t have an account?" + "<font color='#%s'><b>" + " SIGN UP</b></font>",
                    сolorString
                )
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
        binding.editTextUserId.addTextChangedListener(ValidationTextWatcher(binding.editTextUserId))
        binding.editTextPassword.addTextChangedListener(ValidationTextWatcher(binding.editTextPassword))
    }

    fun checkfingerprintabove29() {
        val biometricManager: BiometricManager = from(this@LoginActivity)
        binding?.signinAuth?.setVisibility(View.VISIBLE)
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
            binding.signinAuth.setVisibility(View.GONE)
            binding.signinBtn.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
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
            binding?.signinAuth?.setVisibility(View.GONE)
            binding?.signinBtn?.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )
        } else if (biometricManager.canAuthenticate() === BiometricManager.BIOMETRIC_SUCCESS) {
            binding?.signinAuth?.setVisibility(View.VISIBLE)
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
        if (biometricPrompt != null) biometricPrompt?.cancelAuthentication()
    }

    fun biometric(status: Boolean, normal: String) {
        //   Executor executor = Executors.newSingleThreadExecutor();
        val activity: FragmentActivity = this
        biometricPrompt =
            BiometricPrompt(activity, getMainThreadExecutor(), object : AuthenticationCallback() {
                fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT) {
                        /**
                         * The device does not have a biometric sensor.
                         */
                        binding.signinAuth.setVisibility(View.GONE)
                        binding.signinBtn.setLayoutParams(
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                        )
                    } else if (errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE) {
                        /**
                         * The hardware is unavailable. Try again later.
                         */
//                    binding.signinAuth.setVisibility(View.GONE);
//                    binding.signinBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    } else if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS) {
                        /**
                         * The user does not have any biometrics enrolled.
                         */
                        binding.signinAuth.setVisibility(View.GONE)
                        binding.signinBtn.setLayoutParams(
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                        )
                    } else if (errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT) {
                        /**
                         * The operation was canceled because ERROR_LOCKOUT occurred too many times.
                         * Biometric authentication is disabled until the user unlocks with strong authentication
                         * (PIN/Pattern/Password)
                         */
                        binding.signinAuth.setVisibility(View.GONE)
                        binding.signinBtn.setLayoutParams(
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                        )
                    } else if (errorCode == BiometricPrompt.ERROR_VENDOR) {
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
                        CustomSnackBar.make(binding?.rootLayout, this@LoginActivity,
                            CustomSnackBar.WARNING, getString(R.string.Authfaildnew),
                            CustomSnackBar.TOP, 3000, 0)?.show()
                        PrefUtility().saveBooleanInPref(this@LoginActivity,
                            Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                        PrefUtility().saveStringInPref(
                            this@LoginActivity, Constants.SharedPrefConstants.PASSWORD, "")
                        PrefUtility().saveBooleanInPref(this@LoginActivity,
                            Constants.SharedPrefConstants.LOCKFP, true)
                        PrefUtility().saveBooleanInPref(this@LoginActivity,
                            Constants.SharedPrefConstants.LOCKFPemailchange, false)
                        stopAuth()
                        binding?.signinAuth?.setEnabled(false)
                        //   binding.editTextUserId.setText("");
                        binding?.editTextPassword?.setText("")

                        //handleMultipleClicknew(binding.signinAuth);
                    } else if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // user clicked negative button
                    } else if (errorCode == BiometricPrompt.ERROR_TIMEOUT) {
                        /**
                         * Error state returned when the current request has been running too long. This is intended to
                         * prevent programs from waiting for the biometric sensor indefinitely. The timeout is platform
                         * and sensor-specific, but is generally on the order of 30 seconds.
                         */
//                    CustomSnackBar.make(dialogView, LoginActivity.this,CustomSnackBar.WARNING, getString(R.string.fpdisable), CustomSnackBar.TOP,3000,0).show();
//                    stopAuth();

                        /* CustomSnackBar.make(binding.rootLayout, LoginActivity.this,CustomSnackBar.WARNING, getString(R.string.Authfaildnew), CustomSnackBar.TOP,3000,0).show();

                    PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
                    PrefUtility.saveStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
                    PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.LOCKFP, true);
                    stopAuth();
                    binding.signinAuth.setEnabled(false);
                    binding.editTextUserId.setText("");
                    binding.editTextPassword.setText("");*/
                    } else if (errorCode == BiometricPrompt.ERROR_CANCELED) {
                        // you can either show the dialog again here
//                    CustomSnackBar.make(binding.container, MyProfileActivity.this,CustomSnackBar.WARNING, getString(R.string.fpdisablenew), CustomSnackBar.TOP,3000,0).show();
//
//                    PrefUtility.saveBooleanInPref(MyProfileActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//                    PrefUtility.saveStringInPref(MyProfileActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
//                    fingerprintonoff.setChecked(false);
//                    stopAuth();
//                    final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                            .setTitle("Authentication Required")
//                            .setNegativeButtonText("Cancel")
//                            .build();
//
//                    biometricPrompt.authenticate(promptInfo);
                    }
                }

                fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (normal == "normal") {
                        val email: String? = PrefUtility().getStringInPref(this@LoginActivity,
                            Constants.SharedPrefConstants.EMAIL, "")
                        val password: String? = PrefUtility().getStringInPref(this@LoginActivity,
                            Constants.SharedPrefConstants.PASSWORD, "")
                        if (email == "" || password == "") {
                        } else {
                            EncUtil().generateKey(this@LoginActivity)
                            val decryptpassword: String? =
                                EncUtil().decrypt(this@LoginActivity, password)
                            binding?.editTextPassword?.setText(decryptpassword)
                            checkEmail(true)
                            onClickSignIn()
                        }
                    } else {
                        if (status) {
                            val email2: String? = PrefUtility().getStringInPref(this@LoginActivity,
                                Constants.SharedPrefConstants.EMAIL, "")
                            val password2: String? = PrefUtility().getStringInPref(this@LoginActivity,
                                Constants.SharedPrefConstants.PASSWORD, "")
                            if (email2 == "" || password2 == "") {
                            } else {
                                EncUtil().generateKey(this@LoginActivity)
                                val decryptpassword: String? =
                                    EncUtil().decrypt(this@LoginActivity, password2)
                                binding?.editTextPassword?.setText(decryptpassword)
                                fingerprintdata = "yes"
                                checkEmail(true)
                                onClickSignIn()
                            }
                        } else {
                            val email1: String = binding?.editTextUserId?.getText().toString()
                            val password1: String = binding?.editTextPassword.getText().toString()
                            if (email1 == "" || password1 == "") {
                            } else {
                                fingerprintdata = "yes"
                                checkEmail(true)
                                onClickSignIn()
                            }
                        }
                    }
                    super.onAuthenticationSucceeded(result)
                    //TODO: Called when a biometric is recognized.
                }

                fun onAuthenticationFailed() {
                    val finerprintstate: Boolean = PrefUtility().getBooleanInPref(this@LoginActivity,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG, false)
                    if (finerprintstate) {
                        CustomSnackBar.make(binding?.rootLayout,
                            this@LoginActivity, CustomSnackBar.WARNING,
                            getString(R.string.Authfaild), CustomSnackBar.TOP, 3000, 0)?.show()

                        // diaalog("Authentication Failed Please Try Again");
                    }
                    super.onAuthenticationFailed()
                    //TODO: Called when a biometric is valid but not recognized.
                }
            })
        val promptInfo: BiometricPrompt.PromptInfo = OkHttpClient.Builder()
            .setTitle("Authentication Required")
            .setNegativeButtonText("Cancel")
            .build()
        biometricPrompt?.authenticate(promptInfo)
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

    private fun handleMultipleClicknew(item: View) {
        val multipleclieck: Boolean = PrefUtility().getBooleanInPref(this@LoginActivity,
            Constants.SharedPrefConstants.MUTILPLECLICK, false)
        if (multipleclieck) {
            // item.setEnabled(false);
            CustomSnackBar.make(binding?.rootLayout, this@LoginActivity,
                CustomSnackBar.WARNING, getString(R.string.Authfaildnew),
                CustomSnackBar.TOP, 3000, 0)?.show()
            // mHandler.postDelayed(() -> item.setEnabled(true), 30000);
            PrefUtility().saveBooleanInPref(this@LoginActivity,
                Constants.SharedPrefConstants.LOCKFP, true)
            PrefUtility().saveBooleanInPref(this@LoginActivity,
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
            this@LoginActivity,
            Constants.SharedPrefConstants.FINGERPRINTFLAG,
            false
        )
        if (finerprintstate) {
            if (password == "") {
                binding?.signinAuth?.setEnabled(false)
            } else {
                binding?.editTextUserId?.setText(email)
                checkEmail(true)
                binding?.signinAuth?.setEnabled(true)
                biometric(false, "normal")
            }
        } else {
            binding?.signinAuth?.setEnabled(false)
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
                    if (binding?.editTextPassword?.getText().toString().length > 0) {
                        checkPasswordforpopup(true)
                    } else {
                        biometric(true, "n")
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
        handleMultipleClick(binding?.signinBtn)
        if (!isValid()) {
            return
        }
        if (signInApiFlag) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.rootLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.rootLayout, this@LoginActivity,
                CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.signin.toString()))
        val userIdStr: String = binding?.editTextUserId?.getText().toString()
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
        val password: String = binding?.editTextPassword?.getText().toString()
        val email = emailText
        signInApiFlag = true
        Log.d(TAG, "loginApi: $password")
        showProgressBar()
        viewModel.login(email.toLowerCase(), password, fcmToken, overrideFlag, version)
            .observe(this,
                Observer<Any?> { commonResponse ->
                    dismissProgressBar()
                    Log.i(TAG, "Login response " + Gson().toJson(commonResponse))
                    signInApiFlag = false
                    if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                        PrefUtility().clearRedirectValidation(this@LoginActivity)
                        if (commonResponse.getFeedbackForm() != null) {
                            val encryptionKey1: String = commonResponse.getEncryptionKey()
                            PrefUtility().saveStringInPref(
                                this@LoginActivity,
                                Constants.SharedPrefConstants.ENCRYPTIONKEY,
                                encryptionKey1
                            )
                            strFeedbackForm = commonResponse.getFeedbackForm()
                            //fp
                            val LOCKFP: Boolean = PrefUtility().getBooleanInPref(
                                this@LoginActivity,
                                Constants.SharedPrefConstants.LOCKFP,
                                false
                            )
                            if (LOCKFP) {
                                PrefUtility().saveBooleanInPref(
                                    this@LoginActivity,
                                    Constants.SharedPrefConstants.LOCKFP,
                                    false
                                )
                                //    PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, true);
                                val encryptionKey: String = commonResponse.getEncryptionKey()
                                PrefUtility().saveStringInPref(
                                    this@LoginActivity,
                                    Constants.SharedPrefConstants.ENCRYPTIONKEY,
                                    encryptionKey
                                )
                            }
                            PrefUtility().saveStringInPref(
                                this@LoginActivity,
                                Constants.SharedPrefConstants.FEEDBACK_URL,
                                commonResponse.getFeedbackForm()
                            )
                        }
                        if (commonResponse.getTutorial_url() != null) {
                            PrefUtility().saveStringInPref(
                                this@LoginActivity,
                                Constants.SharedPrefConstants.TUTORIAL_URL,
                                commonResponse.getTutorial_url()
                            )
                        }
                        if (commonResponse.getAesEncryptionKey() != null) {
                            EncUtil().generateKey(this@LoginActivity)
                            val encyptKey: String? = EncUtil().encrypt(this@LoginActivity,
                                commonResponse.getAesEncryptionKey()
                            )
                            PrefUtility().saveStringInPref(this@LoginActivity,
                                Constants.SharedPrefConstants.AES_KEY, encyptKey)
                        }
                        if (commonResponse.getAgoraAppCertificate() != null) {
                            PrefUtility().saveStringInPref(
                                this@LoginActivity,
                                Constants.SharedPrefConstants.AGORA_CERTIFICATE,
                                commonResponse.getAgoraAppCertificate()
                            )
                        }
                        if (commonResponse.getAgoraAppId() != null) {
                            PrefUtility().saveStringInPref(
                                this@LoginActivity,
                                Constants.SharedPrefConstants.AGORA_APP_ID,
                                commonResponse.getAgoraAppId()
                            )
                        }
                        if (commonResponse.getIdToken() != null) {
                            PrefUtility().saveStringInPref(
                                this@LoginActivity,
                                Constants.SharedPrefConstants.FIREBASE_IDTOKEN,
                                commonResponse.getIdToken()
                            )
                        }
                        if (commonResponse.getRefreshToken() != null) {
                            PrefUtility().saveStringInPref(
                                this@LoginActivity,
                                Constants.SharedPrefConstants.FIREBASE_REFRESH_TOKEN,
                                commonResponse.getRefreshToken()
                            )
                        }
                        if (commonResponse.getProvider().getRole()
                                .equalsIgnoreCase(Constants.ProviderRole.RD.toString())
                        ) {
                            val topic: String = UtilityMethods().getFCMTopic()
                            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                                .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "topic subscribe:success")
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "topic subscribe:failure", task.exception)
                                        Toast.makeText(
                                            this@LoginActivity,
                                            getString(R.string.authentication_failed),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                })
                        }
                        //encrypt
                        EncUtil().generateKey(this@LoginActivity)
                        val encyuptpassword: String? = EncUtil().encrypt(this@LoginActivity, password)
                        commonResponse.getProvider().setPassword(encyuptpassword)
                        onLoginSuccess(commonResponse.getProvider())
                    } else {
                        var errorId = 0
                        if (commonResponse.getErrorId() != null) {
                            errorId = commonResponse.getErrorId()
                            if (errorId != 150) {
                                binding.editTextPassword.setText("")
                            }
                        }
                        if (commonResponse.getProvider() != null) {
                            redirectPage(
                                commonResponse.getProvider(),
                                errorId,
                                commonResponse.getErrorMessage()
                            )
                        }
                        if (commonResponse.getErrorMessage() != null && commonResponse.getErrorMessage()
                                .equalsIgnoreCase("redirect")
                        ) {
                            val intent =
                                Intent(this@LoginActivity, RegistrationSuccessActivity::class.java)
                            intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
                            startActivity(intent)
                            return@Observer
                        }
                        if (commonResponse.getErrorMessage() != null && commonResponse.getErrorMessage()
                                .equalsIgnoreCase("unauthorized")
                        ) {
                            val intent =
                                Intent(this@LoginActivity, RegistrationSuccessActivity::class.java)
                            intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
                            startActivity(intent)
                            return@Observer
                        }
                        val errMsg1 = getString(R.string.temporarily_locked)
                        if (commonResponse.getErrorMessage() != null && commonResponse.getErrorMessage()
                                .equals(errMsg1)
                        ) {
                            //                        PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
                            //                        PrefUtility.saveStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
                            binding?.editTextPassword?.setText("")
                            binding?.editTextUserId?.setText("")
                            if (alertDialog != null) {
                                alertDialog!!.dismiss()
                            }
                        }
                        val errMsg2 = getString(R.string.invalid_creds)
                        if (commonResponse.getErrorMessage() != null && commonResponse.getErrorMessage()
                                .equals(errMsg2)
                        ) {
                            binding?.editTextPassword?.setText("")
                            if (alertDialog != null) {
                                alertDialog!!.dismiss()
                            }
                        }
                        if (errorId == 150) {
                            alreadyLoggedInPopup()
                        } else if (errorId == 103) {
                            val intent =
                                Intent(this@LoginActivity, RegistrationSuccessActivity::class.java)
                            intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
                            startActivity(intent)
                        } else {
                            if (errorId != 110) {
                                val errMsg: String? = ErrorMessages().getErrorMessage(
                                    this@LoginActivity,
                                    commonResponse.getErrorMessage(),
                                    Constants.API.signin
                                )
                                //                            UtilityMethods.showErrorSnackBar(binding.rootLayout, errMsg, Snackbar.LENGTH_LONG);
                                errMsg?.let {
                                    CustomSnackBar.make(
                                        binding?.rootLayout,
                                        this@LoginActivity,
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

    private fun loginFailedApi() {
        val email = emailText
        val password: String = binding.editTextPassword.getText().toString()
        viewModel.loginFailed(email.toLowerCase(), password).observe(this) { commonResponse ->
            dismissProgressBar()
            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
            } else {
                val errMsg: String = commonResponse.getErrorMessage()
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
//                    PrefUtility.saveBooleanInPref(LoginActivity.this, Constants.SharedPrefConstants.FINGERPRINTFLAG, false);
//                    PrefUtility.saveStringInPref(LoginActivity.this, Constants.SharedPrefConstants.PASSWORD, "");
                    binding?.editTextPassword.setText("")
                    binding?.editTextUserId.setText("")
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
        viewModel.getEmail(phone).observe(this) { commonResponse ->
            println("getEmailResponse $commonResponse")
            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                emailText = commonResponse.getEmail()
                signInFirebase()
            } else {
                dismissProgressBar()
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    commonResponse.getErrorMessage(),
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
                ).show()
            }
        }
    }

    private fun redirectPage(provider: Provider, errorId: Int, errMsg: String) {
        if (errorId == 101) {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.getId())
            intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO, provider.getPhone())
            intent.putExtra(Constants.IntentKeyConstants.COUNTRY_CODE, provider.getCountryCode())
            intent.putExtra(Constants.IntentKeyConstants.FROM_PAGE, "login")
            startActivity(intent)
        } else if (errorId == 102) {
            val intent = Intent(this, EmailOTPActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_EMAIL, provider.getEmail())
            intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.getId())
            intent.putExtra(Constants.IntentKeyConstants.FROM_PAGE, "login")
            startActivity(intent)
        } else if (errorId == 103) {
            val intent = Intent(this, RegistrationSuccessActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
            startActivity(intent)
        } else if (errorId == 110) {
            expirePasswordPopup(
                errMsg,
                provider.getEmail(),
                provider.getFname(),
                provider.getLname(),
                provider.getToken()
            )
        }
    }

    private fun onClickForgotPassword() {
        handleMultipleClick(binding.idForgotPassword)
        Log.i(TAG, "onClickForgotPassword: ")
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun onClickSignUp() {
        Log.i(TAG, "onClickSignUp: ")
        handleMultipleClick(binding.idSignupText)
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
            PrefUtility().getStringInPref(this@LoginActivity, Constants.SharedPrefConstants.EMAIL, "")
        if (email == binding?.editTextUserId?.getText().toString()) {
        } else {
            if (email == "") {
            } else {
                PrefUtility().saveBooleanInPref(
                    this@LoginActivity,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    false
                )
                PrefUtility().saveStringInPref(
                    this@LoginActivity,
                    Constants.SharedPrefConstants.DUMMYPASSWORD,
                    ""
                )
                PrefUtility().saveStringInPref(
                    this@LoginActivity,
                    Constants.SharedPrefConstants.PASSWORD,
                    ""
                )
            }
        }
        PrefUtility().saveLongInPref(
            this@LoginActivity,
            Constants.SharedPrefConstants.APP_ACTIVE_TIME,
            System.currentTimeMillis()
        )
        PrefUtility().saveUserData(this, provider)
        Log.d(TAG, "onLoginSuccess: " + provider.getUserId())
        val intent = Intent(this@LoginActivity, MyDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
//        signUpFirebase(provider.getPhone());
    }

    private fun isValid(): Boolean {
        val errMsg: String = ValidationUtil.isValidate(binding)
        return if (!errMsg.isEmpty()) {
            //            UtilityMethods.showErrorSnackBar(binding.rootLayout, errMsg, Snackbar.LENGTH_LONG);
            false
        } else true
    }

    fun expirePasswordPopup(
        errMsg: String?,
        email: String?,
        strFirstName: String?,
        strLastName: String?,
        token: String?
    ) {
        val builder = AlertDialog.Builder(this@LoginActivity, R.style.CustomAlertDialog)
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
            val intent = Intent(this@LoginActivity, ExpirePasswordActivity::class.java)
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
        val builder = AlertDialog.Builder(this@LoginActivity, R.style.CustomAlertDialog)
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
            val finerprintstate: Boolean = PrefUtility.getBooleanInPref(
                this@LoginActivity,
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
        binding.rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                var isKeyboardShowing = false
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.rootLayout.getWindowVisibleDisplayFrame(r)
                    val screenHeight: Int = binding.rootLayout.getRootView().getHeight()

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    val keypadHeight = screenHeight - r.bottom
                    Log.d(TAG, "keypadHeight = $keypadHeight")
                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
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
            binding.idSignupText.setVisibility(View.GONE)
        } else {
            mHandler.postDelayed(Runnable { binding.idSignupText.setVisibility(View.VISIBLE) }, 100)
        }
    }

    var mobileFlag = false
    var emailText = ""
    var showError = true

    fun signInFirebase() {
        val email = emailText
        val pwd: String = binding.editTextPassword.getText().toString()
        mFirebaseAuth!!.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                val user1 = mFirebaseAuth!!.currentUser
                println("user_details " + user1!!.uid)
                PrefUtility.saveStringInPref(
                    this@LoginActivity,
                    Constants.SharedPrefConstants.FIREBASE_UID,
                    user1.uid
                )
                user1.getIdToken(false).addOnSuccessListener { result ->
                    val idToken = result.token
                    if (task.isSuccessful) {
                        println("getToken $idToken")
                        PrefUtility.saveStringInPref(
                            this@LoginActivity,
                            Constants.SharedPrefConstants.FIREBASE_IDTOKEN,
                            idToken
                        )
                        loginApi(false)
                        // Send token to your backend via HTTPS
                        // ...
                    }
                    //Do whatever
                    Log.d(TAG, "GetTokenResult result = $idToken")
                }
            } else {
                //                    String errMsg = "";
                //                    try {
                //                        throw task.getException();
                //                    } catch(FirebaseAuthInvalidUserException e) {
                //                        errMsg = getString(R.string.user_not_exist);
                //                    } catch(FirebaseAuthInvalidCredentialsException e) {
                //                        errMsg = getString(R.string.invalid_creds);
                //                    } catch(FirebaseTooManyRequestsException e) {
                //                        errMsg = getString(R.string.temporarily_locked);
                //                    } catch(Exception e) {
                //                        errMsg = getString(R.string.user_not_exist);
                //                        Log.e(TAG, e.getMessage());
                //                    }

                // loginFailedApi();
                Log.w(TAG, "signInWithEmail:taskUnSuccessfull" + task.exception)
                // CustomSnackBar.make(binding.rootLayout, LoginActivity.this, CustomSnackBar.WARNING, task.getException().getMessage(), CustomSnackBar.TOP, 3000, 0).show();
            }


            // ...
        }.addOnFailureListener { e ->
            binding.editTextPassword.setText("")
            var errMsg = ""
            //                if (e.getClass().getSimpleName().equals("FirebaseAuthInvalidUserException")) {
            if (e is FirebaseAuthInvalidUserException) {
                errMsg =
                    if (e.getLocalizedMessage() == "The user account has been disabled by an administrator.") {
                        "Provider Denied"
                    } else {
                        getString(R.string.user_not_exist)
                    }
                showError = false
                CustomSnackBar.make(
                    binding.rootLayout,
                    this@LoginActivity,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
                //                } else if (e.getClass().getSimpleName().equals("FirebaseAuthInvalidCredentialsException")) {
            } else if (e is FirebaseAuthInvalidCredentialsException) {
                errMsg = getString(R.string.invalid_creds)
                //                } else if (e.getClass().getSimpleName().equals("FirebaseTooManyRequestsException")) {
            } else if (e is FirebaseTooManyRequestsException) {
                errMsg = getString(R.string.temporarily_locked)
            } else {
                errMsg = getString(R.string.user_not_exist)
                Log.e(TAG, e.message!!)
            }
            loginFailedApi()
            Log.w(
                TAG,
                "signInWithEmail:failure" + e.message + " " + e.javaClass.simpleName
            )
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
        permissions: Array<String?>, grantResults: IntArray
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

//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA);
//                } else {
//                    finish();
//                }
                checkSelfPermissionGrantedCheck(
                    Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA
                )
            }
            ConstantApp().PERMISSION_REQ_ID_CAMERA -> {

//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
//                } else {
//                    finish();
//                }
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
        if (s.length <= 3 && s.matches("[0-9]+")) {
            return true
        } else if (s.length > 3 && s.length <= 7) {
            s = s.substring(0, 3) + s.substring(4)
        } else if (s.length > 7 && s.length <= 12 && s.replace("-", "").matches("[0-9]+")) {
            s = s.substring(0, 3) + s.substring(4, 7) + s.substring(8)
        }
        return if (s.matches("[0-9]+")) {
            true
        } else false
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
                    LoginActivity(). binding?.editTextUserId?.getText()?.let {
                        Selection.setSelection(
                            LoginActivity(). binding?.editTextUserId?.getText(), it.length
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

    /*private boolean isNumberFormatRequired(String str){
        if(str.startsWith(Constants.US_COUNTRY_CODE + "-")){
            String s = str.replace(Constants.US_COUNTRY_CODE +"-","");
            if(s.startsWith("-") || s.startsWith("+")){
                return false;
            }
        }else if(str.startsWith(Constants.US_COUNTRY_CODE)){
            String s = str.replace(Constants.US_COUNTRY_CODE,"");
            if(s.startsWith("-") || s.startsWith("+")){
                return false;
            }
        }
        String strWithCntryCode = str.startsWith(Constants.US_COUNTRY_CODE)
                ? str.replace("+","").replace("-","") : str.replace("-","");

        if(strWithCntryCode.matches("[0-9]+")){
            return true;
        }
        return false;
    }*/

    //fp


}
