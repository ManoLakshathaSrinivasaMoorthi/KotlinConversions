package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.GetProviderListRequestBody
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.GroupCall
import omnicurekotlin.example.com.providerEndpoints.model.ProviderListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupCallViewModel: ViewModel() {
    private var providerListObservable: MutableLiveData<ProviderListResponse?>? = null
    private var providerObservable: MutableLiveData<CommonResponse?>? = null

    fun getProviderList(
        providerId: Long,
        token: String,
        role: String,
    ): LiveData<ProviderListResponse?>? {
        providerListObservable = MutableLiveData<ProviderListResponse?>()
        getProviders(providerId, token, role)
        return providerListObservable
    }

    fun multipleCall(content: GroupCall): LiveData<CommonResponse?>? {
        providerObservable = MutableLiveData<CommonResponse?>()
        MultiplecallRetro(content)
        return providerObservable
    }

    private fun MultiplecallRetro(content: GroupCall) {
        val call: Call<CommonResponse?>? =
            ApiClient().getApiProviderEndpoints(encrypt = true, decrypt = true)?.Multiplecall(content)
        call?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>,
            ) {
                if (response.isSuccessful) {

                    if (providerObservable == null) {
                        providerObservable = MutableLiveData<CommonResponse?>()
                    }
                    providerObservable!!.setValue(response.body())
                } else {

                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (providerObservable == null) {
                        providerObservable = MutableLiveData<CommonResponse?>()
                    }
                    providerObservable!!.setValue(commonResponse)
                }
            }

            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {

                val commonResponse = CommonResponse()
                commonResponse.setErrorMessage(Constants.API_ERROR)
                if (providerObservable == null) {
                    providerObservable = MutableLiveData<CommonResponse?>()
                }
                providerObservable!!.setValue(commonResponse)
            }
        })

    }


    private fun getProviders(providerId: Long, token: String, role: String) {
        val errMsg = ""

        //Backend changed the endpoint.
        val url = "providerEndpoints/v1/getProviderListBackup/"
        ApiClient().getApiProviderEndpoints(encrypt = true, decrypt = true)
            ?.getProviderList(url, GetProviderListRequestBody(role, token, providerId))
            ?.enqueue(object : Callback<ProviderListResponse?> {
                override fun onResponse(
                    call: Call<ProviderListResponse?>,
                    response: Response<ProviderListResponse?>, ) {
                    if (response.isSuccessful) {

                        val commonResponse: ProviderListResponse? = response.body()
                        if (providerListObservable == null) {
                            providerListObservable = MutableLiveData<ProviderListResponse?>()
                        }
                        providerListObservable!!.setValue(commonResponse)
                    } else {

                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = ProviderListResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (providerListObservable == null) {
                                providerListObservable = MutableLiveData<ProviderListResponse?>()
                            }
                            providerListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ProviderListResponse?>, t: Throwable, ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse =
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
                val commonResponse =
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
        providerListObservable = null
        providerObservable = null
    }
}