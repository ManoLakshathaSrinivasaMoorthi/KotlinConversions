package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.AthenaDeviceListAdapter
import com.example.kotlinomnicure.databinding.ActivityAddPatientVitalsBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.AddPatientViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.patientsEndpoints.model.AthenaDeviceData
import omnicurekotlin.example.com.patientsEndpoints.model.AthenaDeviceListResponse
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.Patient
import java.util.*

class AddPatientVitalsActivity : BaseActivity() {

    private val TAG = AddPatientVitalsActivity::class.java.simpleName
    private var binding: ActivityAddPatientVitalsBinding? = null
    private var viewModel: AddPatientViewModel? = null
    private var mDocBoxManagerId: String? = null
    private var mDocBoxId: Long? = null
    private var mDocBoxPatientId: String? = null
    private val phnNumber: String? = null
    private val mAppointmentId: Long? = null
    private var mAthenaDeviceId: String? = null
    private var strScreenCensus: String? = ""
    val onCheckedChangeListener=  View.OnClickListener { v ->
        val selectedId = v.id
        if (selectedId == R.id.radioBtnAlert || selectedId == R.id.radioBtnPain) {
            binding?.radioGrpAvpu2?.clearCheck()
        } else if (selectedId == R.id.radioBtnVoice || selectedId == R.id.radioBtnUnresponsive) {
            binding?.radioGrpAvpu1?.clearCheck()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_patient_vitals)
        viewModel = ViewModelProvider(this).get(AddPatientViewModel::class.java)
        initToolbar()
        setView()
    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        binding?.toolbar?.title = getString(R.string.add_new_patient_information)
        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.patient_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.patientList -> //                onClickAddButton();
                true
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setView() {
//        binding.idDob.setOnClickListener(view -> selectDOB());
        binding?.idSave?.setOnClickListener { view -> onClickSaveButton() }
        strScreenCensus = intent.getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)
        val prefix = "<b>" + getString(R.string.chief_complaint) + "</b>"
        binding?.idNotes?.setText(Html.fromHtml(prefix))
        binding?.idNotes?.text?.length?.let { Selection.setSelection(binding?.idNotes?.text, it) }
        binding?.idNotes?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith(Html.fromHtml(prefix).toString())) {
                    binding?.idNotes?.setText(Html.fromHtml(prefix))
                    binding?.idNotes?.text?.length?.let {
                        Selection.setSelection(binding?.idNotes?.text, it)
                    }
                }
            }
        })
        binding?.idNotes?.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                onClickSaveButton()
            }
            false
        }
        binding?.addPatientVitalsCheckbox?.setOnClickListener {
            if (binding?.addPatientVitalsCheckbox?.isChecked == true) {
                binding?.addPatientVitalsContainer?.visibility = View.VISIBLE
            } else {
                binding?.addPatientVitalsContainer?.visibility = View.GONE
            }
        }
        binding?.radioBtnAlert?.setOnClickListener(onCheckedChangeListener)
        binding?.radioBtnPain?.setOnClickListener(onCheckedChangeListener)
        binding?.radioBtnVoice?.setOnClickListener(onCheckedChangeListener)
        binding?.radioBtnUnresponsive?.setOnClickListener(onCheckedChangeListener)
        binding?.connectToDeviceBtn?.setOnClickListener {
            Log.i(TAG, "onClick of connect to device ")
            mAthenaDeviceId = null
            binding?.athenaDeviceTv?.text = getString(R.string.connect_to_a_device)
            onClickFetchAthena()
        }
        binding?.idNotes?.setOnTouchListener { v, event ->
            if (v.id == R.id.id_notes) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    private fun onClickFetchAthena() {
        showProgressBar(getString(R.string.fetching_athena_list_pb_msg))
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        viewModel?.getAthenaDevicetList(providerID, token)?.observe(this, {
            val response=it.body()
            dismissProgressBar()
            binding?.idSave?.isEnabled = true
            if (response != null && response.getStatus() != null && response.getStatus()!!) {
                showAthenaList(response)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this, response?.getErrorMessage(), Constants.API.getAthenaDeviceList)
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let {
                    CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING, it,
                        CustomSnackBar.TOP, 3000, 0)?.show()
                }
            }
        })
    }

    private fun showAthenaList(response: AthenaDeviceListResponse) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.doc_box_patient_list)
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        dialog.window!!.setLayout(7 * width / 8, ViewGroup.LayoutParams.WRAP_CONTENT)
        val patientListView = dialog.findViewById<View>(R.id.patient_list_view) as ListView
        val titleView = dialog.findViewById<View>(R.id.select_patient) as TextView
        titleView.text = getString(R.string.select_athena_device)
        Collections.sort(response.getAthenaDeviceDataList(), object : Comparator<AthenaDeviceData?> {
            override fun compare(lhs: AthenaDeviceData?, rhs: AthenaDeviceData?): Int {
                if (lhs?.getUpdateTime() == null) {
                    return 1
                }
                return if (rhs?.getUpdateTime() == null) {
                    -1
                } else lhs.getUpdateTime()!!.compareTo(rhs.getUpdateTime()!!)
                //  return 1 if rhs should be before lhs
                //  return -1 if lhs should be before rhs
                //  return 0 otherwise (meaning the order stays the same)

            }
        })
        val athenaDeviceListAdapter =
            response.getAthenaDeviceDataList()?.let { AthenaDeviceListAdapter(this, R.layout.doc_box_patient_list,
                    it as List<AthenaDeviceData>
                )
            }
        patientListView.adapter = athenaDeviceListAdapter
        dialog.show()
        patientListView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val docBoxPatient = response.getAthenaDeviceDataList()!![position]
                if (docBoxPatient != null) {
                    mDocBoxManagerId = null
                    mDocBoxPatientId = null
                    mDocBoxId = null
                    mAthenaDeviceId = response.getAthenaDeviceDataList()!![position]!!.getHardwareId()
                    binding?.athenaDeviceTv?.text = "" + response.getAthenaDeviceDataList()!![position]!!.getDeviceID1()
                    dialog.dismiss()
                } else {
                    //                    UtilityMethods.showErrorSnackBar(binding.getRoot(), getString(R.string.device_details_not_found), Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        binding?.root, this, CustomSnackBar.WARNING, getString(R.string.device_details_not_found),
                        CustomSnackBar.TOP, 3000, 0)?.show()
                }
            }
    }

    private fun createPatientObject(): Patient {
        val providerID: Long? = PrefUtility().getProviderId(this)
        val role: String? = PrefUtility().getRole(this)
        val extras = intent.extras
        val patient = Patient()
        if (extras != null) {
            patient.setFname(extras.getString(Constants.IntentKeyConstants.FIRST_NAME))
            patient.setLname(extras.getString(Constants.IntentKeyConstants.LAST_NAME))
            patient.setDob(extras.getLong(Constants.IntentKeyConstants.DOB))
            patient.setGender(extras.getString(Constants.IntentKeyConstants.GENDER))
            patient.setWardName(extras.getString(Constants.IntentKeyConstants.WARD))
            if (extras.getString(Constants.IntentKeyConstants.PHONE_NO) != null) {
                patient.setPhone(extras.getString(Constants.IntentKeyConstants.PHONE_NO))
            }
            if (extras.getLong(Constants.IntentKeyConstants.DOC_BOX_ID) != 0L) {
                mDocBoxId = extras.getLong(Constants.IntentKeyConstants.DOC_BOX_ID)
            }
            if (extras.getString(Constants.IntentKeyConstants.DOCBOX_MANAGER_ID) != null) {
                mDocBoxManagerId = extras.getString(Constants.IntentKeyConstants.DOCBOX_MANAGER_ID)
            }
            if (extras.getString(Constants.IntentKeyConstants.DOCBOX_ID) != null) {
                mDocBoxPatientId = extras.getString(Constants.IntentKeyConstants.DOCBOX_ID)
            }
            if (!binding?.idHeartRate?.text.toString().contentEquals("")) {
                patient.setHeartRate(java.lang.Double.valueOf(binding?.idHeartRate?.text.toString()))
            }
            if (!binding?.idBloodPressureSystolic?.text.toString().contentEquals("")) {
                patient.setArterialBloodPressureSystolic(java.lang.Double.valueOf(binding?.idBloodPressureSystolic?.text.toString()))
            }
            if (!binding?.idBloodPressureDiastolic?.text.toString().contentEquals("")) {
                patient.setArterialBloodPressureDiastolic(java.lang.Double.valueOf(binding?.idBloodPressureDiastolic?.text.toString()))
            }
            if (!binding?.idSp02?.text.toString().contentEquals("")) {
                patient.setSpO2(java.lang.Double.valueOf(binding?.idSp02?.text.toString()))
            }
            if (!binding?.idFi02?.text.toString()?.contentEquals("")) {
                patient.setFio2(java.lang.Double.valueOf(binding?.idFi02?.text.toString()))
            }
            if (!binding?.idRespitoryRate?.text.toString().contentEquals("")) {
                patient.setRespiratoryRate(java.lang.Double.valueOf(binding?.idRespitoryRate?.text.toString()))
            }
            if (!binding?.idTemperature?.text.toString().contentEquals("")) {
                patient.setTemperature(java.lang.Double.valueOf(binding?.idTemperature?.text.toString()))
            }
            if (patient.getHeartRate() != null || patient.getArterialBloodPressureSystolic() != null || patient.getArterialBloodPressureDiastolic() != null || patient.getSpO2() != null || patient.getFio2() != null || patient.getRespiratoryRate() != null || patient.getTemperature() != null) {
                val time = System.currentTimeMillis()
                patient.setSyncTime(time)
            }
        }
        val providerName: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        if (role.equals(Constants.ProviderRole.BD.toString(), ignoreCase = true)) {
            patient.setBdProviderId(providerID)
            if (!TextUtils.isEmpty(providerName)) {
                patient.setBdProviderName(providerName)
            }
        } else if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            patient.setRdProviderId(providerID)
            if (!TextUtils.isEmpty(providerName)) {
                patient.setRdProviderName(providerName)
            }
        }
        if (binding?.radioBtnNo?.isChecked == true) {
            patient.setOxygenSupplement(false)
        } else if (binding?.radioBtnYes?.isChecked == true) {
            patient.setOxygenSupplement(true)
        }
        if (binding?.radioCovidYes?.isChecked == true) {
            patient.setCovidPositive(Constants.CovidPositive.Yes.toString())
        } else if (binding?.radioCovidNo?.isChecked == true) {
            patient.setCovidPositive(Constants.CovidPositive.No.toString())
        } else if (binding?.radioCovidPending?.isChecked == true) {
            patient.setCovidPositive(Constants.CovidPositive.Pending.toString())
        }
        if (binding?.radioBtnAlert?.isChecked == true) {
            patient.setPatientCondition(Constants.PatientCondition.Alert.toString())
        } else if (binding?.radioBtnVoice?.isChecked == true) {
            patient.setPatientCondition(Constants.PatientCondition.Voice.toString())
        } else if (binding?.radioBtnPain?.isChecked == true) {
            patient.setPatientCondition(Constants.PatientCondition.Pain.toString())
        } else if (binding?.radioBtnUnresponsive?.isChecked == true) {
            patient.setPatientCondition(Constants.PatientCondition.Unresponsive.toString())
        }
        if (binding?.urgentCheckBox?.isChecked == true) {
            patient.setUrgent(true)
        } else {
            patient.setUrgent(false)
        }
        patient.setNote(binding?.idNotes?.text.toString().trim())
        patient.setHospital(PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.HOSPITAL_NAME, null))
        patient.setHospitalId(PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.HOSPITAL_ID, 0L))
        patient.setDocBoxManagerId(mDocBoxManagerId)
        patient.setDocBoxPatientId(mDocBoxPatientId)
        patient.setAthenaDeviceId(mAthenaDeviceId)
        patient.setAppointmentId(mAppointmentId)
        if (mDocBoxId != null) {
            patient.setDocBoxId(java.lang.Long.valueOf(mDocBoxId!!))
        }
        Log.d(TAG, "PATIENT DETAILS : " + Gson().toJson(patient))
        return patient
    }

    private fun onClickSaveButton() {
        binding?.idSave?.isEnabled = false
        //        handleMultipleClick(binding.idSave);
        if (!isValid()) {
            binding?.idSave?.isEnabled = true
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP, 3000, 0)?.show()
            return
        }
        binding?.idSave?.isEnabled = false
        showProgressBar(PBMessageHelper().getMessage(this, Constants.API.addPatient.toString()))
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        val patient = createPatientObject()
        Log.i(TAG, "add patient params $patient")
        providerID?.let {
            token?.let { it1 -> viewModel?.addNewPatient(it, it1, patient)?.observe(this, {
                val commonResponse=it.body()
                    Log.d(TAG, "AddPatient Response$commonResponse")
                    dismissProgressBar()
                    if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()!!) {
                        onAddPatientSuccess(commonResponse)
                    } else if (!TextUtils.isEmpty(commonResponse?.getErrorMessage()) && commonResponse?.getErrorMessage() != null) {
                        binding?.idSave?.isEnabled = true
                        val errMsg: String? = ErrorMessages().getErrorMessage(this, commonResponse.getErrorMessage(), Constants.API.register)
                        //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                        onAddPatientFailure(errMsg)
                    } else {
                        CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(R.string.api_error),
                            CustomSnackBar.TOP, 3000, 0)?.show()
                    }
                })
            }
        }
    }

    fun onAddPatientSuccess(response: CommonResponse?) {
        if (!TextUtils.isEmpty(strScreenCensus) && strScreenCensus.equals(Constants.IntentKeyConstants.SCREEN_CENSUS, ignoreCase = true)) {
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.SUCCESS,
                getString(R.string.patient_added_successfully), CustomSnackBar.TOP, 3000, 4)?.show()
        } else {
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.SUCCESS,
                getString(R.string.patient_added_successfully), CustomSnackBar.TOP, 3000, 1)?.show()
        }
    }

    fun onAddPatientFailure(strErrMessage: String?) {
        CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING, strErrMessage!!,
            CustomSnackBar.TOP, 3000, 0)?.show()
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {
//            UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
            errMsg?.let {
                CustomSnackBar.make(binding?.idContainerLayout, this,
                    CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0) }?.show()
            return false
        }
        return true
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler!!.postDelayed({ view.isEnabled = true }, 500)
    }

}
