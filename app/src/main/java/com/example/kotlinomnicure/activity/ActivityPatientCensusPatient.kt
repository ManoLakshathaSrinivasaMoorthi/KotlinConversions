package com.example.kotlinomnicure.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.CensusPatientListAdapter
import com.example.kotlinomnicure.adapter.CensusWardListViewAdapter
import com.example.kotlinomnicure.databinding.ActivityPatientCensusPatientBinding
import com.example.kotlinomnicure.helper.NotificationHelper
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.interfaces.OnItemClickListener
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.CensusPatientListViewModel
import com.example.kotlinomnicure.viewmodel.ChatActivityViewModel
import com.example.kotlinomnicure.viewmodel.MyVirtualViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Patient
import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientList
import omnicurekotlin.example.com.providerEndpoints.model.OtherRebroadcastRequest
import java.text.SimpleDateFormat
import java.util.*


class ActivityPatientCensusPatient : BaseActivity(), OnItemClickListener {
    private val TAG = ActivityPatientCensusPatient::class.java.simpleName
    protected var binding: ActivityPatientCensusPatientBinding? = null
    private var viewModel: CensusPatientListViewModel? = null
    private var patientCensusListAdapter: CensusPatientListAdapter? = null
    private var censusWardListAdapter: CensusWardListViewAdapter? = null
    private var strWardName: String? = null
    private  var strHospitalName: String? = null
    private  var strHospitalAddress: String? = null
    private var strHospitalID: Long? = null
    private var acuityLevel: Constants.AcuityLevel = Constants.AcuityLevel.NA
    private var filterByDialog: Dialog? = null
    private var censusConsultDetailsDialog: Dialog? = null
    private var mPatientList: List<Patient>? = null
    private var isActivePatient = false
    private var teamNameArrayList: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_census_patient)
        viewModel = ViewModelProvider(this).get(CensusPatientListViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        strHospitalName = intent.getStringExtra("hospitalName")
        strHospitalAddress = intent.getStringExtra("hospitalAddress")
        strWardName = intent.getStringExtra("wardName")
        strHospitalID = intent.getLongExtra("wardHospitalId", 0)
        if (!TextUtils.isEmpty(strWardName)) {
            binding?.txtWard?.text = strWardName
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCensusPatientList?.layoutManager = linearLayoutManager
        if (PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "").equals("RD")) {
            binding?.fab?.visibility = View.GONE
            getTeamList()
        } else {
            binding?.fab?.visibility = View.VISIBLE
        }
        getCensusPatientList()
        binding?.idNavIconContainerLayout?.setOnClickListener { finish() }
        binding?.rlWardFilter?.setOnClickListener { v ->
            handleMultipleClick(v)
            FilterByWardDialog(this)
        }
        binding?.imgFilter?.setOnClickListener { v ->
            handleMultipleClick(v)
            FilterByDialog(this)
        }
        binding?.txtClear?.setOnClickListener { v ->
            handleMultipleClick(v)
            isActivePatient = false
            filterByAcuityActive(Constants.AcuityLevel.NA)
            binding?.txtClear?.visibility = View.GONE
            binding?.imgFilter?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter))
        }
        binding?.llSearch?.setOnClickListener {
            binding?.searchLayout?.visibility = View.VISIBLE
        }
        binding?.closeSearch?.setOnClickListener { view ->
            if (TextUtils.isEmpty(binding?.searchEditText?.text.toString())) {
                binding?.searchLayout?.visibility = View.GONE
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            } else if (binding?.searchEditText?.getText().toString().length > 0) {
                binding?.searchEditText?.setText("")
            }
        }
        binding?.fab?.setOnClickListener { v ->
            handleMultipleClick(v)
            val intent = Intent(this, AddPatientActivity::class.java)
            intent.putExtra(
                Constants.IntentKeyConstants.SCREEN_TYPE,
                Constants.IntentKeyConstants.SCREEN_CENSUS
            )
            startActivityForResult(intent, 501)
        }
        binding?.swipeLayout?.setOnRefreshListener {
            binding?.swipeLayout?.isRefreshing = false
            acuityLevel = Constants.AcuityLevel.NA
            isActivePatient = false
            binding?.txtWard?.text = strWardName
            binding?.txtClear?.visibility = View.GONE
            binding?.imgFilter?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter))
            if (patientCensusListAdapter != null) {
                patientCensusListAdapter?.updateList(mPatientList)
                patientCensusListAdapter?.notifyDataSetChanged()
                getCensusPatientList()
                if (patientCensusListAdapter?.getItemCount()!! <= 0) {
                    showEmptyErrorMessage()
                } else {
                    showEmptyErrorMessage()
                }
            }
            binding?.searchLayout?.visibility = View.GONE
            binding?.searchEditText?.setText("")
        }
        setSearchTextWatcher()
    }

    private fun onQuerySearch(query: String) {
        if (patientCensusListAdapter != null) {
            patientCensusListAdapter?.getFilter()?.filter(query)
        }
        if (TextUtils.isEmpty(query)) {
            filterByAcuityActive(acuityLevel)
        }
    }

    private fun setSearchTextWatcher() {
        binding?.searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                onQuerySearch(editable.toString())
            }
        })
    }

    private fun getCensusPatientList() {
        showProgressBar(PBMessageHelper().getMessage(this, getString(R.string.please_wait)))
        patientCensusListAdapter = CensusPatientListAdapter(this, ArrayList(), teamNameArrayList)
        binding?.rvCensusPatientList?.adapter = patientCensusListAdapter
        patientCensusListAdapter?.setOnSearchResultListener(object :
            CensusPatientListAdapter.OnSearchResultListener {
            override fun onSearchResult(count: Int) {
                if (count <= 0) {
                    showEmptyErrorMessage()
                    binding?.noPatientLayout?.visibility = View.VISIBLE
                } else {
                    binding?.rvCensusPatientList?.visibility = View.VISIBLE
                    binding?.noPatientLayout?.visibility = View.GONE
                }
            }
        })
        Log.d(
            TAG,
            "getCensusPatientList request : $strHospitalID strWardName :$strWardName"
        )
        strHospitalID?.let {
            strWardName?.let { it1 ->
                viewModel?.getWardHospitalList(it, it1)?.observe(this) {
                    val response=it.body()
                    dismissProgressBar()
                    if (response?.getWardPatientList() != null && response?.getWardPatientList()!!.isNotEmpty()) {
                        if (response?.getWardPatientList()!!.get(0)?.getPatientList() != null &&
                            !response.getWardPatientList()!!.get(0)?.getPatientList()?.isEmpty()!!
                        ) {
                            Log.d(TAG, "getCensusPatientList response : " + Gson().toJson(response))
                            mPatientList = response.getWardPatientList()!!.get(0)?.getPatientList() as List<Patient>?
                            Collections.sort(mPatientList, PatientSortByTime())
                            filterByAcuityActive(acuityLevel)
                            patientCensusListAdapter?.notifyDataSetChanged()
                        } else {
                            if (patientCensusListAdapter?.getItemCount()!! <= 0) {
                                showEmptyErrorMessage()
                                binding?.noPatientLayout?.setVisibility(View.VISIBLE)
                            } else {
                                binding?.rvCensusPatientList?.visibility = View.VISIBLE
                                binding?.noPatientLayout?.setVisibility(View.GONE)
                            }
                        }
                    } else {
                        /*
                                            CustomSnackBar.make(binding.getRoot(), this, CustomSnackBar.WARNING,
                                                    getString(R.string.no_patient_list), CustomSnackBar.TOP, 3000, 0).show();
                            */
                    }
                }
            }
        }
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    fun FilterByWardDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_by_ward_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.attributes.windowAnimations = R.style.SlideUpDialog
        val imgBack = dialog.findViewById<View>(R.id.imgBack) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        val txtTitle = dialog.findViewById<View>(R.id.txtTitle) as TextView
        txtTitle.text = getString(R.string.filter_by_ward)
        val rvWardList = dialog.findViewById<View>(R.id.recyclerviewLocation) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvWardList.layoutManager = linearLayoutManager
        strHospitalID?.let {
            viewModel?.getWardList(it)?.observe(this) {
                val response=it.body()
                dismissProgressBar()
                if (response?.getWardPatientList() != null && !response.getWardPatientList()!!.isEmpty()) {
                    rvWardList.visibility = View.VISIBLE
                    Log.d(TAG, "getCensusWardList response : " + Gson().toJson(response))
                    censusWardListAdapter =
                        CensusWardListViewAdapter(object :
                            CensusWardListViewAdapter.HospitalRecyclerListener {

                            override fun onItemSelected(ward: WardPatientList?) {
                                Log.d(TAG, "selected name : " + ward?.getWardName())
                                binding?.txtWard?.setText(ward?.getWardName())
                                strWardName = ward?.getWardName()
                                dialog.dismiss()
                                binding?.rvCensusPatientList?.setAdapter(null)
                                getCensusPatientList()
                            }
                        }, response.getWardPatientList() as List<WardPatientList>?, strWardName)
                    rvWardList.adapter = censusWardListAdapter
                    censusWardListAdapter!!.notifyDataSetChanged()
                }  else {
                    rvWardList.visibility = View.GONE
                    CustomSnackBar.make(
                        binding?.getRoot(), this, CustomSnackBar.WARNING,
                        getString(R.string.no_ward_list), CustomSnackBar.TOP, 3000, 0
                    )?.show()
                }
            }
        }
        imgBack.setOnClickListener { dialog.dismiss() }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun FilterByAcuityDialog(context: Context?) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.filter_acuity_dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.attributes?.windowAnimations = R.style.SlideUpDialog
        val rlLow = dialog.findViewById<View>(R.id.rlLow) as RelativeLayout
        val rlMedium = dialog.findViewById<View>(R.id.rlMedium) as RelativeLayout
        val rlHigh = dialog.findViewById<View>(R.id.rlHigh) as RelativeLayout
        val rlAll = dialog.findViewById<View>(R.id.rlAll) as RelativeLayout
        val imgLowAcuity = dialog.findViewById<View>(R.id.imgLowAcuity) as ImageView
        val imgMediumAcuity = dialog.findViewById<View>(R.id.imgMediumAcuity) as ImageView
        val imgHighAcuity = dialog.findViewById<View>(R.id.imgHighAcuity) as ImageView
        val imgAcuityAll = dialog.findViewById<View>(R.id.imgAcuityAll) as ImageView
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageButton
        when (acuityLevel) {
            Constants.AcuityLevel.NA -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.VISIBLE
            }
            Constants.AcuityLevel.Low -> {
                imgLowAcuity.visibility = View.VISIBLE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.GONE
            }
            Constants.AcuityLevel.Medium -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.VISIBLE
                imgHighAcuity.visibility = View.GONE
                imgAcuityAll.visibility = View.GONE
            }
            Constants.AcuityLevel.High -> {
                imgLowAcuity.visibility = View.GONE
                imgMediumAcuity.visibility = View.GONE
                imgHighAcuity.visibility = View.VISIBLE
                imgAcuityAll.visibility = View.GONE
            }
            else -> {
            }
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        rlLow.setOnClickListener {
            binding?.txtClear?.visibility = View.VISIBLE
            binding?.imgFilter?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter_selected))
            filterByAcuityActive(Constants.AcuityLevel.Low)
            dialog.dismiss()
            filterByDialog?.dismiss()
        }
        rlMedium.setOnClickListener {
            binding?.txtClear?.setVisibility(View.VISIBLE)
            binding?.imgFilter?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter_selected))
            filterByAcuityActive(Constants.AcuityLevel.Medium)
            dialog.dismiss()
            filterByDialog?.dismiss()
        }
        rlHigh.setOnClickListener {
            binding?.txtClear?.visibility = View.VISIBLE
            binding?.imgFilter?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter_selected))
            filterByAcuityActive(Constants.AcuityLevel.High)
            dialog.dismiss()
            filterByDialog?.dismiss()
        }
        rlAll.setOnClickListener {
            if (!isActivePatient) {
                binding?.txtClear?.setVisibility(View.GONE)
                binding?.imgFilter?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter))
            }
            acuityLevel = Constants.AcuityLevel.NA
            filterByAcuityActive(Constants.AcuityLevel.NA)
            dialog.dismiss()
            filterByDialog?.dismiss()
        }
        dialog.show()
    }

    fun FilterByDialog(context: Context?) {
        filterByDialog = Dialog(context!!, R.style.Theme_Dialog)
        filterByDialog?.setContentView(R.layout.filter_by_patient)
        filterByDialog?.setCancelable(false)
        filterByDialog?.setCanceledOnTouchOutside(false)
        filterByDialog?.window?.setGravity(Gravity.BOTTOM)
        filterByDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        filterByDialog?.window?.attributes?.windowAnimations = R.style.SlideUpDialog
        val rlAcuity = filterByDialog?.findViewById<View>(R.id.rlAcuity) as RelativeLayout
        val rlActive = filterByDialog?.findViewById<View>(R.id.rlActive) as RelativeLayout
        val rlAllConsult = filterByDialog?.findViewById<View>(R.id.rlAllConsult) as RelativeLayout
        val imgCancel = filterByDialog?.findViewById<View>(R.id.imgCancel) as ImageButton
        if (PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "").equals("RD")) {
            rlActive.visibility = View.VISIBLE
            rlAllConsult.visibility = View.GONE
        } else {
            rlActive.visibility = View.GONE
            rlAllConsult.visibility = View.VISIBLE
        }
        rlAcuity.setOnClickListener {
            FilterByAcuityDialog(this)
            filterByDialog?.dismiss()
        }
        rlActive.setOnClickListener {
            isActivePatient = true
            binding?.txtClear?.setVisibility(View.VISIBLE)
            binding?.imgFilter?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter_selected))
            filterByAcuityActive(acuityLevel)
            filterByDialog?.dismiss()
        }
        rlAllConsult.setOnClickListener {
            isActivePatient = true
            binding?.txtClear?.setVisibility(View.VISIBLE)
            binding?.imgFilter?.setImageDrawable(resources.getDrawable(R.drawable.ic_filter_selected))
            filterByAcuityActive(acuityLevel)
            filterByDialog?.dismiss()
        }
        imgCancel.setOnClickListener { filterByDialog!!.dismiss() }
        filterByDialog!!.show()
    }

    fun censusConsultDetailsDialog(context: Context?, patient: Patient) {
        var strGender = ""
        var strPhone = ""
        var strWard = ""
        var strWards = ""
        var strHosName = ""
        var strHosAddress = ""
        val providerId: Long? = PrefUtility().getProviderId(this)
        val strRole: String? = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.ROLE,
            ""
        )
        val strDesignation: String? = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.R_PROVIDER_TYPE,
            ""
        )
        Log.d(TAG, "consultDetailsDialog : " + Gson().toJson(patient))
        censusConsultDetailsDialog = Dialog(context!!, R.style.Theme_Dialog)
        censusConsultDetailsDialog?.setContentView(R.layout.census_econsult_detail_dialog)
        censusConsultDetailsDialog?.window?.setGravity(Gravity.BOTTOM)
        censusConsultDetailsDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        censusConsultDetailsDialog?.window?.attributes?.windowAnimations = R.style.SlideUpDialog

        val txtPatientName = censusConsultDetailsDialog?.findViewById<View>(R.id.txtPatientName) as TextView
        val txtAge = censusConsultDetailsDialog?.findViewById<View>(R.id.txtAge) as TextView
        val txtLocation = censusConsultDetailsDialog?.findViewById<View>(R.id.txtLocation) as TextView
        val txtMRNNumber = censusConsultDetailsDialog?.findViewById<View>(R.id.txtMRNNumber) as TextView
        val txtComplaint = censusConsultDetailsDialog?.findViewById<View>(R.id.txtComplaint) as TextView
        val txtTimeZone = censusConsultDetailsDialog?.findViewById<View>(R.id.txtTimeZone) as TextView
        val txtMessage = censusConsultDetailsDialog?.findViewById<View>(R.id.txtMessage) as TextView
        val txteConsult = censusConsultDetailsDialog?.findViewById<View>(R.id.txteConsult) as TextView
        val imgCancel = censusConsultDetailsDialog?.findViewById<View>(R.id.imgCancel) as ImageView
        val imgMessageAlert = censusConsultDetailsDialog?.findViewById<View>(R.id.imgMessageAlert) as ImageView
        val llMessage = censusConsultDetailsDialog?.findViewById<View>(R.id.llMessage) as LinearLayout
        val llButtonView =
            censusConsultDetailsDialog!!.findViewById<View>(R.id.llButtonView) as LinearLayout
        val llViewDetails =
            censusConsultDetailsDialog!!.findViewById<View>(R.id.llViewDetails) as LinearLayout
        Log.d(TAG, "Patient response provider ID : " + patient.getRdProviderId())
        Log.d(TAG, "Patient Login provider ID  : $providerId")
        Log.d(TAG, "Patient strRole  : $strRole")
        Log.d(TAG, "Patient strDesignation  : $strDesignation")
        if (!TextUtils.isEmpty(patient.getBdProviderId()) && patient.getBdProviderId()
                ?.contentEquals(providerId.toString()) == true ||
            !TextUtils.isEmpty(patient.getRdProviderId()) && patient.getRdProviderId()
                ?.contentEquals(providerId.toString()) == true
        ) {
            llButtonView.visibility = View.VISIBLE
            llViewDetails.visibility = View.GONE
        } else if (strRole == "RD" && !strDesignation.equals("MD/DO", ignoreCase = true)
            && !patient.getStatus()
                ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Pending))!!
        ) {
            if (!TextUtils.isEmpty(patient.getTeamName())) {
                if (teamNameArrayList != null) {
                    Log.d(
                        TAG,
                        "GetTeamName  : " + teamNameArrayList + " : " + patient.getTeamName()
                    )
                    if (teamNameArrayList!!.contains(patient.getTeamName())) {
                        llButtonView.visibility = View.VISIBLE
                        llViewDetails.visibility = View.GONE
                    } else {
                        llButtonView.visibility = View.GONE
                        llViewDetails.visibility = View.VISIBLE
                    }
                } else {
                    llButtonView.visibility = View.GONE
                    llViewDetails.visibility = View.VISIBLE
                }
            } else {
                llButtonView.visibility = View.GONE
                llViewDetails.visibility = View.VISIBLE
            }
        } else if (strRole == "RD" && strDesignation.equals("MD/DO", ignoreCase = true)) {
            if (patient.getStatus()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Pending)) == true
                || patient.getStatus()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Handoff)) == true
            ) {
                llButtonView.visibility = View.VISIBLE
                llViewDetails.visibility = View.GONE
            } else {
                if (!TextUtils.isEmpty(patient.getRdProviderId()) && patient.getRdProviderId()
                        ?.contentEquals(providerId.toString()) == true
                ) {
                    llButtonView.visibility = View.VISIBLE
                    llViewDetails.visibility = View.GONE
                } else {
                    llButtonView.visibility = View.GONE
                    llViewDetails.visibility = View.VISIBLE
                }
            }
        } else {
            llButtonView.visibility = View.GONE
            llViewDetails.visibility = View.VISIBLE
        }
        val stub =
            censusConsultDetailsDialog!!.findViewById<View>(R.id.layout_stub_view) as ViewStub
        val statusStub =
            censusConsultDetailsDialog!!.findViewById<View>(R.id.status_stub) as RelativeLayout
        UtilityMethods().displayCensusVitals(this, stub, patient)
        patient.getUrgent()?.let {
            patient.getStatus()
                ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Pending))
                ?.let { it1 ->
                    UtilityMethods().displayPatientStatusComponent(
                        this, statusStub, it,
                        it1, Constants.AcuityLevel.valueOf(patient.getScore()!!)
                    )
                }
        }
        Log.d(TAG, "Patient Vital Values : " + Gson().toJson(patient))
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        calendar.timeInMillis = patient.getDob()!!
        val agee = year - calendar[Calendar.YEAR]
        val age = agee.toString()
        val dot = " <b>\u00b7</b> "
        val timeInMillis: Long = java.lang.Long.valueOf(patient.getDob()!!)
        val dateString = SimpleDateFormat("MM-dd-yyyy").format(Date(timeInMillis))
        val strDob = dot + dateString
        if (!TextUtils.isEmpty(patient.getGender())) {
            strGender = dot + patient.getGender()
        }
        if (!TextUtils.isEmpty(patient.getGender()) && patient.getGender()
                ?.contentEquals("Male") == true
        ) {
            strGender = dot + "M"
        } else if (!TextUtils.isEmpty(patient.getGender()) && patient.getGender()
                ?.contentEquals("Female") == true
        ) {
            strGender = dot + "F"
        }
        strPhone = if (!TextUtils.isEmpty(patient.getPhone()) &&
            !patient.getPhone()?.contentEquals("null")!!
        ) {
            dot + patient.getPhone()
        } else {
            ""
        }
        strWard = if (!TextUtils.isEmpty(patient.getWardName())) {
            dot + patient.getWardName()
        } else {
            ""
        }
        strWards = if (!TextUtils.isEmpty(patient.getWardName())) {
            dot + patient.getWardName()
        } else {
            ""
        }

      strHosName = if (!TextUtils.isEmpty(patient.getHospital())) ({
          patient.getHospital()
      }).toString() else {
            ""
        }
        strHosAddress = if (!TextUtils.isEmpty(strHospitalAddress)) {
            " , $strHospitalAddress"
        } else {
            ""
        }
        if (!TextUtils.isEmpty(patient.getRecordNumber())) {
            txtMRNNumber.text = Html.fromHtml("MRN&nbsp;" + patient.getRecordNumber())
        } else {
            txtMRNNumber.text = "MRN "
        }
        Log.d(TAG, "strComplaint" + patient.getNote())
        val strComplaint: String? = patient.getNote()
        val stringComplaint = strComplaint?.substring(strComplaint?.indexOf(":") + 1)
        txtComplaint.movementMethod = ScrollingMovementMethod()
        txtComplaint.setOnTouchListener { v, event ->
            txtComplaint.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        txtComplaint.isVerticalScrollBarEnabled = false
        txtComplaint.post {
            val lineCount = txtComplaint.lineCount
            if (lineCount > 3) {
                txtComplaint.isScrollbarFadingEnabled = false
                txtComplaint.isVerticalScrollBarEnabled = true
            }
        }
        txtPatientName.setText(patient.getName())
        txtAge.text = Html.fromHtml(age + strGender + strDob + strPhone)
        txtLocation.text = Html.fromHtml(strHosName + strWard)
        txtComplaint.text = stringComplaint?.trim { it <= ' ' }
        Log.d(TAG, "stringComplaint$stringComplaint")
        if (patient.getSyncTime() != null && !patient.getSyncTime()!!.equals("0")) {
            txtTimeZone.setText(Utils().timestampToDate(patient.getSyncTime()!!))
            txtTimeZone.visibility = View.VISIBLE
        } else {
            txtTimeZone.text = " - "
        }
        imgCancel.setOnClickListener { censusConsultDetailsDialog!!.dismiss() }
        llMessage.setOnClickListener {
            startChat(patient)
            Handler().postDelayed({ censusConsultDetailsDialog!!.dismiss() }, 1000)
        }
        llViewDetails.setOnClickListener {
            var intentConsultChart: Intent? = null
            var strCompletedByName = ""
            if (patient.getCompleted_by() != null && !TextUtils.isEmpty(patient.getCompleted_by())) {
                strCompletedByName = patient.getCompleted_by()!!
            }
            if (patient.getStatus()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Discharged)) == true ||
                patient.getStatus()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Exit)) == true
            ) {
                intentConsultChart =
                    if (!TextUtils.isEmpty(patient.getBdProviderId()) && patient.getBdProviderId()
                            ?.contentEquals(providerId.toString())!! ||
                        !TextUtils.isEmpty(patient.getRdProviderId()) && patient.getRdProviderId()
                            ?.contentEquals(providerId.toString())!!
                    ) {
                        Intent(this, ActivityConsultChartRemote::class.java)
                    } else {
                        Intent(this, ActivityCensusConsultChart::class.java)
                    }
            } else if (strRole == "RD" && (!strDesignation.equals("MD/DO", ignoreCase = true)
                        && patient.getStatus()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Pending)) == true)
            ) {
                intentConsultChart = Intent(this, ActivityCensusConsultChart::class.java)
            } else if (strRole == "RD" && (!strDesignation.equals("MD/DO", ignoreCase = true)
                        && !patient.getStatus()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Pending))!!)
            ) {
                intentConsultChart = Intent(this, ActivityCensusConsultChart::class.java)
                if (!TextUtils.isEmpty(patient.getTeamName())) {
                    if (teamNameArrayList != null) {
                        Log.d(TAG, "GetTeamName  : " + teamNameArrayList + " : " + patient.getTeamName())
                        if (teamNameArrayList?.contains(patient.getTeamName()) == true) {
                            intentConsultChart = Intent(this, ActivityConsultChart::class.java)
                        }
                    }
                }
            } else if (strRole == "RD" && strDesignation.equals("MD/DO", ignoreCase = true)) {
                intentConsultChart = if (patient.getStatus()
                        ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Pending)) == true
                    || patient.getStatus()
                        ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Handoff)) == true
                ) {
                    Intent(this, ActivityConsultChart::class.java)
                } else {
                    if (!TextUtils.isEmpty(patient.getRdProviderId()) && patient.getRdProviderId()
                            ?.contentEquals(providerId.toString()) == true
                    ) {
                        Intent(this, ActivityConsultChart::class.java)
                    } else {
                        Intent(this, ActivityCensusConsultChart::class.java)
                    }
                }
            } else if (strRole == "BD") {
                intentConsultChart =
                    if (!TextUtils.isEmpty(patient.getBdProviderId()) && patient.getBdProviderId()
                            ?.contentEquals(providerId.toString()) == true
                    ) {
                        Intent(this, ActivityConsultChart::class.java)
                    } else {
                        Intent(this, ActivityCensusConsultChart::class.java)
                    }
            } else {
                intentConsultChart = Intent(this, ActivityConsultChart::class.java)
            }
            intentConsultChart.putExtra("patientBdProviderId", "" + patient.getBdProviderId())
            intentConsultChart.putExtra("patientRdProviderId", "" + patient.getRdProviderId())
            intentConsultChart.putExtra("patientName", "" + patient.getName())
            intentConsultChart.putExtra("patientWardName", "" + patient.getWardName())
            intentConsultChart.putExtra("patientScore", "" + patient.getScore())
            intentConsultChart.putExtra("patientDob", patient.getDob())
            intentConsultChart.putExtra("patientGender", patient.getGender())
            intentConsultChart.putExtra("patientPhone", patient.getPhone())
            intentConsultChart.putExtra("patientWard", strWardName)
            intentConsultChart.putExtra("patientHospitalName", patient.getHospital())
            intentConsultChart.putExtra("patientHospitalAddress", strHospitalAddress)
            intentConsultChart.putExtra("patientRecordNumber", patient.getRecordNumber())
            intentConsultChart.putExtra("patientNote", patient.getNote())
            intentConsultChart.putExtra("patientStatus", patient.getStatus())
            intentConsultChart.putExtra("patientHeartRate", patient.getHeartRate())
            intentConsultChart.putExtra(
                "patientSystolic",
                patient.getArterialBloodPressureSystolic()
            )
            intentConsultChart.putExtra(
                "patientDiastolic",
                patient.getArterialBloodPressureDiastolic()
            )
            intentConsultChart.putExtra("patientRespiratoryRate", patient.getRespiratoryRate())
            intentConsultChart.putExtra("patientTemperature", patient.getTemperature())
            intentConsultChart.putExtra("patientFio2", patient.getFio2())
            intentConsultChart.putExtra("patientSp02", patient.getSpO2())
            intentConsultChart.putExtra("patientOxygenSupplement", patient.getOxygenSupplement())
            intentConsultChart.putExtra("patientCondition", patient.getPatientCondition())
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                patient.getUrgent()
            )
            intentConsultChart.putExtra("uid", patient.getId())
            intentConsultChart.putExtra("providerNameType", strCompletedByName)
            intentConsultChart.putExtra(
                "completedTime",
                java.lang.String.valueOf(patient.getTime())
            )
            intentConsultChart.putExtra("status", patient.getStatus())
            intentConsultChart.putExtra("path", "consults/" + patient.getId())
            intentConsultChart.putExtra("consultProviderId", "" + patient.getId())
            intentConsultChart.putExtra("consultProviderPatientId", "" + patient.getId())
            intentConsultChart.putExtra("consultProviderText", "")
            intentConsultChart.putExtra("consultProviderName", patient.getName())
            intentConsultChart.putExtra("unreadMessageCount", 0)
            intentConsultChart.putExtra("dob", patient.getDob())
            intentConsultChart.putExtra("gender", patient.getGender())
            intentConsultChart.putExtra("note", patient.getNote())
            intentConsultChart.putExtra("phone", patient.getPhone())
            intentConsultChart.putExtra("patientId", patient.getId())
            intentConsultChart.putExtra("teamNameConsult", "Team " + patient.getTeamName())
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.SCREEN_TYPE,
                Constants.IntentKeyConstants.SCREEN_CENSUS
            )
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                patient.getUrgent()
            )
            if (patient.getStatus() != null) {
                intentConsultChart.putExtra("status", patient.getStatus().toString())
                if (patient.getStatus()!!
                        .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Invited))
                    || patient.getStatus()!!
                        .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Handoff))
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    clearNotifications(patient.getId()!!.toInt())
                } else if (patient.getStatus()!!
                        .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Discharged))
                    || patient.getStatus()!!
                        .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Exit))
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }
            Log.d(TAG, "mConsultProviderKey : " + patient.getId())
            startActivityForResult(intentConsultChart, 505)
            censusConsultDetailsDialog!!.dismiss()
            Log.d(TAG, "intentConsultChart : " + Gson().toJson(intentConsultChart))
        }

           txteConsult.setOnClickListener {
            var strCompletedByName = ""
            if (patient.getCompleted_by() != null && !TextUtils.isEmpty(patient.getCompleted_by())) {
                strCompletedByName = patient.getCompleted_by()!!
            }
            val intentConsultChart: Intent
            intentConsultChart = if (patient.getStatus()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Discharged)) == true ||
                patient.getStatus()
                    ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Exit)) == true
            ) {
                Intent(this, ActivityConsultChartRemote::class.java)
            } else {
                Intent(this, ActivityConsultChart::class.java)
            }
            intentConsultChart.putExtra("uid", patient.getId())
            intentConsultChart.putExtra("providerNameType", strCompletedByName)
            intentConsultChart.putExtra(
                "completedTime",
                java.lang.String.valueOf(patient.getTime())
            )
            intentConsultChart.putExtra("status", patient.getStatus())
            intentConsultChart.putExtra("path", "consults/" + patient.getId())
            intentConsultChart.putExtra("consultProviderId", "" + patient.getId())
            intentConsultChart.putExtra("consultProviderPatientId", "" + patient.getId())
            intentConsultChart.putExtra("consultProviderText", "")
            intentConsultChart.putExtra("consultProviderName", patient.getName())
            intentConsultChart.putExtra("unreadMessageCount", 0)
            intentConsultChart.putExtra("dob", patient.getDob())
            intentConsultChart.putExtra("gender", patient.getGender())
            intentConsultChart.putExtra("note", patient.getNote())
            intentConsultChart.putExtra("phone", patient.getPhone())
            intentConsultChart.putExtra("patientId", patient.getId())
            intentConsultChart.putExtra("teamNameConsult", "Team " + patient.getTeamName())
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.SCREEN_TYPE,
                Constants.IntentKeyConstants.SCREEN_CENSUS
            )
            intentConsultChart.putExtra(
                Constants.IntentKeyConstants.IS_PATIENT_URGENT,
                patient.getUrgent()
            )
            if (patient.getStatus() != null) {
                intentConsultChart.putExtra("status", patient.getStatus().toString())
                if (patient.getStatus()!!
                        .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Invited))
                    || patient.getStatus()!!
                        .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Handoff))
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                    clearNotifications(patient.getId()!!.toInt())
                } else if (patient.getStatus()!!
                        .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Discharged))
                    || patient.getStatus()!!
                        .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Exit))
                ) {
                    intentConsultChart.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                    clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
                }
            }
            //                startActivity(intentConsultChart);
            startActivityForResult(intentConsultChart, 505)
            censusConsultDetailsDialog!!.dismiss()
            Log.d(TAG, "intentConsultChart : " + Gson().toJson(intentConsultChart))
        }
        censusConsultDetailsDialog!!.show()
    }

    private fun clearNotifications(notificationId: Int) {
        NotificationHelper(this, null).clearNotification(notificationId.toLong())
    }

    override fun onViewClick(position: Int) {
        patientCensusListAdapter?.getItem(position)?.let { censusConsultDetailsDialog(this, it) }
    }

    override fun onClickStartConsultationSame(position: Int) {
        patientCensusListAdapter?.getItem(position)?.let {
            DoStartConsultationSameProvider(
                this,
                it
            )
        }
    }

    override fun onClickStartConsultationOther(position: Int) {
        patientCensusListAdapter?.getItem(position)?.let {
            DoStartConsultationOtherProvider(
                this,
                it
            )
        }
    }

    fun startChat(patient: Patient?) {
        if (patient == null || patient.getId() == null) {
            CustomSnackBar.make(
                binding?.getRoot(), this, CustomSnackBar.WARNING,
                "Patient does not exist", CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        val intent = Intent(baseContext, ChatActivity::class.java)
        intent.putExtra("uid", patient.getId())
        intent.putExtra("path", "consults/" + patient.getId())
        intent.putExtra("consultProviderId", "" + patient.getId())
        intent.putExtra("consultProviderPatientId", "" + patient.getId())
        intent.putExtra("consultProviderText", "")
        intent.putExtra("consultProviderName", patient.getName())
        intent.putExtra("dob", patient.getDob())
        intent.putExtra("gender", patient.getGender())
        intent.putExtra("note", patient.getNote())
        intent.putExtra("phone", patient.getPhone())
        intent.putExtra("patientId", patient.getId())
        intent.putExtra("teamNameConsult", "Team " + patient.getTeamName())
        intent.putExtra(
            Constants.IntentKeyConstants.SCREEN_TYPE,
            Constants.IntentKeyConstants.SCREEN_CENSUS
        )
        intent.putExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT, patient.getUrgent())
        if (patient.getStatus() != null) {
            intent.putExtra("status", patient.getStatus().toString())
            if (patient.getStatus()!!
                    .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Invited))
                || patient.getStatus()!!
                    .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Handoff))
            ) {
                intent.putExtra(Constants.IntentKeyConstants.INVITATION, true)
                clearNotifications(patient.getId()!!.toInt())
            } else if (patient.getStatus()!!
                    .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Discharged))
                || patient.getStatus()!!
                    .contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Exit))
            ) {
                intent.putExtra(Constants.IntentKeyConstants.COMPLETED, true)
                clearNotifications(Constants.NotificationIds.DISCHARGE_NOTIFICATION_ID)
            }
        }
        startActivity(intent)
        censusConsultDetailsDialog!!.dismiss()
        Log.d(TAG, "onDataChange: Chat activity started...")
    }

    fun filterByAcuityActive(level: Constants.AcuityLevel) {
        if (mPatientList == null) {
            return
        }
        if (level === Constants.AcuityLevel.NA) {
            var filteredData: Any? = ArrayList<Any?>(mPatientList)
            filteredData =
                if (PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
                        .equals("RD")
                ) {
                    filterShowMyActiveConsult(filteredData as MutableList<Patient>) as ArrayList<Patient>
                } else {
                    filterShowMyeConsult(filteredData as MutableList<Patient>) as ArrayList<Patient>
                }

            // perform search
            filteredData = performSearch(filteredData) as ArrayList<Patient>
            acuityLevel = level

            // This is for ALL acuity filter option
            if (patientCensusListAdapter != null) {
                patientCensusListAdapter?.updateList(filteredData)
                patientCensusListAdapter?.notifyDataSetChanged()
                if (patientCensusListAdapter?.getItemCount()!! <= 0) {
                    // This is to show error message layout with correct message
                    showEmptyErrorMessage()
                    binding?.noPatientLayout?.setVisibility(View.VISIBLE)
                } else {
                    binding?.rvCensusPatientList?.setVisibility(View.VISIBLE)
                    binding?.noPatientLayout?.setVisibility(View.GONE)
                }
            }
            return
        }
        var filteredData: ArrayList<Patient> = ArrayList<Patient>()
        //Fil
        for (i in mPatientList!!.indices) {
            val patient: Patient = mPatientList!![i]
            when (level) {
                Constants.AcuityLevel.Low -> if (patient.getScore()
                        ?.contentEquals(java.lang.String.valueOf(Constants.AcuityLevel.Low)) == true
                ) {
                    filteredData.add(patient)
                }
                Constants.AcuityLevel.Medium -> if (patient.getScore()
                        ?.contentEquals(java.lang.String.valueOf(Constants.AcuityLevel.Medium)) == true
                ) {
                    filteredData.add(patient)
                }
                Constants.AcuityLevel.High -> if (patient.getScore()
                        ?.contentEquals(java.lang.String.valueOf(Constants.AcuityLevel.High)) == true
                ) {
                    filteredData.add(patient)
                }
                else -> {
                }
            }
        }
        filteredData =
            if (PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
                    .equals("RD")
            ) {
                filterShowMyActiveConsult(filteredData) as ArrayList<Patient>
            } else {
                filterShowMyeConsult(filteredData) as ArrayList<Patient>
            }
        // perform search
        filteredData = performSearch(filteredData) as ArrayList<Patient>
        acuityLevel = level
        if (patientCensusListAdapter != null) {
            patientCensusListAdapter?.updateList(filteredData)
            patientCensusListAdapter?.notifyDataSetChanged()
            if (patientCensusListAdapter?.getItemCount() !!<= 0) {
                showEmptyErrorMessage()
                binding?.noPatientLayout?.setVisibility(View.VISIBLE)
            } else {
                binding?.rvCensusPatientList?.setVisibility(View.VISIBLE)
                binding?.noPatientLayout?.setVisibility(View.GONE)
            }
        }
    }

    private fun filterShowMyActiveConsult(filteredData: MutableList<Patient>): List<Patient> {
        // filter if active enabled
        val providerId: Long? = PrefUtility().getProviderId(this)
        if (isActivePatient) {
            val tempList: Any? = ArrayList<Any?>(filteredData)
            filteredData.clear()
            for (i in tempList.indices) {
                val patient: Patient = tempList[i]
                val strDesignation: String? =
                    PrefUtility().getStringInPref(
                        this,
                        Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                        ""
                    )
                if (strDesignation == "MD/DO") {
                    if (!TextUtils.isEmpty(patient.getRdProviderId()) && patient.getRdProviderId()
                            ?.contentEquals(providerId.toString()) == true
                    ) {
                        if (patient.getStatus()
                                ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Active)) == true
                        ) {
                            filteredData.add(patient)
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(patient.getTeamName())) {
                        if (teamNameArrayList != null) {
                            if (teamNameArrayList!!.contains(patient.getTeamName())) {
                                if (patient.getStatus()
                                        ?.contentEquals(java.lang.String.valueOf(Constants.PatientStatus.Active)) == true
                                ) {
                                    filteredData.add(patient)
                                }
                            }
                        }
                    }
                }
            }
        }
        return filteredData
    }

    private fun filterShowMyeConsult(filteredData: MutableList<Patient>): List<Patient> {
        val providerId: Long? = PrefUtility().getProviderId(this)
        if (isActivePatient) {
            val tempList: Any? = ArrayList<Any?>(filteredData)
            filteredData.clear()
            for (i in tempList.indices) {
                val patient: Patient = tempList[i]
                if (!TextUtils.isEmpty(patient.getBdProviderId()) && patient.getBdProviderId()
                        ?.contentEquals(providerId.toString()) == true
                ) {
                    filteredData.add(patient)
                }
            }
        }
        return filteredData
    }

    private fun performSearch(patientFilterList: MutableList<Patient>): List<Patient> {
        val searchQuery: String = binding?.searchEditText?.getText().toString()
        if (!TextUtils.isEmpty(searchQuery)) {
            val tempOfFilterList: Any? = ArrayList<Any?>(patientFilterList)
            patientFilterList.clear()
            for (patient in tempOfFilterList) {
                var firstName: String? = patient.getFname()
                val lastName: String? = patient.getLname()
                if (!TextUtils.isEmpty(patient.getName())) {
                    firstName = patient.getName()
                }
                if (firstName != null && firstName.trim { it <= ' ' }.toLowerCase()
                        .contains(searchQuery)
                    || lastName != null && lastName.trim { it <= ' ' }.toLowerCase()
                        .contains(searchQuery)
                ) {
                    patientFilterList.add(patient)
                }
            }
        }
        return patientFilterList
    }

    private fun getTeamList() {
        val uid: Long = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0)
        showProgressBar()
        val virtualTeamsViewModel = ViewModelProvider(this).get(MyVirtualViewModel::class.java)
        virtualTeamsViewModel?.getTeams(uid)?.observe(this, {
            val response=it.body()
            dismissProgressBar()
            Log.d(TAG, "Teams Details Response : " + Gson().toJson(response))
            if (response?.status != null && response.status!!) {
              //  val obj = JSONObject(response)
                teamNameArrayList = ArrayList()

                patientCensusListAdapter?.setTeamNameArrayList(teamNameArrayList)
                Log.d(TAG,
                    "teamNameArrayList : " + (teamNameArrayList as ArrayList<String>).size
                )
            }
        })
    }

    private fun showEmptyErrorMessage() {
        if (acuityLevel === Constants.AcuityLevel.NA && !isActivePatient
            && TextUtils.isEmpty(binding?.searchEditText?.text.toString())
        ) {
            // This means there is no selected filter applied so resetted to default all section
            binding?.noPatientsImage?.visibility = View.VISIBLE
            binding?.noPatientTitle?.visibility = View.GONE
            binding?.noPatientText?.text = resources.getString(R.string.no_census_patient_found)
        } else {
            showFilterErrorMessage()
        }
    }

    private fun showFilterErrorMessage() {
        binding?.noPatientLayout?.visibility = View.VISIBLE
        binding?.noPatientsImage?.visibility = View.GONE
        binding?.noPatientTitle?.visibility = View.VISIBLE
        binding?.noPatientText?.text = resources.getString(R.string.no_results_for_filter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 501) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                getCensusPatientList()
            }
        }
        if (requestCode == 505) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                if (censusConsultDetailsDialog != null) {
                    if (censusConsultDetailsDialog!!.isShowing) {
                        censusConsultDetailsDialog!!.dismiss()
                    }
                }
                //asd
                getCensusPatientList()
            }
        }
    }

    fun DoStartConsultationSameProvider(context: Context?, patient: Patient) {
        if (!UtilityMethods().isInternetConnected(this)!!) {
            CustomSnackBar.make(
                binding?.content, this,
                CustomSnackBar.WARNING, getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        val providerId: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getStringInPref(
            this,
            Constants.SharedPrefConstants.TOKEN,
            ""
        )
        showProgressBar(PBMessageHelper().getMessage(this, getString(R.string.invite_provider)))
        Log.d(
            TAG,
            "DoStartConsultationSameProvider request :  " + providerId + "patient id :" + patient.getId()
        )
        val chatViewModel: ChatActivityViewModel = ViewModelProvider(this).get(ChatActivityViewModel::class.java)
        providerId?.let {
            token?.let { it1 ->
                patient.getId()?.let { it2 ->
                    chatViewModel.inviteProviderBroadCast(it, it1, it2)?.observe(this,  {

                                dismissProgressBar()
                                Log.d(TAG, "DoStartConsultationSameProvider response : " + Gson().toJson(it))
                                if (it != null &&it.getStatus() != null && it.getStatus()!!) {
                                    getCensusPatientList()
                                } else {
                                    val errMsg: String? = ErrorMessages().getErrorMessage(this, it?.getErrorMessage(), Constants.API.invite)

                                }
                            })
                }
            }
        }
    }

    private fun DoStartConsultationOtherProvider(context: Context, patient: Patient) {
        val providerId: Long? = PrefUtility().getProviderId(this)
        if (!UtilityMethods().isInternetConnected(this)!!) {
            CustomSnackBar.make(
                binding?.content, this, CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity), CustomSnackBar.TOP, 3000, 0
            )?.show()
            return
        }
        showProgressBar(PBMessageHelper().getMessage(this, getString(R.string.invite_provider)))
        val otherRebroadcastRequest = OtherRebroadcastRequest()
        otherRebroadcastRequest.setBspProviderId(patient.getBdProviderId())
        otherRebroadcastRequest.setOtherBspProviderId(providerId.toString())
        otherRebroadcastRequest.setPatientId(java.lang.String.valueOf(patient.getId()))
        Log.d(
            TAG,
            "DoStartConsultationOtherProvider request :  " + Gson().toJson(otherRebroadcastRequest)
        )
        viewModel?.reconsultOtherPatient(otherRebroadcastRequest)?.observe(this) {
            val commonResponse=it.body()
            Log.d(
                TAG, "DoStartConsultationOtherProvider response : " + Gson().toJson(commonResponse)
            )
            dismissProgressBar()
            if (commonResponse != null && commonResponse.status!= null && commonResponse.status!!) {
                getCensusPatientList()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    commonResponse?.getErrorMessage(), Constants.API.invite
                )
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class PatientSortByTime : Comparator<Patient?> {


        override fun compare(patient1: Patient?, patient2: Patient?): Int {
            return if (patient1?.getTime() == null || patient2 == null || patient2.getTime() == null
            ) {
                Int.MIN_VALUE
            } else patient2.getTime()!!.compareTo(patient1.getTime()!!)
        }
    }


    private operator fun Any?.iterator(): Iterator<Patient> {
    return  iterator()
    }



    private operator fun Any?.get(i: Patient): Patient {
        return  i
    }
    private val Any?.indices: Any
        get() {return indices}
}

