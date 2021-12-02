package com.mvp.omnicure.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.*
import com.example.kotlinomnicure.databinding.ActivityEconsultChartRemoteBinding
import com.example.kotlinomnicure.helper.NotificationHelper
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.PatientDetailViewModel
import omnicurekotlin.example.com.providerEndpoints.model.Members

import com.google.firebase.database.*
import omnicurekotlin.example.com.patientsEndpoints.model.DischargePatientRequest
import omnicurekotlin.example.com.patientsEndpoints.model.PatientDetail

import java.lang.Exception
import java.text.SimpleDateFormat


import java.util.ArrayList

import java.util.Calendar

import java.util.Date


class ActivityConsultChartRemote : BaseActivity() {
    protected var binding: ActivityEconsultChartRemoteBinding? = null
    var membersList: MutableList<Members> = ArrayList()
    var errorTeams: String? = null
    var mUnreadMessageListener: ValueEventListener = object : ValueEventListener {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val consultProviderA: ConsultProvider? =
                dataSnapshot.getValue(ConsultProvider::class.java)!!

            if (consultProviderA != null) {
                if (consultProviderA.getUnread() > 0) {

                    binding?.imgMessageAlert?.setVisibility(View.VISIBLE)
                } else {
                    binding?.imgMessageAlert?.setVisibility(View.GONE)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }
    var statusStub: RelativeLayout? = null
    private var uid: Long = 0
    private var viewModel: PatientDetailViewModel? = null
    private var patientDetails: PatientDetail? = null
    private var strPhone: String? = null
    private var strDob: String? = null
    private var strGender: String? = null
    private var strWard: String? = null
    private var mConsultProvider: ConsultProvider? = null
    private var mConsultProviderKey: String? = null
    private var strProviderNameType: String? = null
    private var strTime: String? = null
    private var strStatus: String? = null
    private var stub: ViewStub? = null
    private var strMessageReaded: String? = null
    private var mUnreadMessageDB: DatabaseReference? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_econsult_chart_remote)
        viewModel = ViewModelProvider(this).get(PatientDetailViewModel::class.java)
        initViews()
        initOnClickListener()
    }

    private fun initViews() {
        stub = findViewById(R.id.layout_stub_view) as ViewStub?
        statusStub = findViewById(R.id.status_stub) as RelativeLayout?
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference


        uid = getIntent().getLongExtra("uid", 0)
      binding?.llCallLayout?.setVisibility(View.GONE)
        setProviderObject()
        val role: String? = PrefUtility().getRole(this)


        getPatientDetails(uid)
    }

