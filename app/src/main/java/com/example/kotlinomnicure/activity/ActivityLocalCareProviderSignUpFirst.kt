package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityLocalCareProviderSignUpFirstBinding
import com.example.kotlinomnicure.helper.MobileNumberFormatter
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.LocalCareProviderSignUpFirstViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeList
import omnicurekotlin.example.com.userEndpoints.model.Provider
import java.util.*


@Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")
 class ActivityLocalCareProviderSignUpFirst : BaseActivity() {

    private val TAG = ActivityLocalCareProviderSignUpFirst::class.java.simpleName
    protected var binding: ActivityLocalCareProviderSignUpFirstBinding? = null
    protected var viewModel: LocalCareProviderSignUpFirstViewModel? = null
    var role: String? = null
    private var sequenceWordList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_care_provider_sign_up_first)
        viewModel = ViewModelProvider(this).get(LocalCareProviderSignUpFirstViewModel::class.java)
        setView()
    }

    private fun setView() {
        setOnclickListener()
        setCountryCode()
        ArrayListSequenceWord()
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding?.alreadySigninText?.text = Html.fromHtml(String.format("Already have account?" + "<font color='#%s'><b>" + " LOG IN</b></font>", сolorString))
        binding?.alreadySigninText?.setOnClickListener {
            Log.d(TAG, "onClick: of already signin ")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        val extras = intent.extras
        role = extras!!.getString(Constants.SharedPrefConstants.ROLE, null)
        if (role != null && role == "RP") {
            binding?.idCreateProfileTxt?.text = "Sign up as Remote Provider"
        } else {
            binding?.idCreateProfileTxt?.text = "Sign up as Local Care Provider"
        }
        binding?.idPassword?.typeface = Typeface.DEFAULT
        binding?.idFirstName?.addTextChangedListener(ValidationTextWatcher(binding!!.idFirstName))
        binding?.idLastName?.addTextChangedListener(ValidationTextWatcher(binding!!.idLastName))
        binding?.idEmailId?.addTextChangedListener(ValidationTextWatcher(binding!!.idEmailId))
        binding?.idPassword?.addTextChangedListener(ValidationTextWatcher(binding!!.idPassword))
        binding?.idPhoneNumber?.addTextChangedListener(ValidationTextWatcher(binding!!.idPhoneNumber))
        checkButton()
    }

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    private fun setOnclickListener() {
        binding?.idPhoneNumber?.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (binding?.idPhoneNumber?.getErrorMessage() === "") {
                    binding?.seperator?.setBackgroundColor(ContextCompat.getColor(view.context, R.color.black))
                    binding?.phoneLayout?.background = resources.getDrawable(R.drawable.border_black_edittext_bg)
                }
            } else {
                binding?.seperator?.setBackgroundColor(ContextCompat.getColor(view.context, R.color.edittext_stroke_color))
                binding?.phoneLayout?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
                binding?.idPhoneNumber?.addTextChangedListener(GenericTextWatcher(binding?.idPhoneNumber!!))
                checkPhone(true)
            }
        }
        binding?.idPassword?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding?.idPassword?.getErrorMessage() === "") {
                    binding?.passwordLayout?.background = resources.getDrawable(R.drawable.border_black_edittext_bg)
                }
            } else {
                binding?.passwordLayout?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
                binding?.idPassword?.addTextChangedListener(GenericTextWatcher(binding!!.idPassword))
                checkPassword(true)
            }
        }
        binding?.idEmailId?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding?.idEmailId?.addTextChangedListener(GenericTextWatcher(binding!!.idEmailId))
                checkEmail(true)
            }
        }
        binding?.idFirstName?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding?.idFirstName?.addTextChangedListener(GenericTextWatcher(binding!!.idFirstName))
                checkFirstName(true)
            }
        }
        binding?.idLastName?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding!!.idLastName.addTextChangedListener(GenericTextWatcher(binding!!.idLastName))
                checkLastName(true)
            }
        }
        binding?.idBackButton?.setOnClickListener { finish() }
        binding?.idPassword?.setOnEditorActionListener { _, id, _ ->
            if (id === EditorInfo.IME_ACTION_DONE) {
                onClickNext()
                return@setOnEditorActionListener true
            }
            false
        }
        binding?.idPhoneNumber?.addTextChangedListener(object : TextWatcher {
            val FIRST_SEP_LENGTH = 4
            val SECOND_SEP_LENGTH = 8
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    return
                } else {
                    val str = charSequence.toString()
                    MobileNumberFormatter().formatMobileNumber(str, binding?.idPhoneNumber!!, FIRST_SEP_LENGTH, SECOND_SEP_LENGTH)
                }
            }

            override fun afterTextChanged(editable: Editable) {
//                if (editable == null || editable.toString().trim().isEmpty()) {
//                    binding.spnCountryCode.setVisibility(View.GONE);
//                } else {
//                    binding.spnCountryCode.setVisibility(View.VISIBLE);
//                }
            }
        })
        binding?.passwordVisibility?.setOnClickListener { ValidationUtil().passwordVisibility(binding!!.idPassword, binding!!.passwordVisibility) }
        binding?.passwordInfo?.setOnClickListener {
            ValidationUtil().showPasswordValidationDialog(this@ActivityLocalCareProviderSignUpFirst,
                    binding!!.idPassword.text.toString(), binding!!.idFirstName.text.toString(), binding!!.idLastName.text.toString(), binding!!.idEmailId.text.toString())
        }
        //        binding.idPassword.setOnTouchListener((v, event) -> {
