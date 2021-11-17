package omnicurekotlin.example.com.userEndpoints



import omnicurekotlin.example.com.appointmentEndpoints.model.AppointmentListResponse
import omnicurekotlin.example.com.hospitalEndpoints.model.AddNewPatientWardResponse
import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientListResponse
import omnicurekotlin.example.com.patientsEndpoints.model.*
import omnicurekotlin.example.com.patientsEndpoints.model.Patient
import omnicurekotlin.example.com.providerEndpoints.model.CountryCodeList
import omnicurekotlin.example.com.providerEndpoints.model.OtherRebroadcastRequest
import omnicurekotlin.example.com.providerEndpoints.model.PatientHandOffRequest
import omnicurekotlin.example.com.providerEndpoints.model.TeamsDetailListResponse
import omnicurekotlin.example.com.userEndpoints.model.*
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import omnicurekotlin.example.com.userEndpoints.model.Provider
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitService {

    @GET("countrylistresponse")
   suspend fun getAlldata() : Response<CountryCodeListResponse>

    @GET("hospitallistresponse")
    suspend fun getHospitals():Response<HospitalListResponse>

    @POST("registerUser")
   suspend fun getProvider(@Query("providerID")provider: Provider):Response<CommonResponse>

    @GET("getWards?hospitalId={hospitalId}")
  suspend   fun getCensusWardlists(@Query("hospitalID")hospitalId: Long): Response<WardPatientListResponse>

    @GET("hospitallistresponse/{id}")
  suspend   fun getHospitalLists(@Query("id")id: Long): Response<HospitalListResponse>

     @GET("remoteprovidertyperesponse")
    fun getRemoteProviderType():Call<RemoteProviderListResponse>

    @GET("docboxpatientlistresponse/{providerId}/{token}")
  suspend  fun getDocBoxPatientList(@Query("providerId")providerId:Long,
                               @Query("token")token:String):Response<DocBoxPatientListResponse>

    @GET("hospitalresponse/{hospitalId}")
   suspend fun getHospitalWardslist(@Query("hospitalId")hospitalId: Long):Response<AddNewPatientWardResponse>

    @GET("athenadevicelistresponse/{providerId}/{token}")
    suspend fun getAthenaDevicelistApi(@Query("providerId")providerId: Long?,
                                @Query("token") token: String?): Response<AthenaDeviceListResponse>

     @GET("appointmentlistresponse/{id}/{token}/{offset}/{limit}")
   suspend  fun getAppointmentApi(@Query("providerId")providerId: Long,
                           @Query("token")token: String,
                           @Query("offset")offset: Int,
                           @Query("limit")limit: Int) :Response<AppointmentListResponse>

     @POST("addPatient/{providerId}/{token}")
     suspend  fun getPatientsList(@Query("providerId")providerId: Long,
                         @Query("token")token: String,
                         @Query("patients")patient: Patient): Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse>

     @GET("teamDetailsByName?patientId={patientId}&teamName={teamName}")
    suspend fun getMembersApi(@Query("patientId")patientId: Long,
                       @Query("teamName")teamName: String): Response<TeamsDetailListResponse>

     @GET("patientdetailsresponse/{uid}")
    suspend fun getPatientApi(@Query("uid")uid: Long): Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse>

     @POST("doPatientDischarge")
    suspend fun getDischargeApiList(@Query("dischargePatientRequest")
                             dischargePatientRequest: DischargePatientRequest):Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse>

     @GET("getWardsPatient?hospitalId={hospitalId}&wardName={wardName}")
  suspend   fun getHospitalwardList(@Query("hospitalId")hospitalId: Long,
                             @Query("wardName")wardName: String): Response<WardPatientListResponse>

     @GET("getWards?hospitalId={hospitalId}")
    fun getCensusWardApi( @Query("hospitalId")hospitalId: Long): Call<WardPatientListResponse>

    @POST("doReconsultOtherPatient")
    suspend fun getothersreconsult(@Query("otherReBroadcastRequest")otherRebroadcastRequest: OtherRebroadcastRequest):
            Response<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>

    @GET("teamDetails/{providerid}")
    suspend fun getTeamList(@Query("uid")uid: Long): Response<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>

     @POST("inviteProviderBroadCast/{id}/{token}/{patientId}")
   suspend  fun getInviteBroadcastApi(@Query("providerId")providerId: Long,
                               @Query("token")token: String,
                               @Query("patientId")patientId: Long):
           Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse>

     @GET("bSPHandOffList/{providerId}")
  suspend   fun getHandOffListApi(@Query("providerId")providerId: Long): Response<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>

      @POST("doBspHandover")
   suspend   fun getsendBPHandoff(@Query("patientHandoffRequest")patientHandOffRequest: PatientHandOffRequest):
             Response<omnicurekotlin.example.com.providerEndpoints.model.CommonResponse>

       @POST("doTransferWithinHospital/{token}")
    suspend fun getTransferList(@Query("token")token: String,
                         @Query("patientsTransferRequest")patientTransferRequest: PatientTransferRequest):
               Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse>

     @POST("doTransferHospialForPatient/{token}")
    suspend fun getAnotherHospiPatients(@Query("token")token: String,
                                 @Query("patientTransferRequest")patientTransferRequest: PatientTransferRequest):
            Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse>

     @GET("hospitalresponse/{hospitalId}")
   suspend  fun getWardList( @Query("hospitalId")hospitalId: Long): Response<AddNewPatientWardResponse>

   @GET("hospitalprovidersresponse/{hospitalId}/{providerId}")
   suspend fun getTransferProviderList(@Query("hospitalId")hospitalId: Long,
                                @Query("providerId")providerId: Long):
           Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse>

     @GET("GetTransferHospitalList/{token}/{patientId}")
    suspend fun getTransferHospApi(@Query("token")token: String,
                            @Query("patientId")patientId: String):
             Response<omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse>

     @GET("hospitallistresponse")
     fun getHospitalsList():Call<HospitalListResponse>

     @GET("remoteprovidertyperesponse")
      suspend  fun getRemoteProviderList(): Response<RemoteProviderListResponse>

     @POST("registerUser")
  suspend  fun getRegisterList(@Query("provider")provider: Provider): Response<CommonResponse>

  @POST("registrationVerify/{id}/{otp}/{channel}")
    suspend fun getverifyOtp(@Query("uid")uid: Long,
                             @Query("otp")otp: String,
                             @Query("fcm")fcm: String?,
                             @Query("channnel")channel: String): Response<CommonResponse>

    @POST("registrationEmailOTP/{id}/{email}")
    suspend fun getresendOtp(
            @Query("uid")uid: Long,
            @Query("channel")channel: String,
            @Query("channelVal")channelVal: String,
            @Query("cc")cc: String): Response<CommonResponse>

}