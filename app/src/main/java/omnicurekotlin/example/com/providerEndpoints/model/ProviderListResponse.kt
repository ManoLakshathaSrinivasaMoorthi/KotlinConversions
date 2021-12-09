package omnicurekotlin.example.com.providerEndpoints.model


class ProviderListResponse {

     var count: Int? = null
     var errorId: Int? = null
     var errorMessage: String? = null
     var id: Long? = null
     var providerList: List<Provider?>? = null
     var status: Boolean? = null


    @JvmName("getCount1")
    fun getCount(): Int? {
        return count
    }

    fun setCount(count: Int?): ProviderListResponse? {
        this.count = count
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?): ProviderListResponse? {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): ProviderListResponse? {
        this.errorMessage = errorMessage
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): ProviderListResponse? {
        this.id = id
        return this
    }


    @JvmName("getProviderList1")
    fun getProviderList(): List<Provider?>? {
        return providerList
    }


    fun setProviderList(providerList: List<Provider?>?): ProviderListResponse? {
        this.providerList = providerList
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): ProviderListResponse? {
        this.status = status
        return this
    }


}
