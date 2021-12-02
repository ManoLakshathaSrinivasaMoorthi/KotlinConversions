package com.example.kotlinomnicure.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityForgotPasswordOtpBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.ForgotPasswordOTPViewModel
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.ForgotPasswordRequest
import java.lang.Exception

class ForgotPasswordOTPActivity :BaseActivity() {
    private val TAG = ForgotPasswordOTPActivity::class.java.simpleName
    protected var binding: ActivityForgotPasswordOtpBinding? = null
    private var viewModel: ForgotPasswordOTPViewModel? = null
    private var emailID: String? = null
    private var phoneNumber: kotlin.String? = null
    private var countryCode: kotlin.String? = null
    private var emailAddress: String? = ""
    private var strEmail = ""
    private var strFirstName: kotlin.String? = ""
    private var strLastName: kotlin.String? = ""
    private var otpToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password_otp)
        viewModel = ViewModelProvider(this).get(ForgotPasswordOTPViewModel::class.java)
        initViews()
        onClickListener()
    }

    private fun initViews() {
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding?.txtBackToSignIn?.setText(Html.fromHtml(String.format("Back to" + "<font color='#%s'><b> SIGN IN</b></font>",
            сolorString)))
        if (intent.hasExtra(Constants.IntentKeyConstants.EMAIL)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.EMAIL))
        ) {
            emailID = intent.getStringExtra(Constants.IntentKeyConstants.EMAIL)
        }
        if (intent.hasExtra(Constants.IntentKeyConstants.PHONE_NO)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.PHONE_NO))
        ) {
            phoneNumber = intent.getStringExtra(Constants.IntentKeyConstants.PHONE_NO)
        }
        if (intent.hasExtra(Constants.IntentKeyConstants.COUNTRY_CODE)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.COUNTRY_CODE))
        ) {
            countryCode = intent.getStringExtra(Constants.IntentKeyConstants.COUNTRY_CODE)
        }
        emailAddress = intent.getStringExtra("rbEmailAddress")

        binding?.llResendOtp?.setVisibility(View.GONE)
    }

    private fun onClickListener() {
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        binding?.txtBackToSignIn?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        })
        binding?.txtResetPassword?.setEnabled(false)
        binding?.pinEntryBorder?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                throw new UnsupportedOperationException();
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                throw new UnsupportedOperationException();
            }

            override fun afterTextChanged(pin: Editable) {
                if (pin.length == 4) {
                    binding?.txtResetPassword?.setEnabled(true)
                } else {
                    binding?.txtResetPassword?.setEnabled(false)
                }
            }
        })
        binding?.txtResetPassword?.setOnClickListener(View.OnClickListener {
            val pin: String = binding?.pinEntryBorder?.getText().toString()
            if (pin.length < 4) {

                CustomSnackBar.make(binding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.invalid_otp),
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            } else {
                doForgotPasswordOTP(pin)
            }
        })

    }

    private fun doForgotPasswordOTP(pin: String) {
        binding?.txtResetPassword?.let { handleMultipleClick(it) }
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(binding?.containerLayout,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this,
            Constants.API.verifyOTP.toString()))
        val forgotPasswordRequest = ForgotPasswordRequest()
        if (emailAddress == "Email") {
            forgotPasswordRequest.setEmail(emailID)
            forgotPasswordRequest.setToken(Constants.KeyHardcodeToken.HARD_CODE_TOKEN)
            forgotPasswordRequest.setOtp(pin)
        }
        if (emailAddress == "Phone") {
            forgotPasswordRequest.setPhoneNumber(phoneNumber)
            forgotPasswordRequest.setCountryCode(countryCode)
            forgotPasswordRequest.setToken(Constants.KeyHardcodeToken.HARD_CODE_TOKEN)
            forgotPasswordRequest.setOtp(pin)
        }
        viewModel?.forgotPassword(forgotPasswordRequest)?.observe(this,
            Observer<CommonResponse?> { commonResponse ->

                dismissProgressBar()
                if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()!!) {
                    if (commonResponse.getUser() != null) {
                        strEmail = commonResponse.getUser()!!.getEmail().toString()
                        strFirstName = commonResponse.getUser()!!.getFname()
                        strLastName = commonResponse.getUser()!!.getLname()
                        otpToken = commonResponse.getUser()!!.getToken()
                    }
                    val intent = Intent(this,
                        ChangePasswordActivity::class.java)
                    if (emailAddress == "Email") {
                        intent.putExtra("rbEmailAddress", "Email")
                    }
                    if (emailAddress == "Phone") {
                        intent.putExtra("rbEmailAddress", "Phone")
                    }
                    if (emailID != null) {
                        intent.putExtra(Constants.IntentKeyConstants.EMAIL, emailID)
                    } else {
                        intent.putExtra(Constants.IntentKeyConstants.EMAIL, strEmail)
                    }
                    intent.putExtra(Constants.IntentKeyConstants.PHONE_NO, phoneNumber)
                    intent.putExtra(Constants.IntentKeyConstants.COUNTRY_CODE, countryCode)
                    intent.putExtra(Constants.IntentKeyConstants.FIRST_NAME, strFirstName)
                    intent.putExtra(Constants.IntentKeyConstants.LAST_NAME, strLastName)
                    intent.putExtra(Constants.IntentKeyConstants.OTP_TOKEN, otpToken)
                    startActivity(intent)
                } else {
                    var errMsg = ""
                    if (commonResponse != null) {
                        errMsg = ErrorMessages().getErrorMessage(this,
                            commonResponse.getErrorMessage(),
                            Constants.API.forgotPassword).toString()
                    }

                    CustomSnackBar.make(binding?.containerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                }
            })
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }

        if (!TextUtils.isEmpty(errMsg)) {

            CustomSnackBar.make(binding?.containerLayout,
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

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun onResendEmailOTP() {
        if (!UtilityMethods().isInternetConnected(this)!!) {
//            UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }
        showProgressBar(getString(R.string.sending_otp_msg))

        val uid = intent.getLongExtra(Constants.IntentKeyConstants.PROVIDER_ID, -1)
        viewModel?.resendEmailOTP(uid, Constants.OTPChannel.EMAIL.toString(), emailID, "")
            ?.observe(this) { commonResponse ->
                dismissProgressBar()
                if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                    startResetTimer()
                } else {
                    val errMsg: String? =
                        ErrorMessages().getErrorMessage(this,
                            commonResponse.getErrorMessage(),
                            Constants.API.registrationEmailOTP)

                    CustomSnackBar.make(binding?.getRoot(),
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                }
            }
    }

    private fun onResendMobileOTP() {
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }
        showProgressBar(getString(R.string.sending_otp_msg))

        val uid = intent.getLongExtra(Constants.IntentKeyConstants.PROVIDER_ID, -1)
        viewModel?.resendMobileOTP(uid,
            Constants.OTPChannel.SMS.toString(),
            phoneNumber,
            countryCode)?.observe(this) { commonResponse ->
            dismissProgressBar()
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                startResetTimer()
            } else {
                val errMsg: String? =
                    ErrorMessages().getErrorMessage(this,
                        commonResponse.getErrorMessage(),
                        Constants.API.registrationPhoneOTP)

                CustomSnackBar.make(binding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            }
        }
    }

    private fun startResetTimer() {
        binding?.idResendText?.setEnabled(false)
        val btnText = getString(R.string.resent_code_text)
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        try {
            Thread(object : Runnable {
                var pStatus = 30
                override fun run() {
                    while (pStatus >= 0) {
                        mHandler?.post(Runnable {
                            binding?.idResendText?.setText(Html.fromHtml(
                                String.format(btnText + "<font color='#%s'>" + " Request new in " + pStatus + "sec</font>",
                                    сolorString)))
                        })
                        try {
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
//                            Log.e(TAG, "Exception:", e.getCause());
                            Thread.currentThread().interrupt()
                        }
                        pStatus -= 1
                    }
                    mHandler?.post(Runnable {
                        binding?.idResendText?.setEnabled(true)
                        binding?.idResendText?.setText(Html.fromHtml(String.format(btnText + "<font color='#%s'> " + getString(
                            R.string.resent_otp) + "</font>", сolorString)))
                    })
                }
            }).start()
        } catch (e: Exception) {
        }
    }
}