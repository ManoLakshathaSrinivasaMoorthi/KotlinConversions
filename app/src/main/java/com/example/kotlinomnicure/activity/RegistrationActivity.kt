package com.example.kotlinomnicure.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityRegistrationBinding
import com.example.kotlinomnicure.helper.MobileNumberFormatter
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*

import com.example.kotlinomnicure.viewmodel.RegistrationViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import omnicurekotlin.example.com.userEndpoints.model.Provider
import omnicurekotlin.example.com.userEndpoints.model.RemoteProvider
import java.util.*

class RegistrationActivity : BaseActivity() {

    private val TAG = RegistrationActivity::class.java.simpleName
    private var binding: ActivityRegistrationBinding? = null
    private var viewModel: RegistrationViewModel? = null
    private var providerType: String? = null
    private var remoteProvideId: String? = null
    private var context: Context =RegistrationActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        setView()
    }

    private fun setView() {
        val createProfileTxt = getString(R.string.create_your_profile)

        setOnclickListener()
        setLocalCareProviderSpinner()
        setHospitalsSpinner()
        setRemoteProviderSpinner()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnclickListener() {
        binding?.idBackButton?.setOnClickListener { finish() }
        binding?.idContinue?.setOnClickListener { onClickContinue() }
        binding?.idPassword?.setOnEditorActionListener { _, id, _ ->
            if (id === EditorInfo.IME_ACTION_DONE) {
                Log.i(TAG, "onEditorAction: on action done")
                onClickContinue()
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
                    MobileNumberFormatter().formatMobileNumber(
                        str,
                        binding?.idPhoneNumber!!,
                        FIRST_SEP_LENGTH,
                        SECOND_SEP_LENGTH
                    )
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' }.isEmpty()) {
                    binding?.countryStdCode?.visibility = View.GONE
                } else {
                    binding?.countryStdCode?.visibility = View.VISIBLE
                }
            }
        })
        binding?.idPassword?.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action === MotionEvent.ACTION_DOWN) {
                if (event.rawX >= binding?.idPassword?.right!! - binding?.idPassword?.compoundDrawables
                        ?.get(DRAWABLE_RIGHT)?.bounds?.width()!!
                ) {
                    if (binding?.idPassword?.inputType === InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        binding?.idPassword?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding?.idPassword?.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0,
                            R.drawable.ic_visibility, 0
                        )
                        binding?.idPassword?.setSelection(binding?.idPassword!!.text.length)
                    } else {
                        binding?.idPassword?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding?.idPassword?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
                        binding?.idPassword?.setSelection(binding?.idPassword!!.text.length)
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
        try {
            val agreementTxt: String = binding?.agreementCheckBox?.text.toString()
            val ss = SpannableString(agreementTxt)
            val drawableBG: Drawable? = binding?.agreementCheckBox?.background
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    Log.d(TAG, "onClick: of user agreement")
                    binding?.agreementCheckBox?.isEnabled = false
                    binding?.agreementCheckBox?.background = resources.getDrawable(R.drawable.transparent_bg)
                    binding?.agreementCheckBox?.isEnabled = true
                    val uri = Uri.parse(Constants.USER_AGREEMENT_LINK)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }

            }
            ss.setSpan(
                clickableSpan, agreementTxt.indexOf(getString(R.string.agreement)),
                agreementTxt.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            ss.setSpan(
                binding?.agreementCheckBox?.currentTextColor?.let { ForegroundColorSpan(it) },
                agreementTxt.indexOf(getString(R.string.agreement)), agreementTxt.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding?.agreementCheckBox?.text = ss
            binding?.agreementCheckBox?.movementMethod = LinkMovementMethod.getInstance()
            binding?.agreementCheckBox?.setOnCheckedChangeListener { _, isChecked ->
                binding?.agreementCheckBox?.background = drawableBG
                val color = if (isChecked) R.color.white else R.color.gray_500
                ss.setSpan(
                    ForegroundColorSpan(resources.getColor(color)),
                    agreementTxt.indexOf(getString(R.string.agreement)), agreementTxt.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding?.agreementCheckBox?.text = ss
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setLocalCareProviderSpinner() {
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        val providerType = arrayOf(
            "Select Provider Type",
            "Bedside Provider",
            "Home Provider"
        )
        val spinnerArrayAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, providerType)
        binding?.idLcpProviderTypeSpinner?.adapter = spinnerArrayAdapter
        binding?.idLcpProviderTypeSpinner?.onItemSelectedListener = object :
            OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val spinnerText = view as TextView
                spinnerText.maxLines = 1
                if (position != 0) {
                    UtilityMethods().setTextViewColor(RegistrationActivity(), spinnerText, R.color.white)
                    UtilityMethods().setDrawableBackground(context, binding?.idLcpProviderTypeSpinner!!, R.drawable.spinner_drawable_selected)
                } else {
                    UtilityMethods().setTextViewColor(
                        RegistrationActivity(),
                        spinnerText,
                        R.color.gray_500
                    )
                    UtilityMethods().setDrawableBackground(
                        context,
                        binding?.idLcpProviderTypeSpinner!!, R.drawable.spinner_drawable
                    )
                }
                if (position == 1) {
                    binding?.idHospitalContainer?.visibility = View.VISIBLE
                } else {
                    binding?.idHospitalContainer?.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setHospitalsSpinner() {
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        //showProgressBar();
        val hospitals = ArrayList<String>()
        val hospitalMap = LinkedHashMap<String, Long>()
        hospitals.add(getString(R.string.sel_hospital))
        viewModel?.getHospitalList()?.observe(this) { it ->
            val response=it.body()
            if (response?.getStatus() != null && response.getStatus()!!) {
                if (response.getHospitalList() != null && response.getHospitalList()!!.isNotEmpty()) {
                    hospitalMap.putAll(getHospitalNames(response.getHospitalList() as List<Hospital>))
                    hospitals.addAll(hospitalMap.keys)
                    hospitals.remove("Remote Side Provider")
                    hospitals.remove("Home")
                }

            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    response?.getErrorMessage(), Constants.API.getHospital
                )

                errMsg?.let {
                    CustomSnackBar.make(
                        binding?.idContainerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        it,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
        }
        val hospitalListAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, hospitals)

        binding?.idSpinnerHospital?.adapter = hospitalListAdapter
        binding?.idSpinnerHospital?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                try {
                    val spinnerText = view as TextView
                    val hospitalName = hospitals[position]
                    val hospitalId = hospitalMap[hospitalName]
                    if (hospitalId != null) {
                        binding?.idSpinnerHospital?.tag = hospitalId
                    }
                    spinnerText.maxLines = 1
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(
                            RegistrationActivity(),
                            spinnerText,
                            R.color.white
                        )
                        UtilityMethods().setDrawableBackground(
                            context, binding?.idSpinnerHospital!!,
                            R.drawable.spinner_drawable_selected
                        )
                    } else {
                        UtilityMethods().setTextViewColor(
                            RegistrationActivity(), spinnerText, R.color.gray_500
                        )
                        UtilityMethods().setDrawableBackground(
                            context,
                            binding!!.idSpinnerHospital, R.drawable.spinner_drawable
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setRemoteProviderSpinner() {
        if (!UtilityMethods().isInternetConnected(this)!!) {
            CustomSnackBar.make(
                binding?.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }

        val remoteProvider = ArrayList<String?>()
        val providerMap = LinkedHashMap<String?, String>()
        remoteProvider.add(getString(R.string.select_provider_types))
        viewModel?.getRemoteProviderList()?.observe(this) { it ->
            val response=it.body()
            Log.d(TAG, "remote provider data" + response?.getStatus())
            if (response?.getStatus() != null && response.getStatus()!!) {
                if (response.getRemoteProviderTypeList() != null && response.getRemoteProviderTypeList()!!
                        .isNotEmpty()
                ) {
                    providerMap.putAll(getRemoteProviderNames(response.getRemoteProviderTypeList() as List<RemoteProvider>))
                    remoteProvider.addAll(providerMap.keys)
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    java.lang.String.valueOf(response?.getErrorId()), Constants.API.getHospital)

                errMsg?.let {
                    CustomSnackBar.make(
                        binding?.idContainerLayout,
                        this, CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0
                    )?.show()
                }
            }
        }
        val remoteProviderListAdapter =
            ArrayAdapter(this, R.layout.spinner_custom_text, remoteProvider)
        //hospitalListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding?.idRemoteProviderSpinner?.adapter = remoteProviderListAdapter
        binding?.idRemoteProviderSpinner?.onItemSelectedListener = object :
            OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                try {
                    val spinnerText = view as TextView
                    providerType = remoteProvider[position]
                    remoteProvideId = providerMap[providerType]
                    binding?.idRemoteProviderSpinner?.tag = remoteProvideId
                    spinnerText.maxLines = 1
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(
                            RegistrationActivity(),
                            spinnerText,
                            R.color.white
                        )
                        UtilityMethods().setDrawableBackground(
                            context, binding?.idRemoteProviderSpinner!!,
                            R.drawable.spinner_drawable_selected
                        )
                    } else {
                        UtilityMethods().setTextViewColor(
                            RegistrationActivity(),
                            spinnerText,
                            R.color.gray_500
                        )
                        UtilityMethods().setDrawableBackground(
                            context, binding!!.idRemoteProviderSpinner,
                            R.drawable.spinner_drawable
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (errMsg!!.isNotEmpty()) {

            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                errMsg, CustomSnackBar.TOP, 3000, 0
            )?.show()
            return false
        }
        return true
    }

    private fun onClickContinue() {
        binding?.idContinue?.let { handleMultipleClick(it) }
        if (!isValid()) {
            return
        }
        val provider = Provider()
        provider.setFname(binding?.idFirstName?.text.toString().trim())
        provider.setLname(binding?.idLastName?.text.toString().trim())
        provider.setEmail(binding?.idEmailId?.text.toString().trim())
        provider.setPhone(binding?.idPhoneNumber?.text.toString().trim().replace("-", ""))
        if (binding?.radioLocalCareProvider?.isChecked!!) {
            provider.setProviderType("L")
        } else if (binding?.radioBtnRemoteSide?.isChecked!!) {
            provider.setProviderType("R")
            provider.setRole(Constants.ProviderRole.RD.toString())
            provider.setHospital("Remote Side Provider")
            provider.setNpiNumber(binding?.idNpiNumber?.text.toString().trim())
        }
        if (binding?.radioLocalCareProvider?.isChecked!! && binding?.idLcpProviderTypeSpinner?.selectedItemPosition === 1) {
            provider.setLcpType("B")
            provider.setRole(Constants.ProviderRole.BD.toString())
            provider.setHospital(binding?.idSpinnerHospital?.selectedItem.toString())
            if (binding?.idSpinnerHospital?.tag != null) {
                provider.setHospitalId(binding?.idSpinnerHospital?.tag as Long)
            }
        } else if (binding?.radioLocalCareProvider?.isChecked!! && binding?.idLcpProviderTypeSpinner?.selectedItemPosition === 2) {
            provider.setLcpType("H")
        }
        if (binding?.radioBtnRemoteSide?.isChecked == true) {
            provider.setRemoteProviderType(
                binding?.idRemoteProviderSpinner?.selectedItem.toString()
            )
            provider.setRemoteProviderId(java.lang.Long.valueOf(remoteProvideId))

            provider.setPassword(binding?.idPassword?.text.toString().trim())
            if (!UtilityMethods().isInternetConnected(this)!!) {

                CustomSnackBar.make(
                    binding?.idContainerLayout, this,
                    CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP, 3000, 0
                )?.show()
                return
            }
            Log.d("PROVIDER", Gson().toJson(provider))
            showProgressBar(PBMessageHelper().getMessage(this, Constants.API.register.toString()))
            viewModel?.registerProvider(provider)?.observe(this) { it ->
                val commonResponse=it.body()
                dismissProgressBar()
                println("response register $commonResponse")
                if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                    PrefUtility().saveStringInPref(applicationContext, Constants.redirectValidation.EMAIL,
                        binding?.idEmailId?.text.toString())
                    PrefUtility().saveStringInPref(applicationContext,
                        Constants.redirectValidation.PASSWORD, binding?.idPassword?.text.toString())
                    commonResponse.getProvider()?.getId()?.let {
                        PrefUtility().saveLongInPref(applicationContext, Constants.redirectValidation.ID, it)
                    }
                    commonResponse.getProvider()?.let { onRegisterSuccess(it) }
                } else {
                    val errMsg: String? = ErrorMessages().getErrorMessage(
                        this, commonResponse?.getErrorMessage(),
                        Constants.API.register
                    )
                         errMsg?.let {
                        CustomSnackBar.make(
                            binding?.idContainerLayout, this,
                            CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0
                        )?.show()
                    }
                }
            }
        }

    }

    private fun onRegisterSuccess(provider: Provider) {
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra(Constants.IntentKeyConstants.PROVIDER_ID, provider.getId())
        intent.putExtra(Constants.IntentKeyConstants.MOBILE_NO, provider.getPhone())
        startActivity(intent)
    }

    private fun getHospitalNames(hospitals: List<Hospital>): LinkedHashMap<String, Long> {
        val hospitalMap = LinkedHashMap<String, Long>()
        for (i in hospitals.indices) {
            val hospital: Hospital = hospitals[i]
            if (hospital.getId() != null) {
                hospitalMap[hospital.getName()!!.trim()] = hospital.getId()!!
            }
        }
        return hospitalMap
    }

    private fun getRemoteProviderNames(providers: List<RemoteProvider>): LinkedHashMap<String?, String> {
        val providerMap = LinkedHashMap<String?, String>()
        for (i in providers.indices) {
            val remoteProvider: RemoteProvider = providers[i]
            if (remoteProvider.getId() != null) {
                providerMap[remoteProvider.getType()?.trim()] = remoteProvider.getId()!!
            }
        }
        return providerMap
    }

    fun onClickLocalCare() {
        binding?.idHospitalContainer?.visibility = View.VISIBLE
        binding?.idNpiNumber?.visibility = View.GONE
        binding?.idRemoteProviderContainer?.visibility = View.GONE
        binding?.idLcpProviderTypeContainer?.visibility = View.VISIBLE
    }

    fun onClickRemoteSide(v: View?) {
        binding?.idHospitalContainer?.visibility = View.GONE
        binding?.idNpiNumber?.visibility = View.VISIBLE
        binding?.idRemoteProviderContainer?.visibility = View.VISIBLE
        binding?.idLcpProviderTypeContainer?.visibility = View.GONE
    }

    fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHandler?.postDelayed(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            val checkPermissionResult = checkSelfPermissions()
        }, 500)
    }

    private fun checkSelfPermissions(): Boolean {
        return checkSelfPermission(
            Manifest.permission.RECORD_AUDIO,
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO
        ) &&
                checkSelfPermission(
                    Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA
                ) &&
                checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
                )
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i("checkSelfPermission ", "$permission $requestCode")
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("onRequestPermissions", requestCode.toString() + " " +
                permissions.contentToString() + " " + grantResults.contentToString()
        )
        when (requestCode) {
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, ConstantApp().PERMISSION_REQ_ID_CAMERA)
                }
                else {
                    finish()
                }
            }
            ConstantApp().PERMISSION_REQ_ID_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)
                } else {
                    finish()
                }
            }
            ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) { }
                else {
                    finish()
                }
            }
        }
    }
}

