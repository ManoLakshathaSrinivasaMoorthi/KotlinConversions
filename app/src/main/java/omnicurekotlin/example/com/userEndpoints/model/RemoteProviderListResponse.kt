package omnicurekotlin.example.com.userEndpoints.model

class RemoteProviderListResponse {

    var status: Boolean? = null

    var errorId: Int? = null
    var remoteProviderTypeList: List<RemoteProvider>? = null

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
