package com.example.kotlinomnicure.viewmodel

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
import omnicurekotlin.example.com.patientsEndpoints.model.DischargePatientRequest
import omnicurekotlin.example.com.patientsEndpoints.model.PatientDetail
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.HashMap

class PatientDetailViewModel: ViewModel() {
    private val TAG = javaClass.simpleName
    private var commonResponseMutableLiveData: MutableLiveData<PatientDetail?>? = null
    private var memberListObservable: MutableLiveData<TeamsDetailListResponse?>? = null
    private var startSOSObservable: MutableLiveData<SOSResponse?>? = null
    private var patientDischargeObservable: MutableLiveData<CommonResponse?>? = null
    private var resetAcuityObservable: MutableLiveData<CommonResponse?>? = null
    private var dischargePatientResponseObservable: MutableLiveData<CommonResponse?>? = null
    fun getPatienDetails(providerId: Long): LiveData<PatientDetail?>? {
        commonResponseMutableLiveData = MutableLiveData<PatientDetail?>()
        patientDetailsRetro(providerId)
        return commonResponseMutableLiveData
    }

    fun getMemberList(patientId: Long, team: String): LiveData<TeamsDetailListResponse?>? {
        memberListObservable = MutableLiveData<TeamsDetailListResponse?>()
        getMembersRetro(patientId, team)
        return memberListObservable
    }

    fun startSOS(callerId: Long, token: String, patientId: Long): LiveData<SOSResponse?>? {
        startSOSObservable = MutableLiveData<SOSResponse?>()
        startSOSAPI(callerId, token, patientId)
        return startSOSObservable
    }

    fun dischargePatient(providerId: Long, token: String, patientId: Long, notes: String, ): LiveData<CommonResponse?>? {
        patientDischargeObservable = MutableLiveData<CommonResponse?>()
        discharge(providerId, token, patientId, notes)
        return patientDischargeObservable
    }

    fun resetAcuityValue(
        providerId: Long,
        token: String,
        patientId: Long,
        score: String,
    ): LiveData<CommonResponse?>? {
        resetAcuityObservable = MutableLiveData<CommonResponse?>()
        resetAcuity(providerId, token, patientId, score)
        return resetAcuityObservable
    }

    fun bspDischargePatient(dischargePatientRequest: DischargePatientRequest): MutableLiveData<CommonResponse?>? {
        dischargePatientResponseObservable = MutableLiveData<CommonResponse?>()

        doDischargePatientRetro(dischargePatientRequest)
        return dischargePatientResponseObservable
    }

    private fun getMembersRetro(patientId: Long, team: String) {
        val errMsg = arrayOfNulls<String>(1)

        ApiClient().getApiProviderEndpoints(true, true)?.teamDetailsByName(
            TeamDetailsByNameRequestBody(patientId, team))
            ?.enqueue(object : Callback<TeamsDetailListResponse?> {
                override fun onResponse(
                    call: Call<TeamsDetailListResponse?>,
                    response: Response<TeamsDetailListResponse?>,
                ) {
                    Log.d(TAG, "onResponse: " + response.code())
                    if (response.isSuccessful()) {
                        val providerListResponse: TeamsDetailListResponse? = response.body()
                        if (memberListObservable == null) {
                            memberListObservable = MutableLiveData<TeamsDetailListResponse?>()
                        }
                        memberListObservable!!.setValue(providerListResponse)
                    }
                }

                override fun onFailure(call: Call<TeamsDetailListResponse?>, t: Throwable) {

                    errMsg[0] = Constants.API_ERROR
                    val response = TeamsDetailListResponse()
                    response.setErrorMessage(errMsg[0])
                    if (memberListObservable == null) {
                        memberListObservable = MutableLiveData<TeamsDetailListResponse?>()
                    }
                    memberListObservable!!.setValue(response)
                }
            })
    }



    private fun startSOSAPI(callerId: Long, token: String, patientId: Long) {



        // Parsing the values in body
        val bodyValues = HashMap<String?, String?>()
        bodyValues["id"] = callerId.toString()
        bodyValues["token"] = token
        bodyValues["patientId"] = patientId.toString()


        val call: Call<SOSResponse?>? =
            ApiClient().getApiPatientEndpoints(true, true)?.startSOSAPI(bodyValues)

        call?.enqueue(object : Callback<SOSResponse?> {
            override fun onResponse(call: Call<SOSResponse?>, response: Response<SOSResponse?>) {
                if (response.code() == 200) {
                    if (startSOSObservable == null) {
                        startSOSObservable = MutableLiveData<SOSResponse?>()
                    }
                    startSOSObservable!!.setValue(response.body())
                } else {
                    val sosResponse = SOSResponse()
                    if (startSOSObservable == null) {
                        startSOSObservable = MutableLiveData<SOSResponse?>()
                    }
                    startSOSObservable!!.setValue(sosResponse)
                }
            }

            override fun onFailure(call: Call<SOSResponse?>, t: Throwable) {
                Handler(Looper.getMainLooper()).post {
                    val response = SOSResponse()
                    response.setErrorMessage(Constants.API_ERROR)
                    if (startSOSObservable == null) {
                        startSOSObservable = MutableLiveData<SOSResponse?>()
                    }
                    startSOSObservable!!.setValue(response)
                }
            }
        })


    }

