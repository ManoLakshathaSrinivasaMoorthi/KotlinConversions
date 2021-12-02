package com.example.kotlinomnicure.apiRetrofit

import omnicurekotlin.example.com.loginEndpoints.model.LoginRequest
import com.example.dailytasksamplepoc.kotlinomnicure.requestbodys.RegistrationEmailOtpRequestBody
import com.example.dailytasksamplepoc.kotlinomnicure.requestbodys.RegistrationPhoneOtpRequestBody
import com.example.kotlinomnicure.model.CommonResponseRetro
import com.example.kotlinomnicure.requestbodys.CommonPatientIdRequestBody
import com.example.kotlinomnicure.requestbodys.RegistrationVerifyRequestBody

import com.mvp.omnicure.kotlinactivity.requestbodys.LoginFailedRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.PhoneNumberBody
import omnicurekotlin.example.com.userEndpoints.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserEndpoints {
    @GET("userEndpoints/v1/getCountryList")
    fun getCountryCodes(): Call<CountryCodeListResponse>

    @GET("userEndpoints/v1/versioninforesponse/ANDROID")
    fun getVersionInfo(): Call<VersionInfoResponse?>?


    @POST("userEndpoints/v1/getEmailByPhoneNumber")
    fun getEmailByPhoneNumber(@Body body: PhoneNumberBody?): Call<CommonResponse?>?

    @POST("userEndpoints/v1/emailOrPhoneDuplicationCheck")
    fun emailcheckprovider(@Body bodyValues: Provider?): Call<CommonResponse?>?


    @POST("userEndpoints/v1/loginStatus")
    fun loginStatus(@Body redirectRequest: RedirectRequest?): Call<CommonResponse?>?

    @POST("userEndpoints/v1/registerUser")
    fun registerUser(@Body providerData: Provider?): Call<CommonResponse?>?

    @POST("userEndpoints/v1/renewIdToken")
    fun renewIdToken(@Body loginRequest: LoginRequest?): Call<CommonResponse?>?

    @GET("userEndpoints/v1/getHospitals")
    fun hospitalList(): Call<HospitalListResponse?>?

    @GET("userEndpoints/v1/getRemoteProviderType")
    fun getRemoteproviderTypeList(): Call<RemoteProviderListResponse?>?


    @POST("userEndpoints/v1/loginFailed")
    fun loginFailed(@Body body: LoginFailedRequestBody?): Call<CommonResponse?>?


    // Changes in key names in ForgotPasswordRequest class so new Body class.
    @POST("userEndpoints/v1/registrationEmailOTP")
    fun registrationEmailOTP(@Body body: RegistrationEmailOtpRequestBody?): Call<CommonResponse?>?


    // Changes in key names in ForgotPasswordRequest class so new Body class.
    @POST("userEndpoints/v1/registrationPhoneOTP")
    fun registrationPhoneOTP(@Body body: RegistrationPhoneOtpRequestBody?): Call<CommonResponse?>?


    @POST("userEndpoints/v1/registrationVerify")
    fun registrationVerify(@Body body: RegistrationVerifyRequestBody?): Call<CommonResponse?>?

    @POST("userEndpoints/v1/set/new/password")
    fun changePassword(@Body resetPasswordRequest: ResetPasswordRequest?): Call<CommonResponse?>?

    @POST("userEndpoints/v1/forgot/password/otp/verify")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest?): Call<CommonResponse?>?

    @POST("userEndpoints/v1/reset/password")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest?): Call<CommonResponse?>?

    @POST("userEndpoints/v1/forgot/password")
    fun doForgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest?): Call<CommonResponse?>?

    @POST("userEndpoints/v1/emailOrPhoneDuplicationCheck")
    fun emailOrPhoneDuplicationCheck(@Body provider: Provider?): Call<CommonResponse?>?

    // missed out in Api conversion also having changed endpoint
    @GET("userEndpoints/v1/getRemoteProviderType")
    fun getRemoteProviderType(): Call<RemoteProviderListResponse?>?


    @GET("userEndpoints/v1/termsAndCondition")
    fun getTermsAndConditions(): Call<TermsAndConditionsResponse?>?

    @POST("userEndpoints/v1/patientActiveDetails")
    fun getPatientActiveDetails(@Body commonPatientIdRequestBody: CommonPatientIdRequestBody?): Call<CommonResponseRetro?>?
}