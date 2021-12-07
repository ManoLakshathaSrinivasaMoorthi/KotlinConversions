package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.utils.Constants
import com.google.gson.Gson
import omnicurekotlin.example.com.userEndpoints.model.TermsAndConditionsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TermsAndConditionsViewModel:ViewModel(){
    private val TAG = TermsAndConditionsViewModel::class.java.simpleName
    private var termsConditionsObervable: MutableLiveData<TermsAndConditionsResponse?>? = null

    fun getTerms(): LiveData<TermsAndConditionsResponse?>? {
        termsConditionsObervable = MutableLiveData<TermsAndConditionsResponse?>()
        getTermsAndConditions()
        return termsConditionsObervable
    }

    private fun getTermsAndConditions() {
        val errMsg = arrayOfNulls<String>(1)



        ApiClient().getApiUserEndpoints(true, true)?.getTermsAndConditions()
            ?.enqueue(object : Callback<TermsAndConditionsResponse?> {
                override fun onResponse(
                    call: Call<TermsAndConditionsResponse?>,
                    response: Response<TermsAndConditionsResponse?>
                ) {
                    Log.e(TAG, "onResponse: hi" + response.isSuccessful())
                    Log.e(TAG, "onResponse: hi" + response.code())
                    Log.e(TAG, "onResponse: hi" + response.body())
                    if (response.isSuccessful()) {
                        Log.i(TAG, "onResponse: SUCCESS")
                        Log.e(TAG, "onResponse data: " + Gson().toJson(response.body()))
                        if (termsConditionsObervable == null) {
                            termsConditionsObervable =
                                MutableLiveData<TermsAndConditionsResponse?>()
                        }
                        termsConditionsObervable!!.setValue(response.body())
                        Handler(Looper.getMainLooper()).post {
                            if (termsConditionsObervable == null) {
                                termsConditionsObervable =
                                    MutableLiveData<TermsAndConditionsResponse?>()
                            }
                            termsConditionsObervable!!.setValue(response.body())
                        }
                    } else {
                        errMsg[0] = Constants.API_ERROR
                        Log.i(TAG, "onResponse: FAILURE")
                        val commonResponse = TermsAndConditionsResponse()
                        errMsg[0]?.let { commonResponse.setErrorMessage(it) }
                        if (termsConditionsObervable == null) {
                            termsConditionsObervable =
                                MutableLiveData<TermsAndConditionsResponse?>()
                        }
                        termsConditionsObervable!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<TermsAndConditionsResponse?>, t: Throwable) {
                    Handler(Looper.getMainLooper()).post {
                        errMsg[0] = Constants.API_ERROR
                        val commonResponse = TermsAndConditionsResponse()
                        errMsg[0]?.let { commonResponse.setErrorMessage(it) }
                        if (termsConditionsObervable == null) {
                            termsConditionsObervable =
                                MutableLiveData<TermsAndConditionsResponse?>()
                        }
                        termsConditionsObervable!!.setValue(commonResponse)
                    }
                }
            })

    }


    override fun onCleared() {
        super.onCleared()
        termsConditionsObervable = null
    }


}
