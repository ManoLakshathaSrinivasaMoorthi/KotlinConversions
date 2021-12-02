package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.model.CommonResponseRetro
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.GetProviderByIdRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.SaveAuditCallRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.SendMessageOnTopicRequestBody
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallActivityViewModel :ViewModel(){
    private val TAG = "CallActViewModel"
    private var providerObservable: MutableLiveData<CommonResponse?>? =
        null
    private var sosDismissObservable: MutableLiveData<CommonResponseRetro?>? = null
    private var callAuditObservable: MutableLiveData<CommonResponseRetro?>? = null


    fun getProviderById(id: Long, token: String, providerId: Long): LiveData<CommonResponse?>? {
        providerObservable =
            MutableLiveData<CommonResponse?>()
        getProvider(id, token, providerId)
        return providerObservable
    }

    fun sendSOSDismiss(
        callerId: Long,
        token: String,
        channel: String,
        type: String,
        receiverIds: String,
        auditId: String,
    ): LiveData<CommonResponseRetro?>? {
        sosDismissObservable = MutableLiveData<CommonResponseRetro?>()
        //        sendSOSDismissAPI(callerId,token,channel,type, receiverIds, auditId);
        sendSOSDismissAPIRetro(callerId, token, channel, type, receiverIds, auditId)
        return sosDismissObservable
    }

    fun sendAuditId(
        auditId: String,
        callAttend: Boolean,
        pid: String,
        type: String,
    ): LiveData<CommonResponseRetro?>? {
        callAuditObservable = MutableLiveData<CommonResponseRetro?>()
        //        sendAudit(auditId,callAttend,pid,type);
        sendAuditRetro(auditId, callAttend, pid, type)
        return callAuditObservable
    }


    private fun sendSOSDismissAPIRetro(
        callerId: Long,
        token: String,
        channel: String,
        type: String,
        receiverIds: String,
        auditId: String,
    ) {
        val errMsg = arrayOfNulls<String>(1)

        ApiClient().getApiProviderEndpoints(true, true)?.sendMessageOnTopic(SendMessageOnTopicRequestBody(
                callerId,
                token,
                channel,
                type))?.enqueue(object : Callback<CommonResponseRetro?> {
                override fun onResponse(
                    call: Call<CommonResponseRetro?>,
                    response: Response<CommonResponseRetro?>,
                ) {

                    if (response.isSuccessful()) {
                        val commonResponse: CommonResponseRetro? = response.body()
                        if (sosDismissObservable == null) {
                            sosDismissObservable = MutableLiveData<CommonResponseRetro?>()
                        }
                        sosDismissObservable!!.setValue(commonResponse)
                    } else {
                        if (response.code() == 705) {
                            errMsg[0] = "redirect"
                        } else if (response.code() == 403) {
                            errMsg[0] = "unauthorized"
                        } else {
                            errMsg[0] = Constants.API_ERROR
                        }
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = CommonResponseRetro()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (sosDismissObservable == null) {
                                sosDismissObservable = MutableLiveData<CommonResponseRetro?>()
                            }
                            sosDismissObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponseRetro?>, t: Throwable) {

                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponseRetro()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (sosDismissObservable == null) {
                            sosDismissObservable = MutableLiveData<CommonResponseRetro?>()
                        }
                        sosDismissObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = CommonResponseRetro()

            response.setErrorMessage(errMsg[0])
            if (sosDismissObservable == null) {
                sosDismissObservable = MutableLiveData<CommonResponseRetro?>()
            }
            sosDismissObservable!!.setValue(response)
        }
    }



    private fun getProvider(id: Long, token: String, providerId: Long) {
        val errMsg = arrayOf("")

        val url = "providerEndpoints/v1/getProviderById"
        ApiClient().getApiProviderEndpoints(true, true)
            ?.getProviderById(url, GetProviderByIdRequestBody(providerId, token, id))
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    if (response.isSuccessful()) {
//
                        val commonResponse:CommonResponse? =
                            response.body()
                        if (providerObservable == null) {
                            providerObservable =
                                MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    } else {
//                            Log.d("Verifytags", "onResponse: " + response.code());
                        if (response.code() == 705) {
                            errMsg[0] = "redirect"
                        } else if (response.code() == 403) {
                            errMsg[0] = "unauthorized"
                        } else {
                            errMsg[0] = Constants.API_ERROR
                        }
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: CommonResponse =
                                CommonResponse()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (providerObservable == null) {
                                providerObservable =
                                    MutableLiveData<CommonResponse?>()
                            }
                            providerObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable,
                ) {
//                        Log.e("loginTags", "onFailure: " + t.toString());
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (providerObservable == null) {
                            providerObservable =
                                MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val finalErrMsg = errMsg[0]
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.setErrorMessage(finalErrMsg)
                if (providerObservable == null) {
                    providerObservable =
                        MutableLiveData<CommonResponse?>()
                }
                providerObservable!!.setValue(commonResponse)
            }
        }
    }


    private fun sendAuditRetro(auditId: String, callStatus: Boolean, pid: String, type: String) {

        val errMsg = arrayOfNulls<String>(1)
        // Parsing params in body

        val requestBody = SaveAuditCallRequestBody(auditId, callStatus, type, pid)


        ApiClient().getApiProviderEndpoints(true, true)?.saveAuditCall(requestBody)
            ?.enqueue(object : Callback<CommonResponseRetro?> {
                override fun onResponse(
                    call: Call<CommonResponseRetro?>,
                    response: Response<CommonResponseRetro?>,
                ) {

                    if (response.isSuccessful()) {
                        val providerResponse: CommonResponseRetro? = response.body()
                        if (callAuditObservable == null) {
                            callAuditObservable = MutableLiveData<CommonResponseRetro?>()
                        }
                        callAuditObservable!!.setValue(providerResponse)
                    } else {
                        if (response.code() == 705) {
                            errMsg[0] = "redirect"
                        } else if (response.code() == 403) {
                            errMsg[0] = "unauthorized"
                        } else {
                            errMsg[0] = Constants.API_ERROR
                        }
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = CommonResponseRetro()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (callAuditObservable == null) {
                                callAuditObservable = MutableLiveData<CommonResponseRetro?>()
                            }
                            callAuditObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponseRetro?>, t: Throwable) {

                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponseRetro()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (callAuditObservable == null) {
                            callAuditObservable = MutableLiveData<CommonResponseRetro?>()
                        }
                        callAuditObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = CommonResponseRetro()
            response.setErrorMessage(errMsg[0])
            if (callAuditObservable == null) {
                callAuditObservable = MutableLiveData<CommonResponseRetro?>()
            }
            callAuditObservable!!.setValue(response)
        }
    }

    protected override fun onCleared() {
        super.onCleared()
        providerObservable = null
        callAuditObservable = null
        sosDismissObservable = null
    }
}