package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.apiRetrofit.RequestBodys.GetHandoffListRequestBody
import com.example.kotlinomnicure.apiRetrofit.RequestBodys.GetReadStatusRequestBody
import com.example.kotlinomnicure.utils.Constants
import com.google.gson.Gson

import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ENotesViewModel: ViewModel() {
    private val TAG = ENotesViewModel::class.java.simpleName
    private var handOffListObservable: MutableLiveData<CommonResponse?>? = null
    private var logListObservable: MutableLiveData<CommonResponse?>? = null
    private var readStatusObservable: MutableLiveData<CommonResponse?>? = null

    fun getHandOff(providerId: Long, patientId: Long): LiveData<CommonResponse?>? {
        handOffListObservable = MutableLiveData<CommonResponse?>()
        getHandoffList(providerId, patientId)
        return handOffListObservable
    }

    fun getLogs(patientId: Long): LiveData<CommonResponse?>? {
        logListObservable = MutableLiveData<CommonResponse?>()
        getLogsApi(patientId)
        return logListObservable
    }

    fun setRead(patientId: Long, providerId: Long, messageId: String): LiveData<CommonResponse?>? {
        readStatusObservable = MutableLiveData<CommonResponse?>()
        setReadApi(patientId, providerId, messageId)
        return readStatusObservable
    }


    private fun getHandoffList(providerId: Long, patientId: Long) {
        val errMsg = ""

        //Backend changed the endpoint.
        val url = "providerEndpoints/v1/getEnotes"
        ApiClient().getApiProviderEndpoints(true, true)?.getHandoffList(url, GetHandoffListRequestBody(providerId, patientId))
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "HandoffList-onResponse: $response")
                        Log.d(TAG, "HandoffList-onResponse: " + Gson().toJson(response.body()))
                        val commonResponse: CommonResponse? = response.body()
                        if (handOffListObservable == null) {
                            handOffListObservable = MutableLiveData<CommonResponse?>()
                        }
                        handOffListObservable!!.setValue(commonResponse)
                    } else {
                        Log.d(TAG, "HandoffList-onErrpr:: $response")
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = CommonResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (handOffListObservable == null) {
                                handOffListObservable = MutableLiveData<CommonResponse?>()
                            }
                            handOffListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (handOffListObservable == null) {
                            handOffListObservable = MutableLiveData<CommonResponse?>()
                        }
                        handOffListObservable!!.setValue(commonResponse)
                    }
                }
            })

//        Log.e(TAG, "getProviders: errMsgDetail-->"+errMsg );
    }

    private fun getLogsApi(patientId: Long) {
        val errMsg = ""

        //Backend changed the endpoint.
        val url = "providerEndpoints/v1/getEnotesActivityLog"
        ApiClient().getApiProviderEndpoints(true, true)
            ?.getHandoffList(url, GetHandoffListRequestBody(0L, patientId))
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "logList-onResponse: $response")
                        Log.d(TAG, "logList-onResponse: " + Gson().toJson(response.body()))
                        val commonResponse: CommonResponse? = response.body()
                        if (logListObservable == null) {
                            logListObservable = MutableLiveData<CommonResponse?>()
                        }
                        logListObservable!!.setValue(commonResponse)
                    } else {
                        Log.d(TAG, "logList-onErrpr:: $response")
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = CommonResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (logListObservable == null) {
                                logListObservable = MutableLiveData<CommonResponse?>()
                            }
                            logListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable
                ) {
//                        Log.e(TAG, "logList-onFailure: " + t.toString());
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (logListObservable == null) {
                            logListObservable = MutableLiveData<CommonResponse?>()
                        }
                        logListObservable!!.setValue(commonResponse)
                    }
                }
            })

//        Log.e(TAG, "getProviders: logList-errMsgDetail-->"+errMsg );
    }


    private fun setReadApi(patientId: Long, providerId: Long, messageId: String) {
        val errMsg = ""

        //Backend changed the endpoint.
        val url = "providerEndpoints/v1/updateMessageRead"
        ApiClient().getApiProviderEndpoints(true, true)
            ?.setReadStatus(url, GetReadStatusRequestBody(providerId, patientId, messageId))
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful()) {
//                            Log.d(TAG, "read-onResponse: " + response);
//                            Log.d(TAG, "read-onResponse: " + new Gson().toJson(response.body()));
                        val commonResponse: CommonResponse? = response.body()
                        if (readStatusObservable == null) {
                            readStatusObservable = MutableLiveData<CommonResponse?>()
                        }
                        readStatusObservable!!.setValue(commonResponse)
                    } else {
//                            Log.d(TAG, "read-onErrpr:: " + response);
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = CommonResponse()
                            commonResponse.errorMessage
                            if (readStatusObservable == null) {
                                readStatusObservable = MutableLiveData<CommonResponse?>()
                            }
                            readStatusObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable
                ) {
//                        Log.e(TAG, "read-onFailure: " + t.toString());
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponse()
                        commonResponse.errorMessage
                        if (readStatusObservable == null) {
                            readStatusObservable = MutableLiveData<CommonResponse?>()
                        }
                        readStatusObservable!!.setValue(commonResponse)
                    }
                }
            })

//        Log.e(TAG, "getProviders: read-errMsgDetail-->"+errMsg );
    }


    override fun onCleared() {
        super.onCleared()
        handOffListObservable = null
        logListObservable = null
        readStatusObservable = null
    }
}