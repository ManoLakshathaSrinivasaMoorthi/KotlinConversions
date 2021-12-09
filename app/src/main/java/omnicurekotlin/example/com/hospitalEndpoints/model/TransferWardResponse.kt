package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class TransferWardResponse:Serializable {

     var status: Boolean? = null
     var errorId: Int? = null
     var hospitalId: Long? = null
     var wards: List<AddNewPatientWard?>? = null

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

    @JvmName("getHospitalId1")
    fun getHospitalId(): Long? {
        return hospitalId
    }

    @JvmName("setHospitalId1")
    fun setHospitalId(hospitalId: Long?) {
        this.hospitalId = hospitalId
    }

    @JvmName("getWards1")
    fun getWards(): List<AddNewPatientWard?>? {
        return wards
    }

    @JvmName("setWards1")
    fun setWards(wards: List<AddNewPatientWard?>?) {
        this.wards = wards
    }

}