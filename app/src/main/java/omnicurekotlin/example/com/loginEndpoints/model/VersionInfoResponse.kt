
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable


class VersionInfoResponse : Serializable {

    var appConfig: AppConfig? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var id: Long? = null
    var status: Boolean? = null

    fun setAppConfig(appConfig: AppConfig?): VersionInfoResponse {
        this.appConfig = appConfig
        return this
    }


    fun setErrorId(errorId: Int?): VersionInfoResponse {
        this.errorId = errorId
        return this
    }


    fun setErrorMessage(errorMessage: String?): VersionInfoResponse {
        this.errorMessage = errorMessage
        return this
    }


    fun setId(id: Long?): VersionInfoResponse {
        this.id = id
        return this
    }


    fun setStatus(status: Boolean?): VersionInfoResponse {
        this.status = status
        return this
    }
}