package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import omnicurekotlin.example.com.userEndpoints.model.Provider
import omnicurekotlin.example.com.userEndpoints.model.RemoteProviderListResponse
import retrofit2.Response

class RemoteProviderSignUpViewModel:ViewModel() {

    private var providerObservable: MutableLiveData<CommonResponse>? = null
    private var hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private var remoteProviderListObservable: MutableLiveData<RemoteProviderListResponse>? = null
    private lateinit var retService: RetrofitService

    fun registerProvider(provider: Provider):LiveData<Response<CommonResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
      providerObservable = MutableLiveData<CommonResponse>()

        val response = retService.getRegisterList(provider)
        emit(response)
    }

    fun getRemoteProviderList():LiveData<Response<RemoteProviderListResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        remoteProviderListObservable = MutableLiveData<RemoteProviderListResponse>()

        val response = retService.getRemoteProviderList()
        emit(response)
    }







    override fun onCleared() {
        super.onCleared()
        providerObservable = null
        remoteProviderListObservable = null
    }
}


