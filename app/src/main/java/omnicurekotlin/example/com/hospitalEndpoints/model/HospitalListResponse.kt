package omnicurekotlin.example.com.hospitalEndpoints.model

class HospitalListResponse {


    var count: Int? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var hospitalList: List<Hospital>?= null
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




    fun setErrorMessage(errorMessage: String?): HospitalListResponse {
        this.errorMessage = errorMessage
        return this
    }




    fun setHospitalList(hospitalList: List<Hospital?>?): HospitalListResponse {
        this.hospitalList = hospitalList as List<Hospital>?
        return this
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



