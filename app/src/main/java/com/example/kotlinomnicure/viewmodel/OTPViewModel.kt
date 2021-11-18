package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import retrofit2.Response
import java.net.SocketTimeoutException

class OTPViewModel:ViewModel() {
    private var providerObservable: MutableLiveData<CommonResponse>? = null
    private var uidObservable: MutableLiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>? = null
    private lateinit var retService: RetrofitService

    fun submitOTP(uid: Long, otp: String, fcm: String?, channel: String):LiveData<Response<CommonResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        providerObservable = MutableLiveData<CommonResponse>()

        val response = retService.getverifyOtp(uid,otp,fcm,channel)
        emit(response)
    }

    fun resendOTP(uid: Long, channel: String, channelVal: String, cc: String):LiveData<Response<CommonResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        providerObservable = MutableLiveData<CommonResponse>()

        val response = retService.getresendOtp(uid,channel,channelVal,cc)
        emit(response)
    }


    fun registerUid(pid: Long, uid: String): LiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>? {
        uidObservable = MutableLiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>()
      //  sendUid(pid, uid)
        return uidObservable
    }
/*
    private fun sendUid(providerId: Long, uid: String) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val apicall: ProviderEndpoints.RegisterUid = EndPointBuilder.getProviderEndpoints()
                            .registerUid(providerId, uid)
                    val commonResponse: omnicure.mvp.com.providerEndpoints.model.CommonResponse = apicall.execute()
                    Handler(Looper.getMainLooper()).post {
                        if (uidObservable == null) {
                            uidObservable = MutableLiveData<omnicure.mvp.com.providerEndpoints.model.CommonResponse>()
                        }
                        uidObservable!!.setValue(commonResponse)
                    }
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
                    errMsg = Constants.APIErrorType.Exception.toString()
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicure.mvp.com.providerEndpoints.model.CommonResponse = CommonResponse()
                        commonResponse.setErrorMessage(errMsg)
                        if (uidObservable == null) {
                            uidObservable = MutableLiveData<omnicure.mvp.com.providerEndpoints.model.CommonResponse>()
                        }
                        uidObservable!!.setValue(commonResponse)
                    }
                }
            }
        }).start()
    }*/

 /*   private fun verifyOTP(uid: Long, otp: String, fcm: String?, channel: String) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val apicall: UserEndpoints.RegistrationVerify = EndPointBuilder.getUserEndpoints()
                            .registrationVerify(uid, otp, channel)
                    if (fcm != null && fcm.length > 0) {
                        apicall.setFcmkey(fcm)
                    }
                    apicall.setOsType(Constants.OsType.ANDROID.toString())
                    val commonResponse: omnicure.mvp.com.userEndpoints.model.CommonResponse = apicall.execute()
                    Handler(Looper.getMainLooper()).post {
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
                    errMsg = Constants.APIErrorType.Exception.toString()
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicure.mvp.com.userEndpoints.model.CommonResponse = CommonResponse()
                        commonResponse.setErrorMessage(errMsg)
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                }
            }
        }).start()
    }
*/


    override fun onCleared() {
        super.onCleared()
        providerObservable = null
    }
}
