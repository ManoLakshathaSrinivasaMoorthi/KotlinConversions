package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.hospitalEndpoints.model.AddNewPatientWardResponse
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.HashMap

class TransferPatientViewModel : ViewModel() {
    private var wardResponseObservable: MutableLiveData<AddNewPatientWardResponse?>? = null
    private var hospitalListResponseObservable: MutableLiveData<CommonResponse?>? = null
    private var providerListResponseObservable: MutableLiveData<CommonResponse?>? = null
    private var sendTransferResponseObservable: MutableLiveData<CommonResponse?>? = null
    private val TAG = javaClass.simpleName

    fun getWardsList(hospitalId: Long): LiveData<AddNewPatientWardResponse?>? {
        wardResponseObservable = MutableLiveData<AddNewPatientWardResponse?>()
        getWithInHospitalWardList(hospitalId)
        return wardResponseObservable
    }

    fun getHospitalList(token: String, patientId: String): LiveData<CommonResponse?>? {
        hospitalListResponseObservable = MutableLiveData<CommonResponse?>()
        //        getTransferHospitalList(token, patientId);
        getTransferHospitalListRetro(token, patientId)
        return hospitalListResponseObservable
    }

    fun getProviderListResponse(hospitalId: Long, providerId: Long): LiveData<CommonResponse?>? {
        providerListResponseObservable = MutableLiveData<CommonResponse?>()
        getTransferProviderList(hospitalId, providerId)
        return providerListResponseObservable
    }

    private fun getWithInHospitalWardList(hospitalId: Long) {
        val bodyValues = HashMap<String, String>()
        bodyValues["hospitalId"] = hospitalId.toString()

//        Call<AddNewPatientWardResponse> call = ApiClient.getApiHospital(false, false).getAddNewPatientWardList(hospitalId);
        val call: Call<AddNewPatientWardResponse> =
            ApiClient().getApiHospital(true, true).getAddNewPatientWardList(bodyValues)
        call.enqueue(object : Callback<AddNewPatientWardResponse?> {
            override fun onResponse(
                call: Call<AddNewPatientWardResponse?>,
                response: Response<AddNewPatientWardResponse?>
            ) {
                if (response.isSuccessful()) {
                    if (wardResponseObservable == null) {
                        wardResponseObservable = MutableLiveData<AddNewPatientWardResponse?>()
                    }
                    wardResponseObservable!!.setValue(response.body())
                } else {
                    val addNewPatientWardResponse = AddNewPatientWardResponse()
                    if (wardResponseObservable == null) {
                        wardResponseObservable = MutableLiveData<AddNewPatientWardResponse?>()
                    }
                    wardResponseObservable!!.setValue(addNewPatientWardResponse)
                }
            }

            override fun onFailure(call: Call<AddNewPatientWardResponse?>, t: Throwable) {
                Handler(Looper.getMainLooper()).post {
                    val response = AddNewPatientWardResponse()
                    if (wardResponseObservable == null) {
                        wardResponseObservable = MutableLiveData<AddNewPatientWardResponse?>()
                    }
                    wardResponseObservable!!.setValue(response)
                }
            }
        })

        /*new Thread(new Runnable() {
            String errMsg = "";

            @Override
            public void run() {
                try {
                    final AddNewPatientWardResponse transferWardResponse = EndPointBuilder.getHospitalEndpoints()
                            .getAddNewPatientWardList(hospitalId)
                            .execute();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (wardResponseObservable == null) {
                            wardResponseObservable = new MutableLiveData<>();
                        }
                        wardResponseObservable.setValue(transferWardResponse);
                        Log.d(TAG, "Wards hospitalId" + hospitalId);
                        Log.i("TAG", "Wards transferWardResponse " + new Gson().toJson(transferWardResponse));

                    });
                } catch (SocketTimeoutException e) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString();
                } catch (Exception e) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR;

                }
                if (!TextUtils.isEmpty(errMsg)) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        AddNewPatientWardResponse response = new AddNewPatientWardResponse();
                        if (wardResponseObservable == null) {
                            wardResponseObservable = new MutableLiveData<>();
                        }
                        wardResponseObservable.setValue(response);
                    });
                }
            }
        }).start();*/
    }


