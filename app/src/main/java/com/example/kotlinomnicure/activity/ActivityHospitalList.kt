package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.dailytasksamplepoc.kotlinomnicure.adapter.HospitalListAdapter

import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HospitalListViewModel
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.ValidationUtil
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital

class ActivityHospitalList : BaseActivity() {
    private val TAG: String = ActivityHospitalList::class.java.getSimpleName()
    protected var binding: ActivityHospitalListBinding? = null
    var selectedHosp = ""
    private var viewModel: HospitalListViewModel? = null
    private var hospitalListAdapter: HospitalListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hospital_list)
        viewModel = ViewModelProviders.of(this)[HospitalListViewModel::class.java]

        val extras = intent.extras
        selectedHosp = extras!!.getString(Constants.IntentKeyConstants.SELECTED_HOSPITAL, "")

        initViews()
    }
    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (errMsg != null) {
            if (!errMsg.isEmpty()) {

                CustomSnackBar.make(
                    binding!!.containerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
                return false
            }
        }
        return true
    }

    private fun initViews() {
        binding!!.idBackButton.setOnClickListener { view -> finish() }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding!!.rvHospitalList.layoutManager = linearLayoutManager
        getHospitalList()
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        binding!!.searchHospital.isActivated = true
        binding!!.searchHospital.queryHint = resources.getString(R.string.search_hospital)
        binding!!.searchHospital.onActionViewExpanded()
        binding!!.searchHospital.isIconified = false
        binding!!.searchHospital.clearFocus()
        binding!!.searchHospital.setSearchableInfo(
            searchManager
                .getSearchableInfo(componentName)
        )
        binding!!.searchHospital.maxWidth = Int.MAX_VALUE
        binding!!.searchHospital.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                hospitalListAdapter!!.getFilter()!!.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                hospitalListAdapter!!.getFilter()!!.filter(query)
                return false
            }
        })
    }

    private fun getHospitalList() {
        val providerId: Long? = PrefUtility().getProviderId(this)

        viewModel?.getHospitalList()?.observe(this) { response ->
            dismissProgressBar()
            if (response != null) {
                if (response.getHospitalList() != null && !response.getHospitalList()!!.isEmpty()) {
                    Log.d(TAG, "getHospitalList response : " + Gson().toJson(response))
                    if (response != null) {
                        hospitalListAdapter = HospitalListAdapter(object : HospitalListAdapter.HospitalRecyclerListener {
                            override  fun onItemSelected(hospital: Hospital) {
                                Log.d(TAG, "selected name : " + hospital.id.toString() + "----" + hospital.name)
                                val intent = Intent()
                                intent.putExtra("hospitalID", hospital.id)
                                intent.putExtra("hospitalName", hospital.name)
                                setResult(RESULT_OK, intent)
                                finish()
                            }


                        }, response.getHospitalList()!!, selectedHosp)
                    }
                    binding!!.rvHospitalList.adapter = hospitalListAdapter
                    hospitalListAdapter!!.notifyDataSetChanged()
                } else if (response != null) {
                    if (response.getErrorMessage() != null) {
                        val errMsg: String? = ErrorMessages().getErrorMessage(
                            this,
                            response.getErrorMessage(),
                            Constants.API.getHospital
                        )
                        if (errMsg != null) {
                            CustomSnackBar.make(
                                binding!!.containerLayout, this, CustomSnackBar.WARNING,
                                errMsg, CustomSnackBar.TOP, 3000, 0
                            )?.show()
                        }
                        Log.d(TAG, "getHospitalList getErrorMessage : " + response.getErrorMessage())
                    } else {
                        CustomSnackBar.make(
                            binding!!.containerLayout, this, CustomSnackBar.WARNING,
                            getString(R.string.no_hospital_list), CustomSnackBar.TOP, 3000, 0
                        )?.show()
                    }
                }
            }
        }
    }
}