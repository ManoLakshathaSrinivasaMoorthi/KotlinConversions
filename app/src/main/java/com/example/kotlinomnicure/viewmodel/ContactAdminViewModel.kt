package com.example.kotlinomnicure.viewmodel

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

class ContactAdminViewModel:ViewModel() {

    private var providerObservable: MutableLiveData<CommonResponse?>? = null

    fun contactAdminEmail(params: ContactAdminParams): LiveData<CommonResponse?>? {
        providerObservable = MutableLiveData<CommonResponse?>()
        contactAdmin(params)
        return providerObservable
    }

    private fun contactAdmin(params: ContactAdminParams) {
        val errMsg = ""
        ApiClient.getApiProviderEndpoints(true, true).sendContactAdminEmail(params)
            .enqueue(object : Callback<omnicure.mvp.com.providerEndpoints.model.CommonResponse?> {
                override fun onResponse(
                    call: Call<omnicure.mvp.com.providerEndpoints.model.CommonResponse?>,
                    response: Response<omnicure.mvp.com.providerEndpoints.model.CommonResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d("VerifyTags", "onResponse: " + response.code())
                        val commonResponse: omnicure.mvp.com.providerEndpoints.model.CommonResponse? =
                            response.body()
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("Verifytags", "onResponse: " + response.code())
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: omnicure.mvp.com.providerEndpoints.model.CommonResponse =
                                CommonResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (providerObservable == null) {
                                providerObservable = MutableLiveData<CommonResponse?>()
                            }
                            providerObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<omnicure.mvp.com.providerEndpoints.model.CommonResponse?>,
                    t: Throwable
                ) {
                    Log.e("loginTags", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicure.mvp.com.providerEndpoints.model.CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: omnicure.mvp.com.providerEndpoints.model.CommonResponse =
                    CommonResponse()
                commonResponse.setErrorMessage(errMsg)
                if (providerObservable == null) {
                    providerObservable = MutableLiveData<CommonResponse?>()
                }
                providerObservable!!.setValue(commonResponse)
            }
        }
    }

    // old api
    private fun contactAdmins(params: ContactAdminParams) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val commonResponse: CommonResponse = EndPointBuilder.getProviderEndpoints()
                        .sendAdminEmail(params)
                        .execute()
                    Handler(Looper.getMainLooper()).post {
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
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
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                }
            }
        }).start()
    }

    override fun onCleared() {
        super.onCleared()
        providerObservable = null
    }
}

