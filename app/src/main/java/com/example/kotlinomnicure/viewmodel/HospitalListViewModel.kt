package com.example.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.kotlinomnicure.utils.Constants
import omnicurekotlin.example.com.userEndpoints.RetrofitService
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit
import omnicurekotlin.example.com.userEndpoints.model.HospitalListResponse
import retrofit2.Response
import java.net.SocketTimeoutException

class HospitalListViewModel:ViewModel() {

    private var hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private lateinit var retService: RetrofitService


    fun getHospitalList(): LiveData<Response<HospitalListResponse>> =  liveData  {
        retService = UserEndpointsRetrofit.getretrofit()
                .create(RetrofitService::class.java)
        hospitalListObservable = MutableLiveData<HospitalListResponse>()

        val response = retService.getHospitals()
        emit(response)
        return@liveData
    }




}
