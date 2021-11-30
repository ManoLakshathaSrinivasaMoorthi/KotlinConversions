package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.dailytasksamplepoc.kotlinomnicure.activity.BaseActivity
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.customview.PinEntryView
import com.example.kotlinomnicure.databinding.ActivityOtpBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.OTPViewModel
import omnicurekotlin.example.com.userEndpoints.model.User

class OTPActivity : BaseActivity() {
    private val TAG = OTPActivity::class.java.simpleName
    private var binding: ActivityOtpBinding? = null
    private var viewModel: OTPViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        viewModel = ViewModelProvider(this).get(OTPViewModel::class.java)
        setView()
        var fromPage: String? = ""
        if (intent.hasExtra(Constants.IntentKeyConstants.FROM_PAGE)) {
            fromPage = intent.getStringExtra(Constants.IntentKeyConstants.FROM_PAGE)
        }
        if (fromPage == "splash" || fromPage == "login") {
            onResendOTP()
        }
    }

    private fun setView() {
        setOnclickListener()
        val mobile = intent.getStringExtra(Constants.IntentKeyConstants.MOBILE_NO)
        val otpMsg = getString(R.string.otp_msg)
        binding?.idChangeNumberTxt?.text = mobile
        //        SpannableStringBuilder builder = TextSpanBuilder.getPartialBoldText(otpMsg, otpMsg.length() - mobile.length(), otpMsg.length());
        binding?.otpMsg?.text = otpMsg
        val titleStr = getString(R.string.enter_phone_otp)
        //        builder = TextSpanBuilder.getPartialBoldText(titleStr, 5, titleStr.length());
        startResetTimer()
    }


    private fun startResetTimer() {
        binding?.idResendText?.isEnabled = false
        val btnText = getString(R.string.resent_code_text)
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        try {
            Thread(object : Runnable {
                var pStatus = 30
                override fun run() {
                    while (pStatus >= 0) {
                        mHandler!!.post { binding?.idResendText?.text = Html.fromHtml(String.format(btnText + "<font color='#%s'>" + " Request new in " + pStatus + "sec</font>", сolorString)) }
                        try {
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        pStatus -= 1
                    }
                    mHandler!!.post {
                        binding?.idResendText?.isEnabled = true
                        binding?.idResendText?.text = Html.fromHtml(String.format(btnText + "<font color='#%s'> " + getString(R.string.resent_otp) + "</font>", сolorString))
                    }
                }
            }).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setOnclickListener() {
        val uid = intent.getLongExtra(Constants.IntentKeyConstants.PROVIDER_ID, -1)

        binding?.idResendText?.setOnClickListener { onResendOTP() }
        binding?.pinEntryBorder?.setOnPinEnteredListener(object : PinEntryView.OnPinEnteredListener {


            override fun onPinEntered(pin: String?) {
                if (pin?.length == 4) {
                    hideSoftKeyboard()
                    onSubmitOTP(uid, pin)
                }
            }
        })
        binding?.pinEntryBorder?.setOnEditorActionListener(OnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                val pin = binding?.pinEntryBorder?.getText().toString()
                if (pin.length < 4) {
//                        UtilityMethods.showErrorSnackBar(binding.getRoot(), getString(R.string.invalid_otp), Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(binding?.root, this@OTPActivity, CustomSnackBar.WARNING, getString(R.string.invalid_otp), CustomSnackBar.TOP, 3000, 0)?.show()
                    return@OnEditorActionListener false
                }
                onSubmitOTP(uid, pin)
                return@OnEditorActionListener true
            }
            false
        })
        binding?.idBackButton?.setOnClickListener { finish() }
    }

    private fun onSubmitOTP(uid: Long, pin: String) {
        if (!UtilityMethods().isInternetConnected(this)!!) {
//            UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.root, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.verifyOTP.toString()))
        val fcm: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.FCM_TOKEN, "")
        Log.d(TAG, "onClickSignIn: fcm $fcm")
        viewModel?.submitOTP(uid, pin, fcm, Constants.OTPChannel.SMS.toString())!!.observe(this, { it ->
            val commonResponse=it.body()
             println("otp response $commonResponse")
            dismissProgressBar()
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                commonResponse.getUser()?.let { onOTPSuccessVerify(it) }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse?.getErrorMessage(), Constants.API.verifyOTP)
                //                    UtilityMethods.showErrorSnackBar(binding.getRoot(), errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let { CustomSnackBar.make(binding!!.root, this@OTPActivity, CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0) }!!.show()
            }
        })
    }

    private fun onResendOTP() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.root, this@OTPActivity, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)!!.show()
            return
        }
        showProgressBar(getString(R.string.sending_otp_msg))
        Log.d(TAG, "onClick Resend OTP ")
        val uid = intent.getLongExtra(Constants.IntentKeyConstants.PROVIDER_ID, -1)
        val mobile = intent.getStringExtra(Constants.IntentKeyConstants.MOBILE_NO)
        val countryCode = intent.getStringExtra(Constants.IntentKeyConstants.COUNTRY_CODE)
        mobile?.let {
            countryCode?.let { it1 ->
                viewModel?.resendOTP(uid, Constants.OTPChannel.SMS.toString(), it, it1)?.observe(this, {
                val commonResponse=it.body()
                dismissProgressBar()
                if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                    startResetTimer()
                } else {
                    val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse!!.getErrorMessage(), Constants.API.registrationPhoneOTP)
                    //                UtilityMethods.showErrorSnackBar(binding.getRoot(), errMsg, Snackbar.LENGTH_LONG);
                    errMsg?.let { CustomSnackBar.make(binding!!.root, this, CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0) }!!.show()
                }
            })
            }
        }
    }

    private fun onOTPSuccessVerify(provider: User) {
        val intent = Intent(this, EmailOTPActivity::class.java)
        intent.putExtra(Constants.IntentKeyConstants.PROVIDER_EMAIL, provider.getEmail())
        intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.getId())
        startActivity(intent)
    }
}
