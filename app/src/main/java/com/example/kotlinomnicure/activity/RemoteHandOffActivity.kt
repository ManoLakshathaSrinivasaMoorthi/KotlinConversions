package com.example.kotlinomnicure.activity


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
import androidx.lifecycle.ViewModelProviders
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.RemoteHandOffViewModel
import com.example.kotlinomnicure.databinding.ActivityRemoteHandOffBinding
import com.example.kotlinomnicure.utils.CustomSnackBar
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import java.util.ArrayList

class RemoteHandOffActivity :App{
    // Variables
    private var patientID: String? = null
    private var patientName: String? = null
    private var binding: ActivityRemoteHandOffBinding? = null
    private var summaryNote: String? = null
    private var viewModel: RemoteHandOffViewModel? = null
    private var strScreenCensus = ""
    var alertDialog: AlertDialog? = null

    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Databinding and view model intialization
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_hand_off)
        viewModel = ViewModelProvider(this).get(RemoteHandOffViewModel::class.java)
        // Initiating the views for the activity
        initViews()
    }

    /**
     * Initiating the views for the activity
     */
    private fun initViews() {
        val vals = ArrayList<String>()
        vals.add(getString(R.string.hand_off_text))
        val typeAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.spinner_disabled_text, vals)
        //hospitalListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.typeSpinner.setAdapter(typeAdapter)
        binding.typeSpinner.setEnabled(false)
        patientID = java.lang.String.valueOf(getIntent().getLongExtra("patient_id", 0))
        patientName = getIntent().getStringExtra("patient_name")
        strScreenCensus = getIntent().getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)

//        String name = PrefUtility.getStringInPref(this, Constants.SharedPrefConstants.NAME, "");
        binding.editTextUserName.setText(patientName)

        // Remote hand off click listener
        binding.btnRemoteHandoff.setOnClickListener(View.OnClickListener {
            if (validate()) {
                //Performing the Remote HandOff via "performRemoteHandOff" API call ith remote handoff object
                showConfirmationPopup()
            }
        })
        binding.imgBack.setOnClickListener(View.OnClickListener { finish() })
        addMandatoryText(binding.eConsultText)
        addMandatoryText(binding.eNoteText)
        addMandatoryText(binding.planDetailsText)
        binding.etxtRemoteHandoffSummaryNote.addTextChangedListener(ValidationTextWatcher(binding.etxtRemoteHandoffSummaryNote))
        buttonValidation()
    }


    fun showConfirmationPopup() {
        val builder = AlertDialog.Builder(this@RemoteHandOffActivity,
            R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(R.id.content)
        val dialogView: View = LayoutInflater.from(getApplicationContext())
            .inflate(R.layout.alert_custom_dialog, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertTitle.setText(getString(R.string.handoff_confirm_title))
        alertMsg.setText(getString(R.string.handoff_confirm_text))
        val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
        buttonYes.setTextColor(getResources().getColor(R.color.red))
        val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        buttonYes.setOnClickListener { v ->
            handleMultipleClick(v)
            performRemoteHandOff()
            alertDialog!!.dismiss()
        }
        // No click listener
        buttonNo.setOnClickListener { v ->
            handleMultipleClick(v)
            alertDialog!!.dismiss()
        }
        alertDialog!!.show()
    }

    protected fun onPause() {
        super.onPause()
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    /**
     * Performing the Remote HandOff via "performRemoteHandOff" API call ith remote handoff object
     */
    private fun performRemoteHandOff() {
        showProgressBar()
        binding.btnRemoteHandoff.setEnabled(false)
        val uid: Long = PrefUtility.getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0)

        // Input request object for remote handoff
        val remoteHandOffRequest = RemoteHandOffRequest()
        remoteHandOffRequest.setPatientId(patientID)
        remoteHandOffRequest.setSummaryNote(summaryNote)
        remoteHandOffRequest.setRemoteProviderId(uid.toInt())

//        Log.d("REQUEST", new Gson().toJson(remoteHandOffRequest));
        // Triggering the "performRemoteHandOff" API call ith remote handoff object
        viewModel.performRemoteHandOff(remoteHandOffRequest).observe(this) { commonResponse ->
            dismissProgressBar()
            if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                //Handoff success snack bar handling
                handOffInitiateSuccess(commonResponse)
            } else {
                binding.btnRemoteHandoff.setEnabled(true)
                val errMsg: String = ErrorMessages.getErrorMessage(this@RemoteHandOffActivity,
                    commonResponse.getErrorMessage(),
                    Constants.API.register)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(binding.idContainerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0).show()
            }
        }
    }

    private class ValidationTextWatcher private constructor(private val view: EditText) :
        TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            buttonValidation()
        }
    }

    fun buttonValidation() {
        if (binding.etxtRemoteHandoffSummaryNote.getText().toString().trim().length() > 0) {
            binding.btnRemoteHandoff.setEnabled(true)
        } else {
            binding.btnRemoteHandoff.setEnabled(false)
        }
    }

    /**
     * Validation for summary note edit text box
     * @return
     */
    private fun validate(): Boolean {
        if (binding.etxtRemoteHandoffSummaryNote.getText().toString().equalsIgnoreCase("")) {
            CustomSnackBar.make(binding.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                TextUtils(R.string.plan_is_mandatory),
                CustomSnackBar.TOP,
                3000,
                0).show()
            return false
        }
        summaryNote = binding.etxtRemoteHandoffSummaryNote.getText().toString()
        return true
    }

    /**
     * Handoff success snack bar handling
     * @param response
     */
    fun handOffInitiateSuccess(response: CommonResponse?) {
        if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(Constants.IntentKeyConstants.SCREEN_CENSUS,
                ignoreCase = true)
        ) {
            CustomSnackBar.make(binding.idContainerLayout,
                this,
                CustomSnackBar.SUCCESS,
                getString(R.string.Handoff_patient_successfully_rp),
                CustomSnackBar.TOP,
                3000,
                4).show()
        } else {
            CustomSnackBar.make(binding.idContainerLayout,
                this,
                CustomSnackBar.SUCCESS,
                getString(R.string.Handoff_patient_successfully_rp),
                CustomSnackBar.TOP,
                3000,
                1).show()
        }
    }
}
