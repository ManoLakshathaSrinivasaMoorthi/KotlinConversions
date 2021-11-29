package com.example.dailytasksamplepoc.kotlinomnicure.activity

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
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityPatientDetailBinding
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.PatientDetailViewModel
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.patientsEndpoints.model.PatientDetail
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
        val heartRate: Double? = patientDetails?.getPatient()?.getHeartRate()
        val highBP: Double? = patientDetails?.getPatient()?.getArterialBloodPressureSystolic()
        val lowBP: Double? = patientDetails?.getPatient()?.getArterialBloodPressureDiastolic()
        val spo2: Double? = patientDetails?.getPatient()?.getSpO2()
        val respRate: Double ?= patientDetails?.getPatient()?.getRespiratoryRate()
        val fiO2: Double? = patientDetails?.getPatient()?.getFio2()
        val temp: Double? = patientDetails?.getPatient()?.getTemperature()
        val heartRateValue: String? = patientDetails?.getPatient()?.getHeartRateValue()
        val highBPValue: String? =
            patientDetails?.getPatient()?.getArterialBloodPressureSystolicValue()
        val lowBPValue: String? = patientDetails?.getPatient()?.getArterialBloodPressureDiastolicValue()
        val spo2Value: String?= patientDetails?.getPatient()?.getSpO2Value()
        val respRateValue: String? = patientDetails?.getPatient()?.getRespiratoryRateValue()
        val fiO2Value: String? = patientDetails?.getPatient()?.getFio2Value()
        val tempValue: String? = patientDetails?.getPatient()?.getTemperatureValue()
        if (patientDetails?.getPatient()?.getGender().equals("Male",ignoreCase = true)) {
            binding?.txtPatientDetailsNameAge?.setText(
                patientDetails?.getPatient()?.getFname()
                    .toString() + " " + patientDetails?.getPatient()
                    ?.getLname() + " . " + getAge() + " " + "M"
            )
        } else if (patientDetails?.getPatient()?.getGender().equals("Female",ignoreCase = true)) {
            binding?.txtPatientDetailsNameAge?.setText(
                patientDetails?.getPatient()?.getFname()
                    .toString() + " " + patientDetails?.getPatient()
                    ?.getLname() + " . " + getAge() + " " + "F"
            )
        }
        binding?.txtPatientDetailsAge?.setText(getAge())
        binding?.txtPatientDetailsDob?.setText(getDob())
        if (patientDetails?.getPatient()
                ?.getPhone() != null && !TextUtils.isEmpty(patientDetails!!.getPatient()?.getPhone())
        ) {
            binding?.txtPatientDetailsPhone?.setText(patientDetails!!.getPatient()?.getPhone())
            binding?.txtPatientDetailsPhone?.setOnClickListener(View.OnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + patientDetails!!.getPatient()?.getPhone())
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
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getRecordNumber())) {
            binding?.txtPatientDetailsRecordNo?.setText(patientDetails?.getPatient()?.getRecordNumber())
        } else {
            binding?.txtPatientDetailsRecordNo?.setText(" - ")
        }
        binding?.txtPatientDetailsSex?.setText(patientDetails?.getPatient()?.getGender())
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getHospital())) {
            binding?.txtPatientDetailsHospital?.setText(patientDetails?.getPatient()?.getHospital())
        } else {
            binding?.txtPatientDetailsHospital?.setText("-")
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getWardName())) {
            binding?.txtPatientDetailsWard?.setText(patientDetails?.getPatient()?.getWardName())
        } else {
            binding?.txtPatientDetailsWard?.setText("-")
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getCovidPositive())) {
            binding?.txtCovidPositive?.setText(patientDetails?.getPatient()?.getCovidPositive())
        } else {
            binding?.txtCovidPositive?.setText("-")
        }
        binding?.txtPatientDetailsPatientId?.setText(patientDetails?.getPatient()?.getId())
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
        binding?.idPatientDetailsAvpuValue?.setText(patientDetails?.getPatient()?.getPatientCondition())
        if (patientDetails?.getPatient()?.isOxygenSupplement() == true) {
            binding?.idPatientDetailsOxygenValue?.setText("Yes")
        } else {
            binding?.idPatientDetailsOxygenValue?.setText("No")
        }
        if (patientDetails?.getPatient()?.getSyncTime() != null && !patientDetails!!.getPatient()
                ?.getSyncTime().equals("0")
        ) {
            binding?.idMaticsUpdateTime?.setText(
                patientDetails!!.getPatient()?.getSyncTime()?.toLong()?.let {
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
        val acuityLevel: String? = patientDetails?.getPatient()?.getScore()
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
        val timeInMillis: Long = java.lang.Long.valueOf(patientDetails?.getPatient()?.getDob())
        return SimpleDateFormat("dd-MMM-yyyy").format(Date(timeInMillis))
    }

    private fun getAge(): String {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        calendar.timeInMillis = patientDetails?.getPatient()?.getDob()?.toLong()!!
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