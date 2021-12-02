
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable


class VersionInfoResponse : Serializable {

    private var appConfig: AppConfig? = null

    var errorId: Int? = null
    var errorMessage: String? = null
    var id: Long? = null

    var status: Boolean? = null

    fun setAppConfig(appConfig: AppConfig?): VersionInfoResponse {
        this.appConfig = appConfig
        return this
    }

    /**
     * @param errorId errorId or `null` for none
     */
    fun setErrorId(errorId: Int?): VersionInfoResponse {
        this.errorId = errorId
        return this
    }

    /**
     * @param errorMessage errorMessage or `null` for none
     */
    fun setErrorMessage(errorMessage: String?): VersionInfoResponse {
        this.errorMessage = errorMessage
        return this
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): VersionInfoResponse {
        this.id = id
        return this
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: Boolean?): VersionInfoResponse {
        this.status = status
        return this
    } //  @Override
    //  public VersionInfoResponse set(String fieldName, Object value) {
    //    return (VersionInfoResponse) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public VersionInfoResponse clone() {
    //    return (VersionInfoResponse) super.clone();
    //  }
}