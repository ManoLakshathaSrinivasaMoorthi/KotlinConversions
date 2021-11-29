package com.example.kotlinomnicure.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityConsultChartRemoteBinding
import com.example.kotlinomnicure.helper.NotificationHelper
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.PatientDetailViewModel
import com.google.firebase.database.*
import com.google.gson.Gson
import omnicurekotlin.example.com.patientsEndpoints.model.DischargePatientRequest
import omnicurekotlin.example.com.patientsEndpoints.model.PatientDetail
import omnicurekotlin.example.com.providerEndpoints.model.Members
import java.text.SimpleDateFormat
import java.util.*

class ActivityConsultChartRemote : BaseActivity() {

    private val TAG = ActivityConsultChartRemote::class.java.simpleName
    protected var binding: ActivityConsultChartRemoteBinding? = null
    var membersList: MutableList<Members>? = ArrayList<Members>()
    var errorTeams: String? = null
    var statusStub: RelativeLayout? = null
    private var uid: Long = 0
    private var viewModel: PatientDetailViewModel? = null
    private var patientDetails: PatientDetail? = null
    private var strPhone: String? = null
    private var strDob: String? = null
    private var strGender: String? = null
    private  var strWard:kotlin.String? = null
    private var mConsultProvider: ConsultProvider? = null
    private var mConsultProviderKey: String? = null
    private var strProviderNameType: String? = null
    private  var strTime:kotlin.String? = null
    private  var strStatus:kotlin.String? = null
    private var stub: ViewStub? = null
    private var strMessageReaded: String? = null
    private var mUnreadMessageDB: DatabaseReference? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    var mUnreadMessageListener: ValueEventListener = object : ValueEventListener {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val consultProviderA: ConsultProvider? =
                dataSnapshot.getValue(ConsultProvider::class.java)
            println("snapshot data $consultProviderA")
            if (consultProviderA != null && !consultProviderA.equals("null")) {
                if (consultProviderA.getUnread() > 0) {
                    Log.d(TAG, "Unread Message Count" + consultProviderA.getUnread())
                    //                mFirebaseDatabaseReference.child("providers").child(mProviderUid).child("active").child(mConsultProviderKey).child("unread").setValue(0);
                    binding?.imgMessageAlert?.setVisibility(View.VISIBLE)
                } else {
                    binding?.imgMessageAlert?.setVisibility(View.GONE)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "onCancelled: of value event listener $databaseError")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_consult_chart_remote)
        viewModel = ViewModelProvider(this).get(PatientDetailViewModel::class.java)
        initViews()
        initOnClickListener()
    }

