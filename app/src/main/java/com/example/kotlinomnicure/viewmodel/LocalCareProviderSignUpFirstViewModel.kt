package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.*
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeListResponse
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class LocalCareProviderSignUpFirstViewModel:ViewModel() {
    private val hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private var countryListObservable: MutableLiveData<CountryCodeListResponse>? = null
    private lateinit var retService: RetrofitService

    fun getCountry():  LiveData<Response<CountryCodeListResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit().create(RetrofitService::class.java)
        countryListObservable = MutableLiveData<CountryCodeListResponse>()

        val response = retService.getAlldata()
        emit(response)
    }

    }



