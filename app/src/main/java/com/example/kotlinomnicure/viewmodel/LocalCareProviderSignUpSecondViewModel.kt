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
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeListResponse
import omnicurekotlin.example.com.userEndpoints.model.Provider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("NAME_SHADOWING")
class LocalCareProviderSignUpSecondViewModel:ViewModel() {
    private var providerObservable: MutableLiveData<CommonResponse>? = null
    private lateinit var retService: RetrofitService

    fun registerProvider(provider: Provider):LiveData<Response<CommonResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        providerObservable = MutableLiveData<CommonResponse>()

        val response = retService.getProvider(provider)
        emit(response)
    }


    override fun onCleared() {
        super.onCleared()
        providerObservable = null
    }
    }





