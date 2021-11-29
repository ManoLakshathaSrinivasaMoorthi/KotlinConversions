package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.hospitalEndpoints.HospitalRetrofit
import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientListResponse
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Response

class CensusWardListViewModel:ViewModel() {

    private var wardListObservable: MutableLiveData<WardPatientListResponse>? = null
    private var hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private lateinit var retService: RetrofitService

    fun getWardList(hospitalId: Long): LiveData<Response<WardPatientListResponse>> =  liveData  {
        retService = HospitalRetrofit.getRetrofits().create(RetrofitService::class.java)
        wardListObservable = MutableLiveData<WardPatientListResponse>()

        val response = retService.getCensusWardlists(hospitalId)
        emit(response)
        return@liveData
    }

    fun getHospitalList(id: Long): LiveData<Response<HospitalListResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        hospitalListObservable = MutableLiveData<HospitalListResponse>()

        val response = retService.getHospitalLists(id)
        emit(response)
        return@liveData
    }



    override fun onCleared() {
        super.onCleared()
        wardListObservable = null
        hospitalListObservable = null
    }
}
