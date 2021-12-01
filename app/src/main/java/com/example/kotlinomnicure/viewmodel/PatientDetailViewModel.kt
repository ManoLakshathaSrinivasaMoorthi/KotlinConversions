package com.example.kotlinomnicure.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.requestbodys.CommonPatientIdRequestBody

import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.patientsEndpoints.model.PatientDetail
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class PatientDetailViewModel : ViewModel() {
    private val TAG = javaClass.simpleName
    private var commonResponseMutableLiveData: MutableLiveData<PatientDetail>? = null


    fun getPatienDetails(providerId: Long?): LiveData<PatientDetail>? {
        commonResponseMutableLiveData = MutableLiveData<PatientDetail>()
     
        if (providerId != null) {
            patientDetailsRetro(providerId)
        }
        return commonResponseMutableLiveData
    }

    private fun patientDetailsRetro(providerId: Long) {
        val errMsg = arrayOfNulls<String>(1)

        //sending body through data class
        val requestBody = CommonPatientIdRequestBody(providerId)

        ApiClient().getApiPatientEndpoints(true, true)?.patientdetailsresponse(requestBody)
            ?.enqueue(object : Callback<PatientDetail?> {
                override fun onResponse(
                    call: Call<PatientDetail?>,
                    response: Response<PatientDetail?>
                ) {
                    Log.d(TAG, "onResponse: " + response.code())
                    if (response.isSuccessful) {
                        val patientDetail = response.body()
                        if (commonResponseMutableLiveData == null) {
                            commonResponseMutableLiveData = MutableLiveData()
                        }
                        commonResponseMutableLiveData!!.setValue(patientDetail)
                    }
                }

                override fun onFailure(call: Call<PatientDetail?>, t: Throwable) {
                    Log.e(TAG, "onFailure: $t")
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is Exception) errMsg[0] = Constants.API_ERROR
                    val response = PatientDetail()
                    response.setErrorMsg(errMsg[0])
                    if (commonResponseMutableLiveData == null) {
                        commonResponseMutableLiveData = MutableLiveData()
                    }
                    commonResponseMutableLiveData!!.setValue(response)
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = PatientDetail()
            if (commonResponseMutableLiveData == null) {
                commonResponseMutableLiveData = MutableLiveData()
            }
            commonResponseMutableLiveData!!.setValue(response)
        }
    }
}