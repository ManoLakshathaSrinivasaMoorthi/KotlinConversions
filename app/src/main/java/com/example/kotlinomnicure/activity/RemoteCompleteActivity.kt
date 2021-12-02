package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.example.kotlinomnicure.viewmodel.PatientDetailViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.RemoteHandOffViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.CompletePatientBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.firebase.database.FirebaseDatabase
import com.example.kotlinomnicure.utils.UtilityMethods
import java.util.ArrayList

class RemoteCompleteActivity : BaseActivity(){

    //Variables
    private var binding: CompletePatientBinding? = null
    private val summaryNote: String? = null
    private var viewModel: RemoteHandOffViewModel? = null
    private var strScreenCensus: String? = ""
    var alertDialog: AlertDialog? = null
    private var patientID: String? = null
    private  var patientName:kotlin.String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Databinding and view model intialization
        binding = DataBindingUtil.setContentView(this, R.layout.complete_patient)
        viewModel = ViewModelProvider(this).get(RemoteHandOffViewModel::class.java)
        // Initiating the views for the activity
        initViews()
    }

    /**
     * Initiating the views for the activity
     */
    private fun initViews() {
        val vals = ArrayList<String>()
        vals.add(getString(R.string.summary_text))
        val typeAdapter = ArrayAdapter(this, R.layout.spinner_disabled_text, vals)

        binding?.typeSpinner?.setAdapter(typeAdapter)
        binding?.typeSpinner?.setEnabled(false)
        patientID = java.lang.String.valueOf(intent.getLongExtra("patient_id", 0))
        strScreenCensus = intent.getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)
        val name: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        binding?.editTextUserName?.setText(name)
        binding?.imgBack?.setOnClickListener(View.OnClickListener { finish() })

        binding?.btnComplete?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            showConfirmationPopup()
        })
        binding?.eConsultText?.let { addMandatoryText(it) }
        binding?.eNoteText?.let { addMandatoryText(it) }
        binding?.assessDetailsText?.let { addMandatoryText(it) }
        binding?.planDetailsText?.let { addMandatoryText(it) }
        binding?.dischargePlan?.addTextChangedListener(binding?.let { ValidationTextWatcher(it.dischargePlan) })
        binding?.dischargeAssessment?.addTextChangedListener(binding?.let { ValidationTextWatcher(it.dischargeAssessment) })
        buttonValidation()
    }

    fun showConfirmationPopup() {
        val builder = AlertDialog.Builder(
            this,
            R.style.CustomAlertDialog
        )
        val viewGroup: ViewGroup = findViewById(R.id.content)
        val dialogView: View = LayoutInflater.from(applicationContext)
            .inflate(R.layout.alert_custom_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.text = getString(R.string.complete_confirm_title)
        alertMsg.text = getString(R.string.complete_confirm_text)
        val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
        buttonYes.setTextColor(resources.getColor(R.color.red))
        val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        buttonYes.setOnClickListener { v ->
            handleMultipleClick(v)
            performRemoteComplete()
            alertDialog!!.dismiss()
        }
        // No click listener
        buttonNo.setOnClickListener { v ->
            handleMultipleClick(v)
            alertDialog!!.dismiss()
        }
        alertDialog!!.show()
    }

    override fun onPause() {
        super.onPause()
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
    }


    /**
     * Performing the Remote HandOff via "performRemoteHandOff" API call ith remote handoff object
     */
    private fun performRemoteComplete() {
        val assessment: String = binding?.dischargeAssessment?.getText().toString()
        val plan: String = binding?.dischargePlan?.getText().toString()
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding?.getRoot(), this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        if (TextUtils.isEmpty(assessment)) {
            CustomSnackBar.make(
                binding?.getRoot(), this, CustomSnackBar.WARNING,
                getString(R.string.assessment_is_mandatory),
                CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        if (TextUtils.isEmpty(plan)) {
            CustomSnackBar.make(
                binding?.getRoot(), this, CustomSnackBar.WARNING,
                getString(R.string.plan_is_mandatory),
                CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        showProgressBar()
        val notes = "$assessment\n \n$plan"
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.TOKEN,
            ""
        )
        // DischargePatient API call
        if (providerID != null) {
            if (token != null) {
                PatientDetailViewModel().dischargePatient(providerID, token, patientID!!.toLong(), notes)
                    ?.observe(this) { commonResponse ->
                        dismissProgressBar()
                        println("response for discharge $commonResponse")
                        if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()!!) {
                            val providerName: String? = PrefUtility().getStringInPref(
                                this,
                                Constants.SharedPrefConstants.NAME,
                                ""
                            )
                            val role: String? = PrefUtility().getStringInPref(
                                this,
                                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                                ""
                            )
                            FirebaseDatabase.getInstance().reference.child("providers")
                                .child(providerID.toString()).child("active").child(patientID.toString())
                                .child("completed_by").setValue("$providerName, $role")
                            startActivity(
                                Intent(this, HomeActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "completed")
                            )
                            finish()
                        } else {
                            binding?.btnComplete?.setEnabled(true)
                            val errMsg: String? = ErrorMessages().getErrorMessage(
                                this,
                                commonResponse?.getErrorMessage(),
                                Constants.API.register
                            )

                            if (errMsg != null) {
                                CustomSnackBar.make(
                                    binding?.idContainerLayout, this,
                                    CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0
                                )?.show()
                            }
                        }
                    }
            }
        }
    }

    private class ValidationTextWatcher(private val view: EditText) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
           RemoteCompleteActivity().buttonValidation();
        }
    }

    fun buttonValidation() {
        if (binding?.dischargeAssessment?.getText().toString().trim().isNotEmpty() && binding?.dischargePlan?.getText().toString().trim().length > 0
        ) {
            binding?.btnComplete?.setEnabled(true)
        } else {
            binding?.btnComplete?.setEnabled(false)
        }
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }
}