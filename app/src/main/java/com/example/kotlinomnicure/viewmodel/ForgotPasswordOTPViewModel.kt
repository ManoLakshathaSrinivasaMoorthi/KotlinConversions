package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeListResponse
import omnicurekotlin.example.com.userEndpoints.model.ForgotPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class ForgotPasswordOTPViewModel: ViewModel() {
    private var commonResponseObservable: MutableLiveData<CommonResponse?>? = null
    private var providerObservable: MutableLiveData<CommonResponse>? = null

    fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): MutableLiveData<CommonResponse?>? {
        commonResponseObservable = MutableLiveData()
        doForgotPassword(forgotPasswordRequest)
        return commonResponseObservable
    }

    fun resendEmailOTP(
        uid: Long,
        channel: String?,
        channelVal: String?,
        cc: String?,
    ): LiveData<CommonResponse>? {
        providerObservable = MutableLiveData()
        //        doResendEmailOTP(uid, channel, channelVal, cc);
        return providerObservable
    }

    fun resendMobileOTP(
        uid: Long,
        channel: String?,
        channelVal: String?,
        cc: String?,
    ): LiveData<CommonResponse>? {
        providerObservable = MutableLiveData()

        return providerObservable
    }


    private fun doForgotPassword(forgotPasswordRequest: ForgotPasswordRequest) {
        val call: Call<CommonResponse?>? =
            ApiClient().getApiUserEndpoints(true, decrypt = true)?.forgotPassword(forgotPasswordRequest)
        call?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>,
            ) {
                if (response.isSuccessful) {
                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData()
                    }
                    commonResponseObservable!!.setValue(response.body())
                } else {
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData()
                    }
                    commonResponseObservable!!.setValue(commonResponse)
                }
            }

            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {

//                Log.i("TAG", "onFailure: ERRRORRRRRRRR " +t.getMessage() + t.getCause());
                Handler(Looper.getMainLooper()).post {
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData()
                    }
                    commonResponseObservable!!.setValue(commonResponse)
                }
            }
        })

    }
    override fun onCleared() {
        super.onCleared()
        commonResponseObservable = null
        providerObservable = null
    }
}

