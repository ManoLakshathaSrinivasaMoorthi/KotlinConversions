package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model.CommonResponse
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model.VersionInfoResponse
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.LoginDetailsRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.LoginFailedRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.PhoneNumberBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class LoginViewModel: ViewModel() {
    private var providerObservable: MutableLiveData<CommonResponse?>? = null
    private var loginFailedObservable: MutableLiveData<CommonResponse?>? =
        null
    private var passwordObservable: MutableLiveData<CommonResponse?>? = null
    private var emailObservable: MutableLiveData<CommonResponse?>? =
        null
    private var versionInfoObservable: MutableLiveData<VersionInfoResponse?>? = null

    private val TAG = "LoginViewModel"

    fun login(
        email: String,
        password: String,
        fcm: String,
        override: Boolean,
        version: String, ): LiveData<CommonResponse?>? {
        providerObservable = MutableLiveData<CommonResponse?>()
        doLoginRetro(email, password, fcm, override, version)
        return providerObservable
    }

    fun getVersionInfo(): LiveData<VersionInfoResponse?>? {
        versionInfoObservable = MutableLiveData<VersionInfoResponse?>()
        getVersionInfo(Constants.OsType.ANDROID.toString())
        return versionInfoObservable
    }

    fun getEmail(phone: String): LiveData<CommonResponse?>? {
        emailObservable = MutableLiveData<CommonResponse?>()

        getEmailApiRetro(phone)
        return emailObservable
    }

    fun loginFailed(
        email: String,
        password: String,
    ): LiveData<CommonResponse?>? {
        loginFailedObservable =
            MutableLiveData<CommonResponse?>()

        doLoginFailedRetro(email, password)
        return loginFailedObservable
    }

    fun checkPassword(email: String, password: String, token: String): LiveData<CommonResponse?>? {
        passwordObservable = MutableLiveData<CommonResponse?>()
        checkPass(email, password, token)
        return passwordObservable
    }

    private fun doLoginRetro(
        email: String,
        password: String,
        fcm: String,
        override: Boolean,
        version: String ) {

        //sending body through data class
        val requestBody = LoginDetailsRequestBody("", fcm, Constants.OsType.ANDROID.toString(), email, password, override.toString())
        val errMsg = arrayOf("")
        ApiClient().getApi(true, true).doLogin(requestBody)
            .enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>, ) {
                    if (response.isSuccessful()) {
                        val commonResponse: CommonResponse? = response.body()

                        if (providerObservable == null) {
                            providerObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                    else {
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
                            if (providerObservable == null) {
                                providerObservable = MutableLiveData<CommonResponse?>()
                            }
                            providerObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {

                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (providerObservable == null) {
                            providerObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerObservable!!.setValue(commonResponse)
                    }
                }
            })
    }



    private fun doLoginFailedRetro(email: String, password: String) {

        val requestBody = LoginFailedRequestBody()
        requestBody.setEmail(email)
        requestBody.setPassword(password)
        requestBody.setOsType(Constants.OsType.ANDROID.toString())
        val errMsg = arrayOfNulls<String>(1)
        ApiClient().getApiUserEndpoints(true, true).loginFailed(requestBody)
            .enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>, ) {

                    if (response.isSuccessful()) {
                        val commonResponse: CommonResponse? =
                            response.body()

                        if (loginFailedObservable == null) {
                            loginFailedObservable =
                                MutableLiveData<CommonResponse?>()
                        }
                        loginFailedObservable!!.setValue(commonResponse)
                    } else {
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
                            if (loginFailedObservable == null) {
                                loginFailedObservable =
                                    MutableLiveData<CommonResponse?>()
                            }
                            loginFailedObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CommonResponse?>, t: Throwable, ) {

                    errMsg[0] = Constants.APIErrorType.Exception.toString()
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (loginFailedObservable == null) {
                            loginFailedObservable =
                                MutableLiveData<CommonResponse?>()
                        }
                        loginFailedObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val finalErrMsg = errMsg[0]
            Handler(Looper.getMainLooper()).post {
                val commonResponse = CommonResponse()
                commonResponse.setErrorMessage(finalErrMsg)
                if (providerObservable == null) {
                    providerObservable = MutableLiveData<CommonResponse?>()
                }
                providerObservable!!.setValue(commonResponse)
            }
        }
    }



    private fun getEmailApiRetro(phone: String) {

        val errMsg = arrayOf("")
        ApiClient().getApiUserEndpoints(true, true).getEmailByPhoneNumber(PhoneNumberBody(phone))
            .enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>,
                ) {

                    if (response.isSuccessful()) {
                        val commonResponse: CommonResponse? =
                            response.body()
                        if (emailObservable == null) {
                            emailObservable =
                                MutableLiveData<CommonResponse?>()
                        }
                        emailObservable!!.setValue(commonResponse)
                    } else {
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
                            if (emailObservable == null) {
                                emailObservable =
                                    MutableLiveData<CommonResponse?>()
                            }
                            emailObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable, ) {
                    errMsg[0] = Constants.APIErrorType.Exception.toString()

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (emailObservable == null) {
                            emailObservable =
                                MutableLiveData<CommonResponse?>()
                        }
                        emailObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val commonResponse: CommonResponse =
                CommonResponse()
            commonResponse.setErrorMessage(errMsg[0])
            if (emailObservable == null) {
                emailObservable =
                    MutableLiveData<CommonResponse?>()
            }
            emailObservable!!.setValue(commonResponse)
        }
    }



    private fun checkPass(email: String, password: String, token: String) {
        val errMsg = arrayOf("")

        // Parsing the params as body n API request
        val bodyValues = HashMap<String, String>()
        bodyValues["email"] = email
        bodyValues["password"] = password
        bodyValues["token"] = token
        ApiClient().getApi(true, true).loginWithPassword(bodyValues).enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>, ) {
                    if (response.isSuccessful()) {

                        val commonResponse: CommonResponse? =
                            response.body()
                        if (passwordObservable == null) {
                            passwordObservable = MutableLiveData<CommonResponse?>()
                        }
                        passwordObservable!!.setValue(commonResponse)
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
                            if (passwordObservable == null) {
                                passwordObservable = MutableLiveData<CommonResponse?>()
                            }
                            passwordObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable, ) {
//                        Log.d("discharge", "onFailure: " + t.toString());
                    val finalErrMsg = errMsg[0]
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(finalErrMsg)
                        if (passwordObservable == null) {
                            passwordObservable = MutableLiveData<CommonResponse?>()
                        }
                        passwordObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val finalErrMsg = errMsg[0]
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.setErrorMessage(finalErrMsg)
                if (passwordObservable == null) {
                    passwordObservable = MutableLiveData<CommonResponse?>()
                }
                passwordObservable!!.setValue(commonResponse)
            }
        }
    }



    // Converted to retrofit - Get version info API
    private fun getVersionInfo(osType: String) {
        // Parsing the params as body
        val errMsg = arrayOfNulls<String>(1)
        val creds = HashMap<String, String>()
        creds["osType"] = osType

        ApiClient().getApiUserEndpoints(false, false).getVersionInfo()
            .enqueue(object : Callback<VersionInfoResponse?> {
                override fun onResponse(
                    call: Call<VersionInfoResponse?>, response: Response<VersionInfoResponse?>, ) {
                    if (response.isSuccessful()) {

                        val versionInfoRes:VersionInfoResponse? =
                            response.body()
                        if (versionInfoObservable == null) {
                            versionInfoObservable = MutableLiveData<VersionInfoResponse?>()
                        }
                        versionInfoObservable!!.setValue(versionInfoRes)
                    } else {

                        errMsg[0] = Constants.API_ERROR
                        Handler(Looper.getMainLooper()).post {
                            val versionInfoRes = VersionInfoResponse()
                            versionInfoRes.setErrorMessage(errMsg[0])
                            if (versionInfoObservable == null) {
                                versionInfoObservable = MutableLiveData<VersionInfoResponse?>()
                            }
                            versionInfoObservable!!.setValue(versionInfoRes)
                        }
                    }
                }

                override fun onFailure(call: Call<VersionInfoResponse?>, t: Throwable, ) {
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val versionInfoRes = VersionInfoResponse()
                        versionInfoRes.setErrorMessage(errMsg[0])
                        if (versionInfoObservable == null) {
                            versionInfoObservable = MutableLiveData<VersionInfoResponse?>()
                        }
                        versionInfoObservable!!.setValue(versionInfoRes)
                    }
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        providerObservable = null
        passwordObservable = null
    }

}
