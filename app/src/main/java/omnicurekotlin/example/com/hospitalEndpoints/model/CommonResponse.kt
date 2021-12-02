package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class CommonResponse:Serializable {
    private var activeList: List<Patient?>? = null
    private var dischargedCount: Int? = null
    private var dischargedList: List<Patient?>? = null
    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null

    private var pendingList: List<Patient?>? = null
    private var provider: Provider? = null

    private var providerList: List<Provider?>? = null

    private var status: Boolean? = null


    fun getActiveList(): List<Patient?>? {
        return activeList
    }

    /**
     * @param activeList activeList or `null` for none
     */
    fun setActiveList(activeList: List<Patient?>?): CommonResponse {
        this.activeList = activeList
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDischargedCount(): Int? {
        return dischargedCount
    }

    /**
     * @param dischargedCount dischargedCount or `null` for none
     */
    fun setDischargedCount(dischargedCount: Int?): CommonResponse {
        this.dischargedCount = dischargedCount
        return this
    }


    fun getDischargedList(): List<Patient?>? {
        return dischargedList
    }


    fun setDischargedList(dischargedList: List<Patient?>?): CommonResponse {
        this.dischargedList = dischargedList
        return this
    }


    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): CommonResponse {
        this.errorId = errorId
        return this
    }


    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): CommonResponse {
        this.errorMessage = errorMessage
        return this
    }

    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): CommonResponse {
        this.id = id
        return this
    }


    fun getPendingList(): List<Patient?>? {
        return pendingList
    }

    fun setPendingList(pendingList: List<Patient?>?): CommonResponse? {
        this.pendingList = pendingList
        return this
    }


    fun getProvider(): Provider? {
        return provider
    }


    fun setProvider(provider: Provider?):CommonResponse {
        this.provider = provider
        return this
    }


    fun getProviderList(): List<Provider?>? {
        return providerList
    }


    fun setProviderList(providerList: List<Provider?>?): CommonResponse {
        this.providerList = providerList
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getStatus(): Boolean? {
        return status
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: Boolean?): CommonResponse {
        this.status = status
        return this
    }

//  @Override
//  public CommonResponse set(String fieldName, Object value) {
//    return (CommonResponse) super.set(fieldName, value);
//  }
//
//  @Override
//  public CommonResponse clone() {
//    return (CommonResponse) super.clone();
//  }

}

