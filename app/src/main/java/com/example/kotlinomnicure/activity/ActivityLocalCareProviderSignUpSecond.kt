package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityLocalCareProviderSignUpSecondBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.LocalCareProviderSignUpSecondViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import omnicurekotlin.example.com.userEndpoints.model.Provider
import omnicurekotlin.example.com.userEndpoints.model.User

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ActivityLocalCareProviderSignUpSecond : BaseActivity() {

    private val TAG = ActivityLocalCareProviderSignUpSecond::class.java.simpleName
    private var binding: ActivityLocalCareProviderSignUpSecondBinding? = null
    private var viewModel: LocalCareProviderSignUpSecondViewModel? = null
    private var strHospitalID: Long? = null
    private var strHospitalName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_care_provider_sign_up_second)
        viewModel = ViewModelProvider(this).get(LocalCareProviderSignUpSecondViewModel::class.java)
        setView()
    }

    private fun setView() {
        setLocalCareProviderSpinner()
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding?.txtAlreadySignIn?.text = Html.fromHtml(String.format("Already have account?" + "<font color='#%s'><b>" + " LOG IN</b></font>", сolorString))
        binding?.idBackButton?.setOnClickListener { finish() }
        binding?.txtAlreadySignIn?.setOnClickListener {
            handleMultipleClick(binding?.txtAlreadySignIn!!)
            Log.d(TAG, "onClick: of already signing ")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding?.hospitalContainer?.setOnClickListener { v ->
            v.isFocusableInTouchMode = true
            v.requestFocus()
            v.isFocusableInTouchMode = false
            val intent = Intent(this, ActivityHospitalList::class.java)
            intent.putExtra(Constants.IntentKeyConstants.SELECTED_HOSPITAL, binding?.selectHospital?.text.toString())
            startActivityForResult(intent, 2)
        }
        binding?.npiNumber?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding?.npiNumber?.addTextChangedListener(GenericTextWatcher(binding?.npiNumber!!))
                checkNPI(true)
            }
        }
        binding?.txtTermsAndCondition?.setOnClickListener {
            val i = Intent(this, TermsAndConditionsActivity::class.java)
            i.putExtra("isSelected", binding?.checkbox?.isChecked)
            startActivityForResult(i, 1)
            //                passwordValidation();
        }
        binding?.checkbox?.setOnCheckedChangeListener { buttonView, _ ->
            buttonView.isFocusableInTouchMode = true
            buttonView.requestFocus()
            buttonView.isFocusableInTouchMode = false
            checkButtonValidation()
        }
        binding?.btnNext?.setOnClickListener { onVerifyAccount() }
        binding?.npiNumber?.addTextChangedListener(ValidationTextWatcher(binding?.npiNumber!!))
        checkButtonValidation()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!errMsg?.isEmpty()!!) {
//            UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
            errMsg.let { CustomSnackBar.make(binding!!.idContainerLayout, this, CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0) }!!.show()
            return false
        }
        return true
    }

    private fun getHospitalNames(hospitals: List<Hospital>): java.util.LinkedHashMap<String, Long?> {
        val hospitalMap = java.util.LinkedHashMap<String, Long?>()
        for (i in hospitals.indices) {
            val hospital = hospitals[i]
            if (hospital.getId() != null) {
                hospitalMap[hospital.getName()!!.trim()] = hospital.getId()
            }
        }
        return hospitalMap
    }

    private fun setLocalCareProviderSpinner() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding!!.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)!!.show()
            return
        }
        val ProviderType = arrayOf(
                "Select Provider Type",
                "Bedside Provider",
                "Home Provider")
        val spinnerArrayAdapter = ArrayAdapter(
                this, R.layout.spinner_custom_text, ProviderType)
        binding?.lcpProviderTypeSpinner?.adapter = spinnerArrayAdapter
        binding?.lcpProviderTypeSpinner?.setOnTouchListener { _, _ ->
            Log.d(TAG, "onTouch : ")
            binding?.providerTypeContainer?.isEnabled = true
            binding?.providerTypeContainer?.isFocusableInTouchMode = true
            binding?.providerTypeContainer?.requestFocus()
            false
        }
        binding?.lcpProviderTypeSpinner?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val spinnerText = view as TextView
                binding?.providerTypeContainer?.isFocusableInTouchMode = true
                binding?.providerTypeContainer?.requestFocus()
                if (binding?.lcpProviderTypeSpinner?.selectedItem.toString() == "Home Provider") {
                    binding?.hospitalContainer?.isEnabled  = false
                    binding?.hospitalContainer?.background  = resources.getDrawable(R.drawable.ash_border_drawable_grey_bg)
                    binding?.imgLocation?.setImageDrawable(resources.getDrawable(R.drawable.ic_location_ash))
                    binding?.selectHospital?.text = getString(R.string.sel_hospital)
                    binding?.selectHospital?.setTextColor(resources.getColor(R.color.textcolor_title))
                    binding?.verifiedTick?.visibility = View.INVISIBLE
                } else {
                    binding?.hospitalContainer?.background = resources.getDrawable(R.drawable.ash_border_drawable_bg)
                    binding?.imgLocation?.setImageDrawable(resources.getDrawable(R.drawable.ic_location))
                    binding?.hospitalContainer?.isEnabled = true
                }
                if (position == 0) {
                    binding?.verified?.visibility = View.GONE
                } else {
                    binding?.verified?.visibility = View.VISIBLE
                }
                spinnerText.maxLines = 1
                spinnerText.textSize = 16f
                if (position != 0) {
                    UtilityMethods().setTextViewColor(ActivityLocalCareProviderSignUpSecond(), spinnerText, R.color.black)
                    //                        UtilityMethods.setDrawableBackground(ActivityLocalCareProviderSignUpSecond.this, binding.lcpProviderTypeSpinner, R.drawable.spinner_drawable_selected);
                } else {
                    UtilityMethods().setTextViewColor(ActivityLocalCareProviderSignUpSecond(), spinnerText, R.color.title_black)
                    //                        UtilityMethods.setDrawableBackground(ActivityLocalCareProviderSignUpSecond.this, binding.lcpProviderTypeSpinner, R.drawable.spinner_drawable);
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding?.providerTypeContainer?.isFocusableInTouchMode = true
                binding?.providerTypeContainer?.requestFocus()
                binding?.providerTypeContainer?.isFocusableInTouchMode = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val agreeFlag = data!!.getBooleanExtra("agree", false)
                binding?.checkbox?.isChecked = agreeFlag
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                strHospitalID = data?.getLongExtra("hospitalID", 0)
                strHospitalName = data?.getStringExtra("hospitalName")
                binding?.selectHospital?.text = strHospitalName
                binding?.verifiedTick?.visibility = View.VISIBLE
                Log.d(TAG, "strHospitalID:$strHospitalID----$strHospitalName")
            }
        }
    }

    private fun createPatientObject(): Provider {
        val providerID: Long? = PrefUtility().getProviderId(this)
        val role: String? = PrefUtility().getRole(this)
        val extras = intent.extras
        val provider = Provider()
        if (extras != null) {
            val fname = extras.getString(Constants.IntentKeyConstants.FIRST_NAME)
            val lname = extras.getString(Constants.IntentKeyConstants.LAST_NAME)
            provider.setFname(fname?.substring(0, 1)?.toUpperCase() + fname?.substring(1)?.toLowerCase())
            provider.setLname(lname?.substring(0, 1)?.toUpperCase() + lname?.substring(1)?.toLowerCase())
            provider.setEmail(extras.getString(Constants.IntentKeyConstants.EMAIL))
            provider.setPhone(extras.getString(Constants.IntentKeyConstants.PHONE_NO))
            provider.setPassword(extras.getString(Constants.IntentKeyConstants.PASSWORD))
            provider.setCountryCode(extras.getString(Constants.IntentKeyConstants.COUNTRY_CODE))
        }
        provider.setUserType("LCP")
        provider.setProviderType("L")
        provider.setRole(Constants.ProviderRole.BD.toString())
        provider.setNpiNumber(binding?.npiNumber?.text.toString().trim())
        if (binding?.lcpProviderTypeSpinner?.selectedItemPosition === 1) {
            provider.setLcpType("B")
            provider.setUserSubType("BD")
            provider.setRole(Constants.ProviderRole.BD.toString())
            provider.setHospitalId(java.lang.Long.valueOf(strHospitalID!!))
            provider.setHospital(binding?.selectHospital?.text.toString())
        } else if (binding?.lcpProviderTypeSpinner?.selectedItemPosition === 2) {
            provider.setLcpType("H")
            provider.setUserSubType("HD")
        }
        return provider
    }

    private fun onVerifyAccount() {
        binding?.btnNext?.let { handleMultipleClick(it) }
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding!!.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)!!.show()
            return
        }
        val provider = createPatientObject()
        Log.d("PROVIDER", Gson().toJson(provider))
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.register.toString()))
        viewModel?.registerProvider(provider)?.observe(this, { it ->
            dismissProgressBar()
            val commonResponse=it.body()
            Log.d(TAG, "Registration Response " + Gson().toJson(commonResponse))
            if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                PrefUtility().saveStringInPref(applicationContext, Constants.redirectValidation.EMAIL, provider.getEmail())
                PrefUtility().saveStringInPref(applicationContext, Constants.redirectValidation.PASSWORD, provider.getPassword())
                commonResponse.getUser()?.getId()?.let { PrefUtility().saveLongInPref(applicationContext, Constants.redirectValidation.ID, it) }
                commonResponse.getUser()?.let { onRegisterSuccess(it) }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse?.getErrorMessage(), Constants.API.register)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let { CustomSnackBar.make(binding!!.idContainerLayout, this, CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0) }!!.show()
            }
        })
    }

    private fun onRegisterSuccess(provider: User) {
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.getId())
        intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO, provider.getPhone())
        intent.putExtra(Constants.IntentKeyConstants.COUNTRY_CODE, provider.getCountryCode())
        startActivity(intent)
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun checkNPI(showError: Boolean) {
        if (binding?.npiNumber?.text.toString().length === 0) {
            binding?.npiNumber?.setErrorMessage("")
            binding?.npiNumber?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_document, 0, 0, 0)
        } else if (binding?.npiNumber?.text.toString().isNotEmpty() && binding?.npiNumber?.text.toString().length !== 10) {
            if (showError) {
                binding?.npiNumber?.setErrorMessage(getString(R.string.invalid_npi))
            }
            binding?.npiNumber?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_document, 0, 0, 0)
        } else {
            binding?.npiNumber?.setErrorMessage("")
            binding?.npiNumber?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_document, 0, R.drawable.ic_checkmark_edittext, 0)
        }
    }

    private fun checkButtonValidation() {
        val validHospital: Boolean = ValidationUtil().checkTextView(binding?.selectHospital)!!
        binding?.btnNext?.isEnabled = binding?.lcpProviderTypeSpinner?.selectedItemPosition!! > 0 && validHospital && binding!!.checkbox.isChecked
    }

    private class GenericTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                R.id.npi_number -> ActivityLocalCareProviderSignUpSecond().checkNPI(true)
            }
        }

        init {
            this.view = view
        }
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.npi_number -> ActivityLocalCareProviderSignUpSecond().checkNPI(false)
            }
           ActivityLocalCareProviderSignUpSecond(). checkButtonValidation()
        }

        init {
            this.view = view
        }
    }

}
