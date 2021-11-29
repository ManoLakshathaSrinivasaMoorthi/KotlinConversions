package com.mvp.omnicure.kotlinactivity.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.dailytasksamplepoc.databinding.RemoteProviderSignUpSecondBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.BaseActivity
import com.google.gson.Gson

import java.lang.Exception
import java.util.ArrayList
import java.util.LinkedHashMap

class RemoteProviderSignUpSecond : BaseActivity() {
    private val TAG = RemoteProviderSignUpSecond::class.java.simpleName
    protected var binding: RemoteProviderSignUpSecondBinding? = null
    protected var viewModel: RemoteProviderSignUpViewModel? = null
    private var providerType: String? = null
    private var remoteProvideId: String? = null
    private var ctx: Context =com.mvp.omnicure.kotlinactivity.activity.RemoteProviderSignUpSecond()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.remote_provider_sign_up_second)
        viewModel = ViewModelProviders.of(this).get(
            RemoteProviderSignUpViewModel::class.java
        )
        setView()
    }

    @SuppressLint("StringFormatInvalid", "ClickableViewAccessibility")
    override fun setView() {
        val labelColor = resources.getColor(R.color.btn_bg)
        val сolorString = String.format("%X", labelColor).substring(2)
        binding?.txtAlreadySignIn?.setText(
            Html.fromHtml(
                String.format(
                    "Already have account?" + "<font color='#%s'><b>" + " LOG IN</b></font>",
                    сolorString
                )
            )
        )
        binding?.txtTermsAndCondition?.setText(Html.fromHtml("By signing up you agree to " + Buildconfic.value() + " <u>Terms & Conditions</u>"))
        binding?.txtAlreadySignIn?.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "onClick: of already signin ")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        setRemoteProviderSpinner()
        binding?.idRemoteProviderSpinner?.setOnTouchListener(OnTouchListener { v, event ->
            Log.d(TAG, "onTouch : ")
            binding?.idRpProviderTypeContainer?.setEnabled(true)
            binding?.idRpProviderTypeContainer?.setFocusableInTouchMode(true)
            binding?.idRpProviderTypeContainer?.requestFocus()
            false
        })
        binding?.idNpiNumber?.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                binding?.idNpiNumber?.addTextChangedListener(GenericTextWatcher(binding!!.idNpiNumber))
                checkNpi(true)
            }
        })
        binding?.btnNext?.setOnClickListener(View.OnClickListener {
            onClickNext()

        })
        binding?.txtTermsAndCondition?.setOnClickListener(View.OnClickListener {
            val i = Intent(this, TermsAndConditionsActivity::class.java)
            i.putExtra("isSelected", binding!!.checkbox.isChecked())
            startActivityForResult(i, 1)

        })
        binding?.checkbox?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            checkButton()
            buttonView.isFocusableInTouchMode = true
            buttonView.requestFocus()
            buttonView.isFocusableInTouchMode = false
        })
        binding?.idNpiNumber?.addTextChangedListener(binding?.let { ValidationTextWatcher(it.idNpiNumber) })
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
        val validNpi = ValidationUtil.checkNpi(binding?.idNpiNumber)
        if (validNpi && binding!!.idRemoteProviderSpinner.getSelectedItemPosition() > 0 && binding!!.checkbox.isChecked()) {
            binding!!.btnNext.setEnabled(true)
        } else {
            binding?.btnNext?.setEnabled(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                var agreeFlag = false
                if (data != null) {
                    agreeFlag = data.getBooleanExtra("agree", false)
                }
                binding?.checkbox?.setChecked(agreeFlag)
            }
        }
    }

    private fun onClickNext() {
        binding?.let { handleMultipleClick(it.btnNext) }
        if (!isValid()) {
            return
        }
        if (!UtilityMethods.isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            ).show()
            return
        }
        val provider = createPatientObject()
        Log.d("RemotePROVIDER", Gson().toJson(provider))
        showProgressBar(PBMessageHelper.getMessage(this, Constants.API.register.toString()))

        viewModel!!.registerProvider(provider).observe(this, { commonResponse: CommonResponse? ->
            dismissProgressBar()
            if (commonResponse != null && commonResponse.status != null && commonResponse.status) {
                PrefUtility.saveStringInPref(
                    applicationContext,
                    Constants.redirectValidation.EMAIL,
                    provider.email
                )
                PrefUtility.saveStringInPref(
                    applicationContext,
                    Constants.redirectValidation.PASSWORD,
                    provider.password
                )
                PrefUtility.saveLongInPref(
                    applicationContext,
                    Constants.redirectValidation.ID,
                    commonResponse.user.id
                )
                onRegisterSuccess(commonResponse.user)
            } else {
                val errMsg = ErrorMessages.getErrorMessage(
                    this,
                    commonResponse!!.errorMessage,
                    Constants.API.register
                )

                CustomSnackBar.make(
                    binding?.idContainerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
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
        mHandler.postDelayed(Runnable { view.isEnabled = true }, 500)
    }

    private fun isValid(): Boolean {
        val errMsg = ValidationUtil.isValidate(binding)
        if (!errMsg.isEmpty()) {

            CustomSnackBar.make(
                binding?.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                errMsg,
                CustomSnackBar.TOP,
                3000,
                0
            ).show()
            return false
        }
        return true
    }

    private fun setRemoteProviderSpinner() {
        if (!UtilityMethods.isInternetConnected(this)) {

            CustomSnackBar.make(
                binding?.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            ).show()
            return
        }
        showProgressBar()
        val remoteProvider = ArrayList<String?>()
        val providerMap = LinkedHashMap<String?, String>()
        remoteProvider.add(getString(R.string.select_provider_types))
        viewModel!!.remoteProviderList.observe(this, { response: RemoteProviderListResponse? ->
            dismissProgressBar()
            Log.d(TAG, "remote provider data$response")
            if (response != null && response.status != null && response.status) {
                if (response.remoteProviderTypeList != null && !response.remoteProviderTypeList
                        .isEmpty()
                ) {
                    providerMap.putAll(getRemoteProviderNames(response.remoteProviderTypeList)!!)
                    remoteProvider.addAll(providerMap.keys)
                    setRemoteSpinner(remoteProvider, providerMap)
                }
            } else {
                val errMsg = ErrorMessages.getErrorMessage(
                    this,
                    response!!.errorId.toString(),
                    Constants.API.getHospital
                )

                CustomSnackBar.make(
                    binding?.idContainerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
            }
        })
    }

    private fun setRemoteSpinner(
        remoteProvider: ArrayList<String?>,
        providerMap: LinkedHashMap<String?, String>
    ) {
        val remoteProviderListAdapter =
            ArrayAdapter(this, R.layout.spinner_custom_text, remoteProvider)

        binding?.idRemoteProviderSpinner?.setAdapter(remoteProviderListAdapter)
        binding?.idRemoteProviderSpinner?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                binding!!.idRpProviderTypeContainer.setFocusableInTouchMode(true)
                binding!!.idRpProviderTypeContainer.requestFocus()
                binding!!.idRpProviderTypeContainer.setFocusableInTouchMode(false)
                try {
                    checkButton()
                    if (position == 0) {
                        binding!!.verified.setVisibility(View.GONE)
                    } else {
                        binding!!.verified.setVisibility(View.VISIBLE)
                    }
                    val spinnerText = view as TextView
                    providerType = remoteProvider[position]
                    remoteProvideId = providerMap[providerType]
                    binding!!.idRemoteProviderSpinner.setTag(remoteProvideId)
                    if (spinnerText != null) {
                        spinnerText.maxLines = 1
                        if (spinnerText.text.toString().length >= 30) {
                            spinnerText.textSize = 13f
                        } else if (spinnerText.text.toString().length >= 25) {
                        } else {
                            spinnerText.textSize = 16f
                        }
                        if (position != 0) {
                            UtilityMethods.setTextViewColor(
                                ctx,
                                spinnerText,
                                R.color.black
                            )

                        } else {
                            UtilityMethods.setTextViewColor(
                                ctx,
                                spinnerText,
                                R.color.title_black
                            )

                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "exception", e.cause)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                binding!!.idRpProviderTypeContainer.setFocusableInTouchMode(true)
                binding!!.idRpProviderTypeContainer.requestFocus()
                binding!!.idRpProviderTypeContainer.setFocusableInTouchMode(false)
            }
        })
    }

    private fun getRemoteProviderNames(providers: List<RemoteProvider>): LinkedHashMap<String?, String>? {
        val providerMap = LinkedHashMap<String?, String>()
        for (i in providers.indices) {
            val remoteProvider = providers[i]
            if (remoteProvider.id != null) {
                providerMap[remoteProvider.name.trim { it <= ' ' }] = remoteProvider.id
            }
        }
        return providerMap
    }

    private fun createPatientObject(): Provider {
        val providerID = PrefUtility.getProviderId(this)
        val role = PrefUtility.getRole(this)
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
        provider.userType = "RP"
        provider.userSubType = binding?.idRemoteProviderSpinner?.getSelectedItem().toString()
        provider.providerType = "R"
        provider.role = Constants.ProviderRole.RD.toString()
        provider.hospital = "Remote Side Provider"
        provider.npiNumber = binding?.idNpiNumber?.getText().toString().trim()
        provider.remoteProviderType = binding?.idRemoteProviderSpinner?.getSelectedItem().toString()
        provider.remoteProviderId = java.lang.Long.valueOf(remoteProvideId)
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
                R.id.id_npi_number -> com.mvp.omnicure.kotlinactivity.activity.RemoteProviderSignUpSecond().checkNpi(false)
            }
            com.mvp.omnicure.kotlinactivity.activity.RemoteProviderSignUpSecond().checkButton()
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
                R.id.id_npi_number -> com.mvp.omnicure.kotlinactivity.activity.RemoteProviderSignUpSecond().checkNpi(true)
            }
        }

        init {
            this.view = view
        }
    }

}