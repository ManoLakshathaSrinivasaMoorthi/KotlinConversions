package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityCustomNotificationBinding
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model.AddNotificationDataRequest
import com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HomeViewModel
import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.CustomSnackBar
import com.example.kotlinomnicure.utils.ErrorMessages
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods

class CustomNotificationActivity : BaseActivity(){
    // Variables
    private val TAG = CustomNotificationActivity::class.java.simpleName
    protected var viewModel: HomeViewModel? = null
    var providerId: Long = 0
    var uid: String? = null
    private var customNotificationBinding: ActivityCustomNotificationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customNotificationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_custom_notification)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        //Initiating the toolbar with title
        initToolbar()

        //Initiating alerts switch views and updating the data
        initSwitchViews()

        // Getting the provider and uid from shared preference
        providerId = PrefUtility.getLongInPref(this,
            Constants.SharedPrefConstants.USER_ID_PRIMARY,
            -1)



        // Getting the provider notification details via API
        getProviderNotificationDetails(providerId)
    }


    /**
     * Initiating the toolbar with title
     */
    private fun initToolbar() {
        setSupportActionBar(customNotificationBinding.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        customNotificationBinding.toolbar.setTitle(getString(R.string.notification_settings))
        customNotificationBinding.toolbar.setNavigationIcon(R.drawable.ic_back)
    }

    /**
     * Initiating alerts switch views
     */
    private fun initSwitchViews() {
        val role: String? = PrefUtility.getRole(this)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            customNotificationBinding?.txtNoNotification?.setVisibility(View.GONE)
            customNotificationBinding?.notificationItems?.idNotifyTitle?.setVisibility(View.VISIBLE)
            customNotificationBinding?.notificationItems?.idNotifySubTitle?.setVisibility(View.VISIBLE)
            customNotificationBinding?.notificationItems?.customRlAcuity?.setVisibility(View.VISIBLE)
        } else {
            customNotificationBinding?.notificationItems?.customRlAcuity?.setVisibility(View.GONE)
            customNotificationBinding?.notificationItems?.idNotifyTitle?.setVisibility(View.VISIBLE)
            customNotificationBinding?.notificationItems?.idNotifySubTitle?.setVisibility(View.VISIBLE)
            customNotificationBinding?.txtNoNotification?.setVisibility(View.GONE)
        }
        // Acuity
        customNotificationBinding.notificationItems.swAcutiy.setOnCheckedChangeListener(
            SwitchCheckedChangeListener())

        // Populate dynamic alert switch status
        val checkAcuity: Boolean

        // Getting value from shared preference for Acuity alert
        checkAcuity = if (PrefUtility.getStringInPref(this,
                Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                "") == null || PrefUtility.getStringInPref(this,
                Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                "").isEmpty()
        ) {
            false
        } else {
            java.lang.Boolean.parseBoolean(PrefUtility.getStringInPref(this,
                Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                ""))
        }


        // Setting the switch status based on the saved value
        customNotificationBinding?.notificationItems?.swAcutiy.setChecked(checkAcuity)
    }

    /**
     * Add / Update provider notification Data
     *
     * @param addNotificationDataRequest
     */
    private fun addOrUpdateProviderNotification(addNotificationDataRequest: AddNotificationDataRequest) {
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(customNotificationBinding.container,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0).show()
            return
        }
        val gson = Gson()

        //Triggering the "addOrUpdateProviderNotification" API call
        viewModel.addOrUpdateProviderNotification(addNotificationDataRequest)
            .observe(this) { commonResponse ->

                if (commonResponse != null && commonResponse.status != null && commonResponse.status) {

                    dismissProgressBar()
                    finish()
                } else {
                    val errMsg: String? =
                        ErrorMessages().getErrorMessage(this,
                            commonResponse?.errorMessage,
                            Constants.API.updateProvider)

                    CustomSnackBar.make(customNotificationBinding?.container,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0)?.show()
                    dismissProgressBar()
                    finish()
                }
            }
    }

    /**
     * Getting provider notification details
     *
     * @param providerId
     */
    private fun getProviderNotificationDetails(providerId: Long) {


        if (!UtilityMethods().isInternetConnected(this)!!) {
            CustomSnackBar.make(customNotificationBinding.container,
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0)?.show()
            return
        }
        //Triggering the "getProviderNotificationDetails" API call
        viewModel?.getProviderNotificationDetails(providerId)?.observe(this) { notificationResponse ->
            if (notificationResponse != null) {
                val gson = Gson()

                // Saving the Id for addorupdateProviderNotification API
                PrefUtility.saveStringInPref(this,
                    Constants.SharedPrefConstants.ALERT_NOTIFTY_ID,
                    java.lang.String.valueOf(notificationResponse.getNotificationSettings()
                        .id)

                // Saving the current status of Acuity in shared preference
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.ALERT_ACUITY,
                    (notificationResponse.getNotificationRequests().get(0)
                        .getAcuity()))
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.ALERT_ACUITY_STATUS, (notificationResponse.getNotificationRequests().get(0)
                        .getNotificationEnabled()))
                // Saving the current mobile acuity status in shared preference
                PrefUtility().saveStringInPref(this,
                    Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                    java.lang.String.valueOf(notificationResponse.getNotificationSettings()
                        .getMobileAcuity()))

                //Initiating alerts switch views and updating the data
                initSwitchViews()
            } else {
                val errMsg: String? = ErrorMessages().getErrorMessage(this,
                    this.resources.getString(R.string.api_error),
                    Constants.API.updateProvider)
               
                CustomSnackBar.make(customNotificationBinding.container,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
            }
        }
    }

    override fun onBackPressed() {
        //Calling the "AddOrUpdateProviderNotification" API
        val role: String? = PrefUtility.getRole(this)
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            addOrUpdateProviderNotificationAPI()
        } else {
            finish()
        }
    }

    /**
     * Calling the "AddOrUpdateProviderNotification" API
     */
    private fun addOrUpdateProviderNotificationAPI() {
        showProgressBar(PBMessageHelper().getMessage(this,
            Constants.API.updateNotification.toString()))
        val checkAcuity: Boolean
        // Getting value from shared preference for Acuity alert
        checkAcuity = if (PrefUtility.getStringInPref(this,
                Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                "") == null || PrefUtility.getStringInPref(this,
                Constants.SharedPrefConstants.ALERT_ACUITY_STATUS,
                "")?.isEmpty() == true
        ) {
            false
        } else {
            java.lang.Boolean.parseBoolean(com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility
                .getStringInPref(this,
                Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                ""))
        }
        uid = com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility
            .getStringInPref(this, Constants.SharedPrefConstants.ALERT_NOTIFTY_ID, "")
        // Adding the request object to call the "AddOrUpdateProviderNotification" API
        val addNotificationDataRequest = AddNotificationDataRequest()
        addNotificationDataRequest.setId(uid)
        addNotificationDataRequest.setUserId(providerId)
        addNotificationDataRequest.setMobileAcuity(checkAcuity)
        addNotificationDataRequest.setWebAcuity(true)

        // Calling the "AddOrUpdateProviderNotification" API with request object
        addOrUpdateProviderNotification(addNotificationDataRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Action bar back button click listener
        if (item.itemId == android.R.id.home) {
            //Calling the "AddOrUpdateProviderNotification" API
            val role: String? = PrefUtility.getRole(this)
            if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
                addOrUpdateProviderNotificationAPI()
            } else {
                finish()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Switch checked listener
     */
    class SwitchCheckedChangeListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

            if (buttonView.id == R.id.swAcutiy) {

                PrefUtility.saveStringInPref(this,
                    Constants.SharedPrefConstants.ALERT_MOBILE_ACUITY_STATUS,
                    isChecked.toString())
            }
        }
    }
}