    private fun setProviderObject() {
        mConsultProvider = ConsultProvider()
        strProviderNameType = getIntent().getStringExtra("providerNameType")
        strTime = getIntent().getStringExtra("completedTime")
        strStatus = getIntent().getStringExtra("status")

        mConsultProviderKey = getIntent().getStringExtra("consultProviderId")
        mConsultProvider!!.setId(getIntent().getStringExtra("consultProviderPatientId")?.toLong())
        mConsultProvider!!.setPatientsId(getIntent().getStringExtra("consultProviderPatientId")?.toLong())
        mConsultProvider!!.setText(getIntent().getStringExtra("consultProviderText"))
        mConsultProvider!!.setName(getIntent().getStringExtra("consultProviderName"))
        mConsultProvider!!.setUnread(getIntent().getIntExtra("unreadMessageCount", 0))
        val dob: Long = getIntent().getLongExtra("dob", -1)
        mConsultProvider!!.setDob(dob)
        val gender: String? = getIntent().getStringExtra("gender")
        mConsultProvider!!.setGender(gender)
        val note: String? = getIntent().getStringExtra("note")
        mConsultProvider!!.setNote(note)
        val status: String? = getIntent().getStringExtra("status")
        if (!TextUtils.isEmpty(status)) {
            mConsultProvider!!.setStatus(status?.let { Constants.PatientStatus.valueOf(it) })
        }
        mConsultProvider!!.setUrgent(getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT,
            false))
        mConsultProvider!!.setTeamName(getIntent().getStringExtra("teamNameConsult"))
        if (getIntent().hasExtra("phone")) {
            mConsultProvider!!.setPhone(getIntent().getStringExtra("phone"))
        }
        if (mConsultProvider!!.getUnread() > 0) {
            binding?.imgMessageAlert?.setVisibility(View.VISIBLE)
        } else {
            binding?.imgMessageAlert?.setVisibility(View.GONE)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initOnClickListener() {
        binding?.imgBack?.setOnClickListener(View.OnClickListener { finish() })
        binding?.imgDetailUp?.setOnClickListener(View.OnClickListener {
            binding?.imgDetailUp?.setVisibility(View.GONE)
            binding?.imgDetailDown?.setVisibility(View.VISIBLE)
            binding?.llComplaints?.setVisibility(View.GONE)
        })
        binding?.imgDetailDown?.setOnClickListener(View.OnClickListener {
            binding?.imgDetailDown?.setVisibility(View.GONE)
            binding?.imgDetailUp?.setVisibility(View.VISIBLE)
            binding?.llComplaints?.setVisibility(View.VISIBLE)
        })
        binding?.imgVitalUp?.setOnClickListener(View.OnClickListener {
            binding?.imgVitalUp?.setVisibility(View.GONE)
            binding?.imgVitalDown?.setVisibility(View.VISIBLE)
            binding?.llTimeZone?.setVisibility(View.GONE)
            binding?.llVital?.setVisibility(View.GONE)
        })
        binding?.imgVitalDown?.setOnClickListener(View.OnClickListener {
            binding?.imgVitalDown?.setVisibility(View.GONE)
            binding?.imgVitalUp?.setVisibility(View.VISIBLE)
            binding?.llTimeZone?.setVisibility(View.VISIBLE)
            binding?.llVital?.setVisibility(View.VISIBLE)
        })
        binding?.llMessage?.setOnClickListener(View.OnClickListener {
            val intentConsultChart = Intent(this,
                ChatActivity::class.java)
            intentConsultChart.putExtra("uid", mConsultProvider?.getId())
            intentConsultChart.putExtra("path", "consults/" + mConsultProvider?.getId())
            intentConsultChart.putExtra("consultProviderId", "" + mConsultProviderKey)
            intentConsultChart.putExtra("consultProviderPatientId", "" + mConsultProvider?.getId())
            intentConsultChart.putExtra("consultProviderText", mConsultProvider?.getText())
            intentConsultChart.putExtra("consultProviderName", mConsultProvider?.getName())
            intentConsultChart.putExtra("dob", mConsultProvider?.getDob())
            intentConsultChart.putExtra("gender", mConsultProvider?.getGender())
            intentConsultChart.putExtra("note", mConsultProvider?.getNote())
            intentConsultChart.putExtra("phone", mConsultProvider?.getPhone())
            intentConsultChart.putExtra("patientId", mConsultProvider?.getPatientsId())
            intentConsultChart.putExtra("ConsultChartRemote", "chartRemote")
            intentConsultChart.putExtra("status", mConsultProvider?.getStatus())
            intentConsultChart.putExtra("teamNameConsult", mConsultProvider?.getTeamName())


            intentConsultChart.putExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                mConsultProvider?.getUrgent())
            if (mConsultProvider?.getStatus() != null) {
                intentConsultChart.putExtra("status", mConsultProvider?.getStatus().toString())
                if (mConsultProvider?.getStatus() === Constants.PatientStatus.Invited ||
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Handoff
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    mConsultProvider?.id?.let { it1 -> clearNotifications(it1.toInt()) }
                } else if (mConsultProvider?.getStatus() === Constants.PatientStatus.Completed ||
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Discharged
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }
            startActivityForResult(intentConsultChart, 1)
        })
        binding?.txtContactTeam?.setOnClickListener(View.OnClickListener {
            if (membersList!!.size > 0 && membersList != null) {
                mConsultProvider?.let { it1 ->
                    showTeamMembersDialog(this,
                        membersList,
                        it1)
                }
            } else {

                CustomSnackBar.make(binding?.containerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errorTeams,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            }
        })
        if (strProviderNameType != null && !TextUtils.isEmpty(strProviderNameType)) {

            val vals = strProviderNameType!!.split(",").toTypedArray()
            var providerName = ""
            if (vals.size > 1 && vals[0].length > 12) {
                vals[0] = vals[0].substring(0, 12) + ".."
            }
            providerName = TextUtils.join(",", vals)
            providerName = providerName.substring(0, 1).toUpperCase() + providerName.substring(1)
            binding?.txtProviderName?.setText(providerName)
        }
        if (strTime != "null") {
            binding?.txtTime?.setText(Utils().timestampToDate(strTime!!.toLong()))
        }
        if (strStatus != null) {
            if (strStatus.equals("Completed", ignoreCase = true)) {

                binding?.txtStatus?.setText(getApplicationContext().getResources()
                    .getString(R.string.completed))
            } else if (strStatus.equals("Discharged", ignoreCase = true)) {

                binding?.txtStatus?.setText(applicationContext.getResources()
                    .getString(R.string.discharged))
            }
        }
        binding?.floatFab?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            val role: String? = PrefUtility().getRole(this)
            if (role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
                MoreOptionBPDialog(this)
            } else {
                MoreOptionRPDialog(this)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mUnreadMessageDB!!.removeEventListener(mUnreadMessageListener)
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    strMessageReaded = data.getStringExtra("messageValue")
                }
                if (!TextUtils.isEmpty(strMessageReaded)) {
                    binding?.imgMessageAlert?.setVisibility(View.GONE)
                } else {
                    binding?.imgMessageAlert?.setVisibility(View.VISIBLE)
                }
            }
        }
    }
  /*  private fun getTeamMemberDetails(patientId: Long, teamName: String) {
        membersList.clear()

        val patID = mConsultProvider!!.getPatientsId()
        viewModel!!.getMemberList(patID!!, teamName)!!.observe(this, { response ->
            dismissProgressBar()
            if (response?.getStatus() != null && response.getStatus()!!) {
                if (response.getTeamDetails()!!.getMembers() != null) {
                    response.teamDetails?.let { membersList.addAll(it.getMembers()) }
                }
            } else {
                errorTeams = response!!.getErrorMessage()
            }
        })
    }*/

