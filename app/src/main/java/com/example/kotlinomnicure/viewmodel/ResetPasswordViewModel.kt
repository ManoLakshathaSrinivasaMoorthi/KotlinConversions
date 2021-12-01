package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.ResetPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordViewModel : ViewModel(){

    private val TAG = javaClass.simpleName
    private var commonResponseObservable: MutableLiveData<CommonResponse?>? = null

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest): MutableLiveData<CommonResponse?>? {
        commonResponseObservable = MutableLiveData<CommonResponse?>()
        doResetPassword(resetPasswordRequest)
        return commonResponseObservable
    }

    private fun doResetPassword(resetPasswordRequest: ResetPasswordRequest) {
        val errMsg = arrayOfNulls<String>(1)
        val call: Call<CommonResponse> =
            ApiClient().getApiUserEndpoints(true, true).resetPassword(resetPasswordRequest)
        call.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>
            ) {
                if (response.isSuccessful()) {
                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    commonResponseObservable!!.setValue(response.body())
                } else {
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

        /*
        new Thread(new Runnable() {
            String errMsg = "";

            @Override
            public void run() {
                try {
                    final CommonResponse commonResponse = EndPointBuilder.getUserEndpoints()
                            .resetPassword(resetPasswordRequest)
                            .execute();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (commonResponseObservable == null) {
                            commonResponseObservable = new MutableLiveData<>();
                        }
                        commonResponseObservable.setValue(commonResponse);
                    });
                } catch (SocketTimeoutException e) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString();
                } catch (Exception e) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR;
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        CommonResponse response = new CommonResponse();
                        if (commonResponseObservable == null) {
                            commonResponseObservable = new MutableLiveData<>();
                        }
                        commonResponseObservable.setValue(response);
                    });
                }
            }
        }).start();*/
    }

    override fun onCleared() {
        super.onCleared()
        commonResponseObservable = null
    }
}



