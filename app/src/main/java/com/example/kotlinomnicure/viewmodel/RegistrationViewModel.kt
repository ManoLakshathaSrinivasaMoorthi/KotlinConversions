package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationViewModel: ViewModel() {

    private var providerObservable: MutableLiveData<CommonResponse>? = null
    private var hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private var remoteProviderListObservable: MutableLiveData<RemoteProviderListResponse>? = null
    private lateinit var retService: RetrofitService

    fun registerProvider(provider: Provider): LiveData<Response<CommonResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        providerObservable = MutableLiveData<CommonResponse>()

        val response = retService.getRegisterList(provider)
        emit(response)
    }

    fun getHospitalList(): LiveData<Response<HospitalListResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        hospitalListObservable = MutableLiveData<HospitalListResponse>()

        val response = retService.getHospitals()
        emit(response)
    }

    fun getRemoteProviderList():  LiveData<Response<RemoteProviderListResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        remoteProviderListObservable = MutableLiveData<RemoteProviderListResponse>()

        val response = retService.getRemoteProviderList()
        emit(response)
    }




    override fun onCleared() {
        super.onCleared()
        providerObservable = null
        hospitalListObservable = null
    }
}

