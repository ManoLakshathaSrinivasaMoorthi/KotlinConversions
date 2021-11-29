package com.example.kotlinomnicure.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.customview.CustomDialog
import com.example.kotlinomnicure.databinding.ActivityConsultChartBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.viewmodel.PatientDetailViewModel
import com.google.firebase.database.*
import com.google.gson.Gson
import omnicurekotlin.example.com.patientsEndpoints.model.PatientDetail
import omnicurekotlin.example.com.providerEndpoints.model.Members
import java.text.SimpleDateFormat
import java.util.*

class ActivityConsultChart : BaseActivity() {
    // Variables
    private val TAG: String? = ActivityConsultChart::class.java.simpleName
    private var binding: ActivityConsultChartBinding? = null
    var statusStub: RelativeLayout? = null
    var providerApiFlag = false
    private var mUnreadMessageDB: DatabaseReference? = null
    private val membersList: MutableList<Members?> = ArrayList()
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
    private  var strWard:String? = null
    private val role: String? = null
    private var mConsultProvider: ConsultProvider? = null
    private var mConsultProviderKey: String? = null
    private var stub: ViewStub? = null
    private var strMessageReaded: String? = null
    private var customDialog: CustomDialog? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private var strAcuityScore: String? = null
    private var strScreenCensus: String? = ""

