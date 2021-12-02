package com.example.kotlinomnicure.apiRetrofit


import com.example.kotlinomnicure.model.SOSResponse
import com.example.kotlinomnicure.requestbodys.CommonPatientIdRequestBody
import com.google.gson.JsonObject

import com.mvp.omnicure.kotlinactivity.requestbodys.InviteProviderRequestBody
import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import omnicurekotlin.example.com.patientsEndpoints.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.HashMap

interface PatientEndpoints {
    @POST
    fun sendTransferPatientWithInHospital(@Url url: String?, @Body patientTransferRequest: PatientTransferRequest?): Call<CommonResponse?>?


    @POST("patientEndpoints/v1/acceptInvite")
    fun acceptInvite(@Body bodyValues: HashMap<String, String>): Call<CommonResponse?>?

    @POST("patientEndpoints/v1/getPatientDetailsById")
    fun patientdetailsresponse(@Body body: CommonPatientIdRequestBody?): Call<PatientDetail?>?


    @POST("patientEndpoints/v1/GetTransferHospitalList")
    fun GetTransferHospitalList(@Body body: CommonPatientIdRequestBody?): Call<CommonResponse?>?



    @GET("patientEndpoints/v1/getDocBoxPatientList")
    fun docboxpatientlistresponse(@Body request: HashMap<String?, String?>?): Call<DocBoxPatientListResponse?>?

    @POST("patientEndpoints/v1/doPatientDischarge")
    fun doPatientDischarge(@Body dischargePatientRequest: DischargePatientRequest?): Call<CommonResponse?>?

    @POST("patientEndpoints/v1/registerPatient")
    fun registerPatient(@Body appointment: Appointment?): Call<CommonResponse?>?



    @POST("patientEndpoints/v1/inviteProvider")
    fun inviteProvider(@Body body: InviteProviderRequestBody?): Call<CommonResponse?>?


    // doTransferWithinHospital/{token}
    @POST("patientEndpoints/v1/doTransferWithinHospital")
    fun doTransferWithinHospital(@Body patientTransferRequest: PatientTransferRequest?): Call<CommonResponse?>?

    @POST("patientEndpoints/v1/resetAcuityScore")
    fun resetAcuityScoreApi(@Body request: HashMap<String, String>): Call<CommonResponse?>?

    @POST("patientEndpoints/v1/resetAcuityScore")
    fun resetAcuityApi(@Body request: HashMap<String, String>): Call<CommonResponse?>?

    @POST("patientEndpoints/v1/smsRegisterVerify")
    fun verifyOtp(@Body otpModel: PatientOtpModel?): Call<CommonResponse?>?


    @POST("patientEndpoints/v1/addPatient")
    fun addPatient(@Body patient: Patient?): Call<CommonResponse?>?


    @POST("patientEndpoints/v1/athenadevicelistresponse")
    fun getAthenaDeviceListAPI(@Body jsonObject: JsonObject?): Call<AthenaDeviceListResponse?>?

    @POST("patientEndpoints/v1/getAthenaDeviceList")
    fun getAthenaDeviceListAPI(@Body bodyValues: HashMap<String?, String?>?): Call<AthenaDeviceListResponse?>?

    @POST("patientEndpoints/v1/doTransferHospialForPatient")
    fun sendTransferPatientToAnotherHospital(@Body patientTransferRequest: PatientTransferRequest?): Call<CommonResponse?>?


    @POST("patientEndpoints/v1/inviteProviderBroadCast")
    fun inviteBroadCast(@Body bodyValues: HashMap<String?, String?>?): Call<CommonResponse?>?



    @POST("patientEndpoints/v1/sendSOSMessage")
    fun startSOSAPIs(@Body jsonObject: JsonObject?): Call<SOSResponse?>?


    @POST("patientEndpoints/v1/sendSOSMessage")
    fun startSOSAPI(@Body bodyValues: HashMap<String, String>): Call<SOSResponse?>?


    @POST("patientEndpoints/v1/dischargePatient")
    fun dischargePatientApi(@Body bodyValues: HashMap<String, String>): Call<CommonResponse?>?

    @POST("patientEndpoints/v1/getPatientHistory")
    fun patienthistoryAPI(@Body bodyValues: HashMap<String?, String?>?): Call<PatientHistoryResponse?>?



    @POST("patientEndpoints/v1/GetProvidersForHospital")
    fun getTransferHospitalProviderListApi(
        @Body bodyValues: HashMap<String?, String?>?
    ): Call<CommonResponse?>?

}
