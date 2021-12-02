package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model.LoginRequest
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.google.gson.Gson
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.RedirectRequest
import omnicurekotlin.example.com.userEndpoints.model.VersionInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import java.util.HashMap


class SplashViewModel:ViewModel() {
    private val TAG = javaClass.simpleName
    private var versionInfoObservable: MutableLiveData<VersionInfoResponse?>? = null
    private var commonResponseObservable: MutableLiveData<CommonResponse>? = null
    private var tokenResponseObservable: MutableLiveData<CommonResponse>? = null

    //get the version info
    fun getVersionInfo(): LiveData<VersionInfoResponse?>? {
        versionInfoObservable = MutableLiveData()
        getVersionInfo(Constants.OsType.ANDROID.toString())
        return versionInfoObservable
    }

    fun redirectPage(redirectRequest: RedirectRequest?): MutableLiveData<CommonResponse>? {
        commonResponseObservable = MutableLiveData()
        //        setRedirectRequest(redirectRequest);
        if (redirectRequest != null) {
            setRedirectRequestRetro(redirectRequest)
        }
        return commonResponseObservable
    }
    fun renewToken(refreshToken: String?): LiveData<CommonResponse?>? {
        tokenResponseObservable = MutableLiveData()
        //        renewTokenApi(refreshToken);
        if (refreshToken != null) {
            renewTokenApiRetro(refreshToken)
        }
        return tokenResponseObservable
    }

    private fun renewTokenApiRetro(refreshToken: String) {
        val loginRequest = LoginRequest()
        loginRequest.setToken(refreshToken)
        val errMsg = arrayOfNulls<String>(1)
        ApiClient().getApiUserEndpoints(true, true)?.renewIdToken(loginRequest)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    Log.d(TAG, "onResponse:renewTokenCode-> " + response.code())
                    Log.d(TAG, "onResponse:renewToken-> " + Gson().toJson(response.body()))
                    if (response.isSuccessful) {
                        val commonResponse = response.body()
                        if (tokenResponseObservable == null) {
                            tokenResponseObservable = MutableLiveData()
                        }
                        tokenResponseObservable!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
//                Log.e(TAG, "onFailure: "+t.toString() );
                    errMsg[0] = Constants.APIErrorType.Exception.toString()
                    Handler(Looper.getMainLooper()).post {
                        val response = CommonResponse()
                        response.setErrorMessage(errMsg[0])
                        if (tokenResponseObservable == null) {
                            tokenResponseObservable = MutableLiveData()
                        }
                        tokenResponseObservable!!.setValue(response)
                    }
                }
            })
    }
    private fun getVersionInfo(osType: String) {
        // Parsing the params as body
        val errMsg = arrayOfNulls<String>(1)
        val creds = HashMap<String, String>()
        creds["osType"] = osType

        ApiClient().getApiUserEndpoints(false, false)?.getVersionInfo()
            ?.enqueue(object : Callback<VersionInfoResponse?> {
                override fun onResponse(
                    call: Call<VersionInfoResponse?>,
                    response: Response<VersionInfoResponse?>,
                ) {
                    if (response.isSuccessful()) {
                        Log.d("loginTags", "onResponse: " + response.code())
                        val versionInfoRes: VersionInfoResponse? =
                            response.body()
                        if (versionInfoObservable == null) {
                            versionInfoObservable = MutableLiveData()
                        }
                        versionInfoObservable!!.setValue(versionInfoRes)
                    } else {
                        Log.d("loginTags", "onResponse: " + response.code())
                        errMsg[0] = Constants.API_ERROR
                        Handler(Looper.getMainLooper()).post {
                            val versionInfoRes = VersionInfoResponse()
                            versionInfoRes.setErrorMessage(errMsg[0])
                            if (versionInfoObservable == null) {
                                versionInfoObservable = MutableLiveData()
                            }
                            versionInfoObservable!!.setValue(versionInfoRes)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<VersionInfoResponse?>,
                    t: Throwable,
                ) {
//                Log.e("loginTags", "onFailure: "+t.toString());
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val versionInfoRes = VersionInfoResponse()
                        versionInfoRes.setErrorMessage(errMsg[0])
                        if (versionInfoObservable == null) {
                            versionInfoObservable = MutableLiveData()
                        }
                        versionInfoObservable!!.setValue(versionInfoRes)
                    }
                }
            })
    }

    private fun setRedirectRequestRetro(redirectRequest: RedirectRequest) {
        val errMsg = arrayOfNulls<String>(1)
        ApiClient().getApiUserEndpoints(true, true)?.loginStatus(redirectRequest)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    Log.d(TAG, "onResponse: loginStatus " + response.code())
                    if (response.isSuccessful) {
                        val commonResponse = response.body()
                        if (tokenResponseObservable == null) {
                            tokenResponseObservable = MutableLiveData<CommonResponse>()
                        }
                        tokenResponseObservable!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
//                Log.e(TAG, "onFailure: loginStatus "+ t.toString() );
                    errMsg[0] = Constants.APIErrorType.Exception.toString()
                    Handler(Looper.getMainLooper()).post {
                        val response = CommonResponse()
                        response.setErrorMessage(errMsg[0])
                        if (tokenResponseObservable == null) {
                            tokenResponseObservable = MutableLiveData<CommonResponse>()
                        }
                        tokenResponseObservable!!.setValue(response)
                    }
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        versionInfoObservable = null
        commonResponseObservable = null
    }


}