    private fun initViews() {
        stub = findViewById<ViewStub>(R.id.layout_stub_view)
        statusStub = findViewById<RelativeLayout>(R.id.status_stub)
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference

//        uid = PrefUtility.getLongInPref(ActivityConsultChart.this, Constants.SharedPrefConstants.USER_ID, 0);
        uid = intent.getLongExtra("uid", 0)
        if (PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "").equals("RD")
        ) {
            binding?.llCallLayout?.visibility = View.GONE
        } else {
            binding?.llCallLayout?.visibility = View.GONE
        }
        setProviderObject()
        val role: String? = PrefUtility().getRole(this)
        if (role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
            if (mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Discharged) == true) {
                binding?.floatFab?.setVisibility(View.VISIBLE)
            } else {
                binding?.floatFab?.visibility = View.GONE
            }
        } else {
            binding?.floatFab?.setVisibility(View.GONE)
        }
        getPatientDetails(uid)
    }

    private fun setProviderObject() {
        mConsultProvider = ConsultProvider()
        strProviderNameType = intent.getStringExtra("providerNameType")
        strTime = intent.getStringExtra("completedTime")
        strStatus = intent.getStringExtra("status")
        Log.d(TAG, "strstatus$strStatus")
        mConsultProviderKey = intent.getStringExtra("consultProviderId")
        mConsultProvider?.setId(intent.getStringExtra("consultProviderPatientId")?.toLong())
        mConsultProvider?.setPatientId(intent.getStringExtra("consultProviderPatientId"))
        mConsultProvider?.setText(intent.getStringExtra("consultProviderText"))
        mConsultProvider?.setName(intent.getStringExtra("consultProviderName"))
        mConsultProvider?.setUnread(intent.getIntExtra("unreadMessageCount", 0))
        val dob = intent.getLongExtra("dob", -1)
        mConsultProvider?.setDob(dob)
        val gender = intent.getStringExtra("gender")
        mConsultProvider?.setGender(gender)
        val note = intent.getStringExtra("note")
        mConsultProvider?.setNote(note)
        val status = intent.getStringExtra("status")
        if (!TextUtils.isEmpty(status)) {
            mConsultProvider?.setStatus(status?.let { Constants.PatientStatus.valueOf(it) })
        }
        mConsultProvider?.setUrgent(
            intent.getBooleanExtra(
                Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                false
            )
        )
        mConsultProvider?.setTeamName(intent.getStringExtra("teamNameConsult"))
        if (intent.hasExtra("phone")) {
            mConsultProvider?.setPhone(intent.getStringExtra("phone"))
        }
        if (mConsultProvider?.getUnread()!! > 0) {
            binding?.imgMessageAlert?.visibility = View.VISIBLE
        } else {
            binding?.imgMessageAlert?.visibility = View.GONE
        }
    }

    private fun initOnClickListener() {
        binding?.imgBack?.setOnClickListener { finish() }
        binding?.imgDetailUp?.setOnClickListener {
            binding?.imgDetailUp?.visibility = View.GONE
            binding?.imgDetailDown?.visibility = View.VISIBLE
            binding?.llComplaints?.visibility = View.GONE
        }
        binding?.imgDetailDown?.setOnClickListener {
            binding?.imgDetailDown?.visibility = View.GONE
            binding?.imgDetailUp?.visibility = View.VISIBLE
            binding?.llComplaints?.visibility = View.VISIBLE
        }
        binding?.imgVitalUp?.setOnClickListener {
            binding?.imgVitalUp?.visibility = View.GONE
            binding?.imgVitalDown?.visibility = View.VISIBLE
            binding?.llTimeZone?.visibility = View.GONE
            binding?.llVital?.visibility = View.GONE
        }
        binding?.imgVitalDown?.setOnClickListener {
            binding?.imgVitalDown?.visibility = View.GONE
            binding?.imgVitalUp?.visibility = View.VISIBLE
            binding?.llTimeZone?.visibility = View.VISIBLE
            binding?.llVital?.visibility = View.VISIBLE
        }
        binding?.llMessage?.setOnClickListener {
            val intentConsultChart =
                Intent(this@ActivityConsultChartRemote, ChatActivity::class.java)
            intentConsultChart.putExtra("uid", mConsultProvider?.getId())
            intentConsultChart.putExtra("path", "consults/" + mConsultProvider?.getId())
            intentConsultChart.putExtra("consultProviderId", "" + mConsultProvider?.getId())
            intentConsultChart.putExtra("consultProviderPatientId", "" + mConsultProvider?.getId())
            intentConsultChart.putExtra("consultProviderText", mConsultProvider?.getText())
            intentConsultChart.putExtra("consultProviderName", mConsultProvider?.getName())
            intentConsultChart.putExtra("dob", mConsultProvider?.getDob())
            intentConsultChart.putExtra("gender", mConsultProvider?.getGender())
            intentConsultChart.putExtra("note", mConsultProvider?.getNote())
            intentConsultChart.putExtra("phone", mConsultProvider?.getPhone())
            intentConsultChart.putExtra("patientId", mConsultProvider?.getPatientId())
            intentConsultChart.putExtra("ConsultChartRemote", "chartRemote")
            intentConsultChart.putExtra("status", mConsultProvider?.getStatus())
            intentConsultChart.putExtra("teamNameConsult", mConsultProvider?.getTeamName())
            Log.d(TAG, "mConsultProviderKey : " + mConsultProvider?.getId())
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                mConsultProvider?.getUrgent()
            )
            if (mConsultProvider?.getStatus() != null) {
                intentConsultChart.putExtra("status", mConsultProvider?.getStatus().toString())
                if (mConsultProvider?.getStatus() === Constants.PatientStatus.Invited ||
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Handoff
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    mConsultProvider?.getId()?.toInt()?.let { it1 -> clearNotifications(it1) }
                } else if (mConsultProvider?.getStatus() === Constants.PatientStatus.Discharged ||
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Exit
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }
            startActivityForResult(intentConsultChart, 1)
        }
        binding?.txtContactTeam?.setOnClickListener {
            if (membersList!!.size > 0 && membersList != null) {
                mConsultProvider?.let { it1 ->
                    showTeamMembersDialog(this, membersList, it1)
                }
            } else {
//                    UtilityMethods.showErrorSnackBar(binding.containerLayout, errorTeams, Snackbar.LENGTH_LONG);
                errorTeams?.let { it1 ->
                    CustomSnackBar.make(
                        binding?.containerLayout, this, CustomSnackBar.WARNING, it1,
                        CustomSnackBar.TOP, 3000, 0
                    )?.show()
                }
            }
        }
        if (strProviderNameType != null && !TextUtils.isEmpty(strProviderNameType)) {
//            binding.txtProviderName.setText(strProviderNameType);
            val vals = strProviderNameType?.split(",".toRegex())?.toTypedArray()
            var providerName = ""
            if (vals?.size!! > 1 && vals.get(0).length > 12) {
                vals[0] = vals[0].substring(0, 12) + ".."
            }
            providerName = TextUtils.join(",", vals)
            providerName = providerName.substring(0, 1).toUpperCase() + providerName.substring(1)
            binding?.txtProviderName?.setText(providerName)
        }
        if (strTime != "null") {
            binding?.txtTime?.setText(strTime?.toLong()?.let { Utils().timestampToDate(it) })
        }
        if (strStatus != null) {
            if (strStatus.equals("Discharged", ignoreCase = true)) {
                binding?.txtStatus?.setText("Completed")
            } else if (strStatus.equals("Exit", ignoreCase = true)) {
                binding?.txtStatus?.setText("Discharged")
            }
        }
        binding?.floatFab?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            val role: String? = PrefUtility().getRole(this)
            if (role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
                MoreOptionBPDialog(this)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mUnreadMessageDB?.removeEventListener(mUnreadMessageListener)
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                strMessageReaded = data?.getStringExtra("messageValue")
                if (!TextUtils.isEmpty(strMessageReaded)) {
                    binding?.imgMessageAlert?.setVisibility(View.GONE)
                } else {
                    binding?.imgMessageAlert?.setVisibility(View.VISIBLE)
                }
            }
        }
    }

    private fun getTeamMemberDetails(patientId: Long, teamName: String) {
        membersList?.clear()
        val patID = java.lang.Long.valueOf(mConsultProvider?.getPatientId())
        viewModel?.getMemberList(patID, teamName)?.observe(this, {
            val response= it.body()
            dismissProgressBar()
            if (response != null && response.getStatus() != null && response.getStatus()!!) {
                if (response.getTeamDetails()!!.getMembers() != null) {
                    membersList!!.addAll(response.getTeamDetails()!!.getMembers())
                }
            } else {
                errorTeams = response?.getErrorMessage()
            }
        })
    }

    private fun clearNotifications(notificationId: Int) {
        NotificationHelper(this,base = null).clearNotification(notificationId)
    }

    override fun onStart() {
        super.onStart()
        try {
            unReadMessageListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun unReadMessageListener() {
        Log.d(TAG, "unReadMessageListener: ")
        val mProviderUid: String? = PrefUtility().getFireBaseUid(this) //mFirebaseUser.getUid();
        mUnreadMessageDB = mProviderUid?.let {
                mFirebaseDatabaseReference?.child("providers")?.child(it)?.child("active")
                    ?.child(mConsultProviderKey!!)
            }
        mUnreadMessageDB?.addValueEventListener(mUnreadMessageListener)
    }

    private fun getPatientDetails(uid: Long) {
        showProgressBar(
            PBMessageHelper().getMessage(
                this,
                Constants.API.getPatientDetails.toString()
            )
        )
        viewModel?.getPatienDetails(uid)?.observe(this) {
            val response=it.body()
            val patID: Long = java.lang.Long.valueOf(mConsultProvider?.getPatientId())
            Log.d(TAG, "Patient Details Res" + Gson().toJson(response))
            if (response?.getStatus() != null && response?.getStatus()!!) {
                val gson = Gson()
                patientDetails = gson.fromJson(
                    java.lang.String.valueOf(response),
                    PatientDetail::class.java
                )
                populateConsultDetails()
                getTeamMemberDetails(patID, "Team " + patientDetails?.getPatient()?.getTeamName())
            } else {
                dismissProgressBar()
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    java.lang.String.valueOf(response?.getErrorId()),
                    Constants.API.getHospital
                )
                //                UtilityMethods.showErrorSnackBar(binding.containerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let { CustomSnackBar.make(
                    binding?.containerLayout, this, CustomSnackBar.WARNING,
                    it, CustomSnackBar.TOP, 3000, 0
                )?.show()
                }
            }
        }
    }

    private fun populateConsultDetails() {
        val heartRateValue: String? = patientDetails?.getPatient()?.getHeartRateValue()
        val highBPValue: String? = patientDetails?.getPatient()?.getArterialBloodPressureSystolicValue()
        val lowBPValue: String? = patientDetails?.getPatient()?.getArterialBloodPressureDiastolicValue()
        val spo2Value: String? = patientDetails?.getPatient()?.getSpO2Value()
        val respRateValue: String? = patientDetails?.getPatient()?.getRespiratoryRateValue()
        val fiO2Value: String? = patientDetails?.getPatient()?.getFio2Value()
        val tempValue: String? = patientDetails?.getPatient()?.getTemperatureValue()
        val provider = ConsultProvider()
        if (!TextUtils.isEmpty(heartRateValue)) {
            provider.setHeartRate(patientDetails?.getPatient()?.getHeartRate())
        }
        if (!TextUtils.isEmpty(highBPValue)) {
            provider.setArterialBloodPressureSystolic(
                patientDetails?.getPatient()?.getArterialBloodPressureSystolic()
            )
        }
        if (!TextUtils.isEmpty(lowBPValue)) {
            provider.setArterialBloodPressureDiastolic(
                patientDetails?.getPatient()?.getArterialBloodPressureDiastolic()
            )
        }
        if (!TextUtils.isEmpty(spo2Value)) {
            provider.setSpO2(patientDetails?.getPatient()?.getSpO2())
        }
        if (!TextUtils.isEmpty(fiO2Value)) {
            provider.setFio2(patientDetails?.getPatient()?.getFio2())
        }
        if (!TextUtils.isEmpty(respRateValue)) {
            provider.setRespiratoryRate(patientDetails?.getPatient()?.getRespiratoryRate())
        }
        if (!TextUtils.isEmpty(tempValue)) {
            provider.setTemperature(patientDetails?.getPatient()?.getTemperature())
        }
        provider.setOxygenSupplement(patientDetails?.getPatient()?.isOxygenSupplement())
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getPatientCondition())) {
            when {
                patientDetails?.getPatient()?.getPatientCondition()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientCondition.Alert)) == true -> {
                    provider.setPatientCondition(Constants.PatientCondition.Alert)
                }
                patientDetails?.getPatient()?.getPatientCondition()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientCondition.Voice)) == true -> {
                    provider.setPatientCondition(Constants.PatientCondition.Voice)
                }
                patientDetails?.getPatient()?.getPatientCondition()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientCondition.Pain)) == true -> {
                    provider.setPatientCondition(Constants.PatientCondition.Pain)
                }
                patientDetails?.getPatient()?.getPatientCondition()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientCondition.Unresponsive)) == true -> {
                    provider.setPatientCondition(Constants.PatientCondition.Unresponsive)
                }
            }
        }
        stub?.let { UtilityMethods().displayVitals(this, it, provider) }
        mConsultProvider?.getUrgent()?.let {
            patientDetails?.getPatient()?.getScore()?.let { it1 ->
                Constants.AcuityLevel.valueOf(it1)
            }?.let { it2 ->
                UtilityMethods().displayPatientStatusComponent(
                    this,
                    statusStub,
                    it,
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Pending,
                    it2
                )
            }
        }
        if (mConsultProvider?.getStatus() === Constants.PatientStatus.Pending) {
            binding?.txtContactTeam?.setVisibility(View.GONE)
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getRecordNumber())) {
            binding?.txtMRNNumber?.setText(
                Html.fromHtml("MRN&nbsp;" + patientDetails?.getPatient()?.getRecordNumber())
            )
        } else {
            binding?.txtMRNNumber?.setText("MRN ")
        }
        binding?.txtPatientName?.setText(
            patientDetails?.getPatient()?.getFname().toString() + " " + patientDetails?.getPatient()
                ?.getLname()
        )
        val dot = " <b>\u00b7</b> "
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getDob())) {
            strDob = dot + getDob()
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getGender())) {
            strGender = patientDetails?.getPatient()?.getGender()
            if (strGender.equals("Male", ignoreCase = true)) {
                strGender = dot + "M"
            } else if (strGender.equals("Female", ignoreCase = true)) {
                strGender = dot + "F"
            }
        }
        strPhone = if (patientDetails?.getPatient()?.getPhone() != null && !TextUtils.isEmpty(
                patientDetails?.getPatient()?.getPhone()
            ) &&
            !patientDetails?.getPatient()?.getPhone()?.contentEquals("null")!!
        ) {
            dot + patientDetails?.getPatient()?.getPhone()
        } else {
            ""
        }
        binding?.txtAge?.setText(Html.fromHtml(getAge() + strGender + strDob + strPhone))
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getWardName()?.trim()) &&
            !patientDetails?.getPatient()?.getWardName()?.contentEquals("null")!!
        ) {
            strWard = dot + patientDetails?.getPatient()?.getWardName()
        } else {
            strWard = ""
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getHospital())) {
            binding?.txtLocation?.setText(
                Html.fromHtml(patientDetails?.getPatient()?.getHospital().toString() + strWard)
            )
        }
        if (!TextUtils.isEmpty(patientDetails?.getPatient()?.getNote())) {
            val strComplaint: String? = patientDetails?.getPatient()?.getNote()
            val stringComplaint = strComplaint?.substring(strComplaint.indexOf(":") + 1)
            binding?.txtComplaintDetail?.setText(stringComplaint?.trim { it <= ' ' })
        }
        if (patientDetails?.getPatient()?.getSyncTime() != null && !patientDetails!!.getPatient()
                ?.getSyncTime().equals("0")
        ) {
            binding?.txtTimeZone?.setText(
                Utils().timestampToDate(patientDetails?.getPatient()?.getSyncTime()!!.toLong())
            )
        } else {
            binding?.txtTimeZone?.setText(" - ")
        }
    }

    private fun getDob(): String {
        val timeInMillis: Long = java.lang.Long.valueOf(patientDetails?.getPatient()?.getDob())
        return SimpleDateFormat("MM-dd-yyyy").format(Date(timeInMillis))
    }

    private fun getAge(): String {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        calendar.timeInMillis = patientDetails?.getPatient()?.getDob()?.toLong()!!
        val agee = year - calendar[Calendar.YEAR]
        return agee.toString()
    }

    fun MoreOptionBPDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.bp_more_option_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlResetAcuity = dialog.findViewById<View>(R.id.rlResetAcuity) as RelativeLayout
        val rlTransferPatient = dialog.findViewById<View>(R.id.rlTransferPatient) as RelativeLayout
        val rlDischargePatient =
            dialog.findViewById<View>(R.id.rlDischargePatient) as RelativeLayout
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        if (mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Discharged) == true) {
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
            val intent = Intent(this, TransferPatientActivity::class.java)
            intent.putExtra("patientId", mConsultProvider?.getPatientId())
            startActivity(intent)
            dialog.dismiss()
        }
        rlDischargePatient.setOnClickListener {
            doDischargePatient()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun doDischargePatient() {
        val dialog = Dialog(this, R.style.AppTheme_NoActionBarDark)
        dialog.setContentView(R.layout.activity_discharge_patient)
        dialog.show()
        val toolbarDischarge = dialog.findViewById<View>(R.id.toolbarDischarge) as Toolbar
        toolbarDischarge.title = getString(R.string.discharge_patient)
        toolbarDischarge.setNavigationIcon(R.drawable.ic_back)
        toolbarDischarge.setNavigationOnClickListener { dialog.dismiss() }
        val idContainerLayout = dialog.findViewById<LinearLayout>(R.id.idContainerLayout)
        val btnDischarge = dialog.findViewById<Button>(R.id.btnDischarge)
        val edtDischargeSummary = dialog.findViewById<EditText>(R.id.edtDischargeSummary)
        btnDischarge.setOnClickListener(View.OnClickListener { v ->
            val dischargeSummary = edtDischargeSummary.text.toString()
            if (TextUtils.isEmpty(dischargeSummary)) {
                CustomSnackBar.make(
                    v, this, CustomSnackBar.WARNING,
                    getString(R.string.summary_note_is_mandatory), CustomSnackBar.TOP, 3000, 0
                )?.show()
                return@OnClickListener
            }
            if (!UtilityMethods().isInternetConnected(this@ActivityConsultChartRemote)) {
                CustomSnackBar.make(
                    binding?.getRoot(), this@ActivityConsultChartRemote, CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0
                )?.show()
                return@OnClickListener
            }
            dialog.dismiss()
            showProgressBar()
            val strPatientId: String? = mConsultProvider?.getPatientId()
            val dischargePatientRequest = DischargePatientRequest()
            dischargePatientRequest.setPatientId(strPatientId)
            dischargePatientRequest.setDischargeSummary(dischargeSummary)
            viewModel?.bspDischargePatient(dischargePatientRequest)
                ?.observe(this@ActivityConsultChartRemote) {
                    val commonResponse=it.body()
                    Log.d(
                        TAG,
                        "Discharge patient response: " + Gson().toJson(commonResponse)
                    )
                    if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                        dismissProgressBar()
                        if (mConsultProvider != null) {
                            mConsultProvider?.setStatus(Constants.PatientStatus.Exit)
                            CustomSnackBar.make(
                                binding?.getRoot(),
                                this,
                                CustomSnackBar.SUCCESS,
                                getString(R.string.patient_discharged_successfully),
                                CustomSnackBar.TOP,
                                3000,
                                3
                            )?.show()
                        }
                    } else {
                        dismissProgressBar()
                        btnDischarge.isEnabled = true
                        val errMsg: String? = ErrorMessages().getErrorMessage(
                            this, commonResponse?.getErrorMessage(),
                            Constants.API.register
                        )
                        errMsg?.let {
                            CustomSnackBar.make(
                                idContainerLayout, this,
                                CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0
                            )?.show()
                        }
                    }
                }
        })
        dialog.show()
    }


    private fun <E> MutableList<E>.addAll(elements: List<E?>?): List<E?>? {
        return elements
    }
}


