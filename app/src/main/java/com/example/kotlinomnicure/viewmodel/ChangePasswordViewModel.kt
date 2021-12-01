package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model.CommonResponse
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.userEndpoints.model.ResetPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordViewModel:ViewModel() {
    private val TAG = javaClass.simpleName
    private var commonResponseObservable: MutableLiveData<CommonResponse?>? = null

    fun changePassword(resetPasswordRequest: ResetPasswordRequest): MutableLiveData<CommonResponse?>? {
        commonResponseObservable = MutableLiveData<CommonResponse?>()
        doChangePassword(resetPasswordRequest)
        return commonResponseObservable
    }


    private fun doChangePassword(resetPasswordRequest: ResetPasswordRequest) {
        val errMsg = arrayOf("")
        val call: Call<CommonResponse> =
            ApiClient().getApiUserEndpoints(true, true).changePassword(resetPasswordRequest)
        call.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>,
            ) {
                if (response.isSuccessful()) {

                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    commonResponseObservable!!.setValue(response.body())
                } else {

                    if (response.code() == 705) {
                        errMsg[0] = "redirect"
                    } else if (response.code() == 403) {
                        errMsg[0] = "unauthorized"
                    } else {
                        errMsg[0] = Constants.API_ERROR
                    }
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (commonResponseObservable == null) {
                            commonResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        commonResponseObservable!!.setValue(commonResponse)
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {

                errMsg[0] = Constants.API_ERROR
                Handler(Looper.getMainLooper()).post {
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(errMsg[0])
                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    commonResponseObservable!!.setValue(commonResponse)
                }
            }
        })


    }

    override fun onCleared() {
        super.onCleared()
        commonResponseObservable = null
    }
}
