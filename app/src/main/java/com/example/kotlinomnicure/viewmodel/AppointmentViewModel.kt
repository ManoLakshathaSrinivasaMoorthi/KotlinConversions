package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class AppointmentViewModel :ViewModel() {
    private var appointmentObservable: MutableLiveData<CommonResponse?>? = null
    private val TAG = "AppointmentViewModel"

    fun addAppointment(token: String, appointment: Appointment): LiveData<CommonResponse?>? {
        if (appointmentObservable == null) {
            appointmentObservable = MutableLiveData<CommonResponse?>()
        }
        addPatientAppointmentRetro(token, appointment)
        return appointmentObservable
    }

    private fun addPatientAppointmentRetro(token: String, appointment: Appointment) {
        val errMsg = arrayOfNulls<String>(1)
        ApiClient().getApi(true, true)?.addAppointment(token, appointment)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    Log.d(TAG, "onResponse: addAppointment " + response.code())
                    if (response.isSuccessful()) {
                        if (appointmentObservable == null) {
                            appointmentObservable = MutableLiveData<CommonResponse?>()
                        }
                        appointmentObservable!!.setValue(response.body())
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                    Log.e(TAG, "onFailure: $t")
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is Exception) errMsg[0] = Constants.API_ERROR
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val commonResponse = CommonResponse()
            commonResponse.setErrorMessage(errMsg[0])
            if (appointmentObservable == null) {
                appointmentObservable = MutableLiveData<CommonResponse?>()
            }
            appointmentObservable!!.setValue(commonResponse)
        }
    }
}