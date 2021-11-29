package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.providerEndpoints.ProviderRetrofit
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.ProviderListResponse
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyVirtualTeamsViewModel:ViewModel() {
    private val TAG = javaClass.simpleName
    private var commonResponseMutableLiveData: MutableLiveData<CommonResponse>? = null
    private var providerListObservable: MutableLiveData<ProviderListResponse>? = null
    private lateinit var retService: RetrofitService

    fun getTeams(providerId: Long): LiveData<Response<CommonResponse>> =  liveData  {
        retService = ProviderRetrofit.getProvider()
                .create(RetrofitService::class.java)
        commonResponseMutableLiveData = MutableLiveData<CommonResponse>()

        val response = retService.getTeamList(providerId)
        emit(response)
        return@liveData
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
