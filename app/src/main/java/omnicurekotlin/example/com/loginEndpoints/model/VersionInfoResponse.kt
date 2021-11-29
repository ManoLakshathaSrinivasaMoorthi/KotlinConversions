
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model

import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model.AppConfig
import java.io.Serializable


class VersionInfoResponse : Serializable {
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var appConfig: AppConfig? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var errorId: Int? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var errorMessage: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key @com.google.api.client.json.JsonString
    var id: Long? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var status: Boolean? = null
        private set

    /**
     * @param appConfig appConfig or `null` for none
     */
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