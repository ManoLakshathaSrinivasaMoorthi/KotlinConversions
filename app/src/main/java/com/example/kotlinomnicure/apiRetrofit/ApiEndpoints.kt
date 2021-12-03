package com.example.kotlinomnicure.apiRetrofit

import com.mvp.omnicure.kotlinactivity.requestbodys.LoginDetailsRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.LogoutRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.ProviderNotificationDetailsRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.UpdateFcmkKeyRequestBody

import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import omnicurekotlin.example.com.healthcareEndPoints.model.AddNotificationDataRequest
import omnicurekotlin.example.com.healthcareEndPoints.model.CommonResponseProviderNotification
import omnicurekotlin.example.com.healthcareEndPoints.model.ProviderNotificationResponse
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.HashMap

interface ApiEndpoints {
    @POST("loginEndpoints/v1/loginDetails")
    fun doLogin(@Body body: LoginDetailsRequestBody?): Call<CommonResponse?>?

    // loginWithPassword
    @POST("loginEndpoints/v1/loginWithPassword")
    fun loginWithPassword(@Body bodyValues: HashMap<String, String>): Call<CommonResponse?>?

    @POST("healthcareEndpoints/v1/addOrUpdateProviderNotification")
    fun addOrUpdateNotificationSettings(@Body addNotificationDataRequest: AddNotificationDataRequest?): Call<CommonResponseProviderNotification?>?

    @POST("loginEndpoints/v1/updateFcmkKey")
    fun commonresponse(@Body body: UpdateFcmkKeyRequestBody?): Call<CommonResponse?>?

    @POST("appointmentEndpoints/v1/addAppointment/{token}")
    fun addAppointment(
        @Path("token") token: String?,
        @Body appointment: Appointment?
    ): Call<omnicurekotlin.example.com.appointmentEndpoints.model.CommonResponse?>?

    @POST("healthcareEndpoints/v1/getProviderNotification")
    fun getProviderNotificationDetailsApi(@Body providerId: HashMap<String?, String?>?): Call<ProviderNotificationResponse?>?

    //changed "userId" key to "id"
    @POST("healthcareEndpoints/v1/getProviderNotification")
    fun getProviderNotificationDetailsApi(@Body body: ProviderNotificationDetailsRequestBody?): Call<ProviderNotificationResponse?>?

    @POST("loginEndpoints/v1/logout")
    fun doLogout(@Body body: LogoutRequestBody?): Call<CommonResponse?>?

}
