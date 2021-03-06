package com.example.kotlinomnicure.viewmodel

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.apiRetrofit.RequestBodys.CommonPatientIdRequestBody
import com.example.kotlinomnicure.model.SOSResponse

import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.TeamDetailsByNameRequestBody

import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.PatientDetail

import omnicurekotlin.example.com.patientsEndpoints.model.PatientHistoryResponse
import omnicurekotlin.example.com.providerEndpoints.model.GroupCall
import omnicurekotlin.example.com.providerEndpoints.model.SendChatMessageInputRequestModel
import omnicurekotlin.example.com.providerEndpoints.model.SendChatMessageOutuputResponseModel
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.HashMap

class ChatActivityViewModel: ViewModel() {
    private val patientDischargeObservable: MutableLiveData<CommonResponse>? = null
    private val providerInviteObservable: MutableLiveData<CommonResponse>? = null
    private var providerInviteBroadcastObservable: MutableLiveData<CommonResponse>? = null
    private val startCallObservable: MutableLiveData<CommonResponse>? =
        null
    private var startSOSObservable: MutableLiveData<SOSResponse>? = null
    private var chatHistoryObservale: MutableLiveData<PatientHistoryResponse>? = null
    private var sendChatObservale: MutableLiveData<SendChatMessageOutuputResponseModel>? = null
    private val dischargePatientResponseObservable: MutableLiveData<CommonResponse>? = null
    private var providerObservable: MutableLiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>? =
        null

