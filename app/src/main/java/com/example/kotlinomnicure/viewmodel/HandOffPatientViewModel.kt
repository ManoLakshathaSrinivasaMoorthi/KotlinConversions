package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.CommonProviderIdBody
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.HandOffListResponse
import omnicurekotlin.example.com.providerEndpoints.model.PatientHandOffRequest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HandOffPatientViewModel:ViewModel() {
    private val TAG = javaClass.simpleName
    private var commonResponseMutableLiveData: MutableLiveData<HandOffListResponse?>? = null
    private var sendHandOffResponseObservable: MutableLiveData<CommonResponse?>? = null


    fun getHandOffPatientsLists(providerId: Long): LiveData<HandOffListResponse?>? {
        commonResponseMutableLiveData = MutableLiveData()

        getHandOffListRetro(providerId)
        return commonResponseMutableLiveData
    }

    fun bedSideProviderHandOffPatient(patientHandOffRequest: PatientHandOffRequest): MutableLiveData<CommonResponse?>? {
        sendHandOffResponseObservable = MutableLiveData()
        sendBedSideHandOffPatient(patientHandOffRequest)
        return sendHandOffResponseObservable
    }

    private fun getHandOffListRetro(providerId: Long) {
        ApiClient().getApiProviderEndpoints(true, true)
            ?.bSPHandOffList(CommonProviderIdBody(providerId))
            ?.enqueue(object : Callback<HandOffListResponse?> {
                override fun onResponse(
                    call: Call<HandOffListResponse?>,
                    response: Response<HandOffListResponse?>
                ) {
                    Log.d(TAG, "onResponse: bSPHandOffList" + response.code())
                    if (response.isSuccessful) {
                        val handOffListResponse = response.body()
                        if (commonResponseMutableLiveData == null) {
                            commonResponseMutableLiveData = MutableLiveData()
                        }
                        commonResponseMutableLiveData!!.value = handOffListResponse
                    }
                }

                override fun onFailure(call: Call<HandOffListResponse?>, t: Throwable) {
                    Log.e(TAG, "onFailure: bSPHandOffList$t")
                    Handler(Looper.getMainLooper()).post {
                        val response = HandOffListResponse()
                        response.errorMessage = Constants.API_ERROR
                        if (commonResponseMutableLiveData == null) {
                            commonResponseMutableLiveData = MutableLiveData()
                        }
                        commonResponseMutableLiveData!!.setValue(response)
                    }
                }
            })
    }


    private fun sendBedSideHandOffPatient(patientHandOffRequest: PatientHandOffRequest) {
        val call = ApiClient().getApiProviderEndpoints(true, true)
            ?.sendBedSideHandOffPatient(patientHandOffRequest)
        if (call != null) {
            call.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful) {
                        if (sendHandOffResponseObservable == null) {
                            sendHandOffResponseObservable = MutableLiveData()
                        }
                        sendHandOffResponseObservable!!.setValue(response.body())
                    } else {
                        val commonResponse = CommonResponse()
                        commonResponse.errorMessage = Constants.API_ERROR
                        if (sendHandOffResponseObservable == null) {
                            sendHandOffResponseObservable = MutableLiveData()
                        }
                        sendHandOffResponseObservable!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                    val commonResponse = CommonResponse()
                    commonResponse.errorMessage = Constants.API_ERROR
                    if (sendHandOffResponseObservable == null) {
                        sendHandOffResponseObservable = MutableLiveData()
                    }
                    sendHandOffResponseObservable!!.value = commonResponse
                }
            })
        }


    }


    protected override fun onCleared() {
        super.onCleared()
        commonResponseMutableLiveData = null
    }
}