    private fun getTransferHospitalListRetro(token: String, patientId: String) {
        val errMsg = arrayOfNulls<String>(1)

        //sending body through data class
        val requestBody = CommonPatientIdRequestBody(java.lang.Long.valueOf(patientId))

//        ApiClient.getApiPatientEndpoints(false, false).GetTransferHospitalList(token, patientId).enqueue(new Callback<CommonResponse>() {
        ApiClient.getApiPatientEndpoints(true, true).GetTransferHospitalList(requestBody)
            .enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    Log.d(TAG, "onResponse: GetTransferHospitalList " + response.code())
                    if (response.isSuccessful()) {
                        val hospitalListResponse: CommonResponse? = response.body()
                        if (hospitalListResponseObservable == null) {
                            hospitalListResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        hospitalListResponseObservable!!.setValue(hospitalListResponse)
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                    Log.e(TAG, "onFailure: GetTransferHospitalList $t")
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is IOException) errMsg[0] = Constants.API_ERROR
                    val response = CommonResponse()
                    response.setErrorMessage(errMsg[0])
                    if (hospitalListResponseObservable == null) {
                        hospitalListResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    hospitalListResponseObservable!!.setValue(response)
                }
            })
    }

    private fun getTransferHospitalList(token: String, patientId: String) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    Log.d(TAG, "token : $token")
                    Log.d(TAG, "patientId : $patientId")
                    val hospitalListResponse: CommonResponse = EndPointBuilder.getPatientEndpoints()
                        .getTransferHospitalList(token, patientId) //.getHospitalListSample()
                        .execute()
                    Handler(Looper.getMainLooper()).post {
                        if (hospitalListResponseObservable == null) {
                            hospitalListResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        hospitalListResponseObservable!!.setValue(hospitalListResponse)
                        Log.i(
                            TAG,
                            "getHospitalListSample :  " + Gson().toJson(hospitalListResponse)
                        )
                    }
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR
                }
                Log.d("ViewModel", "errMsg : $errMsg")
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val response = CommonResponse()
                        if (hospitalListResponseObservable == null) {
                            hospitalListResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        hospitalListResponseObservable!!.setValue(response)
                    }
                }
            }
        }).start()
    }

    private fun getTransferProviderList(hospitalId: Long, providerId: Long) {
        val errMsg = arrayOfNulls<String>(1)
        // Parsing the values in body
        val bodyValues = HashMap<String, String>()
        bodyValues["hospitalId"] = hospitalId.toString()
        bodyValues["providerId"] = providerId.toString()
        ApiClient.getApiPatientEndpoints(
            true,
            true
        ).getTransferHospitalProviderListApi //                getTransferHospitalProviderListApi(hospitalId.toString(), providerId.toString())
        bodyValues
            .enqueue(object : Callback<omnicure.mvp.com.patientEndpoints.model.CommonResponse?> {
                override fun onResponse(
                    call: Call<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>,
                    response: Response<omnicure.mvp.com.patientEndpoints.model.CommonResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d("discharge", "onResponse: $response")
                        val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse? =
                            response.body()
                        if (providerListResponseObservable == null) {
                            providerListResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerListResponseObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("discharge", "onResponse: $response")
                        errMsg[0] = Constants.API_ERROR
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                                CommonResponse()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (providerListResponseObservable == null) {
                                providerListResponseObservable = MutableLiveData<CommonResponse?>()
                            }
                            providerListResponseObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                    Log.d("discharge", "onFailure: $t")
                    errMsg[0] = Constants.API_ERROR
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (providerListResponseObservable == null) {
                            providerListResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerListResponseObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val finalErrMsg = errMsg[0]
            Handler(Looper.getMainLooper()).post {
                val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                    CommonResponse()
                commonResponse.setErrorMessage(finalErrMsg)
                if (providerListResponseObservable == null) {
                    providerListResponseObservable = MutableLiveData<CommonResponse?>()
                }
                providerListResponseObservable!!.setValue(commonResponse)
            }
        }
    }

    private fun getTransferProviderListOld(hospitalId: Long, providerId: Long) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val wardListResponse: CommonResponse = EndPointBuilder.getPatientEndpoints()
                        .getTransferHospitalProviderList(hospitalId, providerId)
                        .execute()
                    Handler(Looper.getMainLooper()).post {
                        if (providerListResponseObservable == null) {
                            providerListResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerListResponseObservable!!.setValue(wardListResponse)
                    }
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val response = CommonResponse()
                        if (providerListResponseObservable == null) {
                            providerListResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        providerListResponseObservable!!.setValue(response)
                    }
                }
            }
        }).start()
    }


    override fun onCleared() {
        super.onCleared()
        hospitalListResponseObservable = null
        providerListResponseObservable = null
    }

    fun transferPatientWithInHospital(
        token: String,
        patientTransferRequest: PatientTransferRequest
    ): MutableLiveData<CommonResponse?>? {
        sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
        sendTransferPatientWithInHospital(token, patientTransferRequest)
        return sendTransferResponseObservable
    }


    fun transferPatientToAnotherHospital(
        token: String,
        patientTransferRequest: PatientTransferRequest
    ): MutableLiveData<CommonResponse?>? {
        sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
        sendTransferPatientToAnotherHospital(token, patientTransferRequest)
        return sendTransferResponseObservable
    }


    private fun sendTransferPatientWithInHospital(
        token: String,
        patientTransferRequest: PatientTransferRequest
    ) {
        val errMsg = arrayOfNulls<String>(1)
        ApiClient.getApiPatientEndpoints(true, true)
            .doTransferWithinHospital(patientTransferRequest)
            .enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d("transferTag", "onResponse: $response")
                        val commonResponse: CommonResponse? = response.body()
                        if (sendTransferResponseObservable == null) {
                            sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        sendTransferResponseObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("transferTag", "onResponse: $response")
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                                CommonResponse()
                            commonResponse.setErrorMessage(errMsg[0])
                            if (sendTransferResponseObservable == null) {
                                sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
                            }
                            sendTransferResponseObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                    Log.d("transferTag", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(errMsg[0])
                        if (sendTransferResponseObservable == null) {
                            sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        sendTransferResponseObservable!!.setValue(commonResponse)
                    }
                }
            })
    }

    private fun sendTransferPatientWithInHospitalOld(
        token: String,
        patientTransferRequest: PatientTransferRequest
    ) {
        Thread(object : Runnable {
            var errMsg = ""
            override fun run() {
                try {
                    val wardListResponse: CommonResponse = EndPointBuilder.getPatientEndpoints()
                        .doTransferWithinHospital(token, patientTransferRequest)
                        .execute()
                    Handler(Looper.getMainLooper()).post {
                        if (sendTransferResponseObservable == null) {
                            sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        sendTransferResponseObservable!!.setValue(wardListResponse)
                    }
                } catch (e: SocketTimeoutException) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString()
                } catch (e: Exception) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR
                }
                if (!TextUtils.isEmpty(errMsg)) {
                    Handler(Looper.getMainLooper()).post {
                        val response = CommonResponse()
                        if (sendTransferResponseObservable == null) {
                            sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
                        }
                        sendTransferResponseObservable!!.setValue(response)
                    }
                }
            }
        }).start()
    }

    private fun sendTransferPatientToAnotherHospital(
        token: String,
        patientTransferRequest: PatientTransferRequest
    ) {
        val errMsg = arrayOfNulls<String>(1)
        //        Call<CommonResponse> call = ApiClient.getApiPatientEndpoints(false, false).sendTransferPatientToAnotherHospital(token, patientTransferRequest);
        val call: Call<CommonResponse> = ApiClient.getApiPatientEndpoints(true, true)
            .sendTransferPatientToAnotherHospital(patientTransferRequest)
        call.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>,
                response: Response<CommonResponse?>
            ) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: SUCCESS")
                    if (sendTransferResponseObservable == null) {
                        sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    sendTransferResponseObservable!!.setValue(response.body())
                } else {
                    errMsg[0] = Constants.API_ERROR
                    val commonResponse = CommonResponse()
                    commonResponse.setErrorMessage(errMsg[0])
                    if (sendTransferResponseObservable == null) {
                        sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
                    }
                    sendTransferResponseObservable!!.setValue(commonResponse)
                }
            }

            override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                errMsg[0] = Constants.API_ERROR
                val commonResponse = CommonResponse()
                commonResponse.setErrorMessage(errMsg[0])
                if (sendTransferResponseObservable == null) {
                    sendTransferResponseObservable = MutableLiveData<CommonResponse?>()
                }
                sendTransferResponseObservable!!.setValue(commonResponse)
            }
        })

    }

}