package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.utils.Constants
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.interfaces.UserEndpoints
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class HospitalListViewModel: ViewModel() {
    private var hospitalListObservable: MutableLiveData<HospitalListResponse?>? = null
    private val TAG = "HospitalListViewModel"

    fun getHospitalList(): LiveData<HospitalListResponse?>? {
        hospitalListObservable = MutableLiveData<HospitalListResponse?>()

        getHospitalsRetro()
        return hospitalListObservable
    }



    private fun getHospitalsRetro() {
        val errMsg = arrayOfNulls<String>(1)
        //POST changed into GET by backend.
        ApiClient().getApiUserEndpoints(true, true)?.hospitalList()
            ?.enqueue(object : Callback<HospitalListResponse?> {
                override fun onResponse(
                    call: Call<HospitalListResponse?>,
                    response: Response<HospitalListResponse?>) {
                    Log.d(TAG, "onResponse: hospitalList " + response.code())
                    if (response.isSuccessful()) {
                        val hospitalListResponse: HospitalListResponse? = response.body()
                        Log.d(TAG, "onResponse: res " + Gson().toJson(response.body()))
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData<HospitalListResponse?>()
                        }
                        hospitalListObservable!!.setValue(hospitalListResponse)
                    }
                }

                override fun onFailure(call: Call<HospitalListResponse?>, t: Throwable) {
                    Log.e(TAG, "onFailure: hospitalList $t")
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val response = HospitalListResponse()
                        response.setErrorMessage(errMsg[0])
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData<HospitalListResponse?>()
                        }
                        hospitalListObservable!!.setValue(response)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            Handler(Looper.getMainLooper()).post {
                val response = HospitalListResponse()
                response.setErrorMessage(errMsg[0])
                if (hospitalListObservable == null) {
                    hospitalListObservable = MutableLiveData<HospitalListResponse?>()
                }
                hospitalListObservable!!.setValue(response)
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        hospitalListObservable = null
    }
}