    private fun doDischargePatientRetro(dischargePatientRequest: DischargePatientRequest) {
        val errMsg = arrayOfNulls<String>(1)

        ApiClient().getApiPatientEndpoints(true, true)?.doPatientDischarge(dischargePatientRequest)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    Log.d(TAG, "onResponse: doPatientDischarge " + response.code())
                    if (response.isSuccessful()) {
                        val commonResponse: CommonResponse? = response.body()
                        if (dischargePatientResponseObservable == null) {
                            dischargePatientResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        dischargePatientResponseObservable!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
//
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is IOException) errMsg[0] = Constants.API_ERROR
                    val response = CommonResponse()
                    response.setErrorMessage(errMsg[0])
                    if (dischargePatientResponseObservable == null) {
                        dischargePatientResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    dischargePatientResponseObservable!!.setValue(response)
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = CommonResponse()
            if (dischargePatientResponseObservable == null) {
                dischargePatientResponseObservable = MutableLiveData<CommonResponse?>()
            }
            dischargePatientResponseObservable!!.setValue(response)
        }
    }



    private fun patientDetailsRetro(providerId: Long) {
        val errMsg = arrayOfNulls<String>(1)

        //sending body through data class
        val requestBody = CommonPatientIdRequestBody(providerId)

        ApiClient().getApiPatientEndpoints(true, true)?.patientdetailsresponse(requestBody)
            ?.enqueue(object : Callback<PatientDetail?> {
                override fun onResponse(
                    call: Call<PatientDetail?>,
                    response: Response<PatientDetail?>,
                ) {
                    Log.d(TAG, "onResponse: " + response.code())
                    if (response.isSuccessful()) {
                        val patientDetail: PatientDetail? = response.body()
                        if (commonResponseMutableLiveData == null) {
                            commonResponseMutableLiveData = MutableLiveData<PatientDetail?>()
                        }
                        commonResponseMutableLiveData!!.setValue(patientDetail)
                    }
                }

                override fun onFailure(call: Call<PatientDetail?>, t: Throwable) {
//                Log.e(TAG, "onFailure: " + t.toString());
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is Exception) errMsg[0] = Constants.API_ERROR
                    val response = PatientDetail()
                    errMsg[0]?.let { response.setErrorMsg(it) }
                    if (commonResponseMutableLiveData == null) {
                        commonResponseMutableLiveData = MutableLiveData<PatientDetail?>()
                    }
                    commonResponseMutableLiveData!!.setValue(response)
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = PatientDetail()
            if (commonResponseMutableLiveData == null) {
                commonResponseMutableLiveData = MutableLiveData<PatientDetail?>()
            }
            commonResponseMutableLiveData!!.setValue(response)
        }
    }


    private fun resetAcuity(id: Long, token: String, patientId: Long, score: String) {
        val errMsg = ""
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = id.toString()
        bodyValues["token"] = token
        bodyValues["patientId"] = patientId.toString()
        bodyValues["score"] = score
        ApiClient().getApiPatientEndpoints(true, true)?.resetAcuityScoreApi(bodyValues)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    if (response.isSuccessful()) {
                        Log.d("reset Acuity", "onResponse: $response")
                        val commonResponse: CommonResponse? = response.body()
                        if (resetAcuityObservable == null) {
                            resetAcuityObservable = MutableLiveData<CommonResponse?>()
                        }
                        resetAcuityObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("reset Acuity", "onResponse: $response")
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: CommonResponse =
                                CommonResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (resetAcuityObservable == null) {
                                resetAcuityObservable = MutableLiveData<CommonResponse?>()
                            }
                            resetAcuityObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                    Log.d("reset Acuity", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (resetAcuityObservable == null) {
                            resetAcuityObservable = MutableLiveData<CommonResponse?>()
                        }
                        resetAcuityObservable!!.setValue(commonResponse)
                    }
                }
            })
    }



    private fun discharge(providerId: Long, token: String, patientId: Long, notes: String) {
        val errMsg = ""
        // Parsing the values in body
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = providerId.toString()
        bodyValues["token"] = token
        bodyValues["patientId"] = patientId.toString()
        bodyValues["message"] = notes


        ApiClient().getApiPatientEndpoints(true, true)?.dischargePatientApi(bodyValues)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    if (response.isSuccessful()) {
                        Log.d("discharge", "onResponse: $response")
                        val commonResponse: CommonResponse? =
                            response.body()
                        if (patientDischargeObservable == null) {
                            patientDischargeObservable = MutableLiveData<CommonResponse?>()
                        }
                        patientDischargeObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("discharge", "onResponse: $response")
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: CommonResponse =
                                CommonResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (patientDischargeObservable == null) {
                                patientDischargeObservable = MutableLiveData<CommonResponse?>()
                            }
                            patientDischargeObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable,
                ) {
                    Log.d("discharge", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (patientDischargeObservable == null) {
                            patientDischargeObservable = MutableLiveData<CommonResponse?>()
                        }
                        patientDischargeObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.setErrorMessage(errMsg)
                if (patientDischargeObservable == null) {
                    patientDischargeObservable = MutableLiveData<CommonResponse?>()
                }
                patientDischargeObservable!!.setValue(commonResponse)
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        commonResponseMutableLiveData = null
        startSOSObservable = null
        patientDischargeObservable = null
        resetAcuityObservable = null
        dischargePatientResponseObservable = null
    }
}