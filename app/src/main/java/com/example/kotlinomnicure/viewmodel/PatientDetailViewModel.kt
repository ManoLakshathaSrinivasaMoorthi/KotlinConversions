package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.patientsEndpoints.PatientRetrofit
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.DischargePatientRequest
import omnicurekotlin.example.com.providerEndpoints.ProviderRetrofit
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientDetailViewModel: ViewModel() {
    private val TAG = javaClass.simpleName
    private var commonResponseMutableLiveData: MutableLiveData<CommonResponse>? = null
    private var memberListObservable: MutableLiveData<TeamsDetailListResponse>? = null
    private var startSOSObservable: MutableLiveData<CommonResponse>? = null
    private var patientDischargeObservable: MutableLiveData<CommonResponse>? = null
    private var resetAcuityObservable: MutableLiveData<CommonResponse>? = null
    private var dischargePatientResponseObservable: MutableLiveData<CommonResponse>? = null
    private lateinit var retService: RetrofitService

    fun getPatienDetails(providerId: Long): LiveData<Response<CommonResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        commonResponseMutableLiveData = MutableLiveData<CommonResponse>()

        val response = retService.getPatientApi(providerId)
        emit(response)
    }
    fun getMemberList(patientId: Long, team: String):LiveData<Response<TeamsDetailListResponse>> =  liveData  {
        retService =ProviderRetrofit.getProvider()
                .create(RetrofitService::class.java)
        memberListObservable = MutableLiveData<TeamsDetailListResponse>()

        val response = retService.getMembersApi(patientId,team)
        emit(response)
    }

    fun startSOS(callerId: Long, token: String, patientId: Long): LiveData<CommonResponse>? {
        startSOSObservable = MutableLiveData<CommonResponse>()
        startSOSAPI(callerId, token, patientId)
        return startSOSObservable
    }

    fun dischargePatient(providerId: Long, token: String, patientId: Long, notes: String): LiveData<CommonResponse>? {
        patientDischargeObservable = MutableLiveData<CommonResponse>()
        discharge(providerId, token, patientId, notes)
        return patientDischargeObservable
    }

    fun resetAcuityValue(providerId: Long, token: String, patientId: Long, score: String): LiveData<CommonResponse>? {
        resetAcuityObservable = MutableLiveData<CommonResponse>()
        resetAcuity(providerId, token, patientId, score)
        return resetAcuityObservable
    }

    fun bspDischargePatient(dischargePatientRequest: DischargePatientRequest):LiveData<Response<CommonResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        dischargePatientResponseObservable = MutableLiveData<CommonResponse>()

        val response = retService.getDischargeApiList(dischargePatientRequest)
        emit(response)
    }



    private fun startSOSAPI(callerId: Long, token: String, patientId: Long) {

    }




    private fun resetAcuity(id: Long, token: String, patientId: Long, score: String) {

    }

    private fun discharge(providerId: Long, token: String, patientId: Long, notes: String) {

    }

    override fun onCleared() {
        super.onCleared()
        commonResponseMutableLiveData = null
        startSOSObservable = null
        patientDischargeObservable = null
        resetAcuityObservable = null
        dischargePatientResponseObservable = null
    }
}
