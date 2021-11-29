package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.content.ContentValues
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model.SendChatMessageInputRequestModel
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model.SendChatMessageOutuputResponseModel
import com.example.dailytasksamplepoc.kotlinomnicure.model.SOSResponse
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.CommonPatientIdRequestBody
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.patientsEndpoints.model.PatientDetail
import omnicurekotlin.example.com.patientsEndpoints.model.PatientHistoryResponse
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class ChatActivityViewModel: ViewModel() {
    private val patientDischargeObservable: MutableLiveData<CommonResponse>? = null
    private val providerInviteObservable: MutableLiveData<CommonResponse>? = null
    private val providerInviteBroadcastObservable: MutableLiveData<CommonResponse>? = null
    private val startCallObservable: MutableLiveData<CommonResponse>? =
        null
    private val startSOSObservable: MutableLiveData<SOSResponse>? = null
    private val chatHistoryObservale: MutableLiveData<PatientHistoryResponse>? = null
    private var sendChatObservale: MutableLiveData<SendChatMessageOutuputResponseModel>? = null
    private val dischargePatientResponseObservable: MutableLiveData<CommonResponse>? = null
    private val providerObservable: MutableLiveData<CommonResponse>? =
        null
    private val memberListObservable: MutableLiveData<TeamsDetailListResponse>? = null
    private var commonResponseMutableLiveData: MutableLiveData<PatientDetail>? = null
    fun sendChatMessageCall(
        sendChatMessageInputRequestModel: SendChatMessageInputRequestModel?,
        chatId: String?,
    ): LiveData<SendChatMessageOutuputResponseModel?>? {
        sendChatObservale = MutableLiveData<SendChatMessageOutuputResponseModel>()
        sendChatMessages(sendChatMessageInputRequestModel, chatId)
        return sendChatObservale
    }
    fun getPatientDetails(providerId: Long?): LiveData<PatientDetail?>? {
        commonResponseMutableLiveData = MutableLiveData()

        if (providerId != null) {
            patientDetailsRetro(providerId)
        }
        return commonResponseMutableLiveData
    }

    private fun sendChatMessages(sendChatMessageInputRequestModel: SendChatMessageInputRequestModel?, chatId: String?) {

    }
    private fun patientDetailsRetro(uid: Long) {
        val errMsg = arrayOfNulls<String>(1)

        //sending body through data class
        val requestBody = CommonPatientIdRequestBody(uid)

        ApiClient().getApiPatientEndpoints(true, true)?.patientdetailsresponse(requestBody)
            ?.enqueue(object : Callback<PatientDetail?> {
                override fun onResponse(
                    call: Call<PatientDetail?>,
                    response: Response<PatientDetail?>,
                ) {
                    Log.d(ContentValues.TAG, "onResponse: " + response.code())
                    if (response.isSuccessful) {
                        val commonResponse = response.body()
                        if (commonResponseMutableLiveData == null) {
                            commonResponseMutableLiveData = MutableLiveData()
                        }
                        commonResponseMutableLiveData!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<PatientDetail?>, t: Throwable) {

                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is Exception) errMsg[0] = Constants.API_ERROR
                    val response = PatientDetail()
                    response.setErrorMsg(Constants.API_ERROR)
                    if (commonResponseMutableLiveData == null) {
                        commonResponseMutableLiveData = MutableLiveData()
                    }
                    commonResponseMutableLiveData!!.setValue(response)
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = PatientDetail()
            if (commonResponseMutableLiveData == null) {
                commonResponseMutableLiveData = MutableLiveData()
            }
            commonResponseMutableLiveData!!.setValue(response)
        }
    }

}