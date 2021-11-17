package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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


//                    final TermsAndConditionsResponse remoteProviderListResponse = EndPointBuilder.getUserEndpoints()
//                            .getTerms()
//                            .execute();
        ApiClient.getApiUserEndpoints(true, true).getTermsAndConditions()
            .enqueue(object : Callback<TermsAndConditionsResponse> {
                override fun onResponse(
                    call: Call<TermsAndConditionsResponse>,
                    response: Response<TermsAndConditionsResponse>
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
                        commonResponse.setErrorMessage(errMsg[0])
                        if (termsConditionsObervable == null) {
                            termsConditionsObervable =
                                MutableLiveData<TermsAndConditionsResponse?>()
                        }
                        termsConditionsObervable!!.setValue(commonResponse)
                    }
                }

                override fun onFailure(call: Call<TermsAndConditionsResponse>, t: Throwable) {
                    Handler(Looper.getMainLooper()).post {
                        errMsg[0] = Constants.API_ERROR
                        val commonResponse = TermsAndConditionsResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (termsConditionsObervable == null) {
                            termsConditionsObervable =
                                MutableLiveData<TermsAndConditionsResponse?>()
                        }
                        termsConditionsObervable!!.setValue(commonResponse)
                    }
                }
            })
//                    Call<TermsAndConditionsResponse> call = ApiClient.getApiUserEndpoints(true,true).getTermsAndConditions();
//                    call.enqueue(new Callback<TermsAndConditionsResponse>() {
//                        @Override
//                        public void onResponse(Call<TermsAndConditionsResponse> call, Response<TermsAndConditionsResponse> response) {
//                            Log.e(TAG, "onResponse: hi"+response.isSuccessful());
//                            Log.e(TAG, "onResponse: hi"+response.code());
//                            Log.e(TAG, "onResponse: hi"+response.body());
//                            if (response.isSuccessful()) {
//                                Log.i(TAG, "onResponse: SUCCESS");
//                                Log.e(TAG, "onResponse data: "+new Gson().toJson(response.body()));
//                                if (termsConditionsObervable == null) {
//                                    termsConditionsObervable = new MutableLiveData<>();
//                                }
//                                termsConditionsObervable.setValue(response.body());
//
//
//                                new Handler(Looper.getMainLooper()).post(() -> {
//                                    if (termsConditionsObervable == null) {
//                                        termsConditionsObervable = new MutableLiveData<>();
//                                    }
//                                    termsConditionsObervable.setValue(response.body());
//                                });
//
//                            } else {
//                                Log.i(TAG, "onResponse: FAILURE");
//                                TermsAndConditionsResponse commonResponse = new TermsAndConditionsResponse();
//                                if (termsConditionsObervable == null) {
//                                    termsConditionsObervable = new MutableLiveData<>();
//                                }
//                                termsConditionsObervable.setValue(commonResponse);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<TermsAndConditionsResponse> call, Throwable t) {
//
//                            new Handler(Looper.getMainLooper()).post(() -> {
//                                TermsAndConditionsResponse commonResponse = new TermsAndConditionsResponse();
//                                if (termsConditionsObervable == null) {
//                                    termsConditionsObervable = new MutableLiveData<>();
//                                }
//                                termsConditionsObervable.setValue(commonResponse);
//                            });
//                        }
//                    });
    }


    override fun onCleared() {
        super.onCleared()
        termsConditionsObervable = null
    }


}
