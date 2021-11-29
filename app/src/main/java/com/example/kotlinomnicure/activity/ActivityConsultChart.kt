package com.mvp.omnicure.kotlinactivity.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.dailytasksamplepoc.R

import com.example.dailytasksamplepoc.databinding.ActivityEconsultChartBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.*
import com.example.dailytasksamplepoc.kotlinomnicure.model.ConsultProvider
import com.example.dailytasksamplepoc.kotlinomnicure.utils.CustomDialog
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.PatientDetailViewModel
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.PrefUtility

import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.patientsEndpoints.model.PatientDetail
import omnicurekotlin.example.com.providerEndpoints.model.Members
import com.google.firebase.database.*



class ActivityConsultChart : AppCompatActivity() {
    private val TAG = ActivityConsultChart::class.java.simpleName
    protected var binding: ActivityEconsultChartBinding? = null
    var statusStub: RelativeLayout? = null
    var providerApiFlag = false
    private val mUnreadMessageDB: DatabaseReference? = null
    private val membersList: List<Members> = ArrayList()
    private val errorTeams: String? = null
    private val acuityLevel = Constants.AcuityLevel.NA
    private val strConsultTeamName: String? = null
    private var uid: Long = 0
    private val patientId: Long = 0
    private var viewModel: PatientDetailViewModel? = null
    private var patientDetails: PatientDetail? = null
    private val strPhone: String? = null
    private val strDob: String? = null
    private val strGender: String? = null
    private  var strWard:kotlin.String? = null
    private val role: String? = null
    private val mConsultProvider: ConsultProvider? = null
    private val mConsultProviderKey: String? = null
    private var stub: ViewStub? = null
    private val strMessageReaded: String? = null
    private val customDialog: CustomDialog? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private val strAcuityScore: String? = null
    private val strScreenCensus = ""
    val ctx:Context=ActivityConsultChart()

    // Unread message listener from firebase - Add Consult provider data from the snapshot
    var mUnreadMessageListener: ValueEventListener = object : ValueEventListener {
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
            if (consultProviderA.status != null && consultProviderA.status == Constants.PatientStatus.Active && mConsultProvider!!.status != Constants.PatientStatus.Active) {
                println("providerapiflag $providerApiFlag")
                if (providerApiFlag) {
                    return
                }
                mConsultProvider.status = Constants.PatientStatus.Active

                //Getting patient details via "getPatienDetails" API call based on the UID
                getPatientDetails(uid)
                binding!!.txtAccept.visibility = View.GONE
                binding!!.txtContactTeam.visibility = View.VISIBLE
                val role = PrefUtility().getRole(ctx)
                val strRpUserType = PrefUtility().getStringInPref(ctx, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")


                // Condition to show the menu options to the user
                if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                    if (strRpUserType == "MD/DO" && mConsultProvider.status == Constants.PatientStatus.Active) {
                        binding!!.floatFab.visibility = View.VISIBLE
                    } else if (strRpUserType == "MD/DO" && mConsultProvider.status == Constants.PatientStatus.Pending) {
                        binding!!.floatFab.visibility = View.GONE
                    } else {
                        binding!!.floatFab.visibility = View.GONE
                    }
                }
            }
            if (consultProviderA.status != null
                && mConsultProvider!!.status != Constants.PatientStatus.Pending
            ) {
                mConsultProvider.status = consultProviderA.status
            }
            if (consultProviderA.unread > 0) {
                Log.d(TAG, "Unread Message Count" + consultProviderA.unread)

                binding!!.imgMessageAlert.visibility = View.VISIBLE
            } else {
                binding!!.imgMessageAlert.visibility = View.GONE
            }
            // Removing the stubs
            if (mConsultProvider!!.score != null && mConsultProvider.score != consultProviderA.score) {
                statusStub!!.removeAllViews()

                // statusStub
                UtilityMethods().displayPatientStatusComponent(
                    ctx,
                    statusStub!!,
                    mConsultProvider.urgent,
                    consultProviderA.status == Constants.PatientStatus.Pending,
                    consultProviderA.score
                )
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "onCancelled: of value event listener $databaseError")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        stub = findViewById<View>(R.id.layout_stub_view) as ViewStub
        statusStub = findViewById<View>(R.id.status_stub) as RelativeLayout

        // Getting the values from the intent
        uid = intent.getLongExtra("uid", 0)
        Log.d(TAG, "OnCreate UID$uid")
        if (PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "") == "RD") {
            binding!!.llCallLayout.visibility = View.INVISIBLE
        } else {
            binding!!.llCallLayout.visibility = View.VISIBLE
        }

