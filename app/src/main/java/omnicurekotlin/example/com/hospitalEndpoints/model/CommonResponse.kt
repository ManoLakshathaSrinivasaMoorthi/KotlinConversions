package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class CommonResponse:Serializable {
    var activeList: List<Patient?>? = null
    var dischargedCount: Int? = null
    var dischargedList: List<Patient?>? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var id: Long? = null
    var pendingList: List<Patient?>? = null
    var provider: Provider? = null
    var providerList: List<Provider?>? = null
    var status: Boolean? = null


    @JvmName("getActiveList1")
    fun getActiveList(): List<Patient?>? {
        return activeList
    }


    fun setActiveList(activeList: List<Patient?>?): CommonResponse {
        this.activeList = activeList
        return this
    }


    @JvmName("getDischargedCount1")
    fun getDischargedCount(): Int? {
        return dischargedCount
    }


    fun setDischargedCount(dischargedCount: Int?): CommonResponse {
        this.dischargedCount = dischargedCount
        return this
    }


    @JvmName("getDischargedList1")
    fun getDischargedList(): List<Patient?>? {
        return dischargedList
    }


    fun setDischargedList(dischargedList: List<Patient?>?): CommonResponse {
        this.dischargedList = dischargedList
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): CommonResponse {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): CommonResponse {
        this.errorMessage = errorMessage
        return this
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): CommonResponse {
        this.id = id
        return this
    }


    @JvmName("getPendingList1")
    fun getPendingList(): List<Patient?>? {
        return pendingList
    }

    fun setPendingList(pendingList: List<Patient?>?): CommonResponse? {
        this.pendingList = pendingList
        return this
    }


    @JvmName("getProvider1")
    fun getProvider(): Provider? {
        return provider
    }


    fun setProvider(provider: Provider?):CommonResponse {
        this.provider = provider
        return this
    }


    @JvmName("getProviderList1")
    fun getProviderList(): List<Provider?>? {
        return providerList
    }


    fun setProviderList(providerList: List<Provider?>?): CommonResponse {
        this.providerList = providerList
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): CommonResponse {
        this.status = status
        return this
    }



}

