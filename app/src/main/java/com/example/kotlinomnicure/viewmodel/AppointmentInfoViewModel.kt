package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class AppointmentInfoViewModel: ViewModel() {
    private var appointmentObservable: MutableLiveData<CommonResponse?>? = null
    private var patientObservable: MutableLiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>? =
        null
    private val TAG = "Appointment"

    fun addAppointment(token: String, appointment: Appointment): LiveData<CommonResponse?>? {
        if (appointmentObservable == null) {
            appointmentObservable = MutableLiveData<CommonResponse?>()
        }
        //        addPatientAppointment(token, appointment);
        addPatientAppointmentRetro(token, appointment)
        return appointmentObservable
    }

    fun signUpPatient(
        providerId: Long,
        token: String,
        patient: Appointment
    ): LiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>? {
        patientObservable =
            MutableLiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>()
        //        patientSignUp(providerId, token, patient);
        patientSignUpRetro(providerId, token, patient)
        return patientObservable
    }

    private fun patientSignUpRetro(providerId: Long, token: String, patient: Appointment) {
        val errMsg = arrayOfNulls<String>(1)

//        ApiClient.getApiPatientEndpoints(false,false).registerPatient(providerId,token,patient).enqueue(new Callback<omnicure.mvp.com.patientEndpoints.model.CommonResponse>() {
        ApiClient.getApiPatientEndpoints(true, true).registerPatient(patient)
            .enqueue(object : Callback<omnicure.mvp.com.patientEndpoints.model.CommonResponse?> {
                override fun onResponse(
                    call: Call<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>,
                    response: Response<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>
                ) {
                    Log.d(TAG, "onResponse: registerPatient " + response.code())
                    if (response.isSuccessful()) {
                        val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse? =
                            response.body()
                        if (patientObservable == null) {
                            patientObservable =
                                MutableLiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>()
                        }
                        patientObservable!!.setValue(commonResponse)
                    } else {
                        if (response.code() == 705) {
                            errMsg[0] = "redirect"
                        } else if (response.code() == 403) {
                            errMsg[0] = "unauthorized"
                        } else {
                            errMsg[0] = Constants.API_ERROR
                        }
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                                CommonResponse()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (patientObservable == null) {
                                patientObservable =
                                    MutableLiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>()
                            }
                            patientObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>,
                    t: Throwable
                ) {
                    Log.e(TAG, "onFailure: registerPatient$t")
                    //                if (t instanceof SocketTimeoutException)
//                    errMsg[0] = Constants.APIErrorType.SocketTimeoutException.toString();
//                if (t instanceof IOException)
//                    errMsg[0] = Constants.API_ERROR;
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (patientObservable == null) {
                            patientObservable =
                                MutableLiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>()
                        }
                        patientObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                CommonResponse()
            commonResponse.setErrorMessage(errMsg[0])
            if (patientObservable == null) {
                patientObservable =
                    MutableLiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>()
            }
            patientObservable!!.setValue(commonResponse)
        }
    }

    private fun patientSignUp(providerId: Long, token: String, patient: Appointment) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                        EndPointBuilder.getPatientEndpoints()
                            .patientSignUp(providerId, token, patient)
                            .execute()
                    Handler(Looper.getMainLooper()).post {
                        if (patientObservable == null) {
                            patientObservable =
                                MutableLiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>()
                        }
                        patientObservable!!.setValue(commonResponse)
                    }
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(errMsg)
                        if (patientObservable == null) {
                            patientObservable =
                                MutableLiveData<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>()
                        }
                        patientObservable!!.setValue(commonResponse)
                    }
                }
            }
        }).start()
    }

    private fun addPatientAppointmentRetro(token: String, appointment: Appointment) {
        val errMsg = arrayOfNulls<String>(1)
        ApiClient.getApi(true, true).addAppointment(token, appointment)
            .enqueue(object : Callback<CommonResponse?> {
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
                    Log.e(TAG, "onFailure: addAppointment $t")
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

    private fun addPatientAppointment(token: String, appointment: Appointment) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val commonResponse: CommonResponse = EndPointBuilder.getAppointmentEndpoints()
                        .addAppointment(token, appointment)
                        .execute()
                    Handler(Looper.getMainLooper()).post {
                        if (appointmentObservable == null) {
                            appointmentObservable = MutableLiveData<CommonResponse?>()
                        }
                        appointmentObservable!!.setValue(commonResponse)
                    }
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponse()
                        commonResponse.setErrorMessage(errMsg)
                        if (appointmentObservable == null) {
                            appointmentObservable = MutableLiveData<CommonResponse?>()
                        }
                        appointmentObservable!!.setValue(commonResponse)
                    }
                }
            }
        }).start()
    }

    override fun onCleared() {
        super.onCleared()
        appointmentObservable = null
    }
}