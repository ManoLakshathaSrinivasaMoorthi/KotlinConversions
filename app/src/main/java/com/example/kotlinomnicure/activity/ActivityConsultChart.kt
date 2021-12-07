package com.example.kotlinomnicure.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.viewmodel.HomeViewModel

import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.customview.CustomDialog
import com.example.kotlinomnicure.databinding.ActivityEconsultChartBinding
import com.example.kotlinomnicure.helper.NotificationHelper
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.viewmodel.PatientDetailViewModel
import com.google.firebase.database.*


import com.google.gson.Gson
import omnicurekotlin.example.com.patientsEndpoints.model.PatientDetail
import omnicurekotlin.example.com.providerEndpoints.HandOffAcceptRequest

import omnicurekotlin.example.com.providerEndpoints.model.Members

import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ActivityConsultChart : BaseActivity() {
    protected var binding: ActivityEconsultChartBinding? = null
    var statusStub: RelativeLayout? = null
    var providerApiFlag = false
    private var mUnreadMessageDB: DatabaseReference? = null
    private val membersList: MutableList<Members>? = ArrayList<Members>()
    private var errorTeams: String? = null
    private var acuityLevel: Constants.AcuityLevel = Constants.AcuityLevel.NA
    private var strConsultTeamName: String? = null
    private var uid: Long = 0
    private val patientId: Long = 0
    private var viewModel: PatientDetailViewModel? = null
    private var patientDetails: PatientDetail? = null
    private var strPhone: String? = null
    private var strDob: String? = null
    private var strGender: String? = null
    private var strWard: String? = null
    private val role: String? = null
    private var mConsultProvider: ConsultProvider? = null
    private var mConsultProviderKey: String? = null
    private var stub: ViewStub? = null
    private var strMessageReaded: String? = null
    private var customDialog: CustomDialog? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private var strAcuityScore: String? = null
    private var strScreenCensus = ""
    private var context:Context=ActivityConsultChart()

    // Unread message listener from firebase - Add Consult provider data from the snapshot
    private var mUnreadMessageListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val consultProviderA: ConsultProvider? =
                dataSnapshot.getValue(ConsultProvider::class.java)

            // If the provider data is null - show error dialog
            if (consultProviderA == null) {
                showErrorDialog()
                return
            }
            // If the data has status value, details are updated
            if (consultProviderA.getStatus() != null && consultProviderA.getStatus()!!
                    .equals(Constants.PatientStatus.Active)
                && !mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Active)!!) {

                if (providerApiFlag) {
                    return
                }
                mConsultProvider?.setStatus(Constants.PatientStatus.Active)
                //Getting patient details via "getPatienDetails" API call based on the UID
                getPatientDetails(uid)
                binding?.txtAccept?.setVisibility(View.GONE)
                binding?.txtContactTeam?.setVisibility(View.VISIBLE)
                val role: String? = PrefUtility().getRole(context)
                val strRpUserType: String? = PrefUtility().getStringInPref(context,
                    Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")

            }
            if (consultProviderA.getStatus() != null
                && !mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Pending)!!
            ) {
                mConsultProvider?.setStatus(consultProviderA.getStatus())
            }
            if (consultProviderA.getUnread() > 0) {

                binding?.imgMessageAlert?.setVisibility(View.VISIBLE)
            } else {
                binding?.imgMessageAlert?.setVisibility(View.GONE)
            }
            // Removing the stubs
            if (mConsultProvider?.getScore() != null && mConsultProvider?.getScore() !== consultProviderA.getScore()) {
                statusStub!!.removeAllViews()
                //                statusStub
                statusStub?.let {
                    UtilityMethods().displayPatientStatusComponent(context,
                        it,
                        mConsultProvider?.getUrgent(),
                        consultProviderA.getStatus() === Constants.PatientStatus.Pending,
                        consultProviderA.getScore())
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    /**
     * Showing error dialog based on the patient status like Handoff, HandoffPending
     */
    fun showErrorDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View = LayoutInflater.from(getApplicationContext())
            .inflate(R.layout.custom_alert_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.visibility = View.GONE
        var message: String? = getString(R.string.consultation_request_already_accepted_msg)
        val strRole: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        val strDesignation: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        if (mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Handoff) == true
            || mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.HandoffPending) == true
        ) {
            if (strRole == "RD" && !strDesignation.equals("MD/DO", ignoreCase = true)) {
                message = getString(R.string.patient_handed_off_to_other_grp)
            } else {
                message = getString(R.string.handoff_request_already_accepted_msg)
            }
        } else {
            message = getString(R.string.consultation_request_already_accepted_msg)
        }
        alertMsg.text = message
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        // Okay click listener directs the user to home activity or close the activity based on the SCREEN_CENSUS
        buttonOk.setOnClickListener {
            if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(Constants.IntentKeyConstants.SCREEN_CENSUS,
                    ignoreCase = true)
            ) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        alertDialog.show()
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Databinding and view model intialization
        binding = DataBindingUtil.setContentView(this, R.layout.activity_econsult_chart)
        viewModel = ViewModelProvider(this).get(PatientDetailViewModel::class.java)
        // Initiating the views for the activity
        initViews()
        //  Initiating the on click listener for the activity
        initOnClickListener()
    }

    /**
     * Initiating the views for the activity
     */
    private fun initViews() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        stub = findViewById<ViewStub>(R.id.layout_stub_view)
        statusStub = findViewById<RelativeLayout>(R.id.status_stub)

        // Getting the values from the intent
        uid = getIntent().getLongExtra("uid", 0)

