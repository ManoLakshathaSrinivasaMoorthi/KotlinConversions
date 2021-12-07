package omnicurekotlin.example.com.userEndpoints.model

import omnicurekotlin.example.com.loginEndpoints.model.AppConfig


class VersionInfoResponse {

   var appConfig: AppConfig? = null
  var errorId: Int? = null
   var errorMessage: String? = null

  var aesKey: String? = null


   var id: Long? = null

  var status: Boolean? = null


  @JvmName("getAppConfig1")
  fun getAppConfig(): AppConfig? {
    return appConfig
  }


  fun setAppConfig(appConfig: AppConfig?): VersionInfoResponse {
    this.appConfig = appConfig
    return this
  }


  @JvmName("getErrorId1")
  fun getErrorId(): Int? {
    return errorId
  }


  fun setErrorId(errorId: Int?): VersionInfoResponse {
    this.errorId = errorId
    return this
  }

  @JvmName("getAesKey1")
  fun getAesKey(): String? {
    return aesKey
  }

  @JvmName("setAesKey1")
  fun setAesKey(aesKey: String?) {
    this.aesKey = aesKey
  }

  @JvmName("getErrorMessage1")
  fun getErrorMessage(): String? {
    return errorMessage
  }


  fun setErrorMessage(errorMessage: String?): VersionInfoResponse {
    this.errorMessage = errorMessage
    return this
  }


  @JvmName("getId1")
  fun getId(): Long? {
    return id
  }


  fun setId(id: Long?): VersionInfoResponse {
    this.id = id
    return this
  }


  @JvmName("getStatus1")
  fun getStatus(): Boolean? {
    return status
  }


  fun setStatus(status: Boolean?): VersionInfoResponse {
    this.status = status
    return this
  }


}
