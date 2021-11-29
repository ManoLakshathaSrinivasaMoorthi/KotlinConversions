package com.example.dailytasksamplepoc.kotlinomnicure.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar

import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityCensusEconsultChartBinding
import com.example.dailytasksamplepoc.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility


import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import omnicurekotlin.example.com.hospitalEndpoints.model.Patient
import omnicurekotlin.example.com.providerEndpoints.model.Members

import java.lang.Boolean
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ActivityCensusConsultChart : AppCompatActivity() {
    //Declare the variables
    private val TAG = ActivityCensusConsultChart::class.java.simpleName
    protected var binding: ActivityCensusEconsultChartBinding? = null
    private var statusStub: RelativeLayout? = null
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
    private  var strPatientRdProviderId:kotlin.String? = null
    private var stub: ViewStub? = null
    private var patient: Patient? = null
    private val membersList: List<Members> = ArrayList()
    private var strProviderNameType: String? = null
     private  var strTime:kotlin.String? = null
    private  var strStatus:kotlin.String? = null


   //On create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_census_econsult_chart)
        initViews()
        initOnClickListener()
    }


    //set on click listeners
    private fun initViews() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        stub = findViewById<View>(R.id.layout_stub_view) as ViewStub
        statusStub = findViewById<View>(R.id.status_stub) as RelativeLayout
        val providerId = PrefUtility().getProviderId(this)

        patient = Patient()

        strProviderNameType = intent.getStringExtra("providerNameType")
        strTime = intent.getStringExtra("completedTime")
        strStatus = intent.getStringExtra("status")

        if (strProviderNameType != null && !TextUtils.isEmpty(strProviderNameType)) {

            val vals = strProviderNameType!!.split(",").toTypedArray()
            var providerName = ""
            if (vals.size > 1 && vals[0].length > 12) {
                vals[0] = vals[0].substring(0, 12) + ".."
            }
            providerName = TextUtils.join(",", vals)
            providerName = providerName.substring(0, 1).toUpperCase() + providerName.substring(1)
            binding!!.txtProviderName.text = providerName
        }
        if (strTime != "null") {
            binding!!.txtTime.text = Utils().timestampToDate(strTime!!.toLong())
        }
        if (strStatus != null) {
            if (strStatus.equals("Completed", ignoreCase = true)) {

                binding!!.txtStatus.text =
                    applicationContext.resources.getString(R.string.completed)
            } else if (strStatus.equals("Discharged", ignoreCase = true)) {

                binding!!.txtStatus.text =
                    applicationContext.resources.getString(R.string.discharged)
            }
        }

        //initialise the variables
        patientId = intent.getLongExtra("patientId", 0)
        strPatientBdProviderId = intent.getStringExtra("patientBdProviderId")
        strPatientRdProviderId = intent.getStringExtra("patientRdProviderId")
        strPatientName = intent.getStringExtra("patientName")
        strPatientWardName = intent.getStringExtra("patientWardName")
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

        patient!!.setHeartRate(strPatientHeartRate)
        patient!!.setArterialBloodPressureSystolic(strPatientSystolic)
        patient!!.setArterialBloodPressureDiastolic(strPatientDiastolic)
        patient!!.setRespiratoryRate(strPatientRespiratoryRate)
        patient!!.setTemperature(strPatientTemperature)
        patient!!.setFio2(strPatientFio2)
        patient!!.setSpO2(strPatientSp02)
        patient!!.setOxygenSupplement(strPatientOxygenSupplement)
        patient!!.setPatientCondition(strPatientCondition)

        Log.d(TAG, "Patient Vital Values : " + Gson().toJson(patient))
        UtilityMethods().displayCensusVitals(this, stub!!, patient)
        UtilityMethods().displayPatientStatusComponent(
            this, statusStub!!, Boolean.valueOf(strPatientUrgent),
            strPatientStatus.equals(Constants.PatientStatus.Pending.toString(), ignoreCase = true),
            Constants.AcuityLevel.valueOf(strPatientScore!!)
        )

        setPatientValues()

        if (strPatientStatus.equals(Constants.PatientStatus.Completed.toString(), ignoreCase = true) || strPatientStatus.equals(
                Constants.PatientStatus.Discharged.toString(),
                ignoreCase = true)) {
            binding!!.llSuccessBar.visibility = View.VISIBLE
        } else {
            binding!!.llSuccessBar.visibility = View.GONE
        }
    }

    private fun initOnClickListener() {
        binding!!.imgBack.setOnClickListener { finish() }

        binding!!.imgDetailUp.setOnClickListener { v ->
            handleMultipleClick(v)
            binding!!.imgDetailUp.visibility = View.GONE
            binding!!.imgDetailDown.visibility = View.VISIBLE
            binding!!.llComplaints.visibility = View.GONE
        }

        binding!!.imgDetailDown.setOnClickListener { v ->
            handleMultipleClick(v)
            binding!!.imgDetailDown.visibility = View.GONE
            binding!!.imgDetailUp.visibility = View.VISIBLE
            binding!!.llComplaints.visibility = View.VISIBLE
        }

        binding!!.imgVitalUp.setOnClickListener { v ->
            handleMultipleClick(v)
            binding!!.imgVitalUp.visibility = View.GONE
            binding!!.imgVitalDown.visibility = View.VISIBLE
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

    private fun handleMultipleClick(v: View?) {
        if (v != null) {
            v.isEnabled = false
        }
        Handler().postDelayed({
            v?.isEnabled = true
        }, 500)
    }

    private fun setPatientValues() {

        var strAge = ""
        var strGender = ""
        var strDob = ""
        var strPhone = ""
        var strWard = ""
        var strWards = ""
        var strMRN = ""
        val strHosName = ""
        val strHosAddress = ""

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
            strGender = strPatientGender!!
            if (strGender.equals("Male", ignoreCase = true)) {
                strGender = dot + "M"
            } else if (strGender.equals("Female", ignoreCase = true)) {
                strGender = dot + "F"
            }
        } else {
            strGender = ""
        }

        strPhone = if (!TextUtils.isEmpty(strPatientPhone) &&
            !strPatientPhone.equals("null", ignoreCase = true)
        ) {
            dot + strPatientPhone
        } else {
            ""
        }

        strWard = if (!TextUtils.isEmpty(strPatientWardName!!.trim { it <= ' ' }) &&
            !strPatientWardName.equals("null", ignoreCase = true)
        ) {
            dot + strPatientWardName
        } else {
            ""
        }

        strWards = if (!TextUtils.isEmpty(strPatientWardName!!.trim { it <= ' ' }) &&
            !strPatientWardName.equals("null", ignoreCase = true)
        ) {
            dot + strPatientWardName
        } else {
            ""
        }

        strMRN = if (!TextUtils.isEmpty(strPatientRecordNumber) &&
            !strPatientRecordNumber.equals("null", ignoreCase = true)
        ) {
            dot + strPatientRecordNumber
        } else {
            ""
        }

        if (!TextUtils.isEmpty(strPatientNote)) {
            val strComplaint = strPatientNote!!
            var stringComplaint = strComplaint
            if (strComplaint.contains(":")) {
                stringComplaint = strComplaint.substring(strComplaint.indexOf(":") + 1)
            }
            binding!!.txtComplaintDetail.text = stringComplaint.trim { it <= ' ' }
        }

        if (!TextUtils.isEmpty(strPatientName)) {
            binding!!.txtPatientName.text = strPatientName
        }

        binding!!.txtAge.text =
            HtmlCompat.fromHtml(
                strAge + strGender + strDob + strPhone,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        binding!!.txtLocation.text =
            HtmlCompat.fromHtml(strPatientHospitalName + strWard, HtmlCompat.FROM_HTML_MODE_LEGACY)

        if (!TextUtils.isEmpty(strPatientRecordNumber)) {
            binding!!.txtMRNNumber.text =
                HtmlCompat.fromHtml(
                    "MRN&nbsp;$strPatientRecordNumber",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
        } else {
            binding!!.txtMRNNumber.text = "MRN "
        }
    }

    }


