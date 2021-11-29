package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import omnicurekotlin.example.com.patientsEndpoints.PatientRetrofit
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import omnicurekotlin.example.com.patientsEndpoints.model.DischargePatientRequest
import omnicurekotlin.example.com.patientsEndpoints.model.PatientHistoryResponse
import omnicurekotlin.example.com.providerEndpoints.model.GroupCall
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivityViewModel: ViewModel() {
    private var patientDischargeObservable: MutableLiveData<CommonResponse>? = null
    private var providerInviteObservable: MutableLiveData<CommonResponse>? = null
    private var providerInviteBroadcastObservable: MutableLiveData<CommonResponse>? = null
    private var startCallObservable: MutableLiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>? = null
    private var startSOSObservable: MutableLiveData<CommonResponse>? = null
    private var chatHistoryObservale: MutableLiveData<PatientHistoryResponse>? = null
    private var dischargePatientResponseObservable: MutableLiveData<CommonResponse>? = null
    private var providerObservable: MutableLiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>? = null
    private var memberListObservable: MutableLiveData<TeamsDetailListResponse>? = null
    private var commonResponseMutableLiveData: MutableLiveData<CommonResponse>? = null
    private lateinit var retService: RetrofitService

    fun getPatientDetails(providerId: Long): LiveData<CommonResponse>? {
        commonResponseMutableLiveData = MutableLiveData<CommonResponse>()
        patientDetails(providerId)
        return commonResponseMutableLiveData
    }

    fun bspDischargePatient(dischargePatientRequest: DischargePatientRequest): MutableLiveData<CommonResponse>? {
        dischargePatientResponseObservable = MutableLiveData<CommonResponse>()
        doDischargePatient(dischargePatientRequest)
        return dischargePatientResponseObservable
    }

    fun getMemberList(patientId: Long, team: String): LiveData<TeamsDetailListResponse>? {
        memberListObservable = MutableLiveData<TeamsDetailListResponse>()
        getMembers(patientId, team)
        return memberListObservable
    }


    fun dischargePatient(
        providerId: Long,
        token: String,
        patientId: Long,
        notes: String
    ): LiveData<CommonResponse>? {
        patientDischargeObservable = MutableLiveData<CommonResponse>()
        discharge(providerId, token, patientId, notes)
        return patientDischargeObservable
    }

    fun inviteProvider(providerId: Long, token: String, inviteProviderId: Long, patientId: Long): LiveData<CommonResponse>? {
        providerInviteObservable = MutableLiveData<CommonResponse>()
        invite(providerId, token, inviteProviderId, patientId)
        return providerInviteObservable
    }

    fun inviteProviderBroadCast(providerId: Long, token: String, patientId: Long): LiveData<Response<CommonResponse>> =  liveData  {
        retService = PatientRetrofit.getRetrofits()
                .create(RetrofitService::class.java)
        providerInviteBroadcastObservable = MutableLiveData<CommonResponse>()

        val response = retService.getInviteBroadcastApi(providerId,token,patientId)
        emit(response)
        return@liveData
    }

    fun startCall(callerId: Long, token: String, receiverId: Long, patientId: Long, channel: String, type: String): LiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>? {
        startCallObservable =
            MutableLiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>()
        startCallAPI(callerId, token, receiverId, patientId, channel, type)
        return startCallObservable
    }

    fun startSOS(
        callerId: Long,
        token: String,
        patientId: Long
    ): LiveData<CommonResponse>? {
        startSOSObservable =
            MutableLiveData<CommonResponse>()
        startSOSAPI(callerId, token, patientId)
        return startSOSObservable
    }

    fun getPatientChatHistory(
        providerId: Long,
        token: String,
        patientId: Long
    ): LiveData<PatientHistoryResponse>? {
        chatHistoryObservale = MutableLiveData<PatientHistoryResponse>()
        getPatientHistory(providerId, token, patientId)
        return chatHistoryObservale
    }

    fun multipleCall(content: GroupCall): LiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>? {
        providerObservable =
            MutableLiveData<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>()
        Multiplecall(content)
        return providerObservable
    }

    private fun Multiplecall(content: GroupCall) {

    }

    private fun getMembers(patientId: Long, teamName: String) {

    }

    private fun discharge(providerId: Long, token: String, patientId: Long, notes: String) {

    }

    private fun invite(providerId: Long, token: String, inviteProviderId: Long, patientId: Long) {

    }




    private fun startCallAPI(callerId: Long, token: String, receiverId: Long, patientId: Long, channel: String, type: String) {

    }


    private fun startSOSAPI(callerId: Long, token: String, patientId: Long) {

    }

    private fun getPatientHistory(providerId: Long, token: String, patientId: Long) {

    }

    private fun patientDetails(uid: Long) {

    }

    private fun doDischargePatient(dischargePatientRequest: DischargePatientRequest) {

    }


    override fun onCleared() {
        super.onCleared()
        patientDischargeObservable = null
        providerInviteObservable = null
        providerInviteBroadcastObservable = null
        startCallObservable = null
        startSOSObservable = null
        chatHistoryObservale = null
        dischargePatientResponseObservable = null
        memberListObservable = null
        commonResponseMutableLiveData = null
    }
}
