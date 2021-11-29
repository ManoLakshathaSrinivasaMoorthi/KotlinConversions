package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.CommonProviderIdBody
import com.mvp.omnicure.kotlinactivity.requestbodys.GetProviderListRequestBody
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.providerEndpoints.model.ProviderListResponse
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyVirtualViewModel : ViewModel() {
    private val TAG = javaClass.simpleName
    private var commonResponseMutableLiveData: MutableLiveData<TeamsDetailListResponse?>? = null
    private var providerListObservable: MutableLiveData<ProviderListResponse?>? = null

    fun getTeams(providerId: Long): LiveData<TeamsDetailListResponse?>? {
        commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse?>()
        teams(providerId)
        return commonResponseMutableLiveData
    }

    fun getProviderList(
        providerId: Long,
        token: String,
        role: String
    ): LiveData<ProviderListResponse?>? {
        providerListObservable = MutableLiveData<ProviderListResponse?>()
        getProviders(providerId, token, role)
        return providerListObservable
    }


    private fun teams(uid: Long) {


        val call: Call<TeamsDetailListResponse?>? =
            ApiClient().getApiProviderEndpoints(true, true)?.virtualTeams(CommonProviderIdBody(uid))


        if (call != null) {
            call.enqueue(object : Callback<TeamsDetailListResponse?> {
                override fun onResponse(
                    call: Call<TeamsDetailListResponse?>,
                    response: Response<TeamsDetailListResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "onResponse: SUCCESS")
                        if (commonResponseMutableLiveData == null) {
                            commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse?>()
                        }
                        commonResponseMutableLiveData!!.setValue(response.body())
                    } else {
                        Log.i(TAG, "onResponse: FAILURE")
                        val commonResponse = TeamsDetailListResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (commonResponseMutableLiveData == null) {
                            commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse?>()
                        }
                        commonResponseMutableLiveData!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<TeamsDetailListResponse?>, t: Throwable) {
                    Log.i(TAG, "onResponse: FAILLOG " + t.message + t.cause + t.localizedMessage)
                    val commonResponse = TeamsDetailListResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (commonResponseMutableLiveData == null) {
                        commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse?>()
                    }
                    commonResponseMutableLiveData!!.setValue(commonResponse)
                }
            })
        }


    }

    private fun getProviders(providerId: Long, token: String, role: String) {
        val errMsg = ""

        //Backend changed the endpoint.
        val url = "providerEndpoints/v1/getProviderListBackup"
        ApiClient().getApiProviderEndpoints(true, true)
            ?.getProviderList(url, GetProviderListRequestBody(role, token, providerId))
            ?.enqueue(object : Callback<ProviderListResponse?> {
                override fun onResponse(
                    call: Call<ProviderListResponse?>,
                    response: Response<ProviderListResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d("VerifyTags", "onResponse: " + response.code())
                        val commonResponse: ProviderListResponse? = response.body()
                        if (providerListObservable == null) {
                            providerListObservable = MutableLiveData<ProviderListResponse?>()
                        }
                        providerListObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("Verifytags", "onResponse: " + response.code())
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: ProviderListResponse =
                                ProviderListResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (providerListObservable == null) {
                                providerListObservable = MutableLiveData<ProviderListResponse?>()
                            }
                            providerListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ProviderListResponse?>,
                    t: Throwable
                ) {
                    Log.e("ProviderList", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: ProviderListResponse =
                            ProviderListResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (providerListObservable == null) {
                            providerListObservable = MutableLiveData<ProviderListResponse?>()
                        }
                        providerListObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: ProviderListResponse =
                    ProviderListResponse()
                commonResponse.setErrorMessage(errMsg)
                if (providerListObservable == null) {
                    providerListObservable = MutableLiveData<ProviderListResponse?>()
                }
                providerListObservable!!.setValue(commonResponse)
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        commonResponseMutableLiveData = null
    }
}