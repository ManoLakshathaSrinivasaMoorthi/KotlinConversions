package omnicurekotlin.example.com.userEndpoints.model

import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital

class HospitalListResponse {

    var count: Int? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var hospitalList: List<Hospital?>? = null
    var id: Long? = null
    var status: Boolean? = null


    @JvmName("getCount1")
    fun getCount(): Int? {
        return count
    }


    fun setCount(count: Int?): HospitalListResponse {
        this.count = count
        return this
    }


    @JvmName("getErrorId1")
    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?): HospitalListResponse {
        this.errorId = errorId
        return this
    }


    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?): HospitalListResponse {
        this.errorMessage = errorMessage
        return this
    }

    @JvmName("getHospitalList1")
    fun getHospitalList(): List<Hospital?>? {
        return hospitalList
    }


    fun setHospitalList(hospitalList: List<Hospital?>?): HospitalListResponse {
        this.hospitalList = hospitalList
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): HospitalListResponse {
        this.id = id
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?): HospitalListResponse {
        this.status = status
        return this
    }


}
