package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityTransferPatientBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.TransferPatientViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.AddNewPatientWard
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.HospitalList
import omnicurekotlin.example.com.patientsEndpoints.model.PatientTransferRequest
import omnicurekotlin.example.com.patientsEndpoints.model.Provider
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class TransferPatientActivity : BaseActivity() {


    private val TAG = TransferPatientActivity::class.java.simpleName
    private var lcpType: String? = null
    private var patientId: String? = ""
    private var binding: ActivityTransferPatientBinding? = null
    private var viewModel: TransferPatientViewModel? = null
    private var providerId: Long = 0
    private var strScreenCensus: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_patient)
        viewModel = ViewModelProvider(this).get(TransferPatientViewModel::class.java)
        patientId = intent.getStringExtra("patientId")
        initToolbar()
        setViews()
        setSpinnerAnotherHospital()
    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        binding?.toolbar?.title = getString(R.string.transfer_patient)
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
        lcpType = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.LCP_TYPE, "")
        providerId = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0L)
        Log.d(TAG, "providerProviderId$providerId")
    }

    private fun setViews() {
        strScreenCensus = intent.getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)
        if (lcpType.equals(Constants.KeyHardcodeToken.LCP_TYPE_HOME, ignoreCase = true)) {
            binding?.rbTransferWithInHospital?.visibility = View.GONE
            binding?.rbTransferAnotherHospital?.isChecked = true
            binding?.llTransferWithinWard?.visibility = View.GONE
            binding?.llTransferWithinHospitalProvider?.visibility = View.GONE
            binding?.llTransferAnotherHospital?.visibility = View.VISIBLE
            binding?.llTransferAnotherHospitalProvider?.visibility = View.VISIBLE
            setSpinnerAnotherHospital()
        } else {
            binding?.rbTransferWithInHospital?.visibility = View.VISIBLE
            binding?.rbTransferWithInHospital?.isChecked = true
            binding?.rbTransferAnotherHospital?.isEnabled = false
            setSpinnerWithInWard()
            setSpinnerWithinProvider()
            setSpinnerAnotherHospital()
        }
        binding?.radioGrp?.setOnCheckedChangeListener { _, _ ->
            if (binding?.rbTransferWithInHospital?.isChecked == true) {
                binding?.llTransferWithinWard?.visibility = View.VISIBLE
                binding?.llTransferWithinHospitalProvider?.visibility = View.VISIBLE
                binding?.llTransferAnotherHospital?.visibility = View.GONE
                binding?.llTransferAnotherHospitalProvider?.visibility = View.GONE
            } else if (binding?.rbTransferAnotherHospital?.isChecked == true) {
                binding?.llTransferWithinWard?.visibility = View.GONE
                binding?.llTransferWithinHospitalProvider?.visibility = View.GONE
                binding?.llTransferAnotherHospital?.visibility = View.VISIBLE
                binding?.llTransferAnotherHospitalProvider?.visibility = View.VISIBLE
            }
        }
        binding?.btnTransfer?.setOnClickListener { transferPatient() }
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {
//            UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snack.LENGTH_LONG);
            errMsg?.let {
                CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                    it, CustomSnackBar.TOP, 3000, 0)
            }?.show()
            return false
        }
        return true
    }

    private fun transferPatient() {
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snack.LENGTH_LONG);
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        binding?.btnTransfer?.isEnabled = false
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.transferFetchingData.toString()))
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        val patientTransferRequest = PatientTransferRequest()
        when (binding?.radioGrp?.checkedRadioButtonId) {
            R.id.rbTransferWithInHospital -> {
                patientTransferRequest.setPatientId(patientId)
                if (binding?.spinnerWithInWard?.selectedItemPosition!! <= 0) {
                    patientTransferRequest.setWardName(null)
                } else {
                    patientTransferRequest.setWardName(
                        binding?.spinnerWithInWard?.selectedItem.toString()
                    )
                }
                patientTransferRequest.setProviderId(binding?.spinnerWithinProvider?.tag as String)
                patientTransferRequest.setSummaryNote(binding?.edtSummaryNote?.text.toString())
                Log.i(TAG, "TRANSFER REQUEST $patientTransferRequest")
                token?.let { it ->
                    viewModel?.transferPatientWithInHospital(it, patientTransferRequest)?.observe(this) { it ->
                        val  commonResponse=it.body()
                        Log.i(TAG, "tranferWithinHospitalResponse $commonResponse")
                        dismissProgressBar()
                        if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                            onTransferPatientWithInHospitalSuccess(commonResponse)
                        } else {
                            binding?.btnTransfer?.isEnabled = true
                            val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse?.getErrorMessage(),
                                Constants.API.register)
                            //                        UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snack.LENGTH_LONG);
                            errMsg?.let {
                                CustomSnackBar.make(binding?.idContainerLayout, this,
                                    CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0)?.show()
                            }
                        }
                    }
                }
            }
            R.id.rbTransferAnotherHospital -> {
                patientTransferRequest.setPatientId(patientId)
                patientTransferRequest.setWardName(null)
                patientTransferRequest.setHospitalId(binding?.spinnerAnotherHospital?.tag as String)
                patientTransferRequest.setProviderId(binding?.spinnerAnotherProvider?.tag as String)
                patientTransferRequest.setSummaryNote(binding?.edtSummaryNote?.text.toString())
                token?.let { it ->
                    viewModel?.transferPatientToAnotherHospital(it, patientTransferRequest)?.observe(this) { it ->
                        val commonResponse=it.body()
                        Log.i(TAG, "transferAnotherHospitalResponse $commonResponse")
                        dismissProgressBar()
                        if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                            onTransferPatientWithInHospitalSuccess(commonResponse)
                        } else {
                            binding?.btnTransfer?.isEnabled = true
                            val errMsg: String? = ErrorMessages().getErrorMessage(this,
                                commonResponse?.getErrorMessage(), Constants.API.register)
                            //                        UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snack.LENGTH_LONG);
                            errMsg?.let {
                                CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING, it,
                                    CustomSnackBar.TOP, 3000, 0)
                            }?.show()
                        }
                    }
                }
            }
        }
    }

    private fun onTransferPatientWithInHospitalSuccess(response: CommonResponse?) {
        if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(Constants.IntentKeyConstants.SCREEN_CENSUS, ignoreCase = true)) {
            CustomSnackBar.make(
                binding?.idContainerLayout,
                this, CustomSnackBar.SUCCESS, getString(R.string.patient_transfer_successfully), CustomSnackBar.TOP,
                3000, 4)?.show()
        } else {
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.SUCCESS,
                getString(R.string.patient_transfer_successfully), CustomSnackBar.TOP, 3000, 1)?.show()
        }
    }

    private fun getWardNames(wards: List<AddNewPatientWard>): LinkedHashMap<String, String> {
        val wardMap = LinkedHashMap<String, String>()
        for (i in wards.indices) {
            val patientWard: AddNewPatientWard = wards[i]
            if (patientWard.getWardName() != null) {
                wardMap[patientWard.getWardName()!!] = patientWard.getWardName()!!
            }
        }
        return wardMap
    }


    private fun setSpinnerWithInWard() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snack.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        showProgressBar()
        val hospitalId: Long = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.HOSPITAL_ID, 0L)
        val wardList = ArrayList<String>()
        val providerMap = LinkedHashMap<String, String>()
        wardList.add(getString(R.string.select_wards))
        val errMsg = AtomicReference("")
        viewModel?.getWardsList(hospitalId)?.observe(this) {
            val response=it.body()
            dismissProgressBar()
            Log.d(TAG, "Wards response$response")
            if (response?.getStatus() != null && response.getStatus()!!) {
                if (response.getWards() != null && response.getWards()!!.isNotEmpty()) {
                    providerMap.putAll(getWardNames(response.getWards()!! as List<AddNewPatientWard>))
                    wardList.addAll(providerMap.keys)
                    setSpinner(wardList, errMsg)
                }
            } else {
                setSpinner(wardList, errMsg)
                errMsg.set(
                    ErrorMessages().getErrorMessage(this, java.lang.String.valueOf(response?.getErrorId()), Constants.API.getHospital)
                )
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg.get(), Snackbar.LENGTH_LONG);
                CustomSnackBar.make(binding?.idContainerLayout, this,
                    CustomSnackBar.WARNING, errMsg.get(), CustomSnackBar.TOP, 3000, 0)?.show()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSpinner(wardList: ArrayList<String>, errMsg: AtomicReference<String>) {
        val remoteProviderListAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, wardList)
        binding?.spinnerWithInWard?.adapter = remoteProviderListAdapter
        binding?.spinnerWithInWard?.setOnTouchListener { _, motionEvent ->
            if (!TextUtils.isEmpty(errMsg.get())) {
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> {

                        CustomSnackBar.make(
                            binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                            errMsg.get(), CustomSnackBar.TOP, 3000, 0)?.show()
                    }
                }
            }
            false
        }
        binding?.spinnerWithInWard?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
                try {
                    val spinnerText = view as TextView
                    val ward = wardList[position]
                    binding?.spinnerWithInWard?.tag = ward
                    spinnerText.maxLines = 1
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(TransferPatientActivity(), spinnerText, R.color.black)
                        binding?.spinnerWithInWard?.let {
                            UtilityMethods().setDrawableBackground(this, it, R.drawable.spinner_drawable_selected)
                        }
                    } else {
                        UtilityMethods().setTextViewColor(TransferPatientActivity(), spinnerText, R.color.gray_500)
                        binding?.spinnerWithInWard?.let {
                            UtilityMethods().setDrawableBackground(this, it, R.drawable.spinner_drawable)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun getHospitalProviderNames(lists: List<Provider>): LinkedHashMap<String, String> {
        val wardMap = LinkedHashMap<String, String>()
        val providerId: Long = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0L)
        Log.d(TAG, "providerId : $providerId")
        for (i in lists.indices) {
            val providerList: Provider = lists[i]
            if (providerList.getName() != null) {
                if (providerId != providerList.getId()) {
                    wardMap[providerList.getName()!!] = java.lang.String.valueOf(providerList.getId())
                }
            }
        }
        return wardMap
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSpinnerWithinProvider() {
        if (!UtilityMethods().isInternetConnected(this)) {

            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        val hospitalId: Long = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.HOSPITAL_ID, 0L)
        val errMsg = AtomicReference("")
        val providerList = ArrayList<String>()
        val providerMap = LinkedHashMap<String, String>()
        providerList.add(getString(R.string.select_provider))
        println("given values $hospitalId $providerId")
        viewModel?.getProviderListResponse(hospitalId, providerId)?.observe(this) {
            val response=it.body()
            Log.d(TAG, "getProviderListResponse : " + Gson().toJson(response))
            if (response?.getStatus() != null && response.getStatus()!!) {
                if (response.getProviderList() != null && response.getProviderList()!!.isNotEmpty()) {
                    providerMap.putAll(getHospitalProviderNames(response.getProviderList()!! as List<Provider>))
                    providerList.addAll(providerMap.keys)
                }
            } else {
                errMsg.set(
                    ErrorMessages().getErrorMessage(this, java.lang.String.valueOf(response?.getErrorMessage()),
                        Constants.API.getHospital)
                )
            }
        }
        val remoteProviderListAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, providerList)
        binding?.spinnerWithinProvider?.adapter = remoteProviderListAdapter
        binding?.spinnerWithinProvider?.setOnTouchListener { _, motionEvent ->
            if (!TextUtils.isEmpty(errMsg.get())) {
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> {

            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                            errMsg.get(), CustomSnackBar.TOP, 3000, 0)?.show()
                    }
                }
            }
            false
        }
        binding?.spinnerWithinProvider?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
                try {
                    val spinnerText = view as TextView
                    val providerName = providerList[position]
                    val providerId = providerMap[providerName]
                    binding?.spinnerWithinProvider?.tag = providerId
                    spinnerText.maxLines = 1
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(TransferPatientActivity(), spinnerText, R.color.black)
                        binding?.spinnerWithinProvider?.let {
                            UtilityMethods().setDrawableBackground(this, it, R.drawable.spinner_drawable_selected)
                        }
                    } else {
                        UtilityMethods().setTextViewColor(TransferPatientActivity(), spinnerText, R.color.gray_500)
                        binding?.spinnerWithinProvider?.let {
                            UtilityMethods().setDrawableBackground(this, it, R.drawable.spinner_drawable)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSpinnerAnotherHospital() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snack.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        val errMsg = AtomicReference("")
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        val hospitalList = ArrayList<String?>()
        val hospitalIds = ArrayList<String?>()
        hospitalList.add(getString(R.string.sel_hospital))
        token?.let {
            patientId?.let { it1 ->
                viewModel?.getHospitalList(it, it1)?.observe(this) {
                    val response=it.body()
                    println("Hospital response $response")
                    if (response?.getStatus() != null && response.getStatus()!!) {
                        if (response.getHospitalList() != null && response.getHospitalList()!!.isNotEmpty()) {
                            hospitalList.clear()
                            hospitalIds.clear()
                            hospitalList.add(getString(R.string.sel_hospital))
                            for (i in response.getHospitalList()!!.indices) {
                                val hospital: HospitalList? = response.getHospitalList()!![i]
                                val myHospName: String? = PrefUtility().getStringInPref(this,
                                    Constants.SharedPrefConstants.HOSPITAL_NAME, "")
                                if (hospital?.getName() != null && !hospital.getName().equals(myHospName)
                                ) {
                                    hospitalList.add(hospital.getName())
                                    hospitalIds.add(hospital.getId())
                                }
                            }
                        }
                    } else {
                        errMsg.set(ErrorMessages().getErrorMessage(this, java.lang.String.valueOf(response?.getErrorMessage()), Constants.API.getHospital)
                        )
                        //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg.get(), Snack.LENGTH_LONG);
                    }
                }
            }
        }
        val remoteProviderListAdapter =
            ArrayAdapter(this, R.layout.spinner_custom_text, hospitalList)
        binding?.spinnerAnotherHospital?.adapter = remoteProviderListAdapter
        binding?.spinnerAnotherHospital?.setOnTouchListener { _, motionEvent ->
            if (!TextUtils.isEmpty(errMsg.get())) {
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> {
                    CustomSnackBar.make(
                            binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                            errMsg.get(), CustomSnackBar.TOP, 3000, 0)?.show()
                    }
                }
            }
            false
        }
        binding?.spinnerAnotherHospital?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
                try {
                    // To clear provider list
                    val providerList = ArrayList<String>()
                    providerList.add(getString(R.string.select_provider))
                    val remoteProviderListAdapter: ArrayAdapter<String> = ArrayAdapter(TransferPatientActivity(), R.layout.spinner_custom_text, providerList)

                    binding?.spinnerAnotherProvider?.adapter = remoteProviderListAdapter
                    val spinnerText = view as TextView
                    val hospitalId = hospitalIds[position - 1]
                    binding?.spinnerAnotherHospital?.tag = hospitalId
                    println("spinner pos $position $spinnerText")
                    spinnerText.maxLines = 1
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(TransferPatientActivity(), spinnerText, R.color.black)
                        UtilityMethods().setDrawableBackground(this, binding?.spinnerAnotherHospital!!,
                            R.drawable.spinner_drawable_selected)
                        hospitalId?.toLong()?.let { setSpinnerAnotherProvider(it) }
                    } else {
                        UtilityMethods().setTextViewColor(TransferPatientActivity(), spinnerText, R.color.gray_500)
                        UtilityMethods().setDrawableBackground(this,
                            binding?.spinnerAnotherHospital!!, R.drawable.spinner_drawable)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


    private fun setSpinnerAnotherProvider(hospitalId: Long) {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout, this,
                CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.transferPatient.toString()))
        val providerList = ArrayList<String>()
        val providerMap = LinkedHashMap<String, String>()
        providerList.add(getString(R.string.select_provider))
        viewModel?.getProviderListResponse(hospitalId, providerId)?.observe(this) { it ->
            val response=it.body()
            Log.d(TAG, "PROVIDER RESPONSE$response")
            dismissProgressBar()
            if (response?.getStatus() != null && response.getStatus()!!) {
                if (response.getProviderList() != null && response.getProviderList()!!.isNotEmpty()) {
                    providerMap.putAll(getHospitalProviderNames(response.getProviderList()!! as List<Provider>))
                    providerList.addAll(providerMap.keys)
                }
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    java.lang.String.valueOf(response?.getErrorMessage()), Constants.API.getHospital)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let {
                    CustomSnackBar.make(binding?.idContainerLayout, this,
                        CustomSnackBar.WARNING, it, CustomSnackBar.TOP,
                        3000, 0)
                }?.show()
            }
        }
        val remoteProviderListAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, providerList)
        binding?.spinnerAnotherProvider?.adapter = remoteProviderListAdapter
        binding?.spinnerAnotherProvider?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
                try {
                    val spinnerText = view as TextView
                    val providerName = providerList[position]
                    val providerId = providerMap[providerName]
                    binding?.spinnerAnotherProvider?.tag = providerId
                    spinnerText.maxLines = 1
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(this@TransferPatientActivity, spinnerText, R.color.black)
                        UtilityMethods().setDrawableBackground(this, binding?.spinnerAnotherProvider!!,
                            R.drawable.spinner_drawable_selected)
                    } else {
                        UtilityMethods().setTextViewColor(this@TransferPatientActivity,
                            spinnerText, R.color.gray_500)
                        UtilityMethods().setDrawableBackground(this,
                            binding?.spinnerAnotherProvider!!, R.drawable.spinner_drawable)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }
}