    private var memberListObservable: MutableLiveData<TeamsDetailListResponse>? = null
    private var commonResponseMutableLiveData: MutableLiveData<PatientDetail>? = null
    fun sendChatMessageCall(
        sendChatMessageInputRequestModel: SendChatMessageInputRequestModel?,
        chatId: String?,
    ): LiveData<SendChatMessageOutuputResponseModel?>? {
        sendChatObservale = MutableLiveData<SendChatMessageOutuputResponseModel>()
        sendChatMessages(sendChatMessageInputRequestModel, chatId)
        return sendChatObservale
    }
    fun getMemberList(patientId: Long?, team: String?): LiveData<TeamsDetailListResponse?>? {
        memberListObservable = MutableLiveData()

        if (patientId != null) {
            if (team != null) {
                getMembersRetro(patientId, team)
            }
        }
        return memberListObservable
    }
    private fun getMembersRetro(patientId: Long, team: String) {
        val errMsg = arrayOfNulls<String>(1)

        ApiClient().getApiProviderEndpoints(true, true)
            ?.teamDetailsByName(TeamDetailsByNameRequestBody(patientId, team))
            ?.enqueue(object : Callback<TeamsDetailListResponse?> {

                override fun onResponse(
                    call: Call<TeamsDetailListResponse?>,
                    response: Response<TeamsDetailListResponse?>
                ) {
                    Log.d(ContentValues.TAG, "onResponse: " + response.code())
                    if (response.isSuccessful) {
                        val providerListResponse = response.body()
                        if (memberListObservable == null) {
                            memberListObservable = MutableLiveData()
                        }
                        memberListObservable!!.setValue(providerListResponse)
                    }
                }

                override fun onFailure(call: Call<TeamsDetailListResponse?>, t: Throwable) {
//                Log.e(TAG, "onFailure: " + t.toString() );
                    errMsg[0] = Constants.API_ERROR
                    val response = TeamsDetailListResponse()
                    response.setErrorMessage(errMsg[0])
                    if (memberListObservable == null) {
                        memberListObservable = MutableLiveData()
                    }
                    memberListObservable!!.setValue(response)
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = TeamsDetailListResponse()
            response.setErrorMessage(errMsg[0])
            if (memberListObservable == null) {
                memberListObservable = MutableLiveData()
            }
            memberListObservable!!.setValue(response)
        }
    }


    fun startSOS(
        callerId: Long?,
        token: String?,
        patientId: Long?,
        auditId: String?
    ): LiveData<SOSResponse?>? {
        startSOSObservable = MutableLiveData()
        //startSOSAPI(callerId, token, patientId, auditId)
        return startSOSObservable
    }


    fun getPatientChatHistory(
        providerId: Long?,
        token: String?,
        patientId: Long?
    ): LiveData<PatientHistoryResponse?>? {
        chatHistoryObservale = MutableLiveData()
        if (providerId != null) {
            if (token != null) {
                if (patientId != null) {
                    getPatientHistory(providerId, token, patientId)
                }
            }
        }
        return chatHistoryObservale
    }
    fun multipleCall(content: GroupCall?): LiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>? {
        providerObservable = MutableLiveData()
        MultiplecallRetro(content)
        return providerObservable
    }
    private fun getPatientHistory(providerId: Long, token: String, patientId: Long) {
        val errMsg = ""
        // Parsing the values in body
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = providerId.toString()
        bodyValues["token"] = token
        bodyValues["patientId"] = patientId.toString()
        ApiClient().getApiPatientEndpoints(true, true)?.patienthistoryAPI(bodyValues) //        ApiClient.getApiPatientEndpoints(false, false).

            ?.enqueue(object : Callback<PatientHistoryResponse?> {
                override fun onResponse(call: Call<PatientHistoryResponse?>, response: Response<PatientHistoryResponse?>) {
                    if (response.isSuccessful) {
                        Log.d("GetPatientHistory", "onResponse: $response")
                        val commonResponse = response.body()
                        if (chatHistoryObservale == null) {
                            chatHistoryObservale = MutableLiveData()
                        }
                        chatHistoryObservale!!.setValue(commonResponse)
                    } else {
                        Log.d("GetPatientHistory", "onResponse: $response")
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = PatientHistoryResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (chatHistoryObservale == null) {
                                chatHistoryObservale = MutableLiveData()
                            }
                            chatHistoryObservale!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<PatientHistoryResponse?>, t: Throwable) {
                    Log.d("discharge", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = PatientHistoryResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (chatHistoryObservale == null) {
                            chatHistoryObservale = MutableLiveData()
                        }
                        chatHistoryObservale!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse = PatientHistoryResponse()
                commonResponse.setErrorMessage(errMsg)
                if (chatHistoryObservale == null) {
                    chatHistoryObservale = MutableLiveData()
                }
                chatHistoryObservale!!.setValue(commonResponse)
            }
        }
    }


    private fun MultiplecallRetro(content: GroupCall?) {

        val call: Call<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>? =
            ApiClient().getApiProviderEndpoints(true, true)?.Multiplecall(content)
        call?.enqueue(object : Callback<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?> {
            override fun onResponse(
                call: Call<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>,
                response: Response<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>,
            ) {


                if (response.isSuccessful()) {

                    if (providerObservable == null) {
                        providerObservable = MutableLiveData()
                    }
                    providerObservable!!.setValue(response.body())
                } else {

                    val commonResponse: omnicurekotlin.example.com.providerEndpoints.model.CommonResponse =
                        omnicurekotlin.example.com.providerEndpoints.model.CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (providerObservable == null) {
                        providerObservable = MutableLiveData()
                    }
                    providerObservable!!.setValue(commonResponse)
                }
            }

            override fun onFailure(
                call: Call<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse?>,
                t: Throwable,
            ) {

                val commonResponse: omnicurekotlin.example.com.providerEndpoints.model.CommonResponse =
                    omnicurekotlin.example.com.providerEndpoints.model.CommonResponse()
                commonResponse.setErrorMessage(Constants.API_ERROR)
                if (providerObservable == null) {
                    providerObservable = MutableLiveData()
                }
                providerObservable!!.setValue(commonResponse)
            }
        })

    }

    fun getPatientDetails(providerId: Long?): LiveData<PatientDetail?>? {
        commonResponseMutableLiveData = MutableLiveData()

        if (providerId != null) {
            patientDetailsRetro(providerId)
        }
        return commonResponseMutableLiveData
    }
    fun inviteProviderBroadCast(
        providerId: Long?,
        token: String?,
        patientId: Long?,
    ): LiveData<CommonResponse?>? {
        providerInviteBroadcastObservable = MutableLiveData()
        providerId?.let { token?.let { it1 -> patientId?.let { it2 ->
            inviteBroadCast(it, it1,
                it2)
        } } }
        return providerInviteBroadcastObservable
    }


    private fun inviteBroadCast(providerId: Long, token: String, patientId: Long) {

        // Parsing the values in body
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = providerId.toString()
        bodyValues["token"] = token
        bodyValues["patientId"] = patientId.toString()

        val call: Call<CommonResponse?>? =
            ApiClient().getApiPatientEndpoints(true, true)?.inviteBroadCast(bodyValues)
        call?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>,
            ) {
                if (response.isSuccessful) {
//                    Log.i(TAG, "onResponse: SUCCESS");
                    if (providerInviteBroadcastObservable == null) {
                        providerInviteBroadcastObservable = MutableLiveData()
                    }
                    providerInviteBroadcastObservable!!.setValue(response.body())
                } else {
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (providerInviteBroadcastObservable == null) {
                        providerInviteBroadcastObservable = MutableLiveData()
                    }
                    providerInviteBroadcastObservable!!.setValue(commonResponse)
                }
            }

            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                val commonResponse = CommonResponse()
                commonResponse.setErrorMessage(Constants.API_ERROR)
                if (providerInviteBroadcastObservable == null) {
                    providerInviteBroadcastObservable = MutableLiveData()
                }
                providerInviteBroadcastObservable!!.setValue(commonResponse)
            }
        })


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