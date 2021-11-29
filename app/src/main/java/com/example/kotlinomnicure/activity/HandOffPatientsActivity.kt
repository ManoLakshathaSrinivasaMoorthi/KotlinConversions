package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.app.Dialog
import android.content.Context
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
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.HandOffPatientsBinding
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HandOffPatientViewModel
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson



import com.mvp.omnicure.kotlinactivity.adapter.HandOffPatientAdapter
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.mvp.omnicure.kotlinactivity.utils.ValidationUtil

import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.HandOffListResponse
import omnicurekotlin.example.com.providerEndpoints.model.PatientHandOffRequest


class HandOffPatientsActivity : BaseActivity(){

    //Declare the variables
    private val TAG = HandOffPatientsActivity::class.java.simpleName
    private var dialog: Dialog? = null
    private var handOffListResponse: HandOffListResponse? = null
    private var binding: HandOffPatientsBinding? = null
    private var viewModel: HandOffPatientViewModel? = null
    private var handOffPatientAdapter: HandOffPatientAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var selectedProviderId: String? = null
    private var ctx: Context?=HandOffPatientsActivity()

   //on create method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.hand_off_patients)
        viewModel = ViewModelProvider(this).get(HandOffPatientViewModel::class.java)
        initViews()
    }


    //Initialise the views

    fun initViews() {
        getHandOffList()
        binding?.dropdownLayout?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            bottomDialog()
        })
        binding?.dropImg?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            bottomDialog()
        })
        binding?.btnHandOff?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            sendHandOffPatient()
        })


       binding?.imgBack?.setOnClickListener(View.OnClickListener { finish() })


    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    fun bottomDialog() {
        dialog = Dialog(this, R.style.Theme_Dialog)
        dialog!!.setContentView(R.layout.dialog_bottom_list)
        val recyclerHandOff: RecyclerView = dialog!!.findViewById(R.id.recyclerHandOff)
        val imgCancel = dialog!!.findViewById<ImageView>(R.id.imgCancel)
        dialog!!.window!!.setGravity(Gravity.BOTTOM)
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        layoutManager = LinearLayoutManager(this)
        layoutManager!!.orientation = LinearLayoutManager.VERTICAL
        recyclerHandOff.layoutManager = layoutManager
        recyclerHandOff.setHasFixedSize(true)
        recyclerHandOff.adapter = handOffPatientAdapter
        imgCancel.setOnClickListener { dialog!!.dismiss() }
        dialog!!.show()
    }

    private fun getHandOffList() {
        val providerId = PrefUtility().getProviderId(this)
        if (providerId != null) {
            viewModel!!.getHandOffPatientsLists(providerId)?.observe(this
            ) { response: HandOffListResponse? ->
                dismissProgressBar()
                if (response != null && response.status) {
                    //                Gson gson = new Gson();
                    handOffListResponse = response
                    Log.d(TAG, "Handoff provider List Response" + Gson().toJson(response))
                    PopulateHandOffList()
                } else {
                    val errMsg = ErrorMessages().getErrorMessage(
                        this,
                        response!!.errorMessage.toString(),
                        Constants.API.getHospital
                    )

                    if (errMsg != null) {
                        CustomSnackBar.make(
                            binding?.idContainerLayout,
                            this,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        )?.show()
                    }
                }
            }
        }
    }

    private fun PopulateHandOffList() {
        if (handOffPatientAdapter == null) {
            handOffPatientAdapter = HandOffPatientAdapter(this, handOffListResponse)
            handOffPatientAdapter!!.setHandOffRecyclerListener(object : HandOffPatientAdapter.HandOffRecyclerListener {


                override fun onItemSelected(otherBspList: HandOffListResponse.OtherBspList?) {
                    val strName: String? = otherBspList?.name
                    if (otherBspList != null) {
                        selectedProviderId = otherBspList.id
                    }
                    binding!!.txtSelect.text = strName
                    binding!!.txtSelect.setTextColor(resources.getColor(R.color.bg_blue))
                    binding!!.btnHandOff.background =
                        resources.getDrawable(R.drawable.blue_color_btn_bg)
                    binding!!.btnHandOff.setTextColor(resources.getColor(R.color.white))
                    dialog!!.dismiss()
                }
            })
        }
    }

    private fun sendHandOffPatient() {
        if (!isValid() || handOffListResponse == null) {
            return
        }
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
        binding?.btnHandOff?.setEnabled(false)
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.submitHandOffAll.toString()))
        val patientHandOffRequest = PatientHandOffRequest()
        patientHandOffRequest.bspProviderId = handOffListResponse!!.currentProvider?.id
        patientHandOffRequest.otherBspProviderId = selectedProviderId
        Log.d(TAG, "sendHandOffPatient Request: " + Gson().toJson(patientHandOffRequest))
        viewModel!!.bedSideProviderHandOffPatient(patientHandOffRequest) ?.observe(this
        ) { commonResponse: CommonResponse? ->
            Log.d(TAG, "sendHandOffPatient response: " + Gson().toJson(commonResponse))
            dismissProgressBar()
            if (commonResponse != null && commonResponse.status != null && commonResponse.status!!) {
                onHandOffSendSuccess(commonResponse)
            } else {
                binding?.btnHandOff?.setEnabled(true)
                val errMsg = ErrorMessages().getErrorMessage(
                    this,
                    commonResponse!!.errorMessage,
                    Constants.API.register
                )

                if (errMsg != null) {
                    CustomSnackBar.make(
                        binding?.idContainerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
        }
    }

    private fun isValid(): Boolean {
        val errMsg = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {

            if (errMsg != null) {
                CustomSnackBar.make(
                    binding?.idContainerLayout,
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
        return true
    }

    fun onHandOffSendSuccess(response: CommonResponse?) {
        CustomSnackBar.make(
            binding?.idContainerLayout,
            this,
            CustomSnackBar.SUCCESS,
            getString(R.string.Handoff_patient_successfully),
            CustomSnackBar.TOP,
            3000,
            1
        )?.show()
    }
}