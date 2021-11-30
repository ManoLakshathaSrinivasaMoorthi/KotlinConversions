package com.example.kotlinomnicure.apiRetrofit

import com.example.dailytasksamplepoc.kotlinomnicure.requestbodys.CommonIdRequestBody
import com.google.gson.JsonObject
import com.mvp.omnicure.kotlinactivity.requestbodys.HospitalIdRequestBody
import omnicurekotlin.example.com.hospitalEndpoints.model.AddNewPatientWardResponse
import omnicurekotlin.example.com.hospitalEndpoints.model.HospitalListResponse
import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientListResponse
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface  HospitalEndpoints {
    @POST("hospitalEndpoints/v1/getProviderListByHospital")
    fun getHospitalById(@Body bodyValues: HashMap<String, String>): Call<CommonResponse?>?


    @POST("hospitalEndpoints/v1/locationresponse/{id}/{token}/{location}")
    fun locationresponse(@Body bodyValues: HashMap<String?, String?>?): Call<CommonResponse?>?


    @POST("hospitalEndpoints/v1/getWardsPatient?")
    fun getHospitalWardPatient(@Body jsonObject: JsonObject?): Call<WardPatientListResponse?>?


    @GET("hospitalEndpoints/v1/getWardsPatient?")
    fun getHospitalWardPatient(
        @Query("hospitalId") hospitalId: Long?,
        @Query("wardName") wardName: String?
    ): Call<WardPatientListResponse?>?

    @GET("hospitalEndpoints/v1/hospitalresponse/{hospitalId}")
    fun getAddNewPatientWardList(@Path("hospitalId") hospitalId: Long?): Call<AddNewPatientWardResponse?>?


    @POST("hospitalEndpoints/v1/getHospitalById")
    fun getAddNewPatientWardList(@Body bodyValues: HashMap<String?, String?>?): Call<AddNewPatientWardResponse?>?


    @POST("hospitalEndpoints/v1/getWards")
    fun getWards(@Body body: HospitalIdRequestBody?): Call<WardPatientListResponse?>?



    //sending body through data class
    @POST("hospitalEndpoints/v1/getHospitals")
    fun hospitallistresponse(@Body body: CommonIdRequestBody?): Call<HospitalListResponse?>?


    @POST("hospitalEndpoints/v1/getHospitals")
    fun hospitallistresponseUser(@Body bodyValues: HashMap<String, String>): Call<HospitalListResponse?>?
}
