package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinomnicure.apiRetrofit.ApiClient
import com.example.kotlinomnicure.backend.EndPointBuilder
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.*

class CensusHospitalListViewModel : ViewModel() {

    private var hospitalListObservable: MutableLiveData<HospitalListResponse?>? = null
    private val TAG = "CensusHospitalListVM"

    fun getHospitalList(id: Long): LiveData<HospitalListResponse?>? {
        hospitalListObservable = MutableLiveData()
        //        getHospitals(id);
        getHospitalsRetro(id)
        return hospitalListObservable
    }

    private fun getHospitalsRetro(id: Long) {
        val errMsg = arrayOfNulls<String>(1)
        val bodyValues = HashMap<String, String>()
        bodyValues["id"] = id.toString()
        ApiClient().getApiHospital(true, true)?.hospitallistresponseUser(bodyValues)
            ?.enqueue(object : Callback<HospitalListResponse?> {
                override fun onResponse(
                    call: Call<HospitalListResponse?>,
                    response: Response<HospitalListResponse?>
                ) {
//                Log.d(TAG, "onResponse: hospitallistresponsee "+response.code());
//                Log.d(TAG, "onResponse: hospitallistresponse_success "+response.isSuccessful());
//                Log.d(TAG, "onResponse: hospitallistresponsee--> "+new Gson().toJson(response.body()));
                    if (response.isSuccessful) {
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData()
                        }
                        hospitalListObservable!!.setValue(response.body())
                    } else {
                        if (response.code() == 705) {
                            errMsg[0] = "redirect"
                        } else if (response.code() == 403) {
                            errMsg[0] = "unauthorized"
                        } else {
                            errMsg[0] = Constants.API_ERROR
                        }
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse = HospitalListResponse()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (hospitalListObservable == null) {
                                hospitalListObservable = MutableLiveData()
                            }
                            hospitalListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<HospitalListResponse?>, t: Throwable) {
//                Log.e(TAG, "onFailure: " + t.toString() );
//                if (t instanceof SocketTimeoutException)
//                    errMsg[0] = Constants.APIErrorType.SocketTimeoutException.toString();
//                if (t instanceof Exception)
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse = HospitalListResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData()
                        }
                        hospitalListObservable!!.setValue(commonResponse)
                    }
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

    private fun getHospitals(id: Long) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val hospitalListResponse: HospitalListResponse =
                        EndPointBuilder().getHospitalEndpoints()
                            .getHospitalList(id)
                            .execute()
                    Handler(Looper.getMainLooper()).post {
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData()
                        }
                        hospitalListObservable!!.setValue(hospitalListResponse)
                    }
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
                    errMsg = Constants.API_ERROR
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val response = HospitalListResponse()
                        response.setErrorMessage(errMsg)
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData()
                        }
                        hospitalListObservable!!.setValue(response)
                    }
                }
            }
        }).start()
    }

    override fun onCleared() {
        super.onCleared()
        hospitalListObservable = null
    }

    private fun <T> Call<T>?.enqueue(callback: Callback<HospitalListResponse?>) {

    }

}
