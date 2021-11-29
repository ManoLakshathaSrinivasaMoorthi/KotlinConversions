package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.providerEndpoints.ProviderRetrofit
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.PatientHandOffRequest
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import retrofit2.Response

class HandOffPatientViewModel: ViewModel() {
   // private val TAG = javaClass.simpleName
    private var commonResponseMutableLiveData: MutableLiveData<CommonResponse>? = null
    private var sendHandOffResponseObservable: MutableLiveData<CommonResponse>? = null
    private lateinit var retService: RetrofitService


    fun getHandOffPatientsLists(providerId: Long):  LiveData<Response<CommonResponse>> =  liveData  {
        retService = ProviderRetrofit.getProvider()
                .create(RetrofitService::class.java)
        commonResponseMutableLiveData = MutableLiveData<CommonResponse>()

        val response = retService.getHandOffListApi(providerId)
        emit(response)
        return@liveData
    }

    fun bedSideProviderHandOffPatient(patientHandOffRequest: PatientHandOffRequest): LiveData<Response<CommonResponse>> =  liveData  {
        retService = ProviderRetrofit.getProvider()
                .create(RetrofitService::class.java)
     sendHandOffResponseObservable = MutableLiveData<CommonResponse>()

        val response = retService.getsendBPHandoff(patientHandOffRequest)
        emit(response)
        return@liveData
    }





    override fun onCleared() {
        super.onCleared()
        commonResponseMutableLiveData = null
    }

}
