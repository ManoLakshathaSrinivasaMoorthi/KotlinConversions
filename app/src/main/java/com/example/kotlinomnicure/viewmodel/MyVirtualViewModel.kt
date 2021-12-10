package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.CommonProviderIdBody
import omnicurekotlin.example.com.providerEndpoints.model.ProviderListResponse
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyVirtualViewModel:ViewModel() {
    private val TAG = javaClass.simpleName
    private var commonResponseMutableLiveData: MutableLiveData<TeamsDetailListResponse>? = null
    private var providerListObservable: MutableLiveData<ProviderListResponse>? = null
    private lateinit var retService: RetrofitService

    fun getTeams(providerId: Long?): LiveData<TeamsDetailListResponse?>? {
        commonResponseMutableLiveData = MutableLiveData()
        teams(providerId)
        return commonResponseMutableLiveData
    }

    private fun teams(providerId: Long?) {

        val call: Call<TeamsDetailListResponse?>? =
            ApiClient().getApiProviderEndpoints(encrypt = true, decrypt = true)?.virtualTeams(providerId?.let {
                CommonProviderIdBody(it)
            })


        call?.enqueue(object : Callback<TeamsDetailListResponse?> {
            override fun onResponse(
                call: Call<TeamsDetailListResponse?>,
                response: Response<TeamsDetailListResponse?>,
            ) {
                if (response.isSuccessful) {
//                    Log.i(TAG, "onResponse: SUCCESS");
                    if (commonResponseMutableLiveData == null) {
                        commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse>()
                    }
                    commonResponseMutableLiveData!!.setValue(response.body())
                } else {
//                    Log.i(TAG, "onResponse: FAILURE");
                    val commonResponse = TeamsDetailListResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (commonResponseMutableLiveData == null) {
                        commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse>()
                    }
                    commonResponseMutableLiveData!!.setValue(commonResponse)
                }
            }

            override fun onFailure(call: Call<TeamsDetailListResponse?>, t: Throwable) {

//                Log.i(TAG, "onResponse: FAILLOG " + t.getMessage() + t.getCause() + t.getLocalizedMessage());
                val commonResponse = TeamsDetailListResponse()
                commonResponse.setErrorMessage(Constants.API_ERROR)
                if (commonResponseMutableLiveData == null) {
                    commonResponseMutableLiveData = MutableLiveData<TeamsDetailListResponse>()
                }
                commonResponseMutableLiveData!!.value = commonResponse
            }
        })
    }

    fun getProviderList(providerId: Long, token: String, role: String): LiveData<ProviderListResponse>? {
        providerListObservable = MutableLiveData<ProviderListResponse>()
        getProviders(providerId, token, role)
        return providerListObservable
    }

   

    private fun getProviders(providerId: Long, token: String, role: String) {

    }


    override fun onCleared() {
        super.onCleared()
        commonResponseMutableLiveData = null
    }
}
