package omnicurekotlin.example.com.hospitalEndpoints.model

class WardPatientListResponse {

    private var status: Boolean? = null

    private var errorId: Int? = null
    private var hospitalId: Long? = null
    private var totalPatientCount: Int? = null
    private var hospital: Hospital? = null

    private var wardPatientList: List<WardPatientList?>? = null

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

    fun getTotalPatientCount(): Int? {
        return totalPatientCount
    }

    fun setTotalPatientCount(totalPatientCount: Int?) {
        this.totalPatientCount = totalPatientCount
    }

    fun getHospital(): Hospital? {
        return hospital
    }

    fun setHospital(hospital: Hospital?) {
        this.hospital = hospital
    }

    fun getWardPatientList(): List<WardPatientList?>? {
        return wardPatientList
    }

    fun setWardPatientList(wardPatientList: List<WardPatientList?>?): WardPatientListResponse? {
        this.wardPatientList = wardPatientList
        return this
    }




}
