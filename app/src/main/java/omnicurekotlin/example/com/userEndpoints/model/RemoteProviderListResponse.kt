package omnicurekotlin.example.com.userEndpoints.model

class RemoteProviderListResponse {

    var status: Boolean? = null
    var errorId: Int? = null
    var remoteProviderTypeList: List<RemoteProvider>? = null

    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?) {
        this.errorId = errorId
    }

//
//    public double getErrorId() {
//        return errorId;
//    }
//
//    public void setErrorId(double errorId) {
//        this.errorId = errorId;
//    }

    //
    //    public double getErrorId() {
    //        return errorId;
    //    }
    //
    //    public void setErrorId(double errorId) {
    //        this.errorId = errorId;
    //    }
    @JvmName("getRemoteProviderTypeList1")
    fun getRemoteProviderTypeList(): List<RemoteProvider?>? {
        return remoteProviderTypeList
    }

    @JvmName("setRemoteProviderTypeList1")
    fun setRemoteProviderTypeList(remoteProviderTypeList: List<RemoteProvider>?) {
        this.remoteProviderTypeList = remoteProviderTypeList
    }

//    @Override
//    public RemoteProviderListResponse set(String fieldName, Object value) {
//        return (RemoteProviderListResponse) super.set(fieldName, value);
//    }
//
//    @Override
//    public RemoteProviderListResponse clone() {
//        return (RemoteProviderListResponse) super.clone();
//    }

}
