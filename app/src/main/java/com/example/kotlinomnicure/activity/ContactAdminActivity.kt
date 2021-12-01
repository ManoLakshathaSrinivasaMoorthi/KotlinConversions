package com.example.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import omnicurekotlin.example.com.providerEndpoints.model.Provider
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ActivityContactAdminBinding
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.ContactAdminViewModel
import omnicurekotlin.example.com.providerEndpoints.model.ContactAdminParams


class ContactAdminActivity : BaseActivity() {
    private val TAG = ContactAdminActivity::class.java.simpleName
    private val RecordAudioRequestCode = 1
    private var binding: ActivityContactAdminBinding? = null
    private var viewModel: ContactAdminViewModel? = null
    private var currentUser: Provider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_admin)
        viewModel = ViewModelProvider(this).get(ContactAdminViewModel::class.java)
        initToolbar()
        initViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initToolbar() {

        // Back button click listener
        binding?.imgBack?.setOnClickListener { v -> finish() }
    }


    private fun initViews() {
        binding?.btnSubmit?.setOnClickListener(View.OnClickListener { view ->
            handleMultipleClick(view)
            sendAdminMessage()
        })
    }

    private fun sendAdminMessage() {
        if (!isValid()) {
            return
        }
        showProgressBar()
        val providerId: Long? = PrefUtility().getProviderId(this)
        val strDesignation: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        val strRole: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
        val hospital: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.R_PROVIDER_TYPE, "")
        val email: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.EMAIL, "")
        val strUserName: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        val params = ContactAdminParams()
        params.setProviderId(providerId)
        params.setMessage(binding?.textAdmin?.getText().toString().trim())
        params.setAppVersion(getVersion())
        params.setUserDevice("android")

        if (email != null) {
            params.setEmail(email)
        }
        params.setUserName(strUserName)
        if (strRole == "RD") {
            params.setSubUserType(strDesignation)

        } else if (strRole == "BD") {
            params.setSubUserType("Bedside Provider")

        }
        if (getCurrentUser() != null) {
            if (!TextUtils.isEmpty(getCurrentUser()?.getLcpType()) &&
                getCurrentUser()?.getLcpType()
                    .equals(Constants.KeyHardcodeToken.LCP_TYPE_HOME)
            ) {
                params.setSubUserType("Homecare Provider")

            }
        }
        Log.d(TAG, "params contact admin$params")
        viewModel?.contactAdminEmail(params)?.observe(this) { response ->
            Log.d(TAG, "contact_admin_response $response")
            dismissProgressBar()
            if (response != null && response.status != null && response.status!!) {
                onSuccess()
            } else {
                binding?.btnSubmit?.setEnabled(true)
                var errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    response?.getErrorMessage(),
                    Constants.API.getDocBoxPatientList
                )
                if (errMsg.equals("exception", ignoreCase = true)) {
                    errMsg = "Not found"
                }
                onErrorPopup(errMsg)
            }
        }
    }

    private fun getCurrentUser(): Provider? {
        if (currentUser == null) {
            currentUser = PrefUtility().getProviderObject(this)
        }
        return currentUser
    }

    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (!errMsg!!.isEmpty()) {
            CustomSnackBar.make(
                binding?.idContainerLayout,
                this,
                CustomSnackBar.WARNING,
                errMsg,
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return false
        }
        return true
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        mHandler?.postDelayed({ view.isEnabled = true }, 500)
    }

    fun getVersion(): String? {
        var version = "0"
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            version = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Exception:", e.cause)
        }
        return version
    }

    fun onSuccess() {

       CustomSnackBar.make(
            binding?.idContainerLayout,
            this,
            CustomSnackBar.SUCCESS,
            getString(R.string.message_sent_successfully),
            CustomSnackBar.TOP,
            3000,
            8
        )?.show()
    }

    fun onErrorPopup(errMsg: String?) {
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
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
            this,
            "Permission Granted",
            Toast.LENGTH_SHORT
        ).show()
    }

}