package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.AppointmentListAdapter
import com.example.kotlinomnicure.adapter.AthenaDeviceListAdapter
import com.example.kotlinomnicure.adapter.DocBoxPatientListAdapter
import com.example.kotlinomnicure.databinding.ActivityAddPatientBinding
import com.example.kotlinomnicure.interfaces.OnAppointmentItemClick
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.AddPatientViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import omnicurekotlin.example.com.appointmentEndpoints.model.AppointmentListResponse
import omnicurekotlin.example.com.hospitalEndpoints.model.AddNewPatientWard
import omnicurekotlin.example.com.patientsEndpoints.model.*
import java.util.*

class AddPatientActivity : BaseActivity() {
    // Variables
    private val TAG = AddPatientActivity::class.java.simpleName
    private var binding:ActivityAddPatientBinding?= null
    private var viewModel: AddPatientViewModel? = null
    private var mDocBoxManagerId: String? = null
    private var mDocBoxId: Long? = null
    private var mDocBoxPatientId: String? = null
    private var mAppointmentId: Long? = null
    private var mAthenaDeviceId: String? = null
    private var mAppointment: Appointment? = null
    private var wardListAdapter: ArrayAdapter<String>? = null
    private var phnNumber: String? = null
    private var strScreenCensus: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Databinding and view model initialization
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_patient)
        viewModel = ViewModelProvider(this).get(AddPatientViewModel::class.java)
        // Setting the tool bar for the activity
        initToolbar()
        // Setting the view for the activity
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
        strScreenCensus = intent.getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.patient_list_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.patientList -> {
                // On clicking the patient add
                onClickAddButton()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setView() {
        // DOB click listener
        binding?.idDob?.setOnClickListener { selectDOB() }
        // Save click listener
        binding?.idSave?.setOnClickListener { onClickSaveButton() }

        //Setting the hospital name
        val hospitalName: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.HOSPITAL_NAME, "")
        binding?.idHospitalLocation?.text = hospitalName
        // Setting the ward spinner
        setWardSpinner()

    }


    private fun selectDOB() {
        binding?.idDob?.isEnabled = false
        val currentDate = Calendar.getInstance()
        var mYear = currentDate[Calendar.YEAR]
        var mMonth = currentDate[Calendar.MONTH]
        var mDay = currentDate[Calendar.DAY_OF_MONTH]
        if (binding?.idDob?.tag != null && binding?.idDob?.tag.toString().isNotEmpty()) {
            val dob: String = binding?.idDob?.tag.toString()
            val dobArr = dob.split("/".toRegex()).toTypedArray()
            mMonth = dobArr[0].toInt() - 1
            mDay = dobArr[1].toInt()
            mYear = dobArr[2].toInt()
        }
        // DatePickerDialog
        val mDatePicker = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog,
            { _, selectedyear, selectedmonth, selectedday ->
                val selectedDate = Calendar.getInstance()
                selectedDate[selectedyear, selectedmonth] = selectedday
                binding?.idDob?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_calendar_blue, 0)
                binding?.idDob?.setText((selectedmonth + 1).toString() + "/" + selectedday + "/" + selectedyear)
                binding?.idDob?.tag = (selectedmonth + 1).toString() + "/" + selectedday + "/" + selectedyear
                binding?.idDob?.isEnabled = true
            }, mYear, mMonth, mDay
        )
        mDatePicker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDatePicker.datePicker.maxDate = System.currentTimeMillis()
        mDatePicker.show()

        // Date picker click listener
        mDatePicker.setOnDismissListener { binding?.idDob?.isEnabled = true }
    }


    private fun onClickAddButton() {
        val hospital: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.HOSPITAL_NAME, "")
        Log.d(TAG, "showDocBoxPatientList hospital : $hospital")
        if (hospital != null && hospital.equals(Constants.HOSPITAL_TYPE_HOME, ignoreCase = true)) {
            // Fetching the appointment list data
            fetchAppointmentList()
        } else {
            // Fetching the patient from the doc box
            fetchPatientFromDocBox()
        }
    }


    private fun fetchPatientFromDocBox() {
        showProgressBar(getString(R.string.fetching_patient_list_pb_msg))
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(this,
            Constants.SharedPrefConstants.TOKEN, "")

        // "getDocBoxPatientList" API call
        viewModel?.getDocBoxPatientList(providerID, token)?.observe(this, { it ->
            val response=it.body()
            Log.d(TAG, "showDocBoxPatientList : " + Gson().toJson(response))
            dismissProgressBar()
            binding?.idSave?.isEnabled = true
            if (response?.getStatus() != null && response.getStatus()!!) {
                // Parsing the doc box patient list with the retrieved response
                showDocBoxPatientList(response)
            } else if (!TextUtils.isEmpty(response?.getErrorMessage()) && response?.getErrorMessage() != null) {
                val errMsg: String? = ErrorMessages().getErrorMessage(this, response.getErrorMessage(),
                    Constants.API.getDocBoxPatientList)
                Log.d(TAG, "showDocBoxPatientList errMsg : $errMsg")
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                errMsg?.let {
                    CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING, it,
                        CustomSnackBar.TOP, 3000, 0)?.show()
                }
            } else {
                CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(
                        R.string.api_error
                    ),
                    CustomSnackBar.TOP, 3000, 0)?.show()
            }
        })
    }


    private fun fetchAppointmentList() {
        showProgressBar(getString(R.string.fetching_patients_from_appointment_list))
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TOKEN, "")
        // "getAppointmentList" API call
        providerID?.let { token?.let { it1 -> viewModel?.getAppointmentList(it, it1, 0, 50) } }?.observe(this,
            { it ->
                val response=it.body()
                dismissProgressBar()
                binding?.idSave?.isEnabled = true
                if (response?.getStatus() != null && response.getStatus()!!) {
                    showAppointmentList(response)
                } else {
                    val errMsg: String? = ErrorMessages().getErrorMessage(this, response?.getErrorMessage(),
                        Constants.API.getAppointmentList)

                    errMsg?.let {
                        CustomSnackBar.make(binding?.idContainerLayout, this,
                            CustomSnackBar.WARNING, it, CustomSnackBar.TOP, 3000, 0)?.show()
                    }
                }
            })
    }


    private fun showDocBoxPatientList(response: DocBoxPatientListResponse) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.doc_box_patient_list)
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        //        dialog.getWindow().setLayout((7 * width) / 8, ViewGroup.LayoutParams.WRAP_CONTENT);
        val patientListView = dialog.findViewById<View>(R.id.patient_list_view) as ListView
        val imgBack = dialog.findViewById<ImageView>(R.id.imgBack)
        imgBack.setOnClickListener { dialog.dismiss() }
        // Setting up the doc box patient list adapter
        val docBoxPatientListAdapter = DocBoxPatientListAdapter(this, R.layout.doc_box_patient_list,
            response.getDocBoxPatientList() as List<DocBoxPatient>)
        patientListView.adapter = docBoxPatientListAdapter
        dialog.show()
        patientListView.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                val docBoxPatient = response.getDocBoxPatientList()!![position]
                if (docBoxPatient != null) {
                    val firstName = response.getDocBoxPatientList()!![position]!!.getFname()
                    val lastName = response.getDocBoxPatientList()!![position]!!.getLname()
                    if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
                        binding?.idFirstName?.setText(response.getDocBoxPatientList()!![position]!!.getName())
                    } else {
                        binding?.idFirstName?.setText(response.getDocBoxPatientList()!![position]!!.getFname())
                        binding?.idLastName?.setText(response.getDocBoxPatientList()!![position]!!.getLname())
                    }
                    if (response.getDocBoxPatientList()!![position]!!.getDob() != null) {
                        val dobStr: String? = response.getDocBoxPatientList()!![position]!!.getDob()?.let {
                                ChatUtils().getDateFormat(it, "MM/dd/yyyy")
                            }
                        binding?.idDob?.setText(dobStr)
                        binding?.idDob?.tag = dobStr
                    }
                    if (!TextUtils.isEmpty(response.getDocBoxPatientList()!![position]!!.getGender())) {
                        if (response.getDocBoxPatientList()!![position]!!.getGender()
                                ?.contentEquals("Male") == true
                        ) {
                            binding?.radioBtnMale?.isChecked = true
                            binding?.radioBtnFemale?.isChecked = false
                        } else {
                            binding?.radioBtnFemale?.isChecked = true
                            binding?.radioBtnMale?.isChecked = false
                        }
                    }
                    if (!TextUtils.isEmpty(response.getDocBoxPatientList()!![position]!!.getHospital())) {
                        binding!!.idHospitalLocation.text = response.getDocBoxPatientList()!![position]!!.getHospital()
                    }
                    if (!TextUtils.isEmpty(response.getDocBoxPatientList()!![position]!!.getWard())) {
                        if (wardListAdapter != null) {
                            wardListAdapter?.getPosition(response.getDocBoxPatientList()!![position]!!.getWard())?.let {
                                binding?.idSpinnerWard?.setSelection(it)
                            }
                        }
                    }

                    /*if(response.getDocBoxPatientList().get(position).getPhone()!=null){
                            binding.idPhoneNumber.setText(response.getDocBoxPatientList().get(position).getPhone());
                       }*/
                    //                    if (response.getDocBoxPatientList().get(position).getNote() != null) {
                    //                        String str = "<b>" + getString(R.string.chief_complaint) + "</b>" + " " + response.getDocBoxPatientList().get(position).getNote();
                    //                        binding.idNotes.setText(Html.fromHtml(str));
                    //                    }
                    if (!TextUtils.isEmpty(response.getDocBoxPatientList()!![position]!!.getDocBoxManagerId())) {
                        mDocBoxManagerId =
                            response.getDocBoxPatientList()!![position]!!.getDocBoxManagerId()
                    }
                    if (response.getDocBoxPatientList()!![position]!!.getId() != null) {
                        mDocBoxId = response.getDocBoxPatientList()!![position]!!.getId()
                    }
                    if (!TextUtils.isEmpty(response.getDocBoxPatientList()!![position]!!.getDocBoxPatientId())) {
                        mDocBoxPatientId =
                            response.getDocBoxPatientList()!![position]!!.getDocBoxPatientId()
                    }
                    if (!TextUtils.isEmpty(response.getDocBoxPatientList()!![position]!!.getPhone())) {
                        phnNumber = response.getDocBoxPatientList()!![position]!!.getPhone()
                    }
                    mAppointmentId = null
                    mAthenaDeviceId = null
                    dialog.dismiss()
                } else if (!TextUtils.isEmpty(response.getErrorMessage()) && response.getErrorMessage() != null) {
                    CustomSnackBar.make(
                        binding!!.root, this@AddPatientActivity, CustomSnackBar.WARNING,
                        response.getErrorMessage()!!, CustomSnackBar.TOP, 3000, 0
                    )!!.show()
                    //                    CustomSnackBar.make(binding.getRoot(), AddPatientActivity.this, CustomSnackBar.WARNING, getString(R.string.patient_details_not_found), CustomSnackBar.TOP, 3000, 0).show();
                } else {
                    CustomSnackBar.make(
                        binding!!.root,
                        this@AddPatientActivity,
                        CustomSnackBar.WARNING,
                        getString(R.string.api_error),
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )!!.show()
                }
            }
    }



    private fun showAppointmentList(response: AppointmentListResponse?) {
        if (response == null || response.getCount()==0) {
//            UtilityMethods.showErrorSnackBar(binding.getRoot(), getString(R.string.appointment_details_not_found), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.root, this@AddPatientActivity, CustomSnackBar.WARNING, getString(
                    R.string.appointment_details_not_found
                ), CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_appointment_list)
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //        dialog.getWindow().setLayout((7 * width) / 8, ViewGroup.LayoutParams.WRAP_CONTENT);
        val recyclerView: RecyclerView = dialog.findViewById(R.id.patient_list_view)
        val imgBack = dialog.findViewById<ImageView>(R.id.imgBack)
        imgBack.setOnClickListener { dialog.dismiss() }
        val onAppointmentClick: OnAppointmentItemClick = object : OnAppointmentItemClick {
            override fun onClickAppointment(appointment: Appointment?) {
                if (appointment != null) {
                    mAppointment = appointment
                    val firstName: String? = appointment.getFname()
                    val lastName: String? = appointment.getLname()
                    if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
                        binding?.idFirstName?.setText(appointment.getName())
                    } else {
                        binding?.idFirstName?.setText(appointment.getFname())
                        binding?.idLastName?.setText(appointment.getLname())
                    }
                    if (appointment.getDob() != null) {
                        val dobStr: String? = ChatUtils().getDateFormat(
                            appointment.getDob()!!,
                            "MM/dd/yyyy"
                        )
                        binding?.idDob?.setText(dobStr)
                    }
                    if (!TextUtils.isEmpty(appointment.getGender())) {
                        if (appointment.getGender()?.contentEquals("Male") == true) {
                            binding?.radioBtnMale?.isChecked = true
                            binding?.radioBtnFemale?.isChecked = false
                        } else {
                            binding?.radioBtnFemale?.isChecked = true
                            binding?.radioBtnMale?.isChecked = false
                        }
                    }

                    mAppointmentId = appointment.getId()
                    mDocBoxPatientId = null
                    mAthenaDeviceId = null
                    dialog.dismiss()
                } else {
//                    UtilityMethods.showErrorSnackBar(binding.getRoot(), getString(R.string.appointment_details_not_found), Snack.LENGTH_LONG);
                    CustomSnackBar.make(
                        binding?.root, this@AddPatientActivity, CustomSnackBar.WARNING, getString(
                            R.string.appointment_details_not_found
                        ), CustomSnackBar.TOP, 3000, 0
                    )?.show()
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(dialog.context)
        val docBoxPatientListAdapter = AppointmentListAdapter(
            this@AddPatientActivity,
            response.getAppointmentList() as List<Appointment>,
            onAppointmentClick
        )
        recyclerView.adapter = docBoxPatientListAdapter
        dialog.show()
    }

    private fun onClickFetchAthena() {
        showProgressBar(getString(R.string.fetching_athena_list_pb_msg))
        val providerID: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.TOKEN,
            ""
        )
        viewModel?.getAthenaDevicetList(providerID, token)?.observe(this, { it ->
            val response=it.body()
            dismissProgressBar()
            binding?.idSave?.isEnabled = true
            if (response?.getStatus() != null && response.getStatus()!!) {
                showAthenaList(response)
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this@AddPatientActivity,
                    response?.getErrorMessage(),
                    Constants.API.getAthenaDeviceList
                )
                //                UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snack.LENGTH_LONG);
                errMsg?.let {
                    CustomSnackBar.make(
                        binding?.idContainerLayout,
                        this@AddPatientActivity,
                        CustomSnackBar.WARNING,
                        it,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
        })
    }

    /**
     * Displaying the Athena device list
     * @param response
     */
    private fun showAthenaList(response: AthenaDeviceListResponse) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.doc_box_patient_list)
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //        dialog.getWindow().setLayout((7 * width) / 8, ViewGroup.LayoutParams.WRAP_CONTENT);
        val patientListView = dialog.findViewById<View>(R.id.patient_list_view) as ListView
        val titleView = dialog.findViewById<View>(R.id.select_patient) as TextView
        titleView.text = getString(R.string.select_athena_device)
        Collections.sort(
            response.getAthenaDeviceDataList(),
            object : Comparator<AthenaDeviceData?> {


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
        val athenaDeviceListAdapter = AthenaDeviceListAdapter(this, R.layout.doc_box_patient_list, response.getAthenaDeviceDataList() as List<AthenaDeviceData>)
       patientListView.adapter = athenaDeviceListAdapter
        dialog.show()
        patientListView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            val docBoxPatient = response.getAthenaDeviceDataList()!![position]
            if (docBoxPatient != null) {
                mDocBoxManagerId = null
                mDocBoxPatientId = null
                mAthenaDeviceId = response.getAthenaDeviceDataList()!![position]!!.getHardwareId()
                //                    binding.athenaDeviceTv.setText("" + response.getAthenaDeviceDataList().get(position).getDeviceID1());
                dialog.dismiss()
            } else {
//                    UtilityMethods.showErrorSnackBar(binding.getRoot(), getString(R.string.device_details_not_found), Snack.LENGTH_LONG);
                CustomSnackBar.make(
                    binding!!.root, this@AddPatientActivity, CustomSnackBar.WARNING, getString(
                        R.string.device_details_not_found
                    ), CustomSnackBar.TOP, 3000, 0
                )!!.show()
            }
        }
    }

    /**
     * Creating the patient object
     * @return
     */
    private fun createPatientObject(): Patient {
        val providerID: Long? = PrefUtility().getProviderId(this)
        val patient = Patient()
        patient.setFname(binding?.idFirstName?.text.toString().trim())
        patient.setLname(binding?.idLastName?.text.toString().trim())
        patient.setDob(getDOBLongFormat(binding?.idDob?.text.toString()))
        val dob: List<String> = binding?.idDob?.text.toString().split("/")
        val month: String? = UtilityMethods().getMonthName(dob[0].toInt())
        val day = dob[1].toInt()
        val year = dob[2].toInt()
        patient.setDobDay(day)
        patient.setDobMonth(month)
        patient.setDobYear(year)
        Log.d(TAG, "onClickSaveButton: selected date : month : $month, Year : $year, day : $day")
        val role: String? = PrefUtility().getRole(this)
        val providerName: String? = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.NAME,
            ""
        )
        patient.setLocation(binding?.idHospitalLocation?.text.toString())
        if (binding?.idSpinnerWard?.selectedItemPosition!! > 0) {
            patient.setWardName(binding?.idSpinnerWard!!.selectedItem.toString())
        }

        if (binding?.radioBtnMale?.isChecked == true) {
            patient.setGender(Constants.Gender.Male.toString())
        } else if (binding?.radioBtnFemale?.isChecked == true) {
            patient.setGender(Constants.Gender.Female.toString())
        }
        patient.setHospital(
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.HOSPITAL_NAME, null)
        )
        patient.setHospitalId(
            PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.HOSPITAL_ID, 0L)
        )
        patient.setPhone(phnNumber)
        patient.setDocBoxManagerId(mDocBoxManagerId)
        if (mDocBoxId != null) {
            patient.setDocBoxId(mDocBoxId)
        }
        patient.setDocBoxPatientId(mDocBoxPatientId)
        patient.setAthenaDeviceId(mAthenaDeviceId)
        if (mAppointment != null) {
            if (!TextUtils.isEmpty(mAppointment?.getPhone())) {
                patient.setPhone(mAppointment?.getPhone())
            }
            if (!TextUtils.isEmpty(mAppointment?.getEmail())) {
                patient.setEmail(mAppointment?.getEmail())
            }
        }
        patient.setAppointmentId(mAppointmentId)
        return patient
    }

    private fun onClickSaveButton() {
        binding?.let { handleMultipleClick(it.idSave) }
        if (!isValid()) {
            return
        }
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snack.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(
                    R.string.no_internet_connectivity
                ), CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        val patient: Patient = createPatientObject()
        if (System.currentTimeMillis() < patient.getDob()!! || patient.getDob()!! <= -3155692870000L) {
//            UtilityMethods.showErrorSnackBar(binding.idContainerLayout, getString(R.string.invalid_date), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(
                    R.string.invalid_date
                ), CustomSnackBar.TOP, 3000, 0
            )?.show()