//
//            final int DRAWABLE_RIGHT = 2;
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                if (event.getRawX() >= (binding.idPassword.getRight() - binding.idPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                    if (binding.idPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
//                        binding.idPassword.setInputType(InputType.TYPE_CLASS_TEXT |
//                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                        binding.idPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
//                        binding.idPassword.setSelection(binding.idPassword.getText().length());
//                    } else {
//                        binding.idPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                        binding.idPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
//                        binding.idPassword.setSelection(binding.idPassword.getText().length());
//                    }
//                    return true;
//                }
//            }
//            return false;
//        });
        binding?.btnNext?.setOnClickListener { onClickNext() }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun onClickNext() {
        handleMultipleClick(binding!!.btnNext)
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)!!.show()
            return
        }
        val provider = Provider()
        provider.setFname(binding?.idFirstName?.text.toString().trim())
        provider.setLname(binding?.idLastName?.text.toString().trim())
        provider.setEmail(binding?.idEmailId?.text.toString().toLowerCase(Locale.ROOT).trim())
        provider.setPhone(binding?.idPhoneNumber?.text.toString().trim().replace("-", ""))
        provider.setPassword(binding?.idPassword?.text.toString().trim())
        if (binding?.spnCountryCode?.selectedItem != null) {
            provider.setCountryCode(binding?.spnCountryCode?.selectedItem.toString())
        } else {
            provider.setCountryCode("+1")
        }
        val intent: Intent = if (role != null && role == "RP") {
            Intent(this, RemoteProviderSignUpSecond::class.java)
        } else {
            Intent(this, ActivityLocalCareProviderSignUpSecond::class.java)
        }
        val bundle = Bundle()
        bundle.putString(Constants.IntentKeyConstants.FIRST_NAME, provider.getFname())
        bundle.putString(Constants.IntentKeyConstants.LAST_NAME, provider.getLname())
        bundle.putString(Constants.IntentKeyConstants.EMAIL, provider.getEmail())
        bundle.putString(Constants.IntentKeyConstants.PASSWORD, provider.getPassword())
        bundle.putString(Constants.IntentKeyConstants.PHONE_NO, provider.getPhone())
        bundle.putString(Constants.IntentKeyConstants.COUNTRY_CODE, provider.getCountryCode())
        intent.putExtras(bundle)
        startActivityForResult(intent, 201, bundle)
        Log.d("Provider Details", Gson().toJson(provider))
    }

    fun checkButton() {
        val validFirst: Boolean? = ValidationUtil().checkEdittext(binding?.idFirstName)
        val validLast: Boolean? = ValidationUtil().checkEdittext(binding?.idLastName)
        val validEmail: Boolean? = ValidationUtil().checkEmail(binding?.idEmailId?.text.toString())
        val validPass: Boolean? = ValidationUtil().checkPasswordValid(binding?.idPassword?.text.toString(), binding?.idFirstName?.text.toString(),
                binding?.idLastName?.text.toString(), binding?.idEmailId?.text.toString())
        //        Boolean validPass = ValidationUtil.checkPassword(binding.idPassword.getText().toString());
//        boolean isRegularPassword = isRegularPassword(binding.idPassword.getText().toString().trim());
        val validPhone: Boolean? = ValidationUtil().checkPhoneNo(binding?.idPhoneNumber?.text.toString())
        //        if (validFirst && validLast && validEmail && validPass && validPhone && !isRegularPassword) {
        binding?.btnNext?.isEnabled = validFirst == true && validLast == true && validEmail == true && validPass == true && validPhone == true
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        return errMsg!!.isEmpty()
    }

    private fun setCountryCode() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding!!.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)!!.show()
            return
        }
        showProgressBar()
        val countryCode = ArrayList<String>()
        val providerMap = LinkedHashMap<String, String?>()
        //        countryCode.add(getString(R.string.select_provider_types));
        viewModel?.getCountry()?.observe(this, {
            val response= it.body()
            dismissProgressBar()
            Log.d(TAG, "country code data" + response?.getStatus() + response)
            if (response?.getStatus() != null && response.getStatus()!!) {
                if (response.getCountryCodeResponseList() != null && response.getCountryCodeResponseList()!!.isNotEmpty()) {
                    providerMap.putAll(getCountryCodes(response.getCountryCodeResponseList() as List<CountryCodeList>)!!)
                    countryCode.addAll(providerMap.keys)
                    setCountrySpinner(countryCode)
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this, java.lang.String.valueOf(response?.getErrorMessage()), Constants.API.getHospital)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let { it1 -> CustomSnackBar.make(binding!!.idContainerLayout, this, CustomSnackBar.WARNING, it1, CustomSnackBar.TOP, 3000, 0) }!!.show()
            }
        })
    }

    private fun setCountrySpinner(countryCode: ArrayList<String>) {
        val spinnerArrayAdapter = ArrayAdapter(
                this, R.layout.spinner_custom_text, countryCode)
        binding?.spnCountryCode?.adapter = spinnerArrayAdapter
        binding?.spnCountryCode?.setSelection(0)
        binding?.spnCountryCode?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val spinnerText = view as TextView
                if (spinnerText != null) {
                    spinnerText.maxLines = 1
                    spinnerText.textSize = 16f
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(ActivityLocalCareProviderSignUpFirst(), spinnerText, R.color.black)
                        //                            UtilityMethods.setDrawableBackground(RemoteProviderSignUpSecond.this, binding.idRemoteProviderSpinner, R.drawable.spinner_drawable_selected);
                    } else {
                        UtilityMethods().setTextViewColor(ActivityLocalCareProviderSignUpFirst(), spinnerText, R.color.title_black)
                        //                            UtilityMethods.setDrawableBackground(RemoteProviderSignUpSecond.this, binding.idRemoteProviderSpinner, R.drawable.spinner_drawable);
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getCountryCodes(providers: List<CountryCodeList>): LinkedHashMap<String, String?>? {
        val providerMap = LinkedHashMap<String, String?>()
        for (i in providers.indices) {
            val cc = providers[i]
            if (cc != null && cc.getId() != null) {
                providerMap[cc.getCode()!!.trim()] = cc.getId()
            }
        }
        return providerMap
    }

    fun checkFirstName(showError: Boolean) {
        if (binding?.idFirstName?.text.toString().length === 0) {
            if (showError) {
                binding?.idFirstName?.setErrorMessage(getString(R.string.first_name_empty_try_again))
            }
            binding?.idFirstName?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }  else {
            binding?.idFirstName?.setErrorMessage("")
            binding?.idFirstName?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0)
        }
    }

    fun checkLastName(showError: Boolean) {
        if (binding?.idLastName?.text.toString().length === 0) {
            if (showError) {
                binding?.idLastName?.setErrorMessage(getString(R.string.last_name_empty_try_again))
            }
            binding?.idLastName?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }  else {
            binding?.idLastName?.setErrorMessage("")
            binding?.idLastName?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0)
        }
    }

    fun checkEmail(showError: Boolean) {
        if (binding?.let { ValidationUtil().checkEmail(it) } != null) {
            if (showError) {
                ValidationUtil().checkEmail(binding!!)?.let { binding?.idEmailId?.setErrorMessage(it) }
            }
            binding?.idEmailId?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            binding?.idEmailId?.setErrorMessage("")
            binding?.idEmailId?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0)
        }
    }

    fun checkPhone(showError: Boolean) {
        if (binding?.let { ValidationUtil().checkPhoneNo(it) } != null) {
            if (showError) {
                ValidationUtil().checkPhoneNo(binding!!)?.let {
                    binding?.idPhoneNumber?.setErrorMessage(
                        it
                    )
                }
                binding?.phoneLayout?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                binding?.seperator?.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red))
            }
            binding?.idPhoneNumber?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            binding?.idPhoneNumber?.setErrorMessage("")
            binding?.seperator?.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.edittext_stroke_color))
            binding?.phoneLayout?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
            binding?.idPhoneNumber?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0)
        }
    }

    fun checkPassword(showError: Boolean) {
        val errorMessage: String? = binding?.let {
            ValidationUtil().checkPassword(binding?.idPassword?.text.toString(), it, binding?.idFirstName?.text.toString(),
                binding?.idLastName?.text.toString(), binding?.idEmailId?.text.toString())
        }
        if (errorMessage != null) {
            if (showError) {

                binding?.idPassword?.setErrorMessage(errorMessage)
                binding?.passwordLayout?.background = resources.getDrawable(R.drawable.error_edittext_bg)
                binding?.passwordInfo?.visibility = View.VISIBLE
            }
            binding?.passwordVerified?.visibility = View.GONE
        } else {
            binding?.idPassword?.setErrorMessage("")
            binding?.passwordLayout?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
            binding?.passwordInfo?.visibility = View.GONE
            binding?.passwordVerified?.visibility = View.VISIBLE
            //                        binding.idPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0);
        }
    }

    private fun isRegularPassword(password: String): Boolean {
        var password = password
        password = password.toLowerCase()
        for (i in sequenceWordList!!.indices) {
            val seqStr = sequenceWordList!![i].toLowerCase()
            if (password.startsWith(seqStr)) {
                return true
            }
            if (password.endsWith(seqStr)) {
                return true
            }
            if (password.contains(seqStr)) {
                return true
            }
        }
        return false
    }

    fun ArrayListSequenceWord() {
        sequenceWordList = ArrayList()
        sequenceWordList?.add("qwerty")
        sequenceWordList?.add("asdfgh")
        sequenceWordList?.add("zxcvbn")
        sequenceWordList?.add("password")
        sequenceWordList?.add("admin")
        sequenceWordList?.add("test")
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.id_first_name -> ActivityLocalCareProviderSignUpFirst().checkFirstName(false)
                R.id.id_last_name -> ActivityLocalCareProviderSignUpFirst(). checkLastName(false)
                R.id.id_email_id ->  ActivityLocalCareProviderSignUpFirst().checkEmail(false)
                R.id.id_password -> ActivityLocalCareProviderSignUpFirst(). checkPassword(false)
                R.id.id_phone_number -> ActivityLocalCareProviderSignUpFirst(). checkPhone(false)
            }
            ActivityLocalCareProviderSignUpFirst(). checkButton()
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
            editable.toString()
            when (view.id) {
                R.id.id_first_name ->  ActivityLocalCareProviderSignUpFirst().checkFirstName(true)
                R.id.id_last_name ->  ActivityLocalCareProviderSignUpFirst().checkLastName(true)
                R.id.id_email_id -> ActivityLocalCareProviderSignUpFirst(). checkEmail(true)
                R.id.id_password -> ActivityLocalCareProviderSignUpFirst(). checkPassword(true)
                R.id.id_phone_number -> ActivityLocalCareProviderSignUpFirst(). checkPhone(true)
            }
        }

        init {
            this.view = view
        }
    }
}
