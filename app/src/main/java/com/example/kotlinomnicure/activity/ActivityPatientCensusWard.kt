package com.example.kotlinomnicure.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.CensusWardListViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.CensusHospitalListViewAdapter
import com.example.kotlinomnicure.adapter.CensusWardListAdapter
import com.example.kotlinomnicure.databinding.ActivityPatientCensusWardBinding
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility

import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientList
import java.util.*

class ActivityPatientCensusWard : BaseActivity() {
    private val TAG = ActivityPatientCensusWard::class.java.simpleName
    var dialog: Dialog? = null
    protected var binding: ActivityPatientCensusWardBinding? = null
    var strHospitalName: String? = null
    var strHospitalAddress: String? = null
    private var strFromDashboard: String? = null
    var strDashboardHospitalName: String? = null
    var strDashboardHospitalAddress: String? = null
    private var strHospitalID: Long? = null
    private  var strDashboardHospitalID: Long? = null
    private var viewModel: CensusWardListViewModel? = null
    private var censusWardListAdapter: CensusWardListAdapter? = null
    private var hospitalListAdapter: CensusHospitalListViewAdapter? = null
    private var mPatientWardList: ArrayList<WardPatientList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_census_ward)
        viewModel = ViewModelProvider(this).get(CensusWardListViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        val strRole: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        binding?.imgBack?.setOnClickListener { onBackPressed() }
        binding?.dropdownLayout?.setOnClickListener { v ->
            handleMultipleClick(v)
            hospitalListDialog(this)
        }
        binding?.dropImg?.setOnClickListener { v ->
            handleMultipleClick(v)
            hospitalListDialog(this)
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCensusWardList?.layoutManager = linearLayoutManager
        binding?.llSearch?.setOnClickListener {
            binding?.searchLayout?.visibility = View.VISIBLE
        }
        binding?.closeSearch?.setOnClickListener { view ->
            if (TextUtils.isEmpty(binding?.searchEditText?.text.toString())) {
                binding?.searchLayout?.visibility = View.GONE
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            } else if (binding?.searchEditText?.text.toString().isNotEmpty()) {
                binding?.searchEditText?.setText("")
            }
        }
        if (strRole == "BD") {
            strFromDashboard = intent.getStringExtra(Constants.IntentKeyConstants.SCREEN_TYPE)
            Log.d(TAG, "strFromDashboard : $strFromDashboard")
        }
        if (!TextUtils.isEmpty(strFromDashboard) && strFromDashboard.equals(Constants.IntentKeyConstants.SCREEN_DASHBOARD, ignoreCase = true)) {
            strDashboardHospitalID = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.HOSPITAL_ID, 0L)
            strDashboardHospitalName = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.HOSPITAL_NAME, "")
            strDashboardHospitalAddress = intent.getStringExtra(Constants.IntentKeyConstants.HOSPITAL_ADDRESS)
            if (!TextUtils.isEmpty(strDashboardHospitalName)) {
                if (strDashboardHospitalName?.trim { it <= ' ' }?.length !! > 20) {
                    val subStrHospitalName: String = strDashboardHospitalName?.substring(0, 20) + "..."
                    binding?.txtSelect?.text = "$subStrHospitalName - $strDashboardHospitalAddress"
                } else {
                    binding?.txtSelect?.setText(strDashboardHospitalName + " - " + strDashboardHospitalAddress)
                }
            }
            binding?.dropdownLayout?.isEnabled = false
            binding?.dropImg?.isEnabled = false
            getBPCensusWardList()
        }
        if (strRole == "RD") {
            strHospitalName = intent.getStringExtra("hospitalName")
            strHospitalAddress = intent.getStringExtra("hospitalAddress")
            strHospitalID = intent.getLongExtra("hospitalID", 0)
            Log.d(TAG, "strHospitalID : $strHospitalID")
            if (!TextUtils.isEmpty(strHospitalName)) {
                if (strHospitalName?.trim { it <= ' ' }?.length!! > 20) {
                    val subHospitalName = strHospitalName?.substring(0, 20) + "..."
                    binding?.txtSelect?.text = "$subHospitalName - $strHospitalAddress"
                } else {
                    binding?.txtSelect?.text = "$strHospitalName - $strHospitalAddress"
                }
            }
            getHospitalList()
            getCensusWardList()
        }
        binding?.swipeLayout?.setOnRefreshListener {
            binding?.swipeLayout?.isRefreshing = false
            val strRole: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
            if (strRole == "RD") {
                getCensusWardList()
                if (censusWardListAdapter != null) {
                    censusWardListAdapter?.updateList(mPatientWardList)
                    censusWardListAdapter?.notifyDataSetChanged()
                    if (censusWardListAdapter?.itemCount!! <= 0) {
                        showEmptyErrorMessage()
                    } else {
                        showEmptyErrorMessage()
                    }
                }
                binding?.searchLayout?.visibility = View.GONE
                binding?.searchEditText?.setText("")
            }
        }
        setSearchTextWatcher()
    }

    private fun onQuerySearch(query: String) {
        if (censusWardListAdapter != null) {
            censusWardListAdapter?.filter?.filter(query)
        }
    }

    override fun onResume() {
        super.onResume()
        val strRole: String? = PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        if (strRole == "RD") {
            getCensusWardList()
        } else {
            getBPCensusWardList()
        }
    }

    private fun setSearchTextWatcher() {
        binding!!.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                onQuerySearch(editable.toString())
            }
        })
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun hospitalListDialog(context: Context?) {
        dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog?.setContentView(R.layout.filter_by_location_dialog)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.attributes?.windowAnimations = R.style.SlideUpDialog
        val imgBack = dialog?.findViewById<View>(R.id.imgBack) as ImageView
        val txtTitle = dialog?.findViewById<View>(R.id.txtTitle) as TextView
        imgBack.visibility = View.GONE
        val imgCancel = dialog?.findViewById<ImageButton>(R.id.imgCancel)
        txtTitle.text = getString(R.string.select_hospital_location)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val rvHospitalList: RecyclerView = dialog!!.findViewById(R.id.recyclerviewLocation)
        rvHospitalList.layoutManager = layoutManager
        rvHospitalList.setHasFixedSize(true)
        rvHospitalList.adapter = hospitalListAdapter

        imgCancel?.setOnClickListener { dialog!!.dismiss() }
        dialog!!.show()
    }

    private fun getHospitalList() {
        val providerId: Long? = PrefUtility().getProviderId(this)
        providerId?.let { it ->
            viewModel?.getHospitalList(it)?.observe(this) {
                val response=it
                if (response?.getHospitalList() != null && response.getHospitalList()!!.isNotEmpty()) {
                    Log.d(TAG, "getHospitalList response : " + Gson().toJson(response))
                    hospitalListAdapter = strHospitalID?.let { it1 ->
                        CensusHospitalListViewAdapter(object :
                            CensusHospitalListViewAdapter.HospitalRecyclerListener {
                            override fun onItemSelected(hospital: Hospital?) {
                                val strName: String? = hospital?.getName()
                                val strSubRegionName: String? = hospital?.getSubRegionName()
                                strHospitalID = hospital?.getId()
                                if (!TextUtils.isEmpty(strName)) {
                                    if (strName?.trim { it <= ' ' }?.length!! > 20) {
                                        val subHospitalName =
                                            strName?.substring(0, 20) + "..."
                                        binding!!.txtSelect.text =
                                            "$subHospitalName - $strSubRegionName"
                                    } else {
                                        binding!!.txtSelect.text = "$strName - $strSubRegionName"
                                    }
                                }
                                binding!!.txtSelect.setTextColor(resources.getColor(R.color.bg_blue))
                                dialog!!.dismiss()
                                getCensusWardList()
                            }
                        }, response.getHospitalList() as List<Hospital>?, it1)
                    }
                } else if (response?.getErrorMessage() != null) {
                    val errMsg: String? = ErrorMessages().getErrorMessage(this, response.getErrorMessage(), Constants.API.getHospital)
                    errMsg?.let { it1 ->
                        CustomSnackBar.make(binding!!.root, this, CustomSnackBar.WARNING,
                            it1, CustomSnackBar.TOP, 3000, 0)?.show()
                    }
                    Log.d(TAG, "getHospitalList getErrorMessage : " + response.getErrorMessage())
                } else {
                    CustomSnackBar.make(binding?.root, this, CustomSnackBar.WARNING,
                        getString(R.string.no_hospital_list), CustomSnackBar.TOP, 3000, 0
                    )?.show()
                }
            }
        }
    }

    private fun getCensusWardList() {
        showProgressBar(PBMessageHelper().getMessage(this, "Getting ward list"))
        strHospitalID?.let { viewModel?.getWardList(it)?.observe(this) {
            dismissProgressBar()
            val response=it
                if (response?.getWardPatientList() != null && !response.getWardPatientList()!!.isEmpty()) {
                    binding!!.rvCensusWardList.visibility = View.VISIBLE
                    val wardPatientList: ArrayList<WardPatientList> = response.getWardPatientList() as ArrayList<WardPatientList>
                    val itr: MutableIterator<WardPatientList> = wardPatientList.iterator()
                    while (itr.hasNext()) {
                        val ward: WardPatientList = itr.next()
                        if (ward.getWardName()?.contentEquals("All") == true) {
                            itr.remove()
                        } else if (ward.getCount()!! <= 0) {
                            itr.remove()
                        }
                    }
                    Log.d(TAG, "getCensusWardList response : " + Gson().toJson(response))
                    censusWardListAdapter = CensusWardListAdapter(object :
                        CensusWardListAdapter.HospitalRecyclerListener {

                        override fun onItemSelected(ward: WardPatientList?) {
                            Log.d(TAG, "selected name : " + ward?.getWardName())
                            val intent = Intent(ActivityPatientCensusWard(), ActivityPatientCensusPatient::class.java)
                            intent.putExtra("hospitalName", strHospitalName)
                            intent.putExtra("hospitalAddress", strHospitalAddress)
                            intent.putExtra("wardName", ward?.getWardName())
                            intent.putExtra("wardHospitalId", response.getHospitalId())
                            startActivityForResult(intent, 502)
                        }


                    }, wardPatientList, "")
                    binding!!.rvCensusWardList.adapter = censusWardListAdapter
                    mPatientWardList = wardPatientList
                    censusWardListAdapter?.setOnSearchResultListener(object : CensusWardListAdapter.OnSearchResultListener {
                        override fun onSearchResult(count: Int) {
                            if (count <= 0) {
                                showEmptyErrorMessage()
                            } else {
                                binding!!.rvCensusWardList.visibility = View.VISIBLE
                                binding!!.noPatientLayout.visibility = View.GONE
                            }
                        }
                    })
                    censusWardListAdapter?.notifyDataSetChanged()
                    if (wardPatientList.size > 0) {
                        binding?.rvCensusWardList?.visibility = View.VISIBLE
                        binding?.noPatientLayout?.visibility = View.GONE
                    } else {
                        binding?.rvCensusWardList?.visibility = View.GONE
                        binding?.noPatientLayout?.visibility = View.VISIBLE
                    }
                } else {
                    binding?.rvCensusWardList?.visibility = View.GONE
                    binding?.noPatientLayout?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getBPCensusWardList() {
        showProgressBar(PBMessageHelper().getMessage(this, "Getting ward list"))
        strDashboardHospitalID?.let { viewModel?.getWardList(it)?.observe(this) {
            dismissProgressBar()
             val response=it
                if (response?.getWardPatientList() != null && !response.getWardPatientList()!!.isEmpty()) {
                    binding!!.rvCensusWardList.visibility = View.VISIBLE
                    val wardPatientList: ArrayList<WardPatientList> = response.getWardPatientList() as ArrayList<WardPatientList>
                    val itr: MutableIterator<WardPatientList> =
                        wardPatientList.iterator()
                    while (itr.hasNext()) {
                        val ward: WardPatientList = itr.next()
                        if (ward.getWardName()?.contentEquals("All") == true) {
                            itr.remove()
                        } else if (ward.getCount()!! <= 0) {
                            itr.remove()
                        }
                    }
                    Log.d(TAG, "getCensusWardList response : " + Gson().toJson(response))
                    censusWardListAdapter = CensusWardListAdapter(object : CensusWardListAdapter.HospitalRecyclerListener {
                        override fun onItemSelected(ward: WardPatientList?) {
                            var intent = Intent(this, ActivityPatientCensusPatient::class.java)
                            intent.putExtra("hospitalName", strDashboardHospitalName)
                            intent.putExtra("hospitalAddress", strDashboardHospitalAddress)
                            intent.putExtra("wardName", ward?.getWardName())
                            intent.putExtra("wardHospitalId", response.getHospitalId())
                            startActivityForResult(intent, 503)
                        }
                    }, wardPatientList, "")
                    binding!!.rvCensusWardList.adapter = censusWardListAdapter
                    censusWardListAdapter?.setOnSearchResultListener(object :
                        CensusWardListAdapter.OnSearchResultListener {
                        override fun onSearchResult(count: Int) {
                            if (count <= 0) {
                                showEmptyErrorMessage()
                            } else {
                                binding!!.rvCensusWardList.visibility = View.VISIBLE
                                binding!!.noPatientLayout.visibility = View.GONE
                            }
                        }
                    })
                    censusWardListAdapter?.notifyDataSetChanged()


                } else {
                    binding!!.rvCensusWardList.visibility = View.GONE
                    binding!!.noPatientLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 502) {
            if (resultCode == RESULT_OK) {
                getCensusWardList()
            }
        }
        if (requestCode == 503) {
            if (resultCode == RESULT_OK) {
                getBPCensusWardList()
            }
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
    }

    private fun showEmptyErrorMessage() {
        if (TextUtils.isEmpty(binding?.searchEditText?.text.toString())) {
            binding?.noPatientLayout?.visibility = View.VISIBLE
            binding?.noPatientsImage?.visibility = View.VISIBLE
            binding?.noPatientTitle?.visibility = View.GONE
            binding?.noPatientText?.text = resources.getString(R.string.no_census_ward_found)
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
}