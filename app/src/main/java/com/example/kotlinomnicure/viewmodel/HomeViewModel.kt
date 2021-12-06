package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.GetProviderByIdRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.GetProviderListRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.ProviderNotificationDetailsRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.SendMessageRequestBody

import omnicurekotlin.example.com.hospitalEndpoints.model.HospitalListResponse

import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.apiRetrofit.RequestBodys.CommonIdRequestBody
import omnicurekotlin.example.com.healthcareEndPoints.model.AddNotificationDataRequest
import omnicurekotlin.example.com.healthcareEndPoints.model.CommonResponseProviderNotification
import omnicurekotlin.example.com.healthcareEndPoints.model.ProviderNotificationResponse
import omnicurekotlin.example.com.providerEndpoints.HandOffAcceptRequest
import omnicurekotlin.example.com.providerEndpoints.model.*
import omnicurekotlin.example.com.userEndpoints.model.VersionInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.HashMap

class HomeViewModel : ViewModel() {
   private val TAG = "HomeViewModel"

    private var providerListObservable: MutableLiveData<ProviderListResponse>? = null
    private var hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private var providerNotificationListObservable: MutableLiveData<ProviderNotificationResponse>? =
        null
    private var updateProviderObservable: MutableLiveData<CommonResponse>? = null
    private var handOffAcceptObservable: MutableLiveData<CommonResponse>? = null
    private var addOrUpdateObservable: MutableLiveData<CommonResponseProviderNotification>? = null

    private var passwordObservable: MutableLiveData<omnicurekotlin.example.com.userEndpoints.model.CommonResponse>? =
        null
    private var acceptInviteObservable: MutableLiveData<CommonResponse>? =
        null
    private var startCallObservable: MutableLiveData<CommonResponse>? =
        null
    private var resetAcuityObservable: MutableLiveData<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse >? =
        null
    private var providerObservable: MutableLiveData<CommonResponse>? =
        null
    private var hospitalObservable: MutableLiveData<CommonResponse>? =
        null
    private var versionInfoObservable: MutableLiveData<VersionInfoResponse>? = null

    fun getVersionInfo(toString: String): LiveData<VersionInfoResponse?>? {
        versionInfoObservable = MutableLiveData()
       getVersionInfoRetro(Constants.OsType.ANDROID.toString())

        return versionInfoObservable
    }

    fun checkPassword(
        email: String?,
        password: String?,
        token: String?,
    ): MutableLiveData<omnicurekotlin.example.com.userEndpoints.model.CommonResponse>? {
        passwordObservable = MutableLiveData()
        if (email != null) {
            password?.let {
                if (token != null) {
                    checkPass(email, it, token)
                }
            }
        }
        return passwordObservable
    }

