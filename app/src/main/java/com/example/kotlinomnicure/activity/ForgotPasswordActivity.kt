package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityForgotPasswordBinding
import com.example.kotlinomnicure.helper.MobileNumberFormatter
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.ForgotPasswordViewModel
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeList
import omnicurekotlin.example.com.userEndpoints.model.ForgotPasswordRequest
import java.util.*

class ForgotPasswordActivity :BaseActivity() {
    private val TAG = ForgotPasswordActivity::class.java.simpleName
    private var binding: ActivityForgotPasswordBinding? = null
    private var viewModel: ForgotPasswordViewModel? = null
    private var context: Context =ForgotPasswordActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        initView()
        onClickListener()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        val labelColor = ContextCompat.getColor(this, R.color.btn_bg)
        val mColorString = String.format("%X", labelColor).substring(2)
        binding?.txtBackToSignIn?.setText(HtmlCompat
            .fromHtml(String.format("Back to" +
                    "<font color='#%s'><b> SIGN IN</b></font>",
                mColorString), Html.FROM_HTML_MODE_LEGACY))
        setCountryCode()
        binding?.txtResetPassword?.setEnabled(false)
        binding?.edtEmailAddress?.addTextChangedListener(ValidationTextWatcher(binding!!.edtEmailAddress))
        binding?.edtPhoneNumber?.addTextChangedListener(ValidationTextWatcher(binding!!.edtPhoneNumber))
    }

    private fun onClickListener() {
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        binding?.edtPhoneNumber?.addTextChangedListener(object : TextWatcher {
            val FIRST_SEP_LENGTH = 4
            val SECOND_SEP_LENGTH = 8
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // add code here
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence != null && !charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    val str = charSequence.toString()
                    MobileNumberFormatter().formatMobileNumber(str,
                        binding!!.edtPhoneNumber,
                        FIRST_SEP_LENGTH,
                        SECOND_SEP_LENGTH)
                }
            }

            override fun afterTextChanged(editable: Editable) {
                // add code here
            }
        })
        binding?.edtPhoneNumber?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (binding?.edtPhoneNumber?.getErrorMessage().equals("")) {
                    binding?.separator?.setBackgroundColor(ContextCompat.getColor(view.getContext(),
                        R.color.black))
                    binding?.phoneLayout?.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.border_black_edittext_bg))
                }
            } else {
                binding?.separator?.setBackgroundColor(ContextCompat.getColor(view.getContext(),
                    R.color.edittext_stroke_color))
                binding?.phoneLayout?.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ash_border_drawable_bg))
                binding?.edtPhoneNumber?.addTextChangedListener(GenericTextWatcher(binding?.edtPhoneNumber!!))
                checkPhone(true)
            }
        }
        binding?.edtEmailAddress?.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                binding?.edtEmailAddress?.addTextChangedListener(GenericTextWatcher(binding?.edtEmailAddress!!))
                checkEmail(true)
            }
        }
        binding?.radioGrp?.setOnCheckedChangeListener { group, checkedId ->
            if (binding?.rbEmailAddress?.isChecked() == true) {
                binding?.edtEmailAddress?.setVisibility(View.VISIBLE)
                binding?.phoneLayout?.setVisibility(View.GONE)
            } else if (binding?.rbPhoneNumber?.isChecked() == true) {
                binding?.phoneLayout?.setVisibility(View.VISIBLE)
                binding?.edtEmailAddress?.setVisibility(View.GONE)
            }
            checkButton()
        }
        binding?.txtResetPassword?.setOnClickListener { view -> doForgotPassword() }
        binding?.txtBackToSignIn?.setOnClickListener { view ->
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    fun checkButton() {
        val validEmail: Boolean =
            ValidationUtil().checkEmail(Objects.requireNonNull(binding?.edtEmailAddress?.getText())
                .toString()) == true
        val validPhone: Boolean =
            ValidationUtil().isValidPhone(Objects.requireNonNull(binding?.edtPhoneNumber?.getText())
                .toString())
        if (binding?.radioGrp?.getCheckedRadioButtonId()  == R.id.rbEmailAddress && validEmail) {
            binding?.txtResetPassword?.setEnabled(true)
        } else if (binding?.radioGrp?.getCheckedRadioButtonId()   == R.id.rbPhoneNumber && validPhone) {
            binding?.txtResetPassword?.setEnabled(true)
        } else {
            binding?.txtResetPassword?.setEnabled(false)
        }
    }

    private fun doForgotPassword() {
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
        showProgressBar()
        val forgotPasswordRequest = ForgotPasswordRequest()
        if (binding?.rbEmailAddress?.isChecked() == true) {
            if (!TextUtils.isEmpty(binding?.edtEmailAddress?.getText().toString())) {
                forgotPasswordRequest.setEmail(binding?.edtEmailAddress?.getText().toString()
                    .toLowerCase())
            }
        }
        if (binding?.rbPhoneNumber?.isChecked() == true && !TextUtils.isEmpty(binding?.edtPhoneNumber?.getText()
                .toString())
        ) {
            forgotPasswordRequest.setPhoneNumber(binding?.edtPhoneNumber?.getText().toString().trim()
                .replace("-", ""))
            forgotPasswordRequest.setCountryCode(binding?.spnCountryCode?.getSelectedItem()
                .toString())
        }
        forgotPasswordRequest.setToken(Constants.KeyHardcodeToken.HARD_CODE_TOKEN)

        viewModel?.forgotPassword(forgotPasswordRequest)?.observe(this,
            Observer<CommonResponse?> { commonResponse -> //
                dismissProgressBar()
                if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                    val intent = Intent(this,
                        ForgotPasswordOTPActivity::class.java)
                    if (binding?.rbEmailAddress?.isChecked() == true) {
                        intent.putExtra("rbEmailAddress", "Email")
                    }
                    if (binding?.rbPhoneNumber?.isChecked() == true) {
                        intent.putExtra("rbEmailAddress", "Phone")
                    }
                    intent.putExtra(Constants.IntentKeyConstants.EMAIL,
                        binding?.edtEmailAddress?.getText().toString().toLowerCase())
                    intent.putExtra(Constants.IntentKeyConstants.PHONE_NO,
                        binding?.edtPhoneNumber?.getText().toString().trim().replace("-", ""))
                    intent.putExtra(Constants.IntentKeyConstants.COUNTRY_CODE,
                        binding?.spnCountryCode?.getSelectedItem().toString())
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

        return TextUtils.isEmpty(errMsg)
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun setCountryCode() {
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


        //showProgressBar();
        val countryCode = ArrayList<String>()
        val providerMap = LinkedHashMap<String, String>()
        viewModel?.getCountry()?.observe(this) { response ->
            dismissProgressBar()
            //            Log.d(TAG, "country code data" + response.getStatus() + response);
            if (response != null && response.getStatus() != null && response.getStatus()!!) {
                if (response.getCountryCodeResponseList() != null && !response.getCountryCodeResponseList()!!
                        .isEmpty()
                ) {
                    providerMap.putAll(getCountryCodes(response.getCountryCodeResponseList()!! as List<CountryCodeList>)!!)
                    countryCode.addAll(providerMap.keys)
                    setCountrySpinner(countryCode)
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    java.lang.String.valueOf(response?.getErrorId()),
                    Constants.API.getHospital)
                CustomSnackBar.make(binding?.containerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            }
        }
    }

    private fun setCountrySpinner(countryCode: ArrayList<String>) {
        val spinnerArrayAdapter = ArrayAdapter(
            this, R.layout.spinner_custom_text, countryCode)
        binding?.spnCountryCode?.setAdapter(spinnerArrayAdapter)
        binding?.spnCountryCode?.setSelection(0)
        binding?.spnCountryCode?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                val spinnerText = view as TextView
                if (spinnerText != null) {
                    spinnerText.maxLines = 1
                    spinnerText.textSize = 16f
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(context,
                            spinnerText,
                            R.color.black)
                    } else {
                        UtilityMethods().setTextViewColor(context,
                            spinnerText,
                            R.color.title_black)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }

    private fun checkEmail(showError: Boolean) {
        if (binding?.let { ValidationUtil().checkEmail(it) } != null) {
            if (binding?.edtEmailAddress?.getText().toString().length > 0) {
                if (showError) {
                    ValidationUtil().checkEmail(binding!!)?.let {
                        binding?.edtEmailAddress?.setErrorMessage(it)
                    }
                }
                binding?.edtEmailAddress?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        } else {
            binding?.edtEmailAddress?.setErrorMessage("")
            binding?.edtEmailAddress?.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.ic_checkmark_edittext,
                0)
        }
    }

    private fun checkPhone(showError: Boolean) {
        if (binding?.let { ValidationUtil().checkPhoneNo(it) } != null) {
            if (binding?.edtPhoneNumber?.getText().toString().isNotEmpty()) {
                if (showError) {
                    ValidationUtil().checkPhoneNo(binding!!)?.let {
                        binding?.edtPhoneNumber?.setErrorMessage(it)
                    }
                    binding?.phoneLayout?.setBackground(resources.getDrawable(R.drawable.error_edittext_bg))
                    binding?.separator?.setBackgroundColor(ContextCompat.getColor(applicationContext,
                        R.color.red))
                }
                binding?.edtPhoneNumber?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        } else {
            binding?.edtPhoneNumber?.setErrorMessage("")
            binding?.separator?.setBackgroundColor(ContextCompat.getColor(applicationContext,
                R.color.edittext_stroke_color))
            binding?.phoneLayout?.setBackground(resources.getDrawable(R.drawable.ash_border_drawable_bg))
            binding?.edtPhoneNumber?.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.ic_checkmark_edittext,
                0)
        }
    }

    private fun getCountryCodes(providers: List<CountryCodeList>): LinkedHashMap<String, String>? {
        val providerMap = LinkedHashMap<String, String>()
        for (i in providers.indices) {
            val cc: CountryCodeList = providers[i]
            if (cc != null && cc.getId() != null) {
                providerMap[cc.getCode()!!.trim()] = cc.getId()!!
            }
        }
        return providerMap
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
            val str = s.toString()
            if (view.id == R.id.edtEmailAddress) {
                if (str.isNotEmpty() && str.contains(" ")) {
                   ForgotPasswordActivity().binding?.edtEmailAddress?.setText(s.toString().replace(" ", ""))
                    ForgotPasswordActivity(). binding?.edtEmailAddress?.getText()?.length?.let {
                        Selection.setSelection( ForgotPasswordActivity().binding?.edtEmailAddress?.getText(),
                            it)
                    }
                }
            }
        }

        override fun afterTextChanged(editable: Editable) {
            val id = view.id
            if (id == R.id.edtEmailAddress) {
                ForgotPasswordActivity().checkEmail(false)
            } else if (id == R.id.edtPhoneNumber) {
                ForgotPasswordActivity().checkPhone(false)
            }
            ForgotPasswordActivity().checkButton()
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
            when (view.id) {
                R.id.edtEmailAddress ->  ForgotPasswordActivity().checkEmail(true)
                R.id.edtPhoneNumber ->  ForgotPasswordActivity().checkPhone(true)
            }
        }

        init {
            this.view = view
        }
    }
}