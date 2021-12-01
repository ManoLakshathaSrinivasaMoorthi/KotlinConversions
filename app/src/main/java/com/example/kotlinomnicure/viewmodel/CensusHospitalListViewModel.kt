package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants

import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

import java.util.HashMap

class CensusHospitalListViewModel : ViewModel() {
    private var hospitalListObservable: MutableLiveData<HospitalListResponse?>? = null
    private val TAG = "CensusHospitalListVM"

    fun getHospitalList(id: Long): LiveData<HospitalListResponse?>? {
        hospitalListObservable = MutableLiveData()
        //        getHospitals(id);
        getHospitalsRetro(id)
        return hospitalListObservable
    }

    private fun getHospitalsRetro(id: Long) {
        val errMsg = arrayOfNulls<String>(1)
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = id.toString()
        ApiClient().getApiHospital(true, true)?.hospitallistresponseUser(bodyValues)
            ?.enqueue(object : Callback<HospitalListResponse?>! {
                override fun onResponse(
                    call: Call<HospitalListResponse?>,
                    response: Response<HospitalListResponse?>,
                ) {

                    if (response.isSuccessful) {
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData()
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
                                hospitalListObservable = MutableLiveData()
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
                            hospitalListObservable = MutableLiveData()
                        }
                        hospitalListObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = HospitalListResponse()
            response.setErrorMessage(errMsg[0])
            if (hospitalListObservable == null) {
                hospitalListObservable = MutableLiveData()
            }
            hospitalListObservable!!.setValue(response)
        }
    }



    override fun onCleared() {
        super.onCleared()
        hospitalListObservable = null
    }
}