    private fun checkPass(email: String, password: String, token: String) {
        val errMsg = ""
        val bodyValues = HashMap<String, String>()
        bodyValues["email"] = email
        bodyValues["password"] = password
        bodyValues["token"] = token
        ApiClient().getApi(true, true)?.loginWithPassword(bodyValues)?.
        enqueue(object : Callback<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?> {
                override fun onResponse(
                    call: Call<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?>,
                    response: Response<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?>,
                ) {
                    if (response.isSuccessful()) {
                        Log.d("discharge", "onResponse: $response")
                        val commonResponse: omnicurekotlin.example.com.userEndpoints.model.CommonResponse? =
                            response.body()
                        if (passwordObservable == null) {
                            passwordObservable = MutableLiveData()
                        }
                        passwordObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("discharge", "onResponse: $response")
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: omnicurekotlin.example.com.userEndpoints.model.CommonResponse =
                                omnicurekotlin.example.com.userEndpoints.model.CommonResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (passwordObservable == null) {
                                passwordObservable =
                                    MutableLiveData()
                            }
                            passwordObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<omnicurekotlin.example.com.userEndpoints.model.CommonResponse?>,
                    t: Throwable,
                ) {
                    Log.d("discharge", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicurekotlin.example.com.userEndpoints.model.CommonResponse =
                            omnicurekotlin.example.com.userEndpoints.model.CommonResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (passwordObservable == null) {
                            passwordObservable =
                                MutableLiveData()
                        }
                        passwordObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: omnicurekotlin.example.com.userEndpoints.model.CommonResponse =
                    omnicurekotlin.example.com.userEndpoints.model.CommonResponse()
                commonResponse.setErrorMessage(errMsg)
                if (passwordObservable == null) {
                    passwordObservable =
                        MutableLiveData()
                }
                passwordObservable!!.setValue(commonResponse)
            }
        }
    }

    // Convert to retofit
    private fun getVersionInfoRetro(osType: String) {
        // Parsing the params as body
        val errMsg = arrayOfNulls<String>(1)
        val creds = HashMap<String, String>()
        creds["osType"] = osType
        //        ApiClient.getApiUserEndpoints(false, false).getVersionInfo().enqueue(new Callback<omnicure.mvp.com.userEndpoints.model.VersionInfoResponse>() {
        ApiClient().getApiUserEndpoints(false, false)?.getVersionInfo()
            ?.enqueue(object : Callback<VersionInfoResponse?> {
                override fun onResponse(
                    call: Call<VersionInfoResponse?>,
                    response: Response<VersionInfoResponse?>,
                ) {
                    if (response.isSuccessful()) {
                        Log.d("loginTags", "onResponse: " + response.code())
                        val versionInfoRes: VersionInfoResponse? =
                            response.body()
                        if (versionInfoObservable == null) {
                            versionInfoObservable = MutableLiveData()
                        }
                        versionInfoObservable!!.setValue(versionInfoRes)
                    } else {
                        Log.d("loginTags", "onResponse: " + response.code())
                        errMsg[0] = Constants.API_ERROR
                        Handler(Looper.getMainLooper()).post {
                            val versionInfoRes = VersionInfoResponse()
                            versionInfoRes.setErrorMessage(errMsg[0])
                            if (versionInfoObservable == null) {
                                versionInfoObservable = MutableLiveData()
                            }
                            versionInfoObservable!!.setValue(versionInfoRes)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<VersionInfoResponse?>,
                    t: Throwable,
                ) {
//                Log.e("loginTags", "onFailure: "+t.toString());
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val versionInfoRes = VersionInfoResponse()
                        versionInfoRes.setErrorMessage(errMsg[0])
                        if (versionInfoObservable == null) {
                            versionInfoObservable = MutableLiveData()
                        }
                        versionInfoObservable!!.setValue(versionInfoRes)
                    }
                }
            })
    }


    fun getProviderList(
        providerId: Long,
        token: String,
        role: String,
    ): LiveData<ProviderListResponse?>? {
        providerListObservable = MutableLiveData()
        getProviders(providerId, token, role)
        return providerListObservable
    }

    fun getHospitalList(userid: Long): LiveData<HospitalListResponse?>? {
        hospitalListObservable = MutableLiveData()
        //        getHospitalApi(userid);
        getHospitalApiRetro(userid)
        return hospitalListObservable
    }

    fun updateProviderStatus(provider: Provider): LiveData<CommonResponse?>? {
        updateProviderObservable = MutableLiveData()
        updateStatus(provider)
        return updateProviderObservable
    }


    /**
     * Add/ update provider notification
     *
     * @param addNotificationDataRequest
     * @return
     */
    fun addOrUpdateProviderNotification(addNotificationDataRequest: AddNotificationDataRequest): LiveData<CommonResponseProviderNotification?>? {
        addOrUpdateObservable = MutableLiveData()
        addOrUpdateNotificationDetails(addNotificationDataRequest)
        return addOrUpdateObservable
    }

    /**
     * Get provider notification details
     *
     * @param providerId
     * @return
     */
    fun getProviderNotificationDetails(providerId: Long): LiveData<ProviderNotificationResponse?>? {
        providerNotificationListObservable = MutableLiveData()
        getProviderNotificationDetailsApi(providerId)
        return providerNotificationListObservable
    }




    fun getHospitalById(hospitalId: String): LiveData<CommonResponse?>? {
        hospitalObservable = MutableLiveData()
        getHospitalByIdApi(hospitalId)
        return hospitalObservable
    }

    fun getProviderById(id: Long, token: String, providerId: Long): LiveData<CommonResponse?>? {
        providerObservable = MutableLiveData()
        getProvider(id, token, providerId)
        return providerObservable
    }

    fun acceptInvite(
        providerId: Long,
        token: String,
        patientId: Long,
    ): LiveData<CommonResponse?>? {
        acceptInviteObservable = MutableLiveData()
       // acceptInviteCall(providerId, token, patientId)
        return acceptInviteObservable
    }

    fun acceptRemoteHandoff(handOffAcceptRequest: HandOffAcceptRequest): LiveData<CommonResponse?>? {
        handOffAcceptObservable = MutableLiveData()
        RemoteHandOffAccept(handOffAcceptRequest)
        return handOffAcceptObservable
    }

    fun startCall(
        callerId: Long,
        token: String,
        receiverId: Long,
        patientId: Long,
        callType: String,
    ): LiveData<CommonResponse?>? {
        startCallObservable = MutableLiveData()
        startCallAPI(callerId, token, receiverId, patientId, callType)
        return startCallObservable
    }

    fun resetAcuityScore(providerId: Long, token: String, patientId: Long): LiveData<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse ?>? {
        resetAcuityObservable = MutableLiveData()
        resetAcuity(providerId, token, patientId)
        return resetAcuityObservable
    }

    private fun resetAcuity(providerId: Long, token: String, patientId: Long) {
        val errMsg = ""
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = providerId.toString()
        bodyValues["token"] = token
        bodyValues["patientId"] = patientId.toString()
        ApiClient().getApiPatientEndpoints(true, true)?.resetAcuityApi(bodyValues)
           ?.enqueue(object : Callback<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse ?> {
                override fun onResponse(
                    call: Call<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse ?>,
                    response: Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse ?>,
                ) {
                    if (response.isSuccessful()) {
                        Log.d("reset Acuity", "onResponse: $response")
                        val commonResponse: omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse? =
                            response.body()
                        if (resetAcuityObservable == null) {
                            resetAcuityObservable = MutableLiveData()
                        }
                        resetAcuityObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("reset Acuity", "onResponse: $response")
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse =
                                omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (resetAcuityObservable == null) {
                                resetAcuityObservable =
                                    MutableLiveData()
                            }
                            resetAcuityObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse ?>,
                    t: Throwable,
                ) {
                    Log.d("reset Acuity", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse =
                            omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (resetAcuityObservable == null) {
                            resetAcuityObservable =
                                MutableLiveData()
                        }
                        resetAcuityObservable!!.setValue(commonResponse)
                    }
                }
            })

        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse:omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse  =
                    omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse()
                commonResponse.setErrorMessage(errMsg)
                if (resetAcuityObservable == null) {
                    resetAcuityObservable =
                        MutableLiveData()
                }
                resetAcuityObservable!!.setValue(commonResponse)
            }
        }
    }

    private fun getProvider(id: Long, token: String, providerId: Long) {
        val errMsg = ""

        val url = "providerEndpoints/v1/getProviderById"
        ApiClient().getApiProviderEndpoints(true, true)
            ?.getProviderById(url, GetProviderByIdRequestBody(providerId, token, id))
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    if (response.isSuccessful) {

                        val commonResponse: CommonResponse? =
                            response.body()
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData()
                        }
                        providerObservable!!.setValue(commonResponse)
                    } else {

                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable,
                ) {

                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse:CommonResponse =
                    CommonResponse()
                commonResponse.errorMessage
                if (providerObservable == null) {
                    providerObservable =
                        MutableLiveData()
                }
                providerObservable!!.setValue(commonResponse)
            }
        }
    }



    private fun getHospitalByIdApi(hospitalId: String) {
        val errMsg = ""
        val bodyValues = HashMap<String, String>()
        bodyValues["hospitalId"] = hospitalId
        ApiClient().getApiHospital(true, true)?.getHospitalById(bodyValues)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    if (response.isSuccessful()) {

                        val commonResponse: CommonResponse? =
                            response.body()
                        if (hospitalObservable == null) {
                            hospitalObservable = MutableLiveData()
                        }
                        hospitalObservable!!.setValue(commonResponse)
                    } else {

                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable,
                ) {

                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.errorMessage
                if (hospitalObservable == null) {
                    hospitalObservable =
                        MutableLiveData()
                }
                hospitalObservable!!.setValue(commonResponse)
            }
        }
    }



