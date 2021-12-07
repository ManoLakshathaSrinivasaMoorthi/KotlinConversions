package com.example.kotlinomnicure.activity

import android.app.Activity
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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinomnicure.viewmodel.CensusHospitalListViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.CensusHospitalListAdapter
import com.example.kotlinomnicure.databinding.ActivityPatientCensusHospitalBinding

import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital


class ActivityPatientCensusHospital : BaseActivity() {
    private val TAG = ActivityPatientCensusHospital::class.java.simpleName
    protected var binding: ActivityPatientCensusHospitalBinding? = null
    var selectedHosp = ""
    private var viewModel: CensusHospitalListViewModel? = null
    private var censusHospitalListAdapter: CensusHospitalListAdapter? = null
    private var mHospitalList: List<Hospital?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_census_hospital)
        viewModel = ViewModelProviders.of(this)[CensusHospitalListViewModel::class.java]
        initViews()
    }

    fun initViews() {
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
            if (TextUtils.isEmpty(binding!!.searchEditText.text.toString())) {
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
        mHandler!!.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun getCensusHospitalList() {
        showProgressBar()
        val providerId: Long? = PrefUtility().getProviderId(this)
        providerId?.let { viewModel?.getHospitalList(it) }!!.observe(this, { response ->
            dismissProgressBar()
            if (response!!.getHospitalList() != null && response.getHospitalList()!!.isNotEmpty()) {
//                Log.d(TAG, "getCensusHospitalList response : " + new Gson().toJson(response));
                censusHospitalListAdapter =
                    CensusHospitalListAdapter(object :
                        CensusHospitalListAdapter.HospitalRecyclerListener {
                     override   fun onItemSelected(hospital: Hospital?) {
//                        Log.d(TAG, "selected name : " + hospital.getId() + " ---- " + hospital.getName());
                            val intent = Intent(
                                this@ActivityPatientCensusHospital,
                                ActivityPatientCensusWard::class.java
                            )
                            intent.putExtra("hospitalID", hospital?.getId())
                            intent.putExtra("hospitalName", hospital?.getName())
                            intent.putExtra("hospitalAddress", hospital?.getSubRegionName())
                            startActivity(intent)
                        }


                    }, response.getHospitalList()!!, "")
                binding!!.rvCensusHospitalList.adapter = censusHospitalListAdapter
                censusHospitalListAdapter?.notifyDataSetChanged()
                mHospitalList = response.getHospitalList()
                censusHospitalListAdapter!!.setOnSearchResultListener(object :
                    CensusHospitalListAdapter.OnSearchResultListener {
                    override fun onSearchResult(count: Int) {
                        if (count <= 0) {
                            showEmptyErrorMessage()
                        } else {
                            binding!!.noPatientLayout.visibility = View.GONE
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
                CustomSnackBar.make(
                    binding!!.root, this, CustomSnackBar.WARNING,
                    errMsg, CustomSnackBar.TOP, 3000, 0
                )!!.show()
                //                Log.d(TAG, "getCensusHospitalList getErrorMessage : " + response.getErrorMessage());
            } else {
                dismissProgressBar()
                if (response.getHospitalList()!!.isEmpty()) {
                    showEmptyErrorMessage()
                } else {
                    binding!!.rvCensusHospitalList.visibility = View.VISIBLE
                    binding!!.noPatientLayout.visibility = View.GONE
                }
                //                if (censusHospitalListAdapter.getItemCount() <= 0) {
//                    showEmptyErrorMessage();
//                } else {
//                    binding.rvCensusHospitalList.setVisibility(View.VISIBLE);
//                    binding.noPatientLayout.setVisibility(View.GONE);
//                }
            }
        })
    }

    private fun onQuerySearch(query: String) {
        if (censusHospitalListAdapter != null) {
            censusHospitalListAdapter!!.filter!!.filter(query)
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

    private fun showEmptyErrorMessage() {
        if (TextUtils.isEmpty(binding!!.searchEditText.text.toString())) {
            // This means there is no selected filter applied so resetted to default all section
            binding!!.noPatientLayout.visibility = View.VISIBLE
            binding!!.noPatientsImage.visibility = View.VISIBLE
            binding!!.noPatientTitle.visibility = View.GONE
            binding!!.noPatientText.text = resources.getString(R.string.no_census_hospital_found)
        } else {
            showFilterErrorMessage()
        }
    }

    private fun showFilterErrorMessage() {
        binding!!.noPatientLayout.visibility = View.VISIBLE
        binding!!.noPatientsImage.visibility = View.GONE
        binding!!.noPatientTitle.visibility = View.VISIBLE
        binding!!.noPatientText.text = resources.getString(R.string.no_results_for_filter)
    }

}