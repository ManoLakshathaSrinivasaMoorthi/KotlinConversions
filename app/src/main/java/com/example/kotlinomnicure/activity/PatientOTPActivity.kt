package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityPatientOtpBinding
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.PatientOTPViewModel
import com.example.kotlinomnicure.customview.PinEntryView
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.TextSpanBuilder
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.PatientOtpModel
import java.lang.Exception

class PatientOTPActivity : BaseActivity() {
    private val TAG = PatientOTPActivity::class.java.simpleName
    private var binding: ActivityPatientOtpBinding? = null
    private var viewModel: PatientOTPViewModel? = null
    private var patientID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_otp)
        viewModel = ViewModelProvider(this).get(PatientOTPViewModel::class.java)
        setView()
    }

    private fun setView() {
        setOnclickListener()
        val mobile = intent.getStringExtra(Constants.IntentKeyConstants.MOBILE_NO)
        val otpMsg = getString(R.string.otp_msg)
        binding?.idChangeNumberTxt?.setText(mobile)
        var builder: SpannableStringBuilder? = TextSpanBuilder().getPartialBoldText(
            otpMsg,
            otpMsg.length - mobile!!.length,
            otpMsg.length
        )
        binding?.otpMsg?.setText(builder)
        val titleStr = getString(R.string.enter_phone_otp)
        builder = TextSpanBuilder().getPartialBoldText(titleStr, 5, titleStr.length)
        binding?.title?.setText(builder)
        if (intent != null) {
            patientID = intent.getLongExtra(Constants.IntentKeyConstants.PATIENT_ID, -1)
        }
        startResetTimer()
    }

    private fun startResetTimer() {
        binding?.idResendCode?.setEnabled(false)
        val btnText = getString(R.string.resent_code)
        try {
            Thread(object : Runnable {
                var pStatus = 30
                override fun run() {
                    while (pStatus >= 0) {
                        mHandler?.post(Runnable { binding?.idResendCode?.setText("$btnText ($pStatus)") })
                        try {
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            Log.e(TAG, "Exception:", e.cause)
                            Thread.currentThread().interrupt()
                        }
                        pStatus -= 1
                    }
                    mHandler?.post(Runnable {
                        binding?.idResendCode?.setEnabled(true)
                        binding?.idResendCode?.setText(getString(R.string.resent_otp))
                    })
                }
            }).start()
        } catch (e: Exception) {
            Log.e(TAG, "Exception:", e.cause)
        }
    }

    private fun setOnclickListener() {
        binding?.pinEntryBorder?.setOnPinEnteredListener(object :
            PinEntryView.OnPinEnteredListener {
            override fun onPinEntered(pin: String?) {
                if (pin?.length == 4) {
                    hideSoftKeyboard()
                    onSubmitOTP(patientID, pin)
                }
            }

        })
        binding?.pinEntryBorder?.setOnEditorActionListener(OnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                val pin: String = binding?.pinEntryBorder?.getText().toString()
                if (pin.length < 4) {
                    CustomSnackBar.make(
                        binding?.getRoot(),
                        this,
                        CustomSnackBar.WARNING,
                        getString(R.string.invalid_otp),
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                    return@OnEditorActionListener false
                }
                onSubmitOTP(patientID, pin)
                return@OnEditorActionListener true
            }
            false
        })
        binding?.idBackButton?.setOnClickListener(View.OnClickListener { finish() })


    }

    private fun onSubmitOTP(patientId: Long, pin: String) {
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.verifyOTP.toString()))
        val otpModel = PatientOtpModel()
        otpModel.setId(patientId.toString())
        otpModel.setSmsOtp(pin)
        Log.d(TAG, "Request otp" + Gson().toJson(otpModel))
        viewModel?.submitOTP(otpModel)?.observe(this) { commonResponse ->
            Log.d(TAG, "Response otp$commonResponse")
            dismissProgressBar()
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                onOTPSuccessVerify(commonResponse)
            } else {
                Log.d(TAG, "Error otp" + commonResponse?.getErrorMessage())
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    commonResponse?.getErrorMessage(),
                    Constants.API.register
                )

                if (errMsg != null) {
                    CustomSnackBar.make(
                        binding?.getRoot(),
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
    }

    private fun onOTPSuccessVerify(response: CommonResponse) {
        val intent = Intent(this, AppointmentSuccessActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}