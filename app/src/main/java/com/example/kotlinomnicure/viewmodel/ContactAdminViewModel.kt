package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.backend.EndPointBuilder
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.ContactAdminParams
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
        ApiClient().getApiProviderEndpoints(true, true)?.sendContactAdminEmail(params)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d("VerifyTags", "onResponse: " + response.code())
                        val commonResponse:CommonResponse? =
                            response.body()
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("Verifytags", "onResponse: " + response.code())
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse:CommonResponse =
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
                    call: Call<CommonResponse?>,
                    t: Throwable
                ) {
                    Log.e("loginTags", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
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
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.setErrorMessage(errMsg)
                if (providerObservable == null) {
                    providerObservable = MutableLiveData<CommonResponse?>()
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