//        Log.d(TAG, "OnCreate UID" + uid);
        if (PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
                .equals("RD")
        ) {
            binding?.llCallLayout?.setVisibility(View.INVISIBLE)
        } else {
            binding?.llCallLayout?.setVisibility(View.VISIBLE)
        }

        // Setting up the providing object
        setProviderObject()
        val role: String? = PrefUtility().getRole(this)
        val strRpUserType: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.R_PROVIDER_TYPE,
            "")

        val providerID: Long? = PrefUtility().getProviderId(this)

        // Getting the patient details via API call using UID as inout
        getPatientDetails(uid)
    }

    /**
     * Removing the event listener for unread message listener
     */
    override fun onStop() {
//        System.out.println("consult chart onstop");
        super.onStop()
        mUnreadMessageDB!!.removeEventListener(mUnreadMessageListener)
    }

    /**
     * Intiating the on click listener
     */
    private fun initOnClickListener() {
        binding?.imgBack?.setOnClickListener(View.OnClickListener { finish() })
        // Image up click listener
        binding?.imgDetailUp?.setOnClickListener(View.OnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding?.imgDetailUp?.setVisibility(View.GONE)
            binding?.imgDetailDown?.setVisibility(View.VISIBLE)
            binding?.llComplaints?.setVisibility(View.GONE)
        })

        // Image down click listener
        binding?.imgDetailDown?.setOnClickListener(View.OnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding?.imgDetailDown?.setVisibility(View.GONE)
            binding?.imgDetailUp?.setVisibility(View.VISIBLE)
            binding?.llComplaints?.setVisibility(View.VISIBLE)
        })

        // Image vital up click listener
        binding?.imgVitalUp?.setOnClickListener(View.OnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding?.imgVitalUp?.setVisibility(View.GONE)
            binding?.imgVitalDown?.setVisibility(View.VISIBLE)
            binding?.llTimeZone?.setVisibility(View.GONE)
            binding?.llVital?.setVisibility(View.GONE)
        })
        // Image vital down click listener
        binding?.imgVitalDown?.setOnClickListener(View.OnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding?.imgVitalDown?.setVisibility(View.GONE)
            binding?.imgVitalUp?.setVisibility(View.VISIBLE)
            binding?.llTimeZone?.setVisibility(View.VISIBLE)
            binding?.llVital?.setVisibility(View.VISIBLE)
        })
        // Handling the message click
        binding?.llMessage?.setOnClickListener(View.OnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)

            // Directing the user to chat activity with needed data
            val intentConsultChart = Intent(this, ChatActivity::class.java)
            intentConsultChart.putExtra("uid", mConsultProvider?.getId())
            //                Log.d(TAG, "strUid : " + mConsultProvider.getId());
            intentConsultChart.putExtra("path", "consults/" + mConsultProvider?.getId())
            intentConsultChart.putExtra("consultProviderId", "" + mConsultProviderKey)
            intentConsultChart.putExtra(getString(R.string.consultProviderPatientId),
                "" + mConsultProvider?.getId())
            intentConsultChart.putExtra("consultProviderText", mConsultProvider?.getText())
            intentConsultChart.putExtra("consultProviderName", mConsultProvider?.getName())
            intentConsultChart.putExtra("dob", mConsultProvider?.getDob())
            intentConsultChart.putExtra("gender", mConsultProvider?.getGender())
            intentConsultChart.putExtra("note", mConsultProvider?.getNote())
            intentConsultChart.putExtra("phone", mConsultProvider?.getPhone())
            intentConsultChart.putExtra("patientId", mConsultProvider?.getPatientsId())
            intentConsultChart.putExtra("status", mConsultProvider?.getStatus())
            if (!TextUtils.isEmpty(strConsultTeamName)) {
                intentConsultChart.putExtra("teamNameConsult", strConsultTeamName)
            }
            intentConsultChart.putExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                mConsultProvider?.getUrgent())
            //Setting the consult provider status if not null
            if (mConsultProvider?.getStatus() != null) {
                intentConsultChart.putExtra("status", mConsultProvider!!.getStatus().toString())
                if (mConsultProvider?.getStatus() === Constants.PatientStatus.Invited ||
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Handoff
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    clearNotifications(mConsultProvider?.getId())
                } else if (mConsultProvider?.getStatus() === Constants.PatientStatus.Completed ||
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Discharged
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID.toLong())
                }
            }
            startActivityForResult(intentConsultChart, 1)
        })

        // Handling contact team click
        binding?.txtContactTeam?.setOnClickListener(View.OnClickListener { v ->
            // Handling the multi click
            handleMultipleClick(v)
            if (membersList!!.size > 0 && membersList != null) {
                mConsultProvider?.let { showTeamMembersDialog(this, membersList, it) }
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
        // Handling accept team click
        binding?.txtAccept?.setOnClickListener(View.OnClickListener { // Handling the multi click
            handleMultipleClick(binding?.txtAccept!!)
            // On accept click listener with consult provider data
            acceptClick(mConsultProvider)
        })

        // Handling call click for SoS
        binding?.llCallLayout?.setOnClickListener(View.OnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            // Connecting to the SoS call
            checkSelfPermissionsMediaCheck()
            connectSOSCall()
        })

        // Handling more option click listener
        binding?.floatFab?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            val role: String? = PrefUtility().getRole(this)
            val strRpUserType: String? = PrefUtility().getStringInPref(this,
                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                "")
            if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                // More option UI and its options based on the role (MD/DO)
                MoreOptionRPDialog(this)
            } else {
                // More option UI and its options based on the role(Others)
                MoreOptionBPDialog(this)
            }
        })
    }


    /**
     * Adding the un read message listener
     */
    override fun onStart() {

        super.onStart()
        try {
            unReadMessageListener()
        } catch (e: Exception) {

        }
    }

    /**
     * Method to add un read message listener
     */
    private fun unReadMessageListener() {

        val mProviderUid: String? = PrefUtility().getFireBaseUid(this) //mFirebaseUser.getUid();
        mUnreadMessageDB =
            mProviderUid?.let {
                mFirebaseDatabaseReference!!.child("providers").child(it).child("active")
                    .child(mConsultProviderKey!!)
            }
        mUnreadMessageDB!!.addValueEventListener(mUnreadMessageListener)
    }

    /**
     * Message alert text is handled based on the result code
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    strMessageReaded = data.getStringExtra("messageValue")
                    if (!TextUtils.isEmpty(strMessageReaded)) {
                        binding?.imgMessageAlert?.setVisibility(View.GONE)
                    } else {
                        binding?.imgMessageAlert?.setVisibility(View.VISIBLE)
                    }
                }
            }
        }
        // Finish the code, when the code is - 505
        if (requestCode == 505) {
            if (resultCode == RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    /**
     * Connecting to SoS call via startSOS API call and directing to call activity
     */
    private fun connectSOSCall() {
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }
        val title: String =
            getString(R.string.sos_alert_confirmation_msg).toString() + " " + mConsultProvider?.getName()
        val message: String = getString(R.string.alert_msg)
        customDialog = UtilityMethods().showDialog(this,
            title,
            message,
            false,
            R.string.send,
            {
                showProgressBar()
                val providerID: Long?= PrefUtility().getProviderId(this)
                val token: String? = PrefUtility().getStringInPref(this,
                    Constants.SharedPrefConstants.TOKEN,
                    "")

                if (providerID != null) {
                    if (token != null) {
                        mConsultProvider?.patientId?.let { it1 ->
                            viewModel?.startSOS(providerID, token, it1)
                                ?.observe(this) { commonResponse ->
                                    dismissProgressBar()
                                    if (commonResponse != null && commonResponse.getStatus()) {
                                        // Directing to call activity
                                        val callScreen =
                                            Intent(this, CallActivity::class.java)
                                        callScreen.putExtra("providerName",
                                            mConsultProvider!!.getBdProviderName())
                                        callScreen.putExtra("providerHospitalName",
                                            mConsultProvider!!.getHospital())
                                        callScreen.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME,
                                            java.lang.String.valueOf(mConsultProvider?.patientsId))
                                        callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                                        callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_MODE,
                                            getResources().getStringArray(R.array.encryption_mode_values)
                                                .get(0))
                                        callScreen.putExtra(Constants.IntentKeyConstants.AUDIT_ID,
                                            commonResponse.getAuditId())
                                        callScreen.putExtra("patientId", mConsultProvider?.patientsId)
                                        callScreen.putExtra("sos", true)
                                        callScreen.putExtra("callType", "outgoing")
                                        val gson = Gson()
                                        callScreen.putExtra("providerList",
                                            gson.toJson(commonResponse.getProviderList()))
                                        startActivity(callScreen)
                                    } else {
                                        val errMsg: String? =
                                            ErrorMessages()?.getErrorMessage(this,
                                                commonResponse?.getErrorMessage(),
                                                Constants.API.startCall)

                                        CustomSnackBar.make(binding?.getRoot(),
                                            this,
                                            CustomSnackBar.WARNING,
                                            errMsg,
                                            CustomSnackBar.TOP,
                                            3000,
                                            0)?.show()
                                    }
                                }
                        }
                    }
                }
                customDialog?.dismiss()
            },
            R.string.cancel,
            { customDialog?.cancel() },
            Color.RED,
            true)
    }

    /**
     * Handling the multiple click based on the view
     *
     * @param view
     */
    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    /**
     * Getting the team member details via "getMemberList" API call
     *
     * @param patientId
     * @param teamName
     */
    private fun getTeamMemberDetails(patientId: Long, teamName: String) {
     /*   membersList!!.clear()

        viewModel?.getMemberList(patientId, teamName)?.observe(this) { teamsDetailListResponse ->
            dismissProgressBar()
            if (teamsDetailListResponse != null && teamsDetailListResponse.getStatus() != null && teamsDetailListResponse.getStatus()!!) {

                if (teamsDetailListResponse.getTeamDetails()?.getMembers() != null) {
                    membersList.clear()
                    teamsDetailListResponse.teamDetails?.let { membersList.addAll(it.getMembers()) }
                    strConsultTeamName = teamsDetailListResponse.getTeamDetails()!!.getName()
                }
            } else if (!TextUtils.isEmpty(teamsDetailListResponse?.getErrorMessage()) && teamsDetailListResponse?.getErrorMessage() != null) {
                errorTeams = teamsDetailListResponse?.getErrorMessage()

            } else {
                errorTeams = getString(R.string.api_error)
            }
        }*/
    }

    /**
     * Clearing the notification based in the notification ID
     *
     * @param notificationId
     */
    private fun clearNotifications(notificationId: Long?) {
        NotificationHelper(this, null).clearNotification(notificationId)
    }

    /**
     * Setting the provider data with received input intent values
     */
    private fun setProviderObject() {
        mConsultProvider = ConsultProvider()
        mConsultProviderKey = getIntent().getStringExtra("consultProviderId")

        mConsultProvider?.setId(getIntent().getStringExtra(getString(R.string.consultProviderPatientId))
            ?.toLong())
        mConsultProvider?.setPatientsId(getIntent().getStringExtra(getString(R.string.consultProviderPatientId))
            ?.toLong())
        mConsultProvider?.setText(getIntent().getStringExtra("consultProviderText"))
        mConsultProvider?.setName(getIntent().getStringExtra("consultProviderName"))
        mConsultProvider?.setUnread(getIntent().getIntExtra("unreadMessageCount", 0))
        val dob: Long = getIntent().getLongExtra("dob", -1)
        mConsultProvider?.setDob(dob)
        val gender: String = getIntent().getStringExtra("gender").toString()
        mConsultProvider?.setGender(gender)
        val note: String? = getIntent().getStringExtra("note")
        mConsultProvider?.setNote(note)
        val status: String? = getIntent().getStringExtra("status")
        //        Log.e(TAG, "setProviderObject:status-> " + status);
        if (!TextUtils.isEmpty(status)) {
            mConsultProvider?.setStatus(status?.let { Constants.PatientStatus.valueOf(it) })
        }
        mConsultProvider?.setUrgent(getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT,
            false))
        if (getIntent().hasExtra("phone")) {
            mConsultProvider?.setPhone(getIntent().getStringExtra("phone"))
        }
        if (mConsultProvider?.getUnread()!! > 0) {
            binding?.imgMessageAlert?.setVisibility(View.VISIBLE)
        } else {
            binding?.imgMessageAlert?.setVisibility(View.GONE)
        }
        strScreenCensus = getIntent().getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE).toString()
    }

    /**
     * Getting patient details via "getPatienDetails" API call based on the UID
     *
     * @param uid
     */
    private fun getPatientDetails(uid: Long) {
        // Showing the progres bar
        showProgressBar(PBMessageHelper().getMessage(this,
            Constants.API.getPatientDetails.toString()))
        viewModel?.getPatienDetails(uid)?.observe(this) { response ->

            val patID: Long? = mConsultProvider?.getPatientsId()

            if (response != null && response.getStatus()) {
                val gson = Gson()
                val commonRes = gson.toJson(response)
                try {
                    val jsonObject = JSONObject(commonRes)

                    patientDetails = response
                    populateConsultDetails()
                    if (patID != null) {
                        getTeamMemberDetails(patID, "Team " + patientDetails!!.patient?.getTeamName())
                    }
                    //                    }
                } catch (e: JSONException) {

                }
            } else if (!TextUtils.isEmpty(response?.errorMsg) && response?.errorMsg!= null) {
                dismissProgressBar()
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    java.lang.String.valueOf(response?.errorMsg),
                    Constants.API.getPatientDetails)
                CustomSnackBar.make(binding?.containerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()

            } else {
                dismissProgressBar()
                val strErrMsg: String = getString(R.string.api_error)
                CustomSnackBar.make(binding?.containerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    strErrMsg,
                    CustomSnackBar.TOP,
                    3000,
                    1)?.show()

            }
        }
    }

    /**
     * Populate the consult patient details
     */
    private fun populateConsultDetails() {
        var heartRateValue = -1.0
        var lowBPValue = -1.0
        var highBPValue = -1.0
        var spo2Value = -1.0
        var respRateValue = -1.0
        var fiO2Value = -1.0
        var tempValue = -1.0


        var oxySupp = false
        if (patientDetails?.patient != null) {

            oxySupp = patientDetails?.patient!!.isOxygenSupplement()
            heartRateValue = patientDetails?.patient!!.getHeartRate()
            highBPValue = patientDetails?.patient!!.getArterialBloodPressureSystolic()
            lowBPValue = patientDetails?.patient!!.getArterialBloodPressureDiastolic()
            spo2Value = patientDetails?.patient!!.getRespiratoryRate()
            fiO2Value = patientDetails?.patient!!.getFio2()
            tempValue = patientDetails?.patient!!.getTemperature()
        }
        val provider = ConsultProvider()
        // Heart rate
        if (!TextUtils.isEmpty(heartRateValue.toString())) {
            provider.setHeartRate(heartRateValue)
        }
        // HighBPValue
        if (!TextUtils.isEmpty(highBPValue.toString())) {
            provider.setArterialBloodPressureSystolic(highBPValue)
        }
        // LowBPValue
        if (!TextUtils.isEmpty(lowBPValue.toString())) {
            provider.setArterialBloodPressureDiastolic(lowBPValue)
        }
        // Spo2Value
        if (!TextUtils.isEmpty(spo2Value.toString())) {
            provider.setSpO2(spo2Value)
        }
        // FiO2Value
        if (!TextUtils.isEmpty(fiO2Value.toString())) {
            provider.setFiO2(fiO2Value)
        }
        //RespRateValue
        if (!TextUtils.isEmpty(respRateValue.toString())) {
            provider.setRespiratoryRate(respRateValue)
        }
        //TempValue
        if (!TextUtils.isEmpty(tempValue.toString())) {
            provider.setTemperature(tempValue)
        }
        // O2 supplement
        provider.setOxygenSupplement(oxySupp)
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

        // Accept textview is changed based on thr consult provider status
        val status: Constants.PatientStatus? = mConsultProvider?.getStatus()

        binding?.txtAccept?.setText(getString(R.string.accept))

        if (status === Constants.PatientStatus.Handoff) {
            binding?.txtAccept?.setText(getString(R.string.handoff_accept))

        }


        // Score
        if (patientDetails?.patient?.getScore() != null) {
            mConsultProvider?.setScore(Constants.AcuityLevel.valueOf(patientDetails!!.patient
                ?.getScore()!!))
        }
        stub?.let { UtilityMethods().displayVitals(this, it, provider) }
        statusStub?.let {
            UtilityMethods().displayPatientStatusComponent(this,
                it,
                mConsultProvider?.getUrgent(),
                status === Constants.PatientStatus.Pending,
                patientDetails?.patient?.getScore()?.let { it1 -> Constants.AcuityLevel.valueOf(it1) })
        }

        // Contact team text is changed based on the role
        val strRpUserType: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        strAcuityScore = patientDetails?.patient?.getScore()
        if (status === Constants.PatientStatus.Pending || status === Constants.PatientStatus.Invited || status === Constants.PatientStatus.Handoff) {
            binding?.txtContactTeam?.visibility = View.GONE
            if (strRpUserType.equals("MD/DO", ignoreCase = true)) {
                binding?.txtAccept?.setVisibility(View.VISIBLE)
            }
        } else {
            binding?.txtContactTeam?.setVisibility(View.VISIBLE)
            binding?.txtAccept?.setVisibility(View.GONE)
        }
        // Record number
        if (!TextUtils.isEmpty(patientDetails?.patient?.getRecordNumber())) {
            binding?.txtMRNNumber?.setText(Html.fromHtml("MRN&nbsp;" + patientDetails?.patient
                ?.getRecordNumber()))
        } else {
            binding?.txtMRNNumber?.setText("MRN ")
        }
        // Patient name
        binding?.txtPatientName?.setText(patientDetails?.patient?.getFname()
            .toString() + " " + patientDetails?.patient?.getLname())

        // DOB
        val dot = " <b>\u00b7</b> "
        if (!TextUtils.isEmpty(patientDetails?.patient?.getDob())) {
            strDob = dot + dob
        }
        // Gender
        if (!TextUtils.isEmpty(patientDetails?.patient?.getGender())) {
            strGender = patientDetails?.patient?.getGender()
            if (strGender.equals("Male", ignoreCase = true)) {
                strGender = dot + "M"
            } else if (strGender.equals("Female", ignoreCase = true)) {
                strGender = dot + "F"
            }
        }
        // Phone number
        strPhone = if (patientDetails?.patient?.getPhone() != null && !TextUtils.isEmpty(
                patientDetails?.patient?.getPhone()) &&
            !patientDetails?.patient?.getPhone().equals("null",ignoreCase = true)
        ) {
            dot + patientDetails!!.patient?.getPhone()
        } else {
            ""
        }

        //Age
        binding?.txtAge?.setText(Html.fromHtml(age + strGender + strDob + strPhone))
        strWard = if (!TextUtils.isEmpty(patientDetails?.patient?.getWardName()?.trim()) &&
            !patientDetails?.patient?.getWardName().equals("null",ignoreCase = true)
        ) {
            dot + patientDetails?.patient?.getWardName()
        } else {
            ""
        }
        // Hospital
        if (!TextUtils.isEmpty(patientDetails?.patient?.getHospital())) {
            binding?.txtLocation?.text = Html.fromHtml(patientDetails?.patient?.getHospital()
                .toString() + strWard)
        }

        // Getting the note for complain details
        if (!TextUtils.isEmpty(patientDetails?.patient?.getNote())) {
            val strComplaint: String? = patientDetails?.patient?.getNote()
            var stringComplaint = strComplaint
            if (strComplaint?.contains(":") == true) {
                stringComplaint = strComplaint.substring(strComplaint.indexOf(":") + 1)
            }
            binding?.txtComplaintDetail?.setText(stringComplaint?.trim { it <= ' ' })
        }
        // Sync time
        if (patientDetails?.patient?.getSyncTime() != null && !patientDetails?.patient
                ?.getSyncTime().equals("0")
        ) {
            binding?.txtTimeZone?.setText(patientDetails!!.patient
                ?.getSyncTime()?.toLong()?.let { Utils().timestampToDate(it) })
        } else {
            binding?.txtTimeZone?.setText(" - ")
        }

        // Show the more option based on the role
        val role: String? = PrefUtility().getRole(this)


    }

    /**
     * Accept text click listener - Calling the "acceptInvite" API
     *
     * @param consultProvider
     */
    fun acceptClick(consultProvider: ConsultProvider?) {
        providerApiFlag = true
        // Show progress bar
        showProgressBar()
        // If the consultProvider status is "Invited"
        if (consultProvider?.getStatus() === Constants.PatientStatus.Pending || consultProvider?.getStatus() === Constants.PatientStatus.Invited) {
            val providerId: Long? = PrefUtility().getProviderId(this)
            val token: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
            if (providerId != null) {
                consultProvider.getPatientsId()?.let {
                    if (token != null) {
                        HomeViewModel().acceptInvite(providerId, token, it)
                            ?.observe(this) { listResponse ->

                                providerApiFlag = false
                                if (listResponse?.status != null && listResponse.status!!) {
                                    mConsultProvider?.setStatus(Constants.PatientStatus.Active)

                                    getPatientDetails(uid)
                                    binding?.txtAccept?.setVisibility(View.GONE)
                                    binding?.txtContactTeam?.setVisibility(View.VISIBLE)
                                    val role: String? = PrefUtility().getRole(this)
                                    val strRpUserType: String? =
                                        PrefUtility().getStringInPref(this,
                                            Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                                            "")

                                } else {
                                    // Dissmis progress bar
                                    dismissProgressBar()

                                    var errMsg = ""
                                    if (listResponse != null && !TextUtils.isEmpty(listResponse.errorMessage)) {
                                        errMsg = listResponse.errorMessage.toString()
                                    }
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
                }
            }
        } else {
            // If the consultProvider status is "Handoff" or HandoffPending - Calling the "acceptRemoteHandoff" API
            if (consultProvider?.getStatus() === Constants.PatientStatus.Handoff || consultProvider?.getStatus()
                    ?.equals(Constants.PatientStatus.HandoffPending) == true
            ) {
                val providerID: Long =
                    PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0)
                val handOffAcceptRequest = HandOffAcceptRequest()
                handOffAcceptRequest.setPatientId(consultProvider.getPatientsId())
                handOffAcceptRequest.setProviderId(providerID)

                HomeViewModel().acceptRemoteHandoff(handOffAcceptRequest)
                    ?.observe(this) { listResponse ->
                        providerApiFlag = false

                        if (listResponse?.status != null && listResponse.status!!) {
                            mConsultProvider?.setStatus(Constants.PatientStatus.Active)

                            getPatientDetails(uid)
                            binding?.txtAccept?.setVisibility(View.GONE)
                            binding?.txtContactTeam?.setVisibility(View.VISIBLE)
                            val role: String? = PrefUtility().getRole(this)
                            val strRpUserType: String? =
                                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE,"")

                        }
                        else {
                            // Dismiss progress bar
                            dismissProgressBar()
                              var errMsg = ""
                            if (listResponse != null && !TextUtils.isEmpty(listResponse.errorMessage)) {
                                errMsg = listResponse.errorMessage.toString()
                            }
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
        }
    }

    fun onAcceptSuccess() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View = LayoutInflater.from(getApplicationContext())
            .inflate(R.layout.custom_alert_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.setText(getString(R.string.success))
        alertTitle.visibility = View.GONE
        alertMsg.setText(getString(R.string.invitation_accepted))
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        buttonOk.setOnClickListener { v -> //Handling the multi click
            handleMultipleClick(v)
            //Getting patient details via "getPatienDetails" API call based on the UID
            getPatientDetails(uid)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    /**
     * Getting the DOB in a formatted string as "MM-dd-yyyy"
     *
     * @return
     */
    private val dob: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val timeInMillis: Long =
                java.lang.Long.valueOf(patientDetails?.patient?.getDob())
            return SimpleDateFormat("MM-dd-yyyy").format(Date(timeInMillis))
        }

    /**
     * Getting the Age in a formatted string
     *
     * @return
     */
    private val age: String
         get() {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            calendar.timeInMillis = patientDetails?.patient?.getDob()?.toLong()!!
            val agee = year - calendar[Calendar.YEAR]
            return agee.toString()
        }


    /**
     * Direct to Remote Hand off activity
     */
    private fun doRemoteSideHandOff() {
        val intent = Intent(this, RemoteHandOffActivity::class.java)
        intent.putExtra("patient_id", mConsultProvider?.getPatientsId())
        intent.putExtra("patient_name", patientDetails?.patient?.name)
        intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, strScreenCensus)
        startActivityForResult(intent, 505)
    }

    /**
     * Show dialog to complete the consultation
     */
    private fun doCompleteConsultation() {

        val dialog = Dialog(this, R.style.AppTheme_NoActionBarDark)
        dialog.setContentView(R.layout.mark_complete_dialog)
        dialog.show()
        val toolbarComplete = dialog.findViewById<View>(R.id.toolbarComplete) as Toolbar
        toolbarComplete.setTitle(getString(R.string.complete_consultation))
        toolbarComplete.setNavigationIcon(R.drawable.ic_back)
        toolbarComplete.setNavigationOnClickListener { dialog.dismiss() }
        val idContainerLayout = dialog.findViewById<LinearLayout>(R.id.idContainerLayout)
        val btnComplete = dialog.findViewById<Button>(R.id.btnComplete)
        val edtAssessment = dialog.findViewById<EditText>(R.id.discharge_assessment)
        val edtPlan = dialog.findViewById<EditText>(R.id.discharge_plan)
        val prefixAssessment: String = getString(R.string.assessment)
        val prefixPlan: String = getString(R.string.plan)
        edtAssessment.setText(Html.fromHtml(prefixAssessment))
        edtPlan.setText(Html.fromHtml(prefixPlan))
        Selection.setSelection(edtAssessment.text, edtAssessment.text.length)
        Selection.setSelection(edtPlan.text, edtPlan.text.length)
        edtAssessment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith(Html.fromHtml(prefixAssessment).toString())) {
                    edtAssessment.setText(Html.fromHtml(prefixAssessment))
                    Selection.setSelection(edtAssessment.text, edtAssessment.text.length)
                }
            }
        })
        edtPlan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith(Html.fromHtml(prefixPlan).toString())) {
                    edtPlan.setText(Html.fromHtml(prefixPlan))
                    Selection.setSelection(edtPlan.text, edtPlan.text.length)
                }
            }
        })

        // On Complete click - "dischargePatient" API call is triggered
        btnComplete.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            val assessment = edtAssessment.text.toString()
            val plan = edtPlan.text.toString()
            if (TextUtils.isEmpty(assessment) || assessment.equals("Assessment:",
                    ignoreCase = true)
            ) {
                if (TextUtils.isEmpty(plan) || plan.equals("Plan:", ignoreCase = true)) {

                    CustomSnackBar.make(v, this, CustomSnackBar.WARNING,
                        getString(R.string.summary_note_is_mandatory),
                        CustomSnackBar.TOP, 3000, 0)?.show()
                    return@OnClickListener
                }
            }
            if (TextUtils.isEmpty(plan) || plan.equals("Plan:", ignoreCase = true)) {
                if (TextUtils.isEmpty(assessment) || assessment.equals("Assessment:",
                        ignoreCase = true)) {

                    CustomSnackBar.make(v, this, CustomSnackBar.WARNING,
                        getString(R.string.summary_note_is_mandatory),
                        CustomSnackBar.TOP, 3000, 0)?.show()
                    return@OnClickListener
                }
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

            val notes = "Assessment: $assessment\n  \nPlan: $plan"
            val providerID: Long?= PrefUtility().getProviderId(this)
            val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
            // DischargePatient API call
            if (providerID != null) {
                if (token != null) {
                    mConsultProvider?.getId()?.let {
                        viewModel?.dischargePatient(providerID, token, it, notes)
                            ?.observe(this) { commonResponse ->
                                dismissProgressBar()
                                if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                                    if (mConsultProvider != null) {
                                        val providerName: String? =
                                            PrefUtility().getStringInPref(this,
                                                Constants.SharedPrefConstants.NAME, "")
                                        val role: String? =
                                            PrefUtility().getStringInPref(this,
                                                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                                                "")
                                        mFirebaseDatabaseReference!!.child("providers")
                                            .child(providerID.toString()).child("active")
                                            .child(java.lang.String.valueOf(mConsultProvider!!.getId()))
                                            .child("completed_by").setValue("$providerName, $role")
                                        mConsultProvider!!.setStatus(Constants.PatientStatus.Completed)
                                        if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(
                                                Constants.IntentKeyConstants.SCREEN_CENSUS,
                                                ignoreCase = true)
                                        ) {
                                            setResult(Activity.RESULT_OK)
                                            finish()
                                        } else {
                                            startActivity(Intent(this,
                                                HomeActivity::class.java)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .putExtra(Constants.IntentKeyConstants.TARGET_PAGE,
                                                    "completed"))
                                            finish()
                                        }


                                    }
                                } else {
                                    btnComplete.isEnabled = true
                                    val errMsg: String? =
                                        ErrorMessages().getErrorMessage(this,
                                            commonResponse?.getErrorMessage(),
                                            Constants.API.register)

                                    CustomSnackBar.make(idContainerLayout, this,
                                        CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0)?.show()
                                }
                            }
                    }
                }
            }
        })
        dialog.show()
    }


    /**
     * BP more option click listener popup
     *
     * @param context
     */
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
        rlDischargePatient.visibility = View.GONE
        imgCancel.setOnClickListener { dialog.dismiss() }
        // Resetting the acuity
        rlResetAcuity.setOnClickListener { v -> // Handling the multi click event
            handleMultipleClick(v)
            // Displaying reset acuity dialog
            resetAcuityDialog(this)
            dialog.dismiss()
        }

        // Directed Transfer patient activity
        rlTransferPatient.setOnClickListener { v -> // Handling the multi click event
            handleMultipleClick(v)
            val intent = Intent(this, TransferPatientActivity::class.java)
            intent.putExtra("patientId", mConsultProvider?.getPatientsId())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, strScreenCensus)
            startActivityForResult(intent, 505)
            //                finish();
            dialog.dismiss()
        }
        rleNotes.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, ActivityEnotes::class.java)
            intent.putExtra("patient_id", mConsultProvider?.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            intent.putExtra("patient_status", mConsultProvider?.status.toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, strScreenCensus)
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
        dialog.show()
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
        imgCancel.setOnClickListener { dialog.dismiss() }
        if (mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.HandoffPending) == true) {

            rlHandOffPatient.visibility = View.GONE
        } else {
            rlHandOffPatient.visibility = View.VISIBLE
        }
        val role: String? = PrefUtility().getRole(this)
        val strRpUserType: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.R_PROVIDER_TYPE,
            "")
        if (role.equals(Constants.ProviderRole.RD.toString(),
                ignoreCase = true) && strRpUserType != "MD/DO" ||
            strRpUserType == "MD/DO" && (mConsultProvider?.getStatus()
                ?.equals(Constants.PatientStatus.Invited) == true || mConsultProvider?.getStatus()
                ?.equals(Constants.PatientStatus.Handoff) == true)) {
            rlHandOffPatient.visibility = View.GONE
            rlCompletedConsultation.visibility = View.GONE
            rlCreateProgress.visibility = View.GONE
        }

        //Remote side handoff triggered
        rlHandOffPatient.setOnClickListener {
            doRemoteSideHandOff()
            dialog.dismiss()
        }
        // Complete consultation is triggered
        rlCompletedConsultation.setOnClickListener { v -> //                doCompleteConsultation();
            handleMultipleClick(v)
            val intent = Intent(this, RemoteCompleteActivity::class.java)
            intent.putExtra("patient_id", mConsultProvider?.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, strScreenCensus)
            startActivity(intent)
            dialog.dismiss()
        }
        rleNotes.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, ActivityEnotes::class.java)
            intent.putExtra("patient_id", mConsultProvider?.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            intent.putExtra("patient_status", mConsultProvider?.getStatus().toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, strScreenCensus)
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
            val intent = Intent(this, AddProgressEnoteActivity::class.java)
            intent.putExtra("patient_id", mConsultProvider?.getPatientsId())
            intent.putExtra("patient_name", patientDetails?.patient?.name)
            intent.putExtra("patient_status", mConsultProvider?.getStatus().toString())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, "chart")
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * Displaying reset acuity dialog
     *
     * @param context
     */
    fun resetAcuityDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.reset_acuity_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlLow = dialog.findViewById<View>(R.id.rlLow) as RelativeLayout
        val rlMedium = dialog.findViewById<View>(R.id.rlMedium) as RelativeLayout
        val rlHigh = dialog.findViewById<View>(R.id.rlHigh) as RelativeLayout
        val imgLowAcuity = dialog.findViewById<View>(R.id.imgLowAcuity) as ImageView
        val imgMediumAcuity = dialog.findViewById<View>(R.id.imgMediumAcuity) as ImageView
        val imgHighAcuity = dialog.findViewById<View>(R.id.imgHighAcuity) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val btnResetAcuity = dialog.findViewById<View>(R.id.btnResetAcuity) as Button
        imgCancel.setOnClickListener { dialog.dismiss() }
        if (strAcuityScore.equals(java.lang.String.valueOf(Constants.AcuityLevel.High),
                ignoreCase = true)
        ) {
            imgLowAcuity.visibility = View.GONE
            imgMediumAcuity.visibility = View.GONE
            imgHighAcuity.visibility = View.VISIBLE
            btnResetAcuity.isEnabled = false
        } else if (strAcuityScore.equals(java.lang.String.valueOf(Constants.AcuityLevel.Medium),
                ignoreCase = true)
        ) {
            imgLowAcuity.visibility = View.GONE
            imgMediumAcuity.visibility = View.VISIBLE
            imgHighAcuity.visibility = View.GONE
            btnResetAcuity.isEnabled = false
        } else if (strAcuityScore.equals(java.lang.String.valueOf(Constants.AcuityLevel.Low),
                ignoreCase = true)
        ) {
            imgLowAcuity.visibility = View.VISIBLE
            imgMediumAcuity.visibility = View.GONE
            imgHighAcuity.visibility = View.GONE
            btnResetAcuity.isEnabled = false
        }

        // High acuity click listener
        rlHigh.setOnClickListener {
            if (strAcuityScore.equals(java.lang.String.valueOf(Constants.AcuityLevel.High),
                    ignoreCase = true)
            ) {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.VISIBLE
                btnResetAcuity.isEnabled = false
                btnResetAcuity.background = getResources().getDrawable(R.drawable.btn_bg_grey)
                btnResetAcuity.setTextColor(getResources().getColor(R.color.login_hint_color))
            } else {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.VISIBLE
                btnResetAcuity.isEnabled = true
                btnResetAcuity.background = getResources().getDrawable(R.drawable.login_edittext_bg)
                btnResetAcuity.setTextColor(getResources().getColor(R.color.white))
                acuityLevel = Constants.AcuityLevel.High
            }
        }
        // Medium acuity click listener
        rlMedium.setOnClickListener {
            if (strAcuityScore.equals(java.lang.String.valueOf(Constants.AcuityLevel.Medium),
                    ignoreCase = true)
            ) {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.VISIBLE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = false
                btnResetAcuity.background = getResources().getDrawable(R.drawable.btn_bg_grey)
                btnResetAcuity.setTextColor(getResources().getColor(R.color.login_hint_color))
            } else {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.VISIBLE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = true
                btnResetAcuity.background = getResources().getDrawable(R.drawable.login_edittext_bg)
                btnResetAcuity.setTextColor(getResources().getColor(R.color.white))
                acuityLevel = Constants.AcuityLevel.Medium
            }
        }
        // Low acuity click listener
        rlLow.setOnClickListener {
            if (strAcuityScore.equals(java.lang.String.valueOf(Constants.AcuityLevel.Low),
                    ignoreCase = true)
            ) {
                imgLowAcuity.visibility = View.VISIBLE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = false
                btnResetAcuity.background = getResources().getDrawable(R.drawable.btn_bg_grey)
                btnResetAcuity.setTextColor(getResources().getColor(R.color.login_hint_color))
            } else {
                imgLowAcuity.visibility = View.VISIBLE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = true
                btnResetAcuity.background = getResources().getDrawable(R.drawable.login_edittext_bg)
                btnResetAcuity.setTextColor(getResources().getColor(R.color.white))
                acuityLevel = Constants.AcuityLevel.Low
            }
        }

        // Reset button acuity click
        btnResetAcuity.setOnClickListener {
            val providerId: Long? = PrefUtility().getProviderId(this)
            val token: String? = PrefUtility().getToken(this)
            val patientId: Long =
                if (mConsultProvider?.getId() != null) mConsultProvider!!.getId()!! else 0

            val builder = AlertDialog.Builder(this,
                R.style.CustomAlertDialog)
            val viewGroup: ViewGroup = findViewById(android.R.id.content)
            val dialogView: View = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.alert_custom_dialog, viewGroup, false)
            val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
            val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
            alertTitle.setText(getString(R.string.success))
            alertTitle.visibility = View.GONE
            alertMsg.setText(getString(R.string.reset_acuity_score_confirm))
            val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
            val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
            builder.setView(dialogView)
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            buttonYes.setOnClickListener { v ->
                handleMultipleClick(v)
                showProgressBar(getString(R.string.acuity_score_reset_pb_mgs))
                // ResetAcuityValue API call is triggered
                if (providerId != null) {
                    if (token != null) {
                        // ResetAcuityValue API call is triggered
                        viewModel!!.resetAcuityValue(providerId,
                            token,
                            patientId,
                            java.lang.String.valueOf(acuityLevel))!!
                            .observe(this, { commonResponse ->
                                dismissProgressBar()
                                if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()!!) {

                                    mConsultProvider!!.setResetAcuityFlag(true)
                                    if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(
                                            Constants.IntentKeyConstants.SCREEN_CENSUS,
                                            ignoreCase = true)
                                    ) {
                                        setResult(RESULT_OK)
                                        finish()
                                    } else {
                                        // Directing to home activity based on SCREEN_CENSUS
                                        val intent = Intent(this,
                                            HomeActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        startActivity(intent)
                                        finish()
                                        alertDialog.dismiss()
                                        dialog.dismiss()


                                    }
                                } else if (commonResponse != null && !TextUtils.isEmpty(
                                        commonResponse.getErrorMessage()) && commonResponse.getErrorMessage() != null
                                ) {
                                    val errMsg: String = commonResponse.getErrorMessage()!!
                                    CustomSnackBar.make(binding!!.root,
                                        this,
                                        CustomSnackBar.WARNING,
                                        errMsg,
                                        CustomSnackBar.TOP,
                                        3000,
                                        0)!!
                                        .show()
                                } else {
                                    CustomSnackBar.make(binding!!.root,
                                        this,
                                        CustomSnackBar.WARNING,
                                        getString(R.string.api_error),
                                        CustomSnackBar.TOP,
                                        3000,
                                        0)!!
                                        .show()
                                }
                            })

                    }

                    // No click listener
                    buttonNo.setOnClickListener {
                        alertDialog.dismiss()
                        dialog.dismiss()
                    }
                    alertDialog.show()
                }
                dialog.show()
            }
        }
    }

            /**
             * In-App alert for acuity change only if,
             * The user enabled the acuity alerts in alert/notification settings
             * Else the user wont get notified even if the acuity level is modified
             *
             * @param message
             */
            fun triggerInAppAlertForAcuityChange(message: String) {

                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                val pendingIntent = PendingIntent.getActivity(this, 1,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val notificationHelper = NotificationHelper(getApplicationContext(), null)
                notificationHelper.sendNotification(pendingIntent,
                    "Acuity modified",
                    message,
                    Constants.NotificationIds.NOTIFICATION_ID)
            }

            override fun checkSelfPermissionsMediaCheck(): Boolean {
                return checkSelfPermissionGrantedCheck(Manifest.permission.RECORD_AUDIO,
                    ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO) &&
                        checkSelfPermissionGrantedCheck(Manifest.permission.CAMERA,
                            ConstantApp().PERMISSION_REQ_ID_CAMERA)
            }

            override fun checkSelfPermissionGrantedCheck(permission: String, requestCode: Int): Boolean {

                if (ContextCompat.checkSelfPermission(this,
                        permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(permission),
                        requestCode)
                    return false
                }
                return true
            }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray, ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO -> {
                checkSelfPermissionGrantedCheck(Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA)
            }
            ConstantApp().PERMISSION_REQ_ID_CAMERA -> {
            }
            else -> {
                Toast.makeText(this, "Please give permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

        }