//            binding.idDob.setText("");
            return
        }

        // Directing the "AddPatientVitals" activity with required data
        val intentVital = Intent(this@AddPatientActivity, AddPatientVitalsActivity::class.java)
        val bundle = Bundle()
        bundle.putString(Constants.IntentKeyConstants.FIRST_NAME, patient.getFname())
        bundle.putString(Constants.IntentKeyConstants.LAST_NAME, patient.getLname())
        bundle.putLong(Constants.IntentKeyConstants.DOB, patient.getDob()!!)
        bundle.putString(Constants.IntentKeyConstants.GENDER, patient.getGender())
        bundle.putString(Constants.IntentKeyConstants.WARD, patient.getWardName())
        bundle.putString(Constants.IntentKeyConstants.DOCBOX_ID, patient.getDocBoxPatientId())
        bundle.putString(
            Constants.IntentKeyConstants.DOCBOX_MANAGER_ID,
            patient.getDocBoxManagerId()
        )
        bundle.putString(Constants.IntentKeyConstants.PHONE_NO, patient.getPhone())
        bundle.putString(Constants.IntentKeyConstants.DOCBOX_ID, patient.getDocBoxPatientId())
        if (patient.getDocBoxId() != null) {
            bundle.putLong(Constants.IntentKeyConstants.DOC_BOX_ID, patient.getDocBoxId()!!)
        }
        intentVital.putExtras(bundle)
        intentVital.putExtra(Constants.IntentKeyConstants.SCREEN_TYPE, strScreenCensus)
        startActivityForResult(intentVital, 201, bundle)
    }

    /**
     * Checking the error message and displaying the snack
     * @return
     */
    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!TextUtils.isEmpty(errMsg)) {
//            UtilityMethods.showErrorSnackBar(binding.idContainerLayout, errMsg, Snack.LENGTH_LONG);
            errMsg?.let { CustomSnackBar.make(
                binding?.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                it,
                CustomSnackBar.TOP,
                3000,
                0
            )?.show() }
            return false
        }
        return true
    }

    /**
     * Getting the DOB format
     * @param date
     * @return
     */
    private fun getDOBLongFormat(date: String): Long {
        val d = date.split("/".toRegex()).toTypedArray()
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.clear()
        calendar[Calendar.DAY_OF_MONTH] = d[1].toInt()
        calendar[Calendar.MONTH] = d[0].toInt() - 1
        calendar[Calendar.YEAR] = d[2].toInt()
        calendar[Calendar.HOUR_OF_DAY] = 12
        val formatDate = calendar.time
        Log.i(TAG, "getDateInLongFormat: " + formatDate + " " + d[1].toInt())
        Log.i(TAG, "getDateInLongFormat in long: " + formatDate.time)
        return formatDate.time
    }

    /**
     * Getting the ward names from wards list
     * @param wards
     * @return
     */
    private fun getWardNames(wards: List<AddNewPatientWard>): LinkedHashMap<String, String> {
        val wardMap = LinkedHashMap<String, String>()
        for (i in wards.indices) {
            val patientWard: AddNewPatientWard = wards[i]
            if (patientWard.getWardName() != null) {
                wardMap[patientWard.getWardName()!!] = patientWard.getWardName()!!
            }
        }
        return wardMap
    }


    /**\\\\\\\\\\\\\\\\\\\\\\
     * Setting up the ward spinner by calling "getWardsList" API
     */
    private fun setWardSpinner() {
        if (!UtilityMethods().isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.idContainerLayout, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(
                    R.string.no_internet_connectivity
                ), CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        val hospitalId: Long = PrefUtility().getLongInPref(
            this@AddPatientActivity,
            Constants.SharedPrefConstants.HOSPITAL_ID,
            0L
        )
        val remoteProvider = ArrayList<String>()
        val providerMap = LinkedHashMap<String, String>()
        remoteProvider.add(getString(R.string.select_ward))
        // "getWardsList" API call
        viewModel?.getWardsList(hospitalId)?.observe(this, { it ->
            val response=it.body()
            Log.d(TAG, "Wards data" + response?.getStatus())
            if (response?.getStatus() != null && response.getStatus()!!) {
                if (response.getWards() != null && response.getWards()!!.isNotEmpty()) {
                    providerMap.putAll(getWardNames(response.getWards() as List<AddNewPatientWard>))
                    remoteProvider.addAll(providerMap.keys)
                }
            } else if (response?.getErrorId() != null) {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this, java.lang.String.valueOf(
                        response.getErrorId()
                    ), Constants.API.getHospital
                )
                errMsg?.let {
                    CustomSnackBar.make(
                        binding?.idContainerLayout,
                        this,
                        CustomSnackBar.WARNING,
                        it,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            } else {
                CustomSnackBar.make(
                    binding?.idContainerLayout, this, CustomSnackBar.WARNING, getString(
                        R.string.api_error
                    ), CustomSnackBar.TOP, 3000, 0
                )?.show()
            }
        })
        wardListAdapter = ArrayAdapter(this, R.layout.spinner_custom_text, remoteProvider)
        binding?.idSpinnerWard?.adapter = wardListAdapter
        // Spinner item select listener
        binding?.idSpinnerWard?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ) {
                try {
                    val spinnerText = view as TextView
                    val providerType = remoteProvider[position]
                    val remoteProvideId = providerMap[providerType]
                    binding?.idSpinnerWard?.tag = remoteProvideId
                    spinnerText.maxLines = 1
                    if (position != 0) {
                        UtilityMethods().setTextViewColor(
                            AddPatientActivity(),
                            spinnerText,
                            R.color.black
                        )
                        binding?.let { UtilityMethods().setDrawableBackground(
                            this,
                            it.idSpinnerWard,
                            R.drawable.spinner_drawable_selected
                        ) }
                    } else {
                        UtilityMethods().setTextViewColor(
                            AddPatientActivity(),
                            spinnerText,
                            R.color.gray_500
                        )
                        binding?.let { UtilityMethods().setDrawableBackground(
                            this,
                            it.idSpinnerWard,
                            R.drawable.spinner_drawable
                        ) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }


    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

}
