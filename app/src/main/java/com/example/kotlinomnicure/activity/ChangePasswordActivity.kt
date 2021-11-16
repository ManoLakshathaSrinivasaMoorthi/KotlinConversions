package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityChangePasswordBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.ChangePasswordViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.userEndpoints.model.ResetPasswordRequest

class ChangePasswordActivity : BaseActivity() {
    private val TAG = ChangePasswordActivity::class.java.simpleName
    private var binding: ActivityChangePasswordBinding? = null
    private var viewModel: ChangePasswordViewModel? = null
    private var emailID: String? = null
    private  var phoneNumber:kotlin.String? = null
    private  var countryCode:kotlin.String? = null
    private var otpToken: String? = null
    private var emailAddress: String? = ""
    private var strFirstName: String? = null
    private  var strLastName:kotlin.String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        viewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel::class.java)
        initClickListener()
        initViews()
//        Toast.makeText(this, "CHANGEPASSWORDPAGE", Toast.LENGTH_SHORT).show();
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun initViews() {
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding?.alreadySigninText?.setText(
            Html.fromHtml(String.format(
                    "Back to" + "<font color='#%s'><b>" + " SIGN IN</b></font>", сolorString)))
        emailAddress = intent.getStringExtra("rbEmailAddress")
        if (intent.hasExtra(Constants.IntentKeyConstants.OTP_TOKEN)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.OTP_TOKEN))) {
            otpToken = intent.getStringExtra(Constants.IntentKeyConstants.OTP_TOKEN)
        }
        if (intent.hasExtra(Constants.IntentKeyConstants.EMAIL)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.EMAIL))
        ) {
            emailID = intent.getStringExtra(Constants.IntentKeyConstants.EMAIL)
        }
        if (intent.hasExtra(Constants.IntentKeyConstants.FIRST_NAME)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.FIRST_NAME))
        ) {
            strFirstName = intent.getStringExtra(Constants.IntentKeyConstants.FIRST_NAME)
        }
        if (intent.hasExtra(Constants.IntentKeyConstants.LAST_NAME)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.LAST_NAME))
        ) {
            strLastName = intent.getStringExtra(Constants.IntentKeyConstants.LAST_NAME)
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
        binding?.edtNewPassword?.setTypeface(Typeface.DEFAULT)
        binding?.edtConfirmPassword?.setTypeface(Typeface.DEFAULT)
        Log.d(TAG, "ChangePasswordActivity : $emailID ----- $strFirstName ----- $strLastName")

//        binding.edtNewPassword.addTextChangedListener(new GenericTextWatcher(binding.edtNewPassword));
//        binding.edtConfirmPassword.addTextChangedListener(new GenericTextWatcher(binding.edtConfirmPassword));
        binding?.txtSavePassword?.setOnClickListener(View.OnClickListener { doResetPassword() })
        binding?.edtNewPassword?.addTextChangedListener(ValidationTextWatcher(binding!!.edtNewPassword))
        binding?.edtConfirmPassword?.addTextChangedListener(ValidationTextWatcher(binding!!.edtConfirmPassword))
        binding?.txtSavePassword?.setEnabled(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListener() {
        binding?.alreadySigninText?.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "onClick: of already signin ")
            val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })
        binding?.idBackButton?.setOnClickListener(View.OnClickListener { finish() })
        binding?.passwordInfo?.setOnClickListener(View.OnClickListener {
            strFirstName?.let { it1 ->
                strLastName?.let { it2 ->
                    emailID?.let { it3 ->
                        ValidationUtil().showPasswordValidationDialog(
                            this@ChangePasswordActivity, binding!!.edtNewPassword.getText().toString(),
                            it1, it2, it3
                        )
                    }
                }
            }
        })
        binding?.confirmPasswordInfo?.setOnClickListener(View.OnClickListener {
            strFirstName?.let { it1 ->
                strLastName?.let { it2 ->
                    emailID?.let { it3 ->
                        ValidationUtil().showPasswordValidationDialog(
                            this@ChangePasswordActivity, binding?.edtConfirmPassword?.getText().toString(),
                            it1, it2, it3
                        )
                    }
                }
            }
        })
        binding?.edtNewPassword?.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (binding?.edtNewPassword?.getErrorMessage().equals("")) {
                    binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.border_black_edittext_bg))
                }
            } else {
                binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
                binding?.edtNewPassword?.addTextChangedListener(GenericTextWatcher(binding!!.edtNewPassword))
                checkNewPassword(true)
            }
        })
        binding?.edtConfirmPassword?.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (binding?.edtConfirmPassword?.getErrorMessage().equals("")) {
                    binding?.confirmPasswordLayout?.setBackground(resources.getDrawable(R.drawable.border_black_edittext_bg))
                }
            } else {
                binding?.confirmPasswordLayout?.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
                binding?.edtConfirmPassword?.addTextChangedListener(GenericTextWatcher(binding!!.edtConfirmPassword))
                checkConfirmPassword(true)
            }
        })
        binding?.passwordVisibility?.setOnClickListener(View.OnClickListener {
            ValidationUtil().passwordVisibility(binding!!.edtNewPassword, binding!!.passwordVisibility)
        })
        binding?.confirmPasswordVisibility?.setOnClickListener(View.OnClickListener {
            ValidationUtil().passwordVisibility(binding!!.edtConfirmPassword, binding!!.confirmPasswordVisibility)
        })
    }

    fun checkButton() {
        val validPass: Boolean = strFirstName?.let {
            strLastName?.let { it1 ->
                emailID?.let { it2 ->
                    ValidationUtil().checkPasswordValid(
                        binding?.edtNewPassword?.getText().toString(),
                        it, it1, it2
                    )
                }
            }
        } == true
        val validNewPass: Boolean = strFirstName?.let {
            strLastName?.let { it1 ->
                emailID?.let { it2 ->
                    ValidationUtil().checkPasswordValid(
                        binding?.edtConfirmPassword?.getText().toString(),
                        it, it1, it2
                    )
                }
            }
        } == true
        if (validPass && validNewPass) {
            binding?.txtSavePassword?.setEnabled(true)
        } else {
            binding?.txtSavePassword?.setEnabled(false)
        }
    }

    fun isValidNewPass(): Boolean? {
        return if (binding?.edtNewPassword?.getText().toString()
                .equals(binding?.edtConfirmPassword?.getText().toString())
        ) {
            true
        } else false
    }

    private fun doResetPassword() {
        handleMultipleClick(binding!!.txtSavePassword)
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.changePassword.toString()))
        val resetPasswordRequest = ResetPasswordRequest()
        if (emailAddress == "Email") {
            resetPasswordRequest.setEmail(emailID)
            resetPasswordRequest.setPasswordNew(binding?.edtNewPassword?.getText().toString())
            //            resetPasswordRequest.setToken(Constants.KeyHardcodeToken.HARD_CODE_TOKEN);
            resetPasswordRequest.setToken(otpToken)
            //            resetPasswordRequest.setValidateKey(Constants.ValidateKey.ValKeyZero);
        }
        if (emailAddress == "Phone") {
            resetPasswordRequest.setPhoneNumber(phoneNumber)
            resetPasswordRequest.setCountryCode(countryCode)
            resetPasswordRequest.setPasswordNew(binding?.edtNewPassword?.getText().toString())
            //            resetPasswordRequest.setToken(Constants.KeyHardcodeToken.HARD_CODE_TOKEN);
            resetPasswordRequest.setToken(otpToken)
            //            resetPasswordRequest.setValidateKey(Constants.ValidateKey.ValKeyZero);
        }
        viewModel?.changePassword(resetPasswordRequest).observe(this) { commonResponse ->
            dismissProgressBar()
            Log.d(TAG, "Request " + Gson().toJson(resetPasswordRequest))
            Log.d(TAG, "Response " + Gson().toJson(commonResponse))
            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                val finerprintstate: Boolean = PrefUtility().getBooleanInPref(
                    this@ChangePasswordActivity,
                    Constants.SharedPrefConstants.FINGERPRINTFLAG,
                    false
                )
                if (finerprintstate) {
                    val email: String? = PrefUtility().getStringInPref(
                        this@ChangePasswordActivity,
                        Constants.SharedPrefConstants.EMAIL,
                        ""
                    )
                    if (email == emailID) {
                        EncUtil.generateKey(this@ChangePasswordActivity)
                        val encryptpassword: String = EncUtil.encrypt(
                            this@ChangePasswordActivity,
                            binding.edtNewPassword.getText().toString()
                        )
                        PrefUtility().saveStringInPref(
                            this@ChangePasswordActivity,
                            Constants.SharedPrefConstants.PASSWORD,
                            encryptpassword
                        )
                    }
                } else {
                }
                onResetPasswordSuccessNew(commonResponse)
            } else {
                binding?.txtSavePassword?.setEnabled(true)
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this@ChangePasswordActivity,
                    commonResponse.getErrorMessage(),
                    Constants.API.register
                )
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    binding?.idContainerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg!!,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
            }
        }
    }

    fun onResetPasswordSuccessNew(response: CommonResponse?) {
        val intent = Intent(this@ChangePasswordActivity, ResetSuccessActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun onResetPasswordSuccess(response: CommonResponse?) {
        val builder = AlertDialog.Builder(this@ChangePasswordActivity, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.custom_alert_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.text = getString(R.string.success)
        alertMsg.text = getString(R.string.password_changed_successfully)
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        buttonOk.setOnClickListener { //                launchLoginActivity();
            alertDialog.dismiss()
            val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        alertDialog.show()
    }

    fun checkNewPassword(showError: Boolean) {
        if (strFirstName?.let {
                strLastName?.let { it1 ->
                    emailID?.let { it2 ->
                        ValidationUtil().checkPassword(
                            binding?.edtNewPassword?.getText().toString(), binding,
                            it, it1, it2
                        )
                    }
                }
            } != null
        ) {
            if (showError) {
                binding?.edtNewPassword?.setErrorMessage(
                    strLastName?.let {
                        emailID?.let { it1 ->
                            ValidationUtil().checkPassword(
                                binding?.edtNewPassword?.getText().toString(), binding,
                                strFirstName!!, it, it1
                            )
                        }
                    }
                )
                binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.error_edittext_bg))
                binding?.passwordInfo?.setVisibility(View.VISIBLE)
            }
            binding?.passwordVerified?.setVisibility(View.GONE)
        } else {
            binding?.edtNewPassword?.setErrorMessage("")
            binding?.passwordLayout?.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
            binding?.passwordInfo?.setVisibility(View.GONE)
            binding?.passwordVerified?.setVisibility(View.VISIBLE)
        }
    }

    fun checkConfirmPassword(showError: Boolean) {
        if (binding?.edtNewPassword?.getText().toString().length > 0) {
            if (strFirstName?.let {
                    strLastName?.let { it1 ->
                        emailID?.let { it2 ->
                            ValidationUtil().checkPassword(
                                binding?.edtConfirmPassword?.getText().toString(), binding,
                                it, it1, it2
                            )
                        }
                    }
                } != null
            ) {
                if (showError) {
                    binding?.edtConfirmPassword?.setErrorMessage(
                        strLastName?.let {
                            emailID?.let { it1 ->
                                ValidationUtil().checkPassword(
                                    binding?.edtConfirmPassword?.getText().toString(), binding,
                                    strFirstName!!, it, it1
                                )
                            }
                        }
                    )
                    binding?.confirmPasswordLayout?.setBackground(resources.getDrawable(R.drawable.error_edittext_bg))
                    binding?.confirmPasswordInfo?.setVisibility(View.VISIBLE)
                }
                binding?.confirmPasswordVerified?.setVisibility(View.GONE)
            } else if (!binding?.edtNewPassword?.getText().toString()
                    .equals(binding?.edtConfirmPassword?.getText().toString())
            ) {
                if (showError) {
                    binding?.edtConfirmPassword?.setErrorMessage(getString(R.string.passwords_do_not_match))
                    binding?.confirmPasswordLayout?.setBackground(resources.getDrawable(R.drawable.error_edittext_bg))
                    binding?.confirmPasswordInfo?.setVisibility(View.VISIBLE)
                }
                binding?.confirmPasswordVerified?.setVisibility(View.GONE)
            } else {
                binding?.edtConfirmPassword?.setErrorMessage("")
                binding?.confirmPasswordLayout?.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
                binding?.confirmPasswordInfo?.setVisibility(View.GONE)
                binding?.confirmPasswordVerified?.setVisibility(View.VISIBLE)
            }
        }
    }

    private fun isValid(): Boolean {
        val errMsg: String? = ValidationUtil().isValidate(binding!!)
        return if (!TextUtils.isEmpty(errMsg)) {
            //            UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
            false
        } else true
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.edtNewPassword -> ChangePasswordActivity().checkNewPassword(false)
                R.id.edtConfirmPassword ->ChangePasswordActivity(). checkConfirmPassword(false)
            }
            ChangePasswordActivity().checkButton()
        }

        init {
            this.view = view
        }
    }

    private class GenericTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            val id = view.id
            if (id == R.id.edtNewPassword) {
                ChangePasswordActivity().checkNewPassword(true)
            } else if (id == R.id.edtConfirmPassword) {
                ChangePasswordActivity().checkConfirmPassword(true)
            }
        }

        init {
            this.view = view
        }
    }
}