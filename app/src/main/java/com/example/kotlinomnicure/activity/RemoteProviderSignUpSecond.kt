package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityRemoteProviderSignUpSecondBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.RemoteProviderSignUpViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.userEndpoints.model.Provider
import omnicurekotlin.example.com.userEndpoints.model.RemoteProvider
import omnicurekotlin.example.com.userEndpoints.model.User
import java.util.*

class RemoteProviderSignUpSecond : BaseActivity() {


    private val TAG = RemoteProviderSignUpSecond::class.java.simpleName
    protected var binding: ActivityRemoteProviderSignUpSecondBinding? = null
    protected var viewModel: RemoteProviderSignUpViewModel? = null
    private var providerType: String? = null
    private var remoteProvideId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_provider_sign_up_second)
        viewModel = ViewModelProvider(this).get(RemoteProviderSignUpViewModel::class.java)
        setView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setView() {
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding?.txtAlreadySignIn?.setText(Html.fromHtml(String.format("Already have account?" + "<font color='#%s'><b>" + " LOG IN</b></font>",
                    сolorString)
            )
        )
        binding?.txtAlreadySignIn?.setText(
            Html.fromHtml(
                String.format(
                    "Already have account?" + "<font color='#%s'><b>" + " LOG IN</b></font>",
                    сolorString
                )
            )
        )
        binding?.txtAlreadySignIn?.setOnClickListener {
            Log.d(TAG, "onClick: of already signin ")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding?.idBackButton?.setOnClickListener { finish() }
        setRemoteProviderSpinner()
        binding?.idRemoteProviderSpinner?.setOnTouchListener { v, event ->
            Log.d(TAG, "onTouch : ")
            binding?.idRpProviderTypeContainer?.setEnabled(true)
            binding?.idRpProviderTypeContainer?.setFocusableInTouchMode(true)
            binding?.idRpProviderTypeContainer?.requestFocus()
            false
        }
        binding?.idNpiNumber?.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                binding?.idNpiNumber?.addTextChangedListener(GenericTextWatcher(binding?.idNpiNumber!!))
                checkNpi(true)
            }
        }
        binding?.btnNext?.setOnClickListener {
            onClickNext()
            //                passwordValidation();
        }
        binding?.txtTermsAndCondition?.setOnClickListener {
            val i = Intent(this, TermsAndConditionsActivity::class.java)
            i.putExtra("isSelected", binding?.checkbox?.isChecked())
            startActivityForResult(i, 1)
            //                passwordValidation();
        }
        binding?.checkbox?.setOnCheckedChangeListener { buttonView, isChecked ->
            checkButton()
            buttonView.isFocusableInTouchMode = true
            buttonView.requestFocus()
            buttonView.isFocusableInTouchMode = false
        }
        binding?.idNpiNumber?.addTextChangedListener(ValidationTextWatcher(binding?.idNpiNumber!!))
        checkButton()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun checkButton() {
        val validNpi: Boolean = ValidationUtil().checkNpi(binding?.idNpiNumber) == true
        if (validNpi && binding?.idRemoteProviderSpinner?.selectedItemPosition!! > 0 && binding?.checkbox?.isChecked == true) {
            binding?.btnNext?.setEnabled(true)
        } else {
            binding?.btnNext?.setEnabled(false)
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
    }

    private fun onClickNext() {
        binding?.let { handleMultipleClick(it.btnNext) }
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            Gson()
            return
        }
        val provider = createPatientObject()
        Log.d("PROVIDER", Gson().toJson(provider))
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.register.toString()))
        viewModel?.registerProvider(provider)?.observe(this, {
            val commonResponse=it.body()
                dismissProgressBar()
                if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()!!) {
                    PrefUtility().saveStringInPref(
                        applicationContext,
                        Constants.redirectValidation.EMAIL,
                        provider.getEmail()
                    )
                    PrefUtility().saveStringInPref(
                        applicationContext,
                        Constants.redirectValidation.PASSWORD,
                        provider.getPassword()
                    )
                    commonResponse.getUser()!!
                        .getId()?.let {
                            PrefUtility().saveLongInPref(
                                applicationContext,
                                Constants.redirectValidation.ID,
                                it
                            )
                        }
                    onRegisterSuccess(commonResponse.getUser())
                } else {
                    val errMsg: String? = ErrorMessages().getErrorMessage(
                        this@RemoteProviderSignUpSecond,
                        commonResponse?.getErrorMessage(),
                        Constants.API.register
                    )
                    //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                    errMsg?.let {
                        CustomSnackBar.make(
                            binding?.idContainerLayout,
                            this,
                            CustomSnackBar.WARNING,
                            it,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        )
                    }?.show()
                }
            })
    }

    private fun onRegisterSuccess(provider: User?) {
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider?.getId())
        intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO, provider?.getPhone())
        intent.putExtra(Constants.IntentKeyConstants.COUNTRY_CODE, provider?.getCountryCode())
        startActivity(intent)
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler!!.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!errMsg?.isEmpty()!!) {
//            UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                errMsg,
                CustomSnackBar.TOP,
                3000,
                0
            )!!
                .show()
            return false
        }
        return true
    }

    private fun setRemoteProviderSpinner() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout,
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
        showProgressBar()
        val remoteProvider = ArrayList<String?>()
        val providerMap = LinkedHashMap<String?, String?>()
        remoteProvider.add(getString(R.string.select_provider_types))
        viewModel?.getRemoteProviderList()?.observe(this) {
            val response=it.body()
            dismissProgressBar()
            Log.d(TAG, "remote provider data$response")
            if (response != null && response.getStatus() != null && response.getStatus()!!) {
                if (response.getRemoteProviderTypeList() != null && !response.getRemoteProviderTypeList()!!
                        .isEmpty()
                ) {
                    providerMap.putAll(getRemoteProviderNames(response.getRemoteProviderTypeList() as List<RemoteProvider>)!!)
                    remoteProvider.addAll(providerMap.keys)
                    setRemoteSpinner(remoteProvider, providerMap)
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    java.lang.String.valueOf(response?.getErrorId()),
                    Constants.API.getHospital
                )
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let {
                    CustomSnackBar.make(
                        binding?.idContainerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        it,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )
                }?.show()
            }
        }
    }

    private fun setRemoteSpinner(
        remoteProvider: ArrayList<String?>,
        providerMap: LinkedHashMap<String?, String?>
    ) {
        val remoteProviderListAdapter =
            ArrayAdapter(this, R.layout.spinner_custom_text, remoteProvider)
        //hospitalListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding?.idRemoteProviderSpinner?.setAdapter(remoteProviderListAdapter)
        binding?.idRemoteProviderSpinner?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                binding?.idRpProviderTypeContainer?.setFocusableInTouchMode(true)
                binding?.idRpProviderTypeContainer?.requestFocus()
                binding?.idRpProviderTypeContainer?.setFocusableInTouchMode(false)
                try {
                    checkButton()
                    if (position == 0) {
                        binding?.verified?.setVisibility(View.GONE)
                    } else {
                        binding?.verified?.setVisibility(View.VISIBLE)
                    }
                    val spinnerText = view as TextView
                    providerType = remoteProvider[position]
                    remoteProvideId = providerMap[providerType]
                    binding?.idRemoteProviderSpinner!!.setTag(remoteProvideId)
                    if (spinnerText != null) {
                        spinnerText.maxLines = 1
                        if (spinnerText.text.toString().length >= 30) {
                            spinnerText.textSize = 13f
                        } else if (spinnerText.text.toString().length >= 25) {
                        } else {
                            spinnerText.textSize = 16f
                        }
                        if (position != 0) {
                            UtilityMethods().setTextViewColor(
                                this@RemoteProviderSignUpSecond,
                                spinnerText,
                                R.color.black
                            )
                            //                            UtilityMethods.setDrawableBackground(RemoteProviderSignUpSecond.this, binding.idRemoteProviderSpinner, R.drawable.spinner_drawable_selected);
                        } else {
                            UtilityMethods().setTextViewColor(
                                this@RemoteProviderSignUpSecond,
                                spinnerText,
                                R.color.title_black
                            )
                            //                            UtilityMethods.setDrawableBackground(RemoteProviderSignUpSecond.this, binding.idRemoteProviderSpinner, R.drawable.spinner_drawable);
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                binding?.idRpProviderTypeContainer?.setFocusableInTouchMode(true)
                binding?.idRpProviderTypeContainer?.requestFocus()
                binding?.idRpProviderTypeContainer?.setFocusableInTouchMode(false)
            }
        })
    }

    private fun getRemoteProviderNames(providers: List<RemoteProvider>): LinkedHashMap<String?, String?>? {
        val providerMap = LinkedHashMap<String?, String?>()
        for (i in providers.indices) {
            val remoteProvider = providers[i]
            if (remoteProvider != null && remoteProvider.getId() != null) {
                providerMap[remoteProvider.getName()!!.trim()] = remoteProvider.getId()
            }
        }
        return providerMap
    }

    private fun createPatientObject(): Provider {
        val providerID: Long? = PrefUtility().getProviderId(this)
        val role: String? = PrefUtility().getRole(this)
        val extras = intent.extras
        val provider = Provider()
        if (extras != null) {
            val fname = extras.getString(Constants.IntentKeyConstants.FIRST_NAME)
            val lname = extras.getString(Constants.IntentKeyConstants.LAST_NAME)
            provider.setFname(
                fname!!.substring(0, 1).toUpperCase() + fname.substring(1).toLowerCase()
            )
            provider.setLname(
                lname!!.substring(0, 1).toUpperCase() + lname.substring(1).toLowerCase()
            )
            provider.setEmail(extras.getString(Constants.IntentKeyConstants.EMAIL))
            provider.setPhone(extras.getString(Constants.IntentKeyConstants.PHONE_NO))
            provider.setPassword(extras.getString(Constants.IntentKeyConstants.PASSWORD))
            provider.setCountryCode(extras.getString(Constants.IntentKeyConstants.COUNTRY_CODE))
        }
        provider.setUserType("RP")
        provider.setUserSubType(binding?.idRemoteProviderSpinner?.getSelectedItem().toString())
        provider.setProviderType("R")
        provider.setRole(Constants.ProviderRole.RD.toString())
        provider.setHospital("Remote Side Provider")
        provider.setNpiNumber(binding?.idNpiNumber?.getText().toString().trim())
        provider.setRemoteProviderType(binding?.idRemoteProviderSpinner?.getSelectedItem().toString())
        provider.setRemoteProviderId(java.lang.Long.valueOf(remoteProvideId))
        return provider
    }

    fun checkNpi(showError: Boolean) {
        if (binding?.idNpiNumber?.getText().toString().length < 10) {
            if (showError) {
                binding?.idNpiNumber?.setErrorMessage(getString(R.string.invalid_npi))
            }
            binding?.idNpiNumber?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_document,
                0,
                0,
                0
            )
        } else {
            binding?.idNpiNumber?.setErrorMessage("")
            binding?.idNpiNumber?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_document,
                0,
                R.drawable.ic_checkmark_edittext,
                0
            )
        }
    }

    private class ValidationTextWatcher(view: EditText) : TextWatcher {
        private val view: View
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.id_npi_number -> RemoteProviderSignUpSecond().checkNpi(false)
            }
            RemoteProviderSignUpSecond().checkButton()
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
                R.id.id_npi_number ->RemoteProviderSignUpSecond(). checkNpi(true)
            }
        }

        init {
            this.view = view
        }
    }


}