    private fun getHospitalApiRetro(userid: Long) {
        val errMsg = arrayOfNulls<String>(1)



        //sending body through data class
        val requestBody = CommonIdRequestBody(userid)

        ApiClient().getApiHospital(true, true)?.hospitallistresponse(requestBody)
            ?.enqueue(object : Callback<HospitalListResponse?> {
                override fun onResponse(
                    call: Call<HospitalListResponse?>,
                    response: Response<HospitalListResponse?>,
                ) {

                    if (response.isSuccessful) {
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData()
                        }
                        hospitalListObservable!!.setValue(response.body())
                    }
                }

                override fun onFailure(call: Call<HospitalListResponse?>, t: Throwable) {

                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is Exception) errMsg[0] = Constants.API_ERROR
                    val response = HospitalListResponse()
                    response.setErrorMessage(errMsg[0])
                    if (hospitalListObservable == null) {
                        hospitalListObservable = MutableLiveData()
                    }
                    hospitalListObservable!!.setValue(response)
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = HospitalListResponse()
            response.setErrorMessage(errMsg[0])
            if (hospitalListObservable == null) {
                hospitalListObservable = MutableLiveData()
            }
            hospitalListObservable!!.setValue(response)
        }
    }



    private fun getProviders(providerId: Long, token: String, role: String) {
        val errMsg = ""

        val url = "providerEndpoints/v1/getRemoteProviderRemoteDirectory"
        ApiClient().getApiProviderEndpoints(true, true)
            ?.getProviderList(url, GetProviderListRequestBody(role, token, providerId))
            ?.enqueue(object : Callback<ProviderListResponse?> {
                override fun onResponse(
                    call: Call<ProviderListResponse?>,
                    response: Response<ProviderListResponse?>,
                ) {
                    if (response.isSuccessful) {

                        val commonResponse = response.body()
                        if (providerListObservable == null) {
                            providerListObservable = MutableLiveData()
                        }
                        providerListObservable!!.setValue(commonResponse)
                    } else {

                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: ProviderListResponse =
                                ProviderListResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (providerListObservable == null) {
                                providerListObservable = MutableLiveData()
                            }
                            providerListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ProviderListResponse?>,
                    t: Throwable,
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: ProviderListResponse =
                            ProviderListResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (providerListObservable == null) {
                            providerListObservable = MutableLiveData()
                        }
                        providerListObservable!!.setValue(commonResponse)
                    }
                }
            })


        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: ProviderListResponse =
                    ProviderListResponse()
                commonResponse.setErrorMessage(errMsg)
                if (providerListObservable == null) {
                    providerListObservable = MutableLiveData()
                }
                providerListObservable!!.setValue(commonResponse)
            }
        }
    }



    private fun updateStatus(provider: Provider) {
        val errMsg = ""
        val url = "providerEndpoints/v1/updateProvider"

        ApiClient().getApiProviderEndpoints(true, true)
            ?.updateStatus(provider)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    if (response.isSuccessful()) {

                        val commonResponse: CommonResponse? =
                            response.body()
                        if (updateProviderObservable == null) {
                            updateProviderObservable = MutableLiveData()
                        }
                        updateProviderObservable!!.setValue(commonResponse)
                    } else {

                        Handler(Looper.getMainLooper()).post {
                            val commonResponse:CommonResponse =
                                CommonResponse()
                            commonResponse.errorMessage
                            if (updateProviderObservable == null) {
                                updateProviderObservable = MutableLiveData()
                            }
                            updateProviderObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable,
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.errorMessage
                        if (updateProviderObservable == null) {
                            updateProviderObservable = MutableLiveData()
                        }
                        updateProviderObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.errorMessage
                if (updateProviderObservable == null) {
                    updateProviderObservable = MutableLiveData()
                }
                updateProviderObservable!!.setValue(commonResponse)
            }
        }
    }


    /**
     * Add / Update notificaition Details
     *
     * @param addNotificationDataRequest
     */
    private fun addOrUpdateNotificationDetails(addNotificationDataRequest: AddNotificationDataRequest) {
        val errMsg = ""
        ApiClient().getApi(true, true)?.addOrUpdateNotificationSettings(addNotificationDataRequest)
           ?.enqueue(object : Callback<CommonResponseProviderNotification?> {
                override fun onResponse(
                    call: Call<CommonResponseProviderNotification?>,
                    response: Response<CommonResponseProviderNotification?>,
                ) {
                    if (response.isSuccessful) {

                        val commonResponse = response.body()
                        if (addOrUpdateObservable == null) {
                            addOrUpdateObservable = MutableLiveData()
                        }
                        addOrUpdateObservable!!.setValue(commonResponse)
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse =
                                CommonResponseProviderNotification()
                            commonResponse.errorMessage
                            if (addOrUpdateObservable == null) {
                                addOrUpdateObservable = MutableLiveData()
                            }
                            addOrUpdateObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponseProviderNotification?>,
                    t: Throwable,
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse =
                            CommonResponseProviderNotification()
                        commonResponse.errorMessage
                        if (addOrUpdateObservable == null) {
                            addOrUpdateObservable = MutableLiveData()
                        }
                        addOrUpdateObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse =
                    CommonResponseProviderNotification()
                commonResponse.errorMessage
                if (addOrUpdateObservable == null) {
                    addOrUpdateObservable = MutableLiveData()
                }
                addOrUpdateObservable!!.setValue(commonResponse)
            }
        }
    }



    /**
     * Getting provider notification details
     *
     * @param providerId
     */
    private fun getProviderNotificationDetailsApi(providerId: Long) {


        //sending body through data class
        val requestBody = ProviderNotificationDetailsRequestBody(providerId)


        val call: Call<ProviderNotificationResponse?>? =
            ApiClient().getApi(true, true)?.getProviderNotificationDetailsApi(requestBody)
        call?.enqueue(object : Callback<ProviderNotificationResponse?> {
            override fun onResponse(
                call: Call<ProviderNotificationResponse?>,
                response: Response<ProviderNotificationResponse?>,
            ) {

                if (response.isSuccessful) {
                    if (providerNotificationListObservable == null) {
                        providerNotificationListObservable = MutableLiveData()
                    }
                    providerNotificationListObservable!!.setValue(response.body())
                } else {
                    val providerNotificationResponse = ProviderNotificationResponse()
                    providerNotificationResponse.errorMessage = Constants.API_ERROR
                    if (providerNotificationListObservable == null) {
                        providerNotificationListObservable = MutableLiveData()
                    }
                    providerNotificationListObservable!!.setValue(providerNotificationResponse)
                }
            }

            override fun onFailure(call: Call<ProviderNotificationResponse?>, t: Throwable) {
                val response = ProviderNotificationResponse()
                response.errorMessage = Constants.API_ERROR
                if (providerNotificationListObservable == null) {
                    providerNotificationListObservable = MutableLiveData()
                }
                providerNotificationListObservable!!.setValue(response)
            }
        })

    }


   /* private fun acceptInviteCall(providerId: Long, token: String, patientId: Long) {
        val errMsg = ""
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = providerId.toString()
        bodyValues["token"] = token
        bodyValues["patientId"] = patientId.toString()
        ApiClient().getApiPatientEndpoints(true, true)
            ?.acceptInvite(bodyValues)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful()) {

                        val commonResponse: CommonResponse? =
                            response.body()
                        if (acceptInviteObservable == null) {
                            acceptInviteObservable = MutableLiveData()
                        }
                        acceptInviteObservable!!.setValue(commonResponse)
                    } else {

                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: CommonResponse =
                                CommonResponse()
                            commonResponse.errorMessage
                            if (acceptInviteObservable == null) {
                                acceptInviteObservable =
                                    MutableLiveData()
                            }
                            acceptInviteObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.errorMessage
                        if (acceptInviteObservable == null) {
                            acceptInviteObservable =
                                MutableLiveData()
                        }
                        acceptInviteObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.errorMessage
                if (acceptInviteObservable == null) {
                    acceptInviteObservable =
                        MutableLiveData()
                }
                acceptInviteObservable!!.setValue(commonResponse)
            }
        }
    }*/



    private fun RemoteHandOffAccept(handOffAcceptRequest: HandOffAcceptRequest) {
        val errMsg = ""
        ApiClient().getApiProviderEndpoints(true, true)?.doRDHandOff(handOffAcceptRequest)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    if (response.isSuccessful) {

                        val commonResponse: CommonResponse? =
                            response.body()
                        if (handOffAcceptObservable == null) {
                            handOffAcceptObservable = MutableLiveData()
                        }
                        handOffAcceptObservable!!.setValue(commonResponse)
                    } else {

                        Handler(Looper.getMainLooper()).post {
                            val commonResponse  = CommonResponse()
                            commonResponse.errorMessage
                            if (handOffAcceptObservable == null) {
                                handOffAcceptObservable = MutableLiveData()
                            }
                            handOffAcceptObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>, t: Throwable,
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.errorMessage
                        if (handOffAcceptObservable == null) {
                            handOffAcceptObservable = MutableLiveData()
                        }
                        handOffAcceptObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.errorMessage
                if (handOffAcceptObservable == null) {
                    handOffAcceptObservable = MutableLiveData()
                }
                handOffAcceptObservable!!.setValue(commonResponse)
            }
        }
    }

    private fun startCallAPI(callerId: Long, token: String, receiverId: Long, patientId: Long, callType: String) {
        val errMsg = ""
        val url = "providerEndpoints/v1/sendMessage"
        ApiClient().getApiProviderEndpoints(true, true)?.sendMessage(
            url, SendMessageRequestBody(
                callType,
                "$callerId-$receiverId",
                receiverId,
                token,
                callerId
            )
        )
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {
                    if (response.isSuccessful) {

                        val commonResponse: CommonResponse? =
                            response.body()
                        if (startCallObservable == null) {
                            startCallObservable = MutableLiveData()
                        }
                        startCallObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("Verifytags", "onResponse: " + response.code())
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: CommonResponse =
                                CommonResponse()
                            commonResponse.errorMessage
                            if (startCallObservable == null) {
                                startCallObservable =
                                    MutableLiveData()
                            }
                            startCallObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>,
                    t: Throwable,
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.errorMessage
                        if (startCallObservable == null) {
                            startCallObservable =
                                MutableLiveData()
                        }
                        startCallObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.errorMessage
                if (startCallObservable == null) {
                    startCallObservable =
                        MutableLiveData()
                }
                startCallObservable!!.setValue(commonResponse)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        providerListObservable = null
        updateProviderObservable = null
        acceptInviteObservable = null
        handOffAcceptObservable = null
        startCallObservable = null
        resetAcuityObservable = null
    }
}