        // Setting up the providing object
        setProviderObject()
        val role = PrefUtility().getRole(this)
        val strRpUserType = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.R_PROVIDER_TYPE,
            ""
        )

        val providerID = PrefUtility().getProviderId(this)


        // Display more option based on the provider role and status of the patient
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            if (strRpUserType == "MD/DO" && (mConsultProvider!!.status == Constants.PatientStatus.Active || mConsultProvider.status == Constants.PatientStatus.HandoffPending)) {
                binding!!.floatFab.visibility = View.VISIBLE
            } else if (strRpUserType == "MD/DO" && mConsultProvider!!.status == Constants.PatientStatus.Handoff && !TextUtils.isEmpty(
                    strScreenCensus
                ) && strScreenCensus.equals(
                    Constants.IntentKeyConstants.SCREEN_CENSUS, ignoreCase = true
                )
            ) {
                binding!!.floatFab.visibility = View.VISIBLE
            } else if (strRpUserType == "MD/DO" && mConsultProvider!!.status == Constants.PatientStatus.Pending) {
                binding!!.floatFab.visibility = View.GONE
            } else {
                binding!!.floatFab.visibility = View.GONE
            }
        }
        // Getting the patient details via API call using UID as inout
        getPatientDetails(uid)
    }

    private fun setProviderObject() {

    }



    /**
     * Intiating the on click listener
     */
    private fun initOnClickListener() {
        binding!!.imgBack.setOnClickListener { finish() }
        // Image up click listener
        binding!!.imgDetailUp.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding!!.imgDetailUp.visibility = View.GONE
            binding!!.imgDetailDown.visibility = View.VISIBLE
            binding!!.llComplaints.visibility = View.GONE
        }

        // Image down click listener
        binding!!.imgDetailDown.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding!!.imgDetailDown.visibility = View.GONE
            binding!!.imgDetailUp.visibility = View.VISIBLE
            binding!!.llComplaints.visibility = View.VISIBLE
        }

        // Image vital up click listener
        binding!!.imgVitalUp.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding!!.imgVitalUp.visibility = View.GONE
            binding!!.imgVitalDown.visibility = View.VISIBLE
            binding!!.llTimeZone.visibility = View.GONE
            binding!!.llVital.visibility = View.GONE
        }
        // Image vital down click listener
        binding!!.imgVitalDown.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            binding!!.imgVitalDown.visibility = View.GONE
            binding!!.imgVitalUp.visibility = View.VISIBLE
            binding!!.llTimeZone.visibility = View.VISIBLE
            binding!!.llVital.visibility = View.VISIBLE
        }
        // Handling the message click
        binding!!.llMessage.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)

            // Directing the user to chat activity with needed data
            val intentConsultChart = Intent(this, ChatActivity::class.java)
            intentConsultChart.putExtra("uid", mConsultProvider!!.id)
            Log.d(
                TAG,
                "strUid : " + mConsultProvider.id
            )
            intentConsultChart.putExtra("path", "consults/" + mConsultProvider.id)
            intentConsultChart.putExtra("consultProviderId", "" + mConsultProviderKey)
            intentConsultChart.putExtra(
                getString(R.string.consultProviderPatientId),
                "" + mConsultProvider.id
            )
            intentConsultChart.putExtra("consultProviderText", mConsultProvider.text)
            intentConsultChart.putExtra("consultProviderName", mConsultProvider.name)
            intentConsultChart.putExtra("dob", mConsultProvider.dob)
            intentConsultChart.putExtra("gender", mConsultProvider.gender)
            intentConsultChart.putExtra("note", mConsultProvider.note)
            intentConsultChart.putExtra("phone", mConsultProvider.phone)
            intentConsultChart.putExtra("patientId", mConsultProvider.patientsId)
            intentConsultChart.putExtra("status", mConsultProvider.status)
            if (!TextUtils.isEmpty(strConsultTeamName)) {
                intentConsultChart.putExtra("teamNameConsult", strConsultTeamName)
            }
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                mConsultProvider.urgent
            )
            //Setting the consult provider status if not null
            if (mConsultProvider.status != null) {
                intentConsultChart.putExtra("status", mConsultProvider.status.toString())
                if (mConsultProvider.status == Constants.PatientStatus.Invited ||
                    mConsultProvider.status == Constants.PatientStatus.Handoff
                ) {
                    intentConsultChart.putExtra(
                        Constants.IntentKeyConstants.INVITATION,
                        true
                    )
                    clearNotifications(mConsultProvider.id.toInt())
                } else if (mConsultProvider.status == Constants.PatientStatus.Completed ||
                    mConsultProvider.status == Constants.PatientStatus.Discharged
                ) {
                    intentConsultChart.putExtra(
                        Constants.IntentKeyConstants.COMPLETED,
                        true
                    )
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }
            startActivityForResult(intentConsultChart, 1)
        }

        // Handling contact team click
        binding!!.txtContactTeam.setOnClickListener { v ->
            // Handling the multi click
            handleMultipleClick(v)
            if (membersList.size > 0 && membersList != null) {
             //   showTeamMembersDialog(this, membersList, mConsultProvider)
            } else {

                if (errorTeams != null) {
                    CustomSnackBar.make(
                        binding!!.containerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        errorTeams,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
        }
        // Handling accept team click
        binding!!.txtAccept.setOnClickListener { // Handling the multi click
            handleMultipleClick(binding!!.txtAccept)
            // On accept click listener with consult provider data
           // acceptClick(mConsultProvider)
        }

        // Handling call click for SoS
        binding!!.llCallLayout.setOnClickListener { v -> // Handling the multi click
            handleMultipleClick(v)
            // Connecting to the SoS call
          //  checkSelfPermissionsMediaCheck()
          //  connectSOSCall()
        }

        // Handling more option click listener
        binding!!.floatFab.setOnClickListener { v ->
            handleMultipleClick(v)
            val role = PrefUtility().getRole(this)
            val strRpUserType = PrefUtility().getStringInPref(
                this,
                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                ""
            )
            if (role.equals(
                    Constants.ProviderRole.RD.toString(),
                    ignoreCase = true
                )
            ) {
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

    private fun clearNotifications(toInt: Int) {

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
            //resetAcuityDialog(ctx)
            dialog.dismiss()
        }

        // Directed Transfer patient activity
        rlTransferPatient.setOnClickListener { v -> // Handling the multi click event
            handleMultipleClick(v)
            val intent = Intent(this, TransferPatientActivity::class.java)
            intent.putExtra("patientId", mConsultProvider!!.patientsId)
            intent.putExtra(
                Constants.IntentKeyConstants.SCREEN_TYPE,
                strScreenCensus
            )
            startActivityForResult(intent, 505)
            //                finish();
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun resetAcuityDialog(activityConsultChart: ActivityConsultChart) {


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
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val rlHandOffPatient = dialog.findViewById<View>(R.id.rlHandOffPatient) as RelativeLayout
        val rlCompletedConsultation =
            dialog.findViewById<View>(R.id.rlCompletedConsultation) as RelativeLayout
        val rleNotes = dialog.findViewById<View>(R.id.rleNotes) as RelativeLayout
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        imgCancel.setOnClickListener { dialog.dismiss() }
        if (mConsultProvider!!.status == Constants.PatientStatus.HandoffPending) {
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
        rlCompletedConsultation.setOnClickListener { v -> //                doCompleteConsultation();
            handleMultipleClick(v)
            val intent = Intent(this, RemoteCompleteActivity::class.java)
            intent.putExtra("patient_id", mConsultProvider.patientsId)
            intent.putExtra(
                Constants.IntentKeyConstants.SCREEN_TYPE,
                strScreenCensus
            )
            startActivity(intent)
            dialog.dismiss()
        }
        rleNotes.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, ActivityEnotes::class.java)
            intent.putExtra("patient_id", mConsultProvider.patientsId)
            intent.putExtra("patient_name", patientDetails!!.patient?.name)
            intent.putExtra(
                Constants.IntentKeyConstants.SCREEN_TYPE,
                strScreenCensus
            )
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun doRemoteSideHandOff() {

    }

    private fun handleMultipleClick(v: View?) {


    }



    private fun getPatientDetails(uid: Long) {


    }

    /**
     * Showing error dialog based on the patient status like Handoff, HandoffPending
     */
    fun showErrorDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.custom_alert_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.visibility = View.GONE
        var message = getString(R.string.consultation_request_already_accepted_msg)
        val strRole = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        val strDesignation =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        message =
            if (mConsultProvider!!.status == Constants.PatientStatus.Handoff || mConsultProvider.status == Constants.PatientStatus.HandoffPending) {
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
            if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(
                    Constants.IntentKeyConstants.SCREEN_CENSUS,
                    ignoreCase = true
                )
            ) {
                setResult(RESULT_OK)
                finish()
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra(
                    Constants.IntentKeyConstants.TARGET_PAGE,
                    "pending"
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        alertDialog.show()
    }
    /**
     * Removing the event listener for unread message listener
     */
    override fun onStop() {
        println("consult chart onstop")
        super.onStop()
        mUnreadMessageDB!!.removeEventListener(mUnreadMessageListener)
    }

}