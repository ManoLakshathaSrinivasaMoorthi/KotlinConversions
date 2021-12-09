package omnicurekotlin.example.com.hospitalEndpoints.model

class AddNewPatientWardResponse {

     var status: Boolean? = null
     var errorId: Int? = null
     var wards: List<AddNewPatientWard>? = null

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

    @JvmName("getWards1")
    fun getWards(): List<AddNewPatientWard>? {
        return wards
    }

    @JvmName("setWards1")
    fun setWards(wards: List<AddNewPatientWard>?) {
        this.wards = wards
    }


}
