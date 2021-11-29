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
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.Provider


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LocalCareProviderSignUpSecondViewModel:ViewModel() {
    private var providerObservable: MutableLiveData<CommonResponse>? = null

     fun registerProvider(provider: Provider?): LiveData<CommonResponse>? {
        providerObservable = MutableLiveData()
        if (provider != null) {
            doRegisterRetro(provider)
        }
        return providerObservable
    }


    private  fun doRegisterRetro(provider: Provider) {

        var errMsg = ""
        try {


            ApiClient().getApiUserEndpoints(true, true)?.registerUser(provider)
                ?.enqueue(object : Callback<CommonResponse?> {
                    override fun onResponse(
                        call: Call<CommonResponse?>,
                        response: Response<CommonResponse?>
                    ) {
                        Log.e("Signupsecond", "onResponse: " + Gson().toJson(response.body()))
                        if (response.isSuccessful) {
                            val commonResponse = response.body()
                            Handler(Looper.getMainLooper()).post {
                                if (providerObservable == null) {
                                    providerObservable =
                                        MutableLiveData()
                                }
                                providerObservable!!.setValue(commonResponse)
                            }
                        }
                    }

                    override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse =
                                CommonResponse()
                            commonResponse.errorMessage = Constants.API_ERROR
                            if (providerObservable == null) {
                                providerObservable =
                                    MutableLiveData()
                            }
                            providerObservable!!.setValue(commonResponse)
                        }
                    }
                })
        } catch (e: Exception) {

            errMsg = Constants.API_ERROR
        }
        if (!TextUtils.isEmpty(errMsg)) {
            val finalErrMsg = errMsg
            Handler(Looper.getMainLooper()).post {
                val commonResponse =
                    CommonResponse()
                commonResponse.errorMessage = finalErrMsg
                if (providerObservable == null) {
                    providerObservable =
                        MutableLiveData()
                }
                providerObservable!!.setValue(commonResponse)
            }
        }


    }

    override fun onCleared() {
        super.onCleared()
        providerObservable = null
    }

}