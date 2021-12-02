package omnicurekotlin.example.com.hospitalEndpoints.model

import java.io.Serializable

class TransferWardResponse:Serializable {

    private var status: Boolean? = null
    private var errorId: Int? = null
    private var hospitalId: Long? = null
    private var wards: List<AddNewPatientWard?>? = null

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

    fun getHospitalId(): Long? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: Long?) {
        this.hospitalId = hospitalId
    }

    fun getWards(): List<AddNewPatientWard?>? {
        return wards
    }

    fun setWards(wards: List<AddNewPatientWard?>?) {
        this.wards = wards
    }

}