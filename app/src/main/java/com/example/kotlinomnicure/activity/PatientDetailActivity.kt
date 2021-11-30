package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.example.dailytasksamplepoc.kotlinomnicure.activity.BaseActivity
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.PatientDetailViewModel
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.google.gson.Gson
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.patientsEndpoints.model.PatientDetail
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityPatientDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class PatientDetailActivity : BaseActivity() {
    private val TAG = PatientDetailActivity::class.java.simpleName
    private var binding: ActivityPatientDetailBinding? = null
    private var uid: Long = 0
    private var viewModel: PatientDetailViewModel? = null
    private var patientDetails: PatientDetail? = null
    private var phone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_detail)
        viewModel = ViewModelProvider(this).get(PatientDetailViewModel::class.java)
        uid = intent.getLongExtra("uid", 0)
        phone = intent.getStringExtra("phone")
        getPatientDetails(uid)
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        //        binding.toolbar.setTitle("Patient Details");
        binding?.toolbar?.setTitle(getString(R.string.patient_details))
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
    }

    private fun getPatientDetails(uid: Long) {
        showProgressBar(
            PBMessageHelper().getMessage(this, Constants.API.getPatientDetails.toString()))
        viewModel?.getPatienDetails(uid)?.observe(this) { response ->
            dismissProgressBar()
            Log.d(TAG, "Patient Details Res" + Gson().toJson(response))
            if (response != null && response.getStatus()) {
                patientDetails = response
                populatePatientDetails()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    java.lang.String.valueOf(response.getErrorId()),
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

    @SuppressLint("SetTextI18n")
    private fun populatePatientDetails() {
        setAcuityLevel()
        val heartRate: Double? = patientDetails?.patient?.getHeartRate()
        val highBP: Double? = patientDetails?.patient?.getArterialBloodPressureSystolic()
        val lowBP: Double? = patientDetails?.patient?.getArterialBloodPressureDiastolic()
        val spo2: Double? = patientDetails?.patient?.getSpO2()
        val respRate: Double ?= patientDetails?.patient?.getRespiratoryRate()
        val fiO2: Double? = patientDetails?.patient?.getFio2()
        val temp: Double? = patientDetails?.patient?.getTemperature()
        val heartRateValue: String? = patientDetails?.patient?.getHeartRateValue()
        val highBPValue: String? =
            patientDetails?.patient?.getArterialBloodPressureSystolicValue()
        val lowBPValue: String? = patientDetails?.patient?.getArterialBloodPressureDiastolicValue()
        val spo2Value: String?= patientDetails?.patient?.getSpO2Value()
        val respRateValue: String? = patientDetails?.patient?.getRespiratoryRateValue()
        val fiO2Value: String? = patientDetails?.patient?.getFio2Value()
        val tempValue: String? = patientDetails?.patient?.getTemperatureValue()
        if (patientDetails?.patient?.getGender().equals("Male",ignoreCase = true)) {
            binding?.txtPatientDetailsNameAge?.setText(
                patientDetails?.patient?.getFname()
                    .toString() + " " + patientDetails?.patient
                    ?.getLname() + " . " + getAge() + " " + "M"
            )
        } else if (patientDetails?.patient?.getGender().equals("Female",ignoreCase = true)) {
            binding?.txtPatientDetailsNameAge?.setText(
                patientDetails?.patient?.getFname()
                    .toString() + " " + patientDetails?.patient
                    ?.getLname() + " . " + getAge() + " " + "F"
            )
        }
        binding?.txtPatientDetailsAge?.setText(getAge())
        binding?.txtPatientDetailsDob?.setText(getDob())
        if (patientDetails?.patient
                ?.getPhone() != null && !TextUtils.isEmpty(patientDetails!!.patient?.getPhone())
        ) {
            binding?.txtPatientDetailsPhone?.setText(patientDetails!!.patient?.getPhone())
            binding?.txtPatientDetailsPhone?.setOnClickListener(View.OnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + patientDetails!!.patient?.getPhone())
                startActivity(intent)
            })

        } else {
            binding?.txtPatientDetailsPhone?.setText(" - ")
            binding?.txtPatientDetailsPhone?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
        }
        if (!TextUtils.isEmpty(patientDetails?.patient?.getRecordNumber())) {
            binding?.txtPatientDetailsRecordNo?.setText(patientDetails?.patient?.getRecordNumber())
        } else {
            binding?.txtPatientDetailsRecordNo?.setText(" - ")
        }
        binding?.txtPatientDetailsSex?.setText(patientDetails?.patient?.getGender())
        if (!TextUtils.isEmpty(patientDetails?.patient?.getHospital())) {
            binding?.txtPatientDetailsHospital?.setText(patientDetails?.patient?.getHospital())
        } else {
            binding?.txtPatientDetailsHospital?.setText("-")
        }
        if (!TextUtils.isEmpty(patientDetails?.patient?.getWardName())) {
            binding?.txtPatientDetailsWard?.setText(patientDetails?.patient?.getWardName())
        } else {
            binding?.txtPatientDetailsWard?.setText("-")
        }
        if (!TextUtils.isEmpty(patientDetails?.patient?.getCovidPositive())) {
            binding?.txtCovidPositive?.setText(patientDetails?.patient?.getCovidPositive())
        } else {
            binding?.txtCovidPositive?.setText("-")
        }
        binding?.txtPatientDetailsPatientId?.setText(patientDetails?.patient?.getId())
        if (!TextUtils.isEmpty(heartRateValue)) {
            binding?.idPatientDetailsHrValue?.setText(heartRate?.toInt().toString())
        } else {
            binding?.idPatientDetailsHrValue?.setText("-")
        }
        var hb = "-"
        if (!TextUtils.isEmpty(highBPValue)) {
            hb = highBP?.toInt().toString()
        }
        var lb = "-"
        if (!TextUtils.isEmpty(lowBPValue)) {
            lb = lowBP?.toInt().toString()
        }
        binding?.idPatientDetailsBpValue?.setText("$hb/$lb")

        if (!TextUtils.isEmpty(spo2Value)) {
            val color: Int? = spo2?.let { UtilityMethods().getSpo2TextColor(it.toInt()) }
            val spo2Value1 = spo2?.toInt().toString() + " %"
            val builder: SpannableStringBuilder? = TextSpanBuilder().getSubscriptString(
                spo2Value1,
                spo2Value1.length - 1,
                spo2Value1.length
            )
            binding?.getRoot()?.getContext()?.getResources()?.getColor(color!!)?.let {
                binding?.idPatientDetailsSpValue?.setTextColor(
                    it
                )
            }

            binding?.idPatientDetailsSpValue?.setText(builder)
        } else {
            binding?.idPatientDetailsSpValue?.setText("-")
        }
        if (!TextUtils.isEmpty(fiO2Value)) {
            binding?.idPatientDetailsFi02Value?.setText(fiO2?.toInt().toString() + " %")
        } else {
            binding?.idPatientDetailsFi02Value?.setText("-")
        }
        if (!TextUtils.isEmpty(respRateValue)) {
            binding?.idPatientDetailsRrValue?.setText(respRate?.toInt().toString())
        } else {
            binding?.idPatientDetailsRrValue?.setText("-")
        }
        if (!TextUtils.isEmpty(tempValue)) {
            binding?.idPatientDetailsTempValue?.setText("$temp°F")
        } else {
            binding?.idPatientDetailsTempValue?.setText("-")
        }
        binding?.idPatientDetailsAvpuValue?.setText(patientDetails?.patient?.getPatientCondition())
        if (patientDetails?.patient?.isOxygenSupplement() == true) {
            binding?.idPatientDetailsOxygenValue?.setText("Yes")
        } else {
            binding?.idPatientDetailsOxygenValue?.setText("No")
        }
        if (patientDetails?.patient?.getSyncTime() != null && !patientDetails!!.patient
                ?.getSyncTime().equals("0")
        ) {
            binding?.idMaticsUpdateTime?.setText(
                patientDetails!!.patient?.getSyncTime()?.toLong()?.let {
                    ChatUtils().getTimeAgo(
                        it
                    )
                }
            )
        } else {

            binding?.idMaticsUpdateTime?.setText(" - ")
        }
        Log.d("PATIENTDETAILS", Gson().toJson(patientDetails))
    }

    private fun setAcuityLevel() {
        val acuityLevel: String? = patientDetails?.patient?.getScore()
        if (acuityLevel != null) {
            if (acuityLevel.equals("Low", ignoreCase = true)) {
                binding?.llPatientDetailsAcuityLevel?.getResources()
                    ?.getColor(R.color.color_acuity_low)?.let {
                        binding?.llPatientDetailsAcuityLevel?.setBackgroundColor(
                            it
                        )
                    }
                binding?.acuityValue?.setText(resources.getString(R.string.acuity_low))
            } else if (acuityLevel.equals("Medium", ignoreCase = true)) {
                binding?.llPatientDetailsAcuityLevel?.setBackgroundColor(
                    binding?.llPatientDetailsAcuityLevel!!.getResources()
                        .getColor(R.color.color_acuity_medium)
                )
                binding!!.acuityValue.setText(resources.getString(R.string.acuity_medium))
            } else if (acuityLevel.equals("High", ignoreCase = true)) {
                binding?.llPatientDetailsAcuityLevel?.getResources()
                    ?.getColor(R.color.color_acuity_high)?.let {
                        binding?.llPatientDetailsAcuityLevel?.setBackgroundColor(
                            it
                        )
                    }
                binding?.acuityValue?.setText(resources.getString(R.string.acuity_high))
            } else {
                binding?.llPatientDetailsAcuityLevel?.setBackgroundColor(
                    binding!!.llPatientDetailsAcuityLevel.getResources()
                        .getColor(R.color.color_acuity_low)
                )
                binding?.acuityValue?.setText(resources.getString(R.string.acuity_low))
            }
        }
    }

    private fun getDob(): String? {
        val timeInMillis: Long = java.lang.Long.valueOf(patientDetails?.patient?.getDob())
        return SimpleDateFormat("dd-MMM-yyyy").format(Date(timeInMillis))
    }

    private fun getAge(): String {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        calendar.timeInMillis = patientDetails?.patient?.getDob()?.toLong()!!
        val agee = year - calendar[Calendar.YEAR]
        return agee.toString()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.PermissionCondes.PHONE_CALL_PERMISSION -> {
                val isGranted: Boolean = UtilityMethods().checkPermission(this, permissions)
                if (isGranted) {
                    val phone = intent.getStringExtra("phone")
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:" + phone!!.trim { it <= ' ' })
                    startActivity(intent)
                } else {
                    val permissonErr = getString(R.string.permission_denied)

                    CustomSnackBar.make(
                        binding?.getRoot(),
                        this,
                        CustomSnackBar.WARNING,
                        permissonErr,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
        }
    }
}