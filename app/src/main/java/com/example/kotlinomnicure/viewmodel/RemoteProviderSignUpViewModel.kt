package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import omnicurekotlin.example.com.userEndpoints.model.Provider
import omnicurekotlin.example.com.userEndpoints.model.RemoteProviderListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class RemoteProviderSignUpViewModel: ViewModel() {
    private var providerObservable: MutableLiveData<CommonResponse?>? = null
    private var hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private var remoteProviderListObservable: MutableLiveData<RemoteProviderListResponse?>? = null

    fun registerProvider(provider: Provider): LiveData<CommonResponse?>? {
        providerObservable = MutableLiveData<CommonResponse?>()
        //        doRegister(provider);
//        Log.e("TAG", "registerProvider: api hit-->"+new Gson().toJson(provider) );
        doRegisterRetro(provider)
        return providerObservable
    }

    fun getRemoteProviderList(): LiveData<RemoteProviderListResponse?>? {
        remoteProviderListObservable = MutableLiveData<RemoteProviderListResponse?>()
        //        getRemoteProviderType();
        getRemoteProviderTypeRetro()
        return remoteProviderListObservable
    }




    private fun doRegisterRetro(provider: Provider) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {


                    ApiClient().getApiUserEndpoints(true, true)?.registerUser(provider)
                        ?.enqueue(object : Callback<CommonResponse?> {
                            override fun onResponse(
                                call: Call<CommonResponse?>,
                                response: Response<CommonResponse?>,
                            ) {

                                if (response.isSuccessful()) {
                                    val commonResponse: CommonResponse? = response.body()
                                    Handler(Looper.getMainLooper()).post {
                                        if (providerObservable == null) {
                                            providerObservable = MutableLiveData<CommonResponse?>()
                                        }
                                        providerObservable!!.setValue(commonResponse)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                                errMsg = Constants.API_ERROR
                            }
                        })
                } catch (e: Exception) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    val finalErrMsg = errMsg
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponse()
                        commonResponse.setErrorMessage(finalErrMsg)
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                }
            }
        }).start()
    }





    private fun getRemoteProviderTypeRetro() {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val call: Call<RemoteProviderListResponse?>? =
                        ApiClient().getApiUserEndpoints(true, true)?.getRemoteproviderTypeList()
                    call?.enqueue(object : Callback<RemoteProviderListResponse?> {
                        override fun onResponse(
                            call: Call<RemoteProviderListResponse?>,
                            response: Response<RemoteProviderListResponse?>,
                        ) {
                            if (response.isSuccessful()) {
                                if (remoteProviderListObservable == null) {
                                    remoteProviderListObservable =
                                        MutableLiveData<RemoteProviderListResponse?>()
                                }
                                remoteProviderListObservable!!.setValue(response.body())
                            } else {
                                val remoteProviderListResponse = RemoteProviderListResponse()
                                Handler(Looper.getMainLooper()).post {
                                    if (remoteProviderListObservable == null) {
                                        remoteProviderListObservable =
                                            MutableLiveData<RemoteProviderListResponse?>()
                                    }
                                    remoteProviderListObservable!!.setValue(
                                        remoteProviderListResponse)
                                }
                            }
                        }

                        override fun onFailure(
                            call: Call<RemoteProviderListResponse?>,
                            t: Throwable,
                        ) {

//                            Log.i("TAG", "onFailure: ERRRORRRRRRRR " +t.getMessage() + t.getCause());
                            Handler(Looper.getMainLooper()).post {
                                val remoteProviderListResponse =
                                    RemoteProviderListResponse()
                                if (remoteProviderListObservable == null) {
                                    remoteProviderListObservable =
                                        MutableLiveData<RemoteProviderListResponse?>()
                                }
                                remoteProviderListObservable!!.setValue(remoteProviderListResponse)
                            }
                        }
                    })
                } catch (e: Exception) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val response = RemoteProviderListResponse()
                        if (remoteProviderListObservable == null) {
                            remoteProviderListObservable =
                                MutableLiveData<RemoteProviderListResponse?>()
                        }
                        remoteProviderListObservable!!.setValue(response)
                    }
                }
            }
        }).start()
    }


    override fun onCleared() {
        super.onCleared()
        providerObservable = null
        remoteProviderListObservable = null
    }
}