package com.example.kotlinomnicure.apiRetrofit

import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.HashMap

interface ApiEndpoints {

    /*@POST("loginEndpoints/v1/loginDetails")
    Call<CommonResponse> doLogin(@Query("fcmkey") String fcmkey, @Query("osType") String osType, @Body HashMap<String, String> login);*/


    /*@POST("loginEndpoints/v1/loginDetails")
    Call<CommonResponse> doLogin(@Query("fcmkey") String fcmkey, @Query("osType") String osType, @Body HashMap<String, String> login);*/
    @POST("loginEndpoints/v1/loginDetails")
    fun doLogin(@Body body: LoginDetailsRequestBody?): Call<CommonResponse?>?

    // loginWithPassword
    @POST("loginEndpoints/v1/loginWithPassword")
    fun  //    Call<omnicure.mvp.com.loginEndpoints.model.CommonResponse> loginWithPassword(@Query("email") String email,
    //                                                                                 @Query("password") String password,
    //                                                                                 @Query("token") String token);
            loginWithPassword(@Body bodyValues: HashMap<String?, String?>?): Call<omnicure.mvp.com.loginEndpoints.model.CommonResponse?>?

    @POST("healthcareEndpoints/v1/addOrUpdateProviderNotification")
    fun addOrUpdateNotificationSettings(@Body addNotificationDataRequest: AddNotificationDataRequest?): Call<CommonResponseProviderNotification?>?

    /*@POST("loginEndpoints/v1/commonresponse/{id}/{token}")
    Call<CommonResponse> commonresponse(@Path("id") Long id, @Path("token") String token);*/

    /*@POST("loginEndpoints/v1/commonresponse/{id}/{token}")
    Call<CommonResponse> commonresponse(@Path("id") Long id, @Path("token") String token);*/
    //    @POST("loginEndpoints/v1/commonresponse/{id}/{token}")
    @POST("loginEndpoints/v1/updateFcmkKey")
    fun commonresponse(@Body body: UpdateFcmkKeyRequestBody?): Call<CommonResponse?>?

    @POST("appointmentEndpoints/v1/addAppointment/{token}")
    fun addAppointment(
        @Path("token") token: String?,
        @Body appointment: Appointment?
    ): Call<omnicure.mvp.com.appointmentEndpoints.model.CommonResponse?>?


//    @GET("healthcareEndpoints/v1/getProviderNotification?")
//    Call<ProviderNotificationResponse> getProviderNotificationDetailsApi(@Query("userId") Long providerId);
//    @GET("healthcareEndpoints/v1/getProviderNotification?")
//    Call<ProviderNotificationResponse> getProviderNotificationDetailsApi(@Query("userId") Long providerId);

    //    @GET("healthcareEndpoints/v1/getProviderNotification?")
    //    Call<ProviderNotificationResponse> getProviderNotificationDetailsApi(@Query("userId") Long providerId);
    //    @GET("healthcareEndpoints/v1/getProviderNotification?")
    //    Call<ProviderNotificationResponse> getProviderNotificationDetailsApi(@Query("userId") Long providerId);
    @POST("healthcareEndpoints/v1/getProviderNotification")
    fun getProviderNotificationDetailsApi(@Body providerId: HashMap<String?, String?>?): Call<ProviderNotificationResponse?>?

    //changed "userId" key to "id"
    @POST("healthcareEndpoints/v1/getProviderNotification")
    fun getProviderNotificationDetailsApi(@Body body: ProviderNotificationDetailsRequestBody?): Call<ProviderNotificationResponse?>?

//    @POST("loginEndpoints/v1/logout/{id}/{token}")
//    Call<CommonResponse> doLogout(@Path("id") Long userId, @Path("token") String token);

    /*@POST("loginEndpoints/v1/logout")
    Call<CommonResponse> doLogout(@Body HashMap<String,String> logoutValues);*/

    //    @POST("loginEndpoints/v1/logout/{id}/{token}")
    //    Call<CommonResponse> doLogout(@Path("id") Long userId, @Path("token") String token);
    /*@POST("loginEndpoints/v1/logout")
    Call<CommonResponse> doLogout(@Body HashMap<String,String> logoutValues);*/
    //changed "userId" key to "id"
    @POST("loginEndpoints/v1/logout")
    fun doLogout(@Body body: LogoutRequestBody?): Call<CommonResponse?>?
}
