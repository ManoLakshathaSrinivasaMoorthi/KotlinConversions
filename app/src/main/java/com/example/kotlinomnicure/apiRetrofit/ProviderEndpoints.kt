package com.example.kotlinomnicure.apiRetrofit

import com.example.kotlinomnicure.apiRetrofit.RequestBodys.GetHandoffListRequestBody
import com.example.kotlinomnicure.apiRetrofit.RequestBodys.GetReadStatusRequestBody
import com.example.kotlinomnicure.model.CommonResponseRetro
import com.example.kotlinomnicure.model.HealthMonitoring
import com.google.gson.JsonObject
import com.mvp.omnicure.kotlinactivity.requestbodys.*
import omnicurekotlin.example.com.providerEndpoints.HandOffAcceptRequest
import omnicurekotlin.example.com.providerEndpoints.model.*
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface ProviderEndpoints {
    @POST("providerEndpoints/v1/DoBspHandover")
    fun sendBedSideHandOffPatient(@Body patientHandOffRequest: PatientHandOffRequest?): Call<CommonResponse?>?

    @POST("providerEndpoints/v1/RDHandOffRequest")
    fun remoteHandOff(@Body remoteHandOffRequest: RemoteHandOffRequest?): Call<CommonResponse?>?

    @POST("providerEndpoints/v1/sendMessageMultiple")
    fun Multiplecall(@Body content: GroupCall?): Call<CommonResponse?>?


    @POST("providerEndpoints/v1/teamDetails")
    fun virtualTeams(@Body jsonObject: JsonObject?): Call<CommonResponse?>?

    @POST("providerEndpoints/v1/teamDetails")
    fun virtualTeams(@Body uid: CommonProviderIdBody?): Call<TeamsDetailListResponse?>?

    @POST("providerEndpoints/v1/sendChatMessage")
    fun sendMessage(@Body sendChatMessageInputRequestModel: SendChatMessageInputRequestModel?): Call<SendChatMessageOutuputResponseModel?>?


    @POST("providerEndpoints/v1/getAlerts")
    fun getalertsreponse(@Body body: CommonProviderIdBody?): Call<SystemAlerts?>?

    @POST("providerEndpoints/v1/BspHandOffList")
    fun bSPHandOffList(@Body body: CommonProviderIdBody?): Call<HandOffListResponse?>?

    @POST("providerEndpoints/v1/teamDetailsByName")
    fun teamDetailsByName(@Body requestBody: TeamDetailsByNameRequestBody?): Call<TeamsDetailListResponse?>?


    @POST("providerEndpoints/v1/sendHealthMonitorEvent")
    fun sendHealthMonitorEvent(@Body requestBody: HealthMonitorEventRequestBody?): Call<HealthMonitoring?>?

    @POST("providerEndpoints/v1/doReconsultOtherPatient")
    fun doReconsultOtherPatient(@Body otherRebroadcastRequest: OtherRebroadcastRequest?): Call<CommonResponseRetro?>?

    @POST("providerEndpoints/v1/sendMessageOnTopic")
    fun sendMessageOnTopic(@Body body: SendMessageOnTopicRequestBody?): Call<CommonResponseRetro?>?

    @POST("providerEndpoints/v1/saveAuditCall")
    fun saveAuditCall(@Body body: SaveAuditCallRequestBody?): Call<CommonResponseRetro?>?

    @POST("providerEndpoints/v1/contactAdminEmail")
    fun sendContactAdminEmail(@Body contactAdminParams: ContactAdminParams?): Call<CommonResponse?>?

    @POST("providerEndpoints/v1/doRDHandOff")
    fun doRDHandOff(@Body handOffAcceptRequest: HandOffAcceptRequest?): Call<CommonResponse?>?

    @POST
    fun getProviderById(@Url url: String?, @Body body: GetProviderByIdRequestBody?): Call<CommonResponse?>?

    @POST
    fun getProviderById(@Url url: String?, @Body bodyValues: HashMap<String?, String?>?): Call<CommonResponse?>?

    @GET //    Call<CommonResponse> updateStatus(@Url String url, @Body Provider provider);
    @POST
    fun updateStatus(@Url url: String?, @Body provider: Provider?): Call<CommonResponse?>?

    @POST("providerEndpoints/v1/updateProvider")
    fun updateStatus(@Body provider: Provider?): Call<CommonResponse?>?

    @POST
    fun getHandoffList(@Url url: String?, @Body body: GetHandoffListRequestBody?, ): Call<CommonResponse?>?

    @POST
    fun setReadStatus(@Url url: String?, @Body body: GetReadStatusRequestBody?, ): Call<CommonResponse?>?
    @GET
    fun getProviderList(@Url url: String?, @Query("role") role: String?, ): Call<ProviderListResponse?>?

    @GET
    fun sendMessage(@Url url: String?, @Query("patientId") patientId: String?, ): Call<CommonResponse?>?

    @POST
    fun getProviderList(@Url url: String?, @Body body: GetProviderListRequestBody?): Call<ProviderListResponse?>?


    @POST
    fun sendMessage(@Url url: String?, @Body body: SendMessageRequestBody?): Call<CommonResponse?>?

    @POST("providerEndpoints/v1/sendMessageNotification/{id}/{token}/{receiverId}/{message}/{type}")
    fun sendCallResponseMessage(
        @Path("id") providerId: Long?,
        @Path("token") token: String?,
        @Path("receiverId") receiverId: Long?,
        @Path("message") channelName: String?,
        @Path("type") messageType: String?,
    ): Call<CommonResponse?>?

    @POST("providerEndpoints/v1/sendMessageNotification")
    open fun sendMessageNotification(@Body body: SendMsgNotifyRequestBody?): Call<CommonResponse?>?
}