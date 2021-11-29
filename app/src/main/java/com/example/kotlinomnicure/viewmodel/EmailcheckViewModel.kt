package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.Provider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailcheckViewModel {
    private var providerObservable: MutableLiveData<CommonResponse?>? = null
    fun emailcheckProvider(provider: Provider): LiveData<CommonResponse?> {
        providerObservable = MutableLiveData()
        emailcheckRetro(provider)
        return providerObservable as MutableLiveData<CommonResponse?>
    }

    private fun emailcheckRetro(provider: Provider) {
        val errMsg = arrayOfNulls<String>(1)
        ApiClient().getApiUserEndpoints(true, true)?.emailcheckprovider(provider)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    Log.d("getCountryCodesRetro", "onResponse: " + response.code())
                    if (response.isSuccessful) {
                        val commonResponse = response.body()
                        Log.d("EmailCheck", "commonResponse $commonResponse")
                        Log.d(
                            "getCountryCodesRetro",
                            "onResponse: " + Gson().toJson(commonResponse)
                        )
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                    Log.d("flasmfjaslf", "onFailure: $t")
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val response = CommonResponse()
                        response.setErrorMessage(errMsg[0])
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData()
                        }
                        providerObservable!!.setValue(response)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            Handler(Looper.getMainLooper()).post {
                val response = CommonResponse()
                if (providerObservable == null) {
                    providerObservable = MutableLiveData()
                }
                providerObservable!!.setValue(response)
            }
        }
    }
}