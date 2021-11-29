package com.example.kotlinomnicure.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.HospitalListAdapter
import com.example.kotlinomnicure.databinding.ActivityHospitalListBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.ValidationUtil
import com.example.kotlinomnicure.viewmodel.HospitalListViewModel
import com.google.gson.Gson
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital

open class ActivityHospitalList : BaseActivity() {

    private val TAG by lazy { ActivityHospitalList::class.java.simpleName }
    protected var binding: ActivityHospitalListBinding? = null
    private var selectedHosp=""
    private var viewModel: HospitalListViewModel? = null
    private var hospitalListAdapter: HospitalListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hospital_list)
        viewModel = ViewModelProvider(this).get(HospitalListViewModel::class.java)
        val extras = intent.extras
        selectedHosp = extras?.getString(Constants.IntentKeyConstants.SELECTED_HOSPITAL, "").toString()
        initViews()
    }


    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!errMsg?.isEmpty()!!) {
//            UtilityMethods.showErrorSnackBar(binding.containerLayout, errMsg, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(binding?.containerLayout, this,
                CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0)?.show()
            return false
        }
        return true
    }

    private fun initViews() {
        binding?.idBackButton?.setOnClickListener { finish() }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvHospitalList?.layoutManager = linearLayoutManager
        getHospitalList()
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        binding?.searchHospital?.isActivated = true
        binding?.searchHospital?.queryHint = resources.getString(R.string.search_hospital)
        binding?.searchHospital?.onActionViewExpanded()
        binding?.searchHospital?.isIconified = false
        binding?.searchHospital?.clearFocus()
        binding?.searchHospital?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        binding?.searchHospital?.maxWidth = Int.MAX_VALUE
        binding?.searchHospital?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                hospitalListAdapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                hospitalListAdapter?.filter?.filter(query)
                return false
            }
        })
    }

    private fun getHospitalList() {
        viewModel?.getHospitalList()?.observe(this, {
            val response= it.body()
            dismissProgressBar()
            if (response?.getHospitalList() != null && !response.getHospitalList()!!.isEmpty()) {
                Log.d(TAG, "getHospitalList response : " + Gson().toJson(response))
                hospitalListAdapter = HospitalListAdapter(object :
                    HospitalListAdapter.HospitalRecyclerListener {


                    override fun onItemSelected(hospital: Hospital?) {
                        Log.d(
                            TAG,
                            "selected name : " + hospital?.getId()
                                .toString() + "----" + hospital?.getName()
                        )
                        val intent = Intent()
                        intent.putExtra("hospitalID", hospital?.getId())
                        intent.putExtra("hospitalName", hospital?.getName())
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }, response.getHospitalList()!!, selectedHosp)
                binding?.rvHospitalList?.adapter = hospitalListAdapter
                hospitalListAdapter?.notifyDataSetChanged()
            } else if (response?.getErrorMessage() != null) {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this@ActivityHospitalList,
                    response.getErrorMessage(),
                    Constants.API.getHospital
                )
                errMsg?.let {
                    CustomSnackBar.make(
                        binding?.containerLayout, this, CustomSnackBar.WARNING,
                        it, CustomSnackBar.TOP, 3000, 0
                    )
                }?.show()
                Log.d(TAG, "getHospitalList getErrorMessage : " + response.getErrorMessage())
            } else {
                CustomSnackBar.make(
                    binding?.containerLayout, this, CustomSnackBar.WARNING,
                    getString(R.string.no_hospital_list), CustomSnackBar.TOP, 3000, 0
                )!!.show()
            }
        })
    }
}
