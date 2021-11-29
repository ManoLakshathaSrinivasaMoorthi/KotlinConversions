package omnicurekotlin.example.com.providerEndpoints.model

class RemoteProviderListResponse {

    private var status: Boolean? = null
    private var errorId: Int? = null
    private var remoteProviderTypeList: List<RemoteProvider?>? = null

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

    fun getRemoteProviderTypeList(): List<RemoteProvider?>? {
        return remoteProviderTypeList
    }

    fun setRemoteProviderTypeList(remoteProviderTypeList: List<RemoteProvider?>?) {
        this.remoteProviderTypeList = remoteProviderTypeList
    }


}
