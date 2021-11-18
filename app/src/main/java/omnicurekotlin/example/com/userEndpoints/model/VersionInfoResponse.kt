package omnicurekotlin.example.com.userEndpoints.model


class VersionInfoResponse {

  //private var appConfig: JsonMap? = null

  private var errorId: Int? = null
  private var errorMessage: String? = null
  private var id: Long? = null
  private var status: Boolean? = null


 /* fun getAppConfig(): JsonMap? {
    return appConfig
  }


  fun setAppConfig(appConfig: JsonMap?): VersionInfoResponse? {
    this.appConfig = appConfig
    return this
  }*/

  fun getErrorId(): Int? {
    return errorId
  }

  fun setErrorId(errorId: Int?): VersionInfoResponse? {
    this.errorId = errorId
    return this
  }


  fun getErrorMessage(): String? {
    return errorMessage
  }


  fun setErrorMessage(errorMessage: String?): VersionInfoResponse? {
    this.errorMessage = errorMessage
    return this
  }

  fun getId(): Long? {
    return id
  }

  fun setId(id: Long?): VersionInfoResponse? {
    this.id = id
    return this
  }

  fun getStatus(): Boolean? {
    return status
  }

  fun setStatus(status: Boolean?): VersionInfoResponse? {
    this.status = status
    return this
  }

 /* operator fun set(fieldName: String?, value: Any?): VersionInfoResponse? {
    return super.set(fieldName, value) as VersionInfoResponse?
  }

  fun clone(): VersionInfoResponse? {
    return super.clone() as VersionInfoResponse?
  }*/

}
