package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.HospitalIdRequestBody
import omnicurekotlin.example.com.hospitalEndpoints.model.HospitalListResponse

import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientListResponse

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.HashMap

class CensusWardListViewModel : ViewModel() {
    private var wardListObservable: MutableLiveData<WardPatientListResponse?>? = null
    private var hospitalListObservable: MutableLiveData<HospitalListResponse?>? = null
    private val TAG = "CensusWardListViewModel"

    fun getWardList(hospitalId: Long): LiveData<WardPatientListResponse?>? {
        wardListObservable = MutableLiveData<WardPatientListResponse?>()
        getCensusWardListRetro(hospitalId)
        return wardListObservable
    }

    fun getHospitalList(id: Long): LiveData<HospitalListResponse?>? {
        hospitalListObservable = MutableLiveData<HospitalListResponse?>()
        getHospitalsRetro(id)
        return hospitalListObservable
    }

    private fun getCensusWardListRetro(hospitalId: Long) {
        val errMsg = arrayOfNulls<String>(1)

        //sending body through data class
        val requestBody = HospitalIdRequestBody(hospitalId)
        ApiClient().getApiHospital(true, true)?.getWards(requestBody)
            ?.enqueue(object : Callback<WardPatientListResponse?> {
                override fun onResponse(
                    call: Call<WardPatientListResponse?>,
                    response: Response<WardPatientListResponse?>
                ) {

                    if (response.isSuccessful()) {
                        if (wardListObservable == null) {
                            wardListObservable = MutableLiveData<WardPatientListResponse?>()
                        }
                        wardListObservable!!.setValue(response.body())
                    } else {
                        if (response.code() == 705) {
                            errMsg[0] = "redirect"
                        } else if (response.code() == 403) {
                            errMsg[0] = "unauthorized"
                        } else {
                            errMsg[0] = Constants.API_ERROR
                        }
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = WardPatientListResponse()
                            if (wardListObservable == null) {
                                wardListObservable = MutableLiveData<WardPatientListResponse?>()
                            }
                            wardListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<WardPatientListResponse?>, t: Throwable) {
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is Exception) errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = WardPatientListResponse()

                        if (wardListObservable == null) {
                            wardListObservable = MutableLiveData<WardPatientListResponse?>()
                        }
                        wardListObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = WardPatientListResponse()
            if (wardListObservable == null) {
                wardListObservable = MutableLiveData<WardPatientListResponse?>()
            }
            wardListObservable!!.setValue(response)
        }
    }



    private fun getHospitalsRetro(id: Long) {
        val errMsg = arrayOfNulls<String>(1)
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = id.toString()
        ApiClient().getApiHospital(true, true)?.hospitallistresponseUser(bodyValues)?.enqueue(object :Callback<HospitalListResponse?>
        {

            override fun onResponse(call: Call<HospitalListResponse?>, response: Response<HospitalListResponse?>) {
    //
                    if (response.isSuccessful()) {
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData<HospitalListResponse?>()
                        }
                        hospitalListObservable!!.setValue(response.body())
                    } else {
                        if (response.code() == 705) {
                            errMsg[0] = "redirect"
                        } else if (response.code() == 403) {
                            errMsg[0] = "unauthorized"
                        } else {
                            errMsg[0] = Constants.API_ERROR
                        }
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = HospitalListResponse()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (hospitalListObservable == null) {
                                hospitalListObservable = MutableLiveData<HospitalListResponse?>()
                            }
                            hospitalListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<HospitalListResponse?>, t: Throwable) {

                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = HospitalListResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData<HospitalListResponse?>()
                        }
                        hospitalListObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = HospitalListResponse()
            response.setErrorMessage(errMsg[0])
            if (hospitalListObservable == null) {
                hospitalListObservable = MutableLiveData<HospitalListResponse?>()
            }
            hospitalListObservable!!.setValue(response)
        }
    }




    override fun onCleared() {
        super.onCleared()
        wardListObservable = null
        hospitalListObservable = null
    }
}