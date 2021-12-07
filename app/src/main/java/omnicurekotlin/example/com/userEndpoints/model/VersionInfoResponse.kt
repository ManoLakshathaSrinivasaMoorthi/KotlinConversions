package omnicurekotlin.example.com.userEndpoints.model

import omnicurekotlin.example.com.loginEndpoints.model.AppConfig


class VersionInfoResponse {

  var appConfig: AppConfig? = null
  private var errorId: Int? = null
  var errorMessage: String? = null

  var aesKey: String? = null


  private var id: Long? = null

  var status: Boolean? = null


  fun getAppConfig(): AppConfig? {
    return appConfig
  }


  fun setAppConfig(appConfig: AppConfig?): VersionInfoResponse {
    this.appConfig = appConfig
    return this
  }


  fun getErrorId(): Int? {
    return errorId
  }


  fun setErrorId(errorId: Int?): VersionInfoResponse {
    this.errorId = errorId
    return this
  }

  fun getAesKey(): String? {
    return aesKey
  }

  fun setAesKey(aesKey: String?) {
    this.aesKey = aesKey
  }

  fun getErrorMessage(): String? {
    return errorMessage
  }


  fun setErrorMessage(errorMessage: String?): VersionInfoResponse {
    this.errorMessage = errorMessage
    return this
  }


  fun getId(): Long? {
    return id
  }


  fun setId(id: Long?): VersionInfoResponse {
    this.id = id
    return this
  }


  fun getStatus(): Boolean? {
    return status
  }


  fun setStatus(status: Boolean?): VersionInfoResponse {
    this.status = status
    return this
  }


}
