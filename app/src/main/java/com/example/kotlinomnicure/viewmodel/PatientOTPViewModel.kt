package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.PatientOtpModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientOTPViewModel: ViewModel() {
    private var otpVerifyObservable: MutableLiveData<CommonResponse?>? = null

    fun submitOTP(otpModel: PatientOtpModel): LiveData<CommonResponse?>? {
        otpVerifyObservable = MutableLiveData<CommonResponse?>()
        verifyOTP(otpModel)
        return otpVerifyObservable
    }


    private fun verifyOTP(otpModel: PatientOtpModel) {


        val call: Call<CommonResponse?>? =
            ApiClient().getApiPatientEndpoints(true, true)?.verifyOtp(otpModel)
        call?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>
            ) {
                if (response.isSuccessful()) {
                    if (otpVerifyObservable == null) {
                        otpVerifyObservable = MutableLiveData<CommonResponse?>()
                    }
                    otpVerifyObservable!!.setValue(response.body())
                } else {
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (otpVerifyObservable == null) {
                        otpVerifyObservable = MutableLiveData<CommonResponse?>()
                    }
                    otpVerifyObservable!!.setValue(commonResponse)
                }
            }

            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                val commonResponse = CommonResponse()
                commonResponse.setErrorMessage(Constants.API_ERROR)
                if (otpVerifyObservable == null) {
                    otpVerifyObservable = MutableLiveData<CommonResponse?>()
                }
                otpVerifyObservable!!.setValue(commonResponse)
            }
        })

    }


    override fun onCleared() {
        super.onCleared()
        otpVerifyObservable = null
    }
}