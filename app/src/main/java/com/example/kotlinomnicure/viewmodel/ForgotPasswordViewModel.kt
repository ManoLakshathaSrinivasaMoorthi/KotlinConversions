package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeListResponse
import omnicurekotlin.example.com.userEndpoints.model.ForgotPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

class ForgotPasswordViewModel:ViewModel() {

    private var commonResponseObservable: MutableLiveData<CommonResponse?>? = null
    private var countryListObservable: MutableLiveData<CountryCodeListResponse?>? = null
    private val TAG = "ForgotPasswordViewModel"

    fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): MutableLiveData<CommonResponse?>? {
        commonResponseObservable = MutableLiveData<CommonResponse?>()
        doForgotPassword(forgotPasswordRequest)
        return commonResponseObservable
    }

    fun getCountry(): LiveData<CountryCodeListResponse?>? {
        countryListObservable = MutableLiveData<CountryCodeListResponse?>()

        getCountryCodesRetro()
        return countryListObservable
    }


    private fun doForgotPassword(forgotPasswordRequest: ForgotPasswordRequest) {
        val call: Call<CommonResponse?>? =
            ApiClient().getApiUserEndpoints(encrypt = true, decrypt = true)?.doForgotPassword(forgotPasswordRequest)
        call?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>,
            ) {
                if (response.isSuccessful) {
                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    commonResponseObservable!!.setValue(response.body())
                } else {
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    commonResponseObservable!!.setValue(commonResponse)
                }
            }

            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                Handler(Looper.getMainLooper()).post {
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(Constants.API_ERROR)
                    if (commonResponseObservable == null) {
                        commonResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    commonResponseObservable!!.setValue(commonResponse)
                }
            }
        })


    }

    private fun getCountryCodesRetro() {
        val errMsg = arrayOfNulls<String>(1)
        ApiClient().getApiUserEndpoints(encrypt = true, decrypt = true)?.getCountryCodes()
            ?.enqueue(object : Callback<CountryCodeListResponse?> {
                override fun onResponse(
                    call: Call<CountryCodeListResponse?>,
                    response: Response<CountryCodeListResponse?>,
                ) {

                    if (response.isSuccessful) {
                        val countryCodeListResponse: CountryCodeListResponse? = response.body()
                        if (countryListObservable == null) {
                            countryListObservable = MutableLiveData<CountryCodeListResponse?>()
                        }
                        countryListObservable!!.setValue(countryCodeListResponse)
                    }
                }

                override fun onFailure(call: Call<CountryCodeListResponse?>, t: Throwable) {
//                Log.e(TAG, "onFailure: "+t.toString());
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val response = CountryCodeListResponse()
                        response.setErrorMessage(Constants.API_ERROR)
                        if (countryListObservable == null) {
                            countryListObservable = MutableLiveData<CountryCodeListResponse?>()
                        }
                        countryListObservable!!.setValue(response)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            Handler(Looper.getMainLooper()).post {
                val response = CountryCodeListResponse()
                if (countryListObservable == null) {
                    countryListObservable = MutableLiveData<CountryCodeListResponse?>()
                }
                countryListObservable!!.setValue(response)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        commonResponseObservable = null
    }
}