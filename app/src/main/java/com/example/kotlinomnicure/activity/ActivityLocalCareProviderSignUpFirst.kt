package com.mvp.omnicure.kotlinactivity.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityLocalCareProviderSignUpFirstBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.BaseActivity
import com.example.dailytasksamplepoc.kotlinomnicure.activity.LoginActivity
import com.example.kotlinomnicure.helper.MobileNumberFormatter
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.mvp.omnicure.kotlinactivity.utils.ValidationUtil


import com.mvp.omnicure.kotlinactivity.viewmodel.LocalCareProviderSignUpFirstViewModel
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeList
import omnicurekotlin.example.com.userEndpoints.model.Provider


import java.util.*

class ActivityLocalCareProviderSignUpFirst : BaseActivity(){

    //Declare the variables
    private val TAG = ActivityLocalCareProviderSignUpFirst::class.java.simpleName
    var binding: ActivityLocalCareProviderSignUpFirstBinding? = null
    var viewModel: LocalCareProviderSignUpFirstViewModel? = null
    var role: String? = null
    private var sequenceWordList: ArrayList<String>? = null
    var context: Context =ActivityLocalCareProviderSignUpFirst()


     //on create method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         //Initialise the variables
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_care_provider_sign_up_first)
        viewModel = ViewModelProviders.of(this).get(LocalCareProviderSignUpFirstViewModel::class.java)
        setView()
    }

    //set the view
    fun setView() {
        setOnclickListener()
        setCountryCode()
        ArrayListSequenceWord()
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding!!.alreadySigninText.text = Html.fromHtml(
            String.format("Already have account?" + "<font color='#%s'><b>" + " LOG IN</b></font>", сolorString))

        binding!!.alreadySigninText.setOnClickListener {
            Log.d(TAG, "onClick: of already signin ")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        val extras = intent.extras
        role = extras!!.getString(Constants.SharedPrefConstants.ROLE, null)
        if (role != null && role == "RP") {
            binding!!.idCreateProfileTxt.text = "Sign up as Remote Provider"
        } else {
            binding!!.idCreateProfileTxt.text = "Sign up as Local Care Provider"
        }

        binding!!.idPassword.setTypeface(Typeface.DEFAULT)

        binding!!.idFirstName.addTextChangedListener(ValidationTextWatcher(binding!!.idFirstName))
        binding!!.idLastName.addTextChangedListener(ValidationTextWatcher(binding!!.idLastName))
        binding!!.idEmailId.addTextChangedListener(ValidationTextWatcher(binding!!.idEmailId))
        binding!!.idPassword.addTextChangedListener(ValidationTextWatcher(binding!!.idPassword))
        binding!!.idPhoneNumber.addTextChangedListener(ValidationTextWatcher(binding!!.idPhoneNumber))

        checkButton()

    }



    private fun setOnclickListener() {
        binding!!.idPhoneNumber.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    if (binding!!.idPhoneNumber.getErrorMessage().equals("")) {
                        binding!!.seperator.setBackgroundColor(ContextCompat.getColor(view.context, R.color.black))
                        binding!!.phoneLayout.background = resources.getDrawable(R.drawable.border_black_edittext_bg)
                    }
                } else {
                    binding!!.seperator.setBackgroundColor(
                        ContextCompat.getColor(view.context, R.color.edittext_stroke_color))
                    binding!!.phoneLayout.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
                    binding!!.idPhoneNumber.addTextChangedListener(GenericTextWatcher(binding!!.idPhoneNumber))
                    checkPhone(true)
                }
            }
        binding!!.idPassword.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {

                    if (binding!!.idPassword.getErrorMessage().equals("")) {
                        binding!!.passwordLayout.background = resources.getDrawable(R.drawable.border_black_edittext_bg)
                    }
                } else {
                    binding!!.passwordLayout.background =
                        resources.getDrawable(R.drawable.ash_border_drawable_bg)
                    binding!!.idPassword.addTextChangedListener(
                        GenericTextWatcher(binding!!.idPassword))
                    checkPassword(true)
                }
            }
        binding!!.idEmailId.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    binding!!.idEmailId.addTextChangedListener(GenericTextWatcher(binding!!.idEmailId))
                    checkEmail(true)
                }
            }
        binding!!.idFirstName.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    binding!!.idFirstName.addTextChangedListener(GenericTextWatcher(binding!!.idFirstName))
                    checkFirstName(true)
                }
            }
        binding!!.idLastName.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    binding!!.idLastName.addTextChangedListener(GenericTextWatcher(binding!!.idLastName))
                    checkLastName(true)
                }
            }
        binding!!.idBackButton.setOnClickListener { finish() }

        binding!!.idPassword.setOnEditorActionListener { textView, id, keyEvent ->
            if (id === EditorInfo.IME_ACTION_DONE) {
                onClickNext()
                return@setOnEditorActionListener true
            }
            false
        }

        binding!!.idPhoneNumber.addTextChangedListener(object : TextWatcher {
            val FIRST_SEP_LENGTH = 4
            val SECOND_SEP_LENGTH = 8
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence == null || charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    return
                } else {
                    val str = charSequence.toString()
                    MobileNumberFormatter().formatMobileNumber(
                        str,
                        binding!!.idPhoneNumber,
                        FIRST_SEP_LENGTH,
                        SECOND_SEP_LENGTH
                    )
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        binding!!.passwordVisibility.setOnClickListener {
            ValidationUtil().passwordVisibility(
                binding!!.idPassword,
                binding!!.passwordVisibility
            )
        }

        binding!!.passwordInfo.setOnClickListener {
            ValidationUtil().showPasswordValidationDialog(
                this,
                binding!!.idPassword.text.toString(),
                binding!!.idFirstName.text.toString(),
                binding!!.idLastName.text.toString(),
                binding!!.idEmailId.text.toString()
            )
        }

        binding!!.btnNext.setOnClickListener { onClickNext() }

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
        if (!UtilityMethods().isInternetConnected(this)!!) {
            CustomSnackBar.make(
                binding!!.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        val provider = createPatientObject()
        Log.d("PROVIDER", Gson().toJson(provider))
        onemailchecksuccess()

    }

    private fun createPatientObject(): Provider {
        val provider = Provider()
        provider.email = binding!!.idEmailId.text.toString().toLowerCase().trim()
        if (binding!!.spnCountryCode.selectedItem != null) {
            provider.countryCode = binding!!.spnCountryCode.selectedItem.toString()
        } else {
            provider.countryCode = "+1"
        }
        provider.phone = binding!!.idPhoneNumber.text.toString().trim().replace("-", "")
        return provider
    }

    fun onemailchecksuccess() {
        val provider = Provider()
        provider.fname = binding!!.idFirstName.text.toString().trim()
        provider.lname = binding!!.idLastName.text.toString().trim()
        provider.email = binding!!.idEmailId.text.toString().toLowerCase().trim()
        provider.phone = binding!!.idPhoneNumber.text.toString().trim().replace("-", "")
        provider.password = binding!!.idPassword.text.toString().trim()
        if (binding!!.spnCountryCode.selectedItem != null) {
            provider.countryCode = binding!!.spnCountryCode.selectedItem.toString()
        } else {
            provider.countryCode = "+1"
        }
        val intent: Intent = if (role != null && role == "RP") {
            Intent(this, RemoteProviderSignUpSecond::class.java)
        } else {
            Intent(this, ActivityLocalCareProviderSignUpSecond::class.java
            )
        }
        val bundle = Bundle()
        bundle.putString(Constants.IntentKeyConstants.FIRST_NAME, provider.fname)
        bundle.putString(Constants.IntentKeyConstants.LAST_NAME, provider.lname)
        bundle.putString(Constants.IntentKeyConstants.EMAIL, provider.email)
        bundle.putString(Constants.IntentKeyConstants.PASSWORD, provider.password)
        bundle.putString(Constants.IntentKeyConstants.PHONE_NO, provider.phone)
        bundle.putString(Constants.IntentKeyConstants.COUNTRY_CODE, provider.countryCode)
        intent.putExtras(bundle)
        startActivityForResult(intent, 201, bundle)
        Log.d("Provider Details", Gson().toJson(provider))
    }

    fun checkButton() {
        val validFirst = ValidationUtil().checkEdittext(binding!!.idFirstName)
        val validLast = ValidationUtil().checkEdittext(binding!!.idLastName)
        val validEmail = ValidationUtil().checkEmail(binding!!.idEmailId.text.toString())
        val validPass = ValidationUtil().checkPasswordValid(
            binding!!.idPassword.text.toString(), binding!!.idFirstName.text.toString(),
            binding!!.idLastName.text.toString(), binding!!.idEmailId.text.toString())

        val validPhone = ValidationUtil().checkPhoneNo(binding!!.idPhoneNumber.text.toString())

        binding!!.btnNext.isEnabled = validFirst == true && validLast == true && validEmail == true && validPass == true && validPhone == true
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed(Runnable { view.isEnabled = true }, 500)
    }

    private fun isValid(): Boolean {
        val errMsg = binding?.let { ValidationUtil().isValidate(it) }
        if (errMsg != null) {
            return errMsg.isEmpty()
        }
        return true
    }

    private fun setCountryCode() {
        if (!UtilityMethods().isInternetConnected(this)!!) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding!!.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )!!
                .show()
            return
        }


        //   showProgressBar();
        val countryCode = ArrayList<String?>()
        val providerMap = LinkedHashMap<String?, String>()
        //        countryCode.add(getString(R.string.select_provider_types));
        viewModel!!.getCountry()!!.observe(this, { response ->
            dismissProgressBar()
            //            if (response != null && response.getStatus() && response.getStatus()) {
            if (response != null && response.getStatus()!!) {
                Log.d(TAG, "country code data" + response.getStatus() + Gson().toJson(response))
                if (response.getCountryCodeResponseList() != null && !response.getCountryCodeResponseList()!!
                        .isEmpty()) {
                    val resList = Gson().toJson(response.getCountryCodeResponseList())
                    Log.d("ahfahjfaa", "resList: $resList")
                    val countryCodes = Gson().fromJson<ArrayList<HashMap<String, String>>>(
                        resList,
                        object : TypeToken<ArrayList<HashMap<String?, String?>?>?>() {}.type
                    )
                    Log.d("ahfahjfaa", "setCountryCode: $countryCodes")

                    providerMap.putAll(getCountryCodes(countryCodes))
                    providerMap.putAll(getCountryCodes(response.getCountryCodeResponseList()))
                    countryCode.addAll(providerMap.keys)
                    setCountrySpinner(countryCode)
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this, java.lang.String.valueOf(
                        response!!.getErrorId()
                    ), Constants.API.getHospital
                )

                errMsg?.let {
                    CustomSnackBar.make(
                        binding!!.idContainerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        it,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )
                }!!
                    .show()
            }
        })
    }


    //set the spinner for the country code
    private fun setCountrySpinner(countryCode: ArrayList<String?>) {
        val spinnerArrayAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, countryCode)
        binding!!.spnCountryCode.adapter = spinnerArrayAdapter
        binding!!.spnCountryCode.setSelection(0)
        binding!!.spnCountryCode.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long) {
                val spinnerText = view as TextView
                if (spinnerText != null) {
                    spinnerText.maxLines = 1
                    spinnerText.textSize = 16f
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(context, spinnerText, R.color.black)

                    } else {
                        UtilityMethods().setTextViewColor(context, spinnerText, R.color.title_black
                        )

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    //get the country code
    private fun getCountryCodes(providers: List<CountryCodeList>): LinkedHashMap<String?, String?>? {
        val providerMap = LinkedHashMap<String?, String?>()
        for (i in providers.indices) {
            val cc = providers[i]
            if (cc.id != null) {
                providerMap[cc.code?.trim { it <= ' ' }] = cc.id
            }
        }
        Log.d(TAG, "getCountryCodes: $providerMap")
        return providerMap
    }


    //get the country code
    private fun getCountryCodes(countryCodes: ArrayList<HashMap<String, String>>): LinkedHashMap<String?, String?> {
        val providerMap = LinkedHashMap<String?, String?>()
        for (item in countryCodes) {
            providerMap[item[item.keys.iterator().next()]] = item[item.keys.toTypedArray()[2]]
        }
        return providerMap
    }


    //check the first name
   fun checkFirstName(showError: Boolean) {
        if (binding!!.idFirstName.text.toString().length === 0) {
            if (showError) {
                binding!!.idFirstName.setErrorMessage(getString(R.string.first_name_empty_try_again))
            }
            binding!!.idFirstName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }  else {
            binding!!.idFirstName.setErrorMessage("")
            binding!!.idFirstName.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_checkmark_edittext,
                0
            )
        }
    }

    fun checkLastName(showError: Boolean) {
        if (binding!!.idLastName.text.toString().length === 0) {
            if (showError) {
                binding!!.idLastName.setErrorMessage(getString(R.string.last_name_empty_try_again))
            }
            binding!!.idLastName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            binding!!.idLastName.setErrorMessage("")
            binding!!.idLastName.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_checkmark_edittext,
                0
            )
        }
    }

    fun checkEmail(showError: Boolean) {
        if (binding?.let { ValidationUtil().checkEmail(it) } != null) {
            if (showError) {
                ValidationUtil().checkEmail(binding!!)?.let { binding!!.idEmailId.setErrorMessage(it) }
            }
            binding!!.idEmailId.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            binding!!.idEmailId.setErrorMessage("")
            binding!!.idEmailId.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_checkmark_edittext,
                0
            )
        }
    }

    fun checkPhone(showError: Boolean) {
        if (binding?.let { ValidationUtil().checkPhoneNo(it) } != null) {
            if (showError) {
                binding!!.idPhoneNumber.error = ValidationUtil().checkPhoneNo(binding!!)
                binding!!.phoneLayout.background =
                    resources.getDrawable(R.drawable.error_edittext_bg)
                binding!!.seperator.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.red
                    )
                )
            }
            binding!!.idPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            binding!!.idPhoneNumber.error= ""
            binding!!.seperator.setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.edittext_stroke_color))
            binding!!.phoneLayout.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
            binding!!.idPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark_edittext, 0)
        }
    }

    fun checkPassword(showError: Boolean) {
        val errorMessage = ValidationUtil().checkPassword(
            binding!!.idPassword.text.toString(), binding!!, binding!!.idFirstName.text.toString(),
            binding!!.idLastName.text.toString(), binding!!.idEmailId.text.toString()
        )
        if (errorMessage != null) {
            if (showError) {

                binding!!.idPassword.error = errorMessage
                binding!!.passwordLayout.background =
                    resources.getDrawable(R.drawable.error_edittext_bg)
                binding!!.passwordInfo.visibility = View.VISIBLE
            }
            binding!!.passwordVerified.visibility = View.GONE
        } else {
            binding!!.idPassword.error= ""
            binding!!.passwordLayout.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
            binding!!.passwordInfo.visibility = View.GONE
            binding!!.passwordVerified.visibility = View.VISIBLE

        }
    }



    fun ArrayListSequenceWord() {
        sequenceWordList = ArrayList()
        sequenceWordList!!.add("qwerty")
        sequenceWordList!!.add("asdfgh")
        sequenceWordList!!.add("zxcvbn")
        sequenceWordList!!.add("password")
        sequenceWordList!!.add("admin")
        sequenceWordList!!.add("test")
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
            val str = s.toString()
            if (view.id == R.id.id_email_id) {
                if (str.isNotEmpty() && str.contains(" ")) {

                    ActivityLocalCareProviderSignUpFirst().binding!!.idEmailId.setText(s.toString().replace(" ", ""))
                    ActivityLocalCareProviderSignUpFirst().binding!!.idEmailId.getText()?.let { Selection.setSelection(ActivityLocalCareProviderSignUpFirst().binding!!.idEmailId.getText(), it.length) }
                }
            }
        }

        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.id_first_name -> ActivityLocalCareProviderSignUpFirst().checkFirstName(false)
                R.id.id_last_name ->ActivityLocalCareProviderSignUpFirst(). checkLastName(false)
                R.id.id_email_id -> ActivityLocalCareProviderSignUpFirst().checkEmail(false)
                R.id.id_password ->  ActivityLocalCareProviderSignUpFirst().checkPassword(false)
                R.id.id_phone_number ->  ActivityLocalCareProviderSignUpFirst().checkPhone(false)
            }
            ActivityLocalCareProviderSignUpFirst().checkButton()
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
            when (view.id) {
                R.id.id_first_name -> ActivityLocalCareProviderSignUpFirst().checkFirstName(true)
                R.id.id_last_name -> ActivityLocalCareProviderSignUpFirst().checkLastName(true)
                R.id.id_email_id -> ActivityLocalCareProviderSignUpFirst().checkEmail(true)
                R.id.id_password -> ActivityLocalCareProviderSignUpFirst().checkPassword(true)
                R.id.id_phone_number ->ActivityLocalCareProviderSignUpFirst().checkPhone(true)
            }
        }

        init {
            this.view = view
        }
    }
}