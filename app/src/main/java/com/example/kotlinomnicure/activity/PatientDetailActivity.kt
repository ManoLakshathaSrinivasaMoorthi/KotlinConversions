package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityPatientDetailBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.PatientDetailViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.patientsEndpoints.model.PatientDetail
import java.text.SimpleDateFormat
import java.util.*

class PatientDetailActivity :BaseActivity() {

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
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        binding?.toolbar?.title = "Patient Details"
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
    }

    private fun getPatientDetails(uid: Long) {
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.getPatientDetails.toString()))
        viewModel?.getPatienDetails(uid)?.observe(this) {
            val response=it.body()
            dismissProgressBar()
            Log.d(TAG, "Patient Details Res" + Gson().toJson(response))
            if (response != null && response.getStatus() != null && response.getStatus()!!) {
                val gson = Gson()
                patientDetails = gson.fromJson(java.lang.String.valueOf(response), PatientDetail::class.java)
                populatePatientDetails()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this, java.lang.String.valueOf(response?.getErrorId()),
                    Constants.API.getHospital)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let {
                    CustomSnackBar.make(binding?.idContainerLayout,
                        this, CustomSnackBar.WARNING, it,
                        CustomSnackBar.TOP, 3000, 0)?.show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populatePatientDetails() {
        setAcuityLevel()
        val heartRate = patientDetails?.getPatient()?.getHeartRate()
        val highBP = patientDetails?.getPatient()?.getArterialBloodPressureSystolic()
        val lowBP = patientDetails?.getPatient()?.getArterialBloodPressureDiastolic()
        val spo2 = patientDetails?.getPatient()?.getSpO2()
        val respRate = patientDetails?.getPatient()?.getRespiratoryRate()
        val fiO2 = patientDetails?.getPatient()?.getFio2()
        val temp = patientDetails?.getPatient()?.getTemperature()
        val heartRateValue = patientDetails?.getPatient()?.getHeartRateValue()
        val highBPValue = patientDetails?.getPatient()?.getArterialBloodPressureSystolicValue()
        val lowBPValue = patientDetails?.getPatient()?.getArterialBloodPressureDiastolicValue()
        val spo2Value = patientDetails?.getPatient()?.getSpO2Value()
        val respRateValue = patientDetails?.getPatient()?.getRespiratoryRateValue()
        val fiO2Value = patientDetails?.getPatient()?.getFio2Value()
        val tempValue = patientDetails?.getPatient()?.getTemperatureValue()
        if (patientDetails?.getPatient()?.getGender()?.contentEquals("Male") == true) {
            binding?.txtPatientDetailsNameAge?.text = patientDetails?.getPatient()?.getFname()
                .toString() + " " + patientDetails?.getPatient()?.getLname() + " . " + getAge() + " " + "M"
        } else if (patientDetails?.getPatient()?.getGender()?.contentEquals("Female") == true) {
            binding?.txtPatientDetailsNameAge?.text = patientDetails?.getPatient()?.getFname()
                .toString() + " " + patientDetails?.getPatient()?.getLname() + " . " + getAge() + " " + "F"
        }
        binding?.txtPatientDetailsAge?.text = getAge()
        binding?.txtPatientDetailsDob?.text = getDob()
        if (patientDetails?.getPatient()?.getPhone() != null && !TextUtils.isEmpty(patientDetails?.getPatient()?.getPhone())
        ) {
            binding?.txtPatientDetailsPhone?.text = patientDetails?.getPatient()?.getPhone()
            binding?.txtPatientDetailsPhone?.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + patientDetails?.getPatient()?.getPhone())
                startActivity(intent)
            }
            //            StringUtil.stripUnderlines((Spannable) binding.txtPatientDetailsPhone.getText());
        } else {
            binding?.txtPatientDetailsPhone?.text = " - "
            binding?.txtPatientDetailsPhone?.setTextColor(ContextCompat.getColor(applicationContext, R.color.black)
            )
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getRecordNumber())) {
            binding?.txtPatientDetailsRecordNo?.text = patientDetails?.getPatient()?.getRecordNumber()
        } else {
            binding?.txtPatientDetailsRecordNo?.text = " - "
        }
        binding?.txtPatientDetailsSex?.text = patientDetails?.getPatient()?.getGender()
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getHospital())) {
            binding?.txtPatientDetailsHospital?.text = patientDetails?.getPatient()?.getHospital()
        } else {
            binding?.txtPatientDetailsHospital?.text = "-"
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getWardName())) {
            binding?.txtPatientDetailsWard?.text = patientDetails?.getPatient()?.getWardName()
        } else {
            binding?.txtPatientDetailsWard?.text = "-"
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getCovidPositive())) {
            binding?.txtCovidPositive?.text = patientDetails?.getPatient()?.getCovidPositive()
        } else {
            binding?.txtCovidPositive?.text = "-"
        }
        binding?.txtPatientDetailsPatientId?.text = patientDetails?.getPatient()?.getId()
        if (!TextUtils.isEmpty(heartRateValue)) {
            binding?.idPatientDetailsHrValue?.text = heartRate?.toInt().toString()
        } else {
            binding?.idPatientDetailsHrValue?.text = "-"
        }
        var hb = "-"
        if (!TextUtils.isEmpty(highBPValue)) {
            hb = highBP?.toInt().toString()
        }
        var lb = "-"
        if (!TextUtils.isEmpty(lowBPValue)) {
            lb = lowBP?.toInt().toString()
        }
        binding!!.idPatientDetailsBpValue.text = "$hb/$lb"
        //        binding.idPatientDetailsSpValue.setText(String.valueOf(spo2.intValue() + " %"));
        if (!TextUtils.isEmpty(spo2Value)) {
            val color: Int? = spo2?.toInt()?.let { UtilityMethods().getSpo2TextColor(it) }
            val spo2Value1 = spo2?.toInt().toString() + " %"
            val builder: SpannableStringBuilder? = TextSpanBuilder().getSubscriptString(spo2Value1,
                spo2Value1.length - 1, spo2Value1.length)
            color?.let {
                binding?.root?.context?.resources?.getColor(it)?.let {
                    binding?.idPatientDetailsSpValue?.setTextColor(
                        it)
                }
            }
            //itemBinding.idSpo2Dot.setColorFilter(itemBinding.idSpo2Dot.getContext().getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            binding?.idPatientDetailsSpValue?.text = builder
        } else {
            binding?.idPatientDetailsSpValue?.text = "-"
        }
        if (!TextUtils.isEmpty(fiO2Value)) {
            binding?.idPatientDetailsFi02Value?.text = fiO2?.toInt().toString() + " %"
        } else {
            binding?.idPatientDetailsFi02Value?.text = "-"
        }
        if (!TextUtils.isEmpty(respRateValue)) {
            binding?.idPatientDetailsRrValue?.text = respRate?.toInt().toString()
        } else {
            binding?.idPatientDetailsRrValue?.text = "-"
        }
        if (!TextUtils.isEmpty(tempValue)) {
            binding?.idPatientDetailsTempValue?.text = "$temp°F"
        } else {
            binding?.idPatientDetailsTempValue?.text = "-"
        }
        binding?.idPatientDetailsAvpuValue?.text =
            patientDetails?.getPatient()?.getPatientCondition()
        if (patientDetails?.getPatient()?.isOxygenSupplement() == true) {
            binding?.idPatientDetailsOxygenValue?.text = "Yes"
        } else {
            binding?.idPatientDetailsOxygenValue?.text = "No"
        }
        if (patientDetails?.getPatient()?.getSyncTime() != null &&
            !patientDetails?.getPatient()?.getSyncTime().equals("0")
        ) {
            binding?.idMaticsUpdateTime?.setText(
                patientDetails?.getPatient()?.getSyncTime()?.toLong()?.let {
                    ChatUtils().getTimeAgo(
                        it)
                }
            )
        } else {
//            patientDetails.getPatient().setJoiningTime("");
            binding?.idMaticsUpdateTime?.text = " - "
        }
        Log.d("PATIENTDETAILS", Gson().toJson(patientDetails))
    }

    private fun setAcuityLevel() {
        val acuityLevel = patientDetails?.getPatient()?.getScore()
        if (acuityLevel != null) {
            if (acuityLevel.equals("Low", ignoreCase = true)) {
                binding?.llPatientDetailsAcuityLevel?.resources?.getColor(R.color.color_acuity_low)?.let {
                    binding?.llPatientDetailsAcuityLevel?.setBackgroundColor(
                        it)
                }
                binding?.acuityValue?.text = resources.getString(R.string.acuity_low)
            } else if (acuityLevel.equals("Medium", ignoreCase = true)) {
                binding?.llPatientDetailsAcuityLevel?.resources?.getColor(R.color.color_acuity_medium)?.let {
                    binding?.llPatientDetailsAcuityLevel?.setBackgroundColor(it)
                }
                binding?.acuityValue?.text = resources.getString(R.string.acuity_medium)
            } else if (acuityLevel.equals("High", ignoreCase = true)) {
                binding?.llPatientDetailsAcuityLevel?.resources?.getColor(R.color.color_acuity_high)?.let {
                    binding?.llPatientDetailsAcuityLevel?.setBackgroundColor(it)
                }
                binding?.acuityValue?.text = resources.getString(R.string.acuity_high)
            } else {
                binding?.llPatientDetailsAcuityLevel?.resources?.getColor(R.color.color_acuity_low)?.let {
                    binding?.llPatientDetailsAcuityLevel?.setBackgroundColor(it)
                }
                binding?.acuityValue?.text = resources.getString(R.string.acuity_low)
            }
        }
    }

    private fun getDob(): String? {
        val timeInMillis = java.lang.Long.valueOf(patientDetails?.getPatient()?.getDob())
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
                    //                    UtilityMethods.showErrorSnackBar(binding.getRoot(), permissonErr, Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(binding?.root, this, CustomSnackBar.WARNING,
                        permissonErr, CustomSnackBar.TOP, 3000, 0)?.show()
                }
            }
        }
    }
}
