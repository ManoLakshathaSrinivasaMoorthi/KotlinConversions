package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.appointmentEndpoints.AppointmentRetrofit
import omnicurekotlin.example.com.appointmentEndpoints.model.AppointmentListResponse
import omnicurekotlin.example.com.hospitalEndpoints.HospitalRetrofit
import omnicurekotlin.example.com.hospitalEndpoints.model.AddNewPatientWardResponse
import omnicurekotlin.example.com.patientsEndpoints.PatientRetrofit
import omnicurekotlin.example.com.patientsEndpoints.model.AthenaDeviceListResponse
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.DocBoxPatientListResponse
import omnicurekotlin.example.com.patientsEndpoints.model.Patient
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import retrofit2.Response

class AddPatientViewModel: ViewModel() {
    private var patientObservable: MutableLiveData<CommonResponse>? = null
    private var docBoxPatientListObservable: MutableLiveData<DocBoxPatientListResponse>? = null
    private var athenaDeviceListObservable: MutableLiveData<AthenaDeviceListResponse>? = null
    private var appointmentsObservable: MutableLiveData<AppointmentListResponse>? = null
    private var wardResponseObservable: MutableLiveData<AddNewPatientWardResponse>? = null
    private lateinit var retService: RetrofitService

    fun addNewPatient(providerId: Long, token: String, patient: Patient): LiveData<Response<CommonResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
       patientObservable = MutableLiveData<CommonResponse>()

        val response = retService.getPatientsList(providerId,token,patient)
        emit(response)
        return@liveData
    }

    fun getDocBoxPatientList(providerId: Long?, token: String?): LiveData<Response<DocBoxPatientListResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
       docBoxPatientListObservable = MutableLiveData<DocBoxPatientListResponse>()

        val response = providerId?.let { token?.let { it1 -> retService.getDocBoxPatientList(it, it1) } }
        response?.let { emit(it) }
        return@liveData
    }

    fun getAthenaDevicetList(providerId: Long?, token: String?):  LiveData<Response<AthenaDeviceListResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        athenaDeviceListObservable = MutableLiveData<AthenaDeviceListResponse>()

        val response = retService.getAthenaDevicelistApi(providerId,token)
        emit(response)
        return@liveData
    }

    fun getAppointmentList(providerId: Long, token: String, offset: Int, limit: Int):  LiveData<Response<AppointmentListResponse>> =  liveData  {
        retService = AppointmentRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        appointmentsObservable = MutableLiveData<AppointmentListResponse>()

        val response = retService.getAppointmentApi(providerId,token,offset,limit)
        emit(response)
        return@liveData
    }


    fun getWardsList(hospitalId: Long): LiveData<Response<AddNewPatientWardResponse>> =  liveData  {
        retService = HospitalRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
       wardResponseObservable= MutableLiveData<AddNewPatientWardResponse>()

        val response = retService.getHospitalWardslist(hospitalId)
        emit(response)
        return@liveData
    }









}


