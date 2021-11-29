package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.hospitalEndpoints.HospitalRetrofit
import omnicurekotlin.example.com.hospitalEndpoints.model.AddNewPatientWardResponse
import omnicurekotlin.example.com.patientsEndpoints.PatientRetrofit
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.PatientTransferRequest
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransferPatientViewModel : ViewModel() {

    private var wardResponseObservable: MutableLiveData<AddNewPatientWardResponse>? = null
    private var hospitalListResponseObservable: MutableLiveData<CommonResponse>? = null
    private var providerListResponseObservable: MutableLiveData<CommonResponse>? = null
    private var sendTransferResponseObservable: MutableLiveData<CommonResponse>? = null
    private val TAG = javaClass.simpleName
    private lateinit var retService: RetrofitService

    fun getWardsList(hospitalId: Long):LiveData<Response<AddNewPatientWardResponse>> =  liveData  {
        retService = HospitalRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        wardResponseObservable = MutableLiveData<AddNewPatientWardResponse>()

        val response = retService.getWardList(hospitalId)
        emit(response)
    }

    fun getHospitalList(token: String, patientId: String): LiveData<Response<CommonResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        hospitalListResponseObservable = MutableLiveData<CommonResponse>()

        val response = retService.getTransferHospApi(token,patientId)
        emit(response)
    }

    fun getProviderListResponse(hospitalId: Long, providerId: Long):LiveData<Response<CommonResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        providerListResponseObservable = MutableLiveData<CommonResponse>()

        val response = retService.getTransferProviderList(hospitalId,providerId)
        emit(response)
    }







    override fun onCleared() {
        super.onCleared()
        hospitalListResponseObservable = null
        providerListResponseObservable = null
    }

    fun transferPatientWithInHospital(token: String, patientTransferRequest: PatientTransferRequest): LiveData<Response<CommonResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        providerListResponseObservable = MutableLiveData<CommonResponse>()

        val response = retService.getTransferList(token,patientTransferRequest)
        emit(response)
    }


    fun transferPatientToAnotherHospital(token: String, patientTransferRequest: PatientTransferRequest):LiveData<Response<CommonResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        sendTransferResponseObservable = MutableLiveData<CommonResponse>()

        val response = retService.getAnotherHospiPatients(token,patientTransferRequest)
        emit(response)
    }

}