    private fun clearNotifications(notificationId: Int) {
       // NotificationHelper(this).clearNotification(notificationId)
    }

    override fun onStart() {
        super.onStart()
        try {
            unReadMessageListener()
        } catch (e: Exception) {
//            Log.i(TAG, "onStart: EXCEPTION " + e.getMessage());
        }
    }

    private fun unReadMessageListener() {
//        Log.d(TAG, "unReadMessageListener: ");
        val mProviderUid: String? = PrefUtility().getFireBaseUid(this) //mFirebaseUser.getUid();
        mUnreadMessageDB =
            mProviderUid?.let {
                mFirebaseDatabaseReference!!.child("providers").child(it).child("active")
                    .child(
                        mConsultProviderKey!!)
            }
        mUnreadMessageDB!!.addValueEventListener(mUnreadMessageListener)
    }

    private fun getPatientDetails(uid: Long) {
        showProgressBar(PBMessageHelper().getMessage(this,
            Constants.API.getPatientDetails.toString()))
        viewModel?.getPatienDetails(uid)?.observe(this) { response ->

            val patID: Long? = mConsultProvider?.getPatientsId()


            if (response != null && response.getStatus()) {
                patientDetails = response
                populateConsultDetails()
                if (patID != null) {
                    getTeamMemberDetails(patID, "Team " + patientDetails!!.patient?.getTeamName())
                }
            } else {
                dismissProgressBar()
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    java.lang.String.valueOf(response?.getErrorId()),
                    Constants.API.getHospital)

                CustomSnackBar.make(binding?.containerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateConsultDetails() {
        val heartRateValue: String? = patientDetails?.patient?.getHeartRateValue()
        val highBPValue: String? =
            patientDetails?.patient?.getArterialBloodPressureSystolicValue()
        val lowBPValue: String? = patientDetails?.patient?.getArterialBloodPressureDiastolicValue()
        val spo2Value: String? = patientDetails?.patient?.getSpO2Value()
        val respRateValue: String? = patientDetails?.patient?.getRespiratoryRateValue()
        val fiO2Value: String? = patientDetails?.patient?.getFio2Value()
        val tempValue: String? = patientDetails?.patient?.getTemperatureValue()
        val provider = ConsultProvider()
        if (!TextUtils.isEmpty(heartRateValue)) {
            provider.setHeartRate(patientDetails?.patient?.getHeartRate())
        }
        if (!TextUtils.isEmpty(highBPValue)) {
            provider.setArterialBloodPressureSystolic(patientDetails?.patient
                ?.getArterialBloodPressureSystolic())
        }
        if (!TextUtils.isEmpty(lowBPValue)) {
            provider.setArterialBloodPressureDiastolic(patientDetails?.patient
                ?.getArterialBloodPressureDiastolic())
        }
        if (!TextUtils.isEmpty(spo2Value)) {
            provider.setSpO2(patientDetails?.patient?.getSpO2())
        }
        if (!TextUtils.isEmpty(fiO2Value)) {
            patientDetails?.patient?.getFio2()?.let { provider.setFiO2(it) }
        }
        if (!TextUtils.isEmpty(respRateValue)) {
            provider.setRespiratoryRate(patientDetails?.patient?.getRespiratoryRate())
        }
        if (!TextUtils.isEmpty(tempValue)) {
            provider.setTemperature(patientDetails?.patient?.getTemperature())
        }
        provider.setOxygenSupplement(patientDetails?.patient?.isOxygenSupplement())
        if (!TextUtils.isEmpty(patientDetails?.patient?.getPatientCondition())) {
            if (patientDetails?.patient?.getPatientCondition()
                    .equals(java.lang.String.valueOf(Constants.PatientCondition.Alert),ignoreCase = true)
            ) {
                provider.setPatientCondition(Constants.PatientCondition.Alert)
            } else if (patientDetails?.patient?.getPatientCondition()
                    .equals(java.lang.String.valueOf(Constants.PatientCondition.Voice),ignoreCase = true)
            ) {
                provider.setPatientCondition(Constants.PatientCondition.Voice)
            } else if (patientDetails?.patient?.getPatientCondition()
                    .equals(java.lang.String.valueOf(Constants.PatientCondition.Pain),ignoreCase = true)
            ) {
                provider.setPatientCondition(Constants.PatientCondition.Pain)
            } else if (patientDetails?.patient?.getPatientCondition()
                    .equals(java.lang.String.valueOf(Constants.PatientCondition.Unresponsive),ignoreCase = true)
            ) {
                provider.setPatientCondition(Constants.PatientCondition.Unresponsive)
            }
        }
        stub?.let { UtilityMethods().displayVitals(this, it, provider) }
        statusStub?.let {
            UtilityMethods().displayPatientStatusComponent(this,
                it,
                mConsultProvider?.getUrgent(),
                mConsultProvider?.getStatus() === Constants.PatientStatus.Pending,
                patientDetails?.patient?.getScore()?.let { it1 -> Constants.AcuityLevel.valueOf(it1) })
        }
        if (mConsultProvider?.getStatus() === Constants.PatientStatus.Pending) {
            binding?.txtContactTeam?.setVisibility(View.GONE)
        }
        if (!TextUtils.isEmpty(patientDetails?.patient?.getRecordNumber())) {
            binding?.txtMRNNumber?.setText(Html.fromHtml("MRN&nbsp;" + (patientDetails?.patient
                ?.getRecordNumber() )))
        } else {
            binding?.txtMRNNumber?.setText("MRN ")
        }
        binding?.txtPatientName?.setText(patientDetails?.patient?.getFname()
            .toString() + " " + (patientDetails?.patient?.getLname() ))
        val dot = " <b>\u00b7</b> "
        if (!TextUtils.isEmpty(patientDetails?.patient?.getDob())) {
            strDob = dot + dob
        }
        if (!TextUtils.isEmpty(patientDetails?.patient?.getGender())) {
            strGender = patientDetails?.patient?.getGender()
            if (strGender.equals("Male", ignoreCase = true)) {
                strGender = dot + "M"
            } else if (strGender.equals("Female", ignoreCase = true)) {
                strGender = dot + "F"
            }
        }
        strPhone = if (patientDetails?.patient?.getPhone() != null && !TextUtils.isEmpty(
                patientDetails!!.patient?.getPhone()) &&
            !patientDetails!!.patient?.getPhone().equals("null",ignoreCase = true)
        ) {
            dot + patientDetails!!.patient?.getPhone()
        } else {
            ""
        }
        binding?.txtAge?.setText(Html.fromHtml(age + strGender + strDob + strPhone))
        strWard = if (!TextUtils.isEmpty(patientDetails?.patient?.getWardName()?.trim()) &&
            !patientDetails?.patient?.getWardName().equals("null",ignoreCase = true)
        ) {
            dot + (patientDetails?.patient?.getWardName()  )
        } else {
            ""
        }
        if (!TextUtils.isEmpty(patientDetails?.patient?.getHospital())) {
            binding?.txtLocation?.setText(Html.fromHtml(patientDetails?.patient?.getHospital()
                .toString() + strWard))
        }
        if (!TextUtils.isEmpty(patientDetails?.patient?.getNote())) {
            val strComplaint: String? = patientDetails?.patient?.getNote()
            var stringComplaint = strComplaint
            if (strComplaint?.contains(":") == true) {
                stringComplaint = strComplaint?.substring(strComplaint.indexOf(":") + 1)
            }
            binding?.txtComplaintDetail?.setText(stringComplaint?.trim { it <= ' ' })
        }
        if (patientDetails?.patient?.getSyncTime() != null && !patientDetails?.patient
                ?.getSyncTime().equals("0")) {
            binding?.txtTimeZone?.setText(patientDetails!!.patient
                ?.getSyncTime()?.toLong()?.let { Utils().timestampToDate(it) })
        } else {
            binding?.txtTimeZone?.setText(" - ")
        }
    }

    private val dob: String
        private get() {
            val timeInMillis: Long =
                java.lang.Long.valueOf(patientDetails?.patient?.getDob())
            return SimpleDateFormat("MM-dd-yyyy").format(Date(timeInMillis))
        }
    private val age: String
        private get() {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            calendar.timeInMillis = patientDetails?.patient?.getDob()?.toLong()!!
            val agee = year - calendar[Calendar.YEAR]
            return agee.toString()
        }

    /**
     * More option dialog for RP
     *
     * @param context
     */
    fun MoreOptionRPDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.rp_more_option_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlHandOffPatient = dialog.findViewById<View>(R.id.rlHandOffPatient) as RelativeLayout
        val rlCompletedConsultation =
            dialog.findViewById<View>(R.id.rlCompletedConsultation) as RelativeLayout
        val rlCreateProgress = dialog.findViewById<View>(R.id.rlCreateProgress) as RelativeLayout
        val rleNotes = dialog.findViewById<View>(R.id.rleNotes) as RelativeLayout
        val rlActivityLogs = dialog.findViewById<View>(R.id.rlActivityLogs) as RelativeLayout
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        rlHandOffPatient.visibility = View.GONE
        rlCompletedConsultation.visibility = View.GONE
        rlCreateProgress.visibility = View.GONE
        val role: String? = PrefUtility().getRole(this)
        val strRpUserType: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        if (role.equals(Constants.ProviderRole.RD.toString(),
                ignoreCase = true) && strRpUserType == "MD/DO"
        ) {
            rlCreateProgress.visibility = View.VISIBLE
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        rleNotes.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this,ActivityEnotes::class.java)
            intent.putExtra("patient_id", mConsultProvider?.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            intent.putExtra("patient_status", mConsultProvider?.getStatus().toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, "completedchart")
            startActivity(intent)
            dialog.dismiss()
        }
        rlActivityLogs.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, ActivityLog::class.java)
            intent.putExtra("patient_id", mConsultProvider?.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            startActivity(intent)
            dialog.dismiss()
        }
        rlCreateProgress.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this,
                AddProgressEnoteActivity::class.java)
            intent.putExtra("patient_id", mConsultProvider?.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            intent.putExtra("patient_status", mConsultProvider?.getStatus().toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, "chart")
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun MoreOptionBPDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.bp_more_option_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlResetAcuity = dialog.findViewById<View>(R.id.rlResetAcuity) as RelativeLayout
        val rlTransferPatient = dialog.findViewById<View>(R.id.rlTransferPatient) as RelativeLayout
        val rlDischargePatient =
            dialog.findViewById<View>(R.id.rlDischargePatient) as RelativeLayout
        val rleNotes = dialog.findViewById<View>(R.id.rleNotes) as RelativeLayout
        val rlActivityLogs = dialog.findViewById<View>(R.id.rlActivityLogs) as RelativeLayout
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        if (mConsultProvider?.getStatus()!!.equals(Constants.PatientStatus.Completed)) {
            rlResetAcuity.visibility = View.GONE
            rlTransferPatient.visibility = View.VISIBLE
            rlDischargePatient.visibility = View.VISIBLE
        } else {
            rlResetAcuity.visibility = View.GONE
            rlTransferPatient.visibility = View.GONE
            rlDischargePatient.visibility = View.GONE
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        rlResetAcuity.setOnClickListener { }
        rlTransferPatient.setOnClickListener {
            val intent = Intent(this,
                TransferPatientActivity::class.java)
            intent.putExtra("patientId", mConsultProvider!!.getPatientsId())
            startActivity(intent)
            dialog.dismiss()
        }
        rlDischargePatient.setOnClickListener {
            doDischargePatient()
            dialog.dismiss()
        }
        rleNotes.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, ActivityEnotes::class.java)
            intent.putExtra("patient_id", mConsultProvider!!.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            intent.putExtra("patient_status", mConsultProvider!!.getStatus().toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, "completed")
            startActivity(intent)
            dialog.dismiss()
        }
        rlActivityLogs.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, ActivityLog::class.java)
            intent.putExtra("patient_id", mConsultProvider!!.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun doDischargePatient() {
        val dialog = Dialog(this, R.style.AppTheme_NoActionBarDark)
        dialog.setContentView(R.layout.activity_discharge_patient)
        dialog.show()
        val toolbarDischarge = dialog.findViewById<View>(R.id.toolbarDischarge) as Toolbar
        toolbarDischarge.setTitle(getString(R.string.discharge_patient))
        toolbarDischarge.setNavigationIcon(R.drawable.ic_back)
        toolbarDischarge.setNavigationOnClickListener { dialog.dismiss() }
        val idContainerLayout = dialog.findViewById<LinearLayout>(R.id.idContainerLayout)
        val btnDischarge = dialog.findViewById<Button>(R.id.btnDischarge)
        val edtDischargeSummary = dialog.findViewById<EditText>(R.id.edtDischargeSummary)
        btnDischarge.setOnClickListener(View.OnClickListener { v ->
            val dischargeSummary = edtDischargeSummary.text.toString()
            if (TextUtils.isEmpty(dischargeSummary)) {
                CustomSnackBar.make(v, this, CustomSnackBar.WARNING,
                    getString(R.string.summary_note_is_mandatory), CustomSnackBar.TOP, 3000, 0)
                    ?.show()
                return@OnClickListener
            }
            if (!UtilityMethods().isInternetConnected(this)!!) {
                CustomSnackBar.make(binding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
                return@OnClickListener
            }
            dialog.dismiss()
            showProgressBar()
            val strPatientId: String = java.lang.String.valueOf(mConsultProvider?.getPatientsId())
            val dischargePatientRequest = DischargePatientRequest()
            dischargePatientRequest.setPatientId(strPatientId)
            dischargePatientRequest.setDischargeSummary(dischargeSummary)
            viewModel?.bspDischargePatient(dischargePatientRequest)
                ?.observe(this) { commonResponse ->

                    if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                        dismissProgressBar()
                        if (mConsultProvider != null) {
                            mConsultProvider!!.setStatus(Constants.PatientStatus.Discharged)
                            CustomSnackBar.make(binding?.getRoot(),
                                this,
                                CustomSnackBar.SUCCESS,
                                getString(R.string.patient_discharged_successfully),
                                CustomSnackBar.TOP,
                                3000,
                                3)?.show()
                        }
                    } else {
                        dismissProgressBar()
                        btnDischarge.isEnabled = true
                        val errMsg: String? =
                            ErrorMessages().getErrorMessage(this,
                                commonResponse?.getErrorMessage(),
                                Constants.API.register)
                        CustomSnackBar.make(idContainerLayout,
                            this,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0)?.show()
                    }
                }
        })
        dialog.show()
    }

    companion object {
        private val TAG = ActivityConsultChartRemote::class.java.simpleName
    }
}