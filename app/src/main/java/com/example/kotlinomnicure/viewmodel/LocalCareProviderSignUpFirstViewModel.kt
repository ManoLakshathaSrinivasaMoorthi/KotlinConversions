package com.mvp.omnicure.kotlinactivity.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.utils.Constants
import com.google.gson.Gson

import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.userEndpoints.model.CountryCodeListResponse
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocalCareProviderSignUpFirstViewModel:ViewModel() {
    private val hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private var countryListObservable: MutableLiveData<CountryCodeListResponse?>? = null


    fun getCountry(): LiveData<CountryCodeListResponse?>? {
        countryListObservable = MutableLiveData()
        getCountryCodesRetro()
        return countryListObservable
    }



    private fun getCountryCodesRetro() {
        val errMsg = arrayOfNulls<String>(1)

        ApiClient().getApiUserEndpoints(true, true)?.getCountryCodes()?.enqueue(object :
            Callback<CountryCodeListResponse?> {
            override fun onResponse(
                call: Call<CountryCodeListResponse?>,
                response: Response<CountryCodeListResponse?>
            ) {
                Log.d("getCountryCodesRetro", "onResponse: " + response.code())
                if (response.isSuccessful) {
                    val countryCodeListResponse = response.body()

                    Log.d("", "countryCodes " + (countryCodeListResponse!!.countryCodeResponseList?.get(0) ))
                    Log.d("getCountryCodesRetro", "onResponse: " + Gson().toJson(countryCodeListResponse))
                    if (countryListObservable == null) {
                        countryListObservable = MutableLiveData()
                    }
                    countryListObservable!!.value = countryCodeListResponse
                }
            }

            override fun onFailure(call: Call<CountryCodeListResponse?>, t: Throwable) {
                Log.d("", "onFailure: $t")
                errMsg[0] = Constants.API_ERROR
                Handler(Looper.getMainLooper()).post {
                    val response =
                        CountryCodeListResponse()
                    response.errorMessage = errMsg[0]
                    if (countryListObservable == null) {
                        countryListObservable =
                            MutableLiveData()
                    }
                    countryListObservable!!.setValue(response)
                }
            }
        })
    }
}