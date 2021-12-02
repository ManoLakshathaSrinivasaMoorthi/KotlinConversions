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
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityChangePasswordBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.ValidationUtil
import com.example.kotlinomnicure.viewmodel.ChangePasswordViewModel
import java.util.*

class ExpirePasswordActivity : AppCompatActivity() {
    private val TAG = ExpirePasswordActivity::class.java.simpleName
    private var binding: ActivityChangePasswordBinding? = null
    private var viewModel: ChangePasswordViewModel? = null
    private var emailID: String? = null
    private var strFirstName: String? = null
    private var strLastName: String? = null
    private var phoneNumber: String? = null
    private var countryCode: String? = null
    private var otpToken: String? = null
    private var emailAddress: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
        initClickListener()
        initViews()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("WrongConstant")
    private fun initViews() {
        val labelColor = ContextCompat.getColor(this, R.color.btn_bg)
        val mColorString = String.format("%X", labelColor).substring(2)
        binding?.alreadySigninText?.setText(HtmlCompat.fromHtml(String.format("Back to" + "<font color='#%s'><b>" + " SIGN IN</b></font>",
            mColorString), Html.FROM_HTML_MODE_LEGACY))
        binding?.resetPasswordTitle?.setText(getString(R.string.expired_password_title))
        binding?.resetPasswordDesc?.setText(getString(R.string.expired_password_subtext))
        emailAddress = intent.getStringExtra("rbEmailAddress")
        if (intent.hasExtra(Constants.IntentKeyConstants.OTP_TOKEN)
            && !TextUtils.isEmpty(intent.getStringExtra(Constants.IntentKeyConstants.OTP_TOKEN))
        ) {
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
        binding?.txtSavePassword?.setOnClickListener { v -> doResetPassword() }
        binding?.edtNewPassword?.addTextChangedListener(binding?.edtNewPassword?.let {
            ValidationTextWatcher(it)
        })
        binding?.edtConfirmPassword?.addTextChangedListener(binding?.edtConfirmPassword?.let {
            ValidationTextWatcher(it)
        })
        binding?.txtSavePassword?.setEnabled(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListener() {
        binding?.alreadySigninText.setOnClickListener { view ->
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        binding?.passwordInfo?.setOnClickListener { view ->
            strFirstName?.let {
                ValidationUtil().showPasswordValidationDialog(this,
                    binding?.edtNewPassword.getText().toString(),
                    it,
                    strLastName,
                    emailID)
            }
        }
        binding.confirmPasswordInfo.setOnClickListener { view ->
            ValidationUtil.showPasswordValidationDialog(this@ExpirePasswordActivity,
                binding.edtConfirmPassword.getText().toString(),
                strFirstName,
                strLastName,
                emailID)
        }
        binding.edtNewPassword.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (binding.edtNewPassword.getErrorMessage().equals("")) {
                    binding.passwordLayout.setBackground(resources.getDrawable(R.drawable.border_black_edittext_bg))
                }
            } else {
                binding.passwordLayout.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
                binding.edtNewPassword.addTextChangedListener(GenericTextWatcher(binding.edtNewPassword))
                checkNewPassword(true)
            }
        }
        binding.edtConfirmPassword.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (binding.edtConfirmPassword.getErrorMessage().equals("")) {
                    binding.confirmPasswordLayout.setBackground(resources.getDrawable(R.drawable.border_black_edittext_bg))
                }
            } else {
                binding.confirmPasswordLayout.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
                binding.edtConfirmPassword.addTextChangedListener(GenericTextWatcher(binding.edtConfirmPassword))
                checkConfirmPassword(true)
            }
        }
        binding.passwordVisibility.setOnClickListener { view ->
            ValidationUtil.passwordVisibility(binding.edtNewPassword,
                binding.passwordVisibility)
        }
        binding.confirmPasswordVisibility.setOnClickListener { view ->
            ValidationUtil.passwordVisibility(binding.edtConfirmPassword,
                binding.confirmPasswordVisibility)
        }
    }

    fun checkButton() {
        val validPass: Boolean =
            ValidationUtil.checkPasswordValid(binding.edtNewPassword.getText().toString(),
                strFirstName, strLastName, emailID)
        val validNewPass: Boolean =
            ValidationUtil.checkPasswordValid(binding.edtConfirmPassword.getText().toString(),
                strFirstName, strLastName, emailID)
        binding.txtSavePassword.setEnabled(validPass && validNewPass)
    }

    private fun doResetPassword() {
        handleMultipleClick(binding.txtSavePassword)
        if (!isValid()) {
            return
        }
        if (!UtilityMethods.isInternetConnected(this)) {
            CustomSnackBar.make(binding.idContainerLayout,
                this@ExpirePasswordActivity,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0).show()
            return
        }
        showProgressBar(PBMessageHelper.getMessage(this, Constants.API.changePassword.toString()))
        val resetPasswordRequest = ResetPasswordRequest()
        if (emailAddress == "Email") {
            resetPasswordRequest.setEmail(emailID)
            resetPasswordRequest.setPasswordNew(binding.edtNewPassword.getText().toString())
            //            resetPasswordRequest.setToken(Constants.KeyHardcodeToken.HARD_CODE_TOKEN);
            resetPasswordRequest.setToken(otpToken)
            //            resetPasswordRequest.setValidateKey(Constants.ValidateKey.ValKeyZero);
        } else if (emailAddress == "Phone") {
            resetPasswordRequest.setPhoneNumber(phoneNumber)
            resetPasswordRequest.setCountryCode(countryCode)
            resetPasswordRequest.setPasswordNew(binding.edtNewPassword.getText().toString())
            //            resetPasswordRequest.setToken(Constants.KeyHardcodeToken.HARD_CODE_TOKEN);
            resetPasswordRequest.setToken(otpToken)
            //            resetPasswordRequest.setValidateKey(Constants.ValidateKey.ValKeyZero);
        }
        viewModel.changePassword(resetPasswordRequest).observe(this) { commonResponse ->
            dismissProgressBar()
            //            Log.d(TAG, "Request " + new Gson().toJson(resetPasswordRequest));
//            Log.d(TAG, "Response " + new Gson().toJson(commonResponse));
            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                val finerprintstate: Boolean =
                    PrefUtility.getBooleanInPref(this@ExpirePasswordActivity,
                        Constants.SharedPrefConstants.FINGERPRINTFLAG,
                        false)
                if (finerprintstate) {
                    val email: String = PrefUtility.getStringInPref(this@ExpirePasswordActivity,
                        Constants.SharedPrefConstants.EMAIL,
                        "")
                    if (email == emailID) {
                        EncUtil.generateKey(this@ExpirePasswordActivity)
                        val encryptpassword: String =
                            EncUtil.encrypt(this@ExpirePasswordActivity,
                                binding.edtNewPassword.getText().toString())
                        PrefUtility.saveStringInPref(this@ExpirePasswordActivity,
                            Constants.SharedPrefConstants.PASSWORD,
                            encryptpassword)
                    }
                }
                onResetPasswordSuccessNew()
            } else if (!TextUtils.isEmpty(commonResponse.getErrorMessage()) && commonResponse.getErrorMessage() != null) {
                binding.txtSavePassword.setEnabled(true)
                val errMsg: String = ErrorMessages.getErrorMessage(this@ExpirePasswordActivity,
                    commonResponse.getErrorMessage(),
                    Constants.API.register)
                CustomSnackBar.make(binding.idContainerLayout, this@ExpirePasswordActivity,
                    CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0).show()
            } else {
                CustomSnackBar.make(binding.idContainerLayout,
                    this@ExpirePasswordActivity,
                    CustomSnackBar.WARNING,
                    getString(R.string.api_error),
                    CustomSnackBar.TOP,
                    3000,
                    0)
                    .show()
            }
        }
    }

    fun onResetPasswordSuccessNew() {
        val intent = Intent(this@ExpirePasswordActivity, ResetSuccessActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun onResetPasswordSuccess() {
        val builder = AlertDialog.Builder(this@ExpirePasswordActivity, R.style.CustomAlertDialog)
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
        buttonOk.setOnClickListener { v: View? ->
            alertDialog.dismiss()
            val intent = Intent(this@ExpirePasswordActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        alertDialog.show()
    }

    fun checkNewPassword(showError: Boolean) {
        if (ValidationUtil.checkPassword(Objects.requireNonNull(binding.edtNewPassword.getText())
                .toString(), binding,
                strFirstName, strLastName, emailID) != null
        ) {
            if (showError) {
                binding.edtNewPassword.setErrorMessage(Objects.requireNonNull(ValidationUtil.checkPassword(
                    binding.edtNewPassword.getText().toString(),
                    binding,
                    strFirstName,
                    strLastName,
                    emailID)))
                binding.passwordLayout.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.error_edittext_bg))
                binding.passwordInfo.setVisibility(View.VISIBLE)
            }
            binding.passwordVerified.setVisibility(View.GONE)
        } else {
            binding.edtNewPassword.setErrorMessage("")
            binding.passwordLayout.setBackground(ContextCompat.getDrawable(this,
                R.drawable.ash_border_drawable_bg))
            binding.passwordInfo.setVisibility(View.GONE)
            binding.passwordVerified.setVisibility(View.VISIBLE)
        }
    }

    fun checkConfirmPassword(showError: Boolean) {
        if (Objects.requireNonNull(binding.edtNewPassword.getText()).toString().length > 0) {
            if (ValidationUtil().checkPassword(Objects.requireNonNull(binding.edtConfirmPassword.getText())
                    .toString(), binding,
                    strFirstName, strLastName, emailID) != null
            ) {
                if (showError) {
                    binding.edtConfirmPassword.setErrorMessage(Objects.requireNonNull(ValidationUtil().checkPassword(
                        binding.edtConfirmPassword.getText().toString(),
                        binding,
                        strFirstName,
                        strLastName,
                        emailID)))
                    binding.confirmPasswordLayout.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.error_edittext_bg))
                    binding.confirmPasswordInfo.setVisibility(View.VISIBLE)
                }
                binding.confirmPasswordVerified.setVisibility(View.GONE)
            } else if (!binding.edtNewPassword.getText().toString()
                    .equals(binding.edtConfirmPassword.getText().toString())
            ) {
                if (showError) {
                    binding.edtConfirmPassword.setErrorMessage(getString(R.string.passwords_do_not_match))
                    binding.confirmPasswordLayout.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.error_edittext_bg))
                    binding.confirmPasswordInfo.setVisibility(View.VISIBLE)
                }
                binding.confirmPasswordVerified.setVisibility(View.GONE)
            } else {
                binding.edtConfirmPassword.setErrorMessage("")
                binding.confirmPasswordLayout.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ash_border_drawable_bg))
                binding.confirmPasswordInfo.setVisibility(View.GONE)
                binding.confirmPasswordVerified.setVisibility(View.VISIBLE)
            }
        }
    }

    private fun isValid(): Boolean {
        val errMsg: String = ValidationUtil.isValidate(binding)
        return TextUtils.isEmpty(errMsg)
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler.postDelayed({ view.isEnabled = true }, 500)
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            // add code here
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            // add code here
        }

        override fun afterTextChanged(editable: Editable) {
            val id = view.id
            if (id == R.id.edtNewPassword) {
                checkNewPassword(false)
            } else if (id == R.id.edtConfirmPassword) {
                checkConfirmPassword(false)
            }
            checkButton()
        }

        init {
            this.view = view
        }
    }

    private class GenericTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            // add code here
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            // add code here
        }

        override fun afterTextChanged(editable: Editable) {
            val id = view.id
            if (id == R.id.edtNewPassword) {
                checkNewPassword(true)
            } else if (id == R.id.edtConfirmPassword) {
                checkConfirmPassword(true)
            }
        }

        init {
            this.view = view
        }
    }
}