package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.RemoteHandOffRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteHandOffViewModel : ViewModel() {
    private val TAG = "RemoteHandOffViewModel"
    private var handOffRequestObservalbe: MutableLiveData<CommonResponse?>? = null

    fun performRemoteHandOff(remoteHandOffRequest: RemoteHandOffRequest): MutableLiveData<CommonResponse?>? {
        handOffRequestObservalbe = MutableLiveData<CommonResponse?>()
        remoteHandOff(remoteHandOffRequest)
        return handOffRequestObservalbe
    }

    private fun remoteHandOff(remoteHandOffRequest: RemoteHandOffRequest) {
        val errMsg = arrayOfNulls<String>(1)
        val call: Call<CommonResponse?>? =
            ApiClient().getApiProviderEndpoints(true, true)?.remoteHandOff(remoteHandOffRequest)
        call?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>,
            ) {
                if (response.isSuccessful()) {
                    if (handOffRequestObservalbe == null) {
                        handOffRequestObservalbe = MutableLiveData<CommonResponse?>()
                    }
                    handOffRequestObservalbe!!.setValue(response.body())
                } else {
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (handOffRequestObservalbe == null) {
                        handOffRequestObservalbe = MutableLiveData<CommonResponse?>()
                    }
                    handOffRequestObservalbe!!.setValue(commonResponse)
                }
            }

            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                val commonResponse = CommonResponse()
                commonResponse.setErrorMessage(Constants.API_ERROR)
                if (handOffRequestObservalbe == null) {
                    handOffRequestObservalbe = MutableLiveData<CommonResponse?>()
                }
                handOffRequestObservalbe!!.setValue(commonResponse)
            }
        })

    }


    override fun onCleared() {
        super.onCleared()
        handOffRequestObservalbe = null
    }
}