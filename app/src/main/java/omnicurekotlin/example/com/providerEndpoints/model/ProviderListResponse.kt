package omnicurekotlin.example.com.providerEndpoints.model

class ProviderListResponse {

    private var count: Int? = null
    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var id: Long? = null
    private var providerList: List<Provider?>? = null
    private var status: Boolean? = null


    fun getCount(): Int? {
        return count
    }

    fun setCount(count: Int?): ProviderListResponse? {
        this.count = count
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?): ProviderListResponse? {
        this.errorId = errorId
        return this
    }


    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): ProviderListResponse? {
        this.errorMessage = errorMessage
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): ProviderListResponse? {
        this.id = id
        return this
    }


    fun getProviderList(): List<Provider?>? {
        return providerList
    }


    fun setProviderList(providerList: List<Provider?>?): ProviderListResponse? {
        this.providerList = providerList
        return this
    }


    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): ProviderListResponse? {
        this.status = status
        return this
    }


}
