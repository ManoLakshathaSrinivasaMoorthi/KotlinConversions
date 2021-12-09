package omnicurekotlin.example.com.hospitalEndpoints.model

class WardPatientListResponse {

    var status: Boolean? = null
    var errorId: Int? = null
    var hospitalId: Long? = null
    var totalPatientCount: Int? = null
    var hospital: Hospital? = null
    var wardPatientList: List<WardPatientList?>? = null

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

    @JvmName("getTotalPatientCount1")
    fun getTotalPatientCount(): Int? {
        return totalPatientCount
    }

    @JvmName("setTotalPatientCount1")
    fun setTotalPatientCount(totalPatientCount: Int?) {
        this.totalPatientCount = totalPatientCount
    }

    @JvmName("getHospital1")
    fun getHospital(): Hospital? {
        return hospital
    }

    @JvmName("setHospital1")
    fun setHospital(hospital: Hospital?) {
        this.hospital = hospital
    }

    @JvmName("getWardPatientList1")
    fun getWardPatientList(): List<WardPatientList?>? {
        return wardPatientList
    }

    fun setWardPatientList(wardPatientList: List<WardPatientList?>?): WardPatientListResponse {
        this.wardPatientList = wardPatientList
        return this
    }




}