    // Unread message listener from firebase - Add Consult provider data from the snapshot
    private var mUnreadMessageListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d(TAG, "ConsultProviderChart$dataSnapshot")
            val consultProviderA = dataSnapshot.getValue(ConsultProvider::class.java)
            Log.d(TAG, "ConsultProviderChart" + Gson().toJson(consultProviderA))
            // If the provider data is null - show error dialog
            if (consultProviderA == null) {
                showErrorDialog()
                return
            }
            // If the data has status value, details are updated
            if (consultProviderA.getStatus() != null && consultProviderA.getStatus()!! == Constants.PatientStatus.Active
                && mConsultProvider?.getStatus()!! != Constants.PatientStatus.Active
            ) {
                println("providerapiflag $providerApiFlag")
                if (providerApiFlag) {
                    return
                }
                mConsultProvider?.setStatus(Constants.PatientStatus.Active)
                //Getting patient details via "getPatienDetails" API call based on the UID
                getPatientDetails(uid)
                binding?.txtAccept?.visibility = View.GONE
                binding?.txtContactTeam?.visibility = View.VISIBLE
                val role: String? = PrefUtility().getRole(ActivityConsultChart())
                val strRpUserType: String? = PrefUtility().getStringInPref(ActivityConsultChart(), Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                    "")

                // Condition to show the menu options to the user
                if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                    if (strRpUserType == "MD/DO" && mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Active) == true
                    ) {
                        binding?.floatFab?.visibility = View.VISIBLE
                    } else if (strRpUserType == "MD/DO" && mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Pending) == true
                    ) {
                        binding?.floatFab?.visibility = View.GONE
                    } else {
                        binding?.floatFab?.visibility = View.GONE
                    }
                }
            }
            if (consultProviderA.getStatus() != null) {
                mConsultProvider?.setStatus(consultProviderA.getStatus())
            }
            if (consultProviderA.getUnread() > 0) {
                Log.d(TAG, "Unread Message Count" + consultProviderA.getUnread())
                //                mFirebaseDatabaseReference.child("providers").child(mProviderUid).child("active").child(mConsultProviderKey).child("unread").setValue(0);
                binding?.imgMessageAlert?.visibility = View.VISIBLE
            } else {
                binding?.imgMessageAlert?.visibility = View.GONE
            }
            // Removing the stubs
            if (mConsultProvider?.getScore() != null && mConsultProvider?.getScore() !== consultProviderA.getScore()) {
                statusStub?.removeAllViews()
                //                statusStub
                consultProviderA.getScore()?.let {
                    mConsultProvider?.getUrgent()?.let { it1 ->
                        UtilityMethods().displayPatientStatusComponent(this@ActivityConsultChart, statusStub, it1,
                            consultProviderA.getStatus() === Constants.PatientStatus.Pending, it)
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "onCancelled: of value event listener $databaseError")
        }
    }


     // Showing error dialog based on the patient status like Handoff, HandoffPending

    fun showErrorDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.custom_alert_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.visibility = View.GONE
        var message = getString(R.string.consultation_request_already_accepted_msg)
        val strRole: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        val strDesignation: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        message = if (mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Handoff) == true
            || mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.HandoffPending) == true
        ) {
            if (strRole == "RD" && !strDesignation.equals("MD/DO", ignoreCase = true)) {
                getString(R.string.patient_handed_off_to_other_grp)
            } else {
                getString(R.string.handoff_request_already_accepted_msg)
            }
        } else {
            getString(R.string.consultation_request_already_accepted_msg)
        }
        alertMsg.text = message
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        // Okay click listener directs the user to home activity or close the activity based on the SCREEN_CENSUS
        buttonOk.setOnClickListener {
            if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(Constants.IntentKeyConstants.SCREEN_CENSUS, ignoreCase = true)) {
                setResult(RESULT_OK)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Databinding and view model initialization
        binding = DataBindingUtil.setContentView(this, R.layout.activity_consult_chart)
        viewModel = ViewModelProvider(this).get(PatientDetailViewModel::class.java)
        // Initiating the views for the activity
        initViews()
        //  Initiating the on click listener for the activity
        initOnClickListener()
    }


     // Initiating the views for the activity

    private fun initViews() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        stub = findViewById(R.id.layout_stub_view)
        statusStub = findViewById(R.id.status_stub)

        // Getting the values from the intent
        uid = intent.getLongExtra("uid", 0)
        Log.d(TAG, "OnCreate UID$uid")
        if (PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "").equals("RD")
        ) {
            binding?.llCallLayout?.visibility = View.INVISIBLE
        } else {
            binding?.llCallLayout?.visibility = View.VISIBLE
        }

        // Setting up the providing object
        setProviderObject()
        val role: String? = PrefUtility().getRole(this)
        val strRpUserType: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")

        val providerID: Long? = PrefUtility().getProviderId(this)


        // Display more option based on the provider role and status of the patient
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            if (strRpUserType == "MD/DO" && (mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Active) == true
                        || mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.HandoffPending) == true)
            ) {
//                    || mConsultProvider.getStatus().equals(Constants.PatientStatus.HandoffPending) || mConsultProvider.getStatus().equals(Constants.PatientStatus.Handoff))) {
                binding?.floatFab?.visibility = View.VISIBLE
            } else if (strRpUserType == "MD/DO" && mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Handoff) == true
                && !TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(Constants.IntentKeyConstants.SCREEN_CENSUS, ignoreCase = true)
            ) {
                binding?.floatFab?.visibility = View.VISIBLE
            } else if (strRpUserType == "MD/DO" && mConsultProvider?.getStatus()?.equals(Constants.PatientStatus.Pending) == true
            ) {
                binding?.floatFab?.visibility = View.GONE
            } else {
                binding?.floatFab?.visibility = View.GONE
            }
        }
        // Getting the patient details via API call using UID as inout
        getPatientDetails(uid)
    }

    //Removing the event listener for unread message listener

    override fun onStop() {
        println("consult chart Nonstop")
        super.onStop()
        mUnreadMessageDB?.removeEventListener(mUnreadMessageListener)
    }


    //Initiating the on click listener

    private fun initOnClickListener() {
        binding?.imgBack?.setOnClickListener { finish() }
        // Image up click listener
        binding?.imgDetailUp?.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding?.imgDetailUp?.visibility = View.GONE
            binding?.imgDetailDown?.visibility = View.VISIBLE
            binding?.llComplaints?.visibility = View.GONE
        }

        // Image down click listener
        binding?.imgDetailDown?.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding?.imgDetailDown?.visibility = View.GONE
            binding?.imgDetailUp?.visibility = View.VISIBLE
            binding?.llComplaints?.visibility = View.VISIBLE
        }

        // Image vital up click listener
        binding?.imgVitalUp?.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding?.imgVitalUp?.visibility = View.GONE
            binding?.imgVitalDown?.visibility = View.VISIBLE
            binding?.llTimeZone?.visibility = View.GONE
            binding?.llVital?.visibility = View.GONE
        }
        // Image vital down click listener
        binding?.imgVitalDown?.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding?.imgVitalDown?.visibility = View.GONE
            binding?.imgVitalUp?.visibility = View.VISIBLE
            binding?.llTimeZone?.visibility = View.VISIBLE
            binding?.llVital?.visibility = View.VISIBLE
        }
        // Handling the message click
        binding?.llMessage?.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)

            // Directing the user to chat activity with needed data
            val intentConsultChart = Intent(this, ChatActivity::class.java)
            intentConsultChart.putExtra("uid", mConsultProvider?.getId())
            Log.d(TAG, "strUid : " + mConsultProvider?.getId())
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
            intentConsultChart.putExtra("status", mConsultProvider?.getStatus())
            if (!TextUtils.isEmpty(strConsultTeamName)) {
                intentConsultChart.putExtra("teamNameConsult", strConsultTeamName)
            }
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.IS_PATIENT_URGENT, mConsultProvider?.getUrgent()
            )
            //Setting the consult provider status if not null
            if (mConsultProvider?.getStatus() != null) {
                intentConsultChart.putExtra("status", mConsultProvider?.getStatus().toString())
                if (mConsultProvider?.getStatus() === Constants.PatientStatus.Invited ||
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Handoff
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    // clearNotifications(mConsultProvider?.getId().intValue())
                } else if (mConsultProvider?.getStatus() === Constants.PatientStatus.Discharged ||
                    mConsultProvider?.getStatus() === Constants.PatientStatus.Exit
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }
            startActivityForResult(intentConsultChart, 1)
        }

        // Handling contact team click
        binding?.txtContactTeam?.setOnClickListener { v ->
            // Handling the multi click
            handleMultipleClick(v)
            if (membersList.size > 0) {
                showTeamMembersDialog(this, membersList, mConsultProvider!!)
            } else {
//                    UtilityMethods.showErrorSnackBar(binding.containerLayout, errorTeams, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(binding?.containerLayout, this, CustomSnackBar.WARNING, errorTeams!!,
                        CustomSnackBar.TOP, 3000, 0)?.show()
            }
        }
        // Handling accept team click
        binding?.txtAccept?.setOnClickListener { // Handling the multi click
            handleMultipleClick(binding!!.txtAccept)
            // On accept click listener with consult provider data
            acceptClick(mConsultProvider)
        }

        // Handling call click for SoS
        binding?.llCallLayout?.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            // Connecting to the SoS call
            connectSOSCall()
        }

        // Handling more option click listener
        binding?.floatFab?.setOnClickListener { v ->
            handleMultipleClick(v)
            val role: String? = PrefUtility().getRole(this)
            val strRpUserType: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
            if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                if (strRpUserType == "MD/DO") {
                    // More option UI and its options based on the role (MD/DO)
                    MoreOptionRPDialog(this)
                }
            } else {
                // More option UI and its options based on the role(Others)
                MoreOptionBPDialog(this)
            }
        }
    }



    //Adding the un read message listener

    override fun onStart() {
        println("consult chart onstart")
        super.onStart()
        try {
            unReadMessageListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Method to add un read message listener
     */
    private fun unReadMessageListener() {
        Log.d(TAG, "unReadMessageListener: ")
        val mProviderUid: String? = PrefUtility().getFireBaseUid(this) //mFirebaseUser.getUid();
        mUnreadMessageDB = mProviderUid?.let {
                mFirebaseDatabaseReference?.child("providers")?.child(it)?.child("active")?.child(
                        mConsultProviderKey!!)
            }
        mUnreadMessageDB!!.addValueEventListener(mUnreadMessageListener)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                strMessageReaded = data!!.getStringExtra("messageValue")
                if (!TextUtils.isEmpty(strMessageReaded)) {
                    binding?.imgMessageAlert?.visibility = View.GONE
                } else {
                    binding?.imgMessageAlert?.visibility = View.VISIBLE
                }
            }
        }
        // Finish the code, when the code is - 505
        if (requestCode == 505) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }


     // Connecting to SoS call via startSOS API call and directing to call activity

    private fun connectSOSCall() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.root, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        val title = getString(R.string.sos_alert_confirmation_msg) + " " + mConsultProvider?.getName()
        val message = getString(R.string.alert_msg)
        customDialog = UtilityMethods().showDialog(
            this, title, message, false, R.string.send, {
                    showProgressBar()
                    val providerID: Long? = PrefUtility().getProviderId(this)
                    val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")

                   providerID?.let { it1 ->
                       token?.let { it2 ->
                           viewModel?.startSOS(it1, it2, java.lang.Long.valueOf(mConsultProvider?.getPatientId()))
                               ?.observe(this) { commonResponse ->
                                   dismissProgressBar()
                                   if (commonResponse?.getStatus() != null && commonResponse.getStatus()!!) {
                                       // Directing to call activity
                                       val callScreen = Intent(this, CallActivity::class.java)
                                       callScreen.putExtra("providerName", mConsultProvider?.getBdProviderName())
                                       callScreen.putExtra("providerHospitalName", mConsultProvider?.getHospital())
                                       callScreen.putExtra(ConstantApp().ACTION_KEY_CHANNEL_NAME, java.lang.String.valueOf(mConsultProvider?.getPatientId()))
                                       callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_KEY, "")
                                       callScreen.putExtra(ConstantApp().ACTION_KEY_ENCRYPTION_MODE, resources.getStringArray(R.array.encryption_mode_values)[0])
                                       callScreen.putExtra("patientId", mConsultProvider?.getPatientId())
                                       callScreen.putExtra("sos", true)
                                       callScreen.putExtra("callType", "outgoing")
                                       val gson = Gson()
                                       callScreen.putExtra("providerList", gson.toJson(commonResponse.getProviderList()))
                                       startActivity(callScreen)
                                   } else {
                                       val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse.getErrorMessage(), Constants.API.startCall)
                                       //                        Toast.makeText(ActivityConsultChart.this, errMsg, Toast.LENGTH_SHORT).show();
                                       //                        UtilityMethods.showErrorSnackBar(containerParent, errMsg, Snackbar.LENGTH_LONG);
                                       errMsg?.let { it1 ->
                                           CustomSnackBar.make(binding?.root, this, CustomSnackBar.WARNING, it1, CustomSnackBar.TOP, 3000, 0)
                                       }?.show()
                                   }
                               }
                       }
                   }
                    customDialog?.dismiss()
                },
            R.string.cancel,
            { customDialog?.cancel() },
            Color.RED,
            true
        )
    }

    /**
     * Handling the multiple click based on the view
     * @param view
     */
    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    /**
     * Getting the team member details via "getMemberList" API call
     * @param patientId
     * @param teamName
     */
    private fun getTeamMemberDetails(patientId: Long, teamName: String) {
        membersList?.clear()
        //        Long patientID = Long.valueOf(mConsultProvider.getPatientId());
        println("GetTeamMemberDetails patientID : $patientId")
      /*  viewModel.getMemberList(patientId, teamName).observe(this) { response ->
            dismissProgressBar()
            if (response != null && response.getStatus() != null && response.getStatus()) {
                println("GetTeamMemberDetails response : $response")
                if (response.getTeamDetails().getMembers() != null) {
                    membersList?.clear()
                    membersList?.addAll(response.getTeamDetails().getMembers())
                    strConsultTeamName = response.getTeamDetails().getName()
                }
            } else if (!TextUtils.isEmpty(response.getErrorMessage()) && response.getErrorMessage() != null) {
                errorTeams = response.getErrorMessage()
                Log.d(TAG, "GetTeamMemberDetails errorTeams :  $errorTeams")
            } else {
                errorTeams = getString(R.string.api_error)
            }
        }*/
    }

    /**
     * Clearing the notification based in the notification ID
     * @param notificationId
     */
    private fun clearNotifications(notificationId: Int) {
   //     NotificationHelper(this).clearNotification(notificationId)
    }

    /**
     * Setting the provider data with received input intent values
     */
    private fun setProviderObject() {
        mConsultProvider = ConsultProvider()
        mConsultProviderKey = intent.getStringExtra("consultProviderId")
        Log.d(TAG, "mConsultProviderKey$mConsultProviderKey")
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
        Log.e(TAG, "setProviderObject:status-> $status")
        if (!TextUtils.isEmpty(status)) {
            mConsultProvider?.setStatus(Constants.PatientStatus.valueOf(status!!))
        }
        mConsultProvider?.setUrgent(intent.getBooleanExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT, false))
        if (intent.hasExtra("phone")) {
            mConsultProvider?.setPhone(intent.getStringExtra("phone"))
        }
        if (mConsultProvider?.getUnread()!! > 0) {
            binding?.imgMessageAlert?.visibility = View.VISIBLE
        } else {
            binding?.imgMessageAlert?.visibility = View.GONE
        }
        strScreenCensus = intent.getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)
    }

    /**
     * Getting patient details via "getPatienDetails" API call based on the UID
     * @param uid
     */
    private fun getPatientDetails(uid: Long) {
        // Showing the progres bar
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.getPatientDetails.toString()))
   /*     viewModel.getPatienDetails(uid).observe(this) { response ->
            val patID = java.lang.Long.valueOf(mConsultProvider?.getPatientId())
            Log.d(TAG, "getPatientDetails Response : " + Gson().toJson(response))
            if (response != null && response.getStatus() != null && response.getStatus()) {
                val gson = Gson()
                patientDetails = gson.fromJson(java.lang.String.valueOf(response), PatientDetail::class.java)
                populateConsultDetails()
                getTeamMemberDetails(patID, "Team " + patientDetails?.getPatient()?.getTeamName())
            } else if (!TextUtils.isEmpty(response.getErrorMessage()) && response.getErrorMessage() != null) {
                dismissProgressBar()
                val errMsg: String? = ErrorMessages().getErrorMessage(this, java.lang.String.valueOf(response.getErrorMessage()), Constants.API.getPatientDetails)
                errMsg?.let {
                    CustomSnackBar.make(binding?.containerLayout, this, CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0)
                }?.show()
                Log.d(TAG, "getPatientDetails errMsg : $errMsg")
            } else {
                dismissProgressBar()
                val strErrMsg = getString(R.string.api_error)
                CustomSnackBar.make(binding?.containerLayout, this,
                    CustomSnackBar.WARNING, strErrMsg, CustomSnackBar.TOP, 3000, 1)?.show()
                Log.d(TAG, "getPatientDetails strErrMsg : $strErrMsg")
            }
        }*/
    }

    /**
     * Populate the consult patient details
     */
