package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityCensusConsultChartBinding
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Patient
import omnicurekotlin.example.com.providerEndpoints.model.Members
import java.lang.Boolean
import java.text.SimpleDateFormat
import java.util.*

@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
class ActivityCensusConsultChart : AppCompatActivity() {

    private val TAG = ActivityCensusConsultChart::class.java.simpleName
    protected var binding:ActivityCensusConsultChartBinding? = null
    private var strPatientName: String? = null
    private var strPatientWardName: String? = null
    private val strPatientAge: String? = null
    private var strPatientDob: Long = 0
    private var patientId: Long = 0
    private var strPatientGender: String? = null
    private var strPatientPhone: String? = null
    private var strPatientScore: String? = null
    private var strPatientWard: String? = null
    private var strPatientUrgent: String? = null
    private var strPatientHospitalName: String? = null
    private var strPatientHospitalAddress: String? = null
    private var strPatientRecordNumber: String? = null
    private var strPatientNote: String? = null
    private var strPatientStatus: String? = null
    private var strPatientHeartRate: Double? = null
    private var strPatientSystolic: Double? = null
    private var strPatientDiastolic: Double? = null
    private var strPatientFio2: Double? = null
    private var strPatientSp02: Double? = null
    private var strPatientOxygenSupplement = false
    private var strPatientCondition: String? = null
    private var strPatientRespiratoryRate: Double? = null
    private var strPatientTemperature: Double? = null
    private var strPatientBdProviderId: String? = null
    private  var strPatientRdProviderId:String? = null
    private var patient: Patient? = null
    private val membersList: List<Members> = ArrayList()
    private var strProviderNameType: String? = null
    private  var strTime: String? = null
    private  var strStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_census_consult_chart)
        initViews()
        initOnClickListener()
    }

    private fun initViews() {
        setSupportActionBar(binding?.toolbar)

        val providerId: Long? = PrefUtility().getProviderId(this)
        patient = Patient()
        strProviderNameType = intent.getStringExtra("providerNameType")
        strTime = intent.getStringExtra("completedTime")
        strStatus = intent.getStringExtra("status")
        if (strProviderNameType != null && !TextUtils.isEmpty(strProviderNameType)) {
//            binding.txtProviderName.setText(strProviderNameType);
            val vals = strProviderNameType!!.split(",".toRegex()).toTypedArray()
            if (vals.size > 1 && vals[0].length > 12) {
                vals[0] = vals[0].substring(0, 12) + ".."
            }
            var providerName: String = TextUtils.join(",", vals)
            providerName = providerName.substring(0, 1).toUpperCase(Locale.ROOT) + providerName.substring(1)
            binding?.txtProviderName?.text = providerName
        }
        if (strTime != "null") {
            binding?.txtTime?.text = strTime?.toLong()?.let { Utils().timestampToDate(it) }
        }
        if (strStatus != null) {
            if (strStatus.equals("Discharged", ignoreCase = true)) {
                binding?.txtStatus?.text = "Completed"
            } else if (strStatus.equals("Exit", ignoreCase = true)) {
                binding?.txtStatus?.text = "Discharged"
            }
        }
        patientId = intent.getLongExtra("patientId", 0)
        strPatientBdProviderId = intent.getStringExtra("patientBdProviderId")
        strPatientRdProviderId = intent.getStringExtra("patientRdProviderId")
        strPatientName = intent.getStringExtra("patientName")
        strPatientWardName = intent.getStringExtra("patientWardName")
        //        strPatientAge = getIntent().getStringExtra("patientAge");
        strPatientDob = intent.getLongExtra("patientDob", 0)
        strPatientGender = intent.getStringExtra("patientGender")
        strPatientPhone = intent.getStringExtra("patientPhone")
        strPatientScore = intent.getStringExtra("patientScore")
        strPatientWard = intent.getStringExtra("patientWard")
        strPatientUrgent = intent.getStringExtra("patientUrgent")
        strPatientHospitalName = intent.getStringExtra("patientHospitalName")
        strPatientHospitalAddress = intent.getStringExtra("patientHospitalAddress")
        strPatientRecordNumber = intent.getStringExtra("patientRecordNumber")
        strPatientNote = intent.getStringExtra("patientNote")
        strPatientStatus = intent.getStringExtra("patientStatus")
        strPatientHeartRate = intent.getDoubleExtra("patientHeartRate", 0.0)
        strPatientSystolic = intent.getDoubleExtra("patientSystolic", 0.0)
        strPatientDiastolic = intent.getDoubleExtra("patientDiastolic", 0.0)
        strPatientRespiratoryRate = intent.getDoubleExtra("patientRespiratoryRate", 0.0)
        strPatientTemperature = intent.getDoubleExtra("patientTemperature", 0.0)
        strPatientFio2 = intent.getDoubleExtra("patientFio2", 0.0)
        strPatientSp02 = intent.getDoubleExtra("patientSp02", 0.0)
        strPatientOxygenSupplement = intent.getBooleanExtra("patientOxygenSupplement", false)
        strPatientCondition = intent.getStringExtra("patientCondition")
        patient?.setHeartRate(strPatientHeartRate)
        patient?.setArterialBloodPressureSystolic(strPatientSystolic)
        patient?.setArterialBloodPressureDiastolic(strPatientDiastolic)
        patient?.setRespiratoryRate(strPatientRespiratoryRate)
        patient?.setTemperature(strPatientTemperature)
        patient?.setFio2(strPatientFio2)
        patient?.setSpO2(strPatientSp02)
        patient?.setOxygenSupplement(strPatientOxygenSupplement)
        patient?.setPatientCondition(strPatientCondition)
        Log.d(TAG, "Patient Vital Values : " + Gson().toJson(patient))
       /* binding?.layoutStubView?.let { patient?.let { it1 ->
            UtilityMethods().displayCensusVitals(this, it,
                it1
            )
        } }*/
        strPatientScore?.let { Constants.AcuityLevel.valueOf(it) }?.let {
            UtilityMethods().displayPatientStatusComponent(ActivityCensusConsultChart(), binding?.statusStub, Boolean.valueOf(strPatientUrgent),
                strPatientStatus.equals(java.lang.String.valueOf(Constants.PatientStatus.Pending), ignoreCase = true),
                it
            )
        }
        setPatientValues()
        if (strPatientStatus.equals(java.lang.String.valueOf(Constants.PatientStatus.Discharged), ignoreCase = true) ||
                strPatientStatus.equals(java.lang.String.valueOf(Constants.PatientStatus.Exit), ignoreCase = true)) {
            binding?.llSuccessBar?.visibility = View.VISIBLE
        } else {
            binding?.llSuccessBar?.visibility = View.GONE
        }
    }

    private fun initOnClickListener() {
        binding?.imgBack?.setOnClickListener { finish() }
        binding!!.imgDetailUp.setOnClickListener { v ->
            handleMultipleClick(v)
            binding?.imgDetailUp?.visibility = View.GONE
            binding?.imgDetailDown?.visibility = View.VISIBLE
            binding?.llComplaints?.visibility = View.GONE
        }
        binding?.imgDetailDown?.setOnClickListener { v ->
            handleMultipleClick(v)
            binding?.imgDetailDown?.visibility = View.GONE
            binding?.imgDetailUp?.visibility = View.VISIBLE
            binding?.llComplaints?.visibility = View.VISIBLE
        }
        binding!!.imgVitalUp.setOnClickListener { v ->
            handleMultipleClick(v)
            binding?.imgVitalUp?.visibility = View.GONE
            binding?.imgVitalDown?.visibility = View.VISIBLE
            binding!!.llTimeZone.visibility = View.GONE
            binding!!.llVital.visibility = View.GONE
        }
        binding!!.imgVitalDown.setOnClickListener { v ->
            handleMultipleClick(v)
            binding!!.imgVitalDown.visibility = View.GONE
            binding!!.imgVitalUp.visibility = View.VISIBLE
            binding!!.llTimeZone.visibility = View.VISIBLE
            binding!!.llVital.visibility = View.VISIBLE
        }
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    @SuppressLint("SetTextI18n")
    fun setPatientValues() {
        val strAge: String
        var strGender: String?
        val strDob: String
        val strPhone: String
        val strWard: String
        var strWards = ""
        var strMRN = ""
        var strHosName: String? = ""
        var strHosAddress = ""
        val dot = " <b>\u00b7</b> "
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        calendar.timeInMillis = strPatientDob
        val age = year - calendar[Calendar.YEAR]
        val timeInMillis = strPatientDob
        val strDOB = SimpleDateFormat("MM-dd-yyyy").format(Date(timeInMillis))
        strAge = if (strPatientDob > 0) {
            age.toString()
        } else {
            ""
        }
        strDob = if (strPatientDob > 0) {
            dot + strDOB
        } else {
            ""
        }
        if (!TextUtils.isEmpty(strPatientGender)) {
            strGender = strPatientGender
            if (strGender.equals("Male", ignoreCase = true)) {
                strGender = dot + "M"
            } else if (strGender.equals("Female", ignoreCase = true)) {
                strGender = dot + "F"
            }
        } else {
            strGender = ""
        }
        strPhone = if (!TextUtils.isEmpty(strPatientPhone) &&
                !strPatientPhone.equals("null", ignoreCase = true)) {
            dot + strPatientPhone
        } else {
            ""
        }
        strWard = if (!TextUtils.isEmpty(strPatientWardName?.trim { it <= ' ' }) &&
                !strPatientWardName.equals("null", ignoreCase = true)) {
            dot + strPatientWardName
        } else {
            ""
        }
        strWards = if (!TextUtils.isEmpty(strPatientWardName?.trim { it <= ' ' }) &&
                !strPatientWardName.equals("null", ignoreCase = true)) {
            dot + strPatientWardName
        } else {
            ""
        }
        strMRN = if (!TextUtils.isEmpty(strPatientRecordNumber) &&
                !strPatientRecordNumber.equals("null", ignoreCase = true)) {
            dot + strPatientRecordNumber
        } else {
            ""
        }
        if (!TextUtils.isEmpty(strPatientNote)) {
            val strComplaint = strPatientNote
            val stringComplaint = strComplaint!!.substring(strComplaint.indexOf(":") + 1)
            binding?.txtComplaintDetail?.text = stringComplaint.trim { it <= ' ' }
        }
        strHosName = if (!TextUtils.isEmpty(strPatientHospitalName)) {
            strPatientHospitalName
        } else {
            ""
        }
        strHosAddress = if (!TextUtils.isEmpty(strPatientHospitalAddress)) {
            " , $strPatientHospitalAddress"
        } else {
            ""
        }
        if (!TextUtils.isEmpty(strPatientName)) {
            binding?.txtPatientName?.text = strPatientName
        }
        binding?.txtAge?.text = Html.fromHtml(strAge + strGender + strDob + strPhone)
        binding?.txtLocation?.text = Html.fromHtml(strPatientHospitalName + strWard)
        if (!TextUtils.isEmpty(strPatientRecordNumber)) {
            binding?.txtMRNNumber?.text = Html.fromHtml("MRN&nbsp;$strPatientRecordNumber")
        } else {
            binding?.txtMRNNumber?.text = "MRN "
        }
    }

}
