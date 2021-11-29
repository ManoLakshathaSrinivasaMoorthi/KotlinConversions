package com.mvp.omnicure.kotlinactivity.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityPatientCensusHospitalBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.BaseActivity
import com.example.dailytasksamplepoc.kotlinomnicure.adapter.CensusHospitalListAdapter
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import omnicurekotlin.example.com.hospitalEndpoints.model.HospitalListResponse


class ActivityPatientCensusHospital : BaseActivity() {
    private val TAG = ActivityPatientCensusHospital::class.java.simpleName
    protected var binding: ActivityPatientCensusHospitalBinding? = null
    var selectedHosp = ""
    private var viewModel: CensusHospitalListViewModel? = null
    private var censusHospitalListAdapter: CensusHospitalListAdapter? = null
    private var mHospitalList: List<Hospital>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_census_hospital)
        viewModel = ViewModelProviders.of(this).get(
            CensusHospitalListViewModel::class.java
        )
        initViews()
    }

    fun initViews() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCensusHospitalList?.setLayoutManager(linearLayoutManager)
        binding?.imgBack?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            finish()
        })
        binding?.imgSearch?.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)
            binding!!.llSearch.setVisibility(View.VISIBLE)
        })
        binding?.closeSearch?.setOnClickListener(View.OnClickListener { view ->
            if (TextUtils.isEmpty(binding?.searchEditText?.getText().toString())) {
                binding!!.llSearch.setVisibility(View.GONE)
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            } else if (binding!!.searchEditText.getText().toString().length > 0) {
                binding!!.searchEditText.setText("")
            }
        })
        getCensusHospitalList()
        setSearchTextWatcher()
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed(Runnable { view.isEnabled = true }, 500)
    }

    private fun getCensusHospitalList() {
        showProgressBar()
        val providerId = PrefUtility().getProviderId(this)
        viewModel!!.getHospitalList(providerId).observe(this) { response: HospitalListResponse ->
            dismissProgressBar()
            if (response.hospitalList != null && !response.hospitalList!!.isEmpty()) {
                Log.d(TAG, "getCensusHospitalList response : " + Gson().toJson(response))
                censusHospitalListAdapter = CensusHospitalListAdapter({ hospital ->
                    Log.d(
                        TAG,
                        "selected name : " + response.id + " ---- " + response.name
                    )
                    val intent = Intent(
                        this,
                        ActivityPatientCensusWard::class.java
                    )
                    intent.putExtra("hospitalID",response.id)
                    intent.putExtra("hospitalName", response.name)
                    intent.putExtra("hospitalAddress", response.subRegionName)
                    startActivity(intent)
                }, response.hospitalList!!, "")
                binding?.rvCensusHospitalList?.setAdapter(censusHospitalListAdapter)
                censusHospitalListAdapter!!.notifyDataSetChanged()
                mHospitalList = response.hospitalList as List<Hospital>?
                censusHospitalListAdapter!!.onSearchResultListener =
                    CensusHospitalListAdapter.OnSearchResultListener { count ->
                        if (count <= 0) {
                            showEmptyErrorMessage()
                        } else {
                            binding?.noPatientLayout?.setVisibility(View.GONE)
                        }
                    }
            } else if (response.errorMessage != null) {
                dismissProgressBar()
                val errMsg = ErrorMessages().getErrorMessage(
                    this,
                    response.errorMessage,
                    Constants.API.getHospital
                )
                if (errMsg != null) {
                    CustomSnackBar.make(
                        binding?.getRoot(), this, CustomSnackBar.WARNING,
                        errMsg, CustomSnackBar.TOP, 3000, 0
                    )?.show()
                }
                Log.d(
                    TAG,
                    "getCensusHospitalList getErrorMessage : " + response.errorMessage
                )
            } else {
                dismissProgressBar()
                if (response.hospitalList.isEmpty()) {
                    showEmptyErrorMessage()
                } else {
                    binding?.rvCensusHospitalList?.setVisibility(View.VISIBLE)
                    binding?.noPatientLayout?.setVisibility(View.GONE)
                }

            }
        }
    }

    private fun onQuerySearch(query: String) {
        if (censusHospitalListAdapter != null) {
            censusHospitalListAdapter!!.filter.filter(query)
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

    private fun showEmptyErrorMessage() {
        if (TextUtils.isEmpty(binding?.searchEditText?.getText().toString())) {
            // This means there is no selected filter applied so resetted to default all section
            binding?.noPatientLayout?.setVisibility(View.VISIBLE)
            binding?.noPatientsImage?.setVisibility(View.VISIBLE)
            binding?.noPatientTitle?.setVisibility(View.GONE)
            binding?.noPatientText?.setText(resources.getString(R.string.no_census_hospital_found))
        } else {
            showFilterErrorMessage()
        }
    }

    private fun showFilterErrorMessage() {
        binding?.noPatientLayout?.setVisibility(View.VISIBLE)
        binding?.noPatientsImage?.setVisibility(View.GONE)
        binding?.noPatientTitle?.setVisibility(View.VISIBLE)
        binding?.noPatientText?.setText(resources.getString(R.string.no_results_for_filter))
    }
}