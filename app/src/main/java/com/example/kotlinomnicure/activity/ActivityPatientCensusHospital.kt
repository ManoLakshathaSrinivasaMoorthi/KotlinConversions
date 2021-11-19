package com.example.kotlinomnicure.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.CensusHospitalListAdapter
import com.example.kotlinomnicure.adapter.CensusHospitalListViewAdapter
import com.example.kotlinomnicure.adapter.CensusWardListAdapter
import com.example.kotlinomnicure.databinding.ActivityPatientCensusHospitalBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.viewmodel.CensusHospitalListViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital

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
        viewModel = ViewModelProvider(this).get(CensusHospitalListViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCensusHospitalList?.layoutManager = linearLayoutManager
        binding?.imgBack?.setOnClickListener { v ->
            handleMultipleClick(v)
            finish()
        }
        binding?.imgSearch?.setOnClickListener { v ->
            handleMultipleClick(v)
            binding?.llSearch?.visibility = View.VISIBLE
        }
        binding?.closeSearch?.setOnClickListener { view ->
            if (TextUtils.isEmpty(binding?.searchEditText?.text.toString())) {
                binding?.llSearch?.visibility = View.GONE
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
            } else if (binding?.searchEditText?.text.toString().isNotEmpty()) {
                binding?.searchEditText?.setText("")
            }
        }
        getCensusHospitalList()
        setSearchTextWatcher()
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun getCensusHospitalList() {
        showProgressBar()
        val providerId: Long? = PrefUtility().getProviderId(this)
        providerId?.let { viewModel?.getHospitalList(it)?.observe(this) { response ->
                dismissProgressBar()
                if (response.getHospitalList() != null && !response.getHospitalList()!!.isEmpty()) {
                    Log.d(TAG, "getCensusHospitalList response : " + Gson().toJson(response))
                    censusHospitalListAdapter = CensusHospitalListAdapter(object : CensusHospitalListViewAdapter.HospitalRecyclerListener {

                        override fun onItemSelected(hospital: Hospital?) {
                            Log.d(TAG, "selected name : " + hospital?.getId().toString() + " ---- " + hospital?.getName())
                            val intent = Intent(this@ActivityPatientCensusHospital, ActivityPatientCensusWard::class.java)
                            intent.putExtra("hospitalID", hospital?.getId())
                            intent.putExtra("hospitalName", hospital?.getName())
                            intent.putExtra("hospitalAddress", hospital?.getSubRegionName())
                            startActivity(intent)
                        }
                    }, response.getHospitalList() as List<Hospital>?, "")
                    binding?.rvCensusHospitalList?.setAdapter(censusHospitalListAdapter)
                    censusHospitalListAdapter?.notifyDataSetChanged()
                    mHospitalList = response.getHospitalList() as List<Hospital>
                    censusHospitalListAdapter?.setOnSearchResultListener(object : CensusWardListAdapter.OnSearchResultListener {
                        override fun onSearchResult(count: Int) {
                            if (count <= 0) {
                                showEmptyErrorMessage()
                            } else {
                                binding?.noPatientLayout?.setVisibility(View.GONE)
                            }
                        }
                    })
                } else if (response.getErrorMessage() != null) {
                    dismissProgressBar()
                    val errMsg: String? = ErrorMessages().getErrorMessage(
                        this@ActivityPatientCensusHospital,
                        response.getErrorMessage(),
                        Constants.API.getHospital
                    )
                    errMsg?.let { it1 ->
                        CustomSnackBar.make(
                            binding?.getRoot(), this, CustomSnackBar.WARNING,
                            it1, CustomSnackBar.TOP, 3000, 0
                        )?.show()
                    }
                    Log.d(
                        TAG,
                        "getCensusHospitalList getErrorMessage : " + response.getErrorMessage()
                    )
                } else {
                    dismissProgressBar()
                    if (censusHospitalListAdapter?.getItemCount()!! <= 0) {
                        showEmptyErrorMessage()
                    } else {
                        binding?.rvCensusHospitalList?.setVisibility(View.VISIBLE)
                        binding?.noPatientLayout?.setVisibility(View.GONE)
                    }
                }
            }
        }
    }

    private fun onQuerySearch(query: String) {
        if (censusHospitalListAdapter != null) {
            censusHospitalListAdapter?.getFilter()?.filter(query)
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
        if (TextUtils.isEmpty(binding?.searchEditText?.text.toString())) {
            // This means there is no selected filter applied so resetted to default all section
            binding?.noPatientLayout?.visibility = View.VISIBLE
            binding?.noPatientsImage?.visibility = View.VISIBLE
            binding?.noPatientTitle?.visibility = View.GONE
            binding?.noPatientText?.text = resources.getString(R.string.no_census_hospital_found)
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