package com.example.kotlinomnicure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


import java.util.HashMap


class SplashViewModel:ViewModel() {
    private val TAG = javaClass.simpleName
    private var versionInfoObservable: MutableLiveData<VersionInfoResponse?>? = null
    private var commonResponseObservable: MutableLiveData<CommonResponse>? = null

    //get the version info
    fun getVersionInfo(): LiveData<VersionInfoResponse?>? {
        versionInfoObservable = MutableLiveData()
        getVersionInfo(Constants.OsType.ANDROID.toString())
        return versionInfoObservable
    }


    // Convert to retrofit
    private fun getVersionInfo(osType: String) {
        // Parsing the params as body
        val errMsg = arrayOfNulls<String>(1)
        val creds = HashMap<String, String>()
        creds["osType"] = osType

       /* ApiClient().getApiUserEndpoints(false, false).versionInfo.enqueue(object :
            Callback<VersionInfoResponse?> {
            override fun onResponse(
                call: Call<VersionInfoResponse?>,
                response: Response<VersionInfoResponse?>
            ) {
                if (response.isSuccessful) {
                    Log.d("loginTags", "onResponse: " + response.code())
                    val versionInfoRes = response.body()
                    if (versionInfoObservable == null) {
                        versionInfoObservable = MutableLiveData()
                    }
                    versionInfoObservable!!.setValue(versionInfoRes)
                } else {
                    Log.d("loginTags", "onResponse: " + response.code())
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val versionInfoRes =
                            VersionInfoResponse()
                        versionInfoRes.errorMessage = errMsg[0]
                        if (versionInfoObservable == null) {
                            versionInfoObservable =
                                MutableLiveData()
                        }
                        versionInfoObservable!!.setValue(versionInfoRes)
                    }
                }
            }

            override fun onFailure(call: Call<VersionInfoResponse?>, t: Throwable) {
                Log.e("loginTags", "onFailure: $t")
                errMsg[0] = Constants.API_ERROR
                Handler(Looper.getMainLooper()).post {
                    val versionInfoRes =
                        VersionInfoResponse()
                    versionInfoRes.errorMessage = errMsg[0]
                    if (versionInfoObservable == null) {
                        versionInfoObservable =
                            MutableLiveData()
                    }
                    versionInfoObservable!!.setValue(versionInfoRes)
                }
            }
        })*/
    }
    override fun onCleared() {
        super.onCleared()
        versionInfoObservable = null
        commonResponseObservable = null
    }


}