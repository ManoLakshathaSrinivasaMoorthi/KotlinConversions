package omnicurekotlin.example.com.patientsEndpoints.model

class ProviderListResponse {


    private var status: Boolean? = null
    private var errorId: Int? = null
    private var providerList: List<ProviderList?>? = null

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?) {
        this.errorId = errorId
    }

    fun getProviderList(): List<ProviderList?>? {
        return providerList
    }

    fun setProviderList(providerList: List<ProviderList?>?) {
        this.providerList = providerList
    }

}