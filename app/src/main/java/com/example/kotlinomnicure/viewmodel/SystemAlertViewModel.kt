package com.mvp.omnicure.kotlinactivity.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.mvp.omnicure.apiRetrofit.ApiClient
import com.mvp.omnicure.apiRetrofit.RequestBodys.CommonProviderIdBody
import com.mvp.omnicure.utils.Constants
import omnicure.mvp.com.providerEndpoints.model.SystemAlerts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SystemAlertViewModel: ViewModel() {
    private val TAG = "SystemAlertViewModel"
    private var systemAlertObservable: MutableLiveData<SystemAlerts?>? = null

    fun getSystemAlerts(providerId: Long): LiveData<SystemAlerts?>? {
        systemAlertObservable = MutableLiveData()
        //        systemAlerts(providerId);
        systemAlertsRetro(providerId)
        return systemAlertObservable
    }

    private fun systemAlertsRetro(providerId: Long) {
        val errMsg = arrayOfNulls<String>(1)
        ApiClient.getApiProviderEndpoints(true, true)
            .getalertsreponse(CommonProviderIdBody(providerId))
            .enqueue(object : Callback<SystemAlerts?> {
                override fun onResponse(
                    call: Call<SystemAlerts?>,
                    response: Response<SystemAlerts?>
                ) {
                    Log.d(TAG, "onResponse: getalertsreponse " + response.code())
                    if (response.isSuccessful) {
                        val alertsResponse = response.body()
                        val res = Gson().toJson(alertsResponse)
                        Log.d(TAG, "onResponse: getalertsreponse $res")
                        if (systemAlertObservable == null) {
                            systemAlertObservable = MutableLiveData()
                        }
                        systemAlertObservable!!.value = alertsResponse
                    }
                }

                override fun onFailure(call: Call<SystemAlerts?>, t: Throwable) {
                    errMsg[0] = Constants.API_ERROR
                    Log.e(TAG, "onFailure: getalertsreponse $t")
                    val commonResponse = SystemAlerts()
                    commonResponse.errorMessage = errMsg[0]
                    if (systemAlertObservable == null) {
                        systemAlertObservable = MutableLiveData()
                    }
                    systemAlertObservable!!.value = commonResponse
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val commonResponse = SystemAlerts()
            commonResponse.errorMessage = errMsg[0]
            if (systemAlertObservable == null) {
                systemAlertObservable = MutableLiveData()
            }
            systemAlertObservable!!.value = commonResponse
        }
    }

    override fun onCleared() {
        super.onCleared()
        systemAlertObservable = null
    }
}