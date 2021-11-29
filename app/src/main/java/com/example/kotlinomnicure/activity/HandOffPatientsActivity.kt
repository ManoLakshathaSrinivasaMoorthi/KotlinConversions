package com.example.kotlinomnicure.activity

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.HandOffPatientAdapter
import com.example.kotlinomnicure.databinding.ActivityHandOffPatientsBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.HandOffPatientViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.HandOffListResponse
import omnicurekotlin.example.com.providerEndpoints.model.PatientHandOffRequest

class HandOffPatientsActivity : BaseActivity() {

    private val TAG = HandOffPatientsActivity::class.java.simpleName
    var dialog: Dialog? = null
    private var handOffListResponse: HandOffListResponse? = null
    private var binding: ActivityHandOffPatientsBinding? = null
    private var viewModel: HandOffPatientViewModel? = null
    private var handOffPatientAdapter: HandOffPatientAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var selectedProviderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hand_off_patients)
        viewModel = ViewModelProvider(this).get(HandOffPatientViewModel::class.java)
        initViews()
    }

    fun initViews() {
        getHandOffList()
        binding?.dropdownLayout?.setOnClickListener { v ->
            handleMultipleClick(v)
            bottomDialog()
        }
        binding?.dropImg?.setOnClickListener { v ->
            handleMultipleClick(v)
            bottomDialog()
        }
        binding?.btnHandOff?.setOnClickListener { v ->
            handleMultipleClick(v)
            sendHandOffPatient()
        }
        binding?.imgBack?.setOnClickListener { finish() }


    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    private fun bottomDialog() {
        dialog = Dialog(this, R.style.Theme_Dialog)
        dialog?.setContentView(R.layout.dialog_bottom_list)
        val recyclerHandOff: RecyclerView? = dialog?.findViewById(R.id.recyclerHandOff)
        val imgCancel = dialog?.findViewById<ImageView>(R.id.imgCancel)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.attributes?.windowAnimations = R.style.SlideUpDialog
        layoutManager = LinearLayoutManager(this)
        layoutManager?.orientation = LinearLayoutManager.VERTICAL
        recyclerHandOff?.layoutManager = layoutManager
        recyclerHandOff?.setHasFixedSize(true)
        recyclerHandOff?.adapter = handOffPatientAdapter
        imgCancel?.setOnClickListener { dialog?.dismiss() }
        dialog?.show()
    }

    private fun getHandOffList() {
        val providerId: Long? = PrefUtility().getProviderId(this)
        providerId?.let { viewModel?.getHandOffPatientsLists(it) }?.observe(this, { it ->
            val response=it.body()
            dismissProgressBar()
            if (response?.getStatus() != null && response.getStatus()!!) {
                val gson = Gson()
                handOffListResponse = gson.fromJson(java.lang.String.valueOf(response), HandOffListResponse::class.java)
                Log.d(TAG, "Handoff provider List Response" + Gson().toJson(response))
                PopulateHandOffList()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    java.lang.String.valueOf(response?.getErrorMessage()), Constants.API.getHospital)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let {
                    CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING, it, CustomSnackBar.TOP,
                        3000, 0)
                }?.show()
            }
        })
    }

    private fun PopulateHandOffList() {
        if (handOffPatientAdapter == null) {
            handOffPatientAdapter = HandOffPatientAdapter(this, handOffListResponse)
            handOffPatientAdapter!!.setHandOffRecyclerListener(object : HandOffPatientAdapter.HandOffRecyclerListener{

                override fun onItemSelected(otherBspList: HandOffListResponse.OtherBspList?) {
                    val strName: String? = otherBspList?.getName()
                    selectedProviderId = otherBspList?.getId()
                    binding?.txtSelect?.text = strName
                    binding?.txtSelect?.setTextColor(resources.getColor(R.color.bg_blue))
                    binding?.btnHandOff?.background = resources.getDrawable(R.drawable.blue_color_btn_bg)
                    binding?.btnHandOff?.setTextColor(resources.getColor(R.color.white))
                    dialog?.dismiss()
                }
            })
        }
    }

    private fun sendHandOffPatient() {
        if (!isValid() || handOffListResponse == null) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        binding?.btnHandOff?.setEnabled(false)
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.submitHandOffAll.toString()))
        val patientHandOffRequest = PatientHandOffRequest()
        patientHandOffRequest.setBspProviderId(handOffListResponse?.getCurrentProvider()?.getId())
        patientHandOffRequest.setOtherBspProviderId(selectedProviderId)
        Log.d(TAG, "sendHandOffPatient Request: " + Gson().toJson(patientHandOffRequest))
        viewModel?.bedSideProviderHandOffPatient(patientHandOffRequest)?.observe(this, {
            val commonResponse=it.body()
                Log.d(TAG, "sendHandOffPatient response: " + Gson().toJson(commonResponse))
                dismissProgressBar()
                if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()!!) {
                    onHandOffSendSuccess(commonResponse)
                } else {
                    binding?.btnHandOff?.isEnabled = true
                    val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse?.getErrorMessage(),
                        Constants.API.register)
                    //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                    errMsg?.let {
                        CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                            it, CustomSnackBar.TOP, 3000, 0)
                    }?.show()
                }
            })
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {
//            UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
            errMsg?.let {
                CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                    it, CustomSnackBar.TOP, 3000, 0)
            }?.show()
            return false
        }
        return true
    }

    fun onHandOffSendSuccess(response: CommonResponse?) {
        CustomSnackBar.make(
            binding?.idContainerLayout, this, CustomSnackBar.SUCCESS, getString(R.string.Handoff_patient_successfully),
            CustomSnackBar.TOP, 3000, 1)?.show()

    }
}


