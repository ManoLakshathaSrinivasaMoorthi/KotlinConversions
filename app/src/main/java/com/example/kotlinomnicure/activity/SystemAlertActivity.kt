package com.example.kotlinomnicure.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.SystemAlertAdapter
import com.example.kotlinomnicure.databinding.ActivitySystemAlertBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import com.example.kotlinomnicure.viewmodel.SystemAlertViewModel
import omnicurekotlin.example.com.providerEndpoints.model.SystemAlerts


class SystemAlertActivity : BaseActivity(){

    private val TAG = SystemAlertActivity::class.java.simpleName
    private var binding: ActivitySystemAlertBinding? = null
    private var viewModel: SystemAlertViewModel? = null
    private var systemAlerts: SystemAlerts? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapterRecycler: SystemAlertAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_system_alert)
        viewModel = ViewModelProvider(this).get(SystemAlertViewModel::class.java)
        initToolbar()
        initViews()
    }

    private fun initToolbar() {
        setSupportActionBar(binding?.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
    }

    private fun initViews() {
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding?.rvSystemAlerts?.layoutManager = linearLayoutManager
        getSystemAlerts()
    }

    private fun getSystemAlerts() {
        showProgressBar()
        val providerId = PrefUtility().getProviderId(this)
        Log.d(TAG, "providerId$providerId")
        if (providerId != null) {
            viewModel!!.getSystemAlerts(providerId)?.observe(this, { response: SystemAlerts? ->
                dismissProgressBar()
                if (response != null && response.status) {
                    val gson = Gson()
                    systemAlerts = response
                    Log.d(TAG, "SYSTEM ALERT" + Gson().toJson(response))
                    populateSystemAlerts()
                } else if (response?.errorMessage != null && !TextUtils.isEmpty(response.errorMessage)) {
                    val errMsg = ErrorMessages().getErrorMessage(this, response.errorMessage,
                        Constants.API.getDocBoxPatientList)
                    Log.d(TAG, "getSystemAlerts: $errMsg")

                    if (errMsg != null) {
                        CustomSnackBar.make(binding?.idContainerLayout, this,
                            CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 8)?.show()
                    }
                } else {
                    Log.d(TAG, "getSystemAlerts: " + "else " + Gson().toJson(response))
                    CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                        getString(R.string.api_error), CustomSnackBar.TOP, 3000, 8)?.show()
                }
            })
        }
    }

    fun onErrorPopup(errMsg: String?) {
        if (errMsg != null) {
            CustomSnackBar.make(binding?.idContainerLayout, this, CustomSnackBar.WARNING,
                errMsg, CustomSnackBar.TOP, 3000, 1)?.show()
        }

    }


    private fun populateSystemAlerts() {
        if (adapterRecycler == null) {
            adapterRecycler = SystemAlertAdapter(this, systemAlerts)

            binding?.rvSystemAlerts?.adapter = adapterRecycler
        }
    }
}