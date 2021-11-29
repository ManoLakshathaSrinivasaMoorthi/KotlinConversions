package com.mvp.omnicure.kotlinactivity.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
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
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityLocalCareProviderSignUpSecondBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.*
import com.example.dailytasksamplepoc.kotlinomnicure.utils.Buildconfic
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.LocalCareProviderSignUpSecondViewModel
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.mvp.omnicure.kotlinactivity.utils.ValidationUtil

import omnicurekotlin.example.com.userEndpoints.model.Provider
import omnicurekotlin.example.com.userEndpoints.model.User

class ActivityLocalCareProviderSignUpSecond : BaseActivity() {

    //Declare the variables
    private val TAG = ActivityLocalCareProviderSignUpSecond::class.java.simpleName
    protected var binding: ActivityLocalCareProviderSignUpSecondBinding? = null
    protected var viewModel: LocalCareProviderSignUpSecondViewModel? = null
    private var strHospitalID: Long? = null
    private var strHospitalName: String? = null
    var context: Context? = ActivityLocalCareProviderSignUpSecond()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise the variables
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_care_provider_sign_up_second)
        viewModel = ViewModelProvider(this).get(LocalCareProviderSignUpSecondViewModel::class.java)
        setView()
    }

    fun setView() {
        setLocalCareProviderSpinner()

        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding!!.txtAlreadySignIn.text = Html.fromHtml(
            String.format(
                "Already have account?" + "<font color='#%s'><b>" + " LOG IN</b></font>",
                сolorString
            )
        )
        binding!!.txtTermsAndCondition.text =
            Html.fromHtml("By signing up you agree to " + Buildconfic.value() + " <u>Terms & Conditions</u>")

        binding!!.idBackButton.setOnClickListener { view -> finish() }

        binding!!.txtAlreadySignIn.setOnClickListener {
            handleMultipleClick(binding!!.txtAlreadySignIn)
            Log.d(TAG, "onClick: of already signin ")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding!!.hospitalContainer.setOnClickListener { v ->
            v.isFocusableInTouchMode = true
            v.requestFocus()
            v.isFocusableInTouchMode = false
            val intent = Intent(
                this, ActivityHospitalList::class.java)
            intent.putExtra(Constants.IntentKeyConstants.SELECTED_HOSPITAL, binding!!.selectHospital.text.toString())
            startActivityForResult(intent, 2)
        }

        binding!!.npiNumber.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    binding!!.npiNumber.addTextChangedListener(GenericTextWatcher(binding!!.npiNumber))
                    checkNPI(true)
                }
            }

        binding!!.txtTermsAndCondition.setOnClickListener {
            val i = Intent(
                this, TermsAndConditionsActivity::class.java)
            i.putExtra("isSelected", binding!!.checkbox.isChecked)
            startActivityForResult(i, 1)

        }
        binding!!.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.isFocusableInTouchMode = true
            buttonView.requestFocus()
            buttonView.isFocusableInTouchMode = false
            checkButtonValidation()

        }

        binding!!.btnNext.setOnClickListener { onVerifyAccount() }


    binding!!.npiNumber.addTextChangedListener(ValidationTextWatcher(binding!!.npiNumber))
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
        val errMsg = binding?.let { ValidationUtil().isValidate(it) }
        if (errMsg != null) {
            if (!errMsg.isEmpty()) {

                if (errMsg != null) {
                    CustomSnackBar.make(
                        binding!!.idContainerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
                return false
            }
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setLocalCareProviderSpinner() {
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
        val ProviderType = arrayOf(
            "Select Provider Type",
            "Bedside Provider",
            "Home Provider"
        )
        val spinnerArrayAdapter = ArrayAdapter(
            this, R.layout.spinner_custom_text, ProviderType
        )
        binding!!.lcpProviderTypeSpinner.adapter = spinnerArrayAdapter
        binding!!.lcpProviderTypeSpinner.setOnTouchListener { v, event ->
            Log.d(TAG, "onTouch : ")
            binding!!.providerTypeContainer.isEnabled = true
            binding!!.providerTypeContainer.isFocusableInTouchMode = true
            binding!!.providerTypeContainer.requestFocus()
            false
        }
        binding!!.lcpProviderTypeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val spinnerText = view as TextView
                binding!!.providerTypeContainer.isFocusableInTouchMode = true
                binding!!.providerTypeContainer.requestFocus()
                if (binding!!.lcpProviderTypeSpinner.selectedItem.toString()
                        .equals("Home Provider")
                ) {
                    binding!!.hospitalContainer.isEnabled = false
                    binding!!.hospitalContainer.background =
                        resources.getDrawable(R.drawable.ash_border_drawable_grey_bg)
                    binding!!.imgLocation.setImageDrawable(resources.getDrawable(R.drawable.ic_location_ash))
                    binding!!.selectHospital.text = getString(R.string.sel_hospital)
                    binding!!.selectHospital.setTextColor(resources.getColor(R.color.textcolor_title))
                    binding!!.verifiedTick.visibility = View.INVISIBLE
                } else {
                    binding!!.hospitalContainer.background =
                        resources.getDrawable(R.drawable.ash_border_drawable_bg)
                    binding!!.imgLocation.setImageDrawable(resources.getDrawable(R.drawable.ic_location))
                    binding!!.hospitalContainer.isEnabled = true
                }
                if (position == 0) {
                    binding!!.verified.visibility = View.GONE
                } else {
                    binding!!.verified.visibility = View.VISIBLE
                }
                spinnerText.maxLines = 1
                spinnerText.textSize = 16f
                if (position != 0) {
                    context?.let {
                        UtilityMethods().setTextViewColor(
                            it,
                            spinnerText,
                            R.color.black
                        )
                    }

                } else {
                    context?.let { UtilityMethods().setTextViewColor(it, spinnerText, R.color.title_black) }

                }
                checkButtonValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding!!.providerTypeContainer.isFocusableInTouchMode = true
                binding!!.providerTypeContainer.requestFocus()
                binding!!.providerTypeContainer.isFocusableInTouchMode = false
                checkButtonValidation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                var agreeFlag: Boolean? = null
                if (data != null) {
                    agreeFlag = data.getBooleanExtra("agree", false)
                }
                binding!!.checkbox.isChecked = agreeFlag!!
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    strHospitalID = data.getLongExtra("hospitalID", 0)
                    strHospitalName = data.getStringExtra("hospitalName")
                }
                binding!!.selectHospital.text = strHospitalName
                binding!!.verifiedTick.visibility = View.VISIBLE
                Log.d(TAG, "strHospitalID:$strHospitalID----$strHospitalName")
                checkButtonValidation()
            }
        }
    }

    private fun createPatientObject(): Provider {
        val providerID = PrefUtility().getProviderId(this)
        val role = PrefUtility().getRole(this)
        val extras = intent.extras
        val provider = Provider()
        if (extras != null) {
            val fname = extras.getString(Constants.IntentKeyConstants.FIRST_NAME)
            val lname = extras.getString(Constants.IntentKeyConstants.LAST_NAME)
            provider.fname =
                fname!!.substring(0, 1).toUpperCase() + fname.substring(1).toLowerCase()
            provider.lname =
                lname!!.substring(0, 1).toUpperCase() + lname.substring(1).toLowerCase()
            provider.email = extras.getString(Constants.IntentKeyConstants.EMAIL)
            provider.phone = extras.getString(Constants.IntentKeyConstants.PHONE_NO)
            provider.password = extras.getString(Constants.IntentKeyConstants.PASSWORD)
            provider.countryCode = extras.getString(Constants.IntentKeyConstants.COUNTRY_CODE)
        }
        provider.userType = "LCP"
        provider.providerType = "L"
        provider.role = Constants.ProviderRole.BD.toString()
        provider.npiNumber = binding!!.npiNumber.text.toString().trim()
        if (binding!!.lcpProviderTypeSpinner.selectedItemPosition === 1) {
            provider.lcpType = "B"
            provider.userSubType = "BD"
            provider.role = Constants.ProviderRole.BD.toString()
            //            provider.setHospitalId(Long.valueOf(strHospitalID));
            //strHospitalID is always Long
            provider.hospitalId = strHospitalID
            provider.hospital = binding!!.selectHospital.text.toString()
        } else if (binding!!.lcpProviderTypeSpinner.selectedItemPosition === 2) {
            provider.lcpType = "H"
            provider.userSubType = "HD"
        }
        return provider
    }

    private fun onVerifyAccount() {
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
        Log.d("LocalPROVIDER", Gson().toJson(provider))
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.register.toString()))
        viewModel?.registerProvider(provider)?.observe(this,{
            dismissProgressBar()
            Log.d(TAG, "Registration Response " + Gson().toJson(it))
            if (it != null && it.status != null && it.status!!) {
                PrefUtility().saveStringInPref(
                    applicationContext,
                    Constants.redirectValidation.EMAIL,
                    provider.email
                )
                PrefUtility().saveStringInPref(
                    applicationContext,
                    Constants.redirectValidation.PASSWORD,
                    provider.password
                )
                it.user?.id?.let { it1 ->
                    PrefUtility().saveLongInPref(
                        applicationContext,
                        Constants.redirectValidation.ID,
                        it1
                    )
                }
                it.user?.let { it1 -> onRegisterSuccess(it1) }
            } else {
                val errMsg = ErrorMessages().getErrorMessage(
                    this, it!!.errorMessage, Constants.API.register)

                if (errMsg != null) {
                    CustomSnackBar.make(
                        binding!!.idContainerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
        })


    }

    private fun onRegisterSuccess(provider: User) {
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.id)
        intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO, provider.phone)
        intent.putExtra(Constants.IntentKeyConstants.COUNTRY_CODE, provider.countryCode)
        startActivity(intent)
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private infix fun checkNPI(showError: Boolean) {
        if (binding!!.npiNumber.text.toString().length === 0) {
            binding!!.npiNumber.setErrorMessage("")
            binding!!.npiNumber.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_document,
                0,
                0,
                0
            )
        } else if (binding!!.npiNumber.text.toString().isNotEmpty() && binding!!.npiNumber.text.toString().length !== 10
        ) {
            if (showError) {
                binding!!.npiNumber.setErrorMessage(getString(R.string.invalid_npi))
            }
            binding!!.npiNumber.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_document,
                0,
                0,
                0
            )
        } else {
            binding!!.npiNumber.setErrorMessage("")
            binding!!.npiNumber.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_document,
                0,
                R.drawable.ic_checkmark_edittext,
                0
            )
        }
    }

    private fun checkButtonValidation() {
        var validHospital = false
        validHospital = if (binding!!.lcpProviderTypeSpinner.selectedItemPosition === 1) {
            ValidationUtil().checkTextView(binding!!.selectHospital) == true && !binding!!.selectHospital.text.toString()
                .equals(getString(R.string.sel_hospital))
        } else (if (binding!!.lcpProviderTypeSpinner.selectedItemPosition === 2) {
            ValidationUtil().checkTextView(binding!!.selectHospital)
        } else {
            ValidationUtil().checkTextView(binding!!.selectHospital) == true && !binding!!.selectHospital.text.toString()
                .equals(getString(R.string.sel_hospital))
        }) == true
        val validNpi = !TextUtils.isEmpty(
            binding!!.npiNumber.text.toString().trim()
        ) && binding!!.npiNumber.text.toString().length !== 10
        binding!!.btnNext.isEnabled = (binding!!.lcpProviderTypeSpinner.selectedItemPosition > 0 && validHospital && binding!!.checkbox.isChecked
                && !validNpi)
    }

    private class GenericTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                R.id.npi_number ->ActivityLocalCareProviderSignUpSecond().checkNPI(true)
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
                R.id.npi_number ->ActivityLocalCareProviderSignUpSecond().checkNPI(false)
            }
            ActivityLocalCareProviderSignUpSecond().checkButtonValidation()
        }

        init {
            this.view = view
        }
    }
}