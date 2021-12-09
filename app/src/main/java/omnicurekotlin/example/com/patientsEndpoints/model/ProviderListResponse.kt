package omnicurekotlin.example.com.patientsEndpoints.model

class ProviderListResponse {


     var status: Boolean? = null
     var errorId: Int? = null
     var providerList: List<ProviderList?>? = null

    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Boolean?) {
        this.status = status
    }

    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }

    @JvmName("setErrorId1")
    fun setErrorId(errorId: Int?) {
        this.errorId = errorId
    }

    @JvmName("getProviderList1")
    fun getProviderList(): List<ProviderList?>? {
        return providerList
    }

    @JvmName("setProviderList1")
    fun setProviderList(providerList: List<ProviderList?>?) {
        this.providerList = providerList
    }

}