package omnicurekotlin.example.com.providerEndpoints.model

class RemoteProviderListResponse {

     var status: Boolean? = null
     var errorId: Int? = null
     var remoteProviderTypeList: List<RemoteProvider?>? = null

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

    @JvmName("getRemoteProviderTypeList1")
    fun getRemoteProviderTypeList(): List<RemoteProvider?>? {
        return remoteProviderTypeList
    }

    @JvmName("setRemoteProviderTypeList1")
    fun setRemoteProviderTypeList(remoteProviderTypeList: List<RemoteProvider?>?) {
        this.remoteProviderTypeList = remoteProviderTypeList
    }


}