/*    private fun populateConsultDetails() {
        val heartRateValue = patientDetails!!.getPatient()!!.getHeartRateValue()
        val highBPValue = patientDetails!!.getPatient()!!.getArterialBloodPressureSystolicValue()
        val lowBPValue = patientDetails!!.getPatient()!!.getArterialBloodPressureDiastolicValue()
        val spo2Value = patientDetails!!.getPatient()!!.getSpO2Value()
        val respRateValue = patientDetails!!.getPatient()!!.getRespiratoryRateValue()
        val fiO2Value = patientDetails!!.getPatient()!!.getFio2Value()
        val tempValue = patientDetails!!.getPatient()!!.getTemperatureValue()
        val provider = ConsultProvider()
        // Heart rate
        if (!TextUtils.isEmpty(heartRateValue)) {
            provider.setHeartRate(patientDetails!!.getPatient()!!.getHeartRate())
        }
        // HighBPValue
        if (!TextUtils.isEmpty(highBPValue)) {
            provider.setArterialBloodPressureSystolic(
                patientDetails!!.getPatient()!!.getArterialBloodPressureSystolic()
            )
        }
        // LowBPValue
        if (!TextUtils.isEmpty(lowBPValue)) {
            provider.setArterialBloodPressureDiastolic(
                patientDetails!!.getPatient()!!.getArterialBloodPressureDiastolic()
            )
        }
        // Spo2Value
        if (!TextUtils.isEmpty(spo2Value)) {
            provider.setSpO2(patientDetails!!.getPatient()!!.getSpO2())
        }
        // FiO2Value
        if (!TextUtils.isEmpty(fiO2Value)) {
            provider.setFio2(patientDetails!!.getPatient()!!.getFio2())
        }
        //RespRateValue
        if (!TextUtils.isEmpty(respRateValue)) {
            provider.setRespiratoryRate(patientDetails!!.getPatient()!!.getRespiratoryRate())
        }
        //TempValue
        if (!TextUtils.isEmpty(tempValue)) {
            provider.setTemperature(patientDetails!!.getPatient()!!.getTemperature())
        }
        // O2 supplement
        provider.setOxygenSupplement(patientDetails!!.getPatient()!!.isOxygenSupplement())
        if (!TextUtils.isEmpty(patientDetails!!.getPatient()!!.getPatientCondition())) {
            if (patientDetails!!.getPatient()!!.getPatientCondition()
                    .equalsIgnoreCase(java.lang.String.valueOf(Constants.PatientCondition.Alert))
            ) {
                provider.setPatientCondition(Constants.PatientCondition.Alert)
            } else if (patientDetails!!.getPatient()!!.getPatientCondition()
                    .equalsIgnoreCase(java.lang.String.valueOf(Constants.PatientCondition.Voice))
            ) {
                provider.setPatientCondition(Constants.PatientCondition.Voice)
            } else if (patientDetails!!.getPatient()!!.getPatientCondition()
                    .equalsIgnoreCase(java.lang.String.valueOf(Constants.PatientCondition.Pain))
            ) {
                provider.setPatientCondition(Constants.PatientCondition.Pain)
            } else if (patientDetails!!.getPatient()!!.getPatientCondition()
                    .equalsIgnoreCase(java.lang.String.valueOf(Constants.PatientCondition.Unresponsive))
            ) {
                provider.setPatientCondition(Constants.PatientCondition.Unresponsive)
            }
        }

        // Accept textview is changed based on thr consult provider status
        val status: PatientStatus? = mConsultProvider!!.getStatus()
        Log.e(TAG, "populateConsultDetails: PatientStatus$status")
        binding.txtAccept.setText(getString(R.string.accept))
        Log.e(TAG, "populateConsultDetails:txtAcceptW/o" + binding.txtAccept.getText().toString())
        if (status === Constants.PatientStatus.Handoff) {
            binding.txtAccept.setText(getString(R.string.handoff_accept))
            Log.e(TAG, "populateConsultDetails:txtAccept " + binding.txtAccept.getText().toString())
            //            mConsultProvider.setStatus(Constants.PatientStatus.Handoff);
        }


        // Score
        mConsultProvider!!.setScore(
            Constants.AcuityLevel.valueOf(
                patientDetails!!.getPatient()!!.getScore()!!
            )
        )
        UtilityMethods().displayVitals(this, stub, provider)
        mConsultProvider!!.getUrgent()?.let {
            UtilityMethods().displayPatientStatusComponent(
                this, statusStub, it,
                status === Constants.PatientStatus.Pending, Constants.AcuityLevel.valueOf(
                    patientDetails!!.getPatient()!!.getScore()!!
                )
            )
        }

        // Contact team text is changed based on the role
        val strRpUserType: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        strAcuityScore = patientDetails!!.getPatient()!!.getScore()
        if (status === Constants.PatientStatus.Pending || status === Constants.PatientStatus.Invited || status === Constants.PatientStatus.Handoff) {
            binding?.txtContactTeam?.setVisibility(View.GONE)
            if (strRpUserType.equals("MD/DO", ignoreCase = true)) {
                binding?.txtAccept?.setVisibility(View.VISIBLE)
            }
        } else {
            binding.txtContactTeam.setVisibility(View.VISIBLE)
            binding.txtAccept.setVisibility(View.GONE)
        }
        // Record number
        if (!TextUtils.isEmpty(patientDetails!!.getPatient()!!.getRecordNumber())) {
            binding.txtMRNNumber.setText(
                Html.fromHtml(
                    "MRN&nbsp;" + patientDetails!!.getPatient()!!.getRecordNumber()
                )
            )
        } else {
            binding.txtMRNNumber.setText("MRN ")
        }
        // Patient name
        binding.txtPatientName.setText(
            patientDetails!!.getPatient()!!.getFname()
                .toString() + " " + patientDetails!!.getPatient()!!
                .getLname()
        )

        // DOB
        val dot = " <b>\u00b7</b> "
        if (!TextUtils.isEmpty(patientDetails!!.getPatient()!!.getDob())) {
            strDob = dot + getDob()
        }
        // Gender
        if (!TextUtils.isEmpty(patientDetails!!.getPatient()!!.getGender())) {
            strGender = patientDetails!!.getPatient()!!.getGender()
            if (strGender.equals("Male", ignoreCase = true)) {
                strGender = dot + "M"
            } else if (strGender.equals("Female", ignoreCase = true)) {
                strGender = dot + "F"
            }
        }
        // Phone number
        strPhone = if (patientDetails!!.getPatient()!!.getPhone() != null && !TextUtils.isEmpty(
                patientDetails!!.getPatient()!!.getPhone()
            ) &&
            !patientDetails!!.getPatient()!!.getPhone().equalsIgnoreCase("null")
        ) {
            dot + patientDetails!!.getPatient()!!.getPhone()
        } else {
            ""
        }

        //Age
        binding.txtAge.setText(Html.fromHtml(getAge() + strGender + strDob + strPhone))
        if (!TextUtils.isEmpty(patientDetails!!.getPatient()!!.getWardName()!!.trim()) &&
            !patientDetails!!.getPatient()!!.getWardName().equalsIgnoreCase("null")
        ) {
            strWard = dot + patientDetails!!.getPatient()!!.getWardName()
        } else {
            strWard = ""
        }
        // Hospital
        if (!TextUtils.isEmpty(patientDetails!!.getPatient()!!.getHospital())) {
            binding.txtLocation.setText(
                Html.fromHtml(
                    patientDetails!!.getPatient()!!.getHospital().toString() + strWard
                )
            )
        }

        // Getting the note for complain details
        if (!TextUtils.isEmpty(patientDetails!!.getPatient()!!.getNote())) {
            val strComplaint = patientDetails!!.getPatient()!!.getNote()
            val stringComplaint = strComplaint!!.substring(strComplaint.indexOf(":") + 1)
            binding.txtComplaintDetail.setText(stringComplaint.trim { it <= ' ' })
        }
        // Sync time
        if (patientDetails!!.getPatient()!!
                .getSyncTime() != null && !patientDetails!!.getPatient()!!
                .getSyncTime().equals("0")
        ) {
            binding.txtTimeZone.setText(
                Utils.timestampToDate(
                    patientDetails!!.getPatient()!!.getSyncTime()!!.toLong()
                )
            )
        } else {
            binding.txtTimeZone.setText(" - ")
        }

        // Show the more option based on the role
        val role: String = PrefUtility.getRole(this@ActivityConsultChart)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            if (strRpUserType == "MD/DO" && (mConsultProvider!!.getStatus()!!
                    .equals(Constants.PatientStatus.Active)
                        || mConsultProvider!!.getStatus()!!
                    .equals(Constants.PatientStatus.HandoffPending))
            ) {
                binding.floatFab.setVisibility(View.VISIBLE)
            } else if (strRpUserType == "MD/DO" && mConsultProvider!!.getStatus()!!
                    .equals(Constants.PatientStatus.Handoff) && !TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(
                    Constants.IntentKeyConstants.SCREEN_CENSUS,
                    ignoreCase = true
                ) && !binding.txtAccept.getText().toString()
                    .equalsIgnoreCase(getString(R.string.handoff_accept))
            ) {
//            else if (strRpUserType.equals("MD/DO")  && mConsultProvider.getStatus().equals(Constants.PatientStatus.Handoff) &&(!TextUtils.isEmpty(strScreenCensus)) && strScreenCensus.equalsIgnoreCase(Constants.IntentKeyConstants.SCREEN_CENSUS)) {
                binding.floatFab.setVisibility(View.VISIBLE)
            } else if (strRpUserType == "MD/DO" && mConsultProvider!!.getStatus()!!
                    .equals(Constants.PatientStatus.Pending)
            ) {
                binding.floatFab.setVisibility(View.GONE)
            } else {
                binding.floatFab.setVisibility(View.GONE)
            }
        }
    }*/

    /**
     * Accept text click listener - Calling the "acceptInvite" API
     * @param consultProvider
     */
    private fun acceptClick(consultProvider: ConsultProvider?) {
        providerApiFlag = true
        // Show progress bar
        showProgressBar()
        // If the consultProvider status is "Invited"
        if (consultProvider!!.getStatus() === Constants.PatientStatus.Invited) {
            val providerId: Long? = PrefUtility().getProviderId(this)
            val token: String? =
                PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
           /* HomeViewModel().acceptInvite(providerId, token, consultProvider!!.getId())
                .observe(this) { listResponse ->
                    Log.d(
                        TAG,
                        "acceptInvite Request : " + providerId + "consult Provider id :" + consultProvider.getId()
                    )
                    providerApiFlag = false
                    if (listResponse != null && listResponse.getStatus() != null && listResponse.getStatus()) {
                        mConsultProvider!!.setStatus(Constants.PatientStatus.Active)
                        Log.d(TAG, "acceptInvite Response : " + Gson().toJson(listResponse))
                        //Getting patient details via "getPatienDetails" API call based on the UID
                        getPatientDetails(uid)
                        binding.txtAccept.setVisibility(View.GONE)
                        binding.txtContactTeam.setVisibility(View.VISIBLE)
                        val role: String = PrefUtility.getRole(this@ActivityConsultChart)
                        val strRpUserType: String = PrefUtility.getStringInPref(
                            this@ActivityConsultChart,
                            Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                            ""
                        )
                        Log.e(TAG, "acceptClick:FloatFab strRpUserType-->$strRpUserType")
                        Log.e(
                            TAG,
                            "acceptClick:FloatFab mConsultProvider.getStatus()-->" + mConsultProvider!!.getStatus()
                        )
                        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                            if (strRpUserType == "MD/DO" && mConsultProvider!!.getStatus()!!
                                    .equals(Constants.PatientStatus.Active)
                            ) {
                                binding.floatFab.setVisibility(View.VISIBLE)
                            } else if (strRpUserType == "MD/DO" && mConsultProvider!!.getStatus()!!
                                    .equals(Constants.PatientStatus.Pending)
                            ) {
                                binding.floatFab.setVisibility(View.GONE)
                            } else {
                                binding.floatFab.setVisibility(View.GONE)
                            }
                        }
                    } else {
                        // Dissmis progress bar
                        dismissProgressBar()
                        Log.d(TAG, "acceptClick Error : " + Gson().toJson(listResponse))
                        var errMsg = ""
                        if (listResponse != null && !TextUtils.isEmpty(listResponse.getErrorMessage())) {
                            errMsg = listResponse.getErrorMessage()
                        }
                        CustomSnackBar.make(
                            binding.containerLayout,
                            this@ActivityConsultChart,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        )!!.show()
                    }
                }*/
        } else {
            // If the consultProvider status is "Handoff" or HandoffPending - Calling the "acceptRemoteHandoff" API
            if (consultProvider!!.getStatus() === Constants.PatientStatus.Handoff || consultProvider!!.getStatus()!! == Constants.PatientStatus.HandoffPending
            ) {
                val providerID: Long =
                    PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0)
               /* val handOffAcceptRequest = HandOffAcceptRequest()
                handOffAcceptRequest.setPatientId(consultProvider!!.getId())
                handOffAcceptRequest.setProviderId(providerID)
                Log.d(TAG, "acceptRemoteHandoff Request" + Gson().toJson(handOffAcceptRequest))
                HomeViewModel().acceptRemoteHandoff(handOffAcceptRequest)
                    .observe(this) { listResponse ->
                        providerApiFlag = false
                        Log.d(
                            TAG,
                            "acceptRemoteHandoff Response : " + Gson().toJson(listResponse)
                        )
                        if (listResponse != null && listResponse.getStatus() != null && listResponse.getStatus()) {
                            mConsultProvider!!.setStatus(Constants.PatientStatus.Active)
                            //                    onAcceptSuccess();
                            //Getting patient details via "getPatienDetails" API call based on the UID
                            getPatientDetails(uid)
                            binding?.txtAccept?.setVisibility(View.GONE)
                            binding?.txtContactTeam?.setVisibility(View.VISIBLE)
                            val role: String? = PrefUtility().getRole(this@ActivityConsultChart)
                            val strRpUserType: String? = PrefUtility().getStringInPref(this@ActivityConsultChart, Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                                "")
                            Log.e(TAG, "acceptClick:Else strRpUserType-->$strRpUserType")
                            Log.e(TAG, "acceptClick:Else mConsultProvider.getStatus()-->" + mConsultProvider!!.getStatus())
                            if (role.equals(
                                    Constants.ProviderRole.RD.toString(),
                                    ignoreCase = true
                                )
                            ) {
                                if (strRpUserType == "MD/DO" && mConsultProvider!!.getStatus()!!
                                        .equals(Constants.PatientStatus.Active)
                                ) {
                                    binding.floatFab.setVisibility(View.VISIBLE)
                                } else if (strRpUserType == "MD/DO" && mConsultProvider!!.getStatus()!!
                                        .equals(Constants.PatientStatus.Pending)
                                ) {
                                    binding.floatFab.setVisibility(View.GONE)
                                } else {
                                    binding.floatFab.setVisibility(View.GONE)
                                }
                            }
                        } else {
                            // Dismiss progress bar
                            dismissProgressBar()
                            Log.d(
                                TAG,
                                "acceptRemoteHandoff Error : " + Gson().toJson(listResponse)
                            )
                            var errMsg = ""
                            if (listResponse != null && !TextUtils.isEmpty(listResponse.getErrorMessage())) {
                                errMsg = listResponse.getErrorMessage()
                            }
                            CustomSnackBar.make(
                                binding.containerLayout,
                                this@ActivityConsultChart,
                                CustomSnackBar.WARNING,
                                errMsg,
                                CustomSnackBar.TOP,
                                3000,
                                0
                            )!!.show()
                        }
                    }*/
            }
        }
    }

    fun onAcceptSuccess() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.custom_alert_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.text = getString(R.string.success)
        alertTitle.visibility = View.GONE
        alertMsg.text = getString(R.string.invitation_accepted)
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
     * @return
     */
    private fun getDob(): String {
        val timeInMillis = java.lang.Long.valueOf(patientDetails!!.getPatient()!!.getDob())
        return SimpleDateFormat("MM-dd-yyyy").format(Date(timeInMillis))
    }

    /**
     * Getting the Age in a formatted string
     * @return
     */
    private fun getAge(): String {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        calendar.timeInMillis = patientDetails!!.getPatient()!!.getDob()!!.toLong()
        val agee = year - calendar[Calendar.YEAR]
        return agee.toString()
    }

