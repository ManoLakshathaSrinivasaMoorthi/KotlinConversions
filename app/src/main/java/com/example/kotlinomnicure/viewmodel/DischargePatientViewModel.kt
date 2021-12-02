package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.DischargePatientRequest
import java.lang.Exception
import java.net.SocketTimeoutException

class DischargePatientViewModel:ViewModel() {
    private val TAG = javaClass.simpleName
    private var dischargePatientResponseObservable: MutableLiveData<CommonResponse>? = null


    fun bspDischargePatient(dischargePatientRequest: DischargePatientRequest): MutableLiveData<CommonResponse>? {
        dischargePatientResponseObservable = MutableLiveData<CommonResponse>()
        //doDischargePatient(dischargePatientRequest)
        return dischargePatientResponseObservable
    }





    override fun onCleared() {
        super.onCleared()
        dischargePatientResponseObservable = null
    }

}