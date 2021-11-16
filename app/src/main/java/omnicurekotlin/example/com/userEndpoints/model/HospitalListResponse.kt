package omnicurekotlin.example.com.userEndpoints.model

import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital

class HospitalListResponse {

    private var count: Int? = null
    private var errorId: Int? = null
    private var errorMessage: String? = null
    private var hospitalList: List<Hospital?>? = null
    private var id: Long? = null
    private var status: Boolean? = null


    fun getCount(): Int? {
        return count
    }


    fun setCount(count: Int?): HospitalListResponse {
        this.count = count
        return this
    }


    fun getErrorId(): Int? {
        return errorId
    }

    fun setErrorId(errorId: Int?): HospitalListResponse {
        this.errorId = errorId
        return this
    }


    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?): HospitalListResponse {
        this.errorMessage = errorMessage
        return this
    }

    fun getHospitalList(): List<Hospital?>? {
        return hospitalList
    }


    fun setHospitalList(hospitalList: List<Hospital?>?): HospitalListResponse {
        this.hospitalList = hospitalList
        return this
    }


    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): HospitalListResponse {
        this.id = id
        return this
    }


    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?): HospitalListResponse {
        this.status = status
        return this
    }

 /*   operator fun set(fieldName: String?, value: Any?): omnicure.mvp.com.userEndpoints.model.HospitalListResponse? {
        return super.set(fieldName, value) as omnicure.mvp.com.userEndpoints.model.HospitalListResponse?
    }

    fun clone(): omnicure.mvp.com.userEndpoints.model.HospitalListResponse? {
        return super.clone() as omnicure.mvp.com.userEndpoints.model.HospitalListResponse?
    }*/

}
