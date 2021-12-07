package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.hospitalEndpoints.HospitalRetrofit
import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientListResponse
import omnicurekotlin.example.com.providerEndpoints.ProviderRetrofit
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.OtherRebroadcastRequest
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import retrofit2.Response

class CensusPatientListViewModel : ViewModel(){

    private var wardListObservable: MutableLiveData<WardPatientListResponse>? = null
    private var allWardListObservable: MutableLiveData<WardPatientListResponse>? = null
    private var sendRebroadcastResponseObservable: MutableLiveData<CommonResponse>? = null
    private lateinit var retService: RetrofitService

    fun getWardHospitalList(hospitalId: Long, wardName: String): LiveData<Response<WardPatientListResponse>> =  liveData  {
        retService =HospitalRetrofit.getRetrofits().create(RetrofitService::class.java)
        wardListObservable = MutableLiveData<WardPatientListResponse>()

        val response = retService.getHospitalwardList(hospitalId,wardName)
        emit(response)
        return@liveData
    }

    fun getWardList(hospitalId: Long):  LiveData<Response<WardPatientListResponse>> =  liveData  {
        retService =HospitalRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
       allWardListObservable = MutableLiveData<WardPatientListResponse>()

        val response = retService.getCensusWardlists(hospitalId)
        emit(response)
        return@liveData
    }

    fun reconsultOtherPatient(otherRebroadcastRequest: OtherRebroadcastRequest):  LiveData<Response<CommonResponse>> =  liveData  {
        retService =ProviderRetrofit.getProvider()
                .create(RetrofitService::class.java)
        sendRebroadcastResponseObservable = MutableLiveData<CommonResponse>()

        val response = retService.getothersreconsult(otherRebroadcastRequest)
        emit(response)
        return@liveData
    }






    override fun onCleared() {
        super.onCleared()
        wardListObservable = null
        allWardListObservable = null
        sendRebroadcastResponseObservable = null
    }
}

