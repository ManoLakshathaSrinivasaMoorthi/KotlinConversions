package com.example.kotlinomnicure.activity
import android.content.Intent



import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.example.kotlinomnicure.viewmodel.AppointmentInfoViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityPatientAppointmentInfoBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.google.gson.Gson

import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import java.util.*

class PatientAppointmentActivityInfo : BaseActivity() {
    private val TAG = PatientAppointmentActivityInfo::class.java.simpleName
    private var binding: ActivityPatientAppointmentInfoBinding? = null
    private var viewModel: AppointmentInfoViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_appointment_info)
        viewModel = ViewModelProvider(this).get(AppointmentInfoViewModel::class.java)
        initView()
    }

    private fun initView() {
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding?.idBackButton?.setOnClickListener { view -> finish() }
        binding?.submitBtn?.setOnClickListener { view -> onClickSubmit() }
    }


    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {

            if (errMsg != null) {
                CustomSnackBar.make(
                    binding?.getRoot(),
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

    private fun createAppointmentObject(): Appointment {
        val extras = intent.extras
        val appointment = Appointment()
        appointment.setFname(extras!!.getString(Constants.IntentKeyConstants.FIRST_NAME))
        appointment.setLname(extras.getString(Constants.IntentKeyConstants.LAST_NAME))
        appointment.setEmail(extras.getString(Constants.IntentKeyConstants.EMAIL))
        appointment.setGender(extras.getString(Constants.IntentKeyConstants.GENDER))
        appointment.setDob(extras.getLong(Constants.IntentKeyConstants.DOB))
        appointment.setDob(getDOBLongFormat(extras.getString(Constants.IntentKeyConstants.DOB)))
        val dob =
            extras.getString(Constants.IntentKeyConstants.DOB).toString().split("/").toTypedArray()
        val month: String? = UtilityMethods().getMonthName(dob[0].toInt())
        val day = dob[1].toInt()
        val year = dob[2].toInt()
        appointment.setDobMonth(month)
        appointment.setDobDay(day)
        appointment.setDobYear(year)
        appointment.setPhone(
            extras.getString(Constants.IntentKeyConstants.MOBILE_NO)!!.trim { it <= ' ' }
                .replace("-", ""))
        appointment.setNote(binding?.messageTxt?.getText().toString().trim())
        appointment.setPassword(extras.getString(Constants.IntentKeyConstants.PASSWORD))
        val isRelative = extras.getBoolean(Constants.IntentKeyConstants.IS_RELATIVE)
        if (isRelative) {
            appointment.setFamilyMember(isRelative)
            appointment.setMemFirstName(extras.getString(Constants.IntentKeyConstants.REL_FNAME))
            appointment.setMemLastName(extras.getString(Constants.IntentKeyConstants.REL_LNAME))
            appointment.setRelationship(extras.getString(Constants.IntentKeyConstants.RELATION))
        }
        return appointment
    }

    private fun onClickSubmit() {
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)!!) {
//            UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.addAppointment.toString()))
        val appointment: Appointment = createAppointmentObject()
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        // Added setter
        appointment.setProviderId(providerID)
        appointment.setToken(token)
        Log.d("Patient request ", Gson().toJson(appointment))
        if (providerID != null) {
            if (token != null) {
                viewModel?.signUpPatient(providerID, token, appointment)
                    ?.observe(this) { commonResponse ->
                        dismissProgressBar()
                        Log.i("Patient response ", Gson().toJson(commonResponse))
                        if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()!!) {
                            onAppointmentSuccess(commonResponse)
                        } else {
                            val errMsg: String? = ErrorMessages().getErrorMessage(
                                this,
                                commonResponse?.getErrorMessage(),
                                Constants.API.patientSignUp
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
    }

    private fun onAppointmentSuccess(commonResponse: CommonResponse) {
        Log.d(TAG, "onAppointmentSuccess: ")
        val intent = Intent(this, PatientOTPActivity::class.java)
        intent.putExtra(
            Constants.IntentKeyConstants.MOBILE_NO,
            commonResponse.getPatient()?.getPhone()
        )
        intent.putExtra(
            Constants.IntentKeyConstants.PATIENT_ID,
            commonResponse.getPatient()?.getId()
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.IntentKeyConstants.PREVIOUS_ACTIVITY, TAG)
        startActivity(intent)
        finish()
    }

    private fun getDOBLongFormat(date: String?): Long {
        val d = date!!.split("/").toTypedArray()
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.clear()
        calendar[Calendar.DAY_OF_MONTH] = d[1].toInt()
        calendar[Calendar.MONTH] = d[0].toInt() - 1
        calendar[Calendar.YEAR] = d[2].toInt()
        val formatDate = calendar.time
        Log.i(TAG, "getDateInLongFormat: $formatDate")
        Log.i(TAG, "getDateInLongFormat in long: " + formatDate.time)
        return formatDate.time
    }
}