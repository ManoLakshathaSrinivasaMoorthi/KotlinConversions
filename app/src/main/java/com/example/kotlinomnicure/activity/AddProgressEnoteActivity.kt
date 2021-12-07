package com.example.kotlinomnicure.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.example.kotlinomnicure.viewmodel.ChatActivityViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.RemoteHandOffViewModel


import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityAddProgressBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import omnicurekotlin.example.com.providerEndpoints.model.SendChatMessageInputRequestModel
import java.lang.Exception
import java.util.*

class AddProgressEnoteActivity : BaseActivity() {
    private val TAG: String = AddProgressEnoteActivity::class.java.getSimpleName()

    // variables
    private var patientID: Long? = null
    private var patientName: String? = null
    private  var fromScreen:kotlin.String? = null
    private  var patientStatus:kotlin.String? = null
    private var binding: ActivityAddProgressBinding? = null
    private var descriptionText: String? = null
    private var viewModel: RemoteHandOffViewModel? = null
    private var chatViewModel: ChatActivityViewModel? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Databinding and view model intialization
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_progress)
        chatViewModel = ViewModelProvider(this).get(ChatActivityViewModel::class.java)
        viewModel = ViewModelProvider(this).get(RemoteHandOffViewModel::class.java)
        // Initiating the views for the activity
        initViews()
    }

    /**
     * Initiating the views for the activity
     */
    private fun initViews() {
        val vals = ArrayList<String>()
        vals.add(getString(R.string.progress_text))
        val typeAdapter = ArrayAdapter(this, R.layout.spinner_disabled_text, vals)

        binding!!.typeSpinner.setAdapter(typeAdapter)
        binding!!.typeSpinner.setEnabled(false)
        patientID = intent.getLongExtra("patient_id", 0)
        patientName = intent.getStringExtra("patient_name")
        fromScreen = intent.getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)
        patientStatus = intent.getStringExtra("patient_status")
        binding!!.editTextUserName.setText(patientName)

        // Remote hand off click listener
        binding!!.btnRemoteHandoff.setOnClickListener(View.OnClickListener { v ->
            if (validate()) {
                handleMultipleClick(v)
                addProgressEnote()
            }
        })
        binding!!.infoIcon.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            enotesDetailsDialog(this)
        })
        binding!!.imgBack.setOnClickListener(View.OnClickListener { finish() })
        addMandatoryText(binding!!.eConsultText)
        addMandatoryText(binding!!.eNoteText)
        addMandatoryText(binding!!.descriptionText)
        binding!!.descriptionEditText.addTextChangedListener(ValidationTextWatcher(binding!!.descriptionEditText))
        buttonValidation()
    }

    fun enotesDetailsDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.enotes_details_dialog)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val close = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        close.setOnClickListener { // filterdeselect();
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false

    }

    /**
     * Create a new progress using api
     */
    private fun addProgressEnote() {
        showProgressBar()
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        val message = ""
        try {

            // New sendchatMessage API call input request object
            val type = "eNotes"
            val subType = "Progress"
            val providerID: Long =
                PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, -1)
            val sendChatMessageInputRequestModel = SendChatMessageInputRequestModel()
            sendChatMessageInputRequestModel.senderId
            sendChatMessageInputRequestModel.message
            sendChatMessageInputRequestModel.title
            sendChatMessageInputRequestModel.type
            sendChatMessageInputRequestModel.subType
            sendChatMessageInputRequestModel.patientId
            sendChatMessageInputRequestModel.senderName
              //  binding!!.editTextUserName.getText().toString()
           // )
            sendChatMessageInputRequestModel.time

            // Newly added
            sendChatMessageInputRequestModel.id
            sendChatMessageInputRequestModel.token
            sendChatMessageInputRequestModel.providerId
            binding!!.btnRemoteHandoff.setEnabled(false)

            runOnUiThread {
                chatViewModel?.sendChatMessageCall(sendChatMessageInputRequestModel, "")?.observe(this) { sendChatMessageOutuputResponseModel1 ->
                        dismissProgressBar()

                        if (sendChatMessageOutuputResponseModel1?.status != null && sendChatMessageOutuputResponseModel1.status!!) {
                            progressSuccess()
                        } else {
                            binding!!.btnRemoteHandoff.setEnabled(true)
                            val errMsg: String? = ErrorMessages().getErrorMessage(
                                this,
                                sendChatMessageOutuputResponseModel1?.errorMessage,
                                Constants.API.startCall
                            )
                            if (errMsg != null) {
                                CustomSnackBar.make(
                                    binding!!.getRoot(),
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
        } catch (e: Exception) {

        }
    }

    private class ValidationTextWatcher(private val view: EditText) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            AddProgressEnoteActivity().buttonValidation()
        }
    }

    fun buttonValidation() {
        if (binding!!.descriptionEditText.getText().toString().trim().length > 0) {
            binding!!.btnRemoteHandoff.setEnabled(true)
        } else {
            binding!!.btnRemoteHandoff.setEnabled(false)
        }
    }

    /**
     * Validation for description edit text box
     * @return
     */
    private fun validate(): Boolean {
        if (binding!!.descriptionEditText.getText().toString().equals("",ignoreCase = true)) {
            CustomSnackBar.make(
                binding!!.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                resources.getString(R.string.plan_is_mandatory),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return false
        }
        descriptionText = binding!!.descriptionEditText.getText().toString()
        return true
    }

    /**
     * Progress success snack bar handling
     */
    fun progressSuccess() {
        if (fromScreen == "eNotes") {
            val intent = Intent()
            intent.putExtra("screen", "Progress")
            setResult(RESULT_OK, intent)
            CustomSnackBar.make(
                binding!!.idContainerLayout,
                this,
                CustomSnackBar.SUCCESS,
                getString(R.string.progress_success),
                CustomSnackBar.TOP,
                3000,
                8
            )?.show()
        } else {

            patientID?.let { patientName?.let { it1 ->
                patientStatus?.let { it2 ->
                    CustomSnackBar().setPatientDetails(it,
                        it1, it2
                    )
                }
            } }
            CustomSnackBar.make(
                binding!!.idContainerLayout,
                this,
                CustomSnackBar.SUCCESS,
                getString(R.string.progress_success),
                CustomSnackBar.TOP,
                3000,
                9
            )!!.show()
        }
    }
}