/*
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_reset_acuity:
//                showAcuityResetConfirmDialog();
                return true;
            case R.id.item_transfer_patient:
                Intent intent = new Intent(ActivityConsultChart.this, TransferPatientActivity.class);
                intent.putExtra("patientId", mConsultProvider.getPatientId());
                startActivity(intent);
                return true;
            case R.id.item_handoff_patient:
                doRemoteSideHandOff();
                return true;
            case R.id.item_complete_consultation: {
                doCompleteConsultation();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/

    /*
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_reset_acuity:
//                showAcuityResetConfirmDialog();
                return true;
            case R.id.item_transfer_patient:
                Intent intent = new Intent(ActivityConsultChart.this, TransferPatientActivity.class);
                intent.putExtra("patientId", mConsultProvider.getPatientId());
                startActivity(intent);
                return true;
            case R.id.item_handoff_patient:
                doRemoteSideHandOff();
                return true;
            case R.id.item_complete_consultation: {
                doCompleteConsultation();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
    /**
     * Direct to Remote Hand off activity
     */
    private fun doRemoteSideHandOff() {
        val intent = Intent(this, RemoteHandOffActivity::class.java)
        intent.putExtra("patient_id", mConsultProvider!!.getPatientId())
        intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, strScreenCensus)
        startActivityForResult(intent, 505)
    }

    /**
     * Show dialog to complete the consultation
     */
    private fun doCompleteConsultation() {

//        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        val dialog = Dialog(this, R.style.AppTheme_NoActionBarDark)
        dialog.setContentView(R.layout.mark_complete_dialog)
        dialog.show()
        val toolbarComplete = dialog.findViewById<View>(R.id.toolbarComplete) as Toolbar
        toolbarComplete.title = getString(R.string.complete_consultation)
        toolbarComplete.setNavigationIcon(R.drawable.ic_back)
        toolbarComplete.setNavigationOnClickListener { dialog.dismiss() }
        val idContainerLayout = dialog.findViewById<LinearLayout>(R.id.idContainerLayout)
        val btnComplete = dialog.findViewById<Button>(R.id.btnComplete)
        val edtAssessment = dialog.findViewById<EditText>(R.id.discharge_assessment)
        val edtPlan = dialog.findViewById<EditText>(R.id.discharge_plan)
        val prefixAssessment = getString(R.string.assessment)
        val prefixPlan = getString(R.string.plan)
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
            if (TextUtils.isEmpty(assessment) || assessment.equals(
                    "Assessment:",
                    ignoreCase = true
                )
            ) {
                if (TextUtils.isEmpty(plan) || plan.equals("Plan:", ignoreCase = true)) {
//                        UtilityMethods.showErrorSnackBar(v, getString(R.string.summary_note_is_mandatory), Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        v, this@ActivityConsultChart, CustomSnackBar.WARNING,
                        getString(R.string.summary_note_is_mandatory),
                        CustomSnackBar.TOP, 3000, 0
                    )!!.show()
                    return@OnClickListener
                }
            }
            if (TextUtils.isEmpty(plan) || plan.equals("Plan:", ignoreCase = true)) {
                if (TextUtils.isEmpty(assessment) || assessment.equals(
                        "Assessment:",
                        ignoreCase = true
                    )
                ) {
//                        UtilityMethods.showErrorSnackBar(v, getString(R.string.summary_note_is_mandatory), Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        v, this@ActivityConsultChart, CustomSnackBar.WARNING,
                        getString(R.string.summary_note_is_mandatory),
                        CustomSnackBar.TOP, 3000, 0
                    )!!.show()
                    return@OnClickListener
                }
            }
            if (!UtilityMethods().isInternetConnected(this@ActivityConsultChart)) {
//                    UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    binding?.root, this@ActivityConsultChart, CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP, 3000, 0
                )!!.show()
                return@OnClickListener
            }
            dialog.dismiss()
            showProgressBar()
            val notes = "$assessment\n \n$plan"
            val providerID: Long? = PrefUtility().getProviderId(this@ActivityConsultChart)
            val token: String? = PrefUtility().getStringInPref(
                this@ActivityConsultChart,
                Constants.SharedPrefConstants.TOKEN,
                ""
            )
            // DischargePatient API call
         /*   viewModel.dischargePatient(providerID, token, mConsultProvider!!.getId(), notes)
                .observe(this@ActivityConsultChart) { commonResponse ->
                    dismissProgressBar()
                    if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                        if (mConsultProvider != null) {
                            val providerName: String? = PrefUtility().getStringInPref(
                                this@ActivityConsultChart,
                                Constants.SharedPrefConstants.NAME,
                                ""
                            )
                            val role: String? = PrefUtility().getStringInPref(
                                this@ActivityConsultChart,
                                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                                ""
                            )
                            mFirebaseDatabaseReference!!.child("providers")
                                .child(providerID.toString())
                                .child("active").child(
                                    java.lang.String.valueOf(
                                        mConsultProvider!!.getId()
                                    )
                                ).child("completed_by").setValue("$providerName, $role")
                            mConsultProvider!!.setStatus(Constants.PatientStatus.Discharged)
                            if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(
                                    Constants.IntentKeyConstants.SCREEN_CENSUS,
                                    ignoreCase = true
                                )
                            ) {
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                startActivity(
                                    Intent(this@ActivityConsultChart, HomeActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .putExtra(
                                            Constants.IntentKeyConstants.TARGET_PAGE,
                                            "completed"
                                        )
                                )
                                finish()
                            }

                            *//*  startActivity(new Intent(ActivityConsultChart.this, HomeActivity.class)
                                  .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                  .putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "completed"));
                          finish();*//*
                        }
                    } else {
                        btnComplete.isEnabled = true
                        val errMsg: String = ErrorMessages.getErrorMessage(
                            this@ActivityConsultChart,
                            commonResponse.getErrorMessage(),
                            Constants.API.register
                        )
                        //                        UtilityMethods.showErrorSnackBar(idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                        CustomSnackBar.make(
                            idContainerLayout, this@ActivityConsultChart,
                            CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0
                        )!!.show()
                    }
                }*/
        })
        dialog.show()
    }
    /**
     * BP more option click listener popup
     * @param context
     */
    private fun MoreOptionBPDialog(context: Context?) {
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
            intent.putExtra("patientId", mConsultProvider!!.getPatientId())
            intent.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, strScreenCensus)
            startActivityForResult(intent, 505)
            //                finish();
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * More option dialog for RP
     * @param context
     */
    private fun MoreOptionRPDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.rp_more_option_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlHandOffPatient = dialog.findViewById<View>(R.id.rlHandOffPatient) as RelativeLayout
        val rlCompletedConsultation =
            dialog.findViewById<View>(R.id.rlCompletedConsultation) as RelativeLayout
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        imgCancel.setOnClickListener { dialog.dismiss() }
        if (mConsultProvider!!.getStatus()!! == Constants.PatientStatus.HandoffPending) {
//        if (mConsultProvider.getStatus().equals(Constants.PatientStatus.HandoffPending) || mConsultProvider.getStatus().equals(Constants.PatientStatus.Handoff) ) {
            rlHandOffPatient.visibility = View.GONE
        } else {
            rlHandOffPatient.visibility = View.VISIBLE
        }

        //Remote side handoff triggered
        rlHandOffPatient.setOnClickListener {
            doRemoteSideHandOff()
            dialog.dismiss()
        }
        // Complete consultation is triggered
        rlCompletedConsultation.setOnClickListener {
            doCompleteConsultation()
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * Displaying reset acuity dialog
     * @param context
     */
    private fun resetAcuityDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.reset_acuity_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
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
        when {
            strAcuityScore.equals(
                java.lang.String.valueOf(Constants.AcuityLevel.High),
                ignoreCase = true
            ) -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.VISIBLE
                btnResetAcuity.isEnabled = false
            }
            strAcuityScore.equals(
                java.lang.String.valueOf(Constants.AcuityLevel.Medium),
                ignoreCase = true
            ) -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.VISIBLE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = false
            }
            strAcuityScore.equals(
                java.lang.String.valueOf(Constants.AcuityLevel.Low),
                ignoreCase = true
            ) -> {
                imgLowAcuity.visibility = View.VISIBLE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = false
            }
        }

        // High acuity click listener
        rlHigh.setOnClickListener {
            if (strAcuityScore.equals(
                    java.lang.String.valueOf(Constants.AcuityLevel.High),
                    ignoreCase = true
                )
            ) {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.VISIBLE
                btnResetAcuity.isEnabled = false
                btnResetAcuity.background = resources.getDrawable(R.drawable.btn_bg_grey)
                btnResetAcuity.setTextColor(resources.getColor(R.color.login_hint_color))
            } else {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.VISIBLE
                btnResetAcuity.isEnabled = true
                btnResetAcuity.background = resources.getDrawable(R.drawable.login_edittext_bg)
                btnResetAcuity.setTextColor(resources.getColor(R.color.white))
                acuityLevel = Constants.AcuityLevel.High
            }
        }
        // Medium acuity click listener
        rlMedium.setOnClickListener {
            if (strAcuityScore.equals(
                    java.lang.String.valueOf(Constants.AcuityLevel.Medium),
                    ignoreCase = true
                )
            ) {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.VISIBLE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = false
                btnResetAcuity.background = resources.getDrawable(R.drawable.btn_bg_grey)
                btnResetAcuity.setTextColor(resources.getColor(R.color.login_hint_color))
            } else {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.VISIBLE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = true
                btnResetAcuity.background = resources.getDrawable(R.drawable.login_edittext_bg)
                btnResetAcuity.setTextColor(resources.getColor(R.color.white))
                acuityLevel = Constants.AcuityLevel.Medium
            }
        }
        // Low acuity click listener
        rlLow.setOnClickListener {
            if (strAcuityScore.equals(
                    java.lang.String.valueOf(Constants.AcuityLevel.Low),
                    ignoreCase = true
                )
            ) {
                imgLowAcuity.visibility = View.VISIBLE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = false
                btnResetAcuity.background = resources.getDrawable(R.drawable.btn_bg_grey)
                btnResetAcuity.setTextColor(resources.getColor(R.color.login_hint_color))
            } else {
                imgLowAcuity.visibility = View.VISIBLE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                btnResetAcuity.isEnabled = true
                btnResetAcuity.background = resources.getDrawable(R.drawable.login_edittext_bg)
                btnResetAcuity.setTextColor(resources.getColor(R.color.white))
                acuityLevel = Constants.AcuityLevel.Low
            }
        }

        // Reset button acuity click
        btnResetAcuity.setOnClickListener {
            val providerId: Long? = PrefUtility().getProviderId(this)
            val token: String? = PrefUtility().getToken(this)
            val patientId =
                if (mConsultProvider?.getId() != null) mConsultProvider?.getId()!! else 0.toLong()
            Log.d(
                TAG, "showAcuityReset Request : " + providerId + " : " + token + " : " +
                        patientId + " : " + acuityLevel
            )
            val builder = AlertDialog.Builder(
                this@ActivityConsultChart,
                R.style.CustomAlertDialog
            )
            val viewGroup: ViewGroup = findViewById(android.R.id.content)
            val dialogView = LayoutInflater.from(applicationContext)
                .inflate(R.layout.alert_custom_dialog, viewGroup, false)
            val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
            val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
            alertTitle.text = getString(R.string.success)
            alertTitle.visibility = View.GONE
            alertMsg.text = getString(R.string.reset_acuity_score_confirm)
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
               /* viewModel.resetAcuityValue(providerId, token, patientId, java.lang.String.valueOf(acuityLevel)).observe(this, androidx.lifecycle.Observer<Any?> { commonResponse ->
                        dismissProgressBar()
                        if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                            Log.d(
                                TAG,
                                "showAcuityReset Response : $commonResponse"
                            )
                            mConsultProvider!!.setResetAcuityFlag(true)
                            if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(
                                    Constants.IntentKeyConstants.SCREEN_CENSUS,
                                    ignoreCase = true
                                )
                            ) {
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                // Directing to home activity based on SCREEN_CENSUS
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
                                alertDialog.dismiss()
                                dialog.dismiss()
                            }
                        } *//*else if (!TextUtils.isEmpty(commonResponse.getErrorMessage()) && commonResponse.getErrorMessage() != null) {
                            val errMsg: String = commonResponse.getErrorMessage()
                            CustomSnackBar.make(binding?.getRoot(), this, CustomSnackBar.WARNING,
                                errMsg, CustomSnackBar.TOP, 3000, 0)?.show()
                        }*//* else {
                            CustomSnackBar.make(binding?.getRoot(), this, CustomSnackBar.WARNING,
                                getString(R.string.api_error), CustomSnackBar.TOP, 3000, 0)?.show()
                        }
                    })
        */    }
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
