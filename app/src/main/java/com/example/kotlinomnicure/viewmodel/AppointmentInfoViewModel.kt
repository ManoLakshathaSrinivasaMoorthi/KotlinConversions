package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class AppointmentInfoViewModel: ViewModel() {
    private var appointmentObservable: MutableLiveData<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>? = null
    private var patientObservable: MutableLiveData<CommonResponse?>? =
        null
    private val TAG = "Appointment"

    fun addAppointment(token: String, appointment: Appointment): LiveData<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>? {
        if (appointmentObservable == null) {
            appointmentObservable = MutableLiveData<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>()
        }

        addPatientAppointmentRetro(token, appointment)
        return appointmentObservable
    }

    fun signUpPatient(
        providerId: Long,
        token: String,
        patient: Appointment): LiveData<CommonResponse?>? {
        patientObservable =
            MutableLiveData<CommonResponse?>()

        patientSignUpRetro(providerId, token, patient)
        return patientObservable
    }

    private fun patientSignUpRetro(providerId: Long, token: String, patient: Appointment) {
        val errMsg = arrayOfNulls<String>(1)

        ApiClient().getApiPatientEndpoints(encrypt = true, decrypt = true)?.registerPatient(patient)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    Log.d(TAG, "onResponse: registerPatient " + response.code())
                    if (response.isSuccessful) {
                        val commonResponse: CommonResponse? =
                            response.body()
                        if (patientObservable == null) {
                            patientObservable =
                                MutableLiveData<CommonResponse?>()
                        }
                        patientObservable!!.setValue(commonResponse)
                    } else {
                        when {
                            response.code() == 705 -> {
                                errMsg[0] = "redirect"
                            }
                            response.code() == 403 -> {
                                errMsg[0] = "unauthorized"
                            }
                            else -> {
                                errMsg[0] = Constants.API_ERROR
                            }
                        }
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse=
                                CommonResponse()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (patientObservable == null) {
                                patientObservable =
                                    MutableLiveData<CommonResponse?>()
                            }
                            patientObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable
                ) {
                    Log.e(TAG, "onFailure: registerPatient$t")

                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse= CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (patientObservable == null) {
                            patientObservable =
                                MutableLiveData<CommonResponse?>()
                        }
                        patientObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val commonResponse= CommonResponse()
            commonResponse.setErrorMessage(errMsg[0])
            if (patientObservable == null) {
                patientObservable =
                    MutableLiveData<CommonResponse?>()
            }
            patientObservable!!.value = commonResponse
        }
    }



    private fun addPatientAppointmentRetro(token: String, appointment: Appointment) {
        val errMsg = arrayOfNulls<String>(1)
        ApiClient().getApi(true, decrypt = true)?.addAppointment(token, appointment)
            ?.enqueue(object : Callback<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?> {
                override fun onResponse(
                    call: Call<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>,
                    response: Response<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>
                ) {
                    Log.d(TAG, "onResponse: addAppointment " + response.code())
                    if (response.isSuccessful) {
                        if (appointmentObservable == null) {
                            appointmentObservable = MutableLiveData<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>()
                        }
                        appointmentObservable!!.value = response.body()
                    }
                }

                override fun onFailure(call: Call<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>, t: Throwable) {
                    Log.e(TAG, "onFailure: addAppointment $t")
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is Exception) errMsg[0] = Constants.API_ERROR
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val commonResponse = omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse()
            commonResponse.setErrorMessage(errMsg[0])
            if (appointmentObservable == null) {
                appointmentObservable = MutableLiveData<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>()
            }
            appointmentObservable!!.value = commonResponse
        }
    }



    override fun onCleared() {
        super.onCleared()
        appointmentObservable = null
    }
}