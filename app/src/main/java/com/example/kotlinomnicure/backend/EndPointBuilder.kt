package com.example.kotlinomnicure.backend

//import omnicurekotlin.example.com.providerEndpoints.model.ProviderEndpoints
import android.util.Log
import com.example.kotlinomnicure.apiRetrofit.UserEndpoints
import com.example.kotlinomnicure.utils.BuildConfigConstants
import okhttp3.OkHttpClient
import omnicurekotlin.example.com.userEndpoints.UserEndpointsRetrofit

class EndPointBuilder {

 /* private var providerEndpoints: ProviderEndpoints? = null
  private var userEndpoints: UserEndpoints? = null
  private var loginEndpoints: LoginEndpoints? = null
  private var patientEndpoints: PatientEndpoints? = null
  private var hospitalEndpoints: HospitalEndpoints? = null
  private var appointmentEndpoints: AppointmentEndpoints? = null

  // Healthcare Endpoints
  private var healthCareEndpoints: HealthCareEndpoints? = null

  private fun EndPointBuilder() {}

  fun getProviderEndpoints(): ProviderEndpoints? {
    if (providerEndpoints == null) {
      val builder: ProviderEndpoints.Builder = OkHttpClient.Builder(
        AndroidHttp.newCompatibleTransport(),
        AndroidJsonFactory(), BuildConfigConstants.authorize()
      )
        .setRootUrl(BuildConfigConstants.backendRootUrl)
      builder.setApplicationName(BuildConfigConstants.backendAppName)
      providerEndpoints = builder.build()
    }
    return providerEndpoints
  }

  // Healthcare Endpoints
  fun getHealthCareEndpoints(): HealthCareEndpoints? {
    if (healthCareEndpoints == null) {
      val builder: HealthCareEndpoints.Builder = Builder(
        AndroidHttp.newCompatibleTransport(),
        AndroidJsonFactory(), BuildConfigConstants.authorize()
      )
        .setRootUrl(BuildConfigConstants.backendRootUrl)
      builder.setApplicationName(BuildConfigConstants.backendAppName)
      healthCareEndpoints = builder.build()
    }
    return healthCareEndpoints
  }

  fun getUserEndpoints(): UserEndpoints? {
    if (userEndpoints == null) {
      val builder: UserEndpoints?.Builder = Builder(
        AndroidHttp().newCompatibleTransport(),
        AndroidJsonFactory(), BuildConfigConstants.authorize()
      )
        .setRootUrl(BuildConfigConstants.backendRootUrl)
      builder.setApplicationName(BuildConfigConstants.backendAppName)
      userEndpoints = builder.build()
    }
    return userEndpoints
  }

  fun getLoginEndpoints(): LoginEndpoints? {
    if (loginEndpoints == null) {
      val builder: LoginEndpoints.Builder = Builder(
        AndroidHttp.newCompatibleTransport(),
        AndroidJsonFactory(), BuildConfigConstants.authorize()
      )
        .setRootUrl(BuildConfigConstants.backendRootUrl)
      builder.setApplicationName(BuildConfigConstants.backendAppName)
      loginEndpoints = builder.build()
    }
    return loginEndpoints
  }

  fun getPatientEndpoints(): PatientEndpoints? {
    Log.d(
      "PatientEndpoints",
      "BuildConfigConstants.getBackendRootUrl() : " + BuildConfigConstants.backendRootUrl
    )
    Log.d(
      "PatientEndpoints",
      "BuildConfigConstants.getBackendRootUrl() : " + BuildConfigConstants.backendAppName
    )
    if (patientEndpoints == null) {
      val builder: PatientEndpoints.Builder = Builder(
        AndroidHttp.newCompatibleTransport(),
        AndroidJsonFactory(), BuildConfigConstants.authorize()
      )
        .setRootUrl(BuildConfigConstants.backendRootUrl)
      builder.setApplicationName(BuildConfigConstants.backendAppName)
      patientEndpoints = builder.build()
    }
    return patientEndpoints
  }

  fun getHospitalEndpoints(): HospitalEndpoints? {
    if (hospitalEndpoints == null) {
      val builder: HospitalEndpoints.Builder = Builder(
        AndroidHttp.newCompatibleTransport(),
        AndroidJsonFactory(), BuildConfigConstants.authorize()
      )
        .setRootUrl(BuildConfigConstants.backendRootUrl)
      builder.setApplicationName(BuildConfigConstants.backendAppName)
      hospitalEndpoints = builder.build()
    }
    return hospitalEndpoints
  }

  fun getAppointmentEndpoints(): AppointmentEndpoints? {
    if (appointmentEndpoints == null) {
      val builder: AppointmentEndpoints.Builder = Builder(
        AndroidHttp.newCompatibleTransport(),
        AndroidJsonFactory(), BuildConfigConstants.authorize()
      )
        .setRootUrl(BuildConfigConstants.backendRootUrl)
      builder.setApplicationName(BuildConfigConstants.backendAppName)
      appointmentEndpoints = builder.build()
    }
    return appointmentEndpoints